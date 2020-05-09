package gameai;

public class KeyInput {
	public boolean u, d, l, r;

	public KeyInput(boolean u, boolean d, boolean l, boolean r) {
		this.u = u;
		this.d = d;
		this.l = l;
		this.r = r;
	}

	public KeyInput() {
		u = false;
		d = false;
		l = false;
		r = false;
	}

}
