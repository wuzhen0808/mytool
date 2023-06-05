package mytool.collector;

import mytool.collector.util.EnvUtil;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This code collect data from http site and save to the host folder.
 *
 * @author wuzhen
 */
public abstract class HttpDataCollector {
    private static final Logger LOG = LoggerFactory.getLogger(HttpDataCollector.class);

    private File targetDir;

    private CloseableHttpClient httpclient;

    private HttpHost httpHost;

    private HttpHost proxy;

    private RequestConfig config;

    private String cookie;

    private List<String> corpCodeList = new ArrayList<String>();

    private Set<String> corpCodeSet = new HashSet<String>();

    private long lastAccessTimestamp;

    private long pauseInterval = 15 * 1000;

    private String firstFrom;

    private String[] types;

    private boolean interrupted;

    private String hostName;

    private boolean refresh;

    public HttpDataCollector(File dir, String hostName) {
        this.targetDir = dir;
        this.hostName = hostName;
    }

    public HttpDataCollector refresh(boolean refresh) {
        this.refresh = refresh;
        return this;
    }

    public HttpDataCollector cookie(String cookie) {
        this.cookie = cookie;
        return this;
    }

    public HttpDataCollector corpCodes(List<String> corpCodeL) {
        this.corpCodeList.addAll(corpCodeL);
        return this;
    }

    public HttpDataCollector corpCodes(String... corpCode) {
        for (String code : corpCode) {
            if (!this.corpCodeSet.contains(code)) {
                this.corpCodeList.add(code);
            }
        }
        return this;
    }

    public HttpDataCollector types(String... types) {

        this.types = types;

        return this;
    }

    public void execute() {
        if (this.types == null || types.length == 0) {
            throw new RuntimeException("no types specified.");
        }
        if (this.corpCodeList.isEmpty()) {
            throw new RuntimeException("no corpCodes specified.");
        }

        httpclient = HttpClients.custom().build();
        try {
            //
            httpHost = new HttpHost(this.hostName, 80, "http");

            RequestConfig.Builder configBuilder = RequestConfig.custom();

            if (EnvUtil.isProxyEnabled()) {
                proxy = new HttpHost(EnvUtil.getProxyHost(), EnvUtil.getProxyPort());
                configBuilder.setProxy(proxy);
            }

            config = configBuilder.build();

            boolean running = false;
            for (String code : this.corpCodeList) {
                if (!running) {
                    if (this.firstFrom == null || code.equals(this.firstFrom)) {
                        running = true;
                    } else {
                        LOG.warn("wait:" + this.firstFrom + ",skip:" + code);//
                    }
                }

                if (running) {
                    this.collectFor(code);
                }
                if (this.interrupted) {
                    LOG.warn("interrupted");//
                    break;
                }
            }

        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected void collectFor(String corpCode) {

        for (String type : types) {
            File typeDir = new File(this.targetDir, type);

            String fname = corpCode.substring(0, 4);//
            File areaDir = new File(typeDir, fname);
            if (!areaDir.exists()) {
                areaDir.mkdirs();
            }
            File output = getTargetFile(areaDir, type, corpCode);
            if (output.exists() && this.refresh) {
                output.delete();
            }

            if (output.exists()) {
                LOG.info("skip for not refresh while target file already exist:" + output);
            } else {
                waitAndDoCollectFor(corpCode, type, output);
            }
        }

    }

    protected abstract File getTargetFile(File areaDir, String type, String corpCode);

    private void waitAndDoCollectFor(String corpCode, String type, File outputFile) {
        int retry = 3;
        while (retry > 0) {
            LOG.info("wait..." + this.pauseInterval + "(ms) before next http access.");
            while (true) {
                long pass = System.currentTimeMillis() - this.lastAccessTimestamp;
                if (pass < pauseInterval) {
                    LOG.trace(".");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    continue;
                }
                LOG.info("it's ready for next http access.");

                break;
            }
            boolean got = false;
            try {
                this.doCollectFor(corpCode, type, outputFile);
                got = true;
            } catch (SocketException e) {
                LOG.warn("error when collect:" + outputFile.getAbsolutePath(), e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (got) {
                retry = 0;
            } else {
                retry--;
            }
        }

    }

    protected abstract String getUrl(String corpCode, String type);

    private void doCollectFor(String corpCode, String type, File outputFile) throws IOException {
        // String url = "/service/" + type + "_" + corpCode + ".html?type=year";
        String url = getUrl(corpCode, type);
        HttpGet httpget = new HttpGet(url);

        httpget.setConfig(config);
        if (this.cookie != null) {
            httpget.setHeader("Cookie", cookie);
        }

        LOG.info("Executing request " + httpget.getRequestLine() + " to " + httpHost + " via " + proxy);

        CloseableHttpResponse response = httpclient.execute(httpHost, httpget);
        this.lastAccessTimestamp = System.currentTimeMillis();
        try {

            LOG.info("----------------------------------------");
            if (LOG.isTraceEnabled()) {
                StringBuffer sb = new StringBuffer();

                sb.append("statusLine:").append(response.getStatusLine());

                for (Header header : response.getAllHeaders()) {
                    sb.append("," + header.getName() + ":" + header.getValue());
                }
                LOG.trace(sb.toString());//

            }
            if (response.getStatusLine().getStatusCode() == 200) {
                File workFile = null;
                int i = 0;
                while (true) {
                    workFile = new File(outputFile.getAbsolutePath() + ".work" + (i++));
                    if (!workFile.exists()) {
                        break;
                    }
                }
                FileOutputStream os = new FileOutputStream(workFile);
                response.getEntity().writeTo(os);
                os.close();
                this.validateFileBeforeRename(workFile);
                boolean succ = workFile.renameTo(outputFile);
                if (succ) {
                    LOG.info("got:" + outputFile.getAbsolutePath());//
                } else {
                    LOG.error(
                            "cannot rename from:" + workFile.getAbsolutePath() + ",to:" + outputFile.getAbsolutePath());
                }
            }

        } finally {
            response.close();
        }
    }

    protected abstract void validateFileBeforeRename(File workFile);

    public void interrupt() {
        this.interrupted = true;
    }

    public HttpDataCollector pauseInterval(long pauseInterval) {
        this.pauseInterval = pauseInterval;
        return this;
    }
}
