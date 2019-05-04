package j8c.Core;


import j8c.MainGUI;

public class Graphics {
	public static void Draw(byte[] screen,String info) {
		MainGUI.drawToCanvas(screen);


//		String graphic = "";
//		
//		int i = 0;
//		System.out.println(info);
//		while (i < 64) {
//
//			i++;
//			graphic += "_";
//
//		}
//		graphic += "\n";
//		for (int y = 0; y < 32; y++) {
//			for (int x = 0; x < 64; x++) {
//				byte screenByte = screen[x + y * 64];
//				if (screenByte == 0) {
//
//					graphic += " ";
//				} else {
//					graphic += "*";
//				}
//			}
//			graphic += "\n";
//		}
//		
//		System.out.print(graphic);
//		System.out.flush();
	}
	public static void setExecutedInstructionHex(String hex) {
		
		
	}

}
