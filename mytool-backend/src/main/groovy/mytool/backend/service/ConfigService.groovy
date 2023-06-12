package mytool.backend.service

interface ConfigService {

    File getDataFolder(String ... childPath)

    String getXueQiuCookies()
}
