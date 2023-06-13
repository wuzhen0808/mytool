package mytool.backend.service.impl

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@CompileStatic
@Component
class JdbcTemplateImpl {
    @Autowired
    JdbcTemplate jdbcTemplate

    JdbcTemplate getTemplate() {
        return jdbcTemplate
    }
}
