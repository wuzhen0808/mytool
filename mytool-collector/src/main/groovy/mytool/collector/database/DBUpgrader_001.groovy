package mytool.collector.database

import groovy.transform.CompileStatic
import org.springframework.jdbc.core.JdbcTemplate

import java.sql.Connection

@CompileStatic
public class DBUpgrader_001 extends DBUpgrader {

    public DBUpgrader_001() {
        super(DataVersion.V_UNKNOW, DataVersion.V_0_0_1);
    }

    @Override
    public void doUpgrade( JdbcTemplate t) {

        {
            String sql = "create table " + Tables.TN_ALIAS_INFO + "(reportType int,aliasName varchar,columnIndex int,";
            sql += "primary key(reportType,aliasName))";
            t.update(sql);
        }

        // create report tables
        for (int j = 0; j < Tables.REPORT_TABLES; j++) {
            String sql = "create table corp_report_" + j + "(corpId varchar,corpName varchar,reportDate datetime,";
            for (int i = 0; i < Tables.REPORT_TABLE_COLUMNS; i++) {
                sql += "d_" + i + " double,";
            }
            sql += "primary key(corpId,reportDate))";
            t.update(sql);
        }
    }

}
