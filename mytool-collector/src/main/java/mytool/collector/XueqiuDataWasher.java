package mytool.collector;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.*;

/**
 * Convert original file format to the target format acceptable.
 *
 * @author wu
 */
public class XueqiuDataWasher extends AbstractDataWasher {

    public XueqiuDataWasher(File sourceDir, Charset sourceCharSet, File targetDir) {
        super(sourceDir, sourceCharSet, targetDir);
        //this.max = 1;
    }

    @Override
    protected boolean isAcceptFile(File file) {
        return file.getName().endsWith(".csv");
    }

    @Override
    protected String resolveCodeFromFileName(String type, File file) {
        String name = file.getName();
        return name.substring(type.length(), name.length() - ".csv".length());
    }

    private static final Logger LOG = LoggerFactory.getLogger(XueqiuDataWasher.class);
    private char[] specialChar = new char[]{'：', ':', '、', '(', ')', '<', '>'};
    private static Map<String, String> type2PrefixMap = new HashMap<>();

    static {
        type2PrefixMap.put("balsheet", "A_");
        type2PrefixMap.put("incstatement", "B_");
        type2PrefixMap.put("cfstatement", "C_");
    }

    @Override
    protected void process(File file, String type, String code, Reader fr, CSVWriter w) throws IOException {

        CSVReader r = new CSVReader(fr);

        List<String[]> dataWithHeader = new ArrayList<>();
        while (true) {
            String[] row = r.readNext();
            if (row == null) {
                break;
            }
            dataWithHeader.add(row);
        }
        if (dataWithHeader.size() < 2) {// no data, so ignore it.
            LOG.warn("ignore (for no data found) file:" + file.getAbsolutePath());
            return;
        }
        w.writeNext(new String[]{"Header", ""});
        w.writeNext(new String[]{"日期格式", "yyyyMMdd"});
        w.writeNext(new String[]{"公司代码", code});
        w.writeNext(new String[]{"单位", "1"});
        w.writeNext(new String[]{"备注", type});

        String[] headerRow = dataWithHeader.get(0);
        int col = 0;
        if ("balsheet".equals(type)) {
            String[] row = getColumnAsRow(col++, dataWithHeader);
            // "报表日期",note: the fist element is [,报,表,日,期],expected is [报,表,日,期]
            //
            row[0] = "报告日期";
            w.writeNext(row);

        } else if ("incstatement".equals(type) || "cfstatement".equals(type)) {
            col++;
            // "报表期起始日","报表期截止日"
            String[] row = getColumnAsRow(col++, dataWithHeader);
            row[0] = "报告日期";
            w.writeNext(row);
        }

        w.writeNext(new String[]{"Body", ""});
        Set<String> names = new HashSet<>();
        while (col < headerRow.length) {
            String[] row = getColumnAsRow(col++, dataWithHeader);
            row[0] = normalizeIndexName(names, type, row[0]);
            w.writeNext(row);
        }

    }

    private String normalizeIndexName(Set<String> names, String type, String name) {
        for (char c : this.specialChar) {
            name = name.replace(c, '_');
        }

        String prefix = this.type2PrefixMap.get(type);
        if (prefix == null) {
            throw new RuntimeException("no prefix for type:" + type);
        }
        name = prefix + name;
        String rt = name;

        for (int i = 0; ; i++) {
            if (i > 0) {
                rt = name + i;
            }
            if (!names.contains(rt)) {
                break;
            }

        }
        names.add(rt);
        return rt;

    }

    private String[] getColumnAsRow(int col, List<String[]> data) {
        String[] rt = new String[data.size()];
        for (int i = 0; i < rt.length; i++) {
            rt[i] = data.get(i)[col];
        }
        return rt;
    }

}
