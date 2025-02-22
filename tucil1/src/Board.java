// package
package tucil1.src;

// module
import java.awt.Color;
import java.io.File;
import java.util.Random;



public class Board {
    int n, m, pieceCount;
    int[][] arr;
    boolean[][] customConfig;
    String[] color;
    Color[] actualColors;
    Piece[] pieces;
    boolean foundSolution = false;
    public final String OUTPUT_DIRECTORY = System.getProperty("user.dir") + File.separator + "test" + File.separator + "output" + File.separator;
    public String configType;

    public Board(Piece[] pieces, int n, int m, int pieceCount, String configType, boolean[][] customConfig) {
      this.n = n;
      this.m = m;
      this.pieceCount = pieceCount;
      this.configType = configType;
      this.pieces = new Piece[pieceCount+2];
      this.customConfig = customConfig;

      this.arr = new int[n+2][m+2];
      for (int i = 1; i <= n; i++) {
        for (int j = 1; j <= m; j++) {
          arr[i][j] = -1;
        }
      }

      for (int i = 1; i <= pieceCount; i++) {
        this.pieces[i] = pieces[i];
      }

      this.color = new String[30];
      this.actualColors = new Color[30];

      Random random = new Random();
      for (int i = 1; i <= 26; i++) {
          float hue = (float) i / 26 + random.nextFloat() * 0.038f;
          float saturation = 0.7f + random.nextFloat() * 0.3f;
          float brightness = 0.8f + random.nextFloat() * 0.2f;
          
          Color color = Color.getHSBColor(hue, saturation, brightness);
          this.actualColors[i] = color;
          this.color[i] = "\u001B[38;2;" + color.getRed() + ";" + color.getGreen() + ";" + color.getBlue() + "m";
      }
      
      File outputDir = new File(OUTPUT_DIRECTORY);
      if (!outputDir.exists()) {
        outputDir.mkdirs();
      }
    }

    public int get(int x, int y) {
      return arr[x][y];
    }
    
    public void set(int x, int y, int value) {
      arr[x][y] = value;
    }


    public boolean isValidPosition(int x, int y) {
      if (configType.equals("CUSTOM")) {
        return x >= 1 && x <= n && y >= 1 && y <= m && 
              arr[x][y] == -1 && 
              customConfig[x-1][y-1];
      } else {
        return x >= 1 && x <= n && y >= 1 && y <= m && arr[x][y] == -1;
      }
    }

    private boolean iteratePiece(Piece piece, int x, int y, PieceOperation action) {
      for (int i = 1; i <= piece.n; i++) {
          for (int j = 1; j <= piece.m; j++) {
              if (piece.get(i, j) && !action.apply(i, j)) {
                  return false;
              }
          }
      }
      return true;
  }

    private interface PieceOperation {
        boolean apply(int i, int j);
    }

    private boolean canPlacePiece(Piece piece, int x, int y) {
      return iteratePiece(piece, x, y, (i, j) -> isValidPosition(x + i - 1, y + j - 1));
  }

    public boolean placePiece(Piece piece, int x, int y) {
        if (!canPlacePiece(piece, x, y)) {
            return false;
        }
        
        iteratePiece(piece, x, y, (i, j) -> {
            set(x + i - 1, y + j - 1, piece.id);
            return true;
        });
        
        return true;
    }

    public void removePiece(Piece piece, int x, int y) {
      iteratePiece(piece, x, y, (i, j) -> {
          set(x + i - 1, y + j - 1, -1);
          return true;
      });
  }
}