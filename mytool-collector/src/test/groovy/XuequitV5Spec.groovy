import mytool.collector.xueqiu.v5.XQV5DataCollector
import mytool.collector.xueqiu.v5.XQV5DataWasher
import spock.lang.Ignore
import spock.lang.Specification

import java.nio.charset.Charset

@Ignore
class XuequitV5Spec extends Specification {

    def "testXQV5Collecter"() {

        File folder = new File("d:\\openstock\\xueqiuv5\\raw")
        XQV5DataCollector dc = new XQV5DataCollector(folder)
        dc.cookie("xq_a_token=0f82d04ce8d5080cc888fa50c97b841494e931dd;")
        dc.types(XQV5DataCollector.balance, XQV5DataCollector.income, XQV5DataCollector.cash_flow)
        dc.pauseInterval(1 * 1000);
        dc.refresh(true)
        List<String> corpCodeL = ["000001"]
        dc.corpCodes(corpCodeL)
        dc.run()

        expect:
        1 == 1
    }

    def "testXQV5Washer"() {

        File folder = new File("d:\\openstock\\xueqiuv5\\raw")
        File folder2 = new File("d:\\openstock\\xueqiuv5\\washed")
        XQV5DataWasher w = new XQV5DataWasher(folder, Charset.forName("UTF-8"), folder2);
        w.types(XQV5DataCollector.balance, XQV5DataCollector.income, XQV5DataCollector.cash_flow)
        w.refresh(true)
        w.corpIds("600927")
        w.run()
        expect:
        1 == 1
    }
}
