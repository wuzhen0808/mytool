package mytool.collector.xueqiu.v5

import groovy.json.JsonSlurper
import mytool.collector.HttpDataCollector
import mytool.collector.RtException

/**
 * https://github.com/uname-yang/pysnowball/blob/master/pysnowball/api_ref.py
 *
 */
class XQV5DataCollector extends HttpDataCollector {
    public static String balance = "balance"
    public static String cash_flow = "cash_flow"
    public static String income = "income"

    XQV5DataCollector(File dir) {
        super(dir, "stock.xueqiu.com")
    }

    @Override
    protected File getTargetFile(File areaDir, String type, String corpCode) {
        return new File(areaDir, type + corpCode + ".json")
    }

    @Override
    protected String getUrl(String corpCode, String type) {
        //https://stock.xueqiu.com/v5/stock/finance/cn/income.json?symbol=SZ000001&is_detail=true&count=2&type=Q4
        String macketCode = getMarketCode(corpCode)
        return "/v5/stock/finance/cn/${type}.json?symbol=${macketCode}${corpCode}&is_detail=true&count=10&type=Q4"
    }

    @Override
    protected void validateFileBeforeRename(File workFile) {
        JsonSlurper jsonSlurper = new JsonSlurper()
        Object json = jsonSlurper.parse(workFile)
        if (!(json instanceof Map)) {
            throw new RtException("type is not a map with json object::${json}")
        }
        String errorCode = json.get('error_code') as String
        if (errorCode != "0") {
            String errorDesc = json.get("error_description")
            throw new RtException("error_code:${errorCode}, error_desc:${errorDesc}")
        }
    }

    static String getMarketCode(String corpCode) {
        if (corpCode.startsWith("60")) {
            return "SH";
        } else {
            return "SZ";
        }
    }
}
