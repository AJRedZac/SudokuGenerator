package org.sudokugen;

import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SudokuPDFGenerator {
    private JTextField[][] inputBoard, solvedBoard;
    private boolean includeSolution;

    public boolean isIncludeSolution() {
        return includeSolution;
    }

    public void setIncludeSolution(boolean includeSolution) {
        this.includeSolution = includeSolution;
    }

    public File getSaveDirectory() {
        return saveDirectory;
    }

    public void setSaveDirectory(File saveDirectory) {
        this.saveDirectory = saveDirectory;
    }

    private File saveDirectory;

    // Constructor privado para evitar instancias múltiples
    private SudokuPDFGenerator() {}
    private static SudokuPDFGenerator instance; // Única instancia Singleton

    // Método para obtener la instancia única
    public static SudokuPDFGenerator getInstance() {
        if (instance == null) {
            instance = new SudokuPDFGenerator();
        }
        return instance;
    }

    public void GenerateSingleSudokuPDF(JTextField[][] inputBoard, JTextField[][] solvedBoard, boolean includeSolution, File saveDirectory) {
        this.inputBoard = inputBoard;
        this.solvedBoard = solvedBoard;
        this.includeSolution = includeSolution;
        this.saveDirectory = saveDirectory;
    }

    public void generateBatchPDF(List<int[][]> sudokuList, List<int[][]> solutionList, boolean includeNumbers, boolean includeSolution) {
        if (sudokuList.size() != solutionList.size()) {
            throw new IllegalArgumentException("El número de sudokus y soluciones no coincide.");
        }

        try {
            Document document = new Document(PageSize.A4);
            String batchID = "Lote_" + System.currentTimeMillis() + ".pdf";
            File batchFile = new File(saveDirectory, batchID);
            PdfWriter.getInstance(document, new FileOutputStream(batchFile));

            document.open();
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);

            for (int i = 0; i < sudokuList.size(); i++) {


                PdfPTable outerTable = new PdfPTable(1);
                outerTable.setWidthPercentage(100.0f);

                PdfPCell cell = new PdfPCell();
                cell.setMinimumHeight(document.getPageSize().getHeight() - 36.0f - 36.0f);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                if (includeNumbers) {
                    Paragraph title = new Paragraph("Sudoku " + (i + 1), titleFont);
                    title.setAlignment(Element.ALIGN_CENTER);
                    cell.addElement(title);
                    cell.addElement(new Paragraph(" ")); // Espacio en blanco para separación
                }
                cell.addElement(createCenteredSudokuTable(sudokuList.get(i)));
                cell.setBorder(0);
                outerTable.addCell(cell);
                document.add(outerTable);
                document.newPage();


                if (includeSolution) {
                    //System.out.println("solución incluida");
                    outerTable = new PdfPTable(1);
                    outerTable.setWidthPercentage(100.0f);



                    cell = new PdfPCell();
                    cell.setMinimumHeight(document.getPageSize().getHeight() - 36.0f - 36.0f);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    if (includeNumbers) {
                        Paragraph solutionTitle = new Paragraph("Solución " + (i + 1), titleFont);
                        solutionTitle.setAlignment(Element.ALIGN_CENTER);
                        cell.addElement(solutionTitle);
                        cell.addElement(new Paragraph(" "));
                    }

                    cell.addElement(createCenteredSudokuTable(solutionList.get(i)));
                    cell.setBorder(0);
                    outerTable.addCell(cell);
                    document.add(outerTable);
                    document.newPage();
                }
            }
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generatePDF() {
        try {
            String sudokuID = generateSudokuID();
            File file = new File(saveDirectory, "Sudoku_" + System.currentTimeMillis() + "_" + sudokuID + ".pdf");
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            PdfPTable outerTable = new PdfPTable(1);
            outerTable.setWidthPercentage(100.0f);

            PdfPCell cell = new PdfPCell();
            cell.setMinimumHeight(document.getPageSize().getHeight() - 36.0f - 36.0f);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.addElement(createCenteredSudokuTable(inputBoard));
            cell.setBorder(0);
            outerTable.addCell(cell);
            document.add(outerTable);
            document.newPage();


            if (includeSolution) {
                outerTable = new PdfPTable(1);
                outerTable.setWidthPercentage(100.0f);

                cell = new PdfPCell();
                cell.setMinimumHeight(document.getPageSize().getHeight() - 36.0f - 36.0f);
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setBorder(0);

                Paragraph solutionTitle = new Paragraph("Solución", new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD));
                solutionTitle.setAlignment(Element.ALIGN_CENTER);
                cell.addElement(solutionTitle);
                cell.addElement(new Paragraph(" "));

                cell.addElement(createCenteredSudokuTable(solvedBoard));
                outerTable.addCell(cell);
                document.add(outerTable);
                document.newPage();
            }

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 📌 Nuevo método para centrar el Sudoku en la página
    private PdfPTable createCenteredSudokuTable(int[][] board) {
        PdfPTable table = new PdfPTable(9);

        float cellSize = 50;
        table.setTotalWidth((cellSize * 9) + 12);
        table.setPaddingTop(((cellSize * 9) + 12));
        table.setLockedWidth(true);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);


        Font cellFont = new Font(Font.FontFamily.HELVETICA, 35, Font.NORMAL);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = (board[row][col] == 0) ? " " : String.valueOf(board[row][col]);
                PdfPCell cell = new PdfPCell(new Phrase(text, cellFont));

                cell.setFixedHeight(cellSize);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);

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

    // 📌 Método para centrar Sudoku desde JTextFields
    private PdfPTable createCenteredSudokuTable(JTextField[][] board) {
        PdfPTable table = new PdfPTable(9);
        float cellSize = 50;
        table.setTotalWidth((cellSize * 9) + 12);
        table.setLockedWidth(true);
        table.setHorizontalAlignment(Element.ALIGN_CENTER);

        Font cellFont = new Font(Font.FontFamily.HELVETICA, 35, Font.NORMAL);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = board[row][col].getText().isEmpty() ? " " : board[row][col].getText();
                PdfPCell cell = new PdfPCell(new Phrase(text, cellFont));

                cell.setFixedHeight(cellSize);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setVerticalAlignment(Element.ALIGN_CENTER);

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

    private String generateSudokuID() throws NoSuchAlgorithmException {
        StringBuilder sb = new StringBuilder();
        for (JTextField[] row : inputBoard) {
            for (JTextField cell : row) {
                sb.append(cell.getText().isEmpty() ? "0" : cell.getText());
            }
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(sb.toString().getBytes());
        return bytesToHex(hash).substring(0, 6);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) hex.append(String.format("%02x", b));
        return hex.toString();
    }
}
