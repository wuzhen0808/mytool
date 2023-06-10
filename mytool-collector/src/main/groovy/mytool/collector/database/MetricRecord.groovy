package mytool.collector.database

import groovy.transform.CompileStatic


@CompileStatic
class MetricRecord {
    String corpId
    Date date
    String key
    BigDecimal value

    static List<MetricRecord> asList(String metric, String corpId, Date[] dates, BigDecimal[] values) {
        List<MetricRecord> rtList = []
        for (int i = 0; i < dates.length; i++) {
            rtList.add(new MetricRecord(key: metric, corpId: corpId, date: dates[i], value: values[i]))
        }
        return rtList
    }

    static Map<String, Map<String, Map<Date, MetricRecord>>> groupByCorpIdAndKeyAndDate(List<MetricRecord> records) {
        Map<String, Map<String, Map<Date, MetricRecord>>> map = [:]
        records.each {
            Map<String, Map<Date, MetricRecord>> map2 = map.get(it.corpId)
            if (map2 == null) {
                map2 = [:]
                map.put(it.corpId, map2)
            }

            Map<Date, MetricRecord> map3 = map2.get(it.key)
            if (map3 == null) {
                map3 = [:]
                map2.put(it.key, map3)
            }
            map3.put(it.date, it)
        }
        return map
    }
    static Date[] collectDates(MetricRecord[] records) {
        return collectDates(records as List<MetricRecord>)
    }

    static Date[] collectDates(List<MetricRecord> records) {
        return (records.collect { it.date } as Set<Date> as Date[]).sort()
    }

    static Map<String, Map<String, BigDecimal[]>> groupValueByCorpIdAndKey(List<MetricRecord> records) {
        Date[] dates = collectDates(records)
        Map<String, Map<String, Map<Date, MetricRecord>>> map = groupByCorpIdAndKeyAndDate(records)
        return groupValueByCorpIdAndKey(map, dates)
    }

    static Map<String, Map<String, BigDecimal[]>> groupValueByCorpIdAndKey(MetricRecord[] records, Date[] dates) {
        return groupValueByCorpIdAndKey(records as List<MetricRecord>, dates)
    }

    static Map<String, Map<String, BigDecimal[]>> groupValueByCorpIdAndKey(List<MetricRecord> records, Date[] dates) {
        Map<String, Map<String, Map<Date, MetricRecord>>> map = groupByCorpIdAndKeyAndDate(records)
        return groupValueByCorpIdAndKey(map, dates)
    }

    static Map<String, Map<String, BigDecimal[]>> groupValueByCorpIdAndKey(Map<String, Map<String, Map<Date, MetricRecord>>> map, Date[] dates) {
        Map<String, Map<String, BigDecimal[]>> rtMap = [:]

        map.each {
            Map<String, BigDecimal[]> map2 = [:]
            it.value.each { Map.Entry<String, Map<Date, MetricRecord>> it2 ->
                Map<Date, MetricRecord> map3 = it2.value
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
