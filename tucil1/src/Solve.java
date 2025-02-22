// package
package tucil1.src;

// modules
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import javax.imageio.ImageIO;



public class Solve {
    public Board block;
    public boolean foundSolution = false;
    public int bruteForceCount = 0;
    public long solutionTime;
    private final String OUTPUT_DIRECTORY = System.getProperty("user.dir") + File.separator + "test" + File.separator + "output" + File.separator;
    private static boolean isSolving = false;
    private int totalCells;
    private int filledCells;

    public Solve(Board block) {
        if (isSolving) {
            throw new IllegalStateException("Another solution is already in progress. Please reset first.");
        }
        this.block = block;
        this.filledCells = 0;
        this.totalCells = calculateTotalCells();
        File outputDir = new File(OUTPUT_DIRECTORY);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
    }

    private int calculateTotalCells() {
        int count = 0;
        if (block.configType.equals("CUSTOM")) {
            for (int i = 0; i < block.n; i++) {
                for (int j = 0; j < block.m; j++) {
                    if (block.customConfig[i][j]) count++;
                }
            }
        } else {
            count = block.n * block.m;
        }
        return count;
    }

    public static void reset() {
        isSolving = false;
    }

    public void printBlock() {
        for (int i = 1; i <= block.n; i++) {
            for (int j = 1; j <= block.m; j++) {
                if (block.get(i, j) == -1) {
                    System.out.print("  ");
                } else {
                    System.out.print(block.color[block.get(i, j)] + (char)('A' + block.get(i, j) - 1) + "\u001B[0m");
                }
            }
            System.out.println();
        }
    }

    private boolean isBoardComplete() {
        for (int i = 1; i <= block.n; i++) {
            for (int j = 1; j <= block.m; j++) {
                if (block.configType.equals("CUSTOM")) {
                    if (block.customConfig[i-1][j-1] && block.get(i, j) == -1) {
                        return false;
                    }
                } else {
                    if (block.get(i, j) == -1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void saveToText(String filename) {
        try (FileWriter writer = new FileWriter(OUTPUT_DIRECTORY + filename)) {
            writer.write("Solution:\n");
            for (int i = 1; i <= block.n; i++) {
                for (int j = 1; j <= block.m; j++) {
                    if (block.get(i, j) == -1) {
                        writer.write("  ");
                    } else {
                        writer.write(String.valueOf((char)('A' + block.get(i, j) - 1)));
                    }
                }
                writer.write("\n");
            }
            
            writer.write("\nTime taken: " + solutionTime + " ms\n");
            writer.write("Iterations: " + bruteForceCount + " times\n");
            
            System.out.println("Solution saved to " + OUTPUT_DIRECTORY + filename);
        } catch (IOException e) {
            System.out.println("Error saving text file: " + e.getMessage());
        }
    }

    public void saveToImage(String filename) {
        final int CELL_SIZE = 50;
        final int MARGIN = 20;
        final int TEXT_MARGIN = 40;
        
        int width = block.m * CELL_SIZE + 2 * MARGIN;
        int height = block.n * CELL_SIZE + 2 * MARGIN + TEXT_MARGIN;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        for (int i = 1; i <= block.n; i++) {
            for (int j = 1; j <= block.m; j++) {
                int x = MARGIN + (j-1) * CELL_SIZE;
                int y = MARGIN + (i-1) * CELL_SIZE;
                
                if (block.get(i, j) != -1) {
                    g2d.setColor(block.actualColors[block.get(i, j)]);
                    g2d.fillRect(x, y, CELL_SIZE, CELL_SIZE);
                    
                    g2d.setColor(Color.BLACK);
                    g2d.drawString(String.valueOf((char)('A' + block.get(i, j) - 1)), 
                                 x + CELL_SIZE/2 - 5, y + CELL_SIZE/2 + 5);
                }
                
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRect(x, y, CELL_SIZE, CELL_SIZE);
            }
        }

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(MARGIN, MARGIN, block.m * CELL_SIZE, block.n * CELL_SIZE);

        g2d.drawString("Time taken: " + solutionTime + " ms", 
                      MARGIN, height - TEXT_MARGIN/2);
        g2d.drawString("Iterations: " + bruteForceCount, 
                      MARGIN + width/2, height - TEXT_MARGIN/2);
        
        g2d.dispose();
        
        try {
            File outputFile = new File(OUTPUT_DIRECTORY + filename);
            ImageIO.write(image, "png", outputFile);
            System.out.println("Solution saved to " + outputFile.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("Error saving image: " + e.getMessage());
        }
    }

    private void handleSaveOptions() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nSave options:");
            System.out.println("1. Save as text file (.txt)");
            System.out.println("2. Save as image file (.png)");
            System.out.print("Enter your choice (1-2): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    String txtFilename = "solution_" + block.n + "x" + block.m + ".txt";
                    saveToText(txtFilename);
                    return;
                case "2":
                    String pngFilename = "solution_" + block.n + "x" + block.m + ".png";
                    saveToImage(pngFilename);
                    return;
                default:
                    System.out.println("Invalid input. Please enter 1 or 2.");
            }
        }
    }

    private int countPieceCells(Piece piece) {
        int count = 0;
        for (int i = 1; i <= piece.n; i++) {
            for (int j = 1; j <= piece.m; j++) {
                if (piece.get(i, j)) count++;
            }
        }
        return count;
    }

    public void findSolution(int pos, boolean[] used) {
        this.bruteForceCount++;
        
        if (foundSolution) return;

        if (filledCells == totalCells) {
            foundSolution = true;
            return;
        }

        int i = (pos - 1) / block.m + 1;
        int j = (pos - 1) % block.m + 1;

        if (i > block.n) return;

        if (block.get(i, j) != -1 || 
            (block.configType.equals("CUSTOM") && !block.customConfig[i-1][j-1])) {
            findSolution(pos + 1, used);
            return;
        }

        for (int cur_piece = 1; cur_piece <= block.pieceCount && !foundSolution; cur_piece++) {
            if (used[cur_piece]) continue;
            
            Piece piece = block.pieces[cur_piece];
            used[cur_piece] = true;

            for (int rot = 0; rot < 4 && !foundSolution; rot++) {
                for (int flip = 0; flip < 2 && !foundSolution; flip++) {
                    if (block.placePiece(piece, i, j)) {
                        int pieceCells = countPieceCells(piece);
                        filledCells += pieceCells;
                        
                        findSolution(pos + 1, used);
                        
                        if (!foundSolution) {
                            block.removePiece(piece, i, j);
                            filledCells -= pieceCells;
                        }
                    }
                    if (flip != 1) piece = piece.reflect();
                }
                if (rot != 3) piece = piece.rotate();
            }
            
            if (!foundSolution) used[cur_piece] = false;
        }
    }

    public void solve() {
        isSolving = true;
        boolean[] used = new boolean[block.pieceCount + 1];
        this.foundSolution = false;
        this.bruteForceCount = 0;
        this.filledCells = 0;
        
        long startTime = System.currentTimeMillis();
        findSolution(1, used);
        long endTime = System.currentTimeMillis();
        this.solutionTime = endTime - startTime;
        
        System.out.println("Time taken: " + this.solutionTime + " ms");
        System.out.println("Iterations: " + this.bruteForceCount);
        
        if (this.foundSolution) {
            printBlock();
            Scanner scanner = new Scanner(System.in);
            System.out.print("Save solution? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();
            
            if (response.equals("yes")) {
                handleSaveOptions();
            }
        } else {
            System.out.println("No solution found.");
        }
        
        isSolving = false;
    }
}