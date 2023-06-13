package mytool.collector.database

import groovy.transform.CompileStatic
import org.springframework.jdbc.core.JdbcTemplate

@CompileStatic
public class DBUpgrader_003 extends DBUpgrader {

    public DBUpgrader_003() {
        super(DataVersion.V_0_0_2, DataVersion.V_0_0_3);
    }

    @Override
    public void doUpgrade(JdbcTemplate t) {
        //create corpInfo
        {
            String sql = "drop table " + Tables.TN_CORP_INFO;
            t.update(sql);
        }
        {
            String sql = "create table " + Tables.TN_CORP_INFO + "("
            sql += "corpId varchar,"//
            sql += "corpName varchar,"//
            sql += "category varchar,"//
            sql += "fullName varchar,"//
            sql += "ipoDate datetime,"//
            sql += "province varchar,"//
            sql += "city varchar,"//
            sql += "webSite varchar,"//
            sql += "address varchar,"//
            sql += "primary key(corpId))";
            t.update(sql);
        }


    }

}
