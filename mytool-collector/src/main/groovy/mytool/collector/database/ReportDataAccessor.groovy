package mytool.collector.database

import groovy.transform.CompileStatic
import mytool.collector.MetricType
import mytool.collector.MetricTypes
import mytool.collector.ReportType
import mytool.collector.RtException
import mytool.util.jdbc.JdbcAccessTemplate
import mytool.util.jdbc.ResultSetProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.function.Supplier

@CompileStatic
class ReportDataAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(ReportDataAccessor.class);

    Supplier<Connection> pool;

    ReportTypeAccessor reportTypeAccessor

    JdbcAccessTemplate template

    ReportDataAccessor(Supplier<Connection> pool, JdbcAccessTemplate template, ReportTypeAccessor reportTypeAccessor) {
        this.pool = pool
        this.template = template
        this.reportTypeAccessor = reportTypeAccessor
    }

    void mergeReport(final ReportType reportType, final String corpId, final Date reportDate, List<String> aliasList,
                     final List<BigDecimal> valueList) {

        final List<Integer> columnIndexList = this.reportTypeAccessor.getOrCreateColumnIndexByAliasList(reportType, aliasList);

        this.template.execute(this.pool, new JdbcAccessTemplate.JdbcOperation<Object>() {

            @Override
            Object execute(Connection con, JdbcAccessTemplate t) {
                StringBuffer sb = new StringBuffer();
                sb.append("merge into ");
                sb.append(Tables.getReportTable(reportType));
                sb.append("(corpId,reportDate,");
                for (int i = 0; i < columnIndexList.size(); i++) {
                    Integer cIdx = columnIndexList.get(i);
                    sb.append(Tables.getReportColumn(cIdx));
                    if (i < columnIndexList.size() - 1) {
                        sb.append(",");
                    }
                }

                sb.append(")key(corpId,reportDate)values(");
                sb.append("?,?,");//
                for (int i = 0; i < columnIndexList.size(); i++) {
                    sb.append("?");
                    if (i < columnIndexList.size() - 1) {
                        sb.append(",");
                    }
                }
                sb.append(")");
                List<Object> ps = new ArrayList<Object>();
                ps.add(corpId);
                ps.add(reportDate);
                ps.addAll(valueList);
                t.executeUpdate(con, sb.toString(), ps);

                return null;
            }
        }, true);

    }

    List<MetricRecord> queryReport(final ReportType reportType, final String corpId, final Date[] reportDateList) {
        List<String> aliases = this.reportTypeAccessor.getMetricNames(reportType)
        return queryReport(reportType, corpId, reportDateList, aliases)
    }
    List<MetricRecord> queryReport(final ReportType reportType, final String corpId, final Date[] reportDateList, final List<String> aliasList) {
        //fill default values
        def report = doQueryReport(reportType, corpId, reportDateList, aliasList)
        report.each {
            resolveDefaultValue(it)
        }
        return report
    }

    private List<MetricRecord> doQueryReport(final ReportType reportType, final String corpId, final Date[] reportDateList, final List<String> metrics) {

        final List<Integer> columnIndexList = this.reportTypeAccessor.getColumnIndexByAliasList(reportType, metrics);

        return this.template.execute(pool, new JdbcAccessTemplate.JdbcOperation<List<MetricRecord>>() {

            @Override
            List<MetricRecord> execute(Connection con, JdbcAccessTemplate t) {
                StringBuffer sql = new StringBuffer();
                sql.append("select corpId, reportDate");

                for (int i = 0; i < columnIndexList.size(); i++) {
                    sql.append(",")
                    Integer cIdx = columnIndexList.get(i);
                    if (cIdx == null) {
                        String alias = metrics.get(i)
                        throw new RtException("no index for the report metric:${alias}")
                    }
                    sql.append(Tables.getReportColumn(cIdx))
                    sql.append(" as ").append(metrics.get(i))
                }

                sql.append(" from ");
                sql.append(Tables.getReportTable(reportType));
                sql.append(" where 1=1");

                List<Object> args = new ArrayList<Object>();
                if (corpId != null) {
                    sql.append(" and corpId=?");
                    args.add(corpId);
                }

                if (reportDateList != null && reportDateList.length > 0) {
                    sql.append(" and reportDate in(")
                    for (int i = 0; i < reportDateList.length; i++) {
                        if (i > 0) {
                            sql.append(",")
                        }
                        sql.append("?")
                        args.add(reportDateList[i])
                    }
                    sql.append(")")

                }
                return t.executeQuery(con, sql.toString(), args, new ResultSetProcessor<List<MetricRecord>>() {

                    @Override
                    List<MetricRecord> process(ResultSet rs) throws SQLException {
                        List<MetricRecord> list = []

                        while (rs.next()) {
                            String corpIdI = rs.getString("corpId")
                            Date dateI = rs.getDate("reportDate")
                            metrics.each {
                                BigDecimal value = rs.getBigDecimal(it)
                                MetricType metricType = MetricType.valueOf(reportType, it)
                                MetricRecord record = new MetricRecord(corpId: corpId, date: dateI, key: metricType as String, value: value)
                                list.add(record)
                            }
                        }
                        return list
                    }
                });

            }
        }, false);
    }

    boolean isTableExists(Connection con, JdbcAccessTemplate t, String tableName) {
        // table_schema
        String sql = "select * from information_schema.tables where table_name=?";
        List<Object[]> ll = t.executeQuery(con, sql, tableName.toUpperCase());
        if (ll.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    BigDecimal[] getReportValues(ReportType reportType, String corpId, Date[] dates, String alias) {
        Map<Date, BigDecimal> dateMap = getReport(reportType, corpId, dates, alias).collectEntries({
            [it.date, it.value]
        })
        //sort by dates array

        BigDecimal[] rt = dates.collect {
            dateMap.get(it)
        } as BigDecimal[]

        return rt
    }

    List<MetricRecord> getReport(ReportType reportType, String corpId, Date[] dates, String... metrics) {
        return getReport(reportType, corpId, dates, metrics as List<String>)
    }

    List<MetricRecord> getReport(ReportType reportType, String corpId, Date[] dates, List<String> metrics) {
        List<MetricRecord> list = this.queryReport(reportType, corpId, dates, metrics)

        return list
    }

    void resolveDefaultValue(MetricRecord record) {
        if (record.value) {
            return
        }
        BigDecimal decimal = MetricTypes.getDefaultValue(record.key)
        if (decimal == null) {
            decimal = 0
        }
        record.setValue(decimal)
    }

    List<String> getAllMetricsByReport(ReportType reportType) {

    }
}
