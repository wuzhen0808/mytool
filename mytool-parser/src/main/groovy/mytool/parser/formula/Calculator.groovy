package mytool.parser.formula

interface Calculator<T> {
    T add(Object[] model, int idx, T left, T right)

    T minus(Object[] model, int idx, T left, T right)

    T times(Object[] model, int idx, T left, T right)

    T divide(Object[] model, int idx, T left, T right)

    T number(Object[] objects, int idx, Number value)
}
