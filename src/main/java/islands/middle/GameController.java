package islands.middle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Optional;
import java.util.Random;
import islands.backend.GameModel;

/**
 * Control the game flow
 */
public class GameController implements MouseListener {

    private ViewDelegate viewDelegate;
    private GameModel model;
    private int turn;
    private boolean gameOver;
    private int size;

    private Timer timer;

    private boolean simulationGame;

    public final static int WHITE = -1;
    public final static int BLACK = 1;

    /**
     * The required interface to display the game
     */
    public interface ViewDelegate {
        /**
         * Display the turn for the given colour
         * @param clr the player whose turn it is
         */
        void displayTurn(String clr);

        /**
         * play a piece with a given color in a given position
         * @param row the row to play the piece into
         * @param col the column to play the piece into
         * @param clr the color of the piece
         */
        void setColor(int row, int col, int clr);

        /**
         * Listen for Mouse clicks
         * @param ml the mouse listener
         */
        void listen(MouseListener ml);

        /**
         * Convert y and x screen positions into a Point (x,y) on the game board (specific tile)
         * @param y y screen coordinate
         * @param x screen coordinate
         * @return the specific game tile specified as an x,y Point object
         */
        Optional<Point> getHexDim(int y, int x);

        /**
         * The elapsed duration to update the game board
         * @param ms time in milliseconds
         */
        void setTime(double ms);

        /**
         * The winner of the game
         * @param winner "BLACK", "WHITE" or "TIE"
         */
        void setWinner(String winner);

        /**
         * The current score in the game
         * @param whiteScore white's score
         * @param blackScore black's score
         */
        void setScore(int whiteScore, int blackScore);
    }

    /**
     * public constructor
     * @param simulationGame true for a simulated game and false for 2 player game
     */
    public GameController(boolean simulationGame) {
        turn = WHITE;
        gameOver = false;
        this.simulationGame = simulationGame;

        if (simulationGame) {
            simulate();
        }
    }

    /**
     * Change the turn from white to black or vice versa
     */
    public void toggleTurn() {
        turn = turn * -1;
        viewDelegate.displayTurn(turn == BLACK ? "Black": "White");
    }

    /**
     * Set the size of the game board (square)
     * @param size the size of one side of the sq. game board
     */
    public void setSize(int size) {
        this.size = size;
        model = new GameModel(size);
    }

    /**
     * Set the view delegate where output is sent
     * @param vd the view delegate
     */
    public void setDelegate(ViewDelegate vd) {
        viewDelegate = vd;
        vd.displayTurn("White");
        vd.listen(this);
    }

    /**
     * Play a game piece at a point on the screen
     * @param p the row and col position to play into
     */
    public void tryRowColFromPoint(Point p) {
        int col = p.x;
        int row = p.y;
        System.out.println("click row:" + row + " col:" + col);
        tryRowCol(row, col);
    }

    /**
     * attempt to play a game piece in row, col
     * @param row the row to play into
     * @param col the col to play into
     */
    public void tryRowCol(int row, int col) {
        if(model.canPlay(row, col)) {
            viewDelegate.setColor(row, col, turn);
            long startTime = System.nanoTime();
            int color = GameModel.BLACK; //black
            if (turn == -1) {
                color = GameModel.WHITE;
            }
            if(model.makePlay(row, col, color)) {
                System.out.println("winner");
                gameOver = true;
                if(model.whiteScore() > model.blackScore())
                    viewDelegate.setWinner("WHITE");
                else if (model.blackScore() > model.whiteScore())
                    viewDelegate.setWinner("BLACK");
                else
                    viewDelegate.setWinner("TIE");
            }
            else {
                toggleTurn();
            }
            long endTime = System.nanoTime();
            long timeElapsed = endTime - startTime;
            viewDelegate.setTime(timeElapsed / 1000000.0);
            viewDelegate.setScore(model.whiteScore(), model.blackScore());
        }

    }

    /**
     * Simulate a game
     */
    public void simulate() {
        ActionListener simulateTurn = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Random rand = new Random();
                int row = rand.nextInt(size);
                int col = rand.nextInt(size);

                int tries = 0;
                while(!model.canPlay(row, col) && tries < 50) {
                    row = rand.nextInt(size);
                    col = rand.nextInt(size);
                    tries++;
                }
                //having trouble with randoms, just find the first match
                if (tries == 50) {
                    for(int i = 0; i < size; i++) {
                        for(int j = 0; j < size; j++) {
                            if (model.canPlay(i,j)) {
                                row = i;
                                col = j;
                                break;
                            }
                        }
                    }
                }
                //guaranteed we can play since game is over otherwise
                tryRowCol(row, col);
                if (gameOver) {
                    timer.stop();
                }
            }
        };
        timer = new Timer(250, simulateTurn);
        timer.start();
    }

    /**
     * Is this a simulated game?
     * @return true if simulation is on and false o.w.
     */
    public boolean isSimulationGame() {
        return simulationGame;
    }

    /**
     * stop the timer (not currently used)
     */
    public void stopAll() {
        if(timer != null) {
            timer.stop();
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(simulationGame) {
            return;
        }
        if(!gameOver) {
            Optional<Point> point = viewDelegate.getHexDim(e.getY(), e.getX());
            point.ifPresent(this::tryRowColFromPoint);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
