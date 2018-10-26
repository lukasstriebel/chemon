package MA.Chemon;

import java.awt.*;
import java.awt.event.*;
import java.io.*;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import javax.swing.*;

import MA.util.State;

public class Interface {

	private final int HORIZONTAL_OFFSET = 40, VERTICAL_OFFSET = 40;
	private final int SQUARE_LENGTH = 100;
	String[] gifPath = { "b", "w", "wpb", "wpw", "wrb", "wrw", "wnb", "wnw", "wbb", "wbw", "wqb", "wqw", "wkb", "wkw",
			 "bpb", "bpw", "brb", "brw", "bnb", "bnw", "bbb", "bbw", "bqb", "bqw", "bkb", "bkw" };
	Image[] pictures = new Image[26];
	boolean[] boardColors;
	String path;

	Controller controller;
	JFrame frame, options = new JFrame("Options");
	JLabel l1, l2, l3, l4, l5, l6, l7;
	JRadioButton hvh = new JRadioButton("Human vs. Human"), hvm = new JRadioButton("Human vs. Machine"),
			mvm = new JRadioButton("Machine vs. Machine");
	JButton ok = new JButton("Okay");
	JTextArea movesTextArea, evaluationTextArea;
	JScrollPane scrollBar1, scrollBar2;
	JMenuBar menuBar;
	JMenu fileMenu, gameMenu, viewMenu;
	JMenuItem newGameItem, moveBackItem, exitItem, saveGameItem, openGameItem, resignItem, offerDrawItem, refreshItem,
			optionsItem;
	MyJPanel panel = new MyJPanel();

	class MyJPanel extends JPanel implements ActionListener {
		/**
		 *
		 */
		private static final long serialVersionUID = -7710107088317624786L;

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == newGameItem) {
				controller.newGame();
			} else if (e.getSource() == moveBackItem)
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
		}

		public void paintComponent(Graphics graphics) {
			super.paintComponent(graphics);
			l1.repaint();
			l2.repaint();
			l3.repaint();
			l4.repaint();
			l5.repaint();
			l6.repaint();
			drawBoard(graphics);
		}
	}

	public Interface() {

		setColors();
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame = new JFrame("Chemon");
		frame.setSize(dim);
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		initializeMenu();
		frame.setJMenuBar(menuBar);
		frame.setResizable(false);
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				controller.handleMousePressed(mouseClickToField(e.getLocationOnScreen().x, e.getLocationOnScreen().y));
			}
		});
		l1 = new JLabel("� 2010 - 2018 Lukas Striebel");
		l2 = new JLabel("");
		l3 = new JLabel("Partieverlauf");
		l4 = new JLabel("Stellungsbewertung");
		l5 = new JLabel("Elapsed Time:");
		l6 = new JLabel("Elapsed Time total:");
		l7 = new JLabel("");
		l1.setForeground(Color.BLACK);
		l2.setForeground(Color.BLACK);
		l3.setForeground(Color.BLACK);
		l4.setForeground(Color.BLACK);
		l5.setForeground(Color.BLACK);
		l6.setForeground(Color.BLACK);
		l1.setBounds(340, (int) 9.25 * SQUARE_LENGTH, 200, 25);
		l2.setBounds(4 * SQUARE_LENGTH, 10, 100, 25);
		l3.setBounds(SQUARE_LENGTH * 8 + 120, SQUARE_LENGTH, 200, SQUARE_LENGTH / 4);
		l4.setBounds(SQUARE_LENGTH * 10 + 120, SQUARE_LENGTH, 200, SQUARE_LENGTH / 4);
		l5.setBounds(SQUARE_LENGTH * 8 + 120, 20, 120, 25);
		l6.setBounds(SQUARE_LENGTH * 8 + 120, 50, 170, 25);
		movesTextArea = new JTextArea();
		evaluationTextArea = new JTextArea();
		movesTextArea.setFocusable(false);
		evaluationTextArea.setFocusable(false);
		scrollBar1 = new JScrollPane(movesTextArea);
		scrollBar1.setBounds(SQUARE_LENGTH * 8 + 120, (int) (1.5 * SQUARE_LENGTH), (int) (1.5 * SQUARE_LENGTH),
				6 * SQUARE_LENGTH);
		scrollBar2 = new JScrollPane(evaluationTextArea);
		scrollBar2.setBounds(SQUARE_LENGTH * 10 + 120, (int) (1.5 * SQUARE_LENGTH), SQUARE_LENGTH, 6 * SQUARE_LENGTH);
		frame.getContentPane().add(scrollBar1);
		frame.getContentPane().add(scrollBar2);
		frame.getContentPane().add(l1, BorderLayout.CENTER);
		frame.getContentPane().add(l2, BorderLayout.CENTER);
		frame.getContentPane().add(l3, BorderLayout.CENTER);
		frame.getContentPane().add(l4, BorderLayout.CENTER);
		frame.getContentPane().add(l5, BorderLayout.CENTER);
		frame.getContentPane().add(l6, BorderLayout.CENTER);
		frame.getContentPane().add(l7, BorderLayout.CENTER);
		frame.getContentPane().add(panel);

		try {
			path = new File("").getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < 26; i++) {
			try {
				String file = path + "\\resources\\" + gifPath[i] + ".gif";
				pictures[i] = ImageIO.read(new File(file));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		frame.setVisible(true);
		panel.setLocation(0, 0);
	}

	public int mouseClickToField(int horizontal, int vertical) {
		int row = (vertical - VERTICAL_OFFSET - 50) / SQUARE_LENGTH;
		int column = (horizontal - HORIZONTAL_OFFSET) / SQUARE_LENGTH;
		if(row > 8 || column > 8)
			return -1;
		return column+1 + (9 - row)*10;
	}

	public int fieldToHorizontalPosition(int field) {
		int column = field % 10;
		if(column > 8 || column < 1)
			return -1;
		return HORIZONTAL_OFFSET +  (column - 1) * SQUARE_LENGTH;

	}

	public int fieldToVerticalPosition(int field) {
		int row = field / 10;
		if(row > 10 || row < 2)
			return -1;
		return VERTICAL_OFFSET + (9 - row) * SQUARE_LENGTH;
	}

	public void drawBoard(Graphics graphics) {
		graphics.setColor(new Color(128, 64, 0));
		graphics.fillRect(HORIZONTAL_OFFSET-20, VERTICAL_OFFSET-20, 40 + 8 * SQUARE_LENGTH, 40 + 8 * SQUARE_LENGTH);
		for (int i = 21; i < 99; i++) {
			int piece = controller.position.board[i];
			if(piece == 0) {}
				drawSquare(i, graphics);
			} else if(piece > 0) {
				drawPiece(controller.position.board[i], i, graphics);
			}
		}
	}

	private void drawSquare(int square, Graphics graphics) {
		int x = fieldToHorizontalPosition(square), y = fieldToVerticalPosition(square);
		if (x != -1 && y != -1) {
			if (boardColors[square - 21]) {
				graphics.drawImage(pictures[1], x, y, SQUARE_LENGTH, SQUARE_LENGTH, null);
			} else {
				graphics.drawImage(pictures[0], x, y, SQUARE_LENGTH, SQUARE_LENGTH, null);
			}
		}
	}

	private void drawPiece(int piece, int field, Graphics graphics) {
		int x = fieldToHorizontalPosition(field), y = fieldToVerticalPosition(field);
		if(piece >= 20)
			piece -= 4;
		int blackSquare = boardColors[field - 21] ? 1 : 0;
		int imageIndex = piece * 2 - 18 + blackSquare;
		graphics.drawImage(pictures[imageIndex], x, y, SQUARE_LENGTH, SQUARE_LENGTH, null);

	}


	public void mark(int field, Color colour) {
		int offset = 48;
		int x = fieldToHorizontalPosition(field), y = fieldToVerticalPosition(field);
		Graphics graphics = frame.getGraphics();
		graphics.setColor(colour);
		graphics.drawRect(x, offset+y, SQUARE_LENGTH, SQUARE_LENGTH);
		graphics.drawRect(x + 1, y + 1 + offset, SQUARE_LENGTH - 2, SQUARE_LENGTH - 2);
		graphics.drawRect(x + 2, y + 2 + offset, SQUARE_LENGTH - 4, SQUARE_LENGTH - 4);
		graphics.drawRect(x + 3, y + 3 + offset, SQUARE_LENGTH - 6, SQUARE_LENGTH - 6);
	}

	private void initializeMenu() {
		menuBar = new JMenuBar();
		fileMenu = new JMenu("Datei");
		gameMenu = new JMenu("Partie");
		viewMenu = new JMenu("Ansicht");
		offerDrawItem = new JMenuItem("Remis anbieten");
		resignItem = new JMenuItem("Aufgeben");
		moveBackItem = new JMenuItem("Zug zur�ck");
		exitItem = new JMenuItem("Beenden");
		newGameItem = new JMenuItem("Neue Partie");
		saveGameItem = new JMenuItem("Partie speichern");
		openGameItem = new JMenuItem("Partie �ffnen");
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
		options.setBounds(5 * SQUARE_LENGTH, 4 * SQUARE_LENGTH, 3 * SQUARE_LENGTH, 3 * SQUARE_LENGTH);
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
			}
		});
	}

	public void movesound() { // spielt einen Ton ab um den Spieler �ber einen Move zu informieren
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(path + "\\resources\\MOVE.wav"));
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
