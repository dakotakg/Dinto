package com.example.dinto

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var columns: Array<Array<EditText>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        setContentView(R.layout.activity_main)
        val rows = arrayOf("red", "yellow", "blue")
        columns = arrayOf(
            arrayOf(
                findViewById(R.id.yellowCell1),
                findViewById(R.id.blueCell2)
            ), arrayOf(
                findViewById(R.id.redCell1),
                findViewById(R.id.yellowCell2),
                findViewById(R.id.blueCell3)
            ), arrayOf(
                findViewById(R.id.redCell2),
                findViewById(R.id.yellowCell3),
                findViewById(R.id.blueCell4)
            ), arrayOf(
                findViewById(R.id.redCell3),
                findViewById(R.id.yellowCell4)
            ), arrayOf(
                findViewById(R.id.yellowCell5),
                findViewById(R.id.blueCell6)
            ), arrayOf(
                findViewById(R.id.redCell5),
                findViewById(R.id.blueCell7)
            ), arrayOf(
                findViewById(R.id.redCell6),
                findViewById(R.id.yellowCell7),
                findViewById(R.id.blueCell8)
            ), arrayOf(
                findViewById(R.id.redCell7),
                findViewById(R.id.yellowCell8),
                findViewById(R.id.blueCell9)
            ), arrayOf(
                findViewById(R.id.redCell8),
                findViewById(R.id.yellowCell9),
                findViewById(R.id.blueCell10)
            ), arrayOf(
                findViewById(R.id.redCell9),
                findViewById(R.id.yellowCell10)
            )
        )
        for (row in rows) {
            for (i in 1..10) {
                val cell: EditText = findViewById(
                    resources.getIdentifier(
                        row + "Cell" + Integer.toString(i), "id",
                        packageName
                    )
                )
                cell.setOnEditorActionListener { v, actionId, event ->
                    calcScore(v)
                    actionId != EditorInfo.IME_ACTION_DONE
                }
            }
        }
    }

    private fun getRowScore(tr: TableRow, color: String): Int {
        val rowScore: Int
        var cellValue: Int
        var cellCount = 0
        var rightMostValue = 0
        var previousValue = Int.MIN_VALUE
        var illegalLocation: Int
        var illegalMove = false
        var cellString: String
        var illegalMessage = ""
        for (i in 0 until tr.childCount) {
            val v = tr.getChildAt(i)
            if (v is EditText) {
                cellString = v.text.toString()
                if (!TextUtils.isEmpty(cellString)) {
                    cellValue = cellString.toInt()
                    if (cellValue <= previousValue) {
                        illegalMove = true
                        illegalLocation =
                            if (color === "Red" && i > 2 || color === "Yellow" && i > 4 || color === "Blue" && i > 3) {
                                i // Skip the empty cell
                            } else {
                                i + 1
                            }
                        illegalMessage =
                            "The value($cellString) on the $color row at cell $illegalLocation needs to be greater than all values to the left"
                    }
                    if (cellValue < 1 || cellValue > 18) {
                        illegalMove = true
                        illegalLocation =
                            if (color === "Red" && i > 2 || color === "Yellow" && i > 4 || color === "Blue" && i > 3) {
                                i // Skip the empty cell
                            } else {
                                i + 1
                            }
                        illegalMessage =
                            "The value($cellString) on the $color row at cell $illegalLocation needs to be between 1 and 18"
                    }
                    previousValue = cellValue
                    cellCount += 1
                    if (i == 9) {
                        rightMostValue = cellValue
                    }
                }
            }
        }
        rowScore = if (cellCount == 9) {
            rightMostValue
        } else {
            cellCount
        }
        if (illegalMove) {
            val alertDialog = AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setTitle("Illegal Move")
            alertDialog.setMessage(illegalMessage)
            alertDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL, "Ok"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
        }
        return rowScore
    }

    fun calcScore(view: View?) {
        var illegalMove = false
        var illegalMessage = ""
        var score = 0
        val redRowScore: Int
        val yellowRowScore: Int
        val blueRowScore: Int
        var totalPenalty = 0
        val red: TableRow = findViewById(R.id.redRow)
        val yellow: TableRow = findViewById(R.id.yellowRow)
        val blue: TableRow = findViewById(R.id.blueRow)
        val scoreBox: TextView = findViewById(R.id.scoreBox)
        val penaltyScore: TextView = findViewById(R.id.penaltyScore)
        val redScore: TextView = findViewById(R.id.redScore)
        val yellowScore: TextView = findViewById(R.id.yellowScore)
        val blueScore: TextView = findViewById(R.id.blueScore)
        val pentagons: MutableMap<TextView, Array<EditText>> = HashMap()
        pentagons[findViewById(R.id.pentagonScore1) as TextView] = arrayOf(
            findViewById(R.id.blueCell3),
            findViewById(R.id.yellowCell2),
            findViewById(R.id.redCell1)
        )
        pentagons[findViewById(R.id.pentagonScore2) as TextView] = arrayOf(
            findViewById(R.id.redCell2),
            findViewById(R.id.yellowCell3),
            findViewById(R.id.blueCell4)
        )
        pentagons[findViewById(R.id.pentagonScore3) as TextView] = arrayOf(
            findViewById(R.id.redCell6),
            findViewById(R.id.yellowCell7),
            findViewById(R.id.blueCell8)
        )
        pentagons[findViewById(R.id.pentagonScore4) as TextView] = arrayOf(
            findViewById(R.id.yellowCell8),
            findViewById(R.id.redCell7),
            findViewById(R.id.blueCell9)
        )
        pentagons[findViewById(R.id.pentagonScore5) as TextView] = arrayOf(
            findViewById(R.id.blueCell10),
            findViewById(R.id.yellowCell9),
            findViewById(R.id.redCell8)
        )

        // Add each row's score
        redRowScore = getRowScore(red, "Red")
        score += redRowScore
        redScore.text = Integer.toString(redRowScore)
        yellowRowScore = getRowScore(yellow, "Yellow")
        score += yellowRowScore
        yellowScore.text = Integer.toString(yellowRowScore)
        blueRowScore = getRowScore(blue, "Blue")
        score += blueRowScore
        blueScore.text = Integer.toString(blueRowScore)
        for ((pentagonLabel, columnCells) in pentagons) {
            val pentagonValue = columnCells[0].text.toString()
            var fullColumn = false
            var pentagonScore = 0
            if (!TextUtils.isEmpty(pentagonValue)) {
                fullColumn = true
                for (columnCell in columnCells) {
                    if (TextUtils.isEmpty(columnCell.text.toString())) {
                        fullColumn = false
                        break
                    }
                }
            }
            if (fullColumn) {
                // if the column is fully populated then use the value in the pentagon
                pentagonScore = pentagonValue.toInt()
            }
            pentagonLabel.text = Integer.toString(pentagonScore)
            score += pentagonScore
        }
        for (i in 1..4) {
            val penalty: ToggleButton = findViewById(
                resources.getIdentifier(
                    "penalty" + Integer.toString(i), "id",
                    packageName
                )
            )
            // Minus 5 points for penalty checked
            if (penalty.isChecked()) {
                totalPenalty += 5
            }
        }
        for (i in columns.indices) {
            val columnValues = ArrayList<Int>()
            var columnValue: Int
            val column = columns[i]
            for (aColumn in column) {
                if (!TextUtils.isEmpty(aColumn.text.toString())) {
                    columnValue = aColumn.text.toString().toInt()
                    if (columnValues.contains(columnValue)) {
                        illegalMove = true
                        illegalMessage =
                            "Column " + (i + 1) + " can't have a duplicate value(" + columnValue + ")"
                    } else {
                        columnValues.add(columnValue)
                    }
                }
            }
        }
        if (illegalMove) {
            val alertDialog = AlertDialog.Builder(this@MainActivity).create()
            alertDialog.setTitle("Illegal Move")
            alertDialog.setMessage(illegalMessage)
            alertDialog.setButton(
                AlertDialog.BUTTON_NEUTRAL, "Ok"
            ) { dialog, which -> dialog.dismiss() }
            alertDialog.show()
        }
        score -= totalPenalty
        penaltyScore.text = Integer.toString(totalPenalty)

        // Set final score
        scoreBox.text = Integer.toString(score)
    }

    fun newGame(v: View?) {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("New Game")
        alertDialog.setMessage("Are you sure you want to start a new game?")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "Yes"
        ) { dialog, which ->
            dialog.dismiss()
            reset()
        }
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "No"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()
    }

    fun reset() {
        val i = baseContext.packageManager
            .getLaunchIntentForPackage(baseContext.packageName)
        i!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    fun rollDice(v: View?) {
        val diceRoll: TextView = findViewById(R.id.diceRoll)
        val redDie: CheckBox = findViewById(R.id.redDie)
        val yellowDie: CheckBox = findViewById(R.id.yellowDie)
        val blueDie: CheckBox = findViewById(R.id.blueDie)
        var rollResult = 0
        val rand = Random()
        if (redDie.isChecked) {
            rollResult += rand.nextInt(6) + 1
        }
        if (yellowDie.isChecked) {
            rollResult += rand.nextInt(6) + 1
        }
        if (blueDie.isChecked) {
            rollResult += rand.nextInt(6) + 1
        }
        if (rollResult == 0) {
            return
        }
        diceRoll.text = Integer.toString(rollResult)
    }

    fun clickCheckBox(v: View?) {
        val rollButton: Button = findViewById(R.id.rollButton)
        val redDie: CheckBox = findViewById(R.id.redDie)
        val yellowDie: CheckBox = findViewById(R.id.yellowDie)
        val blueDie: CheckBox = findViewById(R.id.blueDie)
        var diceCount = 0
        if (redDie.isChecked) {
            diceCount++
        }
        if (yellowDie.isChecked) {
            diceCount++
        }
        if (blueDie.isChecked) {
            diceCount++
        }
        rollButton.text = "Roll " + Integer.toString(diceCount) + " Dice"
        if (diceCount == 0) {
            rollButton.isEnabled = false
        } else {
            rollButton.isEnabled = true
        }
    }

    fun showRules(v: View?) {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Rules")
        alertDialog.setMessage("Take turns being active player until Game End.\n\n" +
                "Active player rolls 1 to 3 dice, and must place the sum of the dice in 1 empty box or mark a penalty.\n\n" +
                "Other players may but don't have to use the roll on their boards.\n\n" +
                "Numbers in each row must be in ascending order from left to right.\n" +
                "There cannot be duplicates in a column.\n" +
                "You must place in a row that matches the color of one of the dice rolled (if you only roll the red " +
                "dice it must go in the red row, if you roll the red and blue dice then it must go in the red or blue rows, " +
                "if you roll all 3 dice then it can go in any row).\n\n" +
                "The game ends when 1 player finishes 2 complete rows or fills all 4 penalties, high score wins.\n\n" +
                "Completed rows score the right most value.\nUncompleted rows score 1 per filled box.\n" +
                "Each complete column with a grey square scores the value in the grey square.\n" +
                "Penalties score -5 each.")
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "Ok"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.show()
    }
}