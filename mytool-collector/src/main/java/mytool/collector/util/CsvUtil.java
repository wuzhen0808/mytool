package mytool.collector.util;

import au.com.bytecode.opencsv.CSVReader;
import mytool.collector.RtException;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvUtil {

	public static interface CsvRowHandler<T> {

		public void onRow(Map<String, Integer> colIndexMap, String[] row);

	}


	public static String getValueByColumn(String[] line, Map<String, Integer> colIndexMap, String col) {
		return getValueByColumn(line, colIndexMap, col, "");
	}

	public static String getValueByColumn(String[] line, Map<String, Integer> colIndexMap, String col, String asNull) {
		Integer idx = colIndexMap.get(col);
		if (idx == null) {
			throw new RtException("no column found:" + col + ",all are:" + colIndexMap);
		}

		String rt = line[idx];
		if (rt == null) {
			return null;
		}

		rt = rt.trim();
		if (rt.equals(asNull)) {
			rt = null;
		}

		return rt;
	}


	public static List<String> loadColumnFromCsvFile(Reader csvFile, final String columnName, final char separator) {
		final List<String> rt = new ArrayList<String>();
		CsvRowHandler<String> crh = new CsvRowHandler<String>() {

			@Override
			public void onRow(Map<String, Integer> colIndexMap, String[] row) {
				String code = getValueByColumn(row, colIndexMap, columnName);
				rt.add(code);
			}
		};

		parseCsvFileWithHeader(csvFile, crh, separator);

		return rt;
	}

	public static <T> void parseCsvFileWithHeader(Reader fr, CsvRowHandler<T> crh, char separator) {
		try {
			
			CSVReader reader = new CSVReader(fr, separator);

			// skip header1
			String[] next = reader.readNext();
			Map<String, Integer> colIndexMap = new HashMap<>();
			for (int i = 0; i < next.length; i++) {
				String key = next[i];
				colIndexMap.put(key, i);
			}

			while (true) {
				next = reader.readNext();
				if (next == null) {
					break;
				}
				crh.onRow(colIndexMap, next);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
