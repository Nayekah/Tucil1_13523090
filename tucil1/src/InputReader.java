// package
package tucil1.src;

// module
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;



public class InputReader {
    private String filePath;
    private int height;
    private int width;
    private int pieceCount;
    private String configType;
    private boolean[][] customConfig;
    private ArrayList<String> unprocessedPieces;

    public InputReader(String filePath) {
        this.filePath = filePath;
        this.unprocessedPieces = new ArrayList<>();
    }

    private void isValidConfig() {
        if (!configType.equals("DEFAULT") && !configType.equals("CUSTOM") && !configType.equals("PYRAMID")) {
            throw new IllegalArgumentException("Invalid configuration type. Must be either DEFAULT or CUSTOM or PYRAMID");
        }
    }
    
    public void readInput() {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String[] first = reader.readLine().trim().split("\\s+");

            // Dimension + banyak piece
            height = Integer.parseInt(first[0]);
            width = Integer.parseInt(first[1]);
            pieceCount = Integer.parseInt(first[2]);

            // config
            configType = reader.readLine().trim();
            isValidConfig();

            // kasus custom : terima config board
            if (configType.equals("CUSTOM")) {
                customConfig = new boolean[height][width];
                for (int i = 0; i < height; i++) {
                    String line = reader.readLine();
                    if (line == null || line.length() != width) {
                        throw new IllegalArgumentException("Invalid custom configuration matrix dimensions");
                    }
                    for (int j = 0; j < width; j++) {
                        char c = line.charAt(j);
                        if (c != '.' && c != 'X') {
                            throw new IllegalArgumentException("Invalid character in custom configuration: " + c);
                        }
                        customConfig[i][j] = (c == 'X');
                    }
                }
            }

            // Piece
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    unprocessedPieces.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        printInputData();
    }


    private void printInputData() {
        System.out.println("Board Height: " + height);
        System.out.println("Board Width: " + width);
        System.out.println("Piece Count: " + pieceCount);
        System.out.println("Configuration Type: " + configType);
        if (configType.equals("CUSTOM")) {
            System.out.println("Custom Configuration:");
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    System.out.print(customConfig[i][j] ? 'X' : '.');
                }
                System.out.println();
            }
        }
        System.out.println("Unprocessed Pieces:");
        for (String piece : unprocessedPieces) {
            System.out.println(piece);
        }
    }

    public Piece[] processPieces() {
        boolean[] isUsed = new boolean[26];
        Piece[] pieces = new Piece[pieceCount + 1];
        ArrayList<String> currentPiece = new ArrayList<>();
        int count = 1;
        
        for (int i = 0; i < unprocessedPieces.size(); i++) {
            String currentLine = unprocessedPieces.get(i);
            currentPiece.add(currentLine);
            
            // Cek perbedaan huruf
            boolean isLastLine = (i == unprocessedPieces.size() - 1);
            boolean isNextLineNewPiece = !isLastLine && 
                diff(currentLine, unprocessedPieces.get(i + 1));
            
            if (isLastLine || isNextLineNewPiece) {
                char pieceID = getCharID(currentPiece.get(0));
                validatePiece(pieceID, isUsed, count);
                
                pieces[count++] = processSinglePiece(currentPiece);
                isUsed[pieceID - 'A'] = true;
                currentPiece.clear();
            }
        }

        validatePieceCount(count - 1);
        return pieces;
    }

    private boolean diff(String current, String next) {
        char currentLetter = getCharID(current);
        try {
            char nextLetter = getCharID(next);
            return currentLetter != nextLetter;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void validatePiece(char pieceID, boolean[] isUsed, int count) {
        if (isUsed[pieceID - 'A']) {
            throw new IllegalArgumentException("Invalid Piece: Multiple pieces with same alphabet code");
        }
        if (count > pieceCount) {
            throw new IllegalArgumentException("Invalid Piece: Too many pieces");
        }
    }

    private void validatePieceCount(int count) {
        if (count != pieceCount) {
            throw new IllegalArgumentException(
                "Invalid Piece: Number of pieces does not match with the given number. " +
                "Found " + count + " pieces, expected " + pieceCount
            );
        }
    }

    private static boolean isCapitalLetter(char c) {
        return c >= 'A' && c <= 'Z';
    }

    private static char getCharID(String s) {
        for (char c : s.toCharArray()) {
            if (isCapitalLetter(c)) {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid Piece: No capital letter found");
    }

    private Piece processSinglePiece(List<String> piece) {
        int maxLength = 0;
        for (String line : piece) {
            maxLength = Math.max(maxLength, line.length());
        }
        
        int height = piece.size();
        int width = maxLength;
        boolean[][] arr = new boolean[height][width];
        int rootn = -1, rootm = -1;
    
        for (int i = 0; i < height; i++) {
            String line = piece.get(i);
            char[] chars = line.toCharArray();
            
            for (int j = 0; j < chars.length; j++) {
                if (isCapitalLetter(chars[j])) {
                    arr[i][j] = true;
                    if (rootn == -1) {
                        rootn = i;
                        rootm = j;
                    }
                }
            }
        }
    
        if (rootn == -1 || rootm == -1) {
            throw new IllegalArgumentException("Invalid Piece: Found empty piece");
        }
    
        // Remove empty space
        boolean[][] trimmedArr = trimArray(arr);
        
        isConnected(trimmedArr, trimmedArr.length, trimmedArr[0].length, 
                            findRoot(trimmedArr).getKey(), findRoot(trimmedArr).getValue());
        
        return new Piece(trimmedArr.length, trimmedArr[0].length, trimmedArr);
    }

    private Pair<Integer, Integer> findRoot(boolean[][] arr) {
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
                if (arr[i][j]) {
                    return new Pair<>(i, j);
                }
            }
        }
        throw new IllegalArgumentException("Invalid Piece: No root found");
    }

    private boolean[][] trimArray(boolean[][] original) {
        int minRow = original.length, maxRow = -1;
        int minCol = original[0].length, maxCol = -1;
        
        // cari edge case
        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                if (original[i][j]) {
                    minRow = Math.min(minRow, i);
                    maxRow = Math.max(maxRow, i);
                    minCol = Math.min(minCol, j);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }
        
        // Piece kosong
        if (maxRow == -1) {
            throw new IllegalArgumentException("Invalid Piece: Empty piece");
        }
        
        int newHeight = maxRow - minRow + 1;
        int newWidth = maxCol - minCol + 1;
        boolean[][] trimmed = new boolean[newHeight][newWidth];
        
        for (int i = 0; i < newHeight; i++) {
            for (int j = 0; j < newWidth; j++) {
                trimmed[i][j] = original[i + minRow][j + minCol];
            }
        }
        
        return trimmed;
    }

    private void isConnected(boolean[][] arr, int n, int m, int rootn, int rootm) {
        boolean[][] visited = new boolean[n][m];
        Queue<Pair<Integer, Integer>> q = new LinkedList<>();
        q.add(new Pair<>(rootn, rootm));
        visited[rootn][rootm] = true;
    
        // Kemungkinan letak piece
        int[][] directions = {
            {-1, 0},  // atas
            {1, 0},   // bawah
            {0, -1},  // kiri
            {0, 1},   // kanan
            {-1, -1}, // atas kiri
            {-1, 1},  // atas kanan
            {1, -1},  // bawah kiri
            {1, 1}    // bawah kanan
        };
    
        while (!q.isEmpty()) {
            Pair<Integer, Integer> p = q.poll();
            int x = p.getKey();
            int y = p.getValue();
    
            for (int[] dir : directions) {
                int newX = x + dir[0];
                int newY = y + dir[1];
                
                if (newX >= 0 && newX < n && newY >= 0 && newY < m && 
                    !visited[newX][newY] && arr[newX][newY]) {
                    q.add(new Pair<>(newX, newY));
                    visited[newX][newY] = true;
                }
            }
        }
    
        // cek konektivitas
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (arr[i][j] && !visited[i][j]) {
                    throw new IllegalArgumentException("Invalid Piece: Piece is not connected");
                }
            }
        }
    }

    public int getBoardHeight() { return height; }
    public int getBoardWidth() { return width; }
    public int getPieceCount() { return pieceCount; }
    public String getConfigType() { return configType; }
    public boolean[][] getCustomConfig() { return customConfig; }
}