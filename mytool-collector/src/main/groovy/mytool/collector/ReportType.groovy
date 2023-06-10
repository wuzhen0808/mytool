package mytool.collector

enum ReportType {

    ZCFZB(1),
    LRB(2),
    XJLLB(3),
    quotes(4),
    LIXI(5),
    int type
    static Map<Object, ReportType> types = [:]
    static {
        ReportType.values().each {
            add(it)
        }
    }

    static void add(ReportType reportType) {
        if (types.put(reportType.type as String, reportType)) {
            throw new RuntimeException("duplicated report id:${reportType.type}")
        }
        if (types.put(reportType.name(), reportType)) {
            throw new RuntimeException("duplicated report name:${reportType.name()}")
        }
    }

    ReportType(int type) {
        this.type = type
    }

    static ReportType get(Object key) {
        return types.get(key as String)
    }

    static ReportType get(int index) {
        return get(index as Object)
    }

}