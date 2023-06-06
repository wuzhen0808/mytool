package mytool.util.jdbc;

public class ArrayParameterProvider extends ParameterProvider {

	private Object[] values;

	public ArrayParameterProvider(Object[] pA) {
		this.values = pA;
	}

	@Override
	public int size() {
		return this.values.length;
	}

	@Override
	public Object get(int idx) {
		return this.values[idx];
	}

}