package mytool.util

import groovy.transform.CompileStatic

@CompileStatic
class IoUtil {
    static Reader getResourceAsReader(Class clazz, String res) {
        return getResourceAsReader(clazz.getClassLoader(), res)
    }

    static Reader getResourceAsReader(ClassLoader classLoader, String res) {
        return new InputStreamReader(classLoader.getResourceAsStream(res), "utf-8")
    }
}
