package MA.Chemon;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

import MA.util.Move;
import MA.util.State;

public class Interface {

	private final int HORIZONTAL_OFFSET = 100;
	String[] gifPath = { "b", "w", "bpb", "bpw", "brb", "brw", "bnb", "bnw", "bbb", "bbw", "bqb", "bqw", "bkb", "bkw",
			"wpb", "wpw", "wrb", "wrw", "wnb", "wnw", "wbb", "wbw", "wqb", "wqw", "wkb", "wkw" };
	Image[] pictures = new Image[26];
	int squareLength;
	boolean[] boardColors;
	String path;

	Controller controller;
	JFrame frame, options = new JFrame("Options");
	JLabel l1, l2, l3, l4, l5, l6, l7;
	JRadioButton hvh = new JRadioButton("Human vs. Human"), hvm = new JRadioButton("Human vs. Machine"),
			mvm = new JRadioButton("Machine vs. Machine");
	JButton ok = new JButton("Okay");
	JTextArea t1, t2;
	JScrollPane scrollBar1, scrollBar2;
	JMenuBar menuBar;
	JMenu fileMenu, gameMenu, viewMenu;
	JMenuItem newGameItem, moveBackItem, exitItem, saveGameItem, openGameItem, resignItem, offerDrawItem, refreshItem,
			optionsItem;
	MyJPanel panel = new MyJPanel();

	class MyJPanel extends JPanel implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == newGameItem)
				controller.newGame();
			else if (e.getSource() == moveBackItem)
				controller.moveBack();
			else if (e.getSource() == saveGameItem)
				controller.saveGame();
			else if (e.getSource() == openGameItem)
				controller.openGame();
			else if (e.getSource() == resignItem)
				controller.resign();
			else if (e.getSource() == exitItem)
				System.exit(0);
			else if (e.getSource() == offerDrawItem)
				controller.offerDraw();
			else if (e.getSource() == optionsItem)
				controller.openOptions();
			else if (e.getSource() == refreshItem)
				refresh();
			refresh();
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g); // Java will das so
			l1.repaint();
			l2.repaint();
			l3.repaint();
			l4.repaint();
			l5.repaint();
			l6.repaint();
			drawBoard(g);
			drawPieces(g);
		}
	}

	public Interface() {

		setColors();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		System.out.println(dim);
		frame = new JFrame("Chemon");
		frame.setSize(dim);
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		initializeMenu();
		frame.setJMenuBar(menuBar);
		frame.setResizable(false);
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.handleMousePressed(mouseToBoard(e.getLocationOnScreen().x, e.getLocationOnScreen().y));
			}
		});
		squareLength = dim.height / 10;
		l1 = new JLabel("© 2010 - 2011 Lukas Striebel");
		l2 = new JLabel("Chemon 1.6");
		l3 = new JLabel("Partieverlauf");
		l4 = new JLabel("Stellungsbewertung");
		l5 = new JLabel("Elapsed Time:");
		l6 = new JLabel("Elapsed Time total:");
		l1.setForeground(Color.WHITE);
		l2.setForeground(Color.WHITE);
		l3.setForeground(Color.WHITE);
		l4.setForeground(Color.WHITE);
		l5.setForeground(Color.WHITE);
		l6.setForeground(Color.WHITE);
		l1.setBounds(340, (int) 9.25 * squareLength, 200, 25);
		l2.setBounds(4 * squareLength, 10, 100, 25);
		l3.setBounds(squareLength * 8 + 120, squareLength, 200, squareLength / 4);
		l4.setBounds(squareLength * 10 + 120, squareLength, 200, squareLength / 4);
		l5.setBounds(squareLength * 8 + 120, 20, 120, 25);
		l6.setBounds(squareLength * 8 + 120, 50, 170, 25);
		t1 = new JTextArea();
		t2 = new JTextArea();
		t1.setFocusable(false);
		t2.setFocusable(false);
		scrollBar1 = new JScrollPane(t1);
		scrollBar1.setBounds(squareLength * 8 + 120, (int) (1.5 * squareLength), (int) (1.5 * squareLength),
				6 * squareLength);
		scrollBar2 = new JScrollPane(t2);
		scrollBar2.setBounds(squareLength * 10 + 120, (int) (1.5 * squareLength), squareLength, 6 * squareLength);
		frame.getContentPane().add(scrollBar1);
		frame.getContentPane().add(scrollBar2);
		frame.getContentPane().add(l1, BorderLayout.CENTER);
		frame.getContentPane().add(l2, BorderLayout.CENTER);
		frame.getContentPane().add(l3, BorderLayout.CENTER);
		frame.getContentPane().add(l4, BorderLayout.CENTER);
		frame.getContentPane().add(l5, BorderLayout.CENTER);
		frame.getContentPane().add(l6, BorderLayout.CENTER);
		l7 = new JLabel("");
		frame.add(l7);
		try {
			path = new File("").getCanonicalPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for (int i = 0; i < 26; i++)
			try {
				String file = path + "\\GIF\\" + gifPath[i] + ".gif";
				pictures[i] = ImageIO.read(new File(file));
			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		System.out.println(path);
		frame.setVisible(true);
		drawBoard(frame.getGraphics());
		drawBackground();
	}

	public int mouseToBoard(int pH, int pV) {
		int lH, lV;
		lH = -1;
		lV = 0;
		if (pH > 100 && pH < 100 + 1 * squareLength)
			lH = 1;
		if (pH > 100 + 1 * squareLength && pH < 100 + 2 * squareLength)
			lH = 2;
		if (pH > 100 + 2 * squareLength && pH < 100 + 3 * squareLength)
			lH = 3;
		if (pH > 100 + 3 * squareLength && pH < 100 + 4 * squareLength)
			lH = 4;
		if (pH > 100 + 4 * squareLength && pH < 100 + 5 * squareLength)
			lH = 5;
		if (pH > 100 + 5 * squareLength && pH < 100 + 6 * squareLength)
			lH = 6;
		if (pH > 100 + 6 * squareLength && pH < 100 + 7 * squareLength)
			lH = 7;
		if (pH > 100 + 7 * squareLength && pH < 100 + 8 * squareLength)
			lH = 8;
		if (pV > 100 && pV < 100 + 1 * squareLength)
			lV = 90;
		if (pV > 100 + 1 * squareLength && pV < 100 + 2 * squareLength)
			lV = 80;
		if (pV > 100 + 2 * squareLength && pV < 100 + 3 * squareLength)
			lV = 70;
		if (pV > 100 + 3 * squareLength && pV < 100 + 4 * squareLength)
			lV = 60;
		if (pV > 100 + 4 * squareLength && pV < 100 + 5 * squareLength)
			lV = 50;
		if (pV > 100 + 5 * squareLength && pV < 100 + 6 * squareLength)
			lV = 40;
		if (pV > 100 + 6 * squareLength && pV < 100 + 7 * squareLength)
			lV = 30;
		if (pV > 100 + 7 * squareLength && pV < 100 + 8 * squareLength)
			lV = 20;
		return lH + lV;
	}

	public void drawPieces(Graphics g) {
		for (int i = 21; i < 99; i++) {
			drawPiece(controller.position.Board[i], i, g);
		}
	}

	public int fieldToHorizontalPosition(int field) {
		int column = field % 10;
		if(column > 8)
			return -1;
		return HORIZONTAL_OFFSET +  (column - 1) * squareLength;

	}

	public int fieldToVerticalPosition(int field) {
		if (90 < field && field < 100)
			return 100;
		else if (80 < field && field < 90)
			return 100 + 1 * squareLength;
		else if (70 < field && field < 80)
			return 100 + 2 * squareLength;
		else if (60 < field && field < 70)
			return 100 + 3 * squareLength;
		else if (50 < field && field < 60)
			return 100 + 4 * squareLength;
		else if (40 < field && field < 50)
			return 100 + 5 * squareLength;
		else if (30 < field && field < 40)
			return 100 + 6 * squareLength;
		else if (20 < field && field < 30)
			return 100 + 7 * squareLength;
		else
			return -1;
	}

	private void drawPiece(int piece, int field, Graphics graphics) {// eine Figur
																// wird
																// gezeichnet
		int x = fieldToHorizontalPosition(field), y = fieldToVerticalPosition(field);
		if (piece == 20 && !boardColors[field - 21])
			graphics.drawImage(pictures[2], x, y, squareLength, squareLength, null);
		if (piece == 20 && boardColors[field - 21])
			graphics.drawImage(pictures[3], x, y, squareLength, squareLength, null);
		if (piece == 21 && !boardColors[field - 21])
			graphics.drawImage(pictures[4], x, y, squareLength, squareLength, null);
		if (piece == 21 && boardColors[field - 21])
			graphics.drawImage(pictures[5], x, y, squareLength, squareLength, null);
		if (piece == 22 && !boardColors[field - 21])
			graphics.drawImage(pictures[6], x, y, squareLength, squareLength, null);
		if (piece == 22 && boardColors[field - 21])
			graphics.drawImage(pictures[7], x, y, squareLength, squareLength, null);
		if (piece == 23 && !boardColors[field - 21])
			graphics.drawImage(pictures[8], x, y, squareLength, squareLength, null);
		if (piece == 23 && boardColors[field - 21])
			graphics.drawImage(pictures[9], x, y, squareLength, squareLength, null);
		if (piece == 24 && !boardColors[field - 21])
			graphics.drawImage(pictures[10], x, y, squareLength, squareLength, null);
		if (piece == 24 && boardColors[field - 21])
			graphics.drawImage(pictures[11], x, y, squareLength, squareLength, null);
		if (piece == 25 && !boardColors[field - 21])
			graphics.drawImage(pictures[12], x, y, squareLength, squareLength, null);
		if (piece == 25 && boardColors[field - 21])
			graphics.drawImage(pictures[13], x, y, squareLength, squareLength, null);
		if (piece == 10 && !boardColors[field - 21])
			graphics.drawImage(pictures[14], x, y, squareLength, squareLength, null);
		if (piece == 10 && boardColors[field - 21])
			graphics.drawImage(pictures[15], x, y, squareLength, squareLength, null);
		if (piece == 11 && !boardColors[field - 21])
			graphics.drawImage(pictures[16], x, y, squareLength, squareLength, null);
		if (piece == 11 && boardColors[field - 21])
			graphics.drawImage(pictures[17], x, y, squareLength, squareLength, null);
		if (piece == 12 && !boardColors[field - 21])
			graphics.drawImage(pictures[18], x, y, squareLength, squareLength, null);
		if (piece == 12 && boardColors[field - 21])
			graphics.drawImage(pictures[19], x, y, squareLength, squareLength, null);
		if (piece == 13 && !boardColors[field - 21])
			graphics.drawImage(pictures[20], x, y, squareLength, squareLength, null);
		if (piece == 13 && boardColors[field - 21])
			graphics.drawImage(pictures[21], x, y, squareLength, squareLength, null);
		if (piece == 14 && !boardColors[field - 21])
			graphics.drawImage(pictures[22], x, y, squareLength, squareLength, null);
		if (piece == 14 && boardColors[field - 21])
			graphics.drawImage(pictures[23], x, y, squareLength, squareLength, null);
		if (piece == 15 && !boardColors[field - 21])
			graphics.drawImage(pictures[24], x, y, squareLength, squareLength, null);
		if (piece == 15 && boardColors[field - 21])
			graphics.drawImage(pictures[25], x, y, squareLength, squareLength, null);
		if (piece == 0)
			drawSquare(field, graphics);
	}

	private void drawBoard(Graphics g) {// das ganze Brett wird gezeichnet
		for (int i = 21; i < 99; i++) {
			drawSquare(i, g);
		}
	}

	private void drawSquare(int square, Graphics g) {// ein Feld wird gezeichnet
		int x = fieldToHorizontalPosition(square), y = fieldToVerticalPosition(square);
		if (x != -1 && y != -1) {
			if (boardColors[square - 21])
				g.drawImage(pictures[1], x, y, squareLength, squareLength, null);
			else
				g.drawImage(pictures[0], x, y, squareLength, squareLength, null);
		}
	}

	public void displayMove(Move m) {// der Zug wird grafisch dargestellt
		drawSquare(m.from, frame.getGraphics());
		if (m.addition == 0)
			drawPiece(m.piece, m.to, frame.getGraphics());
		else
			switch (m.addition) {
			// Weiss
			case 1: // en passant schlagen
				drawSquare(m.to - 10, frame.getGraphics());
				drawPiece(m.piece, m.to, frame.getGraphics());
				break;
			case 2: // Bauernumwandlung
				drawPiece(controller.position.Board[m.to], m.to, frame.getGraphics());
				break;
			case 3: // kleine Rochade
				drawSquare(28, frame.getGraphics());
				drawPiece(11, 26, frame.getGraphics());
				drawPiece(15, 27, frame.getGraphics());
				break;
			case 4: // grosseRochade
				drawSquare(21, frame.getGraphics());
				drawPiece(11, 24, frame.getGraphics());
				drawPiece(15, 23, frame.getGraphics());
				break;
			// Schwarz
			case -1: // en passant schlagen
				drawSquare(m.to + 10, frame.getGraphics());
				drawPiece(m.piece, m.to, frame.getGraphics());
				break;
			case -2: // Bauernumwandlung
				drawPiece(controller.position.Board[m.to], m.to, frame.getGraphics());
				break;
			case -3: // kleine Rochade
				drawSquare(98, frame.getGraphics());
				drawPiece(21, 96, frame.getGraphics());
				drawPiece(25, 97, frame.getGraphics());
				break;
			case -4: // grosseRochade
				drawSquare(91, frame.getGraphics());
				drawPiece(21, 94, frame.getGraphics());
				drawPiece(25, 93, frame.getGraphics());
				break;
			}
		movesound();
	}

	/**
	 * ein Feld wird markiert
	 * 
	 * @param pos
	 *            dieses Feld wird markeirt
	 * @param colour
	 *            mit dieser Farbe wird es markiert
	 */
	public void mark(int pos, Color colour) {
		int x = fieldToHorizontalPosition(pos), y = fieldToVerticalPosition(pos);
		Graphics g = frame.getGraphics();
		g.setColor(colour);
		g.drawRect(x, y, squareLength, squareLength);
		g.drawRect(x + 1, y + 1, squareLength - 2, squareLength - 2);
		g.drawRect(x + 2, y + 2, squareLength - 4, squareLength - 4);
		g.drawRect(x + 3, y + 3, squareLength - 6, squareLength - 6);
	}

	/**
	 * ein Feld wird demarkiert
	 * 
	 * @param pos
	 *            dieses Feld wird demarkeirt
	 */
	public void unmark(int pos) {
		drawPiece(controller.position.Board[pos], pos, frame.getGraphics());
		if (30 < pos)
			drawPiece(controller.position.Board[pos - 10], pos - 10, frame.getGraphics());
		if (controller.position.Board[pos + 1] > -1)
			drawPiece(controller.position.Board[pos + 1], pos + 1, frame.getGraphics());
	}

	/**
	 * das Brett wird neu gezeichnet
	 */
	public void refresh() {
		drawBackground();
		drawBoard(frame.getGraphics());
		drawPieces(frame.getGraphics());
	}

	private void initializeMenu() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("Datei");
		gameMenu = new JMenu("Partie");
		viewMenu = new JMenu("Ansicht");
		offerDrawItem = new JMenuItem("Remis anbieten");
		resignItem = new JMenuItem("Aufgeben");
		moveBackItem = new JMenuItem("Zug zurück");
		exitItem = new JMenuItem("Beenden");
		newGameItem = new JMenuItem("Neue Partie");
		saveGameItem = new JMenuItem("Partie speichern");
		openGameItem = new JMenuItem("Partie öffnen");
		refreshItem = new JMenuItem("Aktualisieren");
		optionsItem = new JMenuItem("Einstellungen");
		int km = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
		saveGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, km));
		openGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, km));
		moveBackItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, km));
		exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, km));
		refreshItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, km));
		newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, km));
		optionsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, km));
		newGameItem.addActionListener(panel);
		openGameItem.addActionListener(panel);
		exitItem.addActionListener(panel);
		saveGameItem.addActionListener(panel);
		offerDrawItem.addActionListener(panel);
		resignItem.addActionListener(panel);
		moveBackItem.addActionListener(panel);
		refreshItem.addActionListener(panel);
		optionsItem.addActionListener(panel);
		fileMenu.add(newGameItem);
		fileMenu.add(saveGameItem);
		fileMenu.add(openGameItem);
		fileMenu.add(optionsItem);
		fileMenu.add(exitItem);
		gameMenu.add(offerDrawItem);
		gameMenu.add(resignItem);
		gameMenu.add(moveBackItem);
		viewMenu.add(refreshItem);
		menuBar.add(fileMenu);
		menuBar.add(gameMenu);
		menuBar.add(viewMenu);
	}

	public void setColors() {
		boardColors = new boolean[80];
		for(int i = 1; i < boardColors.length; i += 2) {
			if(i % 10 == 0 || i % 10 == 9)
				i++;
			boardColors[i] = true;
		}
	}

	public void openOptions() {
		options.setBounds(5 * squareLength, 4 * squareLength, 3 * squareLength, 3 * squareLength);
		options.getContentPane().setBackground(Color.WHITE);
		options.setVisible(true);
		options.setResizable(false);
		hvh.setBounds(20, 20, 150, 50);
		hvm.setBounds(20, 70, 150, 50);
		mvm.setBounds(20, 120, 150, 50);
		ok.setBounds(50, 170, 100, 30);
		options.getContentPane().add(hvh);
		options.getContentPane().add(hvm);
		options.getContentPane().add(mvm);
		options.getContentPane().add(ok);
		options.add(l7);
		hvm.setSelected(true);
		hvh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hvh.isSelected() || mvm.isSelected()) {
					hvm.setSelected(false);
					mvm.setSelected(false);
				} else
					hvh.setSelected(true);
				controller.status = State.Human_versus_Human;
			}
		});
		hvm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hvm.isSelected() || mvm.isSelected()) {
					hvh.setSelected(false);
					mvm.setSelected(false);
				} else
					hvm.setSelected(true);
				controller.status = State.Human_versus_Machine;
			}
		});
		mvm.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (hvm.isSelected() || hvh.isSelected()) {
					hvh.setSelected(false);
					hvm.setSelected(false);
				} else
					mvm.setSelected(true);
				controller.status = State.Machine_versus_Machine;
			}
		});
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				options.dispose();
				refresh();
			}
		});
	}

	public void drawBackground() {
		Graphics graphics = frame.getGraphics();
		graphics.setColor(new Color(128, 64, 0));
		graphics.fillRect(80, 80, 40 + 8 * squareLength, 40 + 8 * squareLength);
	}

	public void movesound() { // spielt einen Ton ab um den Spieler über einen Move zu informieren
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path + "\\MOVE.wav"));
			BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream);
			AudioFormat af = audioInputStream.getFormat();
			int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength());
			byte[] audio = new byte[size];
			DataLine.Info info = new DataLine.Info(Clip.class, af, size);
			bufferedInputStream.read(audio, 0, size);
			Clip clip = (Clip) AudioSystem.getLine(info);
			clip.open(af, audio, 0, size);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
