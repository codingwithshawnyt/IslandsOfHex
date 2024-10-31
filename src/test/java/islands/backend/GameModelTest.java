package islands.backend;

import islands.backend.GameModel;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    final int SMALL = 5;
    final int MEDIUM = 11;
    final int LARGE = 25;
    final int SUPER_LARGE = 2500;

    /**
     * The first deliverable requires all of the testcases
     * in the FirstDeliverableTests class to pass
     *
     * i.e., the basics of GameModel.canPlay should be implemented
     */
    @Nested
    class FirstDeliverableTests {

        GameModel model;


        @BeforeEach
        void init() {
            model = new GameModel(SMALL);
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4} )
        @DisplayName("CanPlay True on valid rows")
        void canPlayValidRows(int row){
            assertTrue(model.canPlay(row, 0), "canPlay into valid spot on empty board should be true");
        }
        @ParameterizedTest
        @ValueSource(ints = {0, 1, 2, 3, 4} )
        @DisplayName("CanPlay True on valid cols")
        void canPlayValidCols(int col){
            assertTrue(model.canPlay(0, col),"canPlay into empty board valid spot is true");
        }

        @Test
        @DisplayName("CanPlay throws exception if row is negative")
        void canPlayExceptionNegativeRow() {
            assertThrows(IllegalArgumentException.class, ()->model.canPlay(-1, 0),"expecting illegal argument exception on negative canPlay(-1, col)"  );
        }

        @Test
        @DisplayName("CanPlay throws exception if col is negative")
        void canPlayExceptionNegativeCol() {
            assertThrows(IllegalArgumentException.class, ()->model.canPlay(0, -1),"expecting illegal argument exception on negative canPlay(row, -1)"  );
        }

        @Test
        @DisplayName("CanPlay throws exception if col is negative")
        void canPlayExceptionNegativeRowCol() {
            assertThrows(IllegalArgumentException.class, ()->model.canPlay(-1, -1),"expecting illegal argument exception on negative canPlay(-1,-1)"  );
        }

        @Test
        @DisplayName("CanPlay throws exception if row, col outside bounds")
        void canPlayLargeRowCol() {
            assertThrows(IllegalArgumentException.class, ()->model.canPlay(5, 5),"expecting illegal argument exception on out of bounds canPlay(5,5)"  );
        }

    }

    @Nested
    class canPlayTests {

        @Test
        void playIntoOccupiedSpotWhite() {
            GameModel model = new GameModel(SMALL);
            model.makePlay(0,0, GameModel.WHITE);
            assertFalse(model.canPlay(0,0), "canPlay called on occupied spot should return false");
        }

        @Test
        void playIntoOccupiedSpotBlack() {
            GameModel model = new GameModel(SMALL);
            model.makePlay(0,0, GameModel.BLACK);
            assertFalse(model.canPlay(0,0), "canPlay called on occupied spot should return false");
        }

        @Test
        void playIntoEmptySpotLarge() {
            GameModel model = new GameModel(LARGE);
            assertTrue(model.canPlay(LARGE-1, LARGE-1), "canPlay into empty spot on SUPER_LARGE is True ");
        }

        @Test
        void playIntoEmptySpotSuperLarge() {
            GameModel model = new GameModel(SUPER_LARGE);
            assertTrue(model.canPlay(SUPER_LARGE-1, SUPER_LARGE-1), "canPlay into empty spot on SUPER_LARGE is True ");
        }
    }

    //some manually crafted scenarios


    @Nested
    class ScoreTests {


        GameModel model;
        @BeforeEach
        void init() {
            model = new GameModel(SMALL);
        }

        @Test
        @DisplayName("Initial Board has score 0 0")
        void initialScore() {
            assertEquals(0, model.blackScore(), "empty board should hav black score 0");
            assertEquals(0, model.whiteScore(), "empty board should have white score 0");
        }

        @Test
        @DisplayName("GameBoard with 1 white piece")
        void testSingleWhitePiece() {
            model.makePlay(0,0,GameModel.WHITE);
            assertEquals(1, model.whiteScore(), "expecting white score = 1");
            assertEquals(0, model.blackScore(), "expecting black score = 0");
        }

        @Test
        @DisplayName("GameBoard with 1 black piece")
        void testSingleBlackPiece() {
            model.makePlay(0,0,GameModel.BLACK);
            assertEquals(1, model.blackScore(), "expecting black score is 1");
            assertEquals(0, model.whiteScore(), "expecting white score is 0");
        }

        @Test
        @DisplayName("Connect 2 existing islands of same color through E-W")
        void connect2ExistingSameColorEW() {
            model.makePlay(1,1, GameModel.WHITE);
            model.makePlay(1,3, GameModel.WHITE);
            assertEquals(2, model.whiteScore(), "2 islands should have score 2");
            model.makePlay(1,2, GameModel.WHITE);
            assertEquals(1, model.whiteScore(), "1 island with 3 connected should have score 1");
        }

        @Test
        @DisplayName("Connect 2 existing islands of same color through NW-SE")
        void connect2ExistingSameColorNWSE() {
            model.makePlay(1,1, GameModel.WHITE);
            model.makePlay(3,3, GameModel.WHITE);
            assertEquals(2, model.whiteScore(), "2 islands should have score 2");
            model.makePlay(2,2, GameModel.WHITE);
            assertEquals(1, model.whiteScore(), "1 island with 3 connected should have score 1");
        }

        @Test
        @DisplayName("Connect 2 existing islands of same color through N-S")
        void connect2ExistingSameColorNS() {
            model.makePlay(1,1, GameModel.WHITE);
            model.makePlay(3,1, GameModel.WHITE);
            assertEquals(2, model.whiteScore(), "2 islands should have score 2");
            model.makePlay(2,1, GameModel.WHITE);
            assertEquals(1, model.whiteScore(), "1 island with 3 connected should have score 1");
        }

        @Test
        @DisplayName("Adjacent Tiles of opposite color are separate islands")
        void adjacentOppositeColors() {
            model.makePlay(1,1, GameModel.WHITE);
            model.makePlay(1,2, GameModel.BLACK);
            assertEquals(1, model.whiteScore(), "score is one for single white tile next to black tile");
            assertEquals(1, model.blackScore(), "score is one for single black tile next to white tile");
        }

        @Test
        @DisplayName("Join 3 islands with a single new piece")
        void join3IslandsInto1(){
            model.makePlay(1,1,GameModel.BLACK);
            model.makePlay(2, 3, GameModel.BLACK);
            model.makePlay(3,2, GameModel.BLACK);
            assertEquals(0, model.whiteScore());
            assertEquals(3, model.blackScore());
            model.makePlay(2,2, GameModel.BLACK);
            assertEquals(1, model.blackScore(), "expecting one connected component");
        }


    }

    @Nested
    class GameOverTests {

        @Test
        @DisplayName("White vertical line")
        void whiteVerticalLine() {
            GameModel model = new GameModel(SMALL);
            model.makePlay(0,1, GameModel.WHITE);
            model.makePlay(1,1, GameModel.WHITE);
            model.makePlay(2,1, GameModel.WHITE);
            boolean done = model.makePlay(3,1, GameModel.WHITE);
            assertFalse(done, "game shouldn't be finished yet");
            done = model.makePlay(4,1, GameModel.WHITE);
            assertTrue(done, "game should be over");
        }

        @Test
        @DisplayName("Black vertical line")
        void blackVerticalLine() {
            GameModel model = new GameModel(SMALL);
            model.makePlay(0,1, GameModel.BLACK);
            model.makePlay(1,1, GameModel.BLACK);
            model.makePlay(2,1, GameModel.BLACK);
            model.makePlay(3,1, GameModel.BLACK);
            boolean done = model.makePlay(4,1, GameModel.BLACK);
            assertFalse(done, "game shouldn't be finished yet");
        }

        @Test
        @DisplayName("White horizontal Line")
        void whiteHorizontalLine() {
            GameModel model = new GameModel(SMALL);
            model.makePlay(1,0, GameModel.WHITE);
            model.makePlay(1,1, GameModel.WHITE);
            model.makePlay(1,2, GameModel.WHITE);
            model.makePlay(1,3, GameModel.WHITE);
            boolean done = model.makePlay(1,4, GameModel.WHITE);
            assertFalse(done, "game shouldn't be finished yet");
        }

        @Test
        @DisplayName("Black Horizontal line")
        void blackHorizontalLine() {
            GameModel model = new GameModel(SMALL);
            model.makePlay(1,0, GameModel.BLACK);
            model.makePlay(1,1, GameModel.BLACK);
            model.makePlay(1,2, GameModel.BLACK);
            boolean done = model.makePlay(1,3, GameModel.BLACK);
            assertFalse(done, "game shouldn't be finished yet");
            done = model.makePlay(1,4, GameModel.BLACK);
            assertTrue(done, "game should be over");
        }
    }

    @Nested
    class EfficiencyTests {

        GameModel model;

        @BeforeEach
        void init(){
            model = new GameModel(SUPER_LARGE);
        }

        /**
         * Play a SUPER_LARGE game board
         */
        @Timeout(value = 650, unit = TimeUnit.MILLISECONDS)
        @Test
        void superLargeTimeOutTest() {
            final long startTime = System.currentTimeMillis();

            //fill in the middle
            for(int row = 1; row < SUPER_LARGE-1; row++) {
                for(int col = 1; col < SUPER_LARGE-1; col++) {
                    boolean color = GameModel.BLACK;
                    if (col % 2 == 0) {
                        color = GameModel.WHITE;
                    }
                    model.makePlay(row,col, color);
                }
            }

            model.makePlay(0,2, GameModel.WHITE);
            model.makePlay(0,1, GameModel.BLACK);
            //should complete the game:
            model.makePlay(SUPER_LARGE-1, 2, GameModel.WHITE);
            boolean done = model.makePlay(SUPER_LARGE-1, 1, GameModel.WHITE);
            assertTrue(done, "game should finish");

            final long endTime = System.currentTimeMillis();

            System.out.println("Total execution time: " + (endTime - startTime) + "ms");
        }

    }
}