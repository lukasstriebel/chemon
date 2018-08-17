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

	public Position(int[] board) {
		this.board = board;
	}

	/**
	 * erzeugt eine tiefe Kopie von sich selbst
	 */
	public Position clone() {
		int[] a = new int[120];
		for (int i = 0; i < 120; i++)
			a[i] = board[i];
		Position s = new Position(a);
		s.lastMove = this.lastMove;
		s.klros = this.klros;
		s.klrow = this.klrow;
		s.grros = this.grros;
		s.grrow = this.grrow;
		return s;
	}

	public int whiteKing() {
		for (int i = 21; i < 99; i++) {
			if (board[i] == 15)
				return i;
		}
		return -1;
	}

	public int blackKing() {
		for (int i = 21; i < 99; i++) {
			if (board[i] == 25)
				return i;
		}
		return -1;
	}
}