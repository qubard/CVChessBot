package es.blueberrypancak.chesskurwa;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Scanner;

public class Stockfish {
	
	private static String bestMove, lastFEN;
	private static char currentSide;
	
	public static void run(String FEN) throws IOException{ 
		bestMove = "";
		FEN = lastFEN = "position fen " + FEN + "\ngo movetime " + Config.moveTime + "\n";
		Process exe = Runtime.getRuntime().exec(Config.stockfish);
		OutputStream stdin = exe.getOutputStream();
		stdin.write(FEN.getBytes());
		stdin.flush();
		new Thread(new Runnable() {
			public void run() {
				InputStreamReader reader = new InputStreamReader(exe.getInputStream());
				Scanner scan = new Scanner(reader);
				while (scan.hasNextLine()) {
					String s = scan.nextLine();
					System.out.println(s);
					if(s.contains("bestmove")) { 
						currentSide = lastFEN.split(" ")[3].charAt(0);
						bestMove = s.split(" ")[1]; 
					}
				}
				while(exe.isAlive()) { try { scan.close(); exe.getInputStream().close(); } catch (IOException e) { e.printStackTrace(); } exe.destroy(); }
			}
		}).start();
	}
	
	public static String getBestMove() {
		return bestMove;
	}
	
	public static char getCurrentSide() {
		return currentSide;
	}
}
