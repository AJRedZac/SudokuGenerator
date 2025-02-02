package org.sudokugen;

import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Main {

    private JFrame frame;
    private JTextField[][] inputBoard = new JTextField[9][9];
    private JTextField[][] solvedBoard = new JTextField[9][9];
    private JButton solveButton, generateButton, clearButton, printButton, selectFolderButton;
    private JCheckBox includeSolutionCheckbox;
    private JSlider difficultySlider;
    private JLabel sliderValueLabel, folderLabel;
    private JTextArea logArea;
    private boolean isModoOscuro=false;
    private File defaultSaveFolder;
    private static final String CONFIG_FILE = "config.properties";

    public Main() {
        frame = new JFrame("Sudoku Solver");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setSize(1920, 1080);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setResizable(false);
        frame.setLayout(new BorderLayout());

        loadConfig(); // Cargar la carpeta de guardado


        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBackground(new Color(220, 220, 220));;



        // Crear los tableros de entrada y salida
        JPanel inputPanel = createBoardPanel(inputBoard, "Ingresar Sudoku", 640);
        JPanel solvedPanel = createBoardPanel(solvedBoard, "Solución", 640);


        // Configurar etiqueta del slider
        sliderValueLabel = new JLabel("Pistas: 35", SwingConstants.CENTER);
        sliderValueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        sliderValueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Configurar slider para la dificultad
        difficultySlider = new JSlider(JSlider.HORIZONTAL, 17, 50, 35);
        difficultySlider.setMajorTickSpacing(5);
        //7difficultySlider.setPaintTicks(true);
        //difficultySlider.setPaintLabels(true);
        difficultySlider.setBackground(new Color(220, 220, 220));
        difficultySlider.setBorder(new EmptyBorder(5, 10, 5, 10));
        difficultySlider.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Agregar un listener para actualizar la etiqueta cuando se mueva el slider
        difficultySlider.addChangeListener(e -> {
            int value = difficultySlider.getValue();
            sliderValueLabel.setText("Pistas: " + value);
        });

        // Botón de generar
        generateButton = new JButton("Generar");
        generateButton.addActionListener(e -> GeneratePuzzle());
        styleButton(generateButton);

        // Botón de resolver
        solveButton = new JButton("Resolver");
        solveButton.addActionListener(e -> solveSudoku());
        styleButton(solveButton);

        // **Nuevo** Botón de limpiar
        clearButton = new JButton("Limpiar");
        clearButton.addActionListener(e -> clearBoards()); // Llama a la función clearBoards()
        styleButton(clearButton);

        printButton = new JButton("Guardar");
        printButton.addActionListener(e -> printSudoku());
        styleButton(printButton);

        selectFolderButton = new JButton("Folder");
        selectFolderButton.addActionListener(e -> selectSaveFolder());
        styleButton(selectFolderButton);

        includeSolutionCheckbox = new JCheckBox("Incluir solución");
        includeSolutionCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
        includeSolutionCheckbox.setBackground(new Color(220, 220, 220));




        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(170, 640)); // Tamaño cuadrado como los tableros
        buttonPanel.setMaximumSize(new Dimension(170, 640));


        // Alinear elementos en el panel
        buttonPanel.add(Box.createVerticalGlue());  // Espacio superior
        buttonPanel.add(sliderValueLabel);
        buttonPanel.add(difficultySlider);
        buttonPanel.add(Box.createVerticalStrut(10)); // Espacio más grande entre slider y botones
        buttonPanel.add(generateButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Espacio más grande entre los botones
        buttonPanel.add(solveButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Espacio entre los botones
        buttonPanel.add(clearButton); // Agregar botón de limpiar
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(includeSolutionCheckbox);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(printButton);
        buttonPanel.add(Box.createVerticalStrut(10));
        buttonPanel.add(selectFolderButton);
        buttonPanel.add(Box.createVerticalGlue());


        buttonPanel.setBackground(new Color(220, 220, 220));

        // Crear área de log
        logArea = new JTextArea();
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 20));
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBackground(new Color(240, 240, 240));

        // Panel de scroll para el log
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(800, 100));
        logScrollPane.setBackground(new Color(220,220,220));
        logScrollPane.setFont(new Font("Arial", Font.BOLD, 20));
        logScrollPane.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                "Registro de Eventos",
                0, 0,
                new Font("Arial", Font.BOLD, 20),
                Color.BLACK
        ));

        //Panel de Configuración
        //Folder de Guardado

        JPanel configurationPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        configurationPanel.setBackground(new Color(220, 220, 220));

        folderLabel = new JLabel("Carpeta de Guardado: " + defaultSaveFolder.getAbsolutePath());
        folderLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        configurationPanel.add(folderLabel);
        // Agregar componentes al frame
        mainPanel.add(Box.createHorizontalGlue());
        mainPanel.add(inputPanel);
        mainPanel.add(buttonPanel);
        mainPanel.add(solvedPanel);
        mainPanel.add(Box.createHorizontalGlue());

        frame.setJMenuBar(createMenuBar(logScrollPane));
        frame.add(configurationPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, BorderLayout.NORTH);
        frame.add(logScrollPane, BorderLayout.CENTER); // Agregar log en la parte inferior
        frame.setVisible(true);
    }

    private JMenuBar createMenuBar(JScrollPane logScrollPane) {
        JMenuBar menuBar = new JMenuBar();

        // Menú Archivo
        JMenu archivoMenu = new JMenu("Archivo");
        JMenuItem nuevoItem = new JMenuItem("Nuevo Sudoku");
        JMenuItem abrirItem = new JMenuItem("Abrir...");
        JMenuItem guardarItem = new JMenuItem("Guardar Sudoku");
        JMenuItem salirItem = new JMenuItem("Salir");

        nuevoItem.addActionListener(e -> clearBoards());
        guardarItem.addActionListener(e -> printSudoku());
        salirItem.addActionListener(e -> System.exit(0));

        archivoMenu.add(nuevoItem);
        archivoMenu.add(abrirItem);
        archivoMenu.add(guardarItem);
        archivoMenu.addSeparator();
        archivoMenu.add(salirItem);

        // 🖥️ Menú Vista
        JMenu vistaMenu = new JMenu("Vista");
        JCheckBoxMenuItem modoOscuroItem = new JCheckBoxMenuItem("Modo Oscuro");

        modoOscuroItem.setSelected(isModoOscuro); // Cargar estado desde la configuración
        modoOscuroItem.addActionListener(e -> cambiarModoOscuro(modoOscuroItem.isSelected()));

        JCheckBoxMenuItem mostrarLogItem = new JCheckBoxMenuItem("Mostrar Registro de Eventos", true);
        mostrarLogItem.addActionListener(e -> toggleLogVisibility(mostrarLogItem.isSelected(), logScrollPane));

        vistaMenu.add(modoOscuroItem);
        vistaMenu.add(mostrarLogItem);

        // Menú Ventana
        JMenu ventanaMenu = new JMenu("Ventana");
        JMenuItem pantallaCompletaItem = new JMenuItem("Pantalla Completa");
        JMenuItem restaurarItem = new JMenuItem("Restaurar");

        pantallaCompletaItem.addActionListener(e -> setFullscreen());
        restaurarItem.addActionListener(e -> restaurarVentana());

        ventanaMenu.add(pantallaCompletaItem);
        ventanaMenu.add(restaurarItem);

        // Menú Ayuda
        JMenu ayudaMenu = new JMenu("Ayuda");
        JMenuItem acercaDeItem = new JMenuItem("Acerca de");
        acercaDeItem.addActionListener(e -> mostrarAcercaDe());

        ayudaMenu.add(acercaDeItem);

        // Agregar menús a la barra de menú
        menuBar.add(archivoMenu);
        menuBar.add(vistaMenu);
        menuBar.add(ventanaMenu);
        menuBar.add(ayudaMenu);

        return menuBar;
    }

    private void toggleLogVisibility(boolean visible,JScrollPane logScrollPane) {
        logScrollPane.setVisible(visible);
        frame.revalidate();
        frame.repaint();
    }

    private void selectSaveFolder() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int option = fileChooser.showDialog(null, "Seleccionar Carpeta");
        if (option == JFileChooser.APPROVE_OPTION) {
            defaultSaveFolder = fileChooser.getSelectedFile();
            folderLabel.setText("Carpeta de Guardado: " + defaultSaveFolder.getAbsolutePath());

            // Guardar la nueva carpeta en el archivo de configuración
            saveConfig();
            log("Carpeta de guardado cambiada a: " + defaultSaveFolder.getAbsolutePath());
        }
    }

    private void saveConfig() {
        try {
            Properties props = new Properties();
            props.setProperty("saveFolder", defaultSaveFolder.getAbsolutePath());
            props.setProperty("modoOscuro", String.valueOf(isModoOscuro)); // Guardar estado

            File configFile = new File(CONFIG_FILE);
            FileOutputStream out = new FileOutputStream(configFile);
            props.store(out, "Configuración de Sudoku Generator");
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            log("Error al guardar la configuración.");
        }
    }

    private void loadConfig() {
        Properties props = new Properties();
        File configFile = new File(CONFIG_FILE);

        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                props.load(in);
                defaultSaveFolder = new File(props.getProperty("saveFolder"));
                isModoOscuro = Boolean.parseBoolean(props.getProperty("modoOscuro", "false")); // Cargar modo oscuro
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Si la carpeta guardada no existe, usar la carpeta por defecto
        if (defaultSaveFolder == null || !defaultSaveFolder.exists()) {
            defaultSaveFolder = new File(System.getProperty("user.home") + "/Documents/Sudokus Generados");
            if (!defaultSaveFolder.exists()) {
                defaultSaveFolder.mkdirs();
            }
        }
    }
    private void styleButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        button.setMaximumSize(new Dimension(150, 80));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);  // Centrar el botón
    }
    private JPanel createBoardPanel(JTextField[][] board, String title, int size) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(size, size)); // Tamaño cuadrado
        panel.setMaximumSize(new Dimension(size, size));
        panel.setBackground(new Color(245, 245, 245)); // Fondo gris claro
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 30));
        panel.add(label, BorderLayout.NORTH);

        JPanel gridPanel = new JPanel(new GridLayout(9, 9));
        gridPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                board[row][col] = new JTextField(2);
                board[row][col].setFont(new Font("Monospaced", Font.BOLD, 25));
                board[row][col].setHorizontalAlignment(JTextField.CENTER);
                board[row][col].setBackground(Color.WHITE);
                board[row][col].setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

                // Diferenciar bloques de 3x3 con bordes más gruesos
                if (row == 0 || row == 3 || row == 6) {
                    board[row][col].setBorder(new MatteBorder(2, 1, 1, 1, Color.BLACK));
                    if (col == 3|| col == 6) {
                        board[row][col].setBorder(new MatteBorder(2, 2, 1, 1, Color.BLACK));
                    }
                    if (col == 2 || col == 5) {
                        board[row][col].setBorder(new MatteBorder(2, 1, 1, 2, Color.BLACK));
                    }
                }
                if (row == 2 || row == 5 || row == 8) {
                    board[row][col].setBorder(new MatteBorder(1, 1, 2, 1, Color.BLACK));
                    if (col == 3|| col == 6) {
                        board[row][col].setBorder(new MatteBorder(1, 2, 2, 1, Color.BLACK));
                    }
                    if (col == 2 || col == 5) {
                        board[row][col].setBorder(new MatteBorder(1, 1, 2, 2, Color.BLACK));
                    }
                }
                if (row == 1 || row == 4 || row == 7) {
                    board[row][col].setBorder(new MatteBorder(1, 1, 1, 1, Color.BLACK));
                    if ( col == 3|| col == 6) {
                        board[row][col].setBorder(new MatteBorder(1, 2, 1, 1, Color.BLACK));
                    }
                    if (col == 2 || col == 5) {
                        board[row][col].setBorder(new MatteBorder(1, 1, 1, 2, Color.BLACK));
                    }
                }
                /*if ((row + 1) % 3 == 0) {
                    board[row][col].setBorder(new MatteBorder(1, 1, 2, 1, Color.BLACK));
                }
                if ((col + 1) % 3 == 0) {
                    board[row][col].setBorder(new MatteBorder(1, 1, 1, 2, Color.BLACK));
                }*/

                gridPanel.add(board[row][col]);
            }
        }

        panel.add(gridPanel, BorderLayout.CENTER);
        return panel;
    }

    private void solveSudoku() {
        int[][] board = new int[9][9];

        // Leer valores ingresados
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                String text = inputBoard[row][col].getText();
                board[row][col] = text.isEmpty() ? 0 : Integer.parseInt(text);
            }
        }

        int steps = SudokuSolver.solve(board); // Suponiendo que la función devuelve los pasos tomados
        if (steps > 0) {
            for (int row = 0; row < 9; row++) {
                for (int col = 0; col < 9; col++) {
                    solvedBoard[row][col].setText(String.valueOf(board[row][col]));
                    solvedBoard[row][col].setForeground(new Color(0, 128, 0));
                }
            }
            log("Puzzle resuelto en " + steps + " pasos.");
        } else {
            log("No hay solución para este Sudoku.");
        }
    }


    public void GeneratePuzzle(){
        int hints = difficultySlider.getValue(); // Obtener valor del slider
        SudokuGenerator generator = new SudokuGenerator(hints);
        int[][] generatedBoard = generator.getBoard();

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (generatedBoard[row][col] == 0) {
                    inputBoard[row][col].setText("");
                } else {
                    inputBoard[row][col].setText(String.valueOf(generatedBoard[row][col]));
                    inputBoard[row][col].setForeground(Color.BLUE); // Diferenciar los números generados
                }
            }
        }

        log("Puzzle creado con " + hints + " pistas.");
    }


    // **método para limpiar los tableros y el log**
    private void clearBoards() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                inputBoard[row][col].setText("");
                solvedBoard[row][col].setText("");
                inputBoard[row][col].setForeground(Color.BLACK);
                solvedBoard[row][col].setForeground(Color.BLACK);
            }
        }
        log("¡Tableros reiniciados!"); // Borra el log
    }

    private void abrirSudoku() {
        JOptionPane.showMessageDialog(null, "Función para abrir un Sudoku");
    }

    private void guardarSudoku() {
        JOptionPane.showMessageDialog(null, "Función para guardar el Sudoku");
    }

    private void cambiarModoOscuro(boolean enabled) {
        isModoOscuro = enabled;

        Color fondo = enabled ? new Color(40, 40, 40) : new Color(220, 220, 220);
        Color texto = enabled ? Color.WHITE : Color.BLACK;
        Color borde = enabled ? Color.DARK_GRAY : Color.BLACK;

        // Cambiar colores de fondo y texto
        frame.getContentPane().setBackground(fondo);
        logArea.setBackground(fondo);
        logArea.setForeground(texto);
        folderLabel.setForeground(texto);

        for (JTextField[] row : inputBoard) {
            for (JTextField cell : row) {
                cell.setBackground(fondo);
                cell.setForeground(texto);
                cell.setBorder(BorderFactory.createLineBorder(borde, 1));
            }
        }
        for (JTextField[] row : solvedBoard) {
            for (JTextField cell : row) {
                cell.setBackground(fondo);
                cell.setForeground(texto);
                cell.setBorder(BorderFactory.createLineBorder(borde, 1));
            }
        }

        // Guardar configuración
        saveConfig();
    }


    private void restaurarVista() {
        JOptionPane.showMessageDialog(null, "Restaurando vista...");
    }

    private void setFullscreen() {
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void restaurarVentana() {
        frame.setExtendedState(JFrame.NORMAL);
    }

    private void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(null, "Sudoku Solver\nVersión 1.0\nDesarrollado por Alvaro Redondo",
                                 "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAtajosTeclado() {
        JOptionPane.showMessageDialog(null, "Atajos de teclado:\n- Ctrl+O: Abrir\n- Ctrl+S: Guardar\n- F11: Pantalla completa");
    }
    private void printSudoku() {
        if (isBoardEmpty(inputBoard)) {
            log("⚠ No se puede guardar un Sudoku vacío.");
            return;
        }

        boolean includeSolution = includeSolutionCheckbox.isSelected();
        SudokuPDFGenerator pdfGenerator = new SudokuPDFGenerator(inputBoard, solvedBoard, includeSolution, defaultSaveFolder);
        pdfGenerator.generatePDF();
        log("✅ Sudoku guardado en " + defaultSaveFolder.getAbsolutePath());
    }

    private boolean isBoardEmpty(JTextField[][] board) {
        for (JTextField[] row : board) {
            for (JTextField cell : row) {
                if (!cell.getText().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    private void log(String message) {
        logArea.append(message + "\n");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}