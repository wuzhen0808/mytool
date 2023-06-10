package mytool.collector.database

import groovy.transform.CompileStatic
import mytool.collector.ReportType
import mytool.util.jdbc.JdbcAccessTemplate
import mytool.util.jdbc.ResultSetProcessor

import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException
import java.util.function.Supplier

@CompileStatic
class ReportTypeAccessor {

    JdbcAccessTemplate template
    Supplier<Connection> pool

    ReportTypeAccessor(Supplier<Connection> pool, JdbcAccessTemplate template) {
        this.pool = pool
        this.template = template
    }

    List<String> getMetricNames(ReportType reportType) {
        def all = getAll()
        def get = all.get(reportType.type)
        if (get == null) {
            throw new RuntimeException("no record found for type:${reportType}")
        }
        return get.keySet() as List<String>
    }

    Map<Integer, Map<String, Integer>> getAll() {

        Map<Integer, Map<String, Integer>> reportTypes = [:]

        String sql = "select reportType,aliasName,columnIndex from " + Tables.TN_ALIAS_INFO + "";
        template.executeQuery(pool, sql, [] as Object[], { ResultSet rs ->
            while (rs.next()) {
                Integer reportType = rs.getInt("reportType");
                Map<String, Integer> tc = reportTypes.get(reportType);
                if (tc == null) {
                    tc = new HashMap<>();
                    reportTypes.put(reportType, tc);
                }
                String aliasName = rs.getString("aliasName");
                Integer columnIndex = rs.getInt("columnIndex");
                tc.put(aliasName, columnIndex);
            }
            return null;
        })
        reportTypes
    }

    List<Integer> getColumnIndexByAliasList(final ReportType reportType,
                                            List<String> aliasList) {
        Map<Integer, Map<String, Integer>> reportTypes = getAll()
        Map<String, Integer> aliasMap = reportTypes.get(reportType.type);
        List<Integer> rt = new ArrayList<>();
        for (final String alias : aliasList) {
            Integer columnIndex = null;
            if (aliasMap != null) {
                columnIndex = aliasMap.get(alias);
            }
            rt.add(columnIndex)
        }
        return rt
    }

    List<Integer> getOrCreateColumnIndexByAliasList(final ReportType reportType,
                                                    List<String> aliasList) {

        List<Integer> rt = getColumnIndexByAliasList(reportType, aliasList)
        boolean refresh
        for (int i = 0; i < rt.size(); i++) {
            Integer columnIndex = rt.get(i)

            if (columnIndex == null) {
                String alias = aliasList.get(i)
                columnIndex = template.execute(pool, new JdbcAccessTemplate.JdbcOperation<Integer>() {

                    @Override
                    public Integer execute(Connection con, JdbcAccessTemplate t) {

                        int tmpIndex = getMaxColumIndex(con, t, reportType) + 1;

                        String sql = "insert into " + Tables.TN_ALIAS_INFO + "(reportType,aliasName,columnIndex) values(?,?,?)";
                        t.executeUpdate(con, sql, new Object[]{reportType.type, alias, tmpIndex});
                        return tmpIndex;
                    }
                }, true);
                refresh = true
                rt.set(i, columnIndex);
            }
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
