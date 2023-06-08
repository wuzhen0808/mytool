package mytool.collector

import groovy.transform.CompileStatic

@CompileStatic
class MetricType {
    ReportType reportType
    String name

    static MetricType valueOf(String name) {
        return valueOf(ReportType.NULL, name)
    }

    static MetricType valueOf(ReportType reportType, String name) {
        return new MetricType(reportType: reportType, name: name)
    }

    static MetricType parse(String string) {
        String[] comps = string.split(":")
        if (comps.length == 1) {
            return MetricType.valueOf(ReportType.NULL, comps[0])
        } else if (comps.length == 2) {
            return MetricType.valueOf(ReportType.valueOf(comps[0]), comps[1])
        } else {
            throw new RtException("cannot parse as metric type:${string}")
        }
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
