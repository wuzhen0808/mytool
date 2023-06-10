package mytool.util.jdbc;

import java.sql.Connection;
import java.sql.SQLException
import java.util.function.Supplier;

interface ConnectionProvider extends Supplier<Connection>{

}
