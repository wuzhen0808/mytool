package mytool.parser.formula

import groovy.transform.CompileStatic

@CompileStatic
interface MetricResolver<T> {
    T[] resolve(Object model, Object[] models, String tx, String metric)
}