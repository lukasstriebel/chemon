package MA.util;

/**
 * Die Klasse Position
 * 
 * @author Lukas
 *
 */
public class Position {

	public int[] board;
	public Move lastMove;
	public boolean grrow, klrow, grros, klros, mateW, mateB, checkB, checkW, d1threat, f1threat, d8threat, f8threat;
	public int blackKing, whiteKing;

	public Position(int[] board) {
		this.board = board;
	}

	/**
	 * erzeugt eine tiefe Kopie von sich selbst
	 */
	public Position clone() {
		int[] boardClone = new int[120];
		for (int i = 0; i < 120; i++)
			boardClone[i] = board[i];
		Position positionClone = new Position(boardClone);
		positionClone.lastMove = this.lastMove;
		positionClone.klros = this.klros;
		positionClone.klrow = this.klrow;
		positionClone.grros = this.grros;
		positionClone.grrow = this.grrow;
		positionClone.setBlackKing(blackKing);
		positionClone.setWhiteKing(whiteKing);
		return positionClone;
	}

	public int getWhiteKing() {
		return whiteKing;
	}
	
	public void setWhiteKing(int field) {
		this.whiteKing = field;
	}

	public int getBlackKing() {
		return blackKing;
	}

	public void setBlackKing(int field) {
		this.blackKing = field;
	}
}