package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.CorpInfo
import mytool.backend.service.RecentCorpService
import mytool.collector.database.Tables
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Component

import java.sql.ResultSet

@CompileStatic
@Component
class RecentCorpServiceImpl implements RecentCorpService {

    @Autowired
    JdbcTemplate jdbcTemplate

    String userId = "0"

    @Override
    List<CorpInfo> getRecentCorps() {
        String sql = "select userId,corpId,touched,corpName from ${Tables.TN_RECENT_CORP} where userId=? order by touched desc limit 100"

        return jdbcTemplate.query(sql, [userId] as Object[], { ResultSet rs, int rowNum ->
            new CorpInfo(id: rs.getString("corpId"), name: rs.getString("corpName"))
        } as RowMapper<CorpInfo>)
    }

    @Override
    void addRecent(String corpId, String corpName) {
        StringBuilder sb = new StringBuilder()
        sb.append("merge into ")
        sb.append(Tables.TN_RECENT_CORP)
        sb.append("(userId,corpId,touched,corpName)key(userId,corpId)values(?,?,?,?)")
        Object[] args = [userId, corpId, new Date(), corpName]
        jdbcTemplate.update(sb.toString(), args)

    }
}
