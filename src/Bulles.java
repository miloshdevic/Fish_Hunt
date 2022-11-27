import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;


public class Bulles extends Entity {

    private double rayon;

    public Bulles(double x, double y) {
        this.x = x;
        this.y = y;
        this.rayon = 10 + Math.random()*31;//entre 10px et 40px
        this.color = Color.rgb(0, 0, 255, 0.4);
        this.vy = -350 - Math.random()*101;//vitesse entre 350-450px/s
    }

    /**
     * dessiner les bulles
     * @param context
     * @param fenetreY
     */
    @Override
    public void draw(GraphicsContext context, double fenetreY) {
        double yAffiche = this.y - fenetreY;
        context.setFill(color);
        context.fillOval(x, yAffiche, rayon, rayon);
    }
}