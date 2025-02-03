package org.sudokugen;

import java.util.Random;

public class SudokuGenerator {
    private static final int SIZE = 9;
    private static final int EMPTY = 0;
    private int[][] board = new int[SIZE][SIZE];
    private Random random = new Random();

    public SudokuGenerator(int pistas) {

            resetBoard();
            fillDiagonal();
            fillRemaining(0, 3);
            removeNumbers(81 - pistas); // Ajustar cantidad de pistas

    }

    private void resetBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board[i][j] = 0;
            }
        }
    }
    private void fillDiagonal() {
        for (int i = 0; i < SIZE; i += 3)
            fillBox(i, i);
    }



    private void fillBox(int row, int col) {
        boolean[] used = new boolean[SIZE + 1];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int num;
                do {
                    num = random.nextInt(SIZE) + 1;
                } while (used[num]);
                used[num] = true;
                board[row + i][col + j] = num;
            }
        }
    }

    private boolean fillRemaining(int row, int col) {
        if (row == SIZE - 1 && col == SIZE)
            return true;
        if (col == SIZE) {
            row++;
            col = 0;
        }
        if (board[row][col] != 0)
            return fillRemaining(row, col + 1);

        for (int num = 1; num <= SIZE; num++) {
            if (isValid(row, col, num)) {
                board[row][col] = num;
                if (fillRemaining(row, col + 1))
                    return true;
                board[row][col] = EMPTY;
            }
        }
        return false;
    }

    private void removeNumbers(int count) {
        for (int i = 0; i < count; i++) {
            int row, col;
            do {
                row = random.nextInt(SIZE);
                col = random.nextInt(SIZE);
            } while (board[row][col] == EMPTY);
            board[row][col] = EMPTY;
        }
    }







    private boolean isValid(int row, int col, int num) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == num || board[i][col] == num) return false;
        }

        int startRow = row - row % 3, startCol = col - col % 3;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (board[i + startRow][j + startCol] == num) return false;
            }
        }
        return true;
    }

    public int[][] getBoard() {
        return board;
    }

    public void printBoard() {
        for (int[] row : board) {
            for (int num : row)
                System.out.print(num == EMPTY ? " . " : " " + num + " ");
            System.out.println();
        }
    }


}
