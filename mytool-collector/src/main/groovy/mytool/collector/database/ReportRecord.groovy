package mytool.collector.database

import groovy.transform.CompileStatic

@CompileStatic
class ReportRecord {
    String corpId
    Date date
    String key
    BigDecimal value

    static Map<String, Map<String, Map<Date, ReportRecord>>> groupByCorpIdAndKeyAndDate(List<ReportRecord> records) {
        Map<String, Map<String, Map<Date, ReportRecord>>> map = [:]
        records.each {
            Map<String, Map<Date, ReportRecord>> map2 = map.get(it.corpId)
            if (map2 == null) {
                map2 = [:]
                map.put(it.corpId, map2)
            }

            Map<Date, ReportRecord> map3 = map2.get(it.key)
            if (map3 == null) {
                map3 = [:]
                map2.put(it.key, map3)
            }
            map3.put(it.date, it)
        }
        return map
    }

    static Date[] collectDates(List<ReportRecord> records) {
        return (records.collect { it.date } as Set<Date> as Date[]).sort()
    }

    static Map<String, Map<String, BigDecimal[]>> groupValueByCorpIdAndKey(List<ReportRecord> records) {
        Date[] dates = collectDates(records)
        Map<String, Map<String, Map<Date, ReportRecord>>> map = groupByCorpIdAndKeyAndDate(records)
        return groupValueByCorpIdAndKey(map, dates)
    }

    static Map<String, Map<String, BigDecimal[]>> groupValueByCorpIdAndKey(List<ReportRecord> records, Date[] dates) {
        Map<String, Map<String, Map<Date, ReportRecord>>> map = groupByCorpIdAndKeyAndDate(records)
        return groupValueByCorpIdAndKey(map, dates)
    }

    static Map<String, Map<String, BigDecimal[]>> groupValueByCorpIdAndKey(Map<String, Map<String, Map<Date, ReportRecord>>> map, Date[] dates) {
        Map<String, Map<String, BigDecimal[]>> rtMap = [:]

        map.each {
            Map<String, BigDecimal[]> map2 = [:]
            it.value.each { Map.Entry<String, Map<Date, ReportRecord>> it2 ->
                Map<Date, ReportRecord> map3 = it2.value
                BigDecimal[] values = dates.collect({ Date date ->
                    map3.get(date)?.value
                }) as BigDecimal[]

                map2.put(it2.key, values)
            }
            rtMap.put(it.key, map2)
        }
        return rtMap
    }

}
