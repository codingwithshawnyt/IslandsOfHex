package islands.frontend;

import islands.middle.GameController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Optional;

/**
 * The GUI components of the game including grid and buttons
 */
public class Game extends JPanel implements GameController.ViewDelegate {

    public final static int BOARD_WIDTH = 600;
    public final static int BOARD_HEIGHT = 800;

    private int gridDimension;

    private HexGrid hexGrid;
    private GameController gc;

    private JLabel turnLabel;
    private JLabel sizeLabel;
    //private JRadioButton size3;
    private JRadioButton size5;
    private JRadioButton size11;

    private JRadioButton size25;

    private ButtonGroup group;

    private JButton simulateGame;
    private JLabel timer;
    private JLabel winner;

    private JLabel whiteScoreLabel;
    private JLabel blackScoreLabel;

    public Game() {
        setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(BOARD_WIDTH,BOARD_HEIGHT));
        gridDimension = 5;
        makeLabels();

        initGame(false);
    }

    /**
     * Generate the labels
     */
    private void makeLabels() {
        turnLabel = new JLabel("");

        sizeLabel = new JLabel("Board Size: ");
        group = new ButtonGroup();
        size5 = new JRadioButton("5");
        size5.addActionListener(v->resetGridDimension(5));

        size5.setSelected(true);
        size11 = new JRadioButton("11");
        size11.addActionListener(v->resetGridDimension(11));
        size25 = new JRadioButton("25");
        size25.addActionListener(v->resetGridDimension(25));


        simulateGame = new JButton("new simulated game");
        simulateGame.addActionListener(v->toggleSimulationGame());

        group.add(size5);
        group.add(size11);
        group.add(size25);

        timer = new JLabel("time: ");
        winner = new JLabel("");

        JPanel northPan = new JPanel();
        whiteScoreLabel = new JLabel("White: 0");
        blackScoreLabel = new JLabel("Black: 0");

        northPan.add(whiteScoreLabel);
        northPan.add(blackScoreLabel);
        add(northPan,BorderLayout.NORTH);

        JPanel southPan = new JPanel();
        southPan.add(sizeLabel);

        southPan.add(size5);
        southPan.add(size11);
        southPan.add(size25);
        southPan.add(simulateGame);

        //southPan.add(timer);
        southPan.add(turnLabel);
        southPan.add(winner);

        add(southPan, BorderLayout.SOUTH);

    }

    /**
     * reset the labels
     */
    private void resetLabel() {
        turnLabel.setText("Turn: White");
        timer.setText("time: ");
        winner.setText("");
    }

    /**
     * Start a new game
     * @param simulation true for a simulation game
     */
    private void initGame(boolean simulation) {

        if (gridDimension > 20) {
            Hexagon.hexagonLength = 10;
        }
        else if(gridDimension > 10)
            Hexagon.hexagonLength = 20;
        else if(gridDimension > 6)
            Hexagon.hexagonLength = 25;
        else
            Hexagon.hexagonLength = 35;
        if (hexGrid != null) {
            remove(hexGrid);
        }
        hexGrid = new HexGrid(gridDimension);

        add(hexGrid, BorderLayout.CENTER);
        if (gc !=null) {
            gc.stopAll();
        }
        gc = new GameController(simulation);
        gc.setSize(gridDimension);
        gc.setDelegate(this);

    }

    /**
     * change the dimension of the game (and start a new one)
     * @param dim the new square dimensions of the board
     */
    private void resetGridDimension(int dim) {
        resetLabel();
        if(hexGrid != null) {
            remove(hexGrid);
        }
        gridDimension = dim;
        initGame(gc.isSimulationGame());
    }

    /**
     * convert between simulation game and not
     */
    private void toggleSimulationGame(){
        resetLabel();

        //is the current game a simulation game
        boolean isSimulatedGame = gc.isSimulationGame();
        System.out.println(isSimulatedGame);
        if(isSimulatedGame) {
            simulateGame.setText("simulated game");
        }
        else{
            simulateGame.setText("non-simulated game");
        }
        initGame(!isSimulatedGame);
    }

    @Override
    public void paintComponent(Graphics g) {
        drawBoard(g);
    }

    /**
     * Draw the background
     * @param g graphics
     */
    private void drawBoard(Graphics g) {
        g.setColor(new Color(0x33cccc));
        g.fillRect(0,0, BOARD_WIDTH, BOARD_HEIGHT);
    }

    @Override
    public void displayTurn(String clr) {
        turnLabel.setText("Turn:" + clr);
    }

    @Override
    public void setColor(int row, int col, int clr) {
        hexGrid.setColor(row, col, clr);
    }

    @Override
    public void listen(MouseListener ml) {
        hexGrid.addMouseListener(ml);
    }

    @Override
    public Optional<Point> getHexDim(int y, int x) {
        return hexGrid.getHexDim(y, x);
    }

    @Override
    public void setTime(double ms) {
        String s = String.format("time: %.3f millisecs", ms);
        timer.setText(s);
    }

    @Override
    public void setWinner(String name) {
        turnLabel.setText("Game Over");
        winner.setText(name + " wins");
        repaint();
    }

    /**
     * Display the score
     * @param whiteScore
     * @param blackScore
     */
    public void setScore(int whiteScore, int blackScore) {
        whiteScoreLabel.setText("White: " + whiteScore);
        blackScoreLabel.setText("Black: " + blackScore);
    }
}
