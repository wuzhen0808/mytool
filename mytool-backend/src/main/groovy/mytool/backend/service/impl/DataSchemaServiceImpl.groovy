package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.service.DataSchemaService
import mytool.collector.RtException
import mytool.collector.database.*
import mytool.util.jdbc.ResultSetUtil
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.ResultSetExtractor
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.sql.ResultSet

@CompileStatic
@Component
class DataSchemaServiceImpl implements DataSchemaService {

    private static Logger LOG = LoggerFactory.getLogger(DataSchemaServiceImpl.class)

    private static List<DBUpgrader> upgraderList = new ArrayList<DBUpgrader>();

    static {
        upgraderList.add(new DBUpgrader_001())
        upgraderList.add(new DBUpgrader_002())
        upgraderList.add(new DBUpgrader_003())
        upgraderList.add(new DBUpgrader_004())
    }

    @Autowired
    private JdbcTemplate template

    @PostConstruct
    void init() {

        final String schema = "test";

        final List<String> schemaList = template.query("show schemas", [] as Object[], { ResultSet rs, int rowNum ->
            rs.getString(1)
        } as RowMapper<String>)

        if (!schemaList.contains(schema.toUpperCase())) {
            template.update("create schema " + schema)
        }

        if (!isTableExists(Tables.TN_PROPERTY)) {
            // create property table
            {
                String sql = "create table " + Tables.TN_PROPERTY + "(category varchar,key varchar,value varchar,";
                sql += "primary key(category,key))";
                template.update(sql);
            }
            {
                String sql = "insert into " + Tables.TN_PROPERTY + "(category,key,value)values(?,?,?)";
                template.update(sql, new Object[]{"core", "data-version", DataVersion.V_UNKNOW.toString()});
            }

        }

        upgrade();


    }
    //the target data version to be upgraded to.
    private DataVersion targetDataVersion = DataVersion.V_0_0_4

    private DataVersion dataVersion;

    void upgrade() {
        this.dataVersion = resolveDataVersion();

        LOG.info("dataVersion:" + dataVersion + ",targetVersion:" + this.targetDataVersion);
        while (true) {
            if (this.dataVersion == this.targetDataVersion) {
                // upgrade complete
                break;
            }

            DataVersion pre = this.dataVersion;
            DataVersion dv = this.tryUpgrade();
            if (dv == null) {
                LOG.warn("cannot upgrade from:" + pre + " to target:" + this.targetDataVersion);
                break;
            }
            LOG.info("successfuly upgrade from:" + pre + " to target:" + dv);
        }

    }

    private DataVersion tryUpgrade() {
        DataVersion rt = null;
        for (DBUpgrader up : this.upgraderList) {
            if (this.dataVersion == up.getSourceVersion()) {
                up.upgrade(template);//
                rt = up.getTargetVersion();
            }
        }
        if (rt != null) {
            this.dataVersion = rt;
        }
        return rt;
    }

    private DataVersion resolveDataVersion() {
        String sql = "select category,key,value from " + Tables.TN_PROPERTY + " t where t.category=? and t.key=?";
        List<Object[]> ll = template.query(sql, new Object[]{"core", "data-version"}, { ResultSet rs ->
            return ResultSetUtil.extractAsArrayList(rs)
        } as ResultSetExtractor<List<Object[]>>)

        if (ll.isEmpty()) {
            throw new RtException("bad data base.");
        } else {
            Object[] row = ll.get(0);
            return DataVersion.valueOf((String) row[2]);
        }
    }


    boolean isTableExists(String tableName) {
        // table_schema
        String sql = "select * from information_schema.tables where table_name=?";
        List<Object[]> ll = template.query(sql, [tableName.toUpperCase()] as Object[], { ResultSet it ->
            ResultSetUtil.extractAsArrayList(it)
        } as ResultSetExtractor<List<Object[]>>)
        if (ll.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }


}
