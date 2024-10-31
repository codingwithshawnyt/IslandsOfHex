package islands.frontend;

import islands.middle.GameController;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * GUI grid of the hexagon tiles
 */
public class HexGrid extends JComponent{

    int size;
    Hexagon[][] hexagons;

    /**
     * Create a Hex Grid with size num x num
     * @param num
     */
    public HexGrid(int num) {
        setPreferredSize(new Dimension(600,800));
        size = num;
        hexagons = new Hexagon[size][size];
        makeHexagons();
    }

    /**
     * Create the hexagon objects
     */
    private void makeHexagons() {
        //1.5 and .75
        int topLeftY = (int) (Game.BOARD_HEIGHT/ 2.0 - 1.5 * size* (Hexagon.hexagonLength- Hexagon.hexagonLength/2));
        int topLeftX = (int) (Game.BOARD_WIDTH/ 2.0 - 0.75 * size* (Hexagon.hexagonLength+Hexagon.hexagonLength/3));
        //iterate the columns first since x is fixed
        //per column
        for(int col = 0; col < size; col++) {
            int x = topLeftX + col * (Hexagon.hexagonLength - 1) * 2;
            int y = topLeftY - col * (Hexagon.hexagonLength - 2);
            //int y = startY - row * (Hexagon.hexagonLength-1) * 2;
            //int x = startX + row * (Hexagon.hexagonLength -2);
            for(int row = 0; row < size; row++) {
                int columnY = y + row * (Hexagon.hexagonLength) * 2;

                hexagons[row][col] = new Hexagon(x, columnY, Color.LIGHT_GRAY);
            }
        }
    }

    /**
     * Set the color for a hexagon
     * @param row the row position
     * @param col the column position
     * @param color the color to set
     */
    public void setColor(int row, int col, int color) {
        if (color == GameController.WHITE) {
            hexagons[row][col].setColor(Color.WHITE);
        }
        else
            hexagons[row][col].setColor(Color.BLACK);
        repaint();
    }

    /**
     * Return the array position of the hex corresponding to the given mouse click locatoin
     * @param y mouse y (row)
     * @param x mouse x coordinate (column)
     * @return the corresponding location of the hexagon in the board
     */
    public Optional<Point> getHexDim(int y, int x) {
        for(int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if(hexagons[row][col].getPolygon().contains(x, y) ){
                    return Optional.of(new Point(col, row));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void paintComponent(Graphics g) {
        for(Hexagon[] hs : hexagons) {
            for(Hexagon h: hs) {
                h.draw(g);
            }
        }
    }

}
