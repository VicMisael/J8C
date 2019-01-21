/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j8c.Core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Misael
 */
public class RomLoader {

//    public static void openFile(File file) {
//        try {
//            (Files.readAllBytes(file.toPath()));
//        } catch (IOException ex) {
//            Logger.getLogger(RomLoader.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
	private static RomLoader rl;

	public static RomLoader getInstance() {
		if (rl == null) {
			rl = new RomLoader();
		}
		return rl;
	}

	byte[] ROM;

	private RomLoader() {
	}

	public byte[] getRom() {
		if (ROM != null) {
			return ROM;
		}
		System.out.println("No Rom Loaded");
		return null;
	}

	private String romName;

	public String getRomName() {

		return romName;
	}

	public void setRom(File file) {
		try {

			System.out.println(file.toPath().getFileName());
			romName = file.toPath().getFileName().toString();
			ROM = Files.readAllBytes(file.toPath());
		} catch (IOException ex) {
			Logger.getLogger(RomLoader.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void resetRom() {
		ROM = null;
	}
}
