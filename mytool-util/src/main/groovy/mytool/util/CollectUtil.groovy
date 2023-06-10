package mytool.util

import groovy.transform.CompileStatic

@CompileStatic
class CollectUtil {

    static <S, T> T[] collect(S[] s, Closure<T> closure) {
        List<T> rt = []
        for (int i = 0; i < s.length; i++) {
            rt.add(closure.call(i, s[i] as T))
        }
        return rt as T[]
    }

    static <S, T> T[] collect(S[] s1, S[] s2, Closure<T> closure) {
        if (s1.length != s2.length) {
            throw new IllegalArgumentException("length not match")
        }
        List<T> rt = []
        for (int i = 0; i < s1.length; i++) {
            rt.add(closure.call(i, s1[i], s2[i]) as T)
        }
        return rt as T[]
    }

}
