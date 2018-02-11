package com.example.dakot.dinto;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MainActivity extends AppCompatActivity {
    private EditText[][] columns;

    //TODO:
    // UI Overhaul
    // Add Invalid Move Checks for columns

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
        setContentView(R.layout.activity_main);
        String[] rows = new String[] { "red", "yellow", "blue" };
        this.columns = new EditText[][] {
                {
                        findViewById(R.id.yellowCell1),
                        findViewById(R.id.blueCell2)
                },
                {
                        findViewById(R.id.redCell1),
                        findViewById(R.id.yellowCell2),
                        findViewById(R.id.blueCell3)
                },
                {
                        findViewById(R.id.redCell2),
                        findViewById(R.id.yellowCell3),
                        findViewById(R.id.blueCell4)
                },
                {
                        findViewById(R.id.redCell3),
                        findViewById(R.id.yellowCell4),
                },
                {
                        findViewById(R.id.yellowCell5),
                        findViewById(R.id.blueCell6)
                },
                {
                        findViewById(R.id.redCell5),
                        findViewById(R.id.blueCell7)
                },
                {
                        findViewById(R.id.redCell6),
                        findViewById(R.id.yellowCell7),
                        findViewById(R.id.blueCell8)
                },
                {
                        findViewById(R.id.redCell7),
                        findViewById(R.id.yellowCell8),
                        findViewById(R.id.blueCell9)
                },
                {
                        findViewById(R.id.redCell8),
                        findViewById(R.id.yellowCell9),
                        findViewById(R.id.blueCell10)
                },
                {
                        findViewById(R.id.redCell9),
                        findViewById(R.id.yellowCell10)
                },
        };

        for (String row : rows) {
            for (int i = 1; i < 11; i++) {
                EditText cell = findViewById(getResources().getIdentifier(row + "Cell" + Integer.toString(i), "id", getPackageName()));
                cell.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        calcScore(v);
                        return actionId != EditorInfo.IME_ACTION_DONE;
                    }
                });
            }
        }
    }

    private int getRowScore(TableRow tr, String color) {
        int rowScore;
        int cellValue;
        int cellCount = 0;
        int rightMostValue = 0;
        int previousValue = Integer.MIN_VALUE;
        int illegalLocation;
        boolean illegalMove = false;
        String cellString;
        String illegalMessage = "";

        for (int i = 0; i < tr.getChildCount(); i++) {
            View v = tr.getChildAt(i);
            if (v instanceof EditText) {
                cellString = ((EditText) v).getText().toString();
                if (!TextUtils.isEmpty(cellString)) {
                    cellValue = Integer.parseInt(cellString);
                    if (cellValue <= previousValue) {
                        illegalMove = true;
                        if ((color == "Red" && i > 2) || (color == "Yellow" && i > 4) || (color == "Blue" && i > 3)) {
                            illegalLocation = i; // Skip the empty cell
                        } else {
                            illegalLocation = i + 1;
                        }
                        illegalMessage = "The value(" + cellString + ") on the " + color + " row at cell " + illegalLocation + " needs to be greater than all values to the left";
                    }
                    if (cellValue < 1 || cellValue > 18) {
                        illegalMove = true;
                        if ((color == "Red" && i > 2) || (color == "Yellow" && i > 4) || (color == "Blue" && i > 3)) {
                            illegalLocation = i; // Skip the empty cell
                        } else {
                            illegalLocation = i + 1;
                        }
                        illegalMessage = "The value(" + cellString + ") on the " + color + " row at cell " + illegalLocation + " needs to be between 1 and 18";
                    }

                    previousValue = cellValue;
                    cellCount += 1;
                    if (i == 9) {
                        rightMostValue = cellValue;
                    }
                }
            }
        }

        if (cellCount == 9) {
            rowScore = rightMostValue;
        } else {
            rowScore = cellCount;
        }

        if (illegalMove) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Illegal Move");
            alertDialog.setMessage(illegalMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        return rowScore;
    }

    public void calcScore(android.view.View view) {
        boolean illegalMove = false;
        String illegalMessage = "";
        int score = 0;
        int redRowScore;
        int yellowRowScore;
        int blueRowScore;
        int totalPenalty = 0;
        TableRow red = findViewById(R.id.redRow);
        TableRow yellow = findViewById(R.id.yellowRow);
        TableRow blue = findViewById(R.id.blueRow);
        TextView scoreBox = findViewById(R.id.scoreBox);
        TextView penaltyScore = findViewById(R.id.penaltyScore);
        TextView redScore = findViewById(R.id.redScore);
        TextView yellowScore = findViewById(R.id.yellowScore);
        TextView blueScore = findViewById(R.id.blueScore);

        Map<TextView, EditText[]> pentagons = new HashMap<>();
        pentagons.put((TextView)findViewById(R.id.pentagonScore1), new EditText[] {
                findViewById(R.id.blueCell3),
                findViewById(R.id.yellowCell2),
                findViewById(R.id.redCell1)
        });
        pentagons.put((TextView)findViewById(R.id.pentagonScore2), new EditText[] {
                findViewById(R.id.redCell2),
                findViewById(R.id.yellowCell3),
                findViewById(R.id.blueCell4)
        });
        pentagons.put((TextView)findViewById(R.id.pentagonScore3), new EditText[] {
                findViewById(R.id.redCell6),
                findViewById(R.id.yellowCell7),
                findViewById(R.id.blueCell8)
        });
        pentagons.put((TextView)findViewById(R.id.pentagonScore4), new EditText[] {
                findViewById(R.id.yellowCell8),
                findViewById(R.id.redCell7),
                findViewById(R.id.blueCell9)
        });
        pentagons.put((TextView)findViewById(R.id.pentagonScore5), new EditText[] {
                findViewById(R.id.blueCell10),
                findViewById(R.id.yellowCell9),
                findViewById(R.id.redCell8)
        });

        // Add each row's score
        redRowScore = getRowScore(red, "Red");
        score += redRowScore;
        redScore.setText(Integer.toString(redRowScore));

        yellowRowScore = getRowScore(yellow, "Yellow");
        score += yellowRowScore;
        yellowScore.setText(Integer.toString(yellowRowScore));

        blueRowScore = getRowScore(blue, "Blue");
        score += blueRowScore;
        blueScore.setText(Integer.toString(blueRowScore));

        for (Map.Entry<TextView, EditText[]> entry: pentagons.entrySet()) {
            TextView pentagonLabel = entry.getKey();
            EditText [] columnCells = entry.getValue();
            String pentagonValue = columnCells[0].getText().toString();
            Boolean fullColumn = false;
            int pentagonScore = 0;

            if (!TextUtils.isEmpty(pentagonValue)) {
                fullColumn = true;
                for (EditText columnCell : columnCells) {
                    if (TextUtils.isEmpty(columnCell.getText().toString())) {
                        fullColumn = false;
                        break;
                    }
                }
            }
            if (fullColumn) {
                // if the column is fully populated then use the value in the pentagon
                pentagonScore = Integer.parseInt(pentagonValue);
            }

            pentagonLabel.setText(Integer.toString(pentagonScore));
            score += pentagonScore;
        }

        for (int i = 1; i < 5; i++) {
            Button penalty = findViewById(getResources().getIdentifier("penalty" + Integer.toString(i),"id",getPackageName()));
            // Minus 5 points for penalty checked
            if (penalty.getText().toString() == "X") {
                totalPenalty += 5;
            }
        }

        for (int i = 0; i < this.columns.length; i++) {
            ArrayList<Integer> columnValues = new ArrayList<>();
            int columnValue;
            EditText[] column = this.columns[i];
            for (EditText aColumn : column) {
                if (!TextUtils.isEmpty(aColumn.getText().toString())) {
                    columnValue = Integer.parseInt(aColumn.getText().toString());
                    if (columnValues.contains(columnValue)) {
                        illegalMove = true;
                        illegalMessage = "Column " + (i + 1) + " can't have a duplicate value(" + columnValue + ")";
                    } else {
                        columnValues.add(columnValue);
                    }
                }
            }
        }

        if (illegalMove) {
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Illegal Move");
            alertDialog.setMessage(illegalMessage);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }

        score -= totalPenalty;
        penaltyScore.setText(Integer.toString(totalPenalty));

        // Set final score
        scoreBox.setText(Integer.toString(score));
    }

    public void newGame(android.view.View v) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("New Game");
        alertDialog.setMessage("Are you sure you want to start a new game?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        reset();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alertDialog.show();
    }

    public void reset() {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage( getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    public void rollDice(android.view.View v) {
        TextView diceRoll = findViewById(R.id.diceRoll);
        CheckBox redDie = findViewById(R.id.redDie);
        CheckBox yellowDie = findViewById(R.id.yellowDie);
        CheckBox blueDie = findViewById(R.id.blueDie);
        int rollResult = 0;
        Random rand = new Random();

        if (redDie.isChecked()) {
            rollResult += rand.nextInt(6) + 1;
        }
        if (yellowDie.isChecked()) {
            rollResult += rand.nextInt(6) + 1;
        }
        if (blueDie.isChecked()) {
            rollResult += rand.nextInt(6) + 1;
        }

        if (rollResult == 0) {
            return;
        }

        diceRoll.setText(Integer.toString(rollResult));
    }

    public void clickCheckBox(android.view.View v) {
        Button rollButton = findViewById(R.id.rollButton);
        CheckBox redDie = findViewById(R.id.redDie);
        CheckBox yellowDie = findViewById(R.id.yellowDie);
        CheckBox blueDie = findViewById(R.id.blueDie);
        int diceCount = 0;

        if (redDie.isChecked()) {
            diceCount++;
        }
        if (yellowDie.isChecked()) {
            diceCount++;
        }
        if (blueDie.isChecked()) {
            diceCount++;
        }

        rollButton.setText("Roll " + Integer.toString(diceCount) + " Dice");
        if (diceCount == 0) {
            rollButton.setEnabled(false);
        } else {
            rollButton.setEnabled(true);
        }
    }

    public void penaltyClick(android.view.View b) {
        Button button = (Button) b;
        if (button.getText().toString() == "X") {
            button.setText("");
        } else {
            button.setText("X");
        }
        calcScore(b);
    }
}
