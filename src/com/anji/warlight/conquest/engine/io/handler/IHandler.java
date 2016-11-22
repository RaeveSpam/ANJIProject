package com.anji.warlight.conquest.engine.io.handler;

import com.anji.warlight.conquest.engine.replay.GameLog;

public interface IHandler {

	public void setGameLog(GameLog log, String playerName);
	
	public boolean isRunning();
	public void stop();
	
	public String readLine(long timeOut);
	public boolean writeLine(String line);
	
}
