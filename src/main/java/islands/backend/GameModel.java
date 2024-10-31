/*
 * Shawn Ray
 * Period 4
 * GameModel.java
 * The purpose of this code 
 */

package islands.backend;

public class GameModel {

	public static final int EMPTY = 0; // empty tiles
	public static final int WHITE = -1; // white color tiles
	public static final int BLACK = 1; // black color tiles

	private int[][] board; // playing board
	private int size; // length of the board

	public GameModel(int sz) { // first constructor given board size user wants
		size = sz; // setting size instance var as such
		board = new int[sz][sz]; // making them all empty
	}

	// whether a cell can be used
	public boolean canPlay(int row, int col) {
		if (row >= size || col >= size || col < 0 || row < 0) { // checking bounds of playing boards
			throw new IllegalArgumentException("Location Out of Bounds");
		}

		// simple check if the cell is empty
		return board[row][col] == EMPTY;
	}

	// actually placing a piece on the board
	public boolean makePlay(int row, int col, int clr) {
		if (!canPlay(row, col)) // call to canPlay to check validity of space
		{
			return false;
		}
		board[row][col] = clr; // if it's open, we change the color as needed for whoever clicked on it
		return gameOver(clr); // checking whether that move ends the game
	}

//checking if a move ends the game
	private boolean gameOver(int clr) {
		boolean[][] alreadyVisited = new boolean[size][size]; // boolean array to supplement depth-first search
		for (int i = 0; i < size; i++) { // iterating through board
			if (clr == WHITE && !alreadyVisited[0][i] && board[0][i] == WHITE) { // if we find any white unvisited cells
																					// going from the top to the bottom
				if (dft(0, i, WHITE, alreadyVisited)) { // we run a depth-first search to see if there's anywhere to go
														// from top to bottom
					return true; // if so, we declare the game over
				}
			}

			else if (clr == BLACK && !alreadyVisited[i][0] && board[i][0] == BLACK) { // same thing for black but going
																						// left to right
				if (dft(i, 0, BLACK, alreadyVisited)) {// we run a depth-first search to see if there's anywhere to go
														// from left to right
					return true; // if so, we declare the game over
				}
			}
		}

		return false; // if all the conditions fail the game continues
	}

	//We go about the graph
	private boolean dft(int r, int c, int clr, boolean[][] visited) {
		if (clr == WHITE && r == size - 1 || clr == BLACK && c == size - 1) { //if a path has been found, we are chillin for the recursion
			return true;
		}

		visited[r][c] = true; //we've now checked that spot

		int[][] hexagonality = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { 1, 1 } }; //staggering array to allow for hexagonal approach

		for (int[] dir : hexagonality) { //iterating through hexagonal directions
			int checkRow = r + dir[0];
			int checkCol = c + dir[1];

			// making sure two things
			//1. within board bounds
			//2. same color
			if (checkRow >= 0 && checkRow < size && checkCol >= 0 && checkCol < size 
					&& !visited[checkRow][checkCol] && board[checkRow][checkCol] == clr)
			{
				if (dft(checkRow, checkCol, clr, visited))
				{
					return true; //recursive part where we are iterating through the graph
				}
			}
		}
		return false; //this means there isn't a valid path
	}

	public int whiteScore() {
		return score(WHITE); //call to helper
	}

	public int blackScore() {
		return score(BLACK); //call to helper
	}

	private int score(int clr) {
		boolean[][] visto = new boolean[size][size]; //already visited array
		int score = 0;
		for (int row = 0; row < size; row++) { //iterating through graph
			for (int col = 0; col < size; col++) {
				if (board[row][col] == clr && !visto[row][col]) { //any successful dfs call means the presence of another island
					score++; //so we augment the score
					iterateTraverse(row, col, clr, visto);
				}
			}
		}
		return score; //giving final score
	}

	private void iterateTraverse(int r, int c, int clr, boolean[][] visited) {
		if (r >= size || c >= size || visited[r][c] || r < 0 ||  c < 0 || board[r][c] != clr) //recursion stop point for if out of bounds or wrong color
			return; //end

		visited[r][c] = true; //we have seen this cell

		int[][] hexagonality = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }, { -1, -1 }, { 1, 1 } };
		for (int[] dir : hexagonality) {
			iterateTraverse(r + dir[0], c + dir[1], clr, visited); 
																
		}
	}
}
