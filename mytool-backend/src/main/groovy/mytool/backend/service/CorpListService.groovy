package mytool.backend.service

import groovy.transform.CompileStatic

@CompileStatic
interface CorpListService {
    List<String> corpList()

    String getCorpName(String corpId)
}