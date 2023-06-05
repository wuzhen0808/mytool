package mytool.backend

import groovy.transform.CompileStatic

@CompileStatic
interface CorpListService {
    List<String> corpList()
}