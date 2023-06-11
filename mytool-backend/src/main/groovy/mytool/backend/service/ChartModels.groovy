package mytool.backend.service

import groovy.transform.CompileStatic
import mytool.backend.ChartModel

@CompileStatic
interface ChartModels {
    Map<String, ChartModel> getChartModelMap()

    List<ChartModel> getChartModels()
}