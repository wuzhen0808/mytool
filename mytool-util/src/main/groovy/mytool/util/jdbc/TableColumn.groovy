package mytool.util.jdbc;

public class TableColumn {

	protected String name;

	public String getName() {
		return name;
	}

	public TableColumn(String name) {
		this.name = name;
	}

	public static TableColumn getInstance(String string) {
		//
		return new TableColumn(string);
	}
}
