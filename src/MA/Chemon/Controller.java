package MA.Chemon;

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

import MA.util.Move;
import MA.util.Movetree;
import MA.util.Position;
import MA.util.SearchAlgorithm;
import MA.util.State;

import java.util.*;

public class Controller {

	Engine engine; // Model
	Interface view; // View
	Stack<Position> oldPositions;
	Position position = new Position(new int[120]);
	int start, selectedPiece, fromB, toB, fromW, toW, movecount;
	boolean pieceHeld, running = true, isWhite, humanvsmachine = true, whitesTurn;
	String notation;
	double time;
	SearchAlgorithm searchWith = SearchAlgorithm.AlphaBeta;
	State status = State.Human_versus_Human;

	public Controller() {

		setBoardUp();
		engine = new Engine(position);
		engine.controller = this;
		view = new Interface();
		view.controller = this;
		newGame();
	}

	public static void main(String[] args) {
		Controller con = new Controller();
		//con.openOptions();
	}

	public void handleMousePressed(int where) {
		if (!(status == State.Machine_versus_Machine) && running) {
			if (pieceHeld)
				checkMove(where);
			else {
				selectedPiece = position.Board[where];
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

	public void checkMove(int pos) {
		if (start != pos) {
			Move m = new Move(selectedPiece, start, pos);
			if (status == State.Human_versus_Machine) {
				if (engine.isLegalWhite(m) && running) {
					handleWhiteMove(m);
					if (!engine.blackIsMated)
						goToWork();
				} else {
					JOptionPane.showMessageDialog(null, "This is not a legal move!");
				}
			} else if (status == State.Human_versus_Human) {
				if (whitesTurn && selectedPiece < 18 && engine.isLegalWhite(m))
					handleWhiteMove(m);
				else if (!whitesTurn && selectedPiece > 18 && engine.isLegalBlack(m))
					handleBlackMove(m);
			}
		}
		pieceHeld = false;
		start = 0;
		selectedPiece = 0;
	}

	public void goToWork() {
		double start = System.currentTimeMillis();
		engine.work();// Zuggenerator informieren
		Move m = engine.bestMove;// besten Zug holen
		double end = System.currentTimeMillis();
		view.l5.setText("Elapsed Time: " + (end - start) / 1000);
		time += (end - start) / 1000;
		view.l6.setText("Elapsed Time total: " + time);
		handleBlackMove(m);// Zug ausführen
	}

	public void handleWhiteMove(Move m) {
		boolean pieceTaken = position.Board[m.to] > 17;
		if (m.to > 90 && m.piece == 10)
			position.Board[m.to] = promotePawn();
		Movetree test = new Movetree(m);
		oldPositions.push(position.clone());
		test.position = position;
		position = engine.executeMove(test);
		test.position = position;
		engine.handleWhiteMove(m);
		fromW = m.from;
		toW = m.to;
		

		//view.panel.paintComponent(view.frame.getGraphics());
		view.frame.repaint();
		view.movesound();
		
		view.mark(m.from, Color.blue);
		view.mark(m.to, Color.blue);
		
		movecount++;
		if (engine.legalMovesB(position, test).isEmpty() && engine.controlCheckB(position)) {
			engine.blackIsMated = true;
			running = false;
		}
		whitesTurn = false;
		insertWhiteMove(m, pieceTaken);
		if (engine.blackIsMated) {
			running = false;
			return;
		}
		if (status == State.Human_versus_Human)
			engine.currentPosition = this.position;
		if (status == State.Machine_versus_Machine) {
			engine.work();
			handleBlackMove(engine.bestMove);

		}
	}

	public void handleBlackMove(Move m) {
		boolean pieceTaken = position.Board[m.to] < 17 && position.Board[m.to] > 5;
		oldPositions.push(position.clone());
		position = engine.currentPosition;
		Movetree test = new Movetree(m);
		test.position = position;
		position = engine.executeMove(test);
		test.position = position;
		fromB = m.from;
		toB = m.to;
		//view.panel.paintComponent(view.frame.getGraphics());
		view.frame.repaint();
		view.movesound();

		view.mark(m.from, Color.yellow);
		view.mark(m.to, Color.yellow);
		
		if (engine.legalMovesW(position, test).isEmpty() && engine.controlCheckW(position)) {
			engine.whiteIsMated = true;
			running = false;
		}
		insertBlackMove(m, pieceTaken);
		whitesTurn = true;

		if (status == State.Human_versus_Human)
			engine.currentPosition = this.position;
		if (status == State.Machine_versus_Machine) {
			engine.work();
			handleWhiteMove(engine.bestMove);

		}

	}
	
	public void handleMove(Move m) {

		oldPositions.push(position.clone());

		boolean pieceTaken = position.Board[m.to] > 17;
		if (m.to > 90 && m.piece == 10)
			position.Board[m.to] = promotePawn();
		Movetree test = new Movetree(m);
		test.position = position;
		position = engine.executeMove(test);
		test.position = position;
		if (m.piece < 20) {

		}
		engine.handleWhiteMove(m);
		fromW = m.from;
		toW = m.to;
		
		
		view.mark(m.from, Color.blue);
		view.mark(m.to, Color.blue);
		movecount++;
		if (engine.legalMovesB(position, test).isEmpty() && engine.controlCheckB(position)) {
			engine.blackIsMated = true;
			running = false;
		}
		whitesTurn = false;
		insertWhiteMove(m, pieceTaken);
		if (engine.blackIsMated) {
			running = false;
			return;
		}
		if (status == State.Human_versus_Human)
			engine.currentPosition = this.position;
		if (status == State.Machine_versus_Machine) {
			engine.work();
			handleBlackMove(engine.bestMove);

		}
	}

	public void insertWhiteMove(Move m, boolean b) 
	{
		notation = movecount + ". " + m.toString(b);
		if (engine.blackIsMated) {
			notation += "# 1-0";
			if (status == State.Human_versus_Machine)
				JOptionPane.showMessageDialog(null, "Gratulation! Sie haben gewonnen.");
			else
				JOptionPane.showMessageDialog(null, "Schachmatt. Weiss hat gewonnen.");
		} else if (engine.controlCheckW(engine.currentPosition))
			notation += "+";
		view.movesTextArea.append(notation);
	}

	public void insertBlackMove(Move m, boolean b) 
	{
		notation = " " + m.toString(b);
		if (engine.whiteIsMated) {
			notation += "# 0-1";
			if (status == State.Human_versus_Machine)
				JOptionPane.showMessageDialog(null, "Checkmate.");
			else
				JOptionPane.showMessageDialog(null, "Schachmatt. Schwarz hat gewonnen.");

		} else if (engine.controlCheckB(engine.currentPosition))
			notation += "+";
		notation += "\n";
		view.movesTextArea.append(notation);
		notation = "";
		view.evaluationTextArea.append(engine.evaluateMovetree(engine.startTree) + "\n");
	}

	/**
	 * Das Brett wird neu aufgestellt
	 */

	public void setBoardUp() {
		int[] i = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 11, 12, 13, 14,
				15, 13, 12, 11, -1, -1, 10, 10, 10, 10, 10, 10, 10, 10, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0,
				0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 20, 20, 20, 20,
				20, 20, 20, 20, -1, -1, 21, 22, 23, 24, 25, 23, 22, 21, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
				-1, -1, -1, -1, -1, -1, -1, -1,
				-1, };/*
						 * int[] i = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						 * -1, -1, -1, -1, -1, 11, 0, 0, 0, 0, 11, 15, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0,
						 * 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0,
						 * 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 0,
						 * 0, 0, 0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1,
						 * -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, };
						 */

		position.Board = i;
		position.grros = true;
		position.grrow = true;
		position.klros = true;
		position.klrow = true;
		oldPositions = new Stack<Position>();
		oldPositions.push(position.clone());
	}

	/**
	 * 
	 * @param msec
	 */
	public void sleep(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
		}
	}

	public int promotePawn() {
		int piece = 14;
		String[] s = { "Queen", "Rock", "Bishop", "Knight" };
		int antwort = JOptionPane.showOptionDialog(view.frame, "Umwandeln in?", "Auswahl",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, s, "Queen");
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
				JOptionPane.showMessageDialog(null, "Vielen Dank, gnädiger Herr.\nIhr" + " seid zu grosszügig.");
				running = false;
				view.movesTextArea.append("1/2-1/2");
			} else
				JOptionPane.showMessageDialog(null, "Nichts da. Ich kämpfe bis zum letzen Mann!");
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
		JOptionPane.showMessageDialog(null, "Dankeschön.");
	}

	public void newGame() {
		setBoardUp();
		view.movesTextArea.setText("");
		view.evaluationTextArea.setText("");
		running = true;
		fromB = 0;
		toB = 0;
		fromW = 0;
		toW = 0;
		movecount = 0;
		pieceHeld = false;
		start = 0;
		selectedPiece = 0;
		notation = "";
		whitesTurn = true;
		engine = new Engine(position);
		engine.controller = this;
		if (status == State.Machine_versus_Machine) {
			engine.work();
			handleWhiteMove(engine.bestMove);
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
				StringBuffer s = new StringBuffer();
				for (int i = 0; i < file.length(); i++) {
					s.append(c);
					c = (char) fr.read();
					if (s.length() > 200)
						break;
				}
				int i = s.lastIndexOf("]");
				String string = s.substring(i + 2);
				view.movesTextArea.setText(string);
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
						+ "[White \"" + player + "\"]\n[Black \"Chemon 1.3\"]\n[Round \"1\"]\n" + "[Result \""
						+ result + "\"]\n" + /*
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
		}
	}

	public void openOptions() {
		String[] playmodes = { "Human vs Human ", "Human vs Machine", "Machine vs Machine" };
		int response = JOptionPane.showOptionDialog(view.frame, "Wählen sie den Spielmodus", "Auswahl",
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
