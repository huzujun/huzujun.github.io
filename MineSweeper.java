import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.*;
import java.util.Timer;

public class MineSweeper extends JFrame {
    //menu
    private JMenuBar jMenuBar = new JMenuBar();
    private JMenu jMenuGame, jMenuHelp;
    private JMenuItem newGame, about, help, exit;
    private int scores;
    //frame
    private JFrame f = new JFrame();

    private void displayScores(){
    }

    private class Cell {
        private JButton button;
        private Board board;
        private int value;
        private int id;
        private boolean notChecked;

        private Cell(Board board) {
            button = new JButton();
            button.addActionListener(e -> {
                checkCell();
                jLabelScores.setText(String.format("Scores: %d", scores));
                if (scores + 8 == board.width * board.length){
                    JDialog jDialog = new WinDialog(new JFrame());
                    jDialog.setLocationRelativeTo(f);
                    jDialog.setVisible(true);
                }
            });
            button.setPreferredSize(new Dimension(10, 10));
            button.setMargin(new Insets(0, 0, 0, 0));
            this.board = board;
            notChecked = true;
        }

        private JButton getButton() {
            return button;
        }

        private int getValue() {
            return value;
        }

        private int getId() {
            return id;
        }

        private void setId(int id) {
            this.id = id;
        }

        private void setValue(int value) {
            this.value = value;
        }

        private void displayValue() {
            if (value == -1) {
                button.setText("\u2600");
                button.setBackground(Color.RED);
            } else if (value != 0) {
                button.setText(String.valueOf(value));
            }
        }

        private void checkCell() {
            button.setEnabled(false);
            displayValue();
            notChecked = false;
            if (value != -1) scores++;
            if (value == 0) board.scanForEmptyCells();
            if (value == -1) board.fail();
            displayScores();
        }

        private boolean isNotChecked() {
            return notChecked;
        }

        private boolean isEmpty() {
            return isNotChecked() && value == 0;
        }

        private void reveal() {
            displayValue();
            if (value != -1) button.setBackground(Color.GREEN);
            button.setEnabled(false);
        }
    }

    private class Board extends JFrame {
        private Cell[][] cells;
        private int cnt;
        private int width = 8, length = 8;

        JPanel panel = new JPanel(new GridLayout(width, length));

        private void setBoard() {
            cnt = 0;
            CellsInit();
            f.add(panel);
            plantMines();
            setCellValues();
        }

        private void resetBoard() {
            counter = 0;
            timer();
            scores = 0;
            jLabelScores.setText(String.format("Scores: %d", scores));
            cnt = 0;
            CellsInit();
            plantMines();
            setCellValues();
            panel.revalidate();
            repaint();
        }

        private void CellsInit() {
            cells = new Cell[width][length];
            panel.removeAll();
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < length; j++) {
                    cells[i][j] = new Cell(this);
                    cells[i][j].setId(++cnt);
                    panel.add(cells[i][j].getButton());
                }
            }
        }

        private void plantMines() {
            ArrayList<Integer> loc = generateMinesLocation(8);
            for (int i : loc) {
                getCell(i).setValue(-1);
            }
        }

        //ramdomlly generate the location of Mines
        private ArrayList<Integer> generateMinesLocation(int q) {
            ArrayList<Integer> loc = new ArrayList<>();
            for (int i = 0; i < q; ) {
                int random = (int) (Math.random() * (width * length));
                if (!loc.contains(random)) {
                    loc.add(random);
                    i++;
                }
            }
            return loc;
        }

        private int dx[] = {-1, -1, -1, 0, 0, 1, 1, 1}, dy[] = {-1, 0, 1, -1, 1, -1, 0, 1};

        //set each value of cells
        private void setCellValues() {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < length; j++) {
                    if (cells[i][j].getValue() != -1) {
                        for (int p = 0; p < 8; p++) {
                            int x = i + dx[p], y = j + dy[p];
                            if (x < 0 || x >= width || y < 0 || y >= length) continue;
                            if (cells[x][y].getValue() == -1) cells[i][j].value++;
                        }
                    }
                }
            }
        }

        //the chain reaction after clicking one cell
        private void scanForEmptyCells() {
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < length; j++) {
                    if (!cells[i][j].isNotChecked()) {
                        for (int p = 0; p < 8; p++) {
                            int x = i + dx[p], y = j + dy[p];
                            if (x < 0 || x >= width || y < 0 || y >= length) continue;
                            if (cells[x][y].isEmpty()) cells[x][y].checkCell();
                        }
                    }
                }
            }
        }


        private Cell getCell(int id) {
            int x = id / length, y = id % length;
            return cells[x][y];
        }

        private void fail() {
            isRun = false;
            for (int i = 0; i < width; i++)
                for (int j = 0; j < length; j++)
                    cells[i][j].reveal();
            JDialog jDialog = new failDialog(new JFrame());
            jDialog.setLocationRelativeTo(f);
            jDialog.setVisible(true);
        }
    }

    private class AboutDialog extends JDialog {
        AboutDialog(JFrame parent) {
            super(parent, "About Dialog", true);

            Box b = Box.createVerticalBox();
            b.add(Box.createGlue());
            b.add(new JLabel("Made by HZJ"));
            b.add(new JLabel("Powered by Java"));
            b.add(new JLabel("Student's ID: 17301095"));
            b.add(Box.createGlue());
            getContentPane().add(b, "Center");

            JPanel p2 = new JPanel();
            JButton ok = new JButton("Ok");
            p2.add(ok);
            getContentPane().add(p2, "South");

            ok.addActionListener(e -> setVisible(false));

            setSize(250, 150);
        }
    }
    private class WinDialog extends JDialog {
        WinDialog(JFrame parent) {
            super(parent, "Congratulation!", true);

            Box b = Box.createVerticalBox();
            b.add(Box.createGlue());
            b.add(new JLabel("You are win!"));
            b.add(Box.createGlue());
            getContentPane().add(b, "Center");

            JPanel p2 = new JPanel();
            JButton ok = new JButton("Ok");
            p2.add(ok);
            getContentPane().add(p2, "South");

            ok.addActionListener(e -> setVisible(false));

            setSize(250, 150);
        }
    }

    private class failDialog extends JDialog {
        failDialog(JFrame parent) {
            super(parent, "嘤嘤嘤", true);

            Box b = Box.createVerticalBox();
            b.add(Box.createGlue());
            JLabel tmp = new JLabel("You are lose!");
            tmp.setFont(new Font("Monaco", Font.PLAIN, 20));
            b.add(tmp);
            b.add(Box.createGlue());
            getContentPane().add(b, "Center");

            JPanel p2 = new JPanel();
            JButton ok = new JButton("Ok");
            p2.add(ok);
            getContentPane().add(p2, "South");

            ok.addActionListener(e -> setVisible(false));

            setSize(250, 150);
        }
    }

    private MineSweeper() {
    }

    private void setMenu() {
        f.setJMenuBar(jMenuBar);
        newGame = new JMenuItem("New Game");
        about = new JMenuItem("about");
        exit = new JMenuItem("exit");
        help = new JMenuItem("help");
        jMenuGame = new JMenu("Game");
        jMenuHelp = new JMenu("Help");

        jMenuGame.setMnemonic(KeyEvent.VK_X);
        jMenuHelp.setMnemonic(KeyEvent.VK_K);
        jMenuBar.add(jMenuGame);
        jMenuBar.add(jMenuHelp);
        exit.addActionListener(e -> System.exit(0));
        about.addActionListener(e -> {
            JDialog jDialog = new AboutDialog(new JFrame());
            jDialog.setLocationRelativeTo(f);
            jDialog.setVisible(true);
        });
        help.addActionListener(e -> {
            JDialog jDialog = new AboutDialog(new JFrame());
            jDialog.setLocationRelativeTo(f);
            jDialog.setVisible(true);
        });
        newGame.addActionListener(e -> board.resetBoard());
        jMenuGame.add(newGame);
        jMenuGame.add(exit);
        jMenuHelp.add(about);
        jMenuHelp.add(help);
    }

    private Board board;
    private Panel scorePanel = new Panel();
    private JLabel jLabelScores = new JLabel("Scores: 0"), jLabelTime = new JLabel("Time: 0.0");
    private void setScore(){
        scorePanel.setLayout(new BoxLayout(scorePanel, BoxLayout.Y_AXIS));
        scorePanel.add(jLabelScores);
        scorePanel.add(jLabelTime);
        jLabelScores.setFont(new Font("Monaco", Font.PLAIN, 35));
        jLabelTime.setFont(new Font("Monaco", Font.PLAIN, 35));

        f.getContentPane().add(BorderLayout.SOUTH, scorePanel);
    }
    private static boolean isRun = true;

    private int counter = 0;
    private void timer(){
        isRun = true;
        Timer timer = new Timer("MyTimer");
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                if (isRun) {
                    jLabelTime.setText(String.format("Time: %d.%d", counter / 10, counter % 10));
                    counter++;//increments the counter
                }else {
                    timer.cancel();
                    timer.purge();
                }
            }
        };
        timer.schedule(timerTask, 0, 100);
    }
    private void launchFrame() throws IOException {
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 600);
        f.setTitle("扫雷");

        setMenu();
        setScore();
        timer();
        f.setVisible(true);

        board = new Board();
        board.setBoard();
        f.setLocationRelativeTo(null);
    }

    public static void main(String[] args) throws IOException {
        MineSweeper guiWindow = new MineSweeper();
        guiWindow.launchFrame();
    }

}
