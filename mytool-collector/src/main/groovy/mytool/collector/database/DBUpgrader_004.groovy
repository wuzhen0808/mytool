package mytool.collector.database

import groovy.transform.CompileStatic
import org.springframework.jdbc.core.JdbcTemplate

@CompileStatic
class DBUpgrader_004 extends DBUpgrader {

    public DBUpgrader_004() {
        super(DataVersion.V_0_0_3, DataVersion.V_0_0_4);
    }

    @Override
    public void doUpgrade(JdbcTemplate t) {
        //create recent corp
        {
            String sql = "create table " + Tables.TN_RECENT_CORP + "("
            sql += "corpId varchar,"//
            sql += "userId varchar,"//
            sql += "touched datetime,"//
            sql += "corpName varchar,"//
            sql += "primary key(userId,corpId))"
            t.update(sql)
        }


    }

}
