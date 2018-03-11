package org.alien8.ship;

public class ShipIceTest {
  public static void main(String[] args) {
    boolean[][] iceGrid = new boolean[5][5];
    for (int y = 0; y < 5; y++) {
      for (int x = 0; x < 5; x++) {
        iceGrid[x][y] = false;
      }
    }

    iceGrid[1][2] = true;
    iceGrid[2][1] = true;
    iceGrid[2][2] = true;
    iceGrid[3][2] = true;
    iceGrid[2][3] = true;
    iceGrid[4][2] = true;
    iceGrid[2][4] = true;

    int curX = 2;
    int curY = 2;

    System.out.println(findDiff(iceGrid, curX, curY, 1, 0));
  }

  private static int findDiff(boolean[][] iceGrid, int x, int y, int i, int j) {
    int diff = 0;
    try {
      while (iceGrid[x][y]) {
        diff++;
        x += i;
        y += j;
      }
    } catch (ArrayIndexOutOfBoundsException e) {
      // This could be caused by attempting to index outside of the map
      System.out.println("exception");
      return diff;
    }
    // Return the difference anyway
    return diff;
  }
}
