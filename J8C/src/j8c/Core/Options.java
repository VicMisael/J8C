package j8c.Core;

public class Options {
	private boolean XWrapping = true;
	private boolean YWrapping = false;
	private boolean useOpenGL = false;

	private Options() {
	}

	public static Options options;

	public static Options getInstance() {
		if (options == null)
			options = new Options();
		return options;
	}

	public boolean isXWrappingEnabled() {
		return XWrapping;
	}

	public void setXWrapping(boolean xwrap) {
		XWrapping = xwrap;
	}

	public boolean isYWrappingEnabled() {
		return YWrapping;
	}

	public void setYWrapping(boolean ywrap) {
		YWrapping = ywrap;
	}

	public void setUseOpenGL(boolean use) {
		useOpenGL = use;

	}

	public boolean useOpenGl() {
		return useOpenGL;
	}
}
