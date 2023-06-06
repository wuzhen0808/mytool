package mytool.util.jdbc;

public abstract class ParameterProvider {
	public abstract int size();

	public abstract Object get(int idx);

	public Object[] getAsArray() {
		int size = this.size();
		Object[] rt = new Object[size];
		for (int i = 0; i < size; i++) {
			rt[i] = get(i);
		}
		return rt;
	}
}