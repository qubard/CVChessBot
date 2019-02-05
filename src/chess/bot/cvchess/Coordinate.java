package chess.bot.cvchess;

public class Coordinate {
	
	private static BoardReader board;
	
	private int x, y;
	
	public Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public String toString() {
		return this.x + "," + this.y;
	}
	
	public static void setBoard(BoardReader b) {
		board = b;
	}
	
	public static Coordinate convert(String coordinate) {
		if(coordinate.length() != 2) return new Coordinate(0,0);
		char file = coordinate.charAt(0);
		char rank = coordinate.charAt(1);
		return new Coordinate(convertFileToX(file), convertRankToY(rank));
	}
	
	private static int convertFileToX(char file) {
		int d = (board.isWhite() ? 1 : -1)*((int)(file)-97)*Config.TILE_WIDTH+Config.TILE_WIDTH/2;
		return board.getX() + d + (board.isWhite() ? 0 : Config.WIDTH-Config.TILE_WIDTH);
	}
	
	private static int convertRankToY(char rank) {
		int d = (board.isWhite() ? -1 : 1)*(Integer.parseInt(rank+"")-1)*Config.TILE_WIDTH+Config.TILE_WIDTH/2;
		return board.getY() + d + (board.isWhite() ? Config.WIDTH-Config.TILE_WIDTH : 0);
	}
}
