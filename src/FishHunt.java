import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @authors Milosh Devic (20158232) et Théodore Jordan (20147067)
 */

public class FishHunt extends Application {

    public static final int largeur = 640, hauteur = 480;
    private boolean gameOver = false;//indiquera la fin d'une partie

    private Stage primaryStage;
    private AnimationTimer timer;
    private Controleur controleur;
    private TextField username;
    private ArrayList<String> listeDesParties;

    private HighScores listeScore;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        listeScore = new HighScores();
        listeDesParties = listeScore.lireFichier();

        this.primaryStage = primaryStage;
        primaryStage.setTitle("Fish Hunt");
        primaryStage.setScene(creerSceneMenu());
        primaryStage.show();
    }

    /**
     * créer la scène de menu du jeu
     * @return
     */
    private Scene creerSceneMenu() {
        VBox root = new VBox();

        BackgroundFill bgf = new BackgroundFill(Color.DARKBLUE,
                new CornerRadii(1), new Insets(0.0, 0.0, 0.0, 0.0));

        root.setBackground(new Background(bgf));

        Scene menu = new Scene(root, largeur, hauteur);

        ImageView img = new ImageView(new Image("logo.png"));
        img.setPreserveRatio(true);
        img.setFitWidth(400);

        Button nouvellePartie = new Button("Nouvelle partie");
        Button scores = new Button("Meilleurs scores");

        root.setAlignment(Pos.CENTER);
        root.getChildren().add(img);
        root.setSpacing(100);
        root.getChildren().add(nouvellePartie);
        root.setSpacing(10);
        root.getChildren().add(scores);


        nouvellePartie.setOnAction((e) -> {
            controleur = new Controleur();
            primaryStage.setScene(creerSceneGame());
        });

        scores.setOnAction((event) -> primaryStage.setScene(creerSceneHighScores()));

        return menu;
    }

    /**
     * créer la scène de jeu où se déroule la majorité des fonctionnalités du code
     * @return
     */
    private Scene creerSceneGame() {
        Pane root = new Pane();
        Scene game = new Scene(root, largeur, hauteur);

        Canvas canvas = new Canvas(largeur, hauteur);
        root.getChildren().add(canvas);
        GraphicsContext context = canvas.getGraphicsContext2D();

        game.setOnKeyPressed((value) -> {

            //pour le debug (sauf le exit)
            if (value.getCode() == KeyCode.H) { //incrémente de 1 le niveau
                controleur.augmenterNiveau();
            } else if (value.getCode() == KeyCode.J) { //incrémente de 1 le score
                controleur.augmenterScore();
            } else if (value.getCode() == KeyCode.K) { //incrémente de 1 le nbr de vies
                controleur.augmenterVie();
            }else if (value.getCode() == KeyCode.L) { //finir la partie
                this.gameOver = true;
                primaryStage.setScene(creerSceneHighScores());
                context.clearRect(0, 0, largeur, hauteur);
            } else if (value.getCode() == KeyCode.ESCAPE) {
                Platform.exit();//quitter le jeu
            }
        });


        //pour afficher le viseur
        game.setOnMouseMoved(viser -> controleur.viser(viser.getX(), viser.getY()));

        //pour tirer des "balles"
        game.setOnMouseClicked(tirer -> controleur.tirer(tirer.getX(), tirer.getY()));

        timer = new AnimationTimer() {
            private long lastTime = 0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double deltaTime = (now - lastTime) * 1e-9;

                controleur.update(deltaTime);
                controleur.draw(context);

                gameOver = controleur.fin();

                if (gameOver) {
                    primaryStage.setScene(creerSceneHighScores());
                    context.clearRect(0, 0, largeur, hauteur);
                }

                lastTime = now;
            }
        };
        timer.start();

        return game;
    }


    /**
     * créer la scène pour voir et écrire les scores des parties jouées
     * @return
     */
    private Scene creerSceneHighScores() {
        if (gameOver) {
            timer.stop();//pour pas que la partie continue même si on ne joue plus
        }

        VBox root = new VBox();
        Scene highScores = new Scene(root, largeur, hauteur);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(15);
        BackgroundFill bgf = new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(1),
                new Insets(0.0, 0.0, 0.0, 0.0));
        root.setBackground(new Background(bgf));

        Text text = new Text();
        text.setText("Meilleurs scores");
        text.setFont(Font.font("Verdana", 40));

        root.setAlignment(Pos.CENTER);
        root.getChildren().add(text);
        root.setSpacing(10);

        ListView<String> top10 = new ListView<>();
        root.getChildren().add(top10);
        root.setSpacing(10);

        HBox addScore = new HBox();
        addScore.setAlignment(Pos.CENTER);
        addScore.setSpacing(10);

        top10.getItems().setAll(addIndex(this.listeDesParties));

        if(gameOver) {
            Text nom = new Text("Votre nom :");
            this.username = new TextField();
            Text points = new Text(" a fait " + controleur.getScore() + " points! ");
            Button ajouter = new Button("Ajouter!");

            addScore.getChildren().addAll(nom, this.username, points, ajouter);
            root.getChildren().add(addScore);

            ajouter.setOnAction(input -> {
                this.listeDesParties = listeScore.ajouterScores(this.username.getText(), controleur.getScore());
                top10.getItems().setAll(addIndex(this.listeDesParties));
                ajouter.setDisable(true);
            });
        }

        Button menu = new Button("Menu");
        root.getChildren().add(menu);
        addScore.setSpacing(15);

        //retourner au menu
        menu.setOnAction(event -> primaryStage.setScene(creerSceneMenu()));

        gameOver = false;

        return highScores;
    }

    /**
     * ajouter le rang des joueurs
     * @param list
     * @return
     */
    public ArrayList<String> addIndex(ArrayList<String> list){
        ArrayList<String> index = new ArrayList<>();

        for(int i = 0; i < list.size(); i++){
            index.add("#" + (i + 1) + " - " + list.get(i));
        }

        return index;
    }
}