package edu.ics211.h09;

import java.util.ArrayList;

/**
 * Class for recursively finding a solution to a Hexadecimal Sudoku problem.
 *
 * @author Biagioni, Edoardo, Cam Moore date August 5, 2016 missing solveSudoku, to be implemented by the students in
 * ICS 211
 */
public class HexadecimalSudoku {

  static int counts = 0;
  /**
   * Find an assignment of values to sudoku cells that makes the sudoku valid.
   *
   * @param sudoku the sudoku to be solved.
   * @return whether a solution was found if a solution was found, the sudoku is filled in with the solution if no
   * solution was found, restores the sudoku to its original value.
   */
  public static boolean solveSudoku(int[][] sudoku) {
    // TODO: Implement this method recursively. You may use a recursive
    // helper method.
    // throw new UnsupportedOperationException("solveSudoku not
    // implemented.");

    /*
     * Credit: https://spin.atomicobject.com/2012/06/18/solving-sudoku-in-c-with-recursive-backtracking/
     * 
     * This post was *massively* helpful for understanding this problem.
     * I can definitely say that even though I don't have a great grasp
     * of it yet, this homework has definitely given me a lot of appreciation
     * for seeing firsthand how recursion makes complex problems much simpler.
     * 
     * That being said though, debugging recursion is a pain, especially
     * near the end when it has like 80 frames on the stack, ha.
     */
	  if (sudoku == null)
	  {
		  // Invalid sudoku
		  return false;
	  }
	  
	  int rowlength = 0;
	  int collength = 0;
    
	  for (int i = 0; i < sudoku.length; i++)
	  {
		  rowlength++;
	  }
	  for (int i = 0; i < sudoku[0].length; i++)
	  {
		  collength++;
	  }
	  
	  if (rowlength != collength)
	  {
		  // sudoku length and width uneven
		  return false;
	  }
	  Double check = Math.sqrt(rowlength);
	  check = Math.floor(check);
	  if (rowlength - (check * check) != 0.0)
	  {
		  // Not a perfect square, not required to solve
		  return false;
	  }

    return solveSudoku(sudoku, 0, 0, rowlength, collength);
  }

  /**
   * Literally just the above method but with row and column parameters, to specify
   * which cell the method should look at. It works by looking at a cell, checking
   * whether it's already given, and if not, calculates all possible values
   * that could fulfill the cell. It loops through the list and tries to put one
   * in, then calls itself on the next cell. If the method ever reaches a cell with
   * 0 viable canidates, then it must have made a mistake somewhere, so it backtracks
   * back until it reaches a cell with another possible alternative, which it then
   * substitutes and continues onward again with.
   * 
   * @param row the row to look at.
   * @param column the column to look at.
   */
  public static boolean solveSudoku(int[][] sudoku, int row, int column, int maxrow, int maxcol) {
    if (row >= maxrow) {
      // We've reached every row in the puzzle, since
      // row 15 is the last one, so our base case
      // returns true to show the puzzle is solved
      //
      // Changed to >= just in case it somehow
      // overflows
      return true;
    }
    counts++;
    if (counts > 10000000) {
      System.out.print(toString(sudoku, false));
      counts = 0;
    }

    if (sudoku[row][column] != -1) {
      // Preset value, skip over
      if (column >= maxcol - 1) {
        // Skip down a row
        if (solveSudoku(sudoku, row + 1, 0, maxrow, maxcol) == true) {
          return true;
        }
      } else {
        // move to the next cell
        if (solveSudoku(sudoku, row, column + 1, maxrow, maxcol)) {
          return true;
        }
      }
      // Something down the line returned false
      // So just pass a false back to our original caller
      return false;
    }

    ArrayList<Integer> values = legalValues(sudoku, row, column);
    for (int i : values) {
      // Try setting every value and then recursing onwards
      // If a value fails, whichever if branch is called will
      // fail, and so this instance will return false. Since most
      // every call of this function is itself one of the branchs, this
      // will cause them to backtrack to the next value in values.
      //
      // Brilliant!
      sudoku[row][column] = i;
      if (column == maxcol - 1) {
        // Skip down a row
        if (solveSudoku(sudoku, row + 1, 0, maxrow, maxcol) == true) {
          return true;
        }
      } else {
        // Skip to the next cell
        if (solveSudoku(sudoku, row, column + 1, maxrow, maxcol) == true) {
          return true;
        }
      }
      // Value failed, so reset and try a new number
      sudoku[row][column] = -1;
    }
    // None of the values worked, so return false
    return false;

  }


  /**
   * Find the legal values for the given sudoku and cell.
   *
   * @param sudoku the sudoku being solved.
   * @param row the row of the cell to get values for.
   * @param col the column of the cell.
   * @return an ArrayList of the valid values.
   */
  public static ArrayList<Integer> legalValues(int[][] sudoku, int row, int column) {

    if (row < 0 || row > 16) {
      System.out.println("Error: requested row greater than is valid for sudoku.");
      return null;
    }
    if (column < 0 || column > 16) {
      System.out.println("Error: requested row greater than is valid for sudoku.");
      return null;
    }

    if (sudoku[row][column] != -1) {
      // Cell already occupied
      return null;
    }

    ArrayList<Integer> ret = new ArrayList<Integer>();
    for (int i = 0; i < 16; i++) {
      // Check every possible number from 0 to F
      int old = sudoku[row][column];
      boolean addNum = true;
      sudoku[row][column] = i;

      for (int j = 0; j < sudoku.length; j++) {
        // Check for identical num in the row
        if (j != column && sudoku[row][j] == i) {
          addNum = false;
          sudoku[row][column] = old;
          continue;
        }
      }
      for (int j = 0; j < sudoku.length; j++) {
        // Check for identical num in the col
        if (j != row && sudoku[j][column] == i) {
          addNum = false;
          sudoku[row][column] = old;
          continue;
        }
      }
      for (int k = 0; k < 4; k++) {
        // check for identical num in the square
        for (int m = 0; m < 4; m++) {
          int testRow = (row / 4 * 4) + k; /* test this row */
          int testCol = (column / 4 * 4) + m; /* test this col */
          if (testRow != row && testCol != column && i == sudoku[testRow][testCol]) {
            addNum = false;
            sudoku[row][column] = old;
            continue;
          }
        }
      }
      if (addNum == true) {
        // Number valid so add to the list
        ret.add(i);
      }
      sudoku[row][column] = old; // restore old value
    }
    return ret;
  }


  /**
   * checks that the sudoku rules hold in this sudoku puzzle. cells that contain 0 are not checked.
   *
   * @param sudoku the sudoku to be checked.
   * @param printErrors whether to print the error found, if any.
   * @return true if this sudoku obeys all of the sudoku rules, otherwise false.
   */
  public static boolean checkSudoku(int[][] sudoku, boolean printErrors) {
    if (sudoku.length != 16) {
      if (printErrors) {
        System.out.println("sudoku has " + sudoku.length + " rows, should have 16");
      }
      return false;
    }
    for (int i = 0; i < sudoku.length; i++) {
      if (sudoku[i].length != 16) {
        if (printErrors) {
          System.out.println("sudoku row " + i + " has " + sudoku[i].length + " cells, should have 16");
        }
        return false;
      }
    }
    /* check each cell for conflicts */
    for (int i = 0; i < sudoku.length; i++) {
      for (int j = 0; j < sudoku.length; j++) {
        int cell = sudoku[i][j];
        if (cell == -1) {
          continue; /* blanks are always OK */
        }
        if ((cell < 0) || (cell > 16)) {
          if (printErrors) {
            System.out
                .println("sudoku row " + i + " column " + j + " has illegal value " + String.format("%02X", cell));
          }
          return false;
        }
        /* does it match any other value in the same row? */
        for (int m = 0; m < sudoku.length; m++) {
          if ((j != m) && (cell == sudoku[i][m])) {
            if (printErrors) {
              System.out.println(
                  "sudoku row " + i + " has " + String.format("%X", cell) + " at both positions " + j + " and " + m);
            }
            return false;
          }
        }
        /* does it match any other value it in the same column? */
        for (int k = 0; k < sudoku.length; k++) {
          if ((i != k) && (cell == sudoku[k][j])) {
            if (printErrors) {
              System.out.println(
                  "sudoku column " + j + " has " + String.format("%X", cell) + " at both positions " + i + " and " + k);
            }
            return false;
          }
        }
        /* does it match any other value in the 4x4? */
        for (int k = 0; k < 4; k++) {
          for (int m = 0; m < 4; m++) {
            int testRow = (i / 4 * 4) + k; /* test this row */
            int testCol = (j / 4 * 4) + m; /* test this col */
            if ((i != testRow) && (j != testCol) && (cell == sudoku[testRow][testCol])) {
              if (printErrors) {
                System.out.println("sudoku character " + String.format("%X", cell) + " at row " + i + ", column " + j
                    + " matches character at row " + testRow + ", column " + testCol);
              }
              return false;
            }
          }
        }
      }
    }
    return true;
  }


  /**
   * Converts the sudoku to a printable string.
   *
   * @param sudoku the sudoku to be converted.
   * @param debug whether to check for errors.
   * @return the printable version of the sudoku.
   */
  public static String toString(int[][] sudoku, boolean debug) {
    if ((!debug) || (checkSudoku(sudoku, true))) {
      String result = "";
      for (int i = 0; i < sudoku.length; i++) {
        if (i % 4 == 0) {
          result = result + "+---------+---------+---------+---------+\n";
        }
        for (int j = 0; j < sudoku.length; j++) {
          if (j % 4 == 0) {
            result = result + "| ";
          }
          if (sudoku[i][j] == -1) {
            result = result + "  ";
          } else {
            result = result + String.format("%X", sudoku[i][j]) + " ";
          }
        }
        result = result + "|\n";
      }
      result = result + "+---------+---------+---------+---------+\n";
      return result;
    }
    return "illegal sudoku";
  }
}
