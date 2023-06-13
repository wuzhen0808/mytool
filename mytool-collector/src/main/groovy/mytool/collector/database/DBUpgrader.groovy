package mytool.collector.database

import groovy.transform.CompileStatic
import org.springframework.jdbc.core.JdbcTemplate

@CompileStatic
abstract class DBUpgrader {

    protected DataVersion sourceVersion;
    protected DataVersion targetVersion;

    public DBUpgrader(DataVersion source, DataVersion target) {
        this.sourceVersion = source;
        this.targetVersion = target;
    }

    public DataVersion getTargetVersion() {
        return this.targetVersion;
    }

    public void upgrade(JdbcTemplate t) {

        doUpgrade(t);
        doAfterUpgrade(t);

    }

    /**
     * Upgrade version info .
     *
     * @param con
     * @param t
     */
    protected void doAfterUpgrade(JdbcTemplate t) {
        String sql = "update " + Tables.TN_PROPERTY + " set value=? where category=? and key=?";
        t.update(sql, new Object[]{this.targetVersion.toString(), "core", "data-version"});

    }

    public abstract void doUpgrade(JdbcTemplate t);

    public DataVersion getSourceVersion() {
        return sourceVersion;
    }

}
