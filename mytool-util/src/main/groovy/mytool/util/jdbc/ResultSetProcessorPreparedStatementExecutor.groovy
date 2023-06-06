package mytool.util.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetProcessorPreparedStatementExecutor<T> implements PreparedStatementExecutor<T> {
	private ResultSetProcessor<T> rsp;

	public ResultSetProcessorPreparedStatementExecutor(ResultSetProcessor<T> rsp) {
		this.rsp = rsp;
	}

	@Override
	public T execute(PreparedStatement ps) throws SQLException {
		ResultSet rs = ps.executeQuery();
		return rsp.process(rs);
	}

}