package mytool.collector;

import au.com.bytecode.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * Convert original file format to the target format acceptable.
 *
 * @author wu
 */
public abstract class AbstractDataWasher implements Interruptable, Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractDataWasher.class);

    private File sourceDir;
    private File targetDir;

    Set<String> types = new HashSet<>();

    private boolean interrupted;
    Charset sourceCharSet;
    protected int processed;
    protected int max = Integer.MAX_VALUE;

    public AbstractDataWasher(File sourceDir, Charset sourceCharSet, File targetDir) {
        this.sourceDir = sourceDir;
        this.targetDir = targetDir;
        this.sourceCharSet = sourceCharSet;
    }

    public AbstractDataWasher types(String... types) {
        for (String type : types) {
            this.types.add(type);
        }
        return this;
    }

    @Override
    public void run() {
        try {
            this.process(this.sourceDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } //
    }

    protected abstract boolean isAcceptFile(File file);

    private boolean process(File file) throws IOException {
        if (this.interrupted) {
            LOG.warn("interrupted.");
            return true;
        }
        if (file.isFile()) {
            if (!isAcceptFile(file)) {
                // ignore
                if (LOG.isInfoEnabled()) {
                    LOG.info("ignore file:" + file.getAbsolutePath());
                }
                return false;
            }
            String type = null;
            String name = file.getName();
            for (String typeI : types) {
                if (name.startsWith(typeI)) {
                    type = typeI;
                }
            }

            if (type == null) {
                // ignore
                if (LOG.isInfoEnabled()) {
                    LOG.info("ignore(for type reason) file:" + file.getAbsolutePath() + "");
                }
                return false;
            }
            String code = resolveCodeFromFileName(type, file);
            this.doProcess(file, type, code);
            if (this.processed >= max) {
                return true;
            }
            return false;
        }
        // isDirectory
        for (File f : file.listFiles()) {
            boolean stop = this.process(f);
            if (stop) {
                return true;
            }
        }

        return false;
    }

    protected abstract String resolveCodeFromFileName(String type, File file);

    /**
     * <code>
     * Header,
     * 报告日期,2015-12-31,2014-12-31,2013-12-31,2012-12-31,2011-12-31,2010-12-31,2009-12-31,2008-12-31,
     * 日期格式,yyyy-MM-dd
     * 公司代码,300201
     * 单位,10000
     * 备注,zcfzb
     * Body,
     * ... ...
     * </code>
     */

    private void doProcess(File file, String type, String code) throws IOException {

        File typeDir = new File(this.targetDir.getAbsolutePath(), type);
        File areaDir = new File(typeDir, code.substring(0, 4));

        File output = new File(areaDir, code + "." + type + ".csv");
        if (output.exists()) {
            LOG.info("skip of file for it's already exists:" + output.getAbsolutePath());
            return;
        }
        if (!areaDir.exists()) {
            areaDir.mkdirs();
        }
        LOG.info("generating output file:" + output.getAbsolutePath());

        Reader fr = new InputStreamReader(new FileInputStream(file), this.sourceCharSet);

        CSVWriter w = new CSVWriter(new OutputStreamWriter(new FileOutputStream(output), Charset.forName("UTF-8")), ',',
                CSVWriter.NO_QUOTE_CHARACTER);

        this.process(file, type, code, fr, w);

        w.close();
        this.processed++;

    }

    protected abstract void process(File file, String type, String code, Reader r, CSVWriter w) throws IOException;

    @Override
    public void interrupt() {
        this.interrupted = true;
    }

}
