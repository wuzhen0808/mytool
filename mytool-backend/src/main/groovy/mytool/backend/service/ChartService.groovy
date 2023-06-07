package mytool.backend.service

import groovy.transform.CompileStatic
import mytool.backend.ChartData

@CompileStatic
interface ChartService {
    ChartData getChartData()
}