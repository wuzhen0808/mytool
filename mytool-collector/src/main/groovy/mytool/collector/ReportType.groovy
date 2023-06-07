package mytool.collector

enum ReportType {

    ZCFZB(1),
    LRB(2),
    XJLLB(3),
    quotes(4),
    LIXI(5);
    int type

    ReportType(int type) {
        this.type = type
    }
}