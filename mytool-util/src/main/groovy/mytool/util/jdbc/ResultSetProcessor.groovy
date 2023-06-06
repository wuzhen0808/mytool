package mytool.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetProcessor<T> {
	public T process(ResultSet rs) throws SQLException;
}