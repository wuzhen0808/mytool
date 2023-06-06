
package mytool.util.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CounterResultSetProcessor implements ResultSetProcessor<Long> {

	private static Logger LOG = LoggerFactory.getLogger(CounterResultSetProcessor.class);

	@Override
	public Long process(ResultSet rs) throws SQLException {

		while (rs.next()) {
			return rs.getLong(1);
		}
		return 0L;
	}

}