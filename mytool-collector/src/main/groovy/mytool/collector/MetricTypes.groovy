package mytool.collector

import groovy.transform.CompileStatic
import mytool.collector.MetricType
import mytool.collector.ReportType
import static mytool.collector.MetricType.valueOf

@CompileStatic
class MetricTypes {

    static final MetricType BALANCE_TOTAL_ASSETS = valueOf(ReportType.ZCFZB, "total_assets")

    static final MetricType BALANCE_TOTAL_HOLDERS_EQUITY = valueOf(ReportType.ZCFZB, "total_holders_equity")

    static final MetricType INCOME_NET_PROFIT = valueOf(ReportType.LRB, "net_profit")
    static final MetricType ROE = valueOf(ReportType.NULL, "roe")


}
