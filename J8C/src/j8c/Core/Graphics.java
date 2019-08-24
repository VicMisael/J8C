package j8c.Core;


import j8c.MainGUI;

public class Graphics {
	public static void Draw(byte[] screen,int mode) {
		MainGUI.drawToCanvas(screen);


	}
	public static void cleanBl() {
		byte[] screen=new byte[64*32];
		for(int a=0;a<screen.length;a++) {
			screen[a]=0;
		}
		MainGUI.drawToCanvas(screen);
	}


}
