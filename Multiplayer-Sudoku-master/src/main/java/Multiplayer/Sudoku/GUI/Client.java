package Multiplayer.Sudoku.GUI;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Client extends Application {
    @Override
    public void start(Stage primaryStage) {
        GridPane board = new GridPane();

        PseudoClass right = PseudoClass.getPseudoClass("right");
        PseudoClass bottom = PseudoClass.getPseudoClass("bottom");


        for (int col = 0; col < 9; col++) {
            for (int row = 0; row < 9; row++) {
                StackPane cell = new StackPane();
                cell.getStyleClass().add("cell");
                cell.pseudoClassStateChanged(right, col == 2 || col == 5);
                cell.pseudoClassStateChanged(bottom, row == 2 || row == 5);

                int value = 1;
                
                cell.getChildren().add(createTextField(value));

                board.add(cell, col, row);
            }
        }

        Scene scene = new Scene(board);
        scene.getStylesheets().add("sudoku.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private TextField createTextField(int value) {
        TextField textField = createTextField();
        textField.setText(Integer.toString(value));
        
        return textField;
    }

    private TextField createTextField() {
        TextField textField = new TextField();

        // restrict input to integers:
        textField.setTextFormatter(new TextFormatter<Integer>(c -> {
            if (c.getControlNewText().matches("\\d?")) {
                return c ;
            } else {
                return null ;
            }
        }));
        
        return textField ;
    }

    public static void main(String[] args) {
        launch(args);
    }
}