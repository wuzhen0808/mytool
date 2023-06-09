package mytool.backend

import groovy.transform.CompileStatic
import mytool.collector.database.MetricRecord

import java.text.SimpleDateFormat

@CompileStatic
class ChartData {
    static class Builder {
        String type
        Data data
        Options options

        Builder type(String type) {
            this.type = type
            return this
        }

        Builder data(MetricRecord[] report) {
            return data(report as List<MetricRecord>)
        }

        Builder data(List<MetricRecord> report) {

            Date[] dates = MetricRecord.collectDates(report)
            data = new Data()
            data.labels = dates.collect({ new SimpleDateFormat("yyyyMMdd").format(it) })
            data.datasets = []
            Map<String, Map<String, BigDecimal[]>> map = MetricRecord.groupValueByCorpIdAndKey(report, dates)

            map.each {
                String corpId = it.key
                it.value.each {
                    DataSet ds = new DataSet()
                    ds.label = "${corpId}:${it.key}"
                    ds.data = it.value as List<BigDecimal>
                    data.datasets.add(ds)
                }
            }
            return this
        }

        Builder data(Data data) {
            this.data = data
            return this
        }

        Builder options(Options options) {
            this.options = options
            return this
        }

        ChartData build() {
            ChartData chartData = new ChartData(type: 'bar', data: data, options: options)
            return chartData
        }
    }

    static class DataSet {
        String label
        List<BigDecimal> data
        int borderWidth
    }

    static class Data {
        List<String> labels
        List<DataSet> datasets
    }

    static class Options {
        Scales scales
    }

    static class Scales {
        Scale y
    }

    static class Scale {
        boolean beginAtZero
    }
    String type
    Data data
    Options options


}
