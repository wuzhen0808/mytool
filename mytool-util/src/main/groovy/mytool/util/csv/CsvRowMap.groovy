package mytool.util.csv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvRowMap {

	public Map<String, CsvRow> map = new HashMap<String, CsvRow>();
	public List<String> keyList = new ArrayList<>();

	public CsvRowMap() {

	}

	public void put(String key, CsvRow value) {
		CsvRow old = this.map.put(key, value);
		if (old == null) {
			keyList.add(key);//
		}
	}

	public CsvRow get(String key, boolean force) {
		CsvRow rt = map.get(key);
//		if("报表日期".equals(key)){
//			for(String keyI:map.keySet()){
//				boolean isE = "报表日期".equals(keyI);
//				boolean isE2 = key.equals(keyI);
//				boolean isE3 = true;
//			}
//			boolean c1 = map.keySet().contains(key);
//			boolean c2 = map.keySet().contains("报表日期");
//			boolean c3 = map.containsKey(key);
//			boolean c4 = map.containsKey("报表日期");
//			
//		}
		
		if (rt == null && force) {
			throw new RuntimeException("no value for key:" + key + ",all keys:" + map.keySet());
		}
		return rt;

	}

}
