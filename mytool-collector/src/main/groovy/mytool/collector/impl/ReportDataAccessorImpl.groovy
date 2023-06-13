package mytool.collector.impl

import groovy.transform.CompileStatic
import mytool.collector.MetricSettings
import mytool.collector.MetricType
import mytool.collector.ReportType
import mytool.collector.RtException
import mytool.collector.database.MetricRecord
import mytool.collector.database.ReportDataAccessor
import mytool.collector.database.ReportTypeAccessor
import mytool.collector.database.Tables
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component

import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
@Component
class ReportDataAccessorImpl implements ReportDataAccessor {

    private static final Logger LOG = LoggerFactory.getLogger(ReportDataAccessorImpl.class);

    @Autowired
    ReportTypeAccessor reportTypeAccessor

    @Autowired
    JdbcTemplate template

    @Autowired
    MetricSettings metricSettings

    void mergeReport(final ReportType reportType, final String corpId, final Date reportDate, List<String> aliasList,
                     final List<BigDecimal> valueList) {

        final List<Integer> columnIndexList = this.reportTypeAccessor.getOrCreateColumnIndexByAliasList(reportType, aliasList);

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
        this.template.update(sb.toString(), ps as Object[])

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

        return this.template.query(sql.toString(), args as Object[], new ResultSetExtractor<List<MetricRecord>>() {
            @Override
            List<MetricRecord> extractData(ResultSet rs) throws SQLException, DataAccessException {
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
        })
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
        BigDecimal decimal = metricSettings.getDefaultValue(record.key)
        if (decimal == null) {
            decimal = 0
        }
        record.setValue(decimal)
    }

}
