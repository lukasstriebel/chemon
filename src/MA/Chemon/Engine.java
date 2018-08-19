package MA.Chemon;

import java.util.*;

import javax.swing.JOptionPane;

import MA.Openings.English;
import MA.Openings.Kings_Indian;
import MA.Openings.Opening;
import MA.Openings.Sicilian;
import MA.Openings.Slav;
import MA.Openings.Spanish;
import MA.util.Move;
import MA.util.MoveTree;
import MA.util.Position;
import MA.util.SearchAlgorithm;
import MA.util.State;

public class Engine {

	Move lastMove, bestMove;
	boolean inOpening, blackIsMated, whiteIsMated;
	int numberOfMoves = 0, analyzationDepth;
	MoveTree startTree, testTree = new MoveTree(0, 0, 0);
	Position currentPosition;
	Opening opening;
	Controller controller;
	int analyzedPositions;

	public Engine() {
		lastMove = new Move(10, 0, 0);
	}

	public Engine(Position position) {
		lastMove = new Move(10, 0, 0);
		currentPosition = position.clone();
		currentPosition.lastMove = lastMove;
		startTree = new MoveTree(lastMove);
		startTree.position = currentPosition;
	}

	public void findBestMove() {

		if (controller.whitesTurn) {
			calculateWhiteMove();
			MoveTree bestMoveTree = new MoveTree(bestMove);
			bestMoveTree.position = currentPosition;
			currentPosition = executeMove(bestMoveTree);
			startTree.position = currentPosition;
			lastMove = bestMove;
		} else {
			if (inOpening)
				playOpening();
			else if (numberOfMoves < 2)
				checkOpening();
			else
				calculateBlackMove();
			MoveTree m = new MoveTree(bestMove);
			m.position = currentPosition;
			currentPosition = executeMove(m);
			startTree.position = currentPosition;
			lastMove = bestMove;
			numberOfMoves++;
		}
	}

	private void calculateBlackMove() {
		
		int bestMoveValue = 0;
		startTree.children = legalMovesB(currentPosition, startTree);
		if (startTree.children.isEmpty()) {
			controller.running = false;
			if (blackIsChecked(currentPosition)) {
				blackIsMated = true;
				return;
			} else {
				controller.handleDraw();//
				JOptionPane.showMessageDialog(null, "Stalemate!");
			}
		}
		for (MoveTree moveTree : startTree.children) {
			moveTree.position = executeMove(moveTree);
			if (controller.searchWith == SearchAlgorithm.MinMax)
				bestMoveValue = min(moveTree, 2);
			else if (controller.searchWith == SearchAlgorithm.AlphaBeta)
				bestMoveValue = beta(moveTree, 1, -2000, 2000);
			else if (controller.searchWith == SearchAlgorithm.PrincipalVariation) {
				analyzationDepth = 3;
				bestMoveValue = PrincipalVariation(moveTree, 2, -2000, 2000);
			}
			moveTree.value = bestMoveValue;
		}
		if (startTree.children.size() == 1)
			bestMove = startTree.children.get(0).move;
		else {
			Collections.sort(startTree.children, Collections.reverseOrder());
			int bestValue = startTree.children.get(0).value, secondBestValue;
			double difference;

			for (int i = 1; i < startTree.children.size(); i++) {
				secondBestValue = startTree.children.get(i).value;
				difference = bestValue - secondBestValue;
				if (difference > 2) {
					bestMove = startTree.children.get(randomNumber(0, i - 1)).move;
					break;
				}
				if (i == startTree.children.size() - 1) 
					bestMove = startTree.children.get(randomNumber(0, i)).move;				
			}
		}

		System.out.println(analyzedPositions);
		analyzedPositions = 0;
	}

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
			startTree.children = legalMovesW(currentPosition, startTree);
			if (startTree.children.isEmpty()) {
				controller.running = false;
				if (blackIsChecked(currentPosition)) {
					whiteIsMated = true;
					return;
				} else {
					controller.handleDraw();
					JOptionPane.showMessageDialog(null, "Patt!");
				}
			}
			for (MoveTree m : startTree.children) {
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
						bestMove = startTree.children.get(randomNumber(0, i - 1)).move;
						break;
					}
					startTree.value = startTree.children.get(0).value;
				}
			else {
				controller.running = false;
				controller.handleDraw();
			}
		}
		System.out.println(analyzedPositions);
		analyzedPositions = 0;
	}

	private int PrincipalVariation(MoveTree m, int pDepth, int alpha, int beta) {
		boolean PVfound = false;
		int value = 0;
		if (pDepth == 0)
			return evaluateMovetree(m);
		if (pDepth % 2 == analyzationDepth % 2)
			m.children = legalMovesW(m.position, m);
		else
			m.children = legalMovesB(m.position, m);
		if (!m.children.isEmpty()) {
			for (MoveTree child : m.children) {
				child.position = executeMove(child);
				if (PVfound) {
					value = PrincipalVariation(child, pDepth - 1, -alpha - 1, -alpha);
					if (value > alpha && value < beta)
						value = PrincipalVariation(child, pDepth - 1, -beta, -alpha);
				} else
					value = PrincipalVariation(child, pDepth - 1, -beta, -alpha);
				if (pDepth % 2 == analyzationDepth % 2) {
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
			if (pDepth % 2 == analyzationDepth % 2 && blackIsChecked(m.position))
				return 1000; // Weiss am Züge und weisser König im Schach(=Matt)
			else if ((pDepth + 1) % 2 == analyzationDepth % 2 && whiteIsChecked(m.position))
				return -1000;// Schwarz am Züge und schwarzer König im Schach(=Matt)
			else // Patt
				return 0;
		}
	}

	private int alpha(MoveTree m, int pDepth, int alpha, int beta) {
		if (pDepth == 0)
			return evaluateMovetree(m);
		m.children = legalMovesB(m.position, m);
		if (!m.children.isEmpty()) {
			for (MoveTree child : m.children) {
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
		} else if (blackIsChecked(m.position))
			return -100000;
		else
			return 0;

	}

	private int beta(MoveTree m, int pDepth, int alpha, int beta) {
		if (pDepth == 0)
			return evaluateMovetree(m);
		m.children = legalMovesW(m.position, m);
		if (!m.children.isEmpty()) {
			for (MoveTree child : m.children) {
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
		} else if (whiteIsChecked(m.position))
			return 100000000;
		else
			return 0;

	}

	private int max(MoveTree m, int pDepth) {
		if (pDepth == 0)
			return evaluateMovetree(m);
		int worst = -2000;
		m.children = legalMovesB(m.position, m);
		for (MoveTree child : m.children) {
			child.position = executeMove(child);
			int value = min(child, pDepth - 1);
			if (value > worst)
				worst = value;
		}
		m.children.clear();
		return worst;

	}

	private int min(MoveTree m, int pDepth) {
		if (pDepth == 0)
			return evaluateMovetree(m);
		int worst = 2000;
		m.children = legalMovesW(m.position, m);
		for (MoveTree child : m.children) {
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
		if (opening.helpTree.children != null)
			for (MoveTree m : opening.helpTree.children) {
				Book = m.move;
				if (Book.piece == lastMove.piece && Book.from == lastMove.from && Book.to == lastMove.to
						&& m.children != null) {
					notFound = false;
					opening.helpTree = m;
					int random = randomNumber(0, m.children.size() - 1);
					bestMove = m.children.get(random).move;
					lastMove = bestMove;
					if (controller.status != State.Machine_versus_Machine)
						opening.helpTree = m.children.get(random);
					controller.sleep(200);
				}
			}
		if (notFound) {
			inOpening = false;
			if (!controller.whitesTurn)
				calculateBlackMove();
			else
				calculateWhiteMove();
		}

	}

	private void checkOpening() {
		double random = Math.random();
		if (numberOfMoves == 1) {
			if (lastMove.piece == 10) {
				if (lastMove.from == 33 && lastMove.to == 53) {
					inOpening = true;
					opening = new English();
					playOpening();
				} else if (lastMove.from == 35 && lastMove.to == 55) {
					inOpening = true;
					if (random < 0.5)
						opening = new Sicilian();
					else
						opening = new Spanish();
					playOpening();
				} else if (lastMove.from == 34 && lastMove.to == 54) {
					inOpening = true;
					if (random < 0.5)
						opening = new Slav();
					else
						opening = new Kings_Indian();
					playOpening();
				} else
					calculateBlackMove();
			} else
				calculateBlackMove();
		}
	}

	public Position executeMove(MoveTree movetree) {// liefert Position nach einem Zug

		Move move = movetree.move;
		int[] board = movetree.position.board.clone();
		Position position = movetree.position.clone();
		board[move.to] = move.piece;
		board[move.from] = 0;
		if (move.piece == 15) {
			position.klrow = false;
			position.grrow = false;
			position.setWhiteKing(move.to);
		} else if (move.piece == 11 && move.from == 21)
			position.grrow = false;
		else if (move.piece == 11 && move.from == 28)
			position.klrow = false;

		if (move.piece == 25) {
			position.klros = false;
			position.grros = false;
			position.setBlackKing(move.to);
		} else if (move.piece == 21 && move.from == 91)
			position.grros = false;
		else if (move.piece == 21 && move.from == 98)
			position.klros = false;

		if (move.addition == 1) {// en passant
			board[move.to - 10] = 0;
		} else if (move.addition == 2) { // Umwandlung
			board[move.to] = 14;
		} else if (move.addition == 3) { // kl Rochade
			board[move.to - 1] = move.piece - 4;
			board[move.to + 1] = 0;
			position.klrow = false;
			position.grrow = false;
		}

		else if (move.addition == 4) { // gr Rochade
			board[move.to + 1] = move.piece - 4;
			board[move.to - 2] = 0;
			position.klrow = false;
			position.grrow = false;
		}

		else if (move.addition == -1) {// en passant
			board[move.to + 10] = 0;
		}

		else if (move.addition == -2) { // Umwandlung
			board[move.to] = 24;
		}

		else if (move.addition == -3) { // kl Rochade
			board[move.to - 1] = move.piece - 4;
			board[move.to + 1] = 0;
			position.klros = false;
			position.grros = false;
		}

		else if (move.addition == -4) { // gr Rochade
			board[move.to + 1] = move.piece - 4;
			board[move.to - 2] = 0;
			position.klros = false;
			position.grros = false;
		}
		position.lastMove = move;
		position.board = board;
		return position;
	}

	public boolean isLegalWhite(Move move) {

		ArrayList<MoveTree> possibleMoves = movesWhite(currentPosition, new MoveTree(move));
		boolean result = false;
		MoveTree movetree = null;
		for (MoveTree tree : possibleMoves) {
			if (tree.move.equals(move)) {
				move.addition = tree.move.addition;
				result = true;
				movetree = tree;
				break;
			}
		}

		if (result) {
			movetree.position = currentPosition.clone();
			Position pos = executeMove(movetree);
			int whiteKing = pos.getWhiteKing();
			possibleMoves = movesBlack(pos, movetree);
			for (MoveTree possibleMove : possibleMoves)
				if (possibleMove.to == whiteKing)
					return false;
		}

		return result;
	}

	public boolean isLegalBlack(Move move) {
		ArrayList<MoveTree> possibleMoves = movesBlack(currentPosition, new MoveTree(move));

		boolean result = false;
		MoveTree movetree = null;
		for (MoveTree possibleMove : possibleMoves) {
			if (possibleMove.move.equals(move)) {
				move.addition = possibleMove.move.addition;
				result = true;
				movetree = possibleMove;
				break;
			}
		}

		if (result) {
			movetree.position = currentPosition.clone();
			Position pos = executeMove(movetree);
			int blackKing = pos.getBlackKing();
			possibleMoves = movesWhite(pos, movetree);
			for (MoveTree possibleMove : possibleMoves)
				if (possibleMove.to == blackKing)
					return false;
		}

		return result;
	}

	public boolean blackIsChecked(Position position) {
		ArrayList<MoveTree> list = movesWhite(position, testTree);
		int blackKingPosition = position.getBlackKing();
		position.d8threat = false;
		position.f8threat = false;
		position.lastMove = new Move(0, 0, 0);
		for (MoveTree movetree : list) {
			int compare = movetree.move.to;
			if (compare == 94)
				position.d8threat = true;
			if (compare == 96)
				position.f8threat = true;
			if (compare == blackKingPosition)
				return true;
		}
		return false;
	}

	public boolean whiteIsChecked(Position position) {
		ArrayList<MoveTree> list = movesBlack(position, testTree);
		int whiteKingPosition = position.getWhiteKing();
		position.d1threat = false;
		position.f1threat = false;
		position.lastMove = new Move(0, 0, 0);
		for (MoveTree m : list) {
			int compare = m.move.to;
			if (compare == 24)
				position.d1threat = true;
			if (compare == 26)
				position.f1threat = true;
			if (compare == whiteKingPosition)
				return true;
		}
		return false;
	}

	public void handleWhiteMove(Move move) {
		lastMove = move;
		MoveTree currentMove = new MoveTree(move);
		currentMove.position = currentPosition;
		currentPosition = executeMove(currentMove);
		numberOfMoves++;
		startTree = new MoveTree(move);
		startTree.position = currentPosition;
	}

	public int evaluateMovetree(MoveTree movetree) {
		if (legalMovesW(movetree.position, movetree).size() == 0)
			return 10000000;
		int Insgesamt = evaluatePosition(movetree.position) + evaluateMaterial(movetree.position.board);
		Insgesamt += movesBlack(movetree.position, movetree).size();
		analyzedPositions++;
		return Insgesamt;
	}

	public int evaluatePosition(Position position) {
		int Position = 0;
		if (currentPosition.klros)
			Position += 5;
		if (currentPosition.grros)
			Position += 4;
		if (!currentPosition.klrow)
			Position += 5;
		if (!currentPosition.grrow)
			Position += 4;
		int posKW = 0, posKB = 0;
		for (int i = 21; i < 99; i++) {
			if (position.board[i] == 25)
				posKB = i;
			else if (position.board[i] == 15)
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
		ArrayList<MoveTree> list = movesBlack(position, testTree);
		for (MoveTree m : list) {
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

	public ArrayList<MoveTree> movesBlack(Position position, MoveTree movetreee) {
		int[] board = position.board;
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		for (int i = 21; i < 99; i++) {
			int f = board[i];
			switch (f) {
			case 20:
				list.addAll(pawnmovesB(board, i, movetreee));
				break;
			case 21:
				list.addAll(linemovesB(board, i, 21, 10, movetreee));
				list.addAll(linemovesB(board, i, 21, -10, movetreee));
				list.addAll(linemovesB(board, i, 21, 1, movetreee));
				list.addAll(linemovesB(board, i, 21, -1, movetreee));
				break;
			case 22:
				list.addAll(knightmovesB(board, i, movetreee));
				break;
			case 23:
				list.addAll(linemovesB(board, i, 23, 9, movetreee));
				list.addAll(linemovesB(board, i, 23, -9, movetreee));
				list.addAll(linemovesB(board, i, 23, 11, movetreee));
				list.addAll(linemovesB(board, i, 23, -11, movetreee));
				break;
			case 24:
				list.addAll(linemovesB(board, i, 24, 10, movetreee));
				list.addAll(linemovesB(board, i, 24, -10, movetreee));
				list.addAll(linemovesB(board, i, 24, 1, movetreee));
				list.addAll(linemovesB(board, i, 24, -1, movetreee));
				list.addAll(linemovesB(board, i, 24, 9, movetreee));
				list.addAll(linemovesB(board, i, 24, -9, movetreee));
				list.addAll(linemovesB(board, i, 24, 11, movetreee));
				list.addAll(linemovesB(board, i, 24, -11, movetreee));
				break;
			case 25:
				list.addAll(kingmovesB(position, i, movetreee));
				break;
			}
		}
		return list;
	}

	private ArrayList<MoveTree> kingmovesB(Position position, int from, MoveTree movetree) {
		int[] board = position.board;
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		int to = from + 1;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 25, from, to));
		to = from - 1;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 25, from, to));
		to = from + 10;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 25, from, to));
		to = from - 10;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 25, from, to));
		to = from + 11;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 25, from, to));
		to = from - 11;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 25, from, to));
		to = from + 9;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 25, from, to));
		to = from - 9;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 25, from, to));
		if (from == 95 && position.klros && board[96] == 0 && board[97] == 0 && board[98] == 21 && !position.checkW
				&& !position.f8threat)
			list.add(new MoveTree(movetree, 25, from, 97, -3));
		if (from == 95 && position.grros && board[94] == 0 && board[93] == 0 && board[92] == 0 && board[91] == 21
				&& !position.checkW && !position.d8threat)
			list.add(new MoveTree(movetree, 25, from, 93, -4));
		return list;
	}

	private ArrayList<MoveTree> pawnmovesB(int[] board, int from, MoveTree movetree) {
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		int to = from - 10;
		if (board[to] == 0)
			list.add(new MoveTree(movetree, 20, from, to));
		else if (board[to] == 0 && to < 30)
			list.add(new MoveTree(movetree, 25, from, to, -2));
		to = from - 9;
		if (2 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 20, from, to));
		else if (2 < board[to] && board[to] < 18 && to < 30)
			list.add(new MoveTree(movetree, 25, from, to, -2));
		to = from - 11;
		if (2 < board[to] && board[to] < 18)
			list.add(new MoveTree(movetree, 20, from, to));
		else if (2 < board[to] && board[to] < 18 && to < 30)
			list.add(new MoveTree(movetree, 25, from, to, -2));
		to = from - 20;
		if (from > 80 && board[to] == 0 && board[to + 10] == 0)
			list.add(new MoveTree(movetree, 20, from, to));
		if (lastMove.piece == 10 && lastMove.from == from - 21 && lastMove.to == from - 1)
			list.add(new MoveTree(movetree, 20, from, from - 11, -1));
		else if (lastMove.piece == 10 && lastMove.from == from - 19 && lastMove.to == from + 1)
			list.add(new MoveTree(movetree, 20, from, from - 9, -1));
		return list;
	}

	private ArrayList<MoveTree> knightmovesB(int[] board, int from, MoveTree parent) {
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		int to = from - 21;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, 22, from, to));
		to = from - 19;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, 22, from, to));
		to = from - 12;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, 22, from, to));
		to = from - 8;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, 22, from, to));
		to = from + 8;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, 22, from, to));
		to = from + 12;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, 22, from, to));
		to = from + 19;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, 22, from, to));
		to = from + 21;
		if (-1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, 22, from, to));
		return list;
	}

	private ArrayList<MoveTree> linemovesB(int[] board, int from, int figur, int constant, MoveTree parent) {
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		int to = from + constant;
		while (board[to] == 0) {
			list.add(new MoveTree(parent, figur, from, to));
			to += constant;
		}
		if (1 < board[to] && board[to] < 18)
			list.add(new MoveTree(parent, figur, from, to));
		return list;
	}

	public ArrayList<MoveTree> legalMovesB(Position position, MoveTree parent) {
		position.checkB = blackIsChecked(position);
		ArrayList<MoveTree> raw = movesBlack(position, parent);
		for (int i = 0; i < raw.size(); i++) {
			MoveTree m = raw.get(i);
			Position after = executeMove(m);
			if (blackIsChecked(after)) {
				raw.remove(m);
				i--;
			}
		}
		return raw;
	}

	public ArrayList<MoveTree> legalMovesW(Position position, MoveTree parent) {
		position.checkW = whiteIsChecked(position);
		ArrayList<MoveTree> raw = movesWhite(position, parent);
		for (int i = 0; i < raw.size(); i++) {
			MoveTree m = raw.get(i);
			Position after = executeMove(m);
			if (whiteIsChecked(after)) {
				raw.remove(m);
				i--;
			}
		}
		return raw;
	}

	public ArrayList<MoveTree> movesWhite(Position position, MoveTree movetree) {
		int[] board = position.board;
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		for (int i = 21; i < 99; i++) {
			int piece = board[i];
			switch (piece) {
			case 10:
				list.addAll(pawnmovesW(board, i, movetree));
				break;
			case 11:
				list.addAll(linemovesW(board, i, 11, 10, movetree));
				list.addAll(linemovesW(board, i, 11, -10, movetree));
				list.addAll(linemovesW(board, i, 11, 1, movetree));
				list.addAll(linemovesW(board, i, 11, -1, movetree));
				break;
			case 12:
				list.addAll(knightmovesW(board, i, movetree));
				break;
			case 13:
				list.addAll(linemovesW(board, i, 13, 9, movetree));
				list.addAll(linemovesW(board, i, 13, -9, movetree));
				list.addAll(linemovesW(board, i, 13, 11, movetree));
				list.addAll(linemovesW(board, i, 13, -11, movetree));
				break;
			case 14:
				list.addAll(linemovesW(board, i, 14, 10, movetree));
				list.addAll(linemovesW(board, i, 14, -10, movetree));
				list.addAll(linemovesW(board, i, 14, 1, movetree));
				list.addAll(linemovesW(board, i, 14, -1, movetree));
				list.addAll(linemovesW(board, i, 14, 9, movetree));
				list.addAll(linemovesW(board, i, 14, -9, movetree));
				list.addAll(linemovesW(board, i, 14, 11, movetree));
				list.addAll(linemovesW(board, i, 14, -11, movetree));
				break;
			case 15:
				list.addAll(kingmovesW(position, i, movetree));
				break;
			}
		}
		return list;
	}

	private ArrayList<MoveTree> kingmovesW(Position Pos, int from, MoveTree parent) {
		int[] board = Pos.board;
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		int to = from + 1;
		if (0 == board[to] || board[to] > 18)
			list.add(new MoveTree(parent, 15, from, to));
		to = from - 1;
		if (0 == board[to] || board[to] > 18)
			list.add(new MoveTree(parent, 15, from, to));
		to = from + 10;
		if (0 == board[to] || board[to] > 18)
			list.add(new MoveTree(parent, 15, from, to));
		to = from - 10;
		if (0 == board[to] || board[to] > 18)
			list.add(new MoveTree(parent, 15, from, to));
		to = from + 11;
		if (0 == board[to] || board[to] > 18)
			list.add(new MoveTree(parent, 15, from, to));
		to = from - 11;
		if (0 == board[to] || board[to] > 18)
			list.add(new MoveTree(parent, 15, from, to));
		to = from + 9;
		if (0 == board[to] || board[to] > 18)
			list.add(new MoveTree(parent, 15, from, to));
		to = from - 9;
		if (0 == board[to] || board[to] > 18)
			list.add(new MoveTree(parent, 15, from, to));
		if (from == 25 && Pos.klrow && board[26] == 0 && board[27] == 0 && board[28] == 11 && !Pos.checkB
				&& !Pos.f1threat)
			list.add(new MoveTree(parent, 15, from, 27, 3));
		if (from == 25 && Pos.grrow && board[24] == 0 && board[23] == 0 && board[22] == 0 && board[21] == 11
				&& !Pos.checkB && !Pos.d1threat)
			list.add(new MoveTree(parent, 15, from, 23, 4));
		return list;
	}

	private ArrayList<MoveTree> linemovesW(int[] board, int from, int figur, int constant, MoveTree parent) {
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		int to = from + constant;
		while (board[to] == 0) {
			list.add(new MoveTree(parent, figur, from, to));
			to += constant;
		}
		if (board[to] > 18)
			list.add(new MoveTree(parent, figur, from, to));
		return list;
	}

	private ArrayList<MoveTree> knightmovesW(int[] board, int from, MoveTree parent) {
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		int to = from - 21;
		if (board[to] == 0 || board[to] > 18)
			list.add(new MoveTree(parent, 12, from, to));
		to = from - 19;
		if (board[to] == 0 || board[to] > 18)
			list.add(new MoveTree(parent, 12, from, to));
		to = from - 12;
		if (board[to] == 0 || board[to] > 18)
			list.add(new MoveTree(parent, 12, from, to));
		to = from - 8;
		if (board[to] == 0 || board[to] > 18)
			list.add(new MoveTree(parent, 12, from, to));
		to = from + 8;
		if (board[to] == 0 || board[to] > 18)
			list.add(new MoveTree(parent, 12, from, to));
		to = from + 12;
		if (board[to] == 0 || board[to] > 18)
			list.add(new MoveTree(parent, 12, from, to));
		to = from + 19;
		if (board[to] == 0 || board[to] > 18)
			list.add(new MoveTree(parent, 12, from, to));
		to = from + 21;
		if (board[to] == 0 || board[to] > 18)
			list.add(new MoveTree(parent, 12, from, to));
		return list;
	}

	private ArrayList<MoveTree> pawnmovesW(int[] board, int from, MoveTree parent) {
		ArrayList<MoveTree> list = new ArrayList<MoveTree>();
		int to = from + 10;
		if (board[to] == 0 && to < 90)
			list.add(new MoveTree(parent, 10, from, to));
		else if (board[to] == 0 && to > 90)
			list.add(new MoveTree(parent, 10, from, to, 2));
		to = from + 9;
		if (board[to] > 18 && to < 90)
			list.add(new MoveTree(parent, 10, from, to));
		else if (board[to] > 18 && to > 90)
			list.add(new MoveTree(parent, 10, from, to, 2));
		to = from + 11;
		if (board[to] > 18 && to < 90)
			list.add(new MoveTree(parent, 10, from, to));
		else if (board[to] > 18 && to > 90)
			list.add(new MoveTree(parent, 10, from, to, 2));
		to = from + 20;
		if (from < 40 && board[to] == 0 && board[to - 10] == 0)
			list.add(new MoveTree(parent, 10, from, to));
		if (lastMove.piece == 20 && lastMove.from == from + 21 && lastMove.to == from + 1)
			list.add(new MoveTree(parent, 10, from, from + 11, 1));
		if (lastMove.piece == 20 && lastMove.from == from + 19 && lastMove.to == from - 1)
			list.add(new MoveTree(parent, 10, from, from + 9, 1));
		return list;
	}

	private int randomNumber(int from, int to) {
		if (from == to)
			return from;
		else {
			double d = Math.random();
			d = d * (to - from) + from;
			return (int) Math.round(d);
		}
	}

}