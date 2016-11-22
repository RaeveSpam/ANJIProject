package com.anji.warlight.conquest.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import com.anji.warlight.conquest.engine.RunGame.Config;
import com.anji.warlight.conquest.engine.RunGame.GameResult;

public class FileBotLog {

	private File file;
	
	public FileBotLog(File file) {
		this.file = file;		
	}
	
	public void start() {
		if (file.exists()) file.delete();
	}
	
	public void finish() {		
	}
	
	public synchronized void log(String msg) {
		PrintWriter writer;
		try {
			if (file.exists()) {
				writer = new PrintWriter(new FileOutputStream(file, true));
			} else {
				writer = new PrintWriter(new FileOutputStream(file));
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to log the game into: " + file.getAbsolutePath(), e);
		}
		
		writer.println(msg);
		
		writer.close();
	}

}
