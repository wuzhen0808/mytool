package mytool.collector.database

import groovy.transform.CompileStatic
import org.springframework.jdbc.core.JdbcTemplate

import java.sql.Connection

@CompileStatic
public class DBUpgrader_002 extends DBUpgrader {

    public DBUpgrader_002() {
        super(DataVersion.V_0_0_1, DataVersion.V_0_0_2);
    }

    @Override
    public void doUpgrade(JdbcTemplate t) {
        //create corpInfo
        {
            String sql = "create table " + Tables.TN_CORP_INFO + "(corpId varchar,corpName varchar,";
            sql += "primary key(corpId))";
            t.update(sql);
        }

    }

}
