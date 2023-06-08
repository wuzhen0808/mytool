
import mytool.collector.xueqiu.XueqiuDataCollector;
import mytool.collector.xueqiu.XueqiuDataWasher;
import mytool.collector.util.CsvUtil;
import org.junit.jupiter.api.Test

import java.nio.charset.Charset

public class XueQiuTest {
    private static String shCsv = "d:\\openstock\\sse\\sse.corplist.csv";
    private static String szCsv = "d:\\openstock\\szse\\szse.corplist.csv";
    File folder = new File("d:\\openstock\\xueqiu\\raw");
    File folder2 = new File("d:\\openstock\\xueqiu\\washed");

    @Test
    public void test() throws IOException {
        //this.doCollect();
        this.doWash();

    }

    private void doWash() throws IOException {

        XueqiuDataWasher w = new XueqiuDataWasher(folder, Charset.forName("UTF-8"), folder2);
        w.types("balsheet");
        w.types("incstatement");
        w.types("cfstatement");

        w.run();
    }

    private void doCollect() throws IOException {

        folder.mkdirs();
        XueqiuDataCollector dc = new XueqiuDataCollector(folder);
        dc.types("balsheet", "incstatement", "cfstatement");
        dc.pauseInterval(1 * 1000);
        List<String> corpCodeL = getCorpCodeList();
        dc.corpCodes(corpCodeL);
        dc.run();
    }

    public List<String> getCorpCodeList() throws IOException {
        List<String> rt = new ArrayList<String>();
        if (true) {

            Charset cs = Charset.forName("UTF-8");
            Reader fr = new InputStreamReader(new FileInputStream(szCsv), cs);
            List<String> corpL = CsvUtil.loadColumnFromCsvFile(fr, "A股代码", '\t');
            rt.addAll(corpL);
        }
        {

            Charset cs = Charset.forName("UTF-8");
            Reader fr = new InputStreamReader(new FileInputStream(shCsv), cs);
            List<String> corpL = CsvUtil.loadColumnFromCsvFile(fr, "A股代码", '\t');
            rt.addAll(corpL);
        }

        return rt;

    }
}
