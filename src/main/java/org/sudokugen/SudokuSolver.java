package org.sudokugen;

public class SudokuSolver {
    private static final int SIZE = 9;

    private static int steps; // Contador de pasos

    public static int solve(int[][] board) {
        steps = 0; // Reiniciar contador de pasos
        return solveSudoku(board, 0, 0) ? steps : -1; // Retorna -1 si no hay solución
    }
    private static boolean solveSudoku(int[][] board, int row, int col) {
        if (row == 9) return true; // Fin del tablero

        if (col == 9) return solveSudoku(board, row + 1, 0);

        if (board[row][col] != 0) return solveSudoku(board, row, col + 1);

        for (int num = 1; num <= 9; num++) {
            if (isValid(board, row, col, num)) {
                board[row][col] = num;
                steps++; // Incrementar número de pasos
                if (solveSudoku(board, row, col + 1)) return true;
                board[row][col] = 0; // Retroceso
            }
        }
        return false;
    }

    private static boolean isValid(int[][] board, int row, int col, int num) {
        for (int i = 0; i < 9; i++) {
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
}
