package com.example.android.scorekeeper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    Match currMatch = new Match();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void addPoint(View v) {
        displayStatus(""); //clear status
        switch (v.getId()) {
            case R.id.btn_point_a:
                currMatch.addPoints('A');
                if (currMatch.currGame.A.point == 5) gameWon('A');
                break;
            case R.id.btn_point_b:
                currMatch.addPoints('B');
                if (currMatch.currGame.B.point == 5) gameWon('B');
                break;
        }
        displayPoints();
    }

    public void addFault(View v) {
        switch (v.getId()) {
            case R.id.btn_fault_a:
                if (currMatch.currGame.A.fault) {
                    displayStatus(currMatch.config.playerNameA + " Double Fault");
                    currMatch.addPoints('B');
                    if (currMatch.currGame.B.point == 5) gameWon('B');
                } else {
                    currMatch.currGame.A.fault = true;
                    currMatch.currGame.B.fault = false; //ain't sure if it can happen but can glitch
                    displayStatus(currMatch.config.playerNameA + " Fault");
                }

                break;
            case R.id.btn_fault_b:
                if (currMatch.currGame.B.fault) {
                    displayStatus(currMatch.config.playerNameB + " Double Fault");
                    currMatch.addPoints('A');
                    if (currMatch.currGame.A.point == 5) gameWon('A');
                } else {
                    currMatch.currGame.B.fault = true;
                    currMatch.currGame.A.fault = false;
                    displayStatus(currMatch.config.playerNameB + " Fault");
                }
        }
        displayPoints();
    }


    public void displayPoints() {
        TextView TVscoreA = (TextView) findViewById(R.id.tv_points_a);
        TextView TVscoreB = (TextView) findViewById(R.id.tv_points_b);
        TVscoreA.setText(currMatch.currGame.A.getDisplayPoints());
        TVscoreB.setText(currMatch.currGame.B.getDisplayPoints());
    }

    public void displaySet() {
        TextView[] TVsetsA = {(TextView) findViewById(R.id.tv_set1_a),
                (TextView) findViewById(R.id.tv_set2_a),
                (TextView) findViewById(R.id.tv_set3_a)
        };
        TextView[] TVsetsB = {(TextView) findViewById(R.id.tv_set1_b),
                (TextView) findViewById(R.id.tv_set2_b),
                (TextView) findViewById(R.id.tv_set3_b)
        };
        for (int i = 0; i < currMatch.config.SETS_IN_MATCH; ++i) {
            String tmpA = String.valueOf(currMatch.score[i].A.point);
            String tmpB = String.valueOf(currMatch.score[i].B.point);
            TVsetsA[i].setText(tmpA);
            TVsetsB[i].setText(tmpB);
        }
    }

    public void displayAddButtons(boolean enabled) {
        Button BTNpointA = (Button) findViewById(R.id.btn_point_a);
        Button BTNpointB = (Button) findViewById(R.id.btn_point_b);
        Button BTNfaultA = (Button) findViewById(R.id.btn_fault_a);
        Button BTNfaultB = (Button) findViewById(R.id.btn_fault_b);
        BTNpointA.setEnabled(enabled);
        BTNpointB.setEnabled(enabled);
        BTNfaultA.setEnabled(enabled);
        BTNfaultB.setEnabled(enabled);
    }

    public void displayStatus(String in) {
        TextView TVstatus = (TextView) findViewById(R.id.tv_status);
        TVstatus.setText(in);
    }


    public void resetMatch(View v) {
        displayAddButtons(true);
        displayStatus("Match reset");
        Button BTNnext = (Button) findViewById(R.id.btn_next_game);
        BTNnext.setVisibility(View.INVISIBLE);
        currMatch = new Match();
        displayPoints();
        displaySet();
    }


    public void gameWon(char player) {
        String playername = "";
        Button BTNnext = (Button) findViewById(R.id.btn_next_game);

        switch (player) {
            case 'A':
                playername = currMatch.config.playerNameA;
                break;
            case 'B':
                playername = currMatch.config.playerNameB;
                break;
        }
        displayStatus(playername + " Wins the game.");
        displayAddButtons(false);
        BTNnext.setVisibility(View.VISIBLE);
    }

    public void finishMatch() {
        displayAddButtons(false);
        String playername;
        int A = 0;
        int B = 0;

        for (int i = 0; i < (currMatch.config.SETS_IN_MATCH - 1); ++i) {
            if (currMatch.score[i].A.point > currMatch.score[i].B.point) ++A;
            else ++B;
        }
        if (A > B) playername = currMatch.config.playerNameA;
        else playername = currMatch.config.playerNameB;
        displayStatus("Game, set and Match, " + playername + " wins !");
    }

    public void nextGame(View v) {
        v.setVisibility(View.INVISIBLE);
        displayStatus(""); //clear status
        if (currMatch.currGame.A.point == 5) currMatch.addSet('A');
        else if (currMatch.currGame.B.point == 5) currMatch.addSet('B');

        if (currMatch.endMatch) finishMatch();
        else {
            displayAddButtons(true);
            displayPoints();
            displaySet();
        }
    }

    public class Proporties {
        public final int GAMES_IN_SET = 6;
        public final int SETS_IN_MATCH = 3;
        public String playerNameA = "Player A";
        public String playerNameB = "Player B";
    }

    //seems like only reasonable way i could find that allows me to reference an int.
    public class Point {
        public int point;

        Point() {
            point = 0;
        }
    }

    //Ain't sure whether this is better or should i just put it in Match and switch depending on player.
    public class GamePoint extends Point {
        boolean fault;

        GamePoint() {
            point = 0;
            fault = false;
        }

        public String getDisplayPoints() {
            switch (point) {
                case 0:
                    return "Love";
                case 1:
                    return "15";
                case 2:
                    return "30";
                case 3:
                    return "40";
                case 4:
                    return "ADV";
                case 5:
                    return "GAME";
                default:
                    return "Error";
            }
        }
    }

    public class Game {
        GamePoint A;
        GamePoint B;

        Game() {
            A = new GamePoint();
            B = new GamePoint();
        }
    }

    public class Set {
        Point A;
        Point B;

        Set() {
            A = new Point();
            B = new Point();
        }
    }

    public class Match {
        Proporties config;
        Game currGame;
        int currSet;
        Set[] score;
        boolean endMatch;

        Match() {
            config = new Proporties();
            currGame = new Game();
            currSet = 0;
            score = new Set[config.SETS_IN_MATCH];
            for (int i = 0; i < config.SETS_IN_MATCH; ++i) {
                score[i] = new Set();
            }
            endMatch = false;
        }

        public void addPoints(char player) {
            GamePoint curr = new GamePoint();
            GamePoint other = curr;
            switch (player) {
                case 'A':
                    curr = currGame.A;
                    other = currGame.B;
                    break;
                case 'B':
                    curr = currGame.B;
                    other = currGame.A;
                    break;
            }
            curr.fault = false;
            other.fault = false;
            if (curr.point < 3) ++curr.point; //no deuce, add points
            else if (curr.point == 3) { //potential deuce
                if (other.point < 3) {
                    ++curr.point;
                    ++curr.point;
                } // no deuce, finish game
                else if (curr.point == other.point) ++curr.point; // deuce, add advantage
                else if (curr.point < other.point)
                    --other.point; // deuce, remove advantage from opponent
                else if (curr.point > other.point) ++curr.point; // add adv
            } else if (curr.point == 4) ++curr.point;
        }

        public void addSet(char player) {
            switch (player) {
                case 'A':
                    score[currSet].A.point += 1;
                    if (score[currSet].A.point >= config.GAMES_IN_SET &&
                            Math.abs(score[currSet].A.point - score[currSet].B.point) >= 2) {
                        if (currSet < (config.SETS_IN_MATCH - 1)) ++currSet;
                        else endMatch = true;
                    }
                    break;
                case 'B':
                    score[currSet].B.point += 1;
                    if (score[currSet].B.point >= config.GAMES_IN_SET &&
                            Math.abs(score[currSet].B.point - score[currSet].A.point) >= 2) {
                        if (currSet < (config.SETS_IN_MATCH - 1)) ++currSet;
                        else endMatch = true;
                    }
                    break;
            }
            if (!endMatch) currGame = new Game();
        }
    }
}