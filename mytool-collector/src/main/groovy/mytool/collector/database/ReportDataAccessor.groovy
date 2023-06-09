package mytool.collector.database

import groovy.transform.CompileStatic
import mytool.collector.MetricType
import mytool.collector.MetricTypes
import mytool.collector.ReportType
import mytool.collector.RtException
import mytool.util.jdbc.ConnectionProvider
import mytool.util.jdbc.JdbcAccessTemplate
import mytool.util.jdbc.ResultSetProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
class ReportDataAccessor extends JdbcAccessTemplate {

    private static final Logger LOG = LoggerFactory.getLogger(ReportDataAccessor.class);

    ConnectionProvider pool;

    private static Map<String, ReportDataAccessor> MAP = new HashMap<>();

    private static List<DBUpgrader> upgraderList = new ArrayList<DBUpgrader>();

    static {
        upgraderList.add(new DBUpgrader_001());
        upgraderList.add(new DBUpgrader_002());
        upgraderList.add(new DBUpgrader_003());
    }
    //the target data version to be upgraded to.
    private DataVersion targetDataVersion = DataVersion.V_0_0_3;

    private DataVersion dataVersion;

    private AliasInfos aliasInfos = new AliasInfos();

    ReportDataAccessor(ConnectionProvider pool) {
        this.pool = pool;
    }

    void mergeReport(final ReportType reportType, final String corpId, final Date reportDate, List<String> aliasList,
                     final List<BigDecimal> valueList) {

        final List<Integer> columnIndexList = this.aliasInfos.getOrCreateColumnIndexByAliasList(this, reportType, aliasList);

        this.execute(new JdbcOperation<Object>() {

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

    List<MetricRecord> queryReport(final ReportType reportType, final String corpId, final Date[] reportDateList, final List<String> aliasList) {
        final List<Integer> columnIndexList = this.aliasInfos.getOrCreateColumnIndexByAliasList(this, reportType, aliasList);
        return this.execute(new JdbcOperation<List<MetricRecord>>() {

            @Override
            List<MetricRecord> execute(Connection con, JdbcAccessTemplate t) {
                StringBuffer sql = new StringBuffer();
                sql.append("select corpId, reportDate");

                for (int i = 0; i < columnIndexList.size(); i++) {
                    sql.append(",")
                    Integer cIdx = columnIndexList.get(i);
                    sql.append(Tables.getReportColumn(cIdx))
                    sql.append(" as ").append(aliasList.get(i))
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
                            aliasList.each {
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

    boolean isReportExist(int reportType, String corpId, Date reportDate) {
        return false;
    }

    void initialize() {

        final String schema = "test";

        this.execute(new JdbcOperation<Object>() {

            @Override
            Object execute(Connection con, JdbcAccessTemplate t) {
                final List<String> schemaList = new ArrayList<String>();
                t.executeQuery(con, "show schemas", new ResultSetProcessor<Object>() {

                    @Override
                    Object process(ResultSet rs) throws SQLException {
                        while (rs.next()) {
                            String name = rs.getString(1);
                            schemaList.add(name);
                            // System.out.println(rs.getString(1));
                        }
                        return null;
                    }
                });

                if (!schemaList.contains(schema.toUpperCase())) {
                    t.executeUpdate(con, "create schema " + schema);
                }
                if (!isTableExists(con, t, Tables.TN_PROPERTY)) {
                    // create property table
                    {

                        String sql = "create table " + Tables.TN_PROPERTY + "(category varchar,key varchar,value varchar,";
                        sql += "primary key(category,key))";
                        t.executeUpdate(con, sql);

                    }
                    {
                        String sql = "insert into " + Tables.TN_PROPERTY + "(category,key,value)values(?,?,?)";
                        t.executeUpdate(con, sql,
                                new Object[]{"core", "data-version", DataVersion.V_UNKNOW.toString()});
                    }

                }

                upgrade(con, t);

                aliasInfos.initialize(con, t);
                return null;
            }
        }, true);

    }

    public <T> T execute(JdbcOperation<T> op, boolean transaction) {

        try {
            Connection con = pool.openConnection();
            try {

                if (transaction) {

                    boolean oldAuto = con.getAutoCommit();
                    con.setAutoCommit(false);
                    try {
                        return op.execute(con, this);
                    } catch (Exception e) {
                        con.rollback();
                        throw RtException.toRtException(e);
                    } finally {
                        con.commit();
                        con.setAutoCommit(oldAuto);
                    }

                } else {
                    return op.execute(con, this);
                }
            } finally {
                con.close();
            }
        } catch (SQLException e) {
            throw RtException.toRtException(e);
        }
    }

    private void upgrade(Connection con, JdbcAccessTemplate t) {
        this.dataVersion = resolveDataVersion(con, t);

        LOG.info("dataVersion:" + dataVersion + ",targetVersion:" + this.targetDataVersion);
        while (true) {
            if (this.dataVersion == this.targetDataVersion) {
                // upgrade complete
                break;
            }

            DataVersion pre = this.dataVersion;
            DataVersion dv = this.tryUpgrade(con, t);
            if (dv == null) {
                LOG.warn("cannot upgrade from:" + pre + " to target:" + this.targetDataVersion);
                break;
            }
            LOG.info("successfuly upgrade from:" + pre + " to target:" + dv);
        }

    }

    private DataVersion tryUpgrade(Connection con, JdbcAccessTemplate t) {
        DataVersion rt = null;
        for (DBUpgrader up : this.upgraderList) {
            if (this.dataVersion == up.getSourceVersion()) {
                up.upgrade(con, t);//
                rt = up.getTargetVersion();
            }
        }
        if (rt != null) {
            this.dataVersion = rt;
        }
        return rt;
    }

    private DataVersion resolveDataVersion(Connection con, JdbcAccessTemplate t) {
        String sql = "select category,key,value from " + Tables.TN_PROPERTY + " t where t.category=? and t.key=?";
        List<Object[]> ll = t.executeQuery(con, sql, new Object[]{"core", "data-version"});
        if (ll.isEmpty()) {
            throw new RtException("bad data base.");
        } else {
            Object[] row = ll.get(0);
            return DataVersion.valueOf((String) row[2]);
        }
    }

    private boolean isTableExists(Connection con, JdbcAccessTemplate t, String tableName) {
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
        return dates.collect {
            dateMap.get(it)
        } as BigDecimal[]
    }

    List<MetricRecord> getReport(ReportType reportType, String corpId, Date[] dates, String... aliasList) {
        return getReport(reportType, corpId, dates, aliasList as List<String>)
    }

    List<MetricRecord> getReport(ReportType reportType, String corpId, Date[] dates, List<String> aliases) {
        List<MetricRecord> list = this.queryReport(reportType, corpId, dates, aliases)
        //fill default values
        list.each {
            resolveDefaultValue(it)
        }
        return list
    }

    void resolveDefaultValue(MetricRecord record) {
        if (record.value) {
            return
        }
        BigDecimal decimal = MetricTypes.getDefaultValue(record.key)
        record.setValue(decimal)
    }
}
