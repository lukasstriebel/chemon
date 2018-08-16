package MA.Chemon;

import java.util.*;

import javax.swing.JOptionPane;

import MA.Openings.English;
import MA.Openings.Kings_Indian;
import MA.Openings.Opening;
import MA.Openings.Slav;
import MA.Openings.Spanish;
import MA.util.Move;
import MA.util.Movetree;
import MA.util.Position;
import MA.util.SearchAlgorithm;
import MA.util.State;

public class Engine {

	Move lastMove, bestMove;
	boolean inOpening, mateW, mateB, checkB, checkW;
	int numberOfMoves = 0, depth;
	Movetree startTree, testTree = new Movetree(0, 0, 0);
	Position actualPosition;
	Opening Opening;
	Controller Controller;
	int anzbewert;

	/**
	 * Die Engine berechnet die Züge und Positionen.
	 * 
	 * @param :
	 *            p die Anfangsposition
	 */
	public Engine(Position p) {
		lastMove = new Move(10, 0, 0);
		actualPosition = p.clone();
		actualPosition.lastMove = lastMove;
		startTree = new Movetree(lastMove);
		startTree.position = actualPosition;
	}

	public void work() {

		if (Controller.whitesTurn) {

			calculateWhiteMove();
			Movetree m = new Movetree(bestMove);
			m.position = actualPosition;
			actualPosition = executeMove(m);
			if (controlCheckW(actualPosition))
				checkW = true;
			else
				checkW = false;
			startTree.position = actualPosition;
			lastMove = bestMove;
		} else {
			if (inOpening)
				playOpening();
			else if (numberOfMoves < 2)
				checkOpening();
			else if (numberOfMoves > 1)
				calculateBlackMove();
			Movetree m = new Movetree(bestMove);
			m.position = actualPosition;
			actualPosition = executeMove(m);
			if (controlCheckB(actualPosition))
				checkB = true;
			else
				checkB = false;
			startTree.position = actualPosition;
			lastMove = bestMove;
			numberOfMoves++;
		}
	}

	/*
	 * public double alphabeta(Movetree m, int pDepth, double alpha, double beta) {
	 * if(pDepth == 0) return evaluateMovetree(m); if(pDepth % 2== depth %2)
	 * m.Children=movesW(m.Position, m); else m.Children=movesB(m.Position, m);
	 * for(Movetree child: m.Children){ child.Position = executeMove(child);
	 * if(pDepth % 2== depth %2){ alpha = Math.max(alpha,
	 * alphabeta(child,pDepth-1,alpha,beta)); if(alpha >= beta) return alpha;} else
	 * { beta = Math.min(beta, alphabeta(child,pDepth-1,alpha,beta)); if(alpha >=
	 * beta) return beta; }} if(pDepth % 2== depth %2) return alpha; else return
	 * beta; }
	 */

	/**
	 * berechnet den besten schwarzen Zug
	 */
	private void calculateBlackMove() {
		int d = 0;
		startTree.children = legalMovesB(actualPosition, startTree);
		if (startTree.children.isEmpty()) {
			Controller.running = false;
			if (controlCheckW(actualPosition)) {
				mateW = true;
				return;
			} else {
				Controller.handleDraw();//
				JOptionPane.showMessageDialog(null, "Stalemate!");
			}
		}
		for (Movetree m : startTree.children) {
			m.position = executeMove(m);
			if (Controller.searchWith == SearchAlgorithm.MinMax)
				d = min(m, 2);
			else if (Controller.searchWith == SearchAlgorithm.AlphaBeta)
				d = beta(m, 3, -2000, 2000);
			else if (Controller.searchWith == SearchAlgorithm.PrincipalVariation) {
				depth = 3;
				d = PrincipalVariation(m, 2, -2000, 2000);
			} else if (Controller.searchWith == SearchAlgorithm.Zugvorsortierung) {
				depth = 3;
				d = PrincipalVariationSorted(m, 2, -2000, 2000);
			}
			m.value = d;
		}
		Collections.sort(startTree.children);
		Collections.reverse(startTree.children);
		int value1 = startTree.children.get(0).value, value2;
		double difference;

		for (int i = 1; i < startTree.children.size(); i++) {
			value2 = startTree.children.get(i).value;
			difference = value1 - value2;
			if (difference > 2) {
				bestMove = startTree.children.get(randomnumber(0, i - 1)).move;
				break;
			}
			if (i == startTree.children.size() - 1) {
				bestMove = startTree.children.get(randomnumber(0, i)).move;
				break;
			}
		}

		System.out.println(anzbewert);
		anzbewert = 0;
	}

	/**
	 * berechnet den besten weissen Zug
	 */
	private void calculateWhiteMove() {
		if (inOpening)
			playOpening();
		else if (numberOfMoves == 0) {
			double random = Math.random();
			if (random < 0.4)
				bestMove = new Move(10, 35, 55);
			else if (random < 0.7)
				bestMove = new Move(10, 34, 54);
			else if (random < 0.9)
				bestMove = new Move(10, 33, 53);
			else
				bestMove = new Move(12, 27, 46);
		} else {
			startTree.children = legalMovesW(actualPosition, startTree);
			if (startTree.children.isEmpty()) {
				Controller.running = false;
				if (controlCheckB(actualPosition)) {
					mateB = true;
					return;
				} else {
					Controller.handleDraw();
					JOptionPane.showMessageDialog(null, "Patt!");
				}
			}
			for (Movetree m : startTree.children) {
				m.position = executeMove(m);
				int d = max(m, 2);
				m.value = d;
			}
			Collections.sort(startTree.children);
			int value1 = startTree.children.get(0).value, value2;
			double difference;
			if (!startTree.children.isEmpty())
				for (int i = 1; i < startTree.children.size(); i++) {
					value2 = startTree.children.get(i).value;
					difference = value1 - value2;
					if (difference < -2.1) {
						bestMove = startTree.children.get(randomnumber(0, i - 1)).move;
						break;
					}
					startTree.value = startTree.children.get(0).value;
				}
			else {
				Controller.running = false;
				Controller.handleDraw();
			}
		}
		System.out.println(anzbewert);
		anzbewert = 0;
	}

	private int PrincipalVariation(Movetree m, int pDepth, int alpha, int beta) {
		boolean PVfound = false;
		int value = 0;
		if (pDepth == 0)
			return evaluateMovetree(m);
		if (pDepth % 2 == depth % 2)
			m.children = legalMovesW(m.position, m);
		else
			m.children = legalMovesB(m.position, m);
		if (!m.children.isEmpty()) {
			for (Movetree child : m.children) {
				child.position = executeMove(child);
				if (PVfound) {
					value = PrincipalVariation(child, pDepth - 1, -alpha - 1, -alpha);
					if (value > alpha && value < beta)
						value = PrincipalVariation(child, pDepth - 1, -beta, -alpha);
				} else
					value = PrincipalVariation(child, pDepth - 1, -beta, -alpha);
				if (pDepth % 2 == depth % 2) {
					if (value <= beta) {
						m.children.clear();
						return beta;
					}
					if (value < alpha) {
						alpha = value;
						PVfound = true;
					}
				} else {
					if (value >= beta) {
						m.children.clear();
						return beta;
					}
					if (value > alpha) {
						alpha = value;
						PVfound = true;
					}
				}
			}
			m.children.clear();
			return alpha;
		} else { // keine Züge möglich
			if (pDepth % 2 == depth % 2 && controlCheckB(m.position))
				return 1000; // Weiss am Züge und weisser König im Schach(=Matt)
			else if ((pDepth + 1) % 2 == depth % 2 && controlCheckW(m.position))
				return -1000;// Schwarz am Züge und schwarzer König im Schach(=Matt)
			else // Patt
				return 0;
		}
	}

	private int PrincipalVariationSorted(Movetree m, int pDepth, int alpha, int beta) {
		boolean PVfound = false;
		int value;
		if (pDepth == 0)
			return evaluateMovetree(m);
		if (pDepth % 2 == depth % 2)
			m.children = sortedadmovesW(m.position, m);
		else
			m.children = sortedadmovesB(m.position, m);
		if (!m.children.isEmpty()) {
			for (Movetree child : m.children) {
				child.position = executeMove(child);
				if (PVfound) {
					value = PrincipalVariation(child, pDepth - 1, -alpha - 1, -alpha);
					if (value > alpha && value < beta)
						value = PrincipalVariation(child, pDepth - 1, -beta, -alpha);
				} else
					value = PrincipalVariation(child, pDepth - 1, -beta, -alpha);
				if (pDepth % 2 == depth % 2) {
					if (value <= beta) {
						m.children.clear();
						return beta;
					}
					if (value < alpha) {
						alpha = value;
						PVfound = true;
					}
				} else {
					if (value >= beta) {
						m.children.clear();
						return beta;
					}
					if (value > alpha) {
						alpha = value;
						PVfound = true;
					}
				}
			}
			m.children.clear();
			return alpha;
		} else { // keine Züge möglich
			if (pDepth % 2 == depth % 2 && controlCheckB(m.position))
				return 1000; // Weiss am Züge und weisser König im Schach(=Matt)
			else if ((pDepth + 1) % 2 == depth % 2 && controlCheckW(m.position))
				return -1000;// Schwarz am Züge und schwarzer König im Schach(=Matt)
			else // Patt
				return 0;
		}
	}

	private int alpha(Movetree m, int pDepth, int alpha, int beta) {
		if (pDepth == 0)
			return evaluateMovetree(m);
		m.children = legalMovesB(m.position, m);
		if (!m.children.isEmpty()) {
			for (Movetree child : m.children) {
				child.position = executeMove(child);
				int value = beta(child, pDepth - 1, alpha, beta);
				if (value >= beta) {
					m.children.clear();
					return beta;
				}
				if (value > alpha)
					alpha = value;
			}
			m.children.clear();
			return alpha;
		} else if (controlCheckB(m.position))
			return -100000;
		else
			return 0;

	}

	private int beta(Movetree m, int pDepth, int alpha, int beta) {
		if (pDepth == 0)
			return evaluateMovetree(m);
		m.children = legalMovesW(m.position, m);
		if (!m.children.isEmpty()) {
			for (Movetree child : m.children) {
				child.position = executeMove(child);
				int value = alpha(child, pDepth - 1, alpha, beta);
				if (value <= alpha) {
					m.children.clear();
					return alpha;
				}
				if (value < beta)
					beta = value;
			}
			m.children.clear();
			return beta;
		} else if (controlCheckW(m.position))
			return 100000000;
		else
			return 0;

	}

	private int max(Movetree m, int pDepth) {
		if (pDepth == 0)
			return evaluateMovetree(m);
		int worst = -2000;
		m.children = legalMovesB(m.position, m);
		for (Movetree child : m.children) {
			child.position = executeMove(child);
			int value = min(child, pDepth - 1);
			if (value > worst)
				worst = value;
		}
		m.children.clear();
		return worst;

	}

	private int min(Movetree m, int pDepth) {
		if (pDepth == 0)
			return evaluateMovetree(m);
		int worst = 2000;
		m.children = legalMovesW(m.position, m);
		for (Movetree child : m.children) {
			child.position = executeMove(child);
			int value = max(child, pDepth - 1);
			if (value < worst)
				worst = value;
		}
		m.children.clear();
		return worst;

	}

	private void playOpening() {
		Move Book;
		boolean notFound = true;
		if (Opening.helpTree.children != null)
			for (Movetree m : Opening.helpTree.children) {
				Book = m.move;
				if (Book.piece == lastMove.piece && Book.from == lastMove.from && Book.to == lastMove.to
						&& m.children != null) {
					notFound = false;
					Opening.helpTree = m;
					int random = randomnumber(0, m.children.size() - 1);
					bestMove = m.children.get(random).move;
					lastMove = bestMove;
					if (Controller.status != State.Machine_versus_Machine)
						Opening.helpTree = m.children.get(random);
					Controller.sleep(200);
				}
			}
		if (notFound) {
			inOpening = false;
			if (!Controller.whitesTurn)
				calculateBlackMove();
			else
				calculateWhiteMove();
		}

	}

	private void checkOpening() {
		double Zufall = Math.random();
		if (numberOfMoves == 1) {
			if (lastMove.piece == 10) {
				if (lastMove.from == 33 && lastMove.to == 53) {
					inOpening = true;
					Opening = new English();
					playOpening();
				} else if (lastMove.from == 35 && lastMove.to == 55) {
					inOpening = true;
					// if (Zufall < 0.5)
					// Opening = new Sicilian();
					// else
					Opening = new Spanish();
					playOpening();
				} else if (lastMove.from == 34 && lastMove.to == 54) {
					inOpening = true;
					if (Zufall < 0.5)
						Opening = new Slav();
					else
						Opening = new Kings_Indian();
					playOpening();
				} else
					calculateBlackMove();
			} else
				calculateBlackMove();
		}
	}

	public Position executeMove(Movetree Tree) {// liefert Position nach einem Zug

		Move pMove = Tree.move;
		int[] Board = Tree.position.Board.clone();
		Position s = Tree.position.clone();
		Board[pMove.to] = pMove.piece;
		Board[pMove.from] = 0;
		if (pMove.piece == 15) {
			s.klrow = false;
			s.grrow = false;
		} else if (pMove.piece == 11 && pMove.from == 21)
			s.grrow = false;
		else if (pMove.piece == 11 && pMove.from == 28)
			s.klrow = false;

		if (pMove.piece == 25) {
			s.klros = false;
			s.grros = false;
		} else if (pMove.piece == 21 && pMove.from == 91)
			s.grros = false;
		else if (pMove.piece == 21 && pMove.from == 98)
			s.klros = false;

		if (pMove.addition > 0) {
			if (pMove.addition == 1) {// en passant
				Board[pMove.to - 10] = 0;
			}
			if (pMove.addition == 2) { // Umwandlung
				Board[pMove.to] = 14;
			}
			if (pMove.addition == 3) { // kl Rochade
				Board[pMove.to - 1] = pMove.piece - 4;
				Board[pMove.to + 1] = 0;
				s.klrow = false;
				s.grrow = false;
			}

			if (pMove.addition == 4) { // gr Rochade
				Board[pMove.to + 1] = pMove.piece - 4;
				Board[pMove.to - 2] = 0;
				s.klrow = false;
				s.grrow = false;
			}
		}

		else {
			if (pMove.addition == -1) {// en passant
				Board[pMove.to + 10] = 0;
			}

			if (pMove.addition == -2) { // Umwandlung
				Board[pMove.to] = 24;
			}
		}

		if (pMove.addition == -3) { // kl Rochade
			Board[pMove.to - 1] = pMove.piece - 4;
			Board[pMove.to + 1] = 0;
			s.klros = false;
			s.grros = false;
		}

		if (pMove.addition == -4) { // gr Rochade
			Board[pMove.to + 1] = pMove.piece - 4;
			Board[pMove.to - 2] = 0;
			s.klros = false;
			s.grros = false;
		}
		s.lastMove = pMove;
		s.Board = Board;
		return s;
	}

	public boolean isLegalWhite(Move m) {

		ArrayList<Movetree> list = movesW(actualPosition, new Movetree(m));
		boolean result = false;
		Movetree movetree = null;
		for (Movetree tree : list) {
			if (tree.move.equals(m)) {
				result = true;
				movetree = tree;
				break;
			}
		}

		if (result) {
			movetree.position = actualPosition.clone();
			Position pos = executeMove(movetree);
			int i = pos.whiteKing();
			ArrayList<Movetree> list2 = movesB(pos, movetree);
			for (Movetree tree2 : list2) {
				if (tree2.to == i) {
					result = false;
					break;
				}
			}
		}

		return result;
	}

	public boolean isLegalBlack(Move m) {
		ArrayList<Movetree> l = movesB(actualPosition, new Movetree(m));
		Move compare;
		for (Movetree t : l) {
			compare = t.move;
			if (m.piece == compare.piece && m.from == compare.from && m.to == compare.to) {
				if (compare.addition < 0)
					m.addition = compare.addition;
				return true;
			}
		}

		return false;
	}

	public boolean controlCheckB(Position test) {
		ArrayList<Movetree> list = movesW(test, testTree);
		int posK = 0;
		test.d8threat = false;
		test.f8threat = false;
		test.lastMove = new Move(0, 0, 0);
		for (int i = 21; i < 99; i++) {
			if (test.Board[i] == 25) {
				posK = i;
				break;
			}
		}
		for (Movetree m : list) {
			int compare = m.move.to;
			if (compare == 94)
				test.d8threat = true;
			if (compare == 96)
				test.f8threat = true;
			if (compare == posK)
				return true;
		}
		return false;
	}

	public boolean controlCheckW(Position test) {
		ArrayList<Movetree> list = movesB(test, testTree);
		int posK = 0;
		test.d1threat = false;
		test.f1threat = false;
		test.lastMove = new Move(0, 0, 0);
		for (int i = 21; i < 99; i++) {
			if (test.Board[i] == 15) {
				posK = i;
				break;
			}
		}
		for (Movetree m : list) {
			int compare = m.move.to;
			if (compare == 24)
				test.d1threat = true;
			if (compare == 26)
				test.f1threat = true;
			if (compare == posK)
				return true;
		}
		return false;
	}

	public void handleWhiteMove(Move pMove) {
		lastMove = pMove;
		Movetree m = new Movetree(pMove);
		m.position = actualPosition;
		actualPosition = executeMove(m);
		numberOfMoves++;
		startTree = new Movetree(pMove);
		startTree.position = actualPosition;
		if (controlCheckW(actualPosition))
			checkW = true;
		else
			checkW = false;
	}

	/*
	 * public static void main(String[] a) {
	 * 
	 * int[] i = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	 * -1, -1, -1, -1, -1, 11, 12, 13, 14, 15, 13, 12, 11, -1, -1, 10, 10, 10, 10,
	 * 0, 10, 10, 10, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 10, 0, 0,
	 * 0, -1, -1, 0, 0, 0, 0, 20, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1,
	 * 20, 20, 20, 20, 0, 20, 20, 20, -1, -1, 21, 22, 23, 24, 25, 23, 22, 21, -1,
	 * -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
	 * -1, }; Move z = new Move(10, 54, 65); Movegenerator g = new Movegenerator(i);
	 * g.lastMove = z; Position s = new Position(i); s.lastMove = z; z.st = s;
	 * Position s2 = g.executeMove(z); }
	 */

	/**
	 * liefert eine Bewertung eines Movetrees
	 * 
	 * @param movetree
	 *            : der zu bewertende Movetree
	 * @return double wert, die Bewertung
	 */

	public int evaluateMovetree(Movetree movetree) {
		if (legalMovesW(movetree.position, movetree).size() == 0)
			return 10000000;
		int Insgesamt = evaluatePosition(movetree.position) + evaluateMaterial(movetree.position.Board);
		Insgesamt += movesB(movetree.position, movetree).size();
		anzbewert++;
		return Insgesamt;
	}

	public int evaluatePosition(Position position) {
		int Position = 0;
		if (actualPosition.klros)
			Position += 5;
		if (actualPosition.grros)
			Position += 4;
		if (!actualPosition.klrow)
			Position += 5;
		if (!actualPosition.grrow)
			Position += 4;
		int posKW = 0, posKB = 0;
		for (int i = 21; i < 99; i++) {
			if (position.Board[i] == 25)
				posKB = i;
			else if (position.Board[i] == 15)
				posKW = i;
		}
		if (posKB % 10 == 3 || posKB % 10 == 6)
			Position += 3;
		else if (posKB % 10 == 2 || posKB % 10 == 7)
			Position += 8;
		else if (posKB % 10 == 1 || posKB % 10 == 8)
			Position += 10;
		if (posKB % 10 == 4)
			Position += 10;
		else if (posKB % 10 == 5 || posKB % 10 == 3)
			Position += 8;
		ArrayList<Movetree> list = movesB(position, testTree);
		for (Movetree m : list) {
			int compare = m.move.to;
			if (compare == posKW)
				Position += 15;
			/*
			 * else if (compare == posKW+1 ||compare == posKW-1 ||compare == posKW+10 ||
			 * compare == posKW-10 ||compare == posKW+11 ||compare == posKW-11 || compare ==
			 * posKW+9 ||compare == posKW-9) Position += 10;
			 */
		}
		return Position;
	}

	public int evaluateMaterial(int[] board) {
		int Material = 0;
		for (int i = 21; i < 99; i++) {
			if (board[i] == 10)
				Material -= 10;
			else if (board[i] == 11)
				Material -= 50;
			else if (board[i] == 12)
				Material -= 30;
			else if (board[i] == 13)
				Material -= 30;
			else if (board[i] == 14)
				Material -= 90;
			else if (board[i] == 15)
				Material -= 10000;
			else if (board[i] == 20)
				Material += 10;
			else if (board[i] == 21)
				Material += 50;
			else if (board[i] == 22)
				Material += 30;
			else if (board[i] == 23)
				Material += 30;
			else if (board[i] == 24)
				Material += 90;
			else if (board[i] == 25)
				Material += 10000;
		}
		return Material;
	}

	// Zuggenerator

	/**
	 * 
	 * @param position
	 *            Die Position, deren mögliche Züge berechnet werden soll
	 * @param movetreee
	 *            Movtree, der als Vater dieser Züge angegeben wird
	 * @return ArrayList<Movetree>, enthält alle möglichen schwarzen Züge
	 */
	public ArrayList<Movetree> movesB(Position position, Movetree movetreee) {
		int[] Board = position.Board;
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		for (int i = 21; i < 99; i++) {
			int f = Board[i];
			switch (f) {
			case 20:
				list.addAll(pawnmovesB(Board, i, movetreee));
				break;
			case 21:
				list.addAll(linemovesB(Board, i, 21, 10, movetreee));
				list.addAll(linemovesB(Board, i, 21, -10, movetreee));
				list.addAll(linemovesB(Board, i, 21, 1, movetreee));
				list.addAll(linemovesB(Board, i, 21, -1, movetreee));
				break;
			case 22:
				list.addAll(knightmovesB(Board, i, movetreee));
				break;
			case 23:
				list.addAll(linemovesB(Board, i, 23, 9, movetreee));
				list.addAll(linemovesB(Board, i, 23, -9, movetreee));
				list.addAll(linemovesB(Board, i, 23, 11, movetreee));
				list.addAll(linemovesB(Board, i, 23, -11, movetreee));
				break;
			case 24:
				list.addAll(linemovesB(Board, i, 24, 10, movetreee));
				list.addAll(linemovesB(Board, i, 24, -10, movetreee));
				list.addAll(linemovesB(Board, i, 24, 1, movetreee));
				list.addAll(linemovesB(Board, i, 24, -1, movetreee));
				list.addAll(linemovesB(Board, i, 24, 9, movetreee));
				list.addAll(linemovesB(Board, i, 24, -9, movetreee));
				list.addAll(linemovesB(Board, i, 24, 11, movetreee));
				list.addAll(linemovesB(Board, i, 24, -11, movetreee));
				break;
			case 25:
				list.addAll(kingmovesB(position, i, movetreee));
				break;
			}
		} /*
			 * for(Movetree m:list) { m.Move.Position = z.Position; Position afterwards =
			 * executeMove(m.Move); m.Position=afterwards; if(controlCheckW(afterwards))
			 * list.remove(m); } list.trimToSize(); if(list.isEmpty()) list.add(new
			 * Movetree(new Move(20,0,0)));
			 */
		return list;
	}

	private ArrayList<Movetree> kingmovesB(Position position, int from, Movetree movetree) {
		int[] Board = position.Board;
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + 1;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 25, from, to));
		to = from - 1;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 25, from, to));
		to = from + 10;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 25, from, to));
		to = from - 10;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 25, from, to));
		to = from + 11;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 25, from, to));
		to = from - 11;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 25, from, to));
		to = from + 9;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 25, from, to));
		to = from - 9;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 25, from, to));
		if (from == 95 && position.klros && Board[96] == 0 && Board[97] == 0 && Board[98] == 21 && !position.checkW
				&& !position.f8threat)
			list.add(new Movetree(movetree, 25, from, 97, -3));
		if (from == 95 && position.grros && Board[94] == 0 && Board[93] == 0 && Board[92] == 0 && Board[91] == 21
				&& !position.checkW && !position.d8threat)
			list.add(new Movetree(movetree, 25, from, 93, -4));
		return list;
	}

	private ArrayList<Movetree> pawnmovesB(int[] Board, int from, Movetree movetree) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from - 10;
		if (Board[to] == 0)
			list.add(new Movetree(movetree, 20, from, to));
		else if (Board[to] == 0 && to < 30)
			list.add(new Movetree(movetree, 25, from, to, -2));
		to = from - 9;
		if (2 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 20, from, to));
		else if (2 < Board[to] && Board[to] < 18 && to < 30)
			list.add(new Movetree(movetree, 25, from, to, -2));
		to = from - 11;
		if (2 < Board[to] && Board[to] < 18)
			list.add(new Movetree(movetree, 20, from, to));
		else if (2 < Board[to] && Board[to] < 18 && to < 30)
			list.add(new Movetree(movetree, 25, from, to, -2));
		to = from - 20;
		if (from > 80 && Board[to] == 0 && Board[to + 10] == 0)
			list.add(new Movetree(movetree, 20, from, to));
		if (lastMove.piece == 10 && lastMove.from == from - 21 && lastMove.to == from - 1)
			list.add(new Movetree(movetree, 20, from, from - 11, -1));
		else if (lastMove.piece == 10 && lastMove.from == from - 19 && lastMove.to == from + 1)
			list.add(new Movetree(movetree, 20, from, from - 9, -1));
		return list;
	}

	private ArrayList<Movetree> knightmovesB(int[] Board, int from, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from - 21;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, 22, from, to));
		to = from - 19;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, 22, from, to));
		to = from - 12;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, 22, from, to));
		to = from - 8;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, 22, from, to));
		to = from + 8;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, 22, from, to));
		to = from + 12;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, 22, from, to));
		to = from + 19;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, 22, from, to));
		to = from + 21;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, 22, from, to));
		return list;
	}

	private ArrayList<Movetree> linemovesB(int[] Board, int from, int figur, int constant, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + constant;
		while (Board[to] == 0) {
			list.add(new Movetree(z, figur, from, to));
			to += constant;
		}
		if (1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(z, figur, from, to));
		return list;
	}

	public ArrayList<Movetree> legalMovesB(Position s, Movetree z) {
		s.checkB = controlCheckB(s);
		ArrayList<Movetree> raw = movesB(s, z);
		for (int i = 0; i < raw.size(); i++) {
			Movetree m = raw.get(i);
			Position after = executeMove(m);
			if (controlCheckB(after)) {
				raw.remove(m);
				i--;
			}
		}
		return raw;
	}

	public ArrayList<Movetree> legalMovesW(Position s, Movetree z) {
		s.checkW = controlCheckW(s);
		ArrayList<Movetree> raw = movesW(s, z);
		for (int i = 0; i < raw.size(); i++) {
			Movetree m = raw.get(i);
			Position after = executeMove(m);
			if (controlCheckW(after)) {
				raw.remove(m);
				i--;
			}
		}
		return raw;
	}

	public ArrayList<Movetree> sortedadmovesB(Position s, Movetree z) {
		s.checkB = controlCheckB(s);
		ArrayList<Movetree> raw = sortedmovesB(s, z);
		for (int i = 0; i < raw.size(); i++) {
			Movetree m = raw.get(i);
			Position after = executeMove(m);
			if (controlCheckB(after)) {
				raw.remove(m);
				i--;
			}
		}
		return raw;
	}

	public ArrayList<Movetree> sortedadmovesW(Position s, Movetree z) {
		s.checkW = controlCheckW(s);
		ArrayList<Movetree> raw = sortedmovesW(s, z);
		for (int i = 0; i < raw.size(); i++) {
			Movetree m = raw.get(i);
			Position after = executeMove(m);
			if (controlCheckW(after)) {
				raw.remove(m);
				i--;
			}
		}
		return raw;
	}

	/**
	 * 
	 * @param s
	 *            Die Position, deren mögliche Züge berechnet werden soll
	 * @param z
	 *            Movtree, der als Vater dieser Züge angegeben wird
	 * @return ArrayList<Movetree>, enthält alle möglichen weissen Züge
	 */

	public ArrayList<Movetree> movesW(Position s, Movetree z) {
		int[] Board = s.Board;
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		for (int i = 21; i < 99; i++) {
			int f = Board[i];
			switch (f) {
			case 10:
				list.addAll(pawnmovesW(Board, i, z));
				break;
			case 11:
				list.addAll(linemovesW(Board, i, 11, 10, z));
				list.addAll(linemovesW(Board, i, 11, -10, z));
				list.addAll(linemovesW(Board, i, 11, 1, z));
				list.addAll(linemovesW(Board, i, 11, -1, z));
				break;
			case 12:
				list.addAll(knightmovesW(Board, i, z));
				break;
			case 13:
				list.addAll(linemovesW(Board, i, 13, 9, z));
				list.addAll(linemovesW(Board, i, 13, -9, z));
				list.addAll(linemovesW(Board, i, 13, 11, z));
				list.addAll(linemovesW(Board, i, 13, -11, z));
				break;
			case 14:
				list.addAll(linemovesW(Board, i, 14, 10, z));
				list.addAll(linemovesW(Board, i, 14, -10, z));
				list.addAll(linemovesW(Board, i, 14, 1, z));
				list.addAll(linemovesW(Board, i, 14, -1, z));
				list.addAll(linemovesW(Board, i, 14, 9, z));
				list.addAll(linemovesW(Board, i, 14, -9, z));
				list.addAll(linemovesW(Board, i, 14, 11, z));
				list.addAll(linemovesW(Board, i, 14, -11, z));
				break;
			case 15:
				list.addAll(kingmovesW(s, i, z));
				break;
			}
		} /*
			 * for(Movetree m:list) { m.Move.Position = z.Position; Position afterwards =
			 * executeMove(m.Move); m.Position=afterwards; if(controlCheckB(afterwards))
			 * list.remove(m); } list.trimToSize(); if(list.isEmpty()) list.add(new
			 * Movetree(z,10,0,0)));
			 */
		return list;
	}

	private ArrayList<Movetree> kingmovesW(Position Pos, int from, Movetree z) {
		int[] Board = Pos.Board;
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + 1;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(z, 15, from, to));
		to = from - 1;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(z, 15, from, to));
		to = from + 10;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(z, 15, from, to));
		to = from - 10;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(z, 15, from, to));
		to = from + 11;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(z, 15, from, to));
		to = from - 11;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(z, 15, from, to));
		to = from + 9;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(z, 15, from, to));
		to = from - 9;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(z, 15, from, to));
		if (from == 25 && Pos.klrow && Board[26] == 0 && Board[27] == 0 && Board[28] == 11 && !Pos.checkB
				&& !Pos.f1threat)
			list.add(new Movetree(z, 15, from, 27, 3));
		if (from == 25 && Pos.grrow && Board[24] == 0 && Board[23] == 0 && Board[22] == 0 && Board[21] == 11
				&& !Pos.checkB && !Pos.d1threat)
			list.add(new Movetree(z, 15, from, 23, 4));
		return list;
	}

	private ArrayList<Movetree> linemovesW(int[] Board, int from, int figur, int constant, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + constant;
		while (Board[to] == 0) {
			list.add(new Movetree(z, figur, from, to));
			to += constant;
		}
		if (Board[to] > 18)
			list.add(new Movetree(z, figur, from, to));
		return list;
	}

	private ArrayList<Movetree> knightmovesW(int[] Board, int from, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from - 21;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(z, 12, from, to));
		to = from - 19;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(z, 12, from, to));
		to = from - 12;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(z, 12, from, to));
		to = from - 8;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(z, 12, from, to));
		to = from + 8;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(z, 12, from, to));
		to = from + 12;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(z, 12, from, to));
		to = from + 19;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(z, 12, from, to));
		to = from + 21;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(z, 12, from, to));
		return list;
	}

	private ArrayList<Movetree> pawnmovesW(int[] Board, int from, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + 10;
		if (Board[to] == 0 && to < 90)
			list.add(new Movetree(z, 10, from, to));
		else if (Board[to] == 0 && to > 90)
			list.add(new Movetree(z, 10, from, to, 2));
		to = from + 9;
		if (Board[to] > 18 && to < 90)
			list.add(new Movetree(z, 10, from, to));
		else if (Board[to] > 18 && to > 90)
			list.add(new Movetree(z, 10, from, to, 2));
		to = from + 11;
		if (Board[to] > 18 && to < 90)
			list.add(new Movetree(z, 10, from, to));
		else if (Board[to] > 18 && to > 90)
			list.add(new Movetree(z, 10, from, to, 2));
		to = from + 20;
		if (from < 40 && Board[to] == 0 && Board[to - 10] == 0)
			list.add(new Movetree(z, 10, from, to));
		if (lastMove.piece == 20 && lastMove.from == from + 21 && lastMove.to == from + 1)
			list.add(new Movetree(z, 10, from, from + 11, 1));
		if (lastMove.piece == 20 && lastMove.from == from + 19 && lastMove.to == from - 1)
			list.add(new Movetree(z, 10, from, from + 9, 1));
		return list;
	}

	// Mit Zugvorsortierung
	/**
	 * 
	 * @param s
	 *            Die Position, deren mögliche Züge berechnet werden soll
	 * @param z
	 *            Movtree, der als Vater dieser Züge angegeben wird
	 * @return ArrayList<Movetree>, enthält alle möglichen schwarzen Züge
	 */
	public ArrayList<Movetree> sortedmovesB(Position s, Movetree z) {
		int[] Board = s.Board;
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		for (int i = 21; i < 99; i++) {
			int f = Board[i];
			switch (f) {
			case 20:
				list.addAll(sortedPawnMovesB(Board, i, z));
				break;
			case 21:
				list.addAll(sortedLineMovesB(Board, i, 21, 10, z));
				list.addAll(sortedLineMovesB(Board, i, 21, -10, z));
				list.addAll(sortedLineMovesB(Board, i, 21, 1, z));
				list.addAll(sortedLineMovesB(Board, i, 21, -1, z));
				break;
			case 22:
				list.addAll(sortedKnightMovesB(Board, i, z));
				break;
			case 23:
				list.addAll(sortedLineMovesB(Board, i, 23, 9, z));
				list.addAll(sortedLineMovesB(Board, i, 23, -9, z));
				list.addAll(sortedLineMovesB(Board, i, 23, 11, z));
				list.addAll(sortedLineMovesB(Board, i, 23, -11, z));
				break;
			case 24:
				list.addAll(sortedLineMovesB(Board, i, 24, 10, z));
				list.addAll(sortedLineMovesB(Board, i, 24, -10, z));
				list.addAll(sortedLineMovesB(Board, i, 24, 1, z));
				list.addAll(sortedLineMovesB(Board, i, 24, -1, z));
				list.addAll(sortedLineMovesB(Board, i, 24, 9, z));
				list.addAll(sortedLineMovesB(Board, i, 24, -9, z));
				list.addAll(sortedLineMovesB(Board, i, 24, 11, z));
				list.addAll(sortedLineMovesB(Board, i, 24, -11, z));
				break;
			case 25:
				list.addAll(sortedKingMovesB(s, i, z));
				break;
			}
		}
		Collections.sort(list);
		Collections.reverse(list);
		return list;
	}

	private ArrayList<Movetree> sortedKingMovesB(Position Pos, int from, Movetree z) {
		int[] Board = Pos.Board;
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + 1;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - 10, z, 25, from, to));
		to = from - 1;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - 10, z, 25, from, to));
		to = from + 10;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - 10, z, 25, from, to));
		to = from - 10;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - 10, z, 25, from, to));
		to = from + 11;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - 10, z, 25, from, to));
		to = from - 11;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - 10, z, 25, from, to));
		to = from + 9;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - 10, z, 25, from, to));
		to = from - 9;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - 10, z, 25, from, to));
		if (from == 95 && Pos.klros && Board[96] == 0 && Board[97] == 0 && Board[98] == 21 && !Pos.checkW
				&& !Pos.f8threat)
			list.add(new Movetree(35, z, 25, from, 97, -3));
		if (from == 95 && Pos.grros && Board[94] == 0 && Board[93] == 0 && Board[92] == 0 && Board[91] == 21
				&& !Pos.checkW && !Pos.d8threat)
			list.add(new Movetree(30, z, 25, from, 93, -4));
		return list;
	}

	private ArrayList<Movetree> sortedPawnMovesB(int[] Board, int from, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from - 10;
		if (Board[to] == 0)
			list.add(new Movetree(1, z, 20, from, to));
		else if (Board[to] == 0 && to < 30)
			list.add(new Movetree(40, z, 25, from, to, -2));
		to = from - 9;
		if (2 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to], z, 20, from, to));
		else if (2 < Board[to] && Board[to] < 18 && to < 30)
			list.add(new Movetree(Board[to] + 40, z, 25, from, to, -2));
		to = from - 11;
		if (2 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to], z, 20, from, to));
		else if (2 < Board[to] && Board[to] < 18 && to < 30)
			list.add(new Movetree(Board[to] + 40, z, 25, from, to, -2));
		to = from - 20;
		if (from > 80 && Board[to] == 0 && Board[to + 10] == 0)
			list.add(new Movetree(2, z, 20, from, to));
		if (lastMove.piece == 10 && lastMove.from == from - 21 && lastMove.to == from - 1)
			list.add(new Movetree(11, z, 20, from, from - 11, -1));
		else if (lastMove.piece == 10 && lastMove.from == from - 19 && lastMove.to == from + 1)
			list.add(new Movetree(11, z, 20, from, from - 9, -1));
		return list;
	}

	private ArrayList<Movetree> sortedKnightMovesB(int[] Board, int from, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from - 21;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 22, from, to));
		to = from - 19;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 22, from, to));
		to = from - 12;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 22, from, to));
		to = from - 8;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 22, from, to));
		to = from + 8;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 22, from, to));
		to = from + 12;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 22, from, to));
		to = from + 19;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 22, from, to));
		to = from + 21;
		if (-1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 22, from, to));
		return list;
	}

	private ArrayList<Movetree> sortedPawnMovesW(int[] Board, int from, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + 10;
		if (Board[to] == 0 && to < 90)
			list.add(new Movetree(1, z, 10, from, to));
		else if (Board[to] == 0 && to > 90)
			list.add(new Movetree(40, z, 10, from, to, 2));
		to = from + 9;
		if (Board[to] > 18 && to < 90)
			list.add(new Movetree(Board[to], z, 10, from, to));
		else if (Board[to] > 18 && to > 90)
			list.add(new Movetree(Board[to] + 40, z, 10, from, to, 2));
		to = from + 11;
		if (Board[to] > 18 && to < 90)
			list.add(new Movetree(Board[to], z, 10, from, to));
		else if (Board[to] > 18 && to > 90)
			list.add(new Movetree(Board[to] + 40, z, 10, from, to, 2));
		to = from + 20;
		if (from < 40 && Board[to] == 0 && Board[to - 10] == 0)
			list.add(new Movetree(2, z, 10, from, to));
		if (lastMove.piece == 20 && lastMove.from == from + 21 && lastMove.to == from + 1)
			list.add(new Movetree(21, z, 10, from, from + 11, 1));
		if (lastMove.piece == 20 && lastMove.from == from + 19 && lastMove.to == from - 1)
			list.add(new Movetree(21, z, 10, from, from + 9, 1));
		return list;
	}

	private ArrayList<Movetree> sortedLineMovesB(int[] Board, int from, int figur, int constant, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + constant;
		while (Board[to] == 0) {
			list.add(new Movetree(1, z, figur, from, to));
			to += constant;
		}
		if (1 < Board[to] && Board[to] < 18)
			list.add(new Movetree(Board[to] - figur, z, figur, from, to));
		return list;
	}

	/**
	 * 
	 * @param s
	 *            Die Position, deren mögliche Züge berechnet werden soll
	 * @param z
	 *            Movtree, der als Vater dieser Züge angegeben wird
	 * @return ArrayList<Movetree>, enthält alle möglichen weissen Züge
	 */

	public ArrayList<Movetree> sortedmovesW(Position s, Movetree z) {
		int[] Board = s.Board;
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		for (int i = 21; i < 99; i++) {
			int f = Board[i];
			switch (f) {
			case 10:
				list.addAll(sortedPawnMovesW(Board, i, z));
				break;
			case 11:
				list.addAll(sortedLineMovesW(Board, i, 11, 10, z));
				list.addAll(sortedLineMovesW(Board, i, 11, -10, z));
				list.addAll(sortedLineMovesW(Board, i, 11, 1, z));
				list.addAll(sortedLineMovesW(Board, i, 11, -1, z));
				break;
			case 12:
				list.addAll(sortedKnightMovesW(Board, i, z));
				break;
			case 13:
				list.addAll(sortedLineMovesW(Board, i, 13, 9, z));
				list.addAll(sortedLineMovesW(Board, i, 13, -9, z));
				list.addAll(sortedLineMovesW(Board, i, 13, 11, z));
				list.addAll(sortedLineMovesW(Board, i, 13, -11, z));
				break;
			case 14:
				list.addAll(sortedLineMovesW(Board, i, 14, 10, z));
				list.addAll(sortedLineMovesW(Board, i, 14, -10, z));
				list.addAll(sortedLineMovesW(Board, i, 14, 1, z));
				list.addAll(sortedLineMovesW(Board, i, 14, -1, z));
				list.addAll(sortedLineMovesW(Board, i, 14, 9, z));
				list.addAll(sortedLineMovesW(Board, i, 14, -9, z));
				list.addAll(sortedLineMovesW(Board, i, 14, 11, z));
				list.addAll(sortedLineMovesW(Board, i, 14, -11, z));
				break;
			case 15:
				list.addAll(sortedKingMovesW(s, i, z));
				break;
			}
		}
		Collections.sort(list);
		Collections.reverse(list);
		return list;
	}

	private ArrayList<Movetree> sortedKingMovesW(Position Pos, int from, Movetree z) {
		int[] Board = Pos.Board;
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + 1;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(Board[to] - 10, z, 15, from, to));
		to = from - 1;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(Board[to] - 10, z, 15, from, to));
		to = from + 10;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(Board[to] - 10, z, 15, from, to));
		to = from - 10;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(Board[to] - 10, z, 15, from, to));
		to = from + 11;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(Board[to] - 10, z, 15, from, to));
		to = from - 11;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(Board[to] - 10, z, 15, from, to));
		to = from + 9;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(Board[to] - 10, z, 15, from, to));
		to = from - 9;
		if (0 == Board[to] || Board[to] > 18)
			list.add(new Movetree(Board[to] - 10, z, 15, from, to));
		if (from == 25 && Pos.klrow && Board[26] == 0 && Board[27] == 0 && Board[28] == 11 && !Pos.checkB
				&& !Pos.f1threat)
			list.add(new Movetree(35, z, 15, from, 27, 3));
		if (from == 25 && Pos.grrow && Board[24] == 0 && Board[23] == 0 && Board[22] == 0 && Board[21] == 11
				&& !Pos.checkB && !Pos.d1threat)
			list.add(new Movetree(30, z, 15, from, 23, 4));
		return list;
	}

	private ArrayList<Movetree> sortedLineMovesW(int[] Board, int from, int figur, int constant, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from + constant;
		while (Board[to] == 0) {
			list.add(new Movetree(1, z, figur, from, to));
			to += constant;
		}
		if (Board[to] > 18)
			list.add(new Movetree(Board[to] - figur, z, figur, from, to));
		return list;
	}

	private ArrayList<Movetree> sortedKnightMovesW(int[] Board, int from, Movetree z) {
		ArrayList<Movetree> list = new ArrayList<Movetree>();
		int to = from - 21;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 12, from, to));
		to = from - 19;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(1 + Board[to] - 3, z, 12, from, to));
		to = from - 12;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(2 + Board[to] - 3, z, 12, from, to));
		to = from - 8;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(2 + Board[to] - 3, z, 12, from, to));
		to = from + 8;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(3 + Board[to] - 3, z, 12, from, to));
		to = from + 12;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(3 + Board[to] - 3, z, 12, from, to));
		to = from + 19;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(4 + Board[to] - 3, z, 12, from, to));
		to = from + 21;
		if (Board[to] == 0 || Board[to] > 18)
			list.add(new Movetree(4 + Board[to] - 3, z, 12, from, to));
		return list;
	}

	/**
	 * liefert eine ganze Zufallszahl
	 * 
	 * @param from
	 *            : untere Grenze
	 * @param to
	 *            : obere Grenze
	 * @return int x, from <= x <= to
	 */

	private int randomnumber(int from, int to) {
		if (from == to)
			return from;
		else {
			double d = Math.random();
			d = d * (to - from) + from;
			return (int) Math.round(d);
		}
	}

}