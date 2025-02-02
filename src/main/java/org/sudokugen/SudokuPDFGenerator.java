package org.sudokugen;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;

public class SudokuPDFGenerator {
    private JTextField[][] inputBoard, solvedBoard;
    private boolean includeSolution;
    private File saveDirectory;

    public SudokuPDFGenerator(JTextField[][] inputBoard, JTextField[][] solvedBoard, boolean includeSolution, File saveDirectory) {
        this.inputBoard = inputBoard;
        this.solvedBoard = solvedBoard;
        this.includeSolution = includeSolution;
        this.saveDirectory = saveDirectory;
    }

    public void generatePDF() {
        try {
            String sudokuID = generateSudokuID();
            File file = new File(saveDirectory, "Sudoku_" + sudokuID + ".pdf");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            document.add(createSudokuTable(inputBoard));

            if (includeSolution) {
                document.newPage();
                document.add(new Paragraph("Solución"));
                document.add(createSudokuTable(solvedBoard));
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PdfPTable createSudokuTable(JTextField[][] board) {
        PdfPTable table = new PdfPTable(9);

        // Tamaño de las celdas
        float cellSize = 50; // Ajusta según necesidad
        table.setTotalWidth((cellSize * 9)+12);
        table.setLockedWidth(true);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font cellFont = new Font(Font.FontFamily.HELVETICA, 35, Font.NORMAL);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = board[row][col].getText();
                PdfPCell cell = new PdfPCell(new Phrase(text.isEmpty() ? " " : text, cellFont));

                // Forzar tamaño cuadrado
                cell.setFixedHeight(cellSize);
                cell.setMinimumHeight(cellSize);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);
                //cell.setHorizontalAlignment(Element.ALIGN_CENTER);


                //cell.setPaddingTop(10f);  // Ajuste para centrar verticalmente

                // Borde estándar
                //float borderWidth = 1f;

                // Bordes gruesos para bloques 3x3
                if (row == 0) cell.setBorderWidthTop(3f);
                if (col == 0) cell.setBorderWidthLeft(3f);
                if (row % 3 == 0) cell.setBorderWidthTop(3f);
                if (col % 3 == 0) cell.setBorderWidthLeft(3f);
                if (row == 8) cell.setBorderWidthBottom(3f);
                if (col == 8) cell.setBorderWidthRight(3f);

                table.addCell(cell);
            }
        }
        return table;
    }

    private String generateSudokuID() {
        try {
            StringBuilder sb = new StringBuilder();
            for (JTextField[] row : inputBoard) {
                for (JTextField cell : row) {
                    sb.append(cell.getText().isEmpty() ? "0" : cell.getText());
                }
            }
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(sb.toString().getBytes());
            return bytesToHex(hash).substring(0, 6);
        } catch (Exception e) {
            return "ERROR";
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) hex.append(String.format("%02x", b));
        return hex.toString();
    }
}
