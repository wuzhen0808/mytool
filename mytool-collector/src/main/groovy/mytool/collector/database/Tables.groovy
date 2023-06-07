package mytool.collector.database

import groovy.transform.CompileStatic
import mytool.collector.ReportType;

@CompileStatic
class Tables {
    public static final String TN_PROPERTY = "property";
    public static final String TN_ALIAS_INFO = "alias_info";
    public static final String TN_CORP_INFO = "corp_info";

    public static final int REPORT_TABLE_COLUMNS = 1000;
    public static final int REPORT_TABLES = 100;


    static String getReportTable(ReportType reportType) {
        return "corp_report_" + reportType.type;
    }

    static String getReportColumn(int columnIndex) {
        return "d_" + columnIndex;
    }

}
