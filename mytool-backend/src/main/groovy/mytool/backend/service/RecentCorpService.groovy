package mytool.backend.service

import groovy.transform.CompileStatic
import mytool.backend.CorpInfo

@CompileStatic
interface RecentCorpService {

    List<CorpInfo> getRecentCorps()

    void addRecent(CorpInfo corpInfo)

}
