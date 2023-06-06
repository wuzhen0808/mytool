package mytool.backend.service

import groovy.transform.CompileStatic

@CompileStatic
interface TaskService {


    @CompileStatic
    class TaskInfo {
        String id
        String name
    }

    List<TaskInfo> taskInfoList()

    TaskInfo addTask(String name, Runnable runnable)

    boolean cancel(String id)

    boolean interrupt(String id)

    void cancelAll()
}