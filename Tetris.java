import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Tetris extends JFrame implements ActionListener, ItemListener,
		KeyListener {

	public static int r = 28, c = 11, s = 15, lw = 90;
	public static Color bg = Color.BLACK, zcolor = Color.RED,
			tcolor = Color.GREEN, lcolor = Color.BLUE, kcolor = Color.PINK,
			scolor = Color.CYAN;
	public static int[] lremoved;
	public static int score = 0, level = 0, pause = -1, gover = 0;
	public static Timer task;
	JMenuBar mb;
	JMenu start, edit, help;
	JMenuItem ng, hs, pauseGame, resumeGame, ex, ctrl, hlp, about;
	public GameField gf;
	public Gdetails gd;
	Piece currPiece, nextPiece;

	public void init() {
		// data initialization goes here
		setSize(lw + c * s + 5, (r+1) * s + 5); // 5 for margin
		setLayout(null);
		lremoved = new int[5];
		Arrays.fill(lremoved, 0);
		
		// making the menu
		initializeMenu();

		currPiece = new Piece();
		nextPiece = new Piece();
				
		// Panel for displaying the game (Main Panel)
		initializeGameField();
		// Panel for displaying the Details of the Game
		initializeGameDetails();
		
		/*
		 * //Window Listener for handling window closing event
		 * addWindowListener( new WindowAdapter() { public void
		 * windowClosing(WindowEvent e) { System.exit(0); } } );
		 */
		setVisible(true);
		gf.requestFocus();
	}
	
	public void initializeGameDetails() {
		gd = new Gdetails(nextPiece);
		add(gd);
		gd.setBounds(0, 0, lw, r * s);
	}
	
	public void initializeGameField() {
		gf = new GameField(currPiece);
		add(gf);
		gf.setBounds(lw, 0, c * s, r * s);
		gf.addKeyListener(this);
		// Mouse Listener for focusing
		addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				gf.requestFocus();
				System.out.println("Focus!");
			}
		});
	}
	
	public void initializeMenu() {
		mb = new JMenuBar();
		setJMenuBar(mb);
		start = new JMenu("Start");
		edit = new JMenu("Edit");
		help = new JMenu("Help");
		mb.add(start);
		mb.add(help);

		ng = new JMenuItem("New Game");
		pauseGame = new JMenuItem("Pause");
		resumeGame = new JMenuItem("Resume");
		hs = new JMenuItem("Highest Score");
		ex = new JMenuItem("Exit");
		ctrl = new JMenuItem("Controls");
		hlp = new JMenuItem("Help");
		about = new JMenuItem("About");
		
		start.add(ng);
		start.add(pauseGame);
		start.add(resumeGame);
		start.add(ex);
		resumeGame.setEnabled(false);
		pauseGame.setEnabled(false);
		
		edit.add(ctrl);
		help.add(about);
		ex.addActionListener(this);
		ng.addActionListener(this);
		resumeGame.addActionListener(this);
		pauseGame.addActionListener(this);
		start.addActionListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g);
	}

	public void actionPerformed(ActionEvent ae) {
		String s;
		s = ae.getActionCommand();
		System.out.println(s);
		if (s.equals("Exit"))
			System.exit(0);
		else if (ae.getSource() == ng) {
			startGame();
			myPaint();
		} else if(s.equals("Pause")) {
			pause();
		} else if(s.equals("Resume")) {
			unPause();
		}
	}

	public void itemStateChanged(ItemEvent ie) {
		System.out.println(ie.paramString());
		if (ie.getItem() == ng) {
			startGame();
		}
		myPaint();
	}

	/*
	 * ===================================================
	 * 
	 * GameField Manipulation functions erase,copy,myPaint,oneDown
	 * 
	 * ====================================================
	 */

	public void erase() {
		for (int i = GameField.ptr - 2; i <= GameField.ptr + 2; i++)
			for (int j = GameField.ptc - 2; j <= GameField.ptc + 2; j++)
				if (i >= 0 && i < Tetris.r && j >= 0 && j < Tetris.c) {
					if (gf.p.arr[i - GameField.ptr + 2][j - GameField.ptc + 2].c != Tetris.bg)
						gf.arena[i][j].c = Tetris.bg;
				}

	}

	public void copy() {
		for (int i = Tetris.r - 1; i < Tetris.r; i++)
			for (int j = 0; j < Tetris.c; j++)
				gf.arena[i][j].c = Color.WHITE;
		for (int i = Tetris.r - 2; i < Tetris.r; i++)
			for (int j = 0; j < Tetris.c; j++)
				gf.arena[i][j].c = Color.GRAY;
		for (int i = GameField.ptr - 2; i <= GameField.ptr + 2; i++)
			for (int j = GameField.ptc - 2; j <= GameField.ptc + 2; j++)
				if (i >= 0 && i < Tetris.r && j >= 0 && j < Tetris.c) {
					if (gf.p.arr[i - GameField.ptr + 2][j - GameField.ptc + 2].c != Tetris.bg) {
						gf.arena[i][j].c = GameField.p.arr[i - GameField.ptr
								+ 2][j - GameField.ptc + 2].c;
						gf.arena[Tetris.r - 1][j].c = Color.DARK_GRAY;
					}
				}

	}

	public void myPaint() {
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++)
				gd.array[i][j].repaint();

		for (int i = 0; i < Tetris.r; i++)
			for (int j = 0; j < Tetris.c; j++)
				if (gf.parena[i][j] != gf.arena[i][j].c)
					gf.arena[i][j].repaint();

	}

	public void sidePaint() {
		gd.t1.setText(Integer.toString(Tetris.level));
		gd.t2.setText(Integer.toString(Tetris.score));

	}

	public boolean oneDown() {
		erase();
		GameField.ptr++;
		if (!gf.checkBounds()) {
			GameField.ptr--;
			copy();
			return false;
		}
		copy();
		return true;

	}

	public void stop() {
		for (int i = 0; i < Tetris.r; i++)
			for (int j = 0; j < Tetris.c; j++)
				if (gf.parena[i][j] != gf.arena[i][j].c)
					gf.arena[i][j].c = bg;
		task.cancel();
	}

	public void startGame() {
		if (task != null) {
			task.cancel();
			erase();
		}
		gover = 1; // for gameover
		pause++;
		level = 0;
		score = 0;
		pauseGame.setEnabled(true);
		resumeGame.setEnabled(false);
		Arrays.fill(lremoved, 0);		
		gf.startGame(currPiece);
		gd.sendNextPiece(nextPiece);
		
		if (pause % 2 != 0)
			pause++;
		task = new Timer(true);
		task.scheduleAtFixedRate(new MyTimer(this), 500, 400 - level * 50);
		gf.requestFocus();
	}

	public void pause() {
		if (pause % 2 == 0) {
			pause++;
			task.cancel();
			pauseGame.setEnabled(false);
			resumeGame.setEnabled(true);
		}
	}

	public void unPause() {
		if (pause % 2 == 1 && gover == 1) {
			pause++;
			pauseGame.setEnabled(true);
			resumeGame.setEnabled(false);
			task = new Timer(true);
			task.scheduleAtFixedRate(new MyTimer(this), 500,
					400 - Tetris.level * 50);
		}
	}

	public void gameOver() {
		gover = 0;
		pause();
		System.out.println("game over");
	}

	public void updateScore() {
		if (level != 0)
			score = (100 * lremoved[1] + 200 * lremoved[2] * level + 400
					* lremoved[3] * level * level + 800 * lremoved[4] * level
					* level * level);
		else
			score = ((100 * lremoved[1] + 200 * lremoved[2] + 400 * lremoved[3] + 800 * lremoved[4]) / 2);

	}

	public void updateLevel() {
		System.out.println(lremoved[1] + lremoved[2] + lremoved[3]
				+ lremoved[4]);
		if ((lremoved[1] + lremoved[2] + lremoved[3] + lremoved[4]) % 10 == 0) {

			level = Math
					.min(
							(lremoved[1] + lremoved[2] + lremoved[3] + lremoved[4]) / 10,
							7);
			task.cancel();
			task = new Timer(true);
			task.scheduleAtFixedRate(new MyTimer(this), 500, 400 - level * 50);
		}
	}
	
	public void timerExpired() {
		gf.backup();
		erase();
		if (!oneDown()) {
			if (GameField.ptr == 2 && GameField.ptc == 5)
				gameOver();
			else {
				System.out.println(GameField.ptr + " " + GameField.ptc);
				int i, j, ct = 0;
				for (i = 0; i < r - 2; i++) {
					for (j = 0; j < c; j++) {
						if (gf.arena[i][j].c == bg)
							break;
					}
					if (j == c) {
						ct++;
						for (int k = i - 1; k >= 0; k--)
							for (j = 0; j < c; j++)
								gf.arena[k + 1][j].c = gf.arena[k][j].c;

					}
				}
				lremoved[ct]++;
				updateScore();
				updateLevel();
				sidePaint();
				currPiece = nextPiece;
				nextPiece = new Piece();
				gf.initializeNewPiece(currPiece);
				gd.sendNextPiece(nextPiece);
			}
		}
		copy();
		myPaint();
		
	}

	/*
	 * ========================================================
	 * 
	 * End of GameField Manipulation functions erase,copy,myPaint,oneDown
	 * 
	 * =========================================================
	 */
	public void run() {
	}

	/*
	 * =======================================================
	 * 
	 * Key Board Input Handlers
	 * 
	 * =========================================================
	 */
	public void keyPressed(KeyEvent e) {
		System.out.println("in keypressed");
		gf.backup();
		erase();
		switch (e.getKeyCode()) {

		case KeyEvent.VK_UP:
			if (pause % 2 == 0) {
				GameField.p.rotateclock();
				if (!gf.checkBounds())
					GameField.p.rotateanti();
			}
			break;
		case KeyEvent.VK_LEFT:
			if (pause % 2 == 0) {
				GameField.ptc--;
				if (!gf.checkBounds())
					GameField.ptc++;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (pause % 2 == 0) {
				GameField.ptc++;
				if (!gf.checkBounds())
					GameField.ptc--;
			}
			break;
		case KeyEvent.VK_DOWN:
			if (pause % 2 == 0)
				for (int i = 1; oneDown(); i++)
					;
			break;
		case KeyEvent.VK_P:
			pause();
			break;
		case KeyEvent.VK_U:
			unPause();
			break;
		case KeyEvent.VK_N:
			startGame();
			myPaint();
			break;

		}
		copy();
		myPaint();
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	/*
	 * ======================================================= End of Key Board
	 * Input Handlers =========================================================
	 */

	public static void main(String[] args) {
		Tetris t = new Tetris();
		// use gf.p and gf.ptr,ptc to catch the game piece

		// GameField.p.rotateclock();
		// p.rotateclock();

	}
}

class MyTimer extends TimerTask {

	Tetris tetris;
	
	MyTimer(Tetris t) {
		super();
		tetris = t;
	}

	public void run() {
		tetris.timerExpired();
		
	}
}

class MyPanel extends Panel {
	public Color c;

	MyPanel() {
		c = Tetris.bg;
	}

	public void paint(Graphics g) {
		if (c != Tetris.bg) {
			g.setColor(Color.GRAY);
			g.fillRect(0, 0, Tetris.s, Tetris.s);
			g.setColor(c);
			g.fillRect(1, 1, Tetris.s - 2, Tetris.s - 2);
		} else {
			g.setColor(Tetris.bg);
			g.fillRect(0, 0, Tetris.s, Tetris.s);
		}
	}
}

class GameField extends Panel {
	public static int r, c, s, lw;
	public MyPanel[][] arena;
	public Color[][] parena;
	public static Piece p;
	public static int ptr, ptc;

	GameField(Piece currPiece) {
		r = Tetris.r;
		c = Tetris.c;
		s = Tetris.s;
		lw = Tetris.lw;
		p = currPiece;
		ptr = 2;
		ptc = 5;
		
		Tetris.score = Tetris.level = 0;
		Arrays.fill(Tetris.lremoved, 0);
		setLayout(new GridLayout(Tetris.r, Tetris.c));
		arena = new MyPanel[Tetris.r][Tetris.c];
		parena = new Color[Tetris.r][Tetris.c];
		for (int i = 0; i < Tetris.r; i++)
			for (int j = 0; j < Tetris.c; j++) {
				arena[i][j] = new MyPanel();
				parena[i][j] = new Color(0);
				add(arena[i][j]);
			}

		/*
		 * =================================== to be removed
		 * =====================================
		 */
		for (int i = Tetris.r - 1; i < Tetris.r; i++)
			for (int j = 0; j < Tetris.c; j++)
				arena[i][j].c = Color.WHITE;
		/*
		 * ================================== to be removed
		 * ====================================
		 */

	}
	
	void startGame(Piece currPiece) {
		p=currPiece;

		for (int i = 0; i < Tetris.r; i++)
			for (int j = 0; j < Tetris.c; j++)
				arena[i][j].c = Tetris.bg;
		ptr = 2;
		ptc = 5;
		//System.out.println("level= " + level);

		for (int i = ptr - 2; i <= ptr + 2; i++)
			for (int j = ptc - 2; j <= ptc + 2; j++)
				if (p.arr[i - ptr + 2][j - ptc + 2].c != Tetris.bg)
					arena[i][j].c = p.arr[i - ptr + 2][j
							- ptc + 2].c;

	}
	
	void initializeNewPiece(Piece newPiece) {
		p = newPiece;
		ptr = 2;
		ptc = 5;
	}

	void backup() {
		for (int i = 0; i < Tetris.r; i++)
			for (int j = 0; j < Tetris.c; j++) {
				parena[i][j] = arena[i][j].c;
			}
	}

	boolean checkBounds() {
		for (int i = ptr - 2; i <= ptr + 2; i++)
			for (int j = ptc - 2; j <= ptc + 2; j++) {
				if (p.arr[i - ptr + 2][j - ptc + 2].c != Tetris.bg) // not
																	// BackGround
																	// color
				{
					if (i < 0 || i >= Tetris.r - 2 || j < 0 || j >= Tetris.c)
						return false;
					else if (arena[i][j].c != Tetris.bg)
						return false;

				}
			}
		return true;
	}
	
}

class Piece {
	public MyPanel[][] arr = new MyPanel[5][5];
	public int type;
	public static Random ran = new Random();
	

	Piece() {
		type = ran.nextInt(7);
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) {
				arr[i][j] = new MyPanel();
				arr[i][j].c = Tetris.bg;
			}
		switch (type) {
		case 0: // z
			arr[2][2].c = arr[2][1].c = arr[3][2].c = arr[3][3].c = Tetris.zcolor;
			break;
		case 1: // inverted z
			arr[2][2].c = arr[2][3].c = arr[3][2].c = arr[3][1].c = Tetris.zcolor;
			break;
		case 2: // T
			arr[2][2].c = arr[2][1].c = arr[2][3].c = arr[3][2].c = Tetris.tcolor;
			break;
		case 3: // L
			arr[2][2].c = arr[1][2].c = arr[3][2].c = arr[3][3].c = Tetris.lcolor;
			break;
		case 4: // Inverted L
			arr[2][2].c = arr[1][2].c = arr[3][2].c = arr[3][1].c = Tetris.lcolor;
			break;
		case 5: // kaddi
			arr[0][2].c = arr[1][2].c = arr[2][2].c = arr[3][2].c = Tetris.kcolor;
			break;
		case 6: // Square
			arr[2][2].c = arr[1][1].c = arr[1][2].c = arr[2][1].c = Tetris.scolor;
			break;
		case 7: // +
			arr[2][2].c = arr[2][1].c = arr[2][3].c = arr[3][2].c = arr[1][2].c = Color.GREEN;
			break;
		}
	}

	void rotateclock() {
		MyPanel[][] t = new MyPanel[5][5];
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) {
				t[i][j] = new MyPanel();
				t[i][j].c = arr[i][j].c;
			}
		if (type != 6 && type != 7) {
			if (type == 5 && arr[0][2].c != Tetris.bg) {
				rotateanti();
				return;
			}
			if (type == 0 && arr[2][1].c != Tetris.bg) {
				rotateanti();
				return;
			}
			if (type == 1 && arr[1][1].c != Tetris.bg) {
				rotateanti();
				return;
			}
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++) {
					arr[j][4 - i].c = t[i][j].c;
				}
		}
	}

	void rotateanti() {
		MyPanel[][] t = new MyPanel[5][5];
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) {
				t[i][j] = new MyPanel();
				t[i][j].c = arr[i][j].c;
			}
		if (type != 7 && type != 6) {
			if (type == 5 && arr[2][0].c != Tetris.bg) {
				rotateclock();
				return;
			}
			if (type == 0 && arr[1][3].c != Tetris.bg) {
				rotateclock();
				return;
			}
			if (type == 1 && arr[2][3].c != Tetris.bg) {
				rotateclock();
				return;
			}
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++) {
					arr[4 - j][i].c = t[i][j].c;
				}

		}
	}
}

class Gdetails extends Panel {
	public static int r, c, s, lw;
	public TextField t1, t2;
	public Label level, score;
	public Panel np;
	public Color[][] pbuf;
	public MyPanel[][] array;
	public Piece nextPiece;
	public static final Color BACKGROUND = Color.gray;
	public static final Font TEXTFONT = new Font(Font.SANS_SERIF, Font.BOLD, 14);
	public static final Font LABELFONT = new Font(Font.DIALOG_INPUT, Font.ITALIC, 12);

	Gdetails(Piece nextPiece) {
		r = Tetris.r;
		c = Tetris.c;
		s = Tetris.s;
		lw = Tetris.lw;
		this.nextPiece = nextPiece;
		setLayout(null);
		level = new Label("Level");
		level.setBackground(BACKGROUND);
		level.setFont(LABELFONT);
		add(level);
		level.setBounds(8, 75, 75, 20);
		t1 = new TextField("0");
		t1.setEditable(false);
		add(t1);
		t1.setBounds(8, 100, 75, 20);
		t1.setFont(TEXTFONT);
		score = new Label("Score");
		score.setBackground(BACKGROUND);
		score.setFont(LABELFONT);
		add(score);
		score.setBounds(8, 150, 75, 20);
		t2 = new TextField("0");
		t2.setEditable(false);
		t2.setFont(TEXTFONT);
		add(t2);
		t2.setBounds(8, 175, 75, 20);
		np = new Panel();
		np.setLayout(new GridLayout(5, 5));
		array = new MyPanel[5][5];
		pbuf = new Color[5][5];
		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) {
				array[i][j] = new MyPanel();
				np.add(array[i][j]);
				pbuf[i][j] = Tetris.bg;

			}
		add(np);
		np.setBounds(8, 250, 75, 75);
		setVisible(true);
	}

	public void sendNextPiece(Piece nextP) {
		nextPiece = nextP;

		for (int i = 0; i < 5; i++)
			for (int j = 0; j < 5; j++) {
				if (pbuf[i][j] != nextPiece.arr[i][j].c) {
					array[i][j].c = nextPiece.arr[i][j].c;
					pbuf[i][j] = nextPiece.arr[i][j].c;
				}
			}

	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D x = (Graphics2D) g;
		x.setColor(BACKGROUND);
		x.fillRect(0, 0, lw, 50 + r * s);
	}

}
