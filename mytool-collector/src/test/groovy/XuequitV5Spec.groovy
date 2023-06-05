import mytool.collector.xueqiu.v5.XueqiuV5DataCollector
import spock.lang.Specification

class XuequitV5Spec extends Specification {

    def "test"() {
        File folder = new File("d:\\openstock\\xueqiuv5\\raw")
        XueqiuV5DataCollector dc = new XueqiuV5DataCollector(folder)
        dc.cookie("xq_a_token=0f82d04ce8d5080cc888fa50c97b841494e931dd;")
        dc.types(XueqiuV5DataCollector.balance, XueqiuV5DataCollector.income, XueqiuV5DataCollector.cash_flow)
        dc.pauseInterval(1 * 1000);
        dc.refresh(true)
        List<String> corpCodeL = ["000001"]
        dc.corpCodes(corpCodeL)
        dc.run()
        expect:
        1 == 1
    }
}
