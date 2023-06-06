package mytool.collector.database


import groovy.transform.CompileStatic
import mytool.util.jdbc.JdbcAccessTemplate

import java.sql.Connection

@CompileStatic
public abstract class DBUpgrader {

	protected DataVersion sourceVersion;
	protected DataVersion targetVersion;

	public DBUpgrader(DataVersion source, DataVersion target) {
		this.sourceVersion = source;
		this.targetVersion = target;
	}

	public DataVersion getTargetVersion() {
		return this.targetVersion;
	}

	public void upgrade(Connection con, JdbcAccessTemplate t) {

		doUpgrade(con, t);
		doAfterUpgrade(con, t);

	}

	/**
	 * Upgrade version info .
	 * 
	 * @param con
	 * @param t
	 */
	protected void doAfterUpgrade(Connection con, JdbcAccessTemplate t) {

		String sql = "update " + Tables.TN_PROPERTY + " set value=? where category=? and key=?";
		t.executeUpdate(con, sql, new Object[] { this.targetVersion.toString(), "core", "data-version" });

	}

	public abstract void doUpgrade(Connection con, JdbcAccessTemplate t);

	public DataVersion getSourceVersion() {
		return sourceVersion;
	}

}
