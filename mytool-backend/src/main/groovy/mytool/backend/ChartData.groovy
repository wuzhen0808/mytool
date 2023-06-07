package mytool.backend

import groovy.transform.CompileStatic

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
