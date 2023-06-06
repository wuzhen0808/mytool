package mytool.collector.wash

import groovy.transform.CompileStatic
import mytool.collector.database.DataBaseService
import mytool.util.Attributes
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.Charset
@CompileStatic
public class WashedFileLoader {

    public static class WashedDataTypeContext {
        public static Map<String, Integer> typeMap = new HashMap<>();

        static {
            typeMap.put("ZCFZB", 1);
            typeMap.put("LRB", 2);
            typeMap.put("XJLLB", 3);
            typeMap.put("quotes", 4);
            typeMap.put("LIXI", 5);
        }

        DataBaseService dbs;
        int reportType;

        public WashedDataTypeContext(String type, DataBaseService dbs) {
            this.reportType = typeMap.get(type);
            this.dbs = dbs;
        }

        public void writeRow(String corpId, Date reportDate, String key, BigDecimal value) {
            List<String> kL = new ArrayList<>();
            kL.add(key);
            List<BigDecimal> vL = new ArrayList<>();
            vL.add(value);
            writeRow(corpId, reportDate, kL, vL);
        }

        public void writeRow(String corpId, Date reportDate, List<String> keyList, List<BigDecimal> valueList) {

            dbs.mergeReport(reportType, corpId, reportDate, keyList, valueList);

        }

    }

    public static class WashedFileLoadContext {
        protected Map<String, WashedDataTypeContext> nextRowMap = new HashMap<>();

        public DataBaseService dbs;

        public Attributes attributes = new Attributes();

        public WashedFileLoadContext(DataBaseService dbs) {
            this.dbs = dbs;
        }

        public WashedDataTypeContext getOrCreateTypeContext(String type) {
            WashedDataTypeContext rt = nextRowMap.get(type);
            if (rt == null) {
                rt = createTypeContext(type);
                nextRowMap.put(type, rt);
            }
            return rt;
        }

        public WashedDataTypeContext createTypeContext(String type) {

            return new WashedDataTypeContext(type, this.dbs);
        }
    }
    //

    private static final Logger LOG = LoggerFactory.getLogger(WashedFileLoader.class);

    private Map<String, WashedFileHandler> processMap = new HashMap<>();

    private int maxSize = -1;

    private int processed;

    private boolean interrupted;

    public WashedFileLoader() {

        processMap.put("zcfzb", new DefaultWashedFileHandler("ZCFZB"));
        processMap.put("lrb", new DefaultWashedFileHandler("LRB"));
        processMap.put("xjllb", new DefaultWashedFileHandler("XJLLB"));

        processMap.put("balsheet", new DefaultWashedFileHandler("ZCFZB"));
        processMap.put("incstatement", new DefaultWashedFileHandler("LRB"));
        processMap.put("cfstatement", new DefaultWashedFileHandler("XJLLB"));

        processMap.put("balance", new DefaultWashedFileHandler("ZCFZB"));
        processMap.put("income", new DefaultWashedFileHandler("LRB"));
        processMap.put("cash_flow", new DefaultWashedFileHandler("XJLLB"));
    }

    public void load(File dir, WashedFileLoadContext xContext) {

        if (dir.isFile()) {
            File f = dir;
            String fname = f.getName();
            String[] fnames = fname.split("\\.");

            if (fnames[fnames.length - 1].equals("csv")) {
                String ftype = fnames[fnames.length - 2];
                WashedFileHandler fp = processMap.get(ftype);
                if (fp == null) {
                    LOG.warn("no processor found for file:" + f.getAbsolutePath() + ",type:" + ftype);
                } else {

                    if (LOG.isTraceEnabled()) {
                        LOG.trace("process file:" + f.getAbsolutePath());
                    }

                    LOG.info(
                            "processor:" + this.getClass().getName() + " going to process file:" + f.getAbsolutePath());

                    InputStream is;
                    try {
                        is = new FileInputStream(f);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    Charset cs = Charset.forName("UTF-8");
                    Reader reader = new InputStreamReader(is, cs);

                    fp.process(f, reader, xContext);//
                    if (this.maxSize >= 0 && this.processed++ > this.maxSize) {
                        this.interrupted = true;
                    }

                }
            }
            return;
        }
        // is directory
        if (this.interrupted) {
            LOG.warn("interrupted.");
            return;
        }
        for (File f : dir.listFiles()) {
            // is directory
            if (this.interrupted) {
                LOG.warn("interrupted.");
                return;
            }
            this.load(f, xContext);
        }

    }

}
