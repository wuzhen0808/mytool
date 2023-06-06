package mytool.collector.wash

import au.com.bytecode.opencsv.CSVReader
import groovy.transform.CompileStatic
import mytool.util.csv.CsvHeaderRowMap
import mytool.util.csv.CsvRow
import mytool.util.csv.CsvRowMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@CompileStatic
public class DefaultWashedFileHandler implements WashedFileHandler {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultWashedFileHandler.class);

    String fileType;

    public DefaultWashedFileHandler(String type) {
        this.fileType = type;
    }

    @Override
    public void process(File file, Reader fr, WashedFileLoader.WashedFileLoadContext xContext) {

        CSVReader reader = new CSVReader(fr);
        try {
            CsvHeaderRowMap headers = new CsvHeaderRowMap();
            CsvRowMap body = new CsvRowMap();
            CsvRowMap currentMap = null;
            int lineNumber = 0;

            while (true) {
                lineNumber++;
                String[] next = reader.readNext();
                if (next == null) {
                    break;
                }
                if ("Header".equals(next[0])) {
                    currentMap = headers;
                    continue;
                } else if ("Body".equals(next[0])) {
                    currentMap = body;
                    continue;
                }
                // the name of the item.
                String key = next[0];
                key = key.trim();
                if (key.length() == 0 && next.length <= 1) {
                    // ignore this empty line.
                    continue;
                }
                currentMap.put(key, new CsvRow(lineNumber, next));
            }

            if (headers.keyList.isEmpty()) {
                LOG.debug("ignore file(for reason of no header found):" + file.getAbsolutePath());
                return;
            }

            //
            Date[] reportDateArray = headers.getReportDateArray();
            BigDecimal unit = headers.get("单位", true).getAsBigDecimal(1, true);
            String corpId = headers.get("公司代码", true).getString(1, true);
            List<String> itemKeyList = body.keyList;

            // TODO make sure the itemKeyList is the same sequence with all
            // other body in the same sheet.

            for (int i = 0; i < reportDateArray.length; i++) {

                Date reportDate = headers.get("报告日期", true).getAsDate(i + 1, headers.getDateFormat());
                if (reportDate == null) {
                    break;
                }
                // one row:
                List<String> keyList = new ArrayList<>();
                List<BigDecimal> valueList = new ArrayList<>();

                for (String key : itemKeyList) {
                    BigDecimal value = body.get(key, true).getAsBigDecimal(i + 1, false);
                    if (value != null) {
                        value = value.multiply(unit);
                    }
                    keyList.add(key);
                    valueList.add(value);
                }
                xContext.getOrCreateTypeContext(fileType).writeRow(corpId, reportDate, keyList, valueList);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}