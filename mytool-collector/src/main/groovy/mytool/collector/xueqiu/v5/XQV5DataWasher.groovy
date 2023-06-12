package mytool.collector.xueqiu.v5

import au.com.bytecode.opencsv.CSVWriter
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import mytool.collector.AbstractDataWasher
import mytool.collector.RtException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.Charset
import java.text.SimpleDateFormat

@CompileStatic
class XQV5DataWasher extends AbstractDataWasher {

    static final Logger LOG = LoggerFactory.getLogger(XQV5DataWasher.class)

    XQV5DataWasher(File sourceDir, Charset sourceCharSet, File targetDir) {
        super(sourceDir, sourceCharSet, targetDir)
    }

    @Override
    protected boolean isAcceptFile(File file) {

        return file.getName().endsWith(".json")
    }

    @Override
    protected String resolveCodeFromFileName(String type, File file) {
        String name = file.getName()
        return name.substring(type.length(), name.length() - ".json".length());
    }

    List<String> getColumnAsRow(String name, Map json) {

        return (json["data"]["list"] as List<Map>).collect({
            Object value = it.get(name)
            if (value) {
                if (name == "report_date") {
                    long report_date = value as long
                    return new SimpleDateFormat("yyyyMMdd").format(new Date(report_date))
                } else if (name == "ctime") {
                    throw new RtException("not support")
                } else if (name == "report_name") {
                    throw new RtException("not support")
                } else {
                    return (((value as List<String>).get(0)) as BigDecimal) as String
                }
            } else {
                return null
            }
        })

    }

    String[] buildReportDateRow(Map json) {
        List<String> dateList = getColumnAsRow("report_date", json)
        dateList.add(0, "报告日期")
        return dateList as String[]
    }

    List<String> getAllColumnNames(Map json) {
        List<String> keyList = []
        Set<String> keySet = []
        List<Map> list = json["data"]["list"] as List<Map>
        list.each({
            keySet.addAll(it.keySet() as Set<String>)
        })
        keySet.remove("report_date")
        keySet.remove("ctime")
        keySet.remove("report_name")

        return keySet as List<String>
    }

    @Override
    protected void process(File file, String type, String code, Reader fr, CSVWriter w) throws IOException {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Map json = jsonSlurper.parse(fr) as Map

        w.writeNext(new String[]{"Header", ""})
        w.writeNext(new String[]{"日期格式", "yyyyMMdd"})
        w.writeNext(new String[]{"公司代码", code})
        w.writeNext(new String[]{"单位", "1"})
        w.writeNext(new String[]{"备注", type})
        String[] reportDateRow = buildReportDateRow(json)
        w.writeNext(reportDateRow)
        w.writeNext(new String[]{"Body", ""})

        List<String> names = getAllColumnNames(json)

        names.each { String name ->
            List<String> row = getColumnAsRow(name, json)
            row.add(0, name)
            w.writeNext(row as String[]);
        }

    }
}
