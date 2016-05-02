package es.blueberrypancak.chesskurwa;
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Stack;

public class BoardReader {
	
	/*

	  .oooooo.   oooo                                    oooo    oooo                                                 
	 d8P'  `Y8b  `888                                    `888   .8P'                                                  
	888           888 .oo.    .ooooo.   .oooo.o  .oooo.o  888  d8'    oooo  oooo  oooo d8b oooo oooo    ooo  .oooo.   
	888           888P"Y88b  d88' `88b d88(  "8 d88(  "8  88888[      `888  `888  `888""8P  `88. `88.  .8'  `P  )88b  
	888           888   888  888ooo888 `"Y88b.  `"Y88b.   888`88b.     888   888   888       `88..]88..8'    .oP"888  
	`88b    ooo   888   888  888    .o o.  )88b o.  )88b  888  `88b.   888   888   888        `888'`888'    d8(  888  
	 `Y8bood8P'  o888o o888o `Y8bod8P' 8""888P' 8""888P' o888o  o888o  `V88V"V8P' d888b        `8'  `8'     `Y888""8o                                           
	
	 										- made by cub @ www.blueberrypancak.es -
	*/
	
	private Robot robot;
	
	private int boardX, boardY, nBlackMoves;
	
	private boolean wQCastle, wKCastle, bQCastle, bKCastle;
	
	private static BufferedImage img;

	private Stack<String> fenStack;
	
	private String FEN, enPassant;
	
	private boolean myTurn;
	
	public BoardReader() throws IOException, AWTException {
		robot = new Robot();
		fenStack = new Stack<String>();
		FEN = "";
		enPassant = "-";
		nBlackMoves = 1;
		boardX = boardY = -1;
		wQCastle = wKCastle = bQCastle = bKCastle = true;
		findBoard();
		myTurn = isWhite();
		buildFEN();
	}
	
	public int getX() {
		return this.boardX;
	}
	
	public int getY() {
		return this.boardY;
	}
	
	public String getFEN() {
		return this.FEN;
	}
	
	public boolean isFENBuilt() {
		return this.FEN.split(" ").length > 1;
	}
	
	public boolean isMyTurn() {
		for(int y = boardY+561; y <= boardY+561+13; y++) {
			for(int x = boardX+508; x <= boardX+508+44; x++) {
				if(match(x,y,Config.RED)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private Dimension refresh() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        img = robot.createScreenCapture(new Rectangle(0, 0, screenSize.width, screenSize.height));
        return screenSize;
	}
	
	private void findBoard() {
		Dimension d = refresh();
        for(int x = 0; x < d.width; x++) {
        	for(int y = 0; y < d.height; y++) {
        		if(match(x,y,Config.LIGHT_GRAY) && match(x+1,y,Config.LIGHT_GRAY) 
        				&& match(x,y+1,Config.LIGHT_GRAY) && match(x+1,y+1,Config.DARK_GRAY)) {
        			boardX = x+2;
        			boardY = y+2;
        			return;
        		}
        	}
        }
	}
	
	private boolean tileEmpty(int x, int y) {
		for(int a = 0; a <= Config.TILE_WIDTH; a++) {
			for(int b = 0; b <= Config.TILE_WIDTH; b++) {
				if(match(x+b,y+a,Config.BLACK) || match(x+b,y+a,Config.WHITE)) {
					return false;
				}
			}
		}
		return true;
	}
	
	private int getTile(int x, int y) {
		int n = 0;
		for(int a = 0; a <= Config.TILE_WIDTH; a++) {
			for(int b = 0; b <= Config.TILE_WIDTH; b++) {
				if(match(x+a,y+b,Config.BLACK)) {
					n += 1;
				}
			}
		}
		return n;
	}
	
	public boolean buildFEN() throws IOException {
		refresh();
		String tempFEN = FEN;
		FEN = "";	
		boolean b = false;
		int k = isWhite()? 1 : -1;
		int d = isWhite() ? 0 : (Config.WIDTH-Config.TILE_WIDTH);
		for(int y = boardY + d; isWhite() ? y < boardY+Config.WIDTH : y >= boardY; y+=k*Config.TILE_WIDTH) {
			for(int x = boardX + d; isWhite() ? x < boardX+Config.WIDTH : x >= boardX; x+=k*Config.TILE_WIDTH) {
				if(tileEmpty(x,y)) {
					FEN += '1';
					continue;
				}
				Piece p = Piece.getType(getTile(x,y));
				if(p == null) { 
					FEN = tempFEN;
					return false;
				}
				FEN += p.getName();
			}
			FEN += '/';
		}
		
		FEN = FEN.substring(0, FEN.length()-1);

		if(fenStack.size() >= 1) {
			if(!fenStack.get(fenStack.size()-1).equals(FEN)) {
				fenStack.push(FEN);
			}
		}
		else if(fenStack.size() == 0) { 
			System.out.println("CLEARED");
			fenStack.push(FEN); 
			Stockfish.run("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
		}
		
		if(hasMoved()) {
			b = true;
			if(!myTurn) nBlackMoves += 1;
			myTurn = !myTurn;
			checkEnPassant(); 
		}
		
		if(wQCastle) wQCastle = FEN.split("/")[7].indexOf('K') == 4 && FEN.split("/")[7].indexOf('R') == 0;
		if(wKCastle) wKCastle = FEN.split("/")[7].indexOf('K') == 4 && FEN.split("/")[7].charAt(7) == 'R';
		if(bQCastle) bQCastle = FEN.split("/")[0].indexOf('k') == 4 && FEN.split("/")[0].indexOf('r') == 0;
		if(bKCastle) bKCastle = FEN.split("/")[0].indexOf('k') == 4 && FEN.split("/")[0].charAt(7) == 'r';
		
		for(int i = 8; i >= 2; i--) {
			String s ="";
			for(int x = 0; x < i; x++) s += "1";
			FEN = FEN.replaceAll(s, i+"");
		}
		
		FEN += ' ';
		FEN += getTurn();
		FEN += ' ';
		
		if(wKCastle) FEN += "K";
		if(wQCastle) FEN += "Q";
		if(bKCastle) FEN += "k";
		if(bQCastle) FEN += "q";
		if(!(bQCastle||bKCastle||wKCastle||wQCastle)) FEN += '-';
		
		FEN += ' ';
		FEN += enPassant;
		FEN += " 0 ";
		FEN += nBlackMoves;
		
		return b;
	}
	
	private boolean hasMoved() {
		return fenStack.size() > 1 ? !fenStack.get(fenStack.size()-1).equals(fenStack.get(fenStack.size()-2)) : false;
	}
	
	private void checkEnPassant() {
		if(fenStack.size() > 1) {
			String r = "";
			String currentFEN = fenStack.pop();
			String lastFEN = fenStack.pop();
			String mB = currentFEN.split("/")[3];
			String pB = currentFEN.split("/")[1];
			String oB = lastFEN.split("/")[1];
			String mW = currentFEN.split("/")[4];
			String pW = currentFEN.split("/")[6];
			String oW = lastFEN.split("/")[6];
			for(int i = 0; i < 8; i++){
				if(pW.charAt(i) == '1' && mW.charAt(i) == 'P' && oW.charAt(i) == 'P')  
					r = (char)(97+i)+"3";
				if(pB.charAt(i) == '1' && mB.charAt(i) == 'p' && oB.charAt(i) == 'p') 
					r = (char)(97+i)+"6";
			}
			fenStack.push(currentFEN);
			if(r != "") {
				enPassant = r;
			} else {
				enPassant = "-";
			}
		}
	}

	private char getTurn() {
		return isWhite() ? isMyTurn() ? 'w' : 'b' : isMyTurn() ? 'b' : 'w'; 
	}

	public boolean isWhite() {
		return match(boardX+7,boardY+487,Config.S1) && match(boardX+8,boardY+487,Config.S2) && match(boardX+9,boardY+487,Config.S3);
	}
	
	private static boolean match(int x, int y, Color c) {
		return img.getRGB(x, y) == c.getRGB();
	}
	
	public String toString() {
		return isWhite() + ":bestmove:" + Stockfish.getBestMove() + ":x:" + boardX + ",y:" + boardY + ",FEN:"+FEN;
	}
}
