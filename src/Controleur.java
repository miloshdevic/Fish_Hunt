import javafx.scene.canvas.GraphicsContext;

public class Controleur {
    Game game;

    public Controleur() {
        game = new Game();
    }

    void update(double deltaTime) {
        game.update(deltaTime);
    }

    void draw(GraphicsContext context) {
        game.draw(context);
    }

    void viser(double x, double y) {
        game.viser(x, y);
    }

    void tirer(double x, double y) {
        game.tirer(x, y);
    }

    void augmenterNiveau() {
        game.augmenterNiveau(game.getNiveau());
    }

    void augmenterScore() {
        game.augmenterScore();
    }

    void augmenterVie() {
        game.augmenterVie();
    }

    int getScore() {
        return game.getScore();
    }

    boolean fin() {
        return game.getFin();
    }
}