package javax.vecmath;

import java.io.Serializable;

public class Matrix4f implements Serializable, Cloneable {
	public Matrix4f() {
	}

	@Override
	public Matrix4f clone() {
		try {
			return (Matrix4f) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}
}

