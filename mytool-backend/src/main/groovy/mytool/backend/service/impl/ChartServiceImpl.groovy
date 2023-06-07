package mytool.backend.service.impl

import groovy.transform.CompileStatic
import mytool.backend.ChartData
import mytool.backend.service.ChartService
import org.springframework.stereotype.Component

@CompileStatic
@Component
class ChartServiceImpl implements ChartService {

    @Override
    ChartData getChartData() {
        return new ChartData.Builder()
                .type("bar")
                .data(null)
                .build()
    }
}
