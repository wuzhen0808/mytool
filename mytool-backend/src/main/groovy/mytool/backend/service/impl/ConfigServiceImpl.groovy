package mytool.backend.service.impl

import groovy.json.JsonSlurper
import mytool.backend.service.ConfigService
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct

@Component
class ConfigServiceImpl implements ConfigService {

    private String dataFolderPath = "d:\\openstock"
    private String confFilePath = "d:\\openstock\\conf"
    private Map conf

    @PostConstruct
    void init(){
        conf = new JsonSlurper().parse(new File(confFilePath))
    }
    @Override
    File getDataFolder(String... childPath) {

        if (childPath.length > 0) {
            return new File(dataFolderPath + File.separator + childPath.join(File.separator))
        } else {
            return new File(dataFolderPath)
        }
    }

    @Override
    String getXueQiuCookies() {
        return conf['xueqiu.cookies'] as String
    }
}
