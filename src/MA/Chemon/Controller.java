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
	State status = State.Human_versus_Machine;

	public Controller() {

		setBoardUp();
		engine = new Engine(position);
		engine.Controller = this;
		view = new Interface();
		view.controller = this;
		newGame();
	}

	public static void main(String[] args) {
		Controller con = new Controller();
		con.openOptions();

		/*
		 * int[] i = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		 * -1, -1, -1, -1, -1, 11, 12, 13, 14, 15, 13, 12, 11, -1, -1, 10, 10, 10, 0, 0,
		 * 10, 10, 10, -1, -1, 0, 0, 0, 0, 10, 0, 0, 0, -1, -1, 0, 23, 0, 10, 0, 0, 0,
		 * 0, -1, -1, 0, 0, 0, 20, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 20, 22, 0, 0, -1, -1,
		 * 20, 20, 20, 0, 0, 20, 20, 20, -1, -1, 21, 22, 23, 24, 25, 0, 0, 21, -1, -1,
		 * -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		 * };
		 */
		/*
		 * int[] i = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		 * -1, -1, -1, -1, -1, 11, 22, 23, 23, 15, 13, 12, 11, -1, -1, 10, 10, 20, 0, 0,
		 * 10, 10, 10, -1, -1, 0, 0, 0, 0, 10, 0, 0, 0, -1, -1, 0, 23, 0, 10, 0, 0, 0,
		 * 0, -1, -1, 0, 0, 0, 20, 0, 0, 0, 0, -1, -1, 0, 0, 0, 0, 20, 22, 0, 0, -1, -1,
		 * 20, 20, 20, 0, 0, 20, 20, 20, -1, -1, 21, 22, 23, 24, 25, 0, 0, 21, -1, -1,
		 * -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		 * }; con.Engine = new Engine(new Position(i));
		 * 
		 * ArrayList<Movetree> l = con.Engine.admovesW(con.Engine.actualPosition,
		 * con.Engine.startTree); for (Movetree t : l) System.out.println(t);
		 * System.out.println(l.size());
		 */

	}

	/**
	 * Reagiert auf das Ereignis Maus gedrückt
	 * 
	 * @param where
	 *            Position auf dem Brett wo die Maus gedrückt wurde
	 */
	public void handleMousePressed(int where) {
		if (!(status == State.Machine_versus_Machine) && running) {
			if (pieceHeld) // Fals eine Figur gehalten wird, wird überprüft ob
				// der Zug möglich ist
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
						view.unmark(where);
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
						view.unmark(where);
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
					if (!engine.mateW)
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
		view.unmark(pos);
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
		view.displayMove(m);
		view.unmark(fromW);
		view.unmark(toW);
		fromW = m.from;
		toW = m.to;
		view.mark(m.from, Color.blue);
		view.mark(m.to, Color.blue);
		movecount++;
		if (engine.legalMovesB(position, test).isEmpty() && engine.controlCheckB(position)) {
			engine.mateW = true;
			running = false;
		}
		whitesTurn = false;
		insertWhiteMove(m, pieceTaken);
		if (engine.mateW) {
			running = false;
			return;
		}
		if (status == State.Human_versus_Human)
			engine.actualPosition = this.position;
		if (status == State.Machine_versus_Machine) {
			engine.work();
			handleBlackMove(engine.bestMove);

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
		view.displayMove(m);
		view.unmark(fromW);
		view.unmark(toW);
		fromW = m.from;
		toW = m.to;
		view.mark(m.from, Color.blue);
		view.mark(m.to, Color.blue);
		movecount++;
		if (engine.legalMovesB(position, test).isEmpty() && engine.controlCheckB(position)) {
			engine.mateW = true;
			running = false;
		}
		whitesTurn = false;
		insertWhiteMove(m, pieceTaken);
		if (engine.mateW) {
			running = false;
			return;
		}
		if (status == State.Human_versus_Human)
			engine.actualPosition = this.position;
		if (status == State.Machine_versus_Machine) {
			engine.work();
			handleBlackMove(engine.bestMove);

		}
	}

	public void handleBlackMove(Move m) {
		boolean pieceTaken = position.Board[m.to] < 17 && position.Board[m.to] > 5;
		oldPositions.push(position.clone());
		position = engine.actualPosition;
		Movetree test = new Movetree(m);
		test.position = position;
		position = engine.executeMove(test);
		test.position = position;
		view.displayMove(m);
		view.unmark(fromB);
		view.unmark(toB);
		fromB = m.from;
		toB = m.to;
		view.mark(m.from, Color.yellow);
		view.mark(m.to, Color.yellow);
		if (engine.legalMovesW(position, test).isEmpty() && engine.controlCheckW(position)) {
			engine.mateB = true;
			running = false;
		}
		insertBlackMove(m, pieceTaken);
		whitesTurn = true;

		if (status == State.Human_versus_Human)
			engine.actualPosition = this.position;
		if (status == State.Machine_versus_Machine) {
			engine.work();
			handleWhiteMove(engine.bestMove);

		}

	}

	public void insertWhiteMove(Move m, boolean b) // der weisse
	// Zug wird
	// aufgeschrieben
	{
		notation = movecount + ". " + m.toString(b);
		if (engine.mateW) {
			notation += "# 1-0";
			if (status == State.Human_versus_Machine)
				JOptionPane.showMessageDialog(null, "Gratulation! Sie haben gewonnen.");
			else
				JOptionPane.showMessageDialog(null, "Schachmatt. Weiss hat gewonnen.");
		} else if (engine.controlCheckW(engine.actualPosition))
			notation += "+";
		view.t1.append(notation);
	}

	public void insertBlackMove(Move m, boolean b) // Der schwarze
	// Zug wird
	// aufgeschrieben
	{
		notation = " " + m.toString(b);
		if (engine.mateB) {
			notation += "# 0-1";
			if (status == State.Human_versus_Machine)
				JOptionPane.showMessageDialog(null, "Checkmate.");
			else
				JOptionPane.showMessageDialog(null, "Schachmatt. Schwarz hat gewonnen.");

		} else if (engine.controlCheckB(engine.actualPosition))
			notation += "+";
		notation += "\n";
		view.t1.append(notation);
		notation = "";
		view.t2.append(engine.evaluateMovetree(engine.startTree) + "\n");
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

	/**
	 * Öffnet ein Fenster in welchem der Spieler auswählen kann, zu welcher Figur er
	 * sein Bauer umwandeln will
	 */
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

	// Menu methods

	/**
	 * Der Spieler bietet Remis an
	 */
	public void offerDraw() {
		if (running) {
			if (engine.evaluateMovetree(engine.startTree) < -5) {
				JOptionPane.showMessageDialog(null, "Vielen Dank, gnädiger Herr.\nIhr" + " seid zu grosszügig.");
				running = false;
				view.t1.append("1/2-1/2");
			} else
				JOptionPane.showMessageDialog(null, "Nichts da. Ich kämpfe bis zum letzen Mann!");
		}
	}

	/**
	 * Der Spieler gibt auf
	 */
	public void resign() {
		running = false;
		if (isWhite) {
			view.t1.append("1-0");
			engine.mateW = true;
		} else {
			view.t1.append("0-1");
			engine.mateB = true;
		}
		JOptionPane.showMessageDialog(null, "Dankeschön.");
	}

	/**
	 * Startet ein neues Spiel
	 */
	public void newGame() {
		setBoardUp();
		view.t1.setText("");
		view.t2.setText("");
		running = true;
		view.refresh();
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
		engine.Controller = this;
		if (status == State.Machine_versus_Machine) {
			engine.work();
			handleWhiteMove(engine.bestMove);
		}
	}

	/**
	 * Öffnet ein Spiel das im PGN-Format gespeichert wurde.
	 */
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
				view.t1.setText(string);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Speichert das Spiel im PGN-Format
	 */
	@SuppressWarnings("deprecation")
	public void saveGame() {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(view.frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			FileWriter fw = null;
			String pfad = fc.getCurrentDirectory().getPath();
			String name = fc.getSelectedFile().getName();
			String resultat;
			String player = JOptionPane.showInputDialog("Wie lautet ihr Name?");
			if (engine.mateW)
				resultat = "1-0";
			else if (engine.mateB)
				resultat = "0-1";
			else
				resultat = "1/2-1/2";
			try {
				fw = new FileWriter(pfad + "\\" + name + ".pgn");
				fw.write("[Event \"Trainingspartie\"]\n[Site \"Schweiz\"]\n[Date \"" + new Date().getDate() + "\"]\n"
						+ "[White \"" + player + "\"]\n[Black \"Chemon 1.3\"]\n[Round \"1\"]\n" + "[Result \""
						+ resultat + "\"]\n" + /*
												 * [ECO\ "C10\"]\n[WhiteElo \"2485\"]\n[BlackElo \"2663\"]\n" +
												 */
						"[PlyCount \"" + (engine.numberOfMoves) + "\"]\n" + view.t1.getText());
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
			view.refresh();
			if (success)
				JOptionPane.showMessageDialog(null, "Erfolgreich gespeichert.");
		}
	}

	public void moveBack() {
		if (!oldPositions.empty()) {
			position = oldPositions.pop();
			engine.actualPosition = position;
			whitesTurn = !whitesTurn;
			view.refresh();
		}
	}

	/**
	 * öffnet ein neues Fenster mit Optionen
	 */
	public void openOptions() {
		String[] playmodes = { "Human vs Human ", "Human vs Machine", "Machine vs Machine" };
		int antwort = JOptionPane.showOptionDialog(view.frame, "Wählen sie den Spielmodus", "Auswahl",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, playmodes, "Human vs Machine");
		if (antwort == 0)
			status = State.Human_versus_Human;
		else if (antwort == 1)
			status = State.Human_versus_Machine;
		else if (antwort == 2)
			status = State.Machine_versus_Machine;
		else if (antwort == -1)
			System.exit(0);

		view.refresh();
	}
	
	public void handleDraw() {
		view.t1.append(" 1/2-1/2");
	}

}
