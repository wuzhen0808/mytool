package mytool.backend

import groovy.transform.CompileStatic

@CompileStatic
class ChartData {
    static class Builder {
        String type
        Data data = new Data()
        Options options = new Options()
        boolean fill

        Builder type(String type) {
            this.type = type
            return this
        }

        Builder labels(List<String> labels) {
            data.labels = labels as List<String>
            return this
        }

        Builder data(Map<String, BigDecimal[]> map) {
            data.datasets = []
            map.each {
                DataSet ds = new DataSet()
                ds.label = it.key
                ds.data = it.value as List<BigDecimal>
                data.datasets.add(ds)
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

        Builder fill(boolean fill) {
            this.fill = fill
            return this
        }

        Builder stacked(boolean stacked) {
            getScaleY(true).stacked = stacked
            return this
        }

        Scale getScaleY(boolean build) {
            Scales scales = getScales(true)
            if (scales.y == null && build) {
                scales.y = new Scale()
            }
            return scales.y
        }

        Scales getScales(boolean build) {
            if (this.options.scales == null && build) {
                this.options.scales = new Scales()
            }
            return this.options.scales
        }

        ChartData build() {
            ChartData chartData = new ChartData(type: type, data: data, options: options)
            chartData.data.datasets.each {
                it.fill = this.fill
            }
            return chartData
        }
    }

    static class DataSet {
        String label
        List<BigDecimal> data
        Integer borderWidth = 1
        Boolean fill
    }

    static class Data {
        List<String> labels
        List<DataSet> datasets
    }

    static class Options {
        Scales scales
    }

    static class Scales {
        Scale x
        Scale y
    }

    static class Scale {
        Boolean beginAtZero
        Boolean stacked
        Title title = new Title()
    }

    static class Title {
        Boolean display
        String text
    }

    String type
    Data data
    Options options


}
