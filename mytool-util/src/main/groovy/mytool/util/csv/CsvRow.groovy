package mytool.util.csv;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class CsvRow {

	private int lineNumber;
	private String[] line;

	public CsvRow(int num, String[] line) {
		this.lineNumber = num;
		this.line = line;
	}

	public String getString(int idx, boolean force) {
		if (idx >= this.size()) {
			throw new RuntimeException(
					"index overflow,idx:" + idx + ",ln:" + this.lineNumber + ",line:" + Arrays.asList(this.line));
		}

		String rt = this.line[idx];
		rt = rt.trim();
		if (rt.length() == 0) {
			rt = null;
		}
		if (rt == null && force) {
			throw new RuntimeException(
					"value is null,idx:" + idx + ",ln:" + this.lineNumber + ",line:" + Arrays.asList(this.line));
		}
		return rt;
	}

	public int size() {
		return this.line.length;
	}

	public BigDecimal getAsBigDecimal(int idx, boolean force) {

		String valueS = getString(idx, false);
		if (valueS == null) {
			if (force) {
				throw new RuntimeException("no value for idx:" + idx);
			}
			return null;
		}
		try {

			BigDecimal rt = new BigDecimal(valueS);
			return rt;
		} catch (NumberFormatException e) {
			throw new RuntimeException("row:" + this.lineNumber + ",idx:" + idx + ",valueS:" + valueS, e);
		}
	}

	public Date getAsDate(int idx, SimpleDateFormat df) {
		String valueS = getString(idx, false);
		if (valueS == null) {
			return null;
		}
		Date rt;
		try {
			rt = df.parse(valueS);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return rt;

	}

	public Date[] getAsDateArray(SimpleDateFormat df) {
		//
		Date[] rt = new Date[this.size() - 1];
		for (int i = 0; i < rt.length; i++) {
			rt[i] = this.getAsDate(i + 1, df);
		}
		return rt;
	}
}
