package islands;

import islands.frontend.Game;

import javax.swing.*;
import java.awt.*;

/**
 * Main launching point for the Hex Game
 */
public class GameDriver {

    public static void main(String [] args) {
        JFrame frame = new JFrame("Islands of Hex");

        Game game = new Game();
        frame.add(game);
        frame.setSize(new Dimension(game.BOARD_WIDTH,game.BOARD_HEIGHT));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}
