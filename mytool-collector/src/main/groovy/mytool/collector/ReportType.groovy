package mytool.collector

enum ReportType {

    ZCFZB(1),
    LRB(2),
    XJLLB(3),
    quotes(4),
    LIXI(5),
    int type
    static Map<Integer, ReportType> types = [:]
    static {
        types.put(ZCFZB.type, ZCFZB)
        types.put(LRB.type, LRB)
        types.put(XJLLB.type, XJLLB)
        types.put(LIXI.type, LIXI)
    }

    ReportType(int type) {
        this.type = type
    }

    static ReportType valueOf(int index) {
        return types.get(index)
    }

}