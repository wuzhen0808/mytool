package mytool.collector

enum ReportType {

    NULL(0),
    ZCFZB(1),
    LRB(2),
    XJLLB(3),
    quotes(4),
    LIXI(5),
    int type

    ReportType(int type) {
        this.type = type
    }
}