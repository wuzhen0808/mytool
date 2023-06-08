package mytool.collector

import groovy.transform.CompileStatic

@CompileStatic
class MetricTypes {

    static final MetricType BALANCE_TOTAL_ASSETS = valueOf(ReportType.ZCFZB, "total_assets", "总资产")

    static final MetricType BALANCE_TOTAL_LIAB = valueOf(ReportType.ZCFZB, "total_liab", "总负债")

    static final MetricType BALANCE_TOTAL_HOLDERS_EQUITY = valueOf(ReportType.ZCFZB, "total_holders_equity","股东权益")

    static final MetricType INCOME_NET_PROFIT = valueOf(ReportType.LRB, "net_profit","净利润")

    static final MetricType ROE = valueOf(ReportType.NULL, "roe","净资产收益率")

    static final MetricType YOU_XI_FU_ZAI_LV = valueOf(ReportType.NULL, "you_xi_fu_zhai_lv","有息负债率")

    static Map<String, MetricType> aliasMap = [:]

    static MetricType valueOf(ReportType reportType, String name, String... alias) {
        MetricType metricType = MetricType.valueOf(reportType, name)
        alias.each {
            aliasMap.put(it, metricType)
        }
        return metricType
    }

    MetricType[] getMetrics(String... alias) {

    }

}
