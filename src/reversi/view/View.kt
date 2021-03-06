package reversi.view

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import reversi.model.ChipValue.*
import reversi.model.Field
import reversi.model.Field.Companion.FIELD_SIZE
import reversi.view.Styles.Companion.CELL_SIZE
import reversi.view.Styles.Companion.CHIP_RADIUS
import reversi.view.Styles.Companion.rec
import reversi.view.Styles.Companion.WINDOW_SIZE
import tornadofx.*
import tornadofx.View

class View : View("Reversi") {
    private val field = Field()

    private val buttons = List(FIELD_SIZE) { MutableList(FIELD_SIZE) { StackPane() } }
    private var status: Label? = null

    override val root = borderpane {
        top {
            menubar {
                menu("Menu") {
                    item("New game", "N").action {
                        field.restart()
                        update()
                    }
                    item("Exit", "Esc").action { this@View.close() }
                }
            }
        }
        center {
            gridpane {
                alignment = Pos.CENTER
                prefHeight = WINDOW_SIZE
                prefWidth = WINDOW_SIZE

                for (row in 0 until FIELD_SIZE)
                    row {
                        for (column in 0 until FIELD_SIZE) {
                            val button = stackpane {
                                addClass(rec)
                                update()

                                setOnMouseClicked {
                                    if (field.getCell(row, column) == OCCUPIABLE)
                                        field.makeTurn(row, column)

                                    update()
                                }
                            }
                            buttons[row][column] = button
                        }
                    }
            }
        }
        bottom {
            status = label("Score:  Black: 2, White: 2\t\t\t Player black's turn")
        }
    }

    private fun update() {
        for (x in 0 until FIELD_SIZE)
            for (y in 0 until FIELD_SIZE) {
                val cell = field.getCell(x, y)
                buttons[x][y].apply {
                    rectangle(height = CELL_SIZE, width = CELL_SIZE) {
                        when (cell) {
                            BLACK -> circle(radius = CHIP_RADIUS) { fill = Color.BLACK }
                            WHITE -> circle(radius = CHIP_RADIUS) { fill = Color.ANTIQUEWHITE }
                            EMPTY -> {
                            }
                            OCCUPIABLE -> parent.onHover { hovering ->
                                fillProperty().animate(
                                    if (hovering) Color.LIGHTGREEN else Color.WHITE,
                                    100.millis
                                )
                            }
                        }
                        fill = Color.WHITE
                    }
                }
            }

        val score = field.blackAndWhiteScore()
        status?.text = if (field.hasFreeCells())
            "Score:  Black: ${score.first}, White: ${score.second}\t\t\t " +
                    "Player ${if (field.getCurrentPlayer() == BLACK) "black" else "white"}'s turn"
        else
            "Game is finished.\t" +
                    "Winner is player ${if (score.first > score.second) "Black" else "White"}\t\t " +
                    "Score:  Black: ${score.first}, White: ${score.second}"
    }
}

