package com.nhnent.protobufPacket;

import com.nhnent.protobufPacket.CommonUtil.*;

import java.util.Random;

/**
 * Rock, Paper, Scissor sample game
 */
public class RPSGame {

    private String playerID;

    private int turn;

    private GAMESTATE ENUM_gameState;
    private GAMERESULT ENUM_gameResult;

    // About Scoring
    private final int basicAddition;
    private int currentPlayerCombo;
    private int currentBotCombo;
    private int currentPlayerScore;
    private int currentBotScore;
    private int playerDecision;
    private int botDecision;

    // constructor
    public RPSGame(String playerID)
    {
        this.playerID = playerID;
        this.basicAddition = 10;
        initializeSetting();
    }

    // initialize Setting
    private void initializeSetting()
    {
        this.currentPlayerCombo = 0;
        this.currentBotCombo = 0;
        this.currentPlayerScore = 0;
        this.currentBotScore = 0;
        this.ENUM_gameState = GAMESTATE.BEFORE_GAMESTART;
        this.ENUM_gameResult = GAMERESULT.NONE;
        this.turn = 0;
    }

    public void playGame(int playerDecision)
    {
        Random rand = new Random();

        this.playerDecision = playerDecision;
        this.botDecision = rand.nextInt(3);


        setMatchResult(this.playerDecision, this.botDecision);
    }

    public void setMatchResult(int playerDecision, int botDecision)
    {
        switch (playerDecision)
        {
            case 0:
                switch (botDecision)
                {
                    case 0:
                        ENUM_gameResult = GAMERESULT.DRAW;
                        break;
                    case 1:
                        ENUM_gameResult = GAMERESULT.LOSE;
                        break;
                    case 2:
                        ENUM_gameResult = GAMERESULT.WIN;
                        break;
                }
                break;

            case 1:
                switch (botDecision)
                {
                    case 0:
                        ENUM_gameResult = GAMERESULT.WIN;
                        break;
                    case 1:
                        ENUM_gameResult = GAMERESULT.DRAW;
                        break;
                    case 2:
                        ENUM_gameResult = GAMERESULT.LOSE;
                        break;
                }
                break;

            case 2:
                switch (botDecision)
                {
                    case 0:
                        ENUM_gameResult = GAMERESULT.LOSE;
                        break;
                    case 1:
                        ENUM_gameResult = GAMERESULT.WIN;
                        break;
                    case 2:
                        ENUM_gameResult = GAMERESULT.DRAW;
                        break;
                }
                break;
        }

        turn++;

        if(ENUM_gameResult == GAMERESULT.WIN)
        {
            currentPlayerCombo++;
            currentPlayerScore += basicAddition * currentPlayerCombo;
            currentBotCombo = 0;
        }
        else if(ENUM_gameResult == GAMERESULT.LOSE)
        {
            currentBotCombo++;
            currentBotScore += basicAddition * currentBotCombo;
            currentPlayerCombo = 0;
        }
    }



    public GAMESTATE getENUM_gameState() {
        return ENUM_gameState;
    }
    public void setENUM_gameState(GAMESTATE ENUM_gameState) {
        this.ENUM_gameState = ENUM_gameState;
    }

    public int getCurrentPlayerScore() {
        return currentPlayerScore;
    }

    public int getCurrentBotScore() {
        return currentBotScore;
    }


    public int getTurn() {
        return turn;
    }

    public int getCurrentPlayerCombo() {
        return currentPlayerCombo;
    }

    public int getCurrentBotCombo() {
        return currentBotCombo;
    }

    public GAMERESULT getENUM_gameResult() {
        return ENUM_gameResult;
    }

    public int getPlayerDecision() {
        return playerDecision;
    }

    public int getBotDecision() {
        return botDecision;
    }
}
