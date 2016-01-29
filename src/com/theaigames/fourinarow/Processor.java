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

package com.theaigames.fourinarow;

import java.util.ArrayList;
import java.util.List;

import com.theaigames.fourinarow.field.Field;
import com.theaigames.fourinarow.moves.Move;
import com.theaigames.fourinarow.moves.MoveResult;
import com.theaigames.fourinarow.player.Player;
import com.theaigames.game.player.AbstractPlayer;
import com.theaigames.game.GameHandler;

public class Processor implements GameHandler {
    
    private int mRoundNumber = 1;
    private List<Player> mPlayers;
    private List<Move> mMoves;
    private List<MoveResult> mMoveResults;
    private Field mField;
    private int mGameOverByPlayerErrorPlayerId = 0;

    public Processor(List<Player> players, Field field) {
        mPlayers = players;
        mField = field;
        mMoves = new ArrayList<Move>();
        mMoveResults = new ArrayList<MoveResult>();
        
        /* Create first move with empty field */
        Move move = new Move(mPlayers.get(0));
        MoveResult moveResult = new MoveResult(mPlayers.get(0), mField, mPlayers.get(0).getId());
        mMoves.add(move);
        mMoveResults.add(moveResult);
    }


    @Override
    public void playRound(int roundNumber) {
        for (Player player : mPlayers) {
            player.sendUpdate("round",  mRoundNumber);
            player.sendUpdate("field", mField.toString());
            if (getWinner() == null) {
                String response = player.requestMove("move");
                Move move = new Move(player);
                MoveResult moveResult = new MoveResult(player, mField, player.getId());
                if (parseResponse(response, player)) {
                    move.setColumn(mField.getLastColumn());
                    move.setIllegalMove(mField.getLastError());
                    mMoves.add(move);
                    moveResult = new MoveResult(player, mField, player.getId());
                    moveResult.setColumn(mField.getLastColumn());
                    moveResult.setIllegalMove(mField.getLastError());
                    mMoveResults.add(moveResult);
                } else {
                    move = new Move(player); moveResult = new MoveResult(player, mField, player.getId());
                    move.setColumn(mField.getLastColumn());
                    move.setIllegalMove(mField.getLastError() + " (first try)");
                    mMoves.add(move);
                    moveResult.setColumn(mField.getLastColumn());
                    moveResult.setIllegalMove(mField.getLastError() + " (first try)");
                    mMoveResults.add(moveResult);
                    player.sendUpdate("field", mField.toString());
                    response = player.requestMove("move");
                    if (parseResponse(response, player)) {
                        move = new Move(player); moveResult = new MoveResult(player, mField, player.getId());
                        move.setColumn(mField.getLastColumn());
                        mMoves.add(move);
                        moveResult.setColumn(mField.getLastColumn());
                        mMoveResults.add(moveResult);
                    } else {
                        move = new Move(player); moveResult = new MoveResult(player, mField, player.getId());
                        move.setColumn(mField.getLastColumn());
                        move.setIllegalMove(mField.getLastError() + " (second try)");
                        mMoves.add(move);
                        moveResult.setColumn(mField.getLastColumn());
                        moveResult.setIllegalMove(mField.getLastError() + " (second try)");
                        mMoveResults.add(moveResult);
                        player.sendUpdate("field", mField.toString());
                        response = player.requestMove("move");
                        if (parseResponse(response, player)) {
                            move = new Move(player); moveResult = new MoveResult(player, mField, player.getId());
                            move.setColumn(mField.getLastColumn());
                            mMoves.add(move);                           
                            moveResult.setColumn(mField.getLastColumn());
                            mMoveResults.add(moveResult);
                        } else { /* Too many errors, other player wins */
                            move = new Move(player); moveResult = new MoveResult(player, mField, player.getId());
                            move.setColumn(mField.getLastColumn());
                            move.setIllegalMove(mField.getLastError() + " (last try)");
                            mMoves.add(move);
                            moveResult.setColumn(mField.getLastColumn());
                            moveResult.setIllegalMove(mField.getLastError() + " (last try)");
                            mMoveResults.add(moveResult);
                            mGameOverByPlayerErrorPlayerId = player.getId();
                        }
                    }
                }
                
                player.sendUpdate("field", mField.toString());
                mRoundNumber++;
            }
        }
    }
    
    /**
     * Parses player response and inserts disc in field
     * @param args : command line arguments passed on running of application
     * @return : true if valid move, otherwise false
     */
    private Boolean parseResponse(String r, Player player) {
        String[] parts = r.split(" ");
        if (parts.length >= 2 && parts[0].equals("place_disc")) {
            int column = Integer.parseInt(parts[1]);
            if (mField.addDisc(column, player.getId())) {
                return true;
            }
        }
        mField.mLastError = "Unknown command";
        return false;
    }

    @Override
    public int getRoundNumber() {
        return this.mRoundNumber;
    }

    @Override
    public AbstractPlayer getWinner() {
        int winner = mField.getWinner();
        if (mGameOverByPlayerErrorPlayerId > 0) { /* Game over due to too many player errors. Look up the other player, which became the winner */
            for (Player player : mPlayers) {
                if (player.getId() != mGameOverByPlayerErrorPlayerId) {
                    return player;
                }
            }
        }
        if (winner != 0) {
            for (Player player : mPlayers) {
                if (player.getId() == winner) {
                    return player;
                }
            }
        }
        return null;
    }

    @Override
    public String getPlayedGame() {
        return "";
    }
    
    /**
     * Returns a List of Moves played in this game
     * @param args : 
     * @return : List with Move objects
     */
    public List<Move> getMoves() {
        return mMoves;
    }
    
    public Field getField() {
        return mField;
    }

    @Override
    public boolean isGameOver() {
        return (getWinner() != null || mField.isFull());
    }
}
