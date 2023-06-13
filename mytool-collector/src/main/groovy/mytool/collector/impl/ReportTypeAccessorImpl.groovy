package mytool.collector.impl

import groovy.transform.CompileStatic
import mytool.collector.ReportType
import mytool.collector.database.ReportTypeAccessor
import mytool.collector.database.Tables
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataAccessException
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.stereotype.Component

import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
@Component
class ReportTypeAccessorImpl implements ReportTypeAccessor {

    @Autowired
    JdbcTemplate template

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
        template.query(sql, [] as Object[], { ResultSet rs ->
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
        } as ResultSetExtractor<Void>)
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
        for (int i = 0; i < rt.size(); i++) {
            Integer columnIndex = rt.get(i)

            if (columnIndex == null) {
                String alias = aliasList.get(i)

                int tmpIndex = getMaxColumIndex(reportType) + 1;
                String sql = "insert into " + Tables.TN_ALIAS_INFO + "(reportType,aliasName,columnIndex) values(?,?,?)";
                template.update(sql, new Object[]{reportType.type, alias, tmpIndex});
                rt.set(i, columnIndex);
            }
        }

        return rt;

    }

    protected int getMaxColumIndex(ReportType reportType) {
        String sql = "select max(columnIndex) from " + Tables.TN_ALIAS_INFO + " where reportType=?";

        return template.query(sql, [reportType.type] as Object[], new ResultSetExtractor<Integer>() {
            @Override
            Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
                while (rs.next()) {
                    return rs.getInt(1);
                }
                return 0
            }
        })

    }

}
