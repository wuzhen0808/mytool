package mytool.backend.service

import groovy.transform.CompileStatic

@CompileStatic
interface DataCollectService {

    void collect(String corpId)

    TaskService.TaskInfo collectAll(List<String> corpIds)

    void wash(String corpId)

    TaskService.TaskInfo washAll(List<String> corpIds)

    void load(String corpId)

    TaskService.TaskInfo loadAll(List<String> corpIds)

    void refresh(String corpId)

    TaskService.TaskInfo refreshAll(List<String> corpIds)
}
