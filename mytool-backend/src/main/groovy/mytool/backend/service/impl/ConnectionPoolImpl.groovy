package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.service.ConfigService
import mytool.collector.database.H2ConnectionSupplier
import mytool.collector.util.EnvUtil
import mytool.util.jdbc.ConnectionSupplier
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.sql.Connection
import java.util.function.Supplier

@CompileStatic
@Component
class ConnectionPoolImpl implements ConnectionSupplier {

    @Autowired
    ConfigService configService
    Supplier<Connection> pool

    @PostConstruct
    void init() {

        File dbHome = configService.getDataFolder()
        String dbName = EnvUtil.getDbName()
        String dbUrl = "jdbc:h2:" + dbHome.getAbsolutePath().replace('\\', '/') + "/" + dbName;
        pool = H2ConnectionSupplier.newInstance(dbUrl, "sa", "sa");
    }

    @Override
    Connection get() {
        return pool.get()
    }
}
