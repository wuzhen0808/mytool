package mytool.collector

import groovy.transform.CompileStatic

@CompileStatic
class MetricType {
    ReportType reportType
    String name

    static MetricType valueOf(ReportType reportType, String name) {
        return new MetricType(reportType: reportType, name: name)
    }

    @Override
    String toString() {
        return "${reportType}:${name}"
    }

    @Override
    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        MetricType that = (MetricType) o

        if (name != that.name) return false
        if (reportType != that.reportType) return false

        return true
    }

    @Override
    int hashCode() {
        int result
        result = reportType.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

}
