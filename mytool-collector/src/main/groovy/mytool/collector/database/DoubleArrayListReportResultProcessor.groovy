package mytool.collector.database

import groovy.transform.CompileStatic

import java.sql.ResultSet
import java.sql.SQLException

@CompileStatic
public class DoubleArrayListReportResultProcessor implements ReportResultProcessor<List<Double[]>> {

	@Override
	public List<Double[]> process(int reportType, List<String> aliasList, ResultSet rs) throws SQLException {
		List<Double[]> rt = new ArrayList<>();
		while (rs.next()) {
			Double[] row = new Double[aliasList.size()];
			for (int i = 0; i < row.length; i++) {
				row[i] = rs.getDouble(i + 1);
			}
			rt.add(row);
		}
		return rt;
	}

}
