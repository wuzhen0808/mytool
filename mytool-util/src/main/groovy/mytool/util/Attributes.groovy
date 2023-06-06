package mytool.util

public class Attributes {
    private Map<String, Object> map = new HashMap<>();

    public Object getAttribute(String key) {
        return map.get(key);
    }

    public Object setAttribute(String key, Object value) {
        return map.put(key, value);
    }

}
