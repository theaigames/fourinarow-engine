// Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.theaigames.fourinarow.field;

public class Field {
    
    private int[][] mBoard;
    private int mCols = 0, mRows = 0;
    public String mLastError = "";
    private int mLastColumn = 0;
    private final int INAROW = 4; /* Number of cells in a row needed for a win */
    private String mWinType = "None";
    private Disc mWinDisc;

    public Field(int columns, int rows) {
        mBoard = new int[columns][rows];
        mCols = columns;
        mRows = rows;
        clearBoard();
    }
    
    public void clearBoard() {
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                mBoard[x][y] = 0;
            }
        }
    }
    
    public void dumpBoard() {
        for (int x = 0; x < mCols; x++) {
            System.out.print("--");
        }
        System.out.print("\n");
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                System.out.print(mBoard[x][y]);
                if (x < mCols-1) {
                    System.out.print(",");
                }
            }
            System.out.print("\n");
        }
    }
    
    public static String padRight(String s, int n) {
         return String.format("%1$-" + n + "s", s);  
    }
    
    /**
     * Adds a disc to the board
     * @param args : command line arguments passed on running of application
     * @return : true if disc fits, otherwise false
     */
    public Boolean addDisc(int column, int disc) {
        mLastError = "";
        mLastColumn = column;
        if (column >= 0 && column < mCols) {
            for (int y = mRows-1; y >= 0; y--) { // From bottom column up
                if (mBoard[column][y] == 0) {
                    mBoard[column][y] = disc;
                    return true;
                }
            }
            mLastError = "Column " + column + " is full.";
        } else {
            mLastError = "Move out of bounds. (" + column + ")";
        }
        return false;
    }
    
    /**
     * Returns reason why addDisc returns false
     * @param args : 
     * @return : reason why addDisc returns false
     */
    public String getLastError() {
        return mLastError;
    }
    
    /**
     * Returns last inserted column
     * @param args : 
     * @return : last inserted column
     */
    public int getLastColumn() {
        return mLastColumn;
    }
    
    
    @Override
    /**
     * Creates comma separated String with player names for every cell.
     * @param args : 
     * @return : String with player names for every cell, or 'empty' when cell is empty.
     */
    public String toString() {
        String r = "";
        int counter = 0;
        for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                r += mBoard[x][y];
                if (counter < mRows*mCols-1) {
                    if (x == mCols-1) {
                        r += ";";
                    } else {
                        r += ",";
                    }
                }
                counter++;
            }
        }
        return r;
    }
    
    /**
     * Checks whether the field is full
     * @param args : 
     * @return : Returns true when field is full, otherwise returns false.
     */
    public boolean isFull() {
        for (int x = 0; x < mCols; x++)
          for (int y = 0; y < mRows; y++)
            if (mBoard[x][y] == 0)
              return false; // At least one cell is not filled
        // All cells are filled
        return true;
    }
    
    /**
     * Checks if there is a winner, if so, returns player id.
     * @param args : 
     * @return : Returns player id if there is a winner, otherwise returns 0.
     */
    public int getWinner() {
        /* Check for horizontal wins */
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                int n = mBoard[x][y];
                Boolean win = true;
                if (n != 0) {
                    for (int i = 0; i < INAROW; i++) {
                        if (x + i < mCols) {
                            if (n != (mBoard[x + i][y])) {
                                win = false;
                            }
                        } else {
                            win = false;
                        }
                    }
                    if (win) {
                        mWinType = "horizontal";
                        mWinDisc = new Disc(x, y);
                        return n;
                    }
                }
            }
        }
        
        /* Check for vertical wins */
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                int n = mBoard[x][y];
                Boolean win = true;
                if (n != 0) {
                    for (int i = 0; i < INAROW; i++) {
                        if (y + i < mRows) {
                            if (n != mBoard[x][y + i]) {
                                win = false;
                            }
                        } else {
                            win = false;
                        }
                    }
                    if (win) {
                        mWinType = "vertical";
                        mWinDisc = new Disc(x, y);
                        return n;
                    }
                }
            }
        }
        
        /* Check for diagonal wins */
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                int n = mBoard[x][y];
                Boolean win = true;
                if (n != 0) {
                    for (int i = 0; i < INAROW; i++) {
                        if (x - i >= 0 && y + i < mRows) {
                            if (n !=mBoard[x - i][y + i]) {
                                win = false;
                            }
                        } else {
                            win = false;
                        }
                    }
                    if (win) {
                        mWinType = "diagonal";
                        mWinDisc = new Disc(x, y);
                        return n;
                    }
                }
            }
        }
        /* Check for anti diagonal wins */
        for (int x = 0; x < mCols; x++) {
            for (int y = 0; y < mRows; y++) {
                int n = mBoard[x][y];
                Boolean win = true;
                if (n != 0) {
                    for (int i = 0; i < INAROW; i++) {
                        if (x + i < mCols && y + i < mRows) {
                            if (n != mBoard[x + i][y + i]) {
                                win = false;
                            }
                        } else {
                            win = false;
                        }
                    }
                    if (win) {
                        mWinType = "antidiagonal";
                        mWinDisc = new Disc(x, y);
                        return n;
                    }
                }
            }
        }
        return 0;
    }
    
    
    /**
     * Returns the direction of a win.
     * @param args : 
     * @return : Returns String with direction of win, or 'None' if there is no win yet.
     */
    public String getWinType() {
        return mWinType;
    }
    
    /**
     * Returns the direction of a win.
     * @param args : 
     * @return : Returns String with direction of win, or 'None' if there is no win yet.
     */
    public Disc getWinDisc() {
        return mWinDisc;
    }

    public int getNrColumns() {
        return mCols;
    }
    
    public int getNrRows() {
        return mRows;
    }
}
