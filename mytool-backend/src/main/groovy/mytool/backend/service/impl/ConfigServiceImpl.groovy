package mytool.backend.service.impl

import mytool.backend.service.ConfigService
import org.springframework.stereotype.Component

@Component
class ConfigServiceImpl implements ConfigService {

    private String dataFolderPath = "d:\\openstock"

    @Override
    File getDataFolder(String... childPath) {

        if (childPath.length > 0) {
            return new File(dataFolderPath + File.separator + childPath.join(File.separator))
        } else {
            return new File(dataFolderPath)
        }
    }

    @Override
    String getXueQiuToken() {
        return "xq_a_token=0f82d04ce8d5080cc888fa50c97b841494e931dd;"
    }
}
