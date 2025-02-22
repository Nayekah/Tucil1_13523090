// package
package tucil1.src;



public class Piece {
  int n, m;
  boolean[][] arr;
  int id;
  static int count = 1;

  public Piece(int n, int m, boolean arr[][]) {
    this.n = n;
    this.m = m;
    this.arr = new boolean[n+2][m+2];
    this.id = count++;

    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= m; j++) {
        this.arr[i][j] = arr[i-1][j-1];
      }
    }
  }

  public Piece(int n, int m, boolean arr[][], int id) {
    this.n = n;
    this.m = m;
    this.arr = new boolean[n+2][m+2];
    this.id = id;

    for (int i = 1; i <= n; i++) {
      for (int j = 1; j <= m; j++) {
        this.arr[i][j] = arr[i-1][j-1];
      }
    }
  }

  public void set(int x, int y, boolean value) {
    this.arr[x][y] = value;
  }

  public boolean get(int x, int y) {
    return this.arr[x][y];
  }

  public boolean isValidPosition(int x, int y) {
    return x >= 1 && x <= this.n && y >= 1 && y <= this.m;
  }

  public Piece rotate() {
    boolean[][] newArr = new boolean[this.m][this.n];
    for (int i = 0; i < this.m; i++) {
      for (int j = 0; j < this.n; j++) {
        newArr[i][j] = this.arr[this.n - j][i + 1];
      }
    }
    return new Piece(this.m, this.n, newArr, this.id);
  }

  public Piece reflect() {
    boolean[][] newArr = new boolean[this.n][this.m];
    for (int i = 0; i < this.n; i++) {
      for (int j = 0; j < this.m; j++) {
        newArr[i][j] = this.arr[i + 1][this.m - j];
      }
    }
    return new Piece(this.n, this.m, newArr, this.id);
  }

  public void printPiece() {
    for (int i = 1; i <= this.n; i++) {
      for (int j = 1; j <= this.m; j++) {
        System.out.print(this.arr[i][j] ? "#" : ".");
      }
      System.out.println();
    }
  }
}