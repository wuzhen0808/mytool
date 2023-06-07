package mytool.collector.wash

import groovy.transform.CompileStatic
import mytool.collector.ReportType
import mytool.collector.database.ReportDataAccessor
import mytool.util.Attributes
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.Charset

@CompileStatic
class WashedFileLoader {

    static class WashedDataTypeContext {
        public static Map<String, Integer> typeMap = new HashMap<>();

        static {
            typeMap.put("ZCFZB", 1);
            typeMap.put("LRB", 2);
            typeMap.put("XJLLB", 3);
            typeMap.put("quotes", 4);
            typeMap.put("LIXI", 5);
        }

        ReportDataAccessor dbs;
        ReportType reportType;

        WashedDataTypeContext(ReportType type, ReportDataAccessor dbs) {
            this.reportType = type
            this.dbs = dbs;
        }

        void writeRow(String corpId, Date reportDate, String key, BigDecimal value) {
            List<String> kL = new ArrayList<>();
            kL.add(key);
            List<BigDecimal> vL = new ArrayList<>();
            vL.add(value);
            writeRow(corpId, reportDate, kL, vL);
        }

        void writeRow(String corpId, Date reportDate, List<String> keyList, List<BigDecimal> valueList) {

            dbs.mergeReport(reportType, corpId, reportDate, keyList, valueList);

        }

    }

    static class WashedFileLoadContext {
        protected Map<ReportType, WashedDataTypeContext> nextRowMap = new HashMap<>();

        public ReportDataAccessor dbs;

        public Attributes attributes = new Attributes();

        WashedFileLoadContext(ReportDataAccessor dbs) {
            this.dbs = dbs;
        }

        WashedDataTypeContext getOrCreateTypeContext(ReportType type) {
            WashedDataTypeContext rt = nextRowMap.get(type);
            if (rt == null) {
                rt = createTypeContext(type);
                nextRowMap.put(type, rt);
            }
            return rt;
        }

        WashedDataTypeContext createTypeContext(ReportType type) {

            return new WashedDataTypeContext(type, this.dbs);
        }
    }
    //

    private static final Logger LOG = LoggerFactory.getLogger(WashedFileLoader.class);

    private Map<String, WashedFileHandler> processMap = new HashMap<>();

    private int maxSize = -1;

    private int processed;

    private boolean interrupted;

    WashedFileLoader() {

        processMap.put("zcfzb", new DefaultWashedFileHandler(ReportType.ZCFZB))
        processMap.put("lrb", new DefaultWashedFileHandler(ReportType.LRB))
        processMap.put("xjllb", new DefaultWashedFileHandler(ReportType.XJLLB))

        processMap.put("balsheet", new DefaultWashedFileHandler(ReportType.ZCFZB))
        processMap.put("incstatement", new DefaultWashedFileHandler(ReportType.LRB))
        processMap.put("cfstatement", new DefaultWashedFileHandler(ReportType.XJLLB))

        processMap.put("balance", new DefaultWashedFileHandler(ReportType.ZCFZB))
        processMap.put("income", new DefaultWashedFileHandler(ReportType.LRB))
        processMap.put("cash_flow", new DefaultWashedFileHandler(ReportType.XJLLB))
    }

    void load(File dir, WashedFileLoadContext xContext) {

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
