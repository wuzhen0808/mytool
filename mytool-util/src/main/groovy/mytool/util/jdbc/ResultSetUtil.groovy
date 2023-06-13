package mytool.util.jdbc

import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
class ResultSetUtil {

    private static Logger LOG = LoggerFactory.getLogger(ResultSetUtil.class);

    static List<Object[]> extractAsArrayList(ResultSet rs) throws SQLException {

        List<Object[]> rt = new ArrayList<>();
        int cols = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            Object[] row = new Object[cols];
            for (int i = 0; i < cols; i++) {
                row[i] = rs.getObject(i + 1);
            }
            rt.add(row);
        }
        if (LOG.isTraceEnabled()) {
            for (int i = 0; i < rt.size(); i++) {
                StringBuffer sb = new StringBuffer().append(i).append("\t");
                Object[] row = rt.get(i);

                for (int j = 0; j < cols; j++) {
                    sb.append(row[i]).append("\t");
                }
                sb.append("\t");
                LOG.trace(sb.toString());
            }
        }
        return rt;
    }
}
