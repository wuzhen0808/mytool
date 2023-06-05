package mytool.backend.impl

import groovy.transform.CompileStatic
import mytool.backend.TaskService
import mytool.collector.Interruptable
import mytool.collector.RtException
import org.springframework.stereotype.Component

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

@Component
@CompileStatic
class TaskServiceImpl implements TaskService {

    ExecutorService executorService = Executors.newCachedThreadPool()

    class TaskRunner implements Runnable {
        Runnable runnable
        Future future
        String id = UUID.randomUUID().toString()
        String name
        TaskServiceImpl service
        boolean cancel
        boolean cancelable = true
        Object cancelLock = "cancelLock"

        @Override
        void run() {

            try {
                synchronized (this.cancelLock) {
                    this.cancelable = false
                    if (this.cancel) {
                        return
                    }
                }

                runnable.run()

            } finally {
                service.afterRun(this)
            }
        }

        boolean cancel() {
            synchronized (this.cancelLock) {
                if (this.cancelable) {
                    this.cancel = true
                    return true
                }
                return false
            }
        }
    }

    Map<String, TaskRunner> taskRunners = [:]

    @Override
    List<TaskInfo> taskInfoList() {
        return this.taskRunners.values().collect({
            new TaskInfo(id: it.id, name: it.name)
        })
    }

    void afterRun(TaskRunner taskRunner) {
        synchronized (this) {
            taskRunners.remove(taskRunner.id)
        }
    }

    @Override
    TaskInfo addTask(String name, Runnable runnable) {
        synchronized (this) {

            TaskRunner taskRunner = new TaskRunner()
            taskRunner.name = name
            taskRunner.runnable = runnable
            taskRunner.service = this

            Future future = executorService.submit(taskRunner)
            taskRunner.future = future
            taskRunners.put(taskRunner.id, taskRunner)
            TaskInfo taskInfo = new TaskInfo(id: taskRunner.id, name: name)
            return taskInfo
        }
    }

    @Override
    boolean cancel(String id) {
        TaskRunner taskRunner = this.taskRunners.get(id)
        if (!taskRunner) {
            throw new RtException("no such task with id:${id}")
        }

        return taskRunner.cancel()
    }

    @Override
    boolean interrupt(String id) {
        TaskRunner taskRunner = this.taskRunners.get(id)
        if (!taskRunner) {
            throw new RtException("no such task with id:${id}")
        }
        if (!(taskRunner instanceof Interruptable)) {
            throw new RtException("task not support interrupt.")
        }
        return ((Interruptable) taskRunner).interrupt()
    }

    @Override
    void cancelAll() {

    }
}
