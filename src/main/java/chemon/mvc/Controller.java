package chemon.mvc;

import java.awt.Color;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Stack;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import chemon.util.Move;
import chemon.util.MoveTree;
import chemon.util.Position;
import chemon.util.SearchAlgorithm;
import chemon.util.State;

public class Controller {

	Model engine; // Model
	View view; // View
	Stack<Position> oldPositions;
	Position position = new Position(new int[120]);
	int start, selectedPiece, movecount;
	boolean pieceHeld, running = true, isWhite, humanvsmachine = true, whitesTurn, draw;
	String notation;
	double time;
	SearchAlgorithm searchWith = SearchAlgorithm.AlphaBeta;
	State status = State.Human_versus_Machine;

	public Controller() {

		initializeBoard();
		engine = new Model(position);
		engine.controller = this;
		engine.numberOfMoves = 10;
		view = new View();
		view.controller = this;
		newGame();
	}

	public static void main(String[] args) {
		Controller con = new Controller();
		con.openOptions();
	}

	public void handleMousePressed(int where) {
		if (!(status == State.Machine_versus_Machine) && running) {
			if (pieceHeld) {
				checkMove(where);
			} else {
				selectedPiece = position.board[where];
				if (status == State.Human_versus_Machine) {
					if (selectedPiece > 5 && selectedPiece < 18) {
						pieceHeld = true;
						start = where;
						view.mark(where, Color.BLACK);
					} else {
						selectedPiece = 0;
						pieceHeld = false;
					}
				} else if (status == State.Human_versus_Human)
					if (((selectedPiece > 5 && selectedPiece < 18) && whitesTurn)
							|| (selectedPiece > 18 && !whitesTurn)) {
						pieceHeld = true;
						start = where;
						view.mark(where, Color.BLACK);
					} else {
						selectedPiece = 0;
						pieceHeld = false;
					}
			}
		}
	}

	public void checkMove(int field) {
		if (start != field) {
			Move move = new Move(selectedPiece, start, field);
			if (status == State.Human_versus_Machine) {
				if (engine.isLegalWhite(move) && running) {
					handleMove(move);
					if (running) {
						evaluateBestMove();
					}
				} else
					JOptionPane.showMessageDialog(null, "This is not a legal move!");
			} else if (status == State.Human_versus_Human)
				if (whitesTurn && selectedPiece < 18 && engine.isLegalWhite(move))
					handleMove(move);
				else if (!whitesTurn && selectedPiece > 18 && engine.isLegalBlack(move))
					handleMove(move);
		}
		pieceHeld = false;
		start = 0;
		selectedPiece = 0;
		view.frame.repaint();
	}

	public void evaluateBestMove() {
		double start = System.currentTimeMillis();
		engine.findBestMove();
		Move bestMove = engine.bestMove;
		double end = System.currentTimeMillis();
		view.l5.setText("Elapsed Time: " + (end - start) / 1000);
		time += (end - start) / 1000;
		view.l6.setText("Elapsed Time total: " + time);
		handleMove(bestMove);
	}

	public void handleMove(Move move) {
		boolean pieceTaken = position.board[move.to] > 0;
		oldPositions.push(position.clone());
		position = engine.currentPosition;
		MoveTree test = new MoveTree(move);
		test.position = position;
		position = engine.executeMove(test);
		test.position = position;
		view.frame.paint(view.frame.getGraphics());
		view.movesound();

		view.mark(move.from, Color.blue);
		view.mark(move.to, Color.blue);

		if (whitesTurn) {
			if (engine.legalMovesB(position, test).isEmpty()) {
				if (engine.blackIsChecked(position)) {
					engine.blackIsMated = true;
				} else {
					JOptionPane.showMessageDialog(null, "Stalemate!");
					draw = true;
				}
				running = false;
			}
			movecount++;
			insertWhiteMove(move, pieceTaken);
			engine.handleWhiteMove(move);
		} else {
			if (engine.legalMovesW(position, test).isEmpty()) {
				if (engine.whiteIsChecked(position))
					engine.whiteIsMated = true;
				else {
					JOptionPane.showMessageDialog(null, "Stalemate!");
				}
				running = false;
				draw = true;
			}
			insertBlackMove(move, pieceTaken);
		}
		whitesTurn = !whitesTurn;
		if (draw)
			handleDraw();
		if (status == State.Human_versus_Human)
			engine.currentPosition = this.position;
		if (status == State.Machine_versus_Machine && running) {
			engine.findBestMove();
			handleMove(engine.bestMove);

		}

	}

	public void insertWhiteMove(Move move, boolean pieceTaken) {
		notation = movecount + ". " + move.toString(pieceTaken);
		if (engine.blackIsMated) {
			notation += "# 1-0";
			if (status == State.Human_versus_Machine)
				JOptionPane.showMessageDialog(null, "Gratulation! Sie haben gewonnen.");
			else
				JOptionPane.showMessageDialog(null, "Schachmatt. Weiss hat gewonnen.");
		} else if (engine.blackIsChecked(engine.currentPosition))
			notation += "+";
		view.movesTextArea.append(notation);
	}

	public void insertBlackMove(Move move, boolean pieceTaken) {
		notation = " " + move.toString(pieceTaken);
		if (engine.whiteIsMated) {
			notation += "# 0-1";
			if (status == State.Human_versus_Machine)
				JOptionPane.showMessageDialog(null, "Checkmate.");
			else
				JOptionPane.showMessageDialog(null, "Schachmatt. Schwarz hat gewonnen.");

		} else if (engine.whiteIsChecked(engine.currentPosition))
			notation += "+";
		notation += "\n";
		view.movesTextArea.append(notation);
		notation = "";
		view.evaluationTextArea.append(engine.evaluateMovetree(engine.startTree) + "\n");
	}

	public void initializeBoard() {
		int[] board = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 12, 13,
				14, 15, 13, 12, 11, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0,
				0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 20, 20, 20,
				20, 20, 20, 20, 20, -1, -1, 21, 22, 23, 24, 25, 23, 22, 21, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, };
		position.board = board;
		position.grros = true;
		position.grrow = true;
		position.klros = true;
		position.klrow = true;
		position.setBlackKing(98);
		position.setWhiteKing(25);
		oldPositions = new Stack<Position>();
		oldPositions.push(position.clone());
	}

	public void sleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
		}
	}

	public int promotePawn() {
		int piece = 14;
		String[] s = { "Queen", "Rock", "Bishop", "Knight" };
		int antwort = JOptionPane.showOptionDialog(view.frame, "Umwandeln in?", "Auswahl", JOptionPane.YES_NO_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, s, "Queen");
		if (antwort == 0 || antwort == -1)
			piece = 14;
		else if (antwort == 1)
			piece = 11;
		else if (antwort == 2)
			piece = 13;
		else if (antwort == 3)
			piece = 12;
		return piece;
	}

	public void offerDraw() {
		if (running) {
			if (engine.evaluateMovetree(engine.startTree) < -5) {
				JOptionPane.showMessageDialog(null, "Vielen Dank, gn�diger Herr.\nIhr" + " seid zu grossz�gig.");
				running = false;
				view.movesTextArea.append("1/2-1/2");
			} else
				JOptionPane.showMessageDialog(null, "Nichts da. Ich k�mpfe bis zum letzen Mann!");
		}
	}

	public void resign() {
		running = false;
		if (isWhite) {
			view.movesTextArea.append("1-0");
			engine.blackIsMated = true;
		} else {
			view.movesTextArea.append("0-1");
			engine.whiteIsMated = true;
		}
		JOptionPane.showMessageDialog(null, "Dankesch�n.");
	}

	public void newGame() {
		initializeBoard();
		view.movesTextArea.setText("");
		view.evaluationTextArea.setText("");
		running = true;
		movecount = 0;
		pieceHeld = false;
		start = 0;
		selectedPiece = 0;
		notation = "";
		whitesTurn = true;
		engine = new Model(position);
		draw = false;
		engine.controller = this;
		if (status == State.Machine_versus_Machine) {
			engine.findBestMove();
			handleMove(engine.bestMove);
		}
	}

	public void openGame() {
		File file = null;
		JFileChooser fc = new JFileChooser();
		FileFilter filter = new FileFilter() {
			String extension = ".pgn";

			public String getDescription() {
				return "Portable Game Notation(*.pgn)";
			}

			public boolean accept(File f) {
				if (f == null)
					return false;
				return f.getName().toLowerCase().endsWith(extension);
			}
		};
		fc.setFileFilter(filter);
		int returnVal = fc.showOpenDialog(view.frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			try {
				FileReader fr = new FileReader(file);
				char c = (char) fr.read();
				StringBuffer stringBuffer = new StringBuffer();
				for (int i = 0; i < file.length(); i++) {
					stringBuffer.append(c);
					c = (char) fr.read();
					if (stringBuffer.length() > 200)
						break;
				}
				int i = stringBuffer.lastIndexOf("]");
				String string = stringBuffer.substring(i + 2);
				view.movesTextArea.setText(string);
				fr.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public void saveGame() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(view.frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			FileWriter fw = null;
			String path = fc.getCurrentDirectory().getPath();
			String name = fc.getSelectedFile().getName();
			String result;
			String player = JOptionPane.showInputDialog("Wie lautet ihr Name?");
			if (engine.blackIsMated)
				result = "1-0";
			else if (engine.whiteIsMated)
				result = "0-1";
			else
				result = "1/2-1/2";
			try {
				fw = new FileWriter(path + "\\" + name + ".pgn");
				fw.write("[Event \"Trainingspartie\"]\n[Site \"Schweiz\"]\n[Date \"" + new Date().getDate() + "\"]\n"
						+ "[White \"" + player + "\"]\n[Black \"Chemon 1.3\"]\n[Round \"1\"]\n" + "[Result \"" + result
						+ "\"]\n" + /*
									 * [ECO\ "C10\"]\n[WhiteElo \"2485\"]\n[BlackElo \"2663\"]\n" +
									 */
						"[PlyCount \"" + (engine.numberOfMoves) + "\"]\n" + view.movesTextArea.getText());
			} catch (IOException E) {
				JOptionPane.showMessageDialog(null, "Konnte Datei nicht erstellen.");
			}
			boolean success = true;
			if (fw != null)
				try {
					fw.close();
				} catch (IOException er) {
					JOptionPane.showMessageDialog(null, "Konnte Datei nicht erstellen.");
					success = false;
				}
			if (success)
				JOptionPane.showMessageDialog(null, "Erfolgreich gespeichert.");
		}
	}

	public void moveBack() {
		if (!oldPositions.empty()) {
			position = oldPositions.pop();
			engine.currentPosition = position;
			whitesTurn = !whitesTurn;
			running = true;
		}
		view.frame.repaint();
	}

	public void openOptions() {
		String[] playmodes = { "Human vs Human ", "Human vs Machine", "Machine vs Machine" };
		int response = JOptionPane.showOptionDialog(view.frame, "W�hlen sie den Spielmodus", "Auswahl",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, playmodes, "Human vs Machine");
		if (response == 0)
			status = State.Human_versus_Human;
		else if (response == 1)
			status = State.Human_versus_Machine;
		else if (response == 2)
			status = State.Machine_versus_Machine;
		else
			System.exit(0);
	}

	public void handleDraw() {
		view.movesTextArea.append(" 1/2-1/2");
	}

}
