package MA.util;

public class Move {
	// Bezugsobjekte

	// Attribute
	public int piece, from, to, addition = 0, newPiece = 0;

	// Konstruktor
	public Move(int piece, int from, int to) {
		this.piece = piece;
		this.from = from;
		this.to = to;
	}

	public Move(int piece, int from, int to, int addition) {
		this(piece, from, to);
		this.addition = addition;
	}

	public boolean equals(Move m) {
		return piece == m.piece && from == m.from && m.to == to;
	}

	public String pieceToLetter(int piece) {
		if (piece == 10 || piece == 20)
			return "";
		if (piece == 11 || piece == 21)
			return "R";
		if (piece == 12 || piece == 22)
			return "N";
		if (piece == 13 || piece == 23)
			return "B";
		if (piece == 14 || piece == 24)
			return "Q";
		else
			return "K";
	}

	public String boardNotation(int pFeld) {
		String lLinie, lReihe;
		lLinie = lReihe = "";
		if (pFeld % 10 == 1)
			lLinie = "a";
		if (pFeld % 10 == 2)
			lLinie = "b";
		if (pFeld % 10 == 3)
			lLinie = "c";
		if (pFeld % 10 == 4)
			lLinie = "d";
		if (pFeld % 10 == 5)
			lLinie = "e";
		if (pFeld % 10 == 6)
			lLinie = "f";
		if (pFeld % 10 == 7)
			lLinie = "g";
		if (pFeld % 10 == 8)
			lLinie = "h";
		if (90 < pFeld && pFeld < 100)
			lReihe = "8";
		if (80 < pFeld && pFeld < 90)
			lReihe = "7";
		if (70 < pFeld && pFeld < 80)
			lReihe = "6";
		if (60 < pFeld && pFeld < 70)
			lReihe = "5";
		if (50 < pFeld && pFeld < 60)
			lReihe = "4";
		if (40 < pFeld && pFeld < 50)
			lReihe = "3";
		if (30 < pFeld && pFeld < 40)
			lReihe = "2";
		if (20 < pFeld && pFeld < 30)
			lReihe = "1";
		return lLinie + lReihe;
	}

	public String toString(boolean b) {
		String s = "";
		if (b)
			s = pieceToLetter(piece) + boardNotation(from) + "x" + boardNotation(to);
		else
			s = pieceToLetter(piece) + boardNotation(from) + "-" + boardNotation(to);
		if (addition == 1 || addition == -1)
			s = " e.p.";
		if (addition == 2 || addition == -2)
			s += " = " + pieceToLetter(newPiece);
		if (addition == 3 || addition == -3)
			s = "0-0";
		if (addition == 4 || addition == -4)
			s = "0-0-0";
		return s;
	}
}
