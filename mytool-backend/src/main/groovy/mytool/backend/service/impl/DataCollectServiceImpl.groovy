package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.service.ConfigService
import mytool.backend.service.CorpListService
import mytool.backend.service.DataCollectService
import mytool.backend.service.TaskService
import mytool.collector.database.ReportDataAccessor
import mytool.collector.wash.WashedFileLoader
import mytool.collector.xueqiu.v5.XQV5DataCollector
import mytool.collector.xueqiu.v5.XQV5DataWasher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import java.nio.charset.Charset

@CompileStatic
@Component
class DataCollectServiceImpl implements DataCollectService {

    @Autowired
    ConfigService configService

    @Autowired
    CorpListService corpListService

    @Autowired
    TaskService taskService

    @Autowired
    ReportDataAccessor reportDataAccessor

    File raw
    File washed

    @PostConstruct
    void init() {
        raw = configService.getDataFolder("xueqiuv5", "raw")
        washed = configService.getDataFolder("xueqiuv5", "washed")
    }

    @Override
    void collect(String corpId) {
        buildCollector([corpId]).run()
    }

    @Override
    TaskService.TaskInfo collectAll(List<String> corpIds) {
        return taskService.addTask("collect", buildCollector(corpIds))
    }

    XQV5DataCollector buildCollector(List<String> corpIds) {
        XQV5DataCollector dc = new XQV5DataCollector(raw)
        dc.cookie(configService.getXueQiuCookies())
        dc.types(XQV5DataCollector.balance, XQV5DataCollector.income, XQV5DataCollector.cash_flow)
        dc.pauseInterval(1 * 1000)
        if (corpIds == null) {
            corpIds = this.corpListService.corpList()
        }
        dc.corpCodes(corpIds)
        return dc
    }


    @Override
    void wash(String corpId) {
        buildWasher([corpId]).run()
    }

    @Override
    TaskService.TaskInfo washAll(List<String> corpIds) {

        return taskService.addTask("wash", buildWasher(corpIds))
    }

    XQV5DataWasher buildWasher(List<String> corpIds) {
        XQV5DataWasher w = new XQV5DataWasher(raw, Charset.forName("UTF-8"), washed);
        w.types(XQV5DataCollector.balance, XQV5DataCollector.income, XQV5DataCollector.cash_flow)
        w.corpIds(corpIds)
        return w
    }

    @Override
    void load(String corpId) {
        buildLoader([corpId]).run()
    }

    @Override
    TaskService.TaskInfo loadAll(List<String> corpIds) {

        return taskService.addTask("load", buildLoader(corpIds))
    }

    Runnable buildLoader(List<String> corpIds) {
        return {
            WashedFileLoader.WashedFileLoadContext flc = new WashedFileLoader.WashedFileLoadContext(reportDataAccessor)
            new WashedFileLoader().corpIds(corpIds).load(washed, flc)
        }
    }

    @Override
    void refresh(String corpId) {
        buildCollector([corpId]).run()
        buildWasher([corpId]).run()
        buildLoader([corpId]).run()
    }

    @Override
    TaskService.TaskInfo refreshAll(List<String> corpIds) {
        collectAll(corpIds)
        washAll(corpIds)
        loadAll(corpIds)
    }
}
