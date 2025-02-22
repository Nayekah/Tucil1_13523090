// package
package tucil1.src;

// module
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    private static final String INPUT_FOLDER = System.getProperty("user.dir") + File.separator + "test" + File.separator + "input";
    private static Scanner scanner = new Scanner(System.in);

    
    private static void displayMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Solve puzzle");
        System.out.println("2. Exit");
        System.out.print("Enter your choice (1-2): ");
    }
    
    private static void solvePuzzle() {
        System.out.print("Enter filename (without .txt extension): ");
        String filename = scanner.nextLine();
        String fullPath = INPUT_FOLDER + File.separator + filename + ".txt";
        
        try {
            File file = new File(fullPath);
            if (!file.exists()) {
                throw new FileNotFoundException("File '" + filename + ".txt' not found in the input directory.");
            }

            InputReader reader = new InputReader(fullPath);
            reader.readInput();
            
            Piece[] pieces = reader.processPieces();
            System.out.println("Processed Pieces: ");
            for (int i = 1; i <= reader.getPieceCount(); i++) {
                System.out.println("Piece " + pieces[i].id + " : ");
                pieces[i].printPiece();
            }
            
            if (reader.getConfigType().equals("CUSTOM")) {
                if (!validateCustomConfig(pieces, reader)) {
                    throw new IllegalArgumentException(
                        "Invalid Configuration: Total area of pieces does not match with the custom configuration area"
                        );
                    }
                } else if (!validatePieceArea(pieces, reader)) {
                    throw new IllegalArgumentException(
                        "Invalid Piece: Total area of pieces does not match with the default configuration area"
                        );
                    }
                    
                    Board board = new Board(pieces, reader.getBoardHeight(), reader.getBoardWidth(), 
                    reader.getPieceCount(), reader.getConfigType(), reader.getCustomConfig());
                    Solve solver = new Solve(board);
                    solver.solve();
                    
        } catch (FileNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
            System.err.println("Please make sure the file exists in: " + INPUT_FOLDER);
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
        }
    }
    
    private static boolean validateCustomConfig(Piece[] pieces, InputReader reader) {
        int totalPieceArea = calculateTotalArea(pieces, reader.getPieceCount());
        int customConfigArea = calculateCustomConfigArea(reader.getCustomConfig());
        
        System.out.println("Total piece area: " + totalPieceArea);
        System.out.println("Custom configuration area: " + customConfigArea);
        
        return totalPieceArea == customConfigArea;
    }
    
    private static int calculateCustomConfigArea(boolean[][] customConfig) {
        int area = 0;
        for (int i = 0; i < customConfig.length; i++) {
            for (int j = 0; j < customConfig[0].length; j++) {
                if (customConfig[i][j]) {
                    area++;
                }
            }
        }
        return area;
    }
    
    private static boolean validatePieceArea(Piece[] pieces, InputReader reader) {
        int totalArea = calculateTotalArea(pieces, reader.getPieceCount());
        int boardArea = reader.getBoardHeight() * reader.getBoardWidth();
        
        System.out.println("Total piece area: " + totalArea);
        System.out.println("Board area: " + boardArea);
        
        return totalArea == boardArea;
    }
    
    private static int calculateTotalArea(Piece[] pieces, int pieceCount) {
        int totalArea = 0;
        for (int i = 1; i <= pieceCount; i++) {
            for (int j = 1; j <= pieces[i].n; j++) {
                for (int k = 1; k <= pieces[i].m; k++) {
                    if (pieces[i].get(j, k)) {
                        totalArea++;
                    }
                }
            }
        }
        return totalArea;
    }
    
    public static void main(String[] args) {
        while (true) {
            displayMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    Solve.reset();
                    Piece.count = 1;
                    solvePuzzle();
                    break;
                case "2":
                    System.out.println("Exiting program...");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid input. Please enter 1 or 2.");
            }
        }
    }
}