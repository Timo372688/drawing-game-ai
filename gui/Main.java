package de.uni_hannover.hci.montagsmaler.gui;

import de.uni_hannover.hci.montagsmaler.NeuralNet.*;
import de.uni_hannover.hci.montagsmaler.NeuralNet.Datas.Data;
import javafx.application.Application;
import javafx.beans.property.StringProperty;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static de.uni_hannover.hci.montagsmaler.NeuralNet.Datas.DataReader.createTrainData;
import static de.uni_hannover.hci.montagsmaler.gui.Actions.formatImgFromImg;
import static de.uni_hannover.hci.montagsmaler.gui.Actions.scaleImgArray;

public class Main extends Application {

    double[] moreTrainData;

    // Fügt den Rundenzähler hinzu.
    int lapCounter = 1;
    Label lapCount = new Label();
    RadioButton selectedRound;
    String selectedRoundToStr;

    // Labels für die nächste Maulaufforderung und die Evaluation vom NN.
    int toDraw;
    int rightGuesses = 0;
    Label draw = new Label();
    Label guess = new Label();

    // Liste mit den Elementen, die gemalt werden können.
    SelectTerm term;

    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {

        //Trainiert das nn.
        NN myNN = new NN(1, 0.5, 28*28, 10, 10);
        Data[] trainData = createTrainData();
        myNN.train(trainData);

        // Definition der Main-Scene und des Main-Gridpanes.
        GridPane pane = new GridPane();
        pane.setBackground(new Background(new BackgroundFill(Color.color(0.0f, 0.5019608f, 0.5019608f, 0.15f), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(pane, 710, 720);

        /* Erzeugt ein 10 x 10 Gitter im Gridpane. Das Gitter wird prozentual
         * erzeugt und passt sich unterschiedlichen Fenstergrößen an.
         */
        ColumnConstraints[] col = new ColumnConstraints[8];
        RowConstraints[] row = new RowConstraints[8];

        for (int i = 0; i < col.length; i++) {
            col[i] = new ColumnConstraints();
            col[i].setPercentWidth(12.5);

            row[i] = new RowConstraints();
            row[i].setPercentHeight(12.5);
        }

        pane.getColumnConstraints().addAll(col[0], col[1], col[2], col[3], col[4], col[5], col[6], col[7]);
        pane.getRowConstraints().addAll(row[0], row[1], row[2], row[3], row[4], row[5], row[6], row[7]);

        /* Erzeugt die Fläche, auf der gemalt wird mit angepasstem Hintergrund. */
        StackPane canvasBackground = new StackPane();
        Canvas canvas = new Canvas(532, 532);
        canvasBackground.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));
        canvasBackground.getChildren().add(canvas);

        /* Erzeugt das Werkzeug zum malen. */
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        /* Malt einen Punkt, wenn die Maus gedrückt wird. */
        scene.setOnMousePressed(e -> {
            gc.beginPath();
            gc.lineTo(e.getSceneX() - 0.125 * scene.getWidth(), e.getSceneY() - 0.13 * scene.getHeight());
            gc.stroke();
        });

        /* Malt einen Strich, wenn die Maus gezogen wird. */
        scene.setOnMouseDragged(e -> {
            gc.lineTo(e.getSceneX() - 0.125 * scene.getWidth(), e.getSceneY() - 0.13 * scene.getHeight());
            gc.stroke();

        });

        pane.add(canvasBackground, 1, 1, 6,6);

        /* Buttons:
         * Der newG-Button startet eine neue Runde mit der ausgewählten Rundenanzahl.
         */
        Button newG = new Button("new\nGame");
        pane.add(newG, 0, 0, 1, 1);

        // Der clear-Button setzt das Canvas zurück, so dass eine freie Fläche zum malen entsteht.
        Button clear = new Button("clear");
        clear.setOnAction(e -> gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight()));
        pane.add(clear, 0, 7, 1, 1);

        /* Der save-Button speichert die aktuelle Canvas-Zeichnung im Programmerzeichnes pictures.
         * Das so entstandene Bild kann zum Training des NN verwendet werden.
         */
        Button save = new Button("Save");

        pane.add(save, 0,6,1,1);
        save.setOnAction(e -> {
            Actions.saveImg(canvas);
            moreTrainData = Actions.formatImg(null);
        });

        //Radiobuttons to choose the Gameset to play with
        RadioButton chooseSet1 = new RadioButton("Set 1");
        RadioButton chooseSet2 = new RadioButton("Set 2");
        RadioButton chooseSet3 = new RadioButton("Set 3");
        ToggleGroup SetChoice = new ToggleGroup();
        chooseSet1.setToggleGroup(SetChoice);
        chooseSet2.setToggleGroup(SetChoice);
        chooseSet3.setToggleGroup(SetChoice);
        VBox vBox_set = new VBox(chooseSet1, chooseSet2, chooseSet3);

        //Tickbuttons to choose the Roundnumber to play
        RadioButton roundnbr1 = new RadioButton("1");
        RadioButton roundnbr5 = new RadioButton("5");
        RadioButton roundnbr10 = new RadioButton("10");
        ToggleGroup RoundnbrChoice = new ToggleGroup();
        roundnbr1.setToggleGroup(RoundnbrChoice);
        roundnbr5.setToggleGroup(RoundnbrChoice);
        roundnbr10.setToggleGroup(RoundnbrChoice);
        VBox vBox_round = new VBox(roundnbr1, roundnbr5, roundnbr10);


        /* Labels:
         * Das lapCount-Label gibt die aktuelle Anzahl der Runden und die maximale
         * Rundenanzahl an.
         */
        pane.add(lapCount, 6, 0, 1, 1);

        Label roundNumber = new Label("Rounds");
        pane.add(roundNumber, 0,1 , 1,1);
        GridPane.setHalignment(roundNumber, HPos.CENTER);
        GridPane.setValignment(roundNumber, VPos.BOTTOM);

        pane.add(vBox_round, 0, 2,2,1);
        GridPane.setMargin(vBox_round, new Insets(5, 10, 5, 10));

        // Stellt die 3 verschiedenen Spielmodi dar.
        Label setChoice = new Label("Game set");
        pane.add(setChoice, 0, 3, 1, 1);
        GridPane.setHalignment(setChoice, HPos.CENTER);
        GridPane.setValignment(setChoice, VPos.BOTTOM);

        pane.add(vBox_set, 0, 4, 1, 1);
        GridPane.setMargin(vBox_set, new Insets(5, 10, 5, 10));


        draw = new Label("Draw: ...");
        pane.add(draw, 1, 0, 1, 1);
        guess = new Label("my guess: ...");
        pane.add(guess, 1 ,7, 1, 1);

        /* Beim Drücken vom newGame-Knopf wird das Spiel initialisiert.
         * Dem Spieler wird eine Zeichnungsaufforderung gestellt und der
         * Rundencounter, sowie der counter für die Anzahl erfolgreicher
         * Zeichnungen wird zurückgesetzt.
         */
        newG.setOnAction(e -> {
            lapCounter = 1;
            rightGuesses = 0;

            selectedRound = (RadioButton) RoundnbrChoice.getSelectedToggle();
            selectedRoundToStr = selectedRound.getText();
            lapCount.setText("Runde " + lapCounter + "/" + selectedRoundToStr);

            /* Initialisiert die Liste mit den Elementen, die gemalt werden können.
             * und speichert ein zufälliges Element.
             */
            term = new SelectTerm(10);
            toDraw = term.getRandomElement();

            // Gibt das zufällige Element das gemalt werden soll in der GUI aus.
            draw.setText("Draw: " + toDraw);

            clear.fire();
        });

        /* Beim drücken des nextDraw-Buttons wird der Rundencounter um 1 erhöht,
         * es wird der clear-Button für das Cancvas ausgelöst, sowie eine neue
         * Zeichnungsaufforderung gestellt.
         */
        Button nextDraw = new Button("next");
        pane.add(nextDraw, 7, 0, 1, 1);
        nextDraw.setOnAction(e -> {
            if (lapCounter == Integer.parseInt(selectedRoundToStr)) {
                StackPane lasLayout = new StackPane();
                Label result = new Label("Es wurden " + rightGuesses + "/" + selectedRoundToStr +" Bilder richtig erkannt.");
                lasLayout.getChildren().add(result);

                Scene lastScene = new Scene(lasLayout, 400, 400);
                Stage lastWindow = new Stage();

                lastWindow.setTitle("Congratulations!");
                lastWindow.setScene(lastScene);
                lastWindow.show();
            } else {
                // Rundenzähler
                lapCounter++;
                lapCount.setText("Runde " + lapCounter + "/" + selectedRoundToStr);

                // Zeigt ein neues Element an das gemalt werden soll.
                toDraw = term.getRandomElement();
                draw.setText("Draw: " + toDraw);

                clear.fire();
            }
        });

        /* Erlaubt es Labels und Buttons das gesamte Gritfeld auszufüllen. */
        newG.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        nextDraw.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        clear.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        draw.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        guess.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        /* Folgend wir die Aktion nach dem Absetzen der Maus definiert.
         * Das NN wertet in dem Fall die Zeichnung aus und vergleicht die
         * Auswertung mit der Zeichnungsaufforderung.
         * Im Falle einer Übereinstummg wird ein neues Fenster ausgegeben und
         * falls die maximale Rundenzahl erreicht wurde, wird das Abschiedsfenster
         * angezeigt.
         * Anschließend kann man das Programm beenden, oder ein neues Spiel starten.
         */
        scene.setOnMouseReleased( e -> {
            double[] eval = Actions.evalGuess(canvas);
            double[] scaledEval = formatImgFromImg(scaleImgArray(eval, canvas.getHeight(), canvas.getWidth()));

            if (term != null) {
                guess.setText("my guess " + myNN.out(scaledEval));

                if (myNN.out(scaledEval) == toDraw) {
                    rightGuesses++;

                    if (lapCounter == Integer.parseInt(selectedRoundToStr)) {
                        StackPane lasLayout = new StackPane();
                        Label result = new Label("Es wurden " + rightGuesses + "/" + selectedRoundToStr +" Bilder richtig erkannt.");
                        lasLayout.getChildren().add(result);

                        Scene lastScene = new Scene(lasLayout, 400, 400);
                        Stage lastWindow = new Stage();

                        lastWindow.setTitle("Congratulations!");
                        lastWindow.setScene(lastScene);
                        lastWindow.show();
                    } else {
                        StackPane secondaryLayout = new StackPane();

                        Label niceDraw = new Label("Gut gezeichnet! ;)");
                        Button next = new Button("next");

                        StackPane.setAlignment(niceDraw, Pos.CENTER);
                        StackPane.setAlignment(next, Pos.BOTTOM_CENTER);

                        secondaryLayout.getChildren().add(niceDraw);
                        secondaryLayout.getChildren().add(next);

                        Scene secondScene = new Scene(secondaryLayout, 230, 100);

                        Stage newWindow = new Stage();
                        newWindow.setTitle("Good job!");
                        newWindow.setScene(secondScene);

                        // Ausrichtung in Relation zur primaryStage.
                        newWindow.setX(primaryStage.getX() + 500);
                        newWindow.setX(primaryStage.getY() + 500);

                        newWindow.show();

                        next.setOnAction(k -> {
                            // Rundenzähler
                            lapCounter++;
                            lapCount.setText("Runde " + lapCounter + "/" + selectedRoundToStr);

                            // Zeigt ein neues Element an das gemalt werden soll.
                            toDraw = term.getRandomElement();
                            draw.setText("Draw: " + toDraw);

                            newWindow.close();
                            clear.fire();
                        });
                    }
                }
            }
        });

        primaryStage.setTitle("Montagsmaler");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
