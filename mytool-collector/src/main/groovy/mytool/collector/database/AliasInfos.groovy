package mytool.collector.database

import groovy.transform.CompileStatic
import mytool.collector.ReportType
import mytool.util.jdbc.JdbcAccessTemplate
import mytool.util.jdbc.ResultSetProcessor

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
public class AliasInfos {

    private Map<Integer, Map<String, Integer>> reportAliasColumnMap = new HashMap<>();

    public void initialize(Connection con, JdbcAccessTemplate t) {
        this.updateCache(con, t);
    }

    private void updateCache(Connection con, JdbcAccessTemplate t) {
        this.reportAliasColumnMap.clear();
        String sql = "select reportType,aliasName,columnIndex from " + Tables.TN_ALIAS_INFO + "";
        t.executeQuery(con, sql, new ResultSetProcessor<Object>() {

            @Override
            public Object process(ResultSet rs) throws SQLException {

                while (rs.next()) {
                    Integer reportType = rs.getInt("reportType");
                    Map<String, Integer> tc = reportAliasColumnMap.get(reportType);
                    if (tc == null) {
                        tc = new HashMap<>();
                        reportAliasColumnMap.put(reportType, tc);
                    }
                    String aliasName = rs.getString("aliasName");
                    Integer columnIndex = rs.getInt("columnIndex");
                    tc.put(aliasName, columnIndex);
                }

                return null;
            }
        });

    }

    public List<Integer> getOrCreateColumnIndexByAliasList(ReportDataAccessor dbs, final ReportType reportType,
                                                           List<String> aliasList) {
        Map<String, Integer> aliasMap = reportAliasColumnMap.get(reportType.type);
        List<Integer> rt = new ArrayList<>();
        for (final String alias : aliasList) {
            Integer columnIndex = null;
            if (aliasMap != null) {
                columnIndex = aliasMap.get(alias);
            }

            if (columnIndex == null) {

                columnIndex = dbs.execute(new JdbcAccessTemplate.JdbcOperation<Integer>() {

                    @Override
                    public Integer execute(Connection con, JdbcAccessTemplate t) {

                        int tmpIndex = getMaxColumIndex(con, t, reportType) + 1;

                        String sql = "insert into " + Tables.TN_ALIAS_INFO + "(reportType,aliasName,columnIndex) values(?,?,?)";
                        t.executeUpdate(con, sql, new Object[]{reportType, alias, tmpIndex});

                        updateCache(con, t);

                        return tmpIndex;
                    }
                }, true);

            }

            rt.add(columnIndex);
        }
        return rt;

    }

    protected int getMaxColumIndex(Connection con, JdbcAccessTemplate t, ReportType reportType) {
        String sql = "select max(columnIndex) from " + Tables.TN_ALIAS_INFO + " where reportType=?";

        return t.executeQuery(con, sql, reportType.type, new ResultSetProcessor<Integer>() {

            @Override
            Integer process(ResultSet rs) throws SQLException {
                while (rs.next()) {
                    return rs.getInt(1);
                }
                return null;
            }
        });

    }

}
