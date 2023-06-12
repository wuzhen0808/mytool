package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.service.CorpListService
import mytool.collector.util.CsvUtil
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.nio.charset.Charset

@Component
@CompileStatic
class CorpListServiceImpl implements CorpListService {

    private static String shCsv = "d:\\openstock\\sse\\sse.corplist.csv"

    private static String szCsv = "d:\\openstock\\szse\\szse.corplist.csv"
    static class CorpInfo {
        String id
        String name
    }
    Map<String, CorpInfo> corpInfoMap = [:]
    List<CorpInfo> corpInfoList = []

    @PostConstruct
    void init() {

        this.addShenzhen()
        this.addShanghai()

    }

    @Override
    List<String> corpList() {
        corpInfoList.collect({
            it.id
        })
    }

    @Override
    String getCorpName(String corpId) {
        return corpInfoMap[corpId]?.name
    }

    void addShanghai() {

        Charset cs = Charset.forName("UTF-8")
        Reader fr = new InputStreamReader(new FileInputStream(shCsv), cs)
        List<String[]> corpL = CsvUtil.loadColumnsFromCsvFile(fr, ["A股代码", "证券简称"] as String[], '\t' as char)
        addAll(corpL)
    }

    void addShenzhen() {
        Charset cs = Charset.forName("UTF-8")
        Reader fr = new InputStreamReader(new FileInputStream(szCsv), cs)
        List<String[]> corpL = CsvUtil.loadColumnsFromCsvFile(fr, ["A股代码", "A股简称"] as String[], '\t' as char)
        addAll(corpL)
    }

    void addAll(List<String[]> corpL) {
        corpL.each {
            CorpInfo corpInfo = new CorpInfo(id: it[0], name: it[1])
            corpInfoList.add(corpInfo)
            corpInfoMap.put(corpInfo.id, corpInfo)
        }
    }
}
