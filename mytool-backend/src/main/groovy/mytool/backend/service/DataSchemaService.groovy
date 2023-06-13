package mytool.backend.service

import groovy.transform.CompileStatic

@CompileStatic
interface DataSchemaService {
    boolean isTableExists(String tableName)
}
