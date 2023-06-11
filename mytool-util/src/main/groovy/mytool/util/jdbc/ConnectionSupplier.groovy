package mytool.util.jdbc

import groovy.transform.CompileStatic

import java.sql.Connection
import java.util.function.Supplier

@CompileStatic
interface ConnectionSupplier extends Supplier<Connection> {

}
