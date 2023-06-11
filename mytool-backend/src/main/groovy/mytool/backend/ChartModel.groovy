package mytool.backend


import groovy.transform.CompileStatic

@CompileStatic
class ChartModel {
    static class Metrics {
        Set<String> tags
        Set<String> notTags
    }
    String id
    String name
    String type
    String provider
    String metric
    String report
    Metrics metrics
    boolean enabled
    boolean stacked
    boolean fill
    BigDecimal removeLowLines
    boolean percentage
    String style


}
