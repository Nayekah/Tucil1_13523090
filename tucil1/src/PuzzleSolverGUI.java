// package
package tucil1.src;

// javafx
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.animation.PauseTransition;
import javafx.scene.Node;

// modules
import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class PuzzleSolverGUI extends Application {
    private int boardHeight;
    private int boardWidth;
    private int pieceCount;
    private String configType;
    private boolean[][] customConfig;
    private Piece[] pieces;
    private List<boolean[][]> pieceDesigns = new ArrayList<>();
    private int currentPieceIndex = 0;
    private Board board;
    private GridPane solutionGrid;
    private Label statusLabel;
    private long solveStartTime;
    private long solutionTimes;
    private int iterationCount;

    @Override
    public void start(Stage primaryStage) {
        showInputMethodDialog(primaryStage);
    }
    private void showInputMethodDialog(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Puzzle Solver");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Choose input method:");
        subtitleLabel.setStyle("-fx-font-size: 16px;");

        Button manualButton = new Button("Manual Input");
        Button fileButton = new Button("Load from File");

        manualButton.setStyle("-fx-min-width: 150px;");
        fileButton.setStyle("-fx-min-width: 150px;");

        manualButton.setOnAction(e -> showInitialDialog(primaryStage));
        fileButton.setOnAction(e -> handleFileInput(primaryStage));

        layout.getChildren().addAll(titleLabel, subtitleLabel, manualButton, fileButton);

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Puzzle Solver - Input Method");
        primaryStage.show();
    }

    private void handleFileInput(Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Puzzle File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );

        File file = fileChooser.showOpenDialog(primaryStage);
        if (file != null) {
            try {
                InputReader reader = new InputReader(file.getAbsolutePath());
                reader.readInput();

                boardHeight = reader.getBoardHeight();
                boardWidth = reader.getBoardWidth();
                pieceCount = reader.getPieceCount();
                configType = reader.getConfigType();
                customConfig = reader.getCustomConfig();

                pieces = reader.processPieces();
                pieceDesigns = new ArrayList<>();
                for (int i = 1; i <= pieceCount; i++) {
                    boolean[][] layout = new boolean[pieces[i].n][pieces[i].m];
                    for (int row = 0; row < pieces[i].n; row++) {
                        for (int col = 0; col < pieces[i].m; col++) {
                            layout[row][col] = pieces[i].arr[row + 1][col + 1];
                        }
                    }
                    pieceDesigns.add(layout);
                }

                showSolvingView(primaryStage);

            } catch (Exception e) {
                showAlert("File Error", "Error reading file: " + e.getMessage());
                showInputMethodDialog(primaryStage);
            }
        }
    }

    private void showInitialDialog(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        grid.setAlignment(Pos.CENTER);

        TextField heightField = new TextField("0");
        TextField widthField = new TextField("0");
        TextField pieceCountField = new TextField("0");
        ComboBox<String> configTypeBox = new ComboBox<>();
        configTypeBox.getItems().addAll("DEFAULT", "CUSTOM");
        configTypeBox.setValue("DEFAULT");

        grid.add(new Label("Height:"), 0, 0);
        grid.add(heightField, 1, 0);
        grid.add(new Label("Width:"), 0, 1);
        grid.add(widthField, 1, 1);
        grid.add(new Label("Number of Pieces:"), 0, 2);
        grid.add(pieceCountField, 1, 2);
        grid.add(new Label("Configuration Type:"), 0, 3);
        grid.add(configTypeBox, 1, 3);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button backButton = new Button("Back");
        Button submitButton = new Button("Next");
        
        buttonBox.getChildren().addAll(backButton, submitButton);
        grid.add(buttonBox, 1, 4);

        Label titleLabel = new Label("Puzzle Solver Setup");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        VBox layout = new VBox(20);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.getChildren().addAll(titleLabel, grid);

        Scene scene = new Scene(layout, 400, 350);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Puzzle Solver - Initial Setup");
        primaryStage.show();

        backButton.setOnAction(e -> showInputMethodDialog(primaryStage));

        submitButton.setOnAction(e -> {
            try {
                boardHeight = Integer.parseInt(heightField.getText());
                boardWidth = Integer.parseInt(widthField.getText());
                pieceCount = Integer.parseInt(pieceCountField.getText());
                configType = configTypeBox.getValue();
                
                if (boardHeight <= 0 || boardWidth <= 0 || pieceCount <= 0) {
                    showAlert("Invalid Input", "All dimensions must be positive numbers.");
                    return;
                }
                
                pieces = new Piece[pieceCount + 1];
                
                for (int i = 0; i < pieceCount; i++) {
                    pieceDesigns.add(null);
                }
                
                if (configType.equals("CUSTOM")) {
                    showCustomConfigDialog(primaryStage);
                } else {
                    customConfig = null;
                    showPieceSelectorView(primaryStage);
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid Input", "Please enter valid numbers.");
            }
        });
    }

    private void showCustomConfigDialog(Stage primaryStage) {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Custom Board Configuration");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label instructionLabel = new Label("Click cells to mark valid positions (blue)");
        
        GridPane configGrid = new GridPane();
        configGrid.setHgap(2);
        configGrid.setVgap(2);
        configGrid.setAlignment(Pos.CENTER);

        customConfig = new boolean[boardHeight][boardWidth];
        Rectangle[][] cells = new Rectangle[boardHeight][boardWidth];

        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                Rectangle cell = new Rectangle(30, 30);
                cell.setFill(Color.WHITE);
                cell.setStroke(Color.BLACK);
                final int row = i;
                final int col = j;
                
                cell.setOnMouseClicked(e -> {
                    customConfig[row][col] = !customConfig[row][col];
                    cell.setFill(customConfig[row][col] ? Color.LIGHTBLUE : Color.WHITE);
                });
                
                cells[i][j] = cell;
                configGrid.add(cell, j, i);
            }
        }

        Button nextButton = new Button("Next");
        Button fillAllButton = new Button("Fill All");
        Button clearAllButton = new Button("Clear All");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(fillAllButton, clearAllButton, nextButton);
        
        fillAllButton.setOnAction(e -> {
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    customConfig[i][j] = true;
                    cells[i][j].setFill(Color.LIGHTBLUE);
                }
            }
        });
        
        clearAllButton.setOnAction(e -> {
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    customConfig[i][j] = false;
                    cells[i][j].setFill(Color.WHITE);
                }
            }
        });

        nextButton.setOnAction(e -> {
            boolean hasValidCell = false;
            for (int i = 0; i < boardHeight && !hasValidCell; i++) {
                for (int j = 0; j < boardWidth && !hasValidCell; j++) {
                    if (customConfig[i][j]) {
                        hasValidCell = true;
                    }
                }
            }
            
            if (!hasValidCell) {
                showAlert("Invalid Configuration", "Please mark at least one valid position.");
                return;
            }
            
            showPieceSelectorView(primaryStage);
        });

        mainLayout.getChildren().addAll(titleLabel, instructionLabel, configGrid, buttonBox);
        Scene scene = new Scene(mainLayout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Custom Configuration Setup");
    }
    
    private void showPieceSelectorView(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Piece Selection");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        GridPane pieceButtonsGrid = new GridPane();
        pieceButtonsGrid.setHgap(10);
        pieceButtonsGrid.setVgap(10);
        pieceButtonsGrid.setAlignment(Pos.CENTER);
        
        int columns = Math.min(5, pieceCount);
        int rows = (pieceCount + columns - 1) / columns;
        
        for (int i = 0; i < pieceCount; i++) {
            Button pieceButton = new Button("Piece " + (char)('A' + i));

            updatePieceButtonStyle(pieceButton, pieceDesigns.get(i) != null);
            
            final int pieceIndex = i;
            pieceButton.setOnAction(e -> {
                currentPieceIndex = pieceIndex;
                showPieceDesigner(primaryStage);
            });
            
            pieceButtonsGrid.add(pieceButton, i % columns, i / columns);
        }
        
        Button solveButton = new Button("Solve Puzzle");
        Button mainMenuButton = new Button("Back to Main Menu");
        solveButton.setDisable(true);

        PauseTransition checkDesignsTask = new PauseTransition(Duration.millis(100));
        checkDesignsTask.setOnFinished(e -> {
            boolean allDesigned = true;
            for (boolean[][] design : pieceDesigns) {
                if (design == null) {
                    allDesigned = false;
                    break;
                }
            }
            solveButton.setDisable(!allDesigned);
            checkDesignsTask.play();
        });
        checkDesignsTask.play();
        
        solveButton.setOnAction(e -> {
            for (int i = 0; i < pieceCount; i++) {
                pieces[i+1] = new Piece(pieceDesigns.get(i).length, pieceDesigns.get(i)[0].length, pieceDesigns.get(i));
            }
            
            showSolvingView(primaryStage);
        });
        
        mainMenuButton.setOnAction(e -> {
            pieceDesigns.clear();
            pieces = null;
            currentPieceIndex = 0;
            customConfig = null;

            showInitialDialog(primaryStage);
        });

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(mainMenuButton, solveButton);
        
        layout.getChildren().addAll(titleLabel, new Label("Select a piece to design:"), 
                                   pieceButtonsGrid, buttonBox);
        Scene scene = new Scene(layout, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Piece Selection");
    }

    private void showPieceDesigner(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        GridPane pieceGrid = new GridPane();
        pieceGrid.setHgap(2);
        pieceGrid.setVgap(2);
        pieceGrid.setAlignment(Pos.CENTER);

        int gridHeight = boardHeight;
        int gridWidth = boardWidth;
        Rectangle[][] cells = new Rectangle[gridHeight][gridWidth];
        boolean[][] pieceData = new boolean[gridHeight][gridWidth];

        if (pieceDesigns.get(currentPieceIndex) != null) {
            boolean[][] existing = pieceDesigns.get(currentPieceIndex);
            int offsetRow = (gridHeight - existing.length) / 2;
            int offsetCol = (gridWidth - existing[0].length) / 2;
            for (int i = 0; i < existing.length; i++) {
                for (int j = 0; j < existing[0].length; j++) {
                    if (offsetRow + i < gridHeight && offsetCol + j < gridWidth) {
                        pieceData[offsetRow + i][offsetCol + j] = existing[i][j];
                    }
                }
            }
        }
    
        for (int i = 0; i < gridHeight; i++) {
            for (int j = 0; j < gridWidth; j++) {
                Rectangle cell = new Rectangle(30, 30);
                cell.setFill(pieceData[i][j] ? Color.LIGHTBLUE : Color.WHITE);
                cell.setStroke(Color.BLACK);
                final int row = i;
                final int col = j;
                
                cell.setOnMouseClicked(e -> {
                    pieceData[row][col] = !pieceData[row][col];
                    cell.setFill(pieceData[row][col] ? Color.LIGHTBLUE : Color.WHITE);
                });
                
                cells[i][j] = cell;
                pieceGrid.add(cell, j, i);
            }
        }
    
        Label pieceLabel = new Label("Design Piece " + (char)('A' + currentPieceIndex));
        pieceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label instructionLabel = new Label("Click cells to create a connected piece");
        Label sizeLabel = new Label(String.format("Maximum size: %dx%d", boardHeight, boardWidth));
        
        Button saveButton = new Button("Save Piece");
        Button clearButton = new Button("Clear");
        Button backButton = new Button("Back to Selection");
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(clearButton, saveButton, backButton);
        
        clearButton.setOnAction(e -> {
            for (int i = 0; i < gridHeight; i++) {
                for (int j = 0; j < gridWidth; j++) {
                    pieceData[i][j] = false;
                    cells[i][j].setFill(Color.WHITE);
                }
            }
        });
        
        saveButton.setOnAction(e -> {
            boolean[][] trimmedPiece = trimPieceData(pieceData);
            if (trimmedPiece == null) {
                showAlert("Invalid Piece", "Please design a valid piece with at least one cell.");
                return;
            }

            if (!isPieceConnected(trimmedPiece)) {
                showAlert("Invalid Piece", "All cells in the piece must be connected.");
                return;
            }
            
            pieceDesigns.set(currentPieceIndex, trimmedPiece);
            showPieceSelectorView(primaryStage);
        });
        
        backButton.setOnAction(e -> showPieceSelectorView(primaryStage));
    
        layout.getChildren().addAll(pieceLabel, instructionLabel, sizeLabel, pieceGrid, buttonBox);
        Scene scene = new Scene(layout);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Piece Designer - Piece " + (char)('A' + currentPieceIndex));
    }

    private void showSolvingView(Stage primaryStage) {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Puzzle Solution");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        board = new Board(pieces, boardHeight, boardWidth, pieceCount, configType, customConfig);

        solutionGrid = new GridPane();
        solutionGrid.setHgap(2);
        solutionGrid.setVgap(2);
        solutionGrid.setAlignment(Pos.CENTER);

        for (int i = 1; i <= boardHeight; i++) {
            for (int j = 1; j <= boardWidth; j++) {
                Rectangle cell = new Rectangle(40, 40);
                
                if (configType.equals("CUSTOM")) {
                    cell.setFill(customConfig[i-1][j-1] ? Color.WHITE : Color.LIGHTGRAY);
                } else {
                    cell.setFill(Color.WHITE);
                }
                
                cell.setStroke(Color.BLACK);
                solutionGrid.add(cell, j-1, i-1);
            }
        }

        statusLabel = new Label("Click Solve to find solution");
        statusLabel.setStyle("-fx-font-style: italic;");
        
        Button solveButton = new Button("Solve");
        Button backButton = new Button("Back to Pieces");
        Button saveButton = new Button("Save Solution");
        Button exitButton = new Button("Exit");
        saveButton.setDisable(true);
        
        exitButton.setVisible(false);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(backButton, solveButton, saveButton, exitButton);
        
        solveButton.setOnAction(e -> {
            solveButton.setDisable(true);
            backButton.setDisable(true);
            statusLabel.setText("Solving...");
            PauseTransition pause = new PauseTransition(Duration.millis(100));
            pause.setOnFinished(event -> {
                solveStartTime = System.currentTimeMillis();
                Solve.reset();
                Solve solver = new Solve(board) {
                @Override
                    public void solve() {
                    boolean[] used = new boolean[block.pieceCount + 1];
                    this.foundSolution = false;
                    this.bruteForceCount = 0;
                    long startTime = System.currentTimeMillis();

                    findSolution(1, used);

                    long endTime = System.currentTimeMillis();
                    this.solutionTime = endTime - startTime;
                    iterationCount = this.bruteForceCount;
                    
                    if (this.foundSolution) {
                        block.foundSolution = true;
                    }
                }
            };

solver.solve();
                solutionTimes = System.currentTimeMillis() - solveStartTime;
                
                updateSolutionGrid();
                
                if (board.foundSolution) {
                    statusLabel.setText(String.format("Solution found in %d ms (%d iterations)", 
                                                    solutionTimes, iterationCount));
                    saveButton.setDisable(false);
                    solveButton.setDisable(false);
                    buttonBox.getChildren().removeAll(solveButton, backButton);
                    exitButton.setVisible(true);
                } else {
                    statusLabel.setText(String.format("No solution found after %d iterations (%d ms)", 
                                                    iterationCount, solutionTimes));
                }
                
                backButton.setDisable(false);
                solveButton.setDisable(false);
            });
            pause.play();
        });
        
        backButton.setOnAction(e -> showPieceSelectorView(primaryStage));
        
        exitButton.setOnAction(e -> {
            Alert confirmExit = new Alert(Alert.AlertType.CONFIRMATION);
            confirmExit.setTitle("Confirm Exit");
            confirmExit.setHeaderText(null);
            confirmExit.setContentText("Are you sure you want to exit the application?");

            confirmExit.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Platform.exit();
                }
            });
        });

        saveButton.setOnAction(e -> {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Save Solution");
            
            ButtonType textButtonType = new ButtonType("As Text", ButtonBar.ButtonData.LEFT);
            ButtonType imageButtonType = new ButtonType("As Image", ButtonBar.ButtonData.RIGHT);
            ButtonType cancelButtonType = ButtonType.CANCEL;
            
            dialog.getDialogPane().getButtonTypes().addAll(textButtonType, imageButtonType, cancelButtonType);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == textButtonType) {
                    return "text";
                } else if (dialogButton == imageButtonType) {
                    return "image";
                }
                return null;
            });
            
            dialog.getDialogPane().setContent(new Label("Choose save format:"));
            
            dialog.showAndWait().ifPresent(result -> {
                String filename = "solution_" + boardHeight + "x" + boardWidth;
                if (result.equals("text")) {
                    saveToText(filename + ".txt");
                } else if (result.equals("image")) {
                    saveToImage(filename + ".png");
                }
            });
        });

        layout.getChildren().addAll(titleLabel, solutionGrid, statusLabel, buttonBox);
        Scene scene = new Scene(layout, 550, 500);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Puzzle Solution");
    }

    private javafx.scene.paint.Color convertAwtToFx(java.awt.Color awtColor) {
        return javafx.scene.paint.Color.rgb(
            awtColor.getRed(),
            awtColor.getGreen(),
            awtColor.getBlue(),
            awtColor.getAlpha() / 255.0
        );
    }

    private void updateSolutionGrid() {
        for (int i = 1; i <= boardHeight; i++) {
            for (int j = 1; j <= boardWidth; j++) {
                Rectangle cell = (Rectangle) getNodeFromGridPane(solutionGrid, j-1, i-1);
                int pieceId = board.get(i, j);
                
                if (pieceId != -1) {
                    javafx.scene.paint.Color color = convertAwtToFx(board.actualColors[pieceId]);
                    cell.setFill(color);
                    
                    StackPane stack = new StackPane();
                    stack.getChildren().add(cell);

                    Text text = new Text(String.valueOf((char)('A' + (pieceId - 1) % 26)));
                    text.setFill(getContrastingColor(color));
                    stack.getChildren().add(text);

                    GridPane.setConstraints(stack, j-1, i-1);
                    if (!solutionGrid.getChildren().contains(stack)) {
                        solutionGrid.getChildren().remove(cell);
                        solutionGrid.getChildren().add(stack);
                    }
                }
            }
        }
    }
    
    private Color getContrastingColor(Color color) {
        double luminance = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private void saveToText(String filename) {
        try {
            java.io.File outputDir = new java.io.File(board.OUTPUT_DIRECTORY);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            try (java.io.FileWriter writer = new java.io.FileWriter(board.OUTPUT_DIRECTORY + filename)) {
                writer.write("Solution:\n");
                for (int i = 1; i <= board.n; i++) {
                    for (int j = 1; j <= board.m; j++) {
                        if (board.get(i, j) == -1) {
                            writer.write("  ");
                        } else {
                            writer.write(String.valueOf((char)('A' + board.get(i, j) - 1)));
                        }
                    }
                    writer.write("\n");
                }
                
                writer.write("\nTime taken: " + solutionTimes + " ms\n");
                writer.write("Iterations: " + iterationCount + " times\n");
                
                showInformation("Solution Saved", "Solution saved to " + board.OUTPUT_DIRECTORY + filename);
            }
        } catch (java.io.IOException e) {
            showAlert("Save Error", "Error saving text file: " + e.getMessage());
        }
    }

    private void saveToImage(String filename) {
        try {
            java.io.File outputDir = new java.io.File(board.OUTPUT_DIRECTORY);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            
            final int CELL_SIZE = 50;
            final int MARGIN = 20;
            final int TEXT_MARGIN = 40;
            
            int width = board.m * CELL_SIZE + 2 * MARGIN;
            int height = board.n * CELL_SIZE + 2 * MARGIN + TEXT_MARGIN;
            
            java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
                width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
            java.awt.Graphics2D g2d = image.createGraphics();

            g2d.setColor(java.awt.Color.WHITE);
            g2d.fillRect(0, 0, width, height);

            for (int i = 1; i <= board.n; i++) {
                for (int j = 1; j <= board.m; j++) {
                    int x = MARGIN + (j-1) * CELL_SIZE;
                    int y = MARGIN + (i-1) * CELL_SIZE;
                    
                    if (board.get(i, j) != -1) {
                        g2d.setColor(board.actualColors[board.get(i, j)]);
                        g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                        
                        g2d.setColor(java.awt.Color.BLACK);
                        g2d.drawString(String.valueOf((char)('A' + board.get(i, j) - 1)), 
                                     x + CELL_SIZE/2 - 5, y + CELL_SIZE/2 + 5);
                    }
                    
                    g2d.setColor(java.awt.Color.BLACK);
                    g2d.setStroke(new java.awt.BasicStroke(1));
                    g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
                }
            }

            g2d.setColor(java.awt.Color.BLACK);
            g2d.setStroke(new java.awt.BasicStroke(2));
            g2d.drawRect(MARGIN, MARGIN, board.m * CELL_SIZE, board.n * CELL_SIZE);

            g2d.drawString("Time taken: " + solutionTimes + " ms", 
                          MARGIN, height - TEXT_MARGIN/2);
            g2d.drawString("Iterations: " + iterationCount, 
                          MARGIN + width/2, height - TEXT_MARGIN/2);
            
            g2d.dispose();
            
            java.io.File outputFile = new java.io.File(board.OUTPUT_DIRECTORY + filename);
            javax.imageio.ImageIO.write(image, "png", outputFile);
            
            showInformation("Solution Saved", "Solution saved to " + outputFile.getAbsolutePath());
        } catch (java.io.IOException e) {
            showAlert("Save Error", "Error saving image: " + e.getMessage());
        }
    }

    private boolean[][] trimPieceData(boolean[][] data) {
        int minRow = data.length, maxRow = -1;
        int minCol = data[0].length, maxCol = -1;
        boolean hasTrue = false;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                if (data[i][j]) {
                    hasTrue = true;
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        if (!hasTrue) return null;

        boolean[][] trimmed = new boolean[maxRow - minRow + 1][maxCol - minCol + 1];
        for (int i = minRow; i <= maxRow; i++) {
            for (int j = minCol; j <= maxCol; j++) {
                trimmed[i - minRow][j - minCol] = data[i][j];
            }
        }

        return trimmed;
    }
    
    private boolean isPieceConnected(boolean[][] piece) {
        if (piece == null || piece.length == 0) return false;
        
        int n = piece.length;
        int m = piece[0].length;
        boolean[][] visited = new boolean[n][m];
        
        int startI = -1, startJ = -1;
        outer:
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (piece[i][j]) {
                    startI = i;
                    startJ = j;
                    break outer;
                }
            }
        }
        
        if (startI == -1) return false;

        dfs(piece, visited, startI, startJ, n, m);
        
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (piece[i][j] && !visited[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private void dfs(boolean[][] piece, boolean[][] visited, int i, int j, int n, int m) {
        if (i < 0 || i >= n || j < 0 || j >= m || !piece[i][j] || visited[i][j]) {
            return;
        }
        
        visited[i][j] = true;
        
        dfs(piece, visited, i-1, j, n, m); // up
        dfs(piece, visited, i+1, j, n, m); // down
        dfs(piece, visited, i, j-1, n, m); // left
        dfs(piece, visited, i, j+1, n, m); // right
    }

    private void updatePieceButtonStyle(Button button, boolean isDesigned) {
        if (isDesigned) {
            button.setStyle("-fx-background-color: #90ee90;");
        } else {
            button.setStyle("");
        }
    }
    
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showInformation(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}