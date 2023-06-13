package mytool.backend.conf

import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource

import javax.sql.DataSource

@CompileStatic
@Configuration

class JdbcConfig {

    @Value('${jdbc.url}')
    String url

    @Value('${jdbc.username}')
    String username

    @Value('${jdbc.password}')
    String password

    @Bean
    DataSource dataSource() {

        DriverManagerDataSource ds = new DriverManagerDataSource()
        ds.setDriverClassName("org.h2.Driver")
        ds.setUrl(url)
        ds.setUsername(username)
        ds.setPassword(password)

        return ds
    }
}
