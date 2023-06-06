package mytool.util.csv;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CsvHeaderRowMap extends CsvRowMap {
	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy/MM/dd");

	private SimpleDateFormat dateFormat;

	public SimpleDateFormat getDateFormat() {
		if (dateFormat != null) {
			return this.dateFormat;
		}
		CsvRow r = this.get("日期格式", false);

		if (r == null) {
			this.dateFormat = FORMAT;
		} else {
			String df = r.getString(1, true);
			this.dateFormat = new SimpleDateFormat(df);
		}
		return this.dateFormat;
	}

	public Date[] getReportDateArray() {
		CsvRow row = this.get("报告日期", true);
		//CsvRow row = this.get("报表日期", true);
		SimpleDateFormat df = this.getDateFormat();
		Date[] rt = row.getAsDateArray(df);
		return rt;
	}

}
