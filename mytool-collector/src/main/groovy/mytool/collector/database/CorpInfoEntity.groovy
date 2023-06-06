package mytool.collector.database

import groovy.transform.CompileStatic;
@CompileStatic
public class CorpInfoEntity extends EntityObject {

	public static String tableName = "corpinfo";

	private String code;

	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
