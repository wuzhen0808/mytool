package mytool.backend.service

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.backend.ChartModel

@CompileStatic
interface ChartService {

    ChartData getChartDataById(String corpId, String chartId)

    ChartData getChartData(String corpId, ChartModel chartModel)

}