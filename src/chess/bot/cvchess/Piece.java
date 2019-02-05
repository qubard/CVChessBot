package chess.bot.cvchess;

public class Piece {
	
	private int n;
	private char c;
	private boolean w;
	
	public Piece(char c, int n) {
		this.c = c;
		this.n = n;
		this.w = Character.isLowerCase(c);
	}
	
	public char getName() {
		return c;
	}
	
	public int getValue() {
		return n;
	}
	
	public boolean isWhite() {
		return this.w;
	}
	
	public static Piece getType(int n) {
		for(int i = 0; i < Config.pieces.length; i++) {
			if(Config.pieces[i].getValue() == n) 
				return Config.pieces[i];
		}
		return null;
	}
	
	public String toString() {
		return this.c+"";
	}
}
