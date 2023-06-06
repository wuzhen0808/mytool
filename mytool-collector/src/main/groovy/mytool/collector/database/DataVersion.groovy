package mytool.collector.database

import groovy.transform.CompileStatic

@CompileStatic
public class DataVersion {

	private static Map<Integer, Map<Integer, Map<Integer, DataVersion>>> versionMap = new HashMap<>();
	
	// unknown means not resolved yet.
	public static final DataVersion V_UNKNOW = DataVersion.addVersion(0, 0, 0);
	// v0.0.1,
	public static final DataVersion V_0_0_1 = DataVersion.addVersion(0, 0, 1);

	// v0.0.2,rename table name
	public static final DataVersion V_0_0_2 = DataVersion.addVersion(0, 0, 2);

	// v0.0.3,add property table for saving version information.
	public static final DataVersion V_0_0_3 = DataVersion.addVersion(0, 0, 3);

	public static final DataVersion V_0_0_4 = DataVersion.addVersion(0, 0, 4);

	public static final DataVersion V_0_0_5 = DataVersion.addVersion(0, 0, 5);
	
	public static final DataVersion V_0_0_6 = DataVersion.addVersion(0, 0, 6);
	
	public static final DataVersion V_0_0_7 = DataVersion.addVersion(0, 0, 7);
	
	public static final DataVersion V_0_0_8 = DataVersion.addVersion(0, 0, 8);
	
	public static final DataVersion V_0_0_9 = DataVersion.addVersion(0, 0, 9);
	
	public static final DataVersion V_0_0_10 = DataVersion.addVersion(0, 0, 10);
	
	public static final DataVersion V_0_0_11 = DataVersion.addVersion(0, 0, 11);
	
	public static final DataVersion V_0_0_12 = DataVersion.addVersion(0, 0, 12);
	
	public static final DataVersion V_0_0_13 = DataVersion.addVersion(0, 0, 13);
		
	private int[] verionNumbers;

	static {

	}

	private static DataVersion addVersion(int i, int j, int k) {
		Map<Integer, Map<Integer, DataVersion>> map1 = versionMap.get(i);
		if (map1 == null) {
			map1 = new HashMap<>();
			versionMap.put(i, map1);
		}
		Map<Integer, DataVersion> map2 = map1.get(j);
		if (map2 == null) {
			map2 = new HashMap<>();
			map1.put(j, map2);
		}

		DataVersion dv = map2.get(k);
		if (dv != null) {
			throw new RuntimeException("duplicated");
		}
		dv = new DataVersion(i, j, k);
		map2.put(k, dv);
		return dv;
	}

	private DataVersion(int major, int minor, int variant) {
		this.verionNumbers = new int[] { major, minor, variant };
	}

	static DataVersion valueOf(int i, int j, int k) {
		Map<Integer, Map<Integer, DataVersion>> map1 = versionMap.get(i);
		if (map1 == null) {
			return null;
		}
		Map<Integer, DataVersion> map2 = map1.get(j);
		return map2 == null ? null : map2.get(k);
	}

	public int getMajor() {
		return this.verionNumbers[0];
	}

	public int getMinor() {
		return this.verionNumbers[1];
	}

	public int getVariant() {
		return this.verionNumbers[2];
	}

	public static DataVersion valueOf(String value) {
		//
		String[] vs = value.split("\\.");
		int ma = Integer.parseInt(vs[0]);
		int mi = Integer.parseInt(vs[1]);
		int va = Integer.parseInt(vs[2]);

		return valueOf(ma, mi, va);
	}

	public boolean isGreatOrEquals(DataVersion dv) {
		for (int i = 0; i < 3; i++) {
			if (this.verionNumbers[i] > dv.verionNumbers[i]) {
				return true;
			} else if (this.verionNumbers[i] < dv.verionNumbers[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return this.verionNumbers[0] + "." + this.verionNumbers[1] + "." + this.verionNumbers[2];
	}

}
