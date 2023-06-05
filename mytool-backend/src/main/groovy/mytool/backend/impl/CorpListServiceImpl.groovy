package mytool.backend.impl

import groovy.transform.CompileStatic
import mytool.backend.CorpListService
import mytool.collector.util.CsvUtil
import org.springframework.stereotype.Component

import java.nio.charset.Charset

@Component
@CompileStatic
class CorpListServiceImpl implements CorpListService {

    private static String shCsv = "d:\\openstock\\sse\\sse.corplist.csv"

    private static String szCsv = "d:\\openstock\\szse\\szse.corplist.csv"

    @Override
    List<String> corpList() {

        List<String> list = []

        this.addShenzhen(list)
        this.addShanghai(list)
        return list
    }

    void addShanghai(List<String> list) {

        Charset cs = Charset.forName("UTF-8")
        Reader fr = new InputStreamReader(new FileInputStream(shCsv), cs)
        List<String> corpL = CsvUtil.loadColumnFromCsvFile(fr, "A股代码", '\t' as char)
        list.addAll(corpL)
    }

    void addShenzhen(List<String> list) {
        Charset cs = Charset.forName("UTF-8")
        Reader fr = new InputStreamReader(new FileInputStream(szCsv), cs)
        List<String> corpL = CsvUtil.loadColumnFromCsvFile(fr, "A股代码", '\t' as char)
        list.addAll(corpL)
    }
}
