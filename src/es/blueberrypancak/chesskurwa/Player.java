package es.blueberrypancak.chesskurwa;

import java.awt.AWTException;
import java.io.IOException;

public class Player {
	
	private BoardReader boardReader;
	
	public Player() throws AWTException, IOException {
		boardReader = new BoardReader();
		Coordinate.setBoard(boardReader);
		MouseBot.set(new MouseBot());
	}

	public void play() throws InterruptedException, IOException, AWTException {
		String last = boardReader.toString();
		System.out.println("Starting in 1 second...");
		Thread.sleep(1000);
		while(true) {
			if(boardReader.buildFEN() && boardReader.isFENBuilt()) Stockfish.run(boardReader.getFEN());
			if(!boardReader.toString().equals(last) && Stockfish.getBestMove() != "" && boardReader.isFENBuilt()) {
				last = boardReader.toString();
				if(boardReader.isMyTurn() && boardReader.fenMatchesSide()) { 
					MouseBot.get().Move(Stockfish.getBestMove());
				}
			}
			Thread.sleep(Config.threadTime);
		}
	}
}
