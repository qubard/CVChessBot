package chess.bot.cvchess;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

public class MouseBot {
	
	private static MouseBot bot;
	
	private Robot robot;
	
	public MouseBot() throws AWTException {
		this.robot = new Robot();
	}

	public void Move(String move) throws InterruptedException {
		if(move.length() != 4) return;
		Coordinate from = Coordinate.convert(move.substring(0,2));
		Coordinate to = Coordinate.convert(move.substring(2,4));
		Thread.sleep(Config.mouseDelay);
		robot.mouseMove(from.getX(),from.getY());
		Thread.sleep(Config.mouseDelay);
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		Thread.sleep(Config.mouseDelay);
		int dX = to.getX()-from.getX();
		int dY = to.getY()-from.getY();
		robot.mouseMove(from.getX()+(int)(0.5*dX), from.getY()+(int)(0.5*dY));
		Thread.sleep(Config.mouseDelay);
		robot.mouseMove(to.getX(), to.getY());
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}
	
	public static void set(MouseBot b) {
		bot = b;
	}
	
	public static MouseBot get() {
		return bot;
	}
}
