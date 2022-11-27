import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


public class Poisson extends Entity {

    private int niveau = 1;
    private Image image;
    private Image[] images;
    protected Color color;
    private boolean capture = false;

    public Poisson() {

        images = new Image[]{
                new Image("00.png"),
                new Image("01.png"),
                new Image("02.png"),
                new Image("03.png"),
                new Image("04.png"),
                new Image("05.png"),
                new Image("06.png"),
                new Image("07.png")
        };

        double choisirImage = Math.random() * 8;//entre 0 et 7
        image = images[(int) choisirImage];

        largeur = image.getHeight() / 5;
        hauteur = image.getWidth() / 5;

        double choisirX = Math.random();
        if (choisirX < 0.5) {
            this.x = -50;
            this.vx = 100 * Math.cbrt(this.niveau) + 200;//vitesse horizontale
        } else {
            this.x = FishHunt.largeur;
            this.vx = -100 * Math.cbrt(this.niveau) - 200;//vitesse horizontale
            image = flop(image);//inverser l'image
        }

        //avoir une couleur au hasard
        this.color = Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256));
        image = colorize(image, this.color);

        double choisirY = Math.random();
        if (choisirY < 0.5) {
            this.y = (double) 4 * FishHunt.hauteur / 5;
        } else {
            this.y = (double) FishHunt.hauteur / 5;
        }

        this.ay = 100;//gravité
        this.vy = -100 - Math.random() * 101;//vitesse verticale entre 100-200px/s
    }

    /**
     * fais une mise à jour pour le poisson et ses attributs
     * @param dt Temps écoulé depuis le dernier update() en secondes
     */
    @Override
    public void update(double dt) {
        super.update(dt);
    }

    /**
     * dessiner le poisson
     * @param context
     * @param fenetreY
     */
    @Override
    public void draw(GraphicsContext context, double fenetreY) {
        if (!this.capture)
            context.drawImage(this.image, this.x, this.y, this.largeur, this.hauteur);
    }


    /**
     * Inversion horizontale d'une image.
     * @param img L'image à inverser
     * @return Une nouvelle image contenant une inversion horizontale des pixels
     * de l'image originale
     */
    public static Image flop(Image img) {
        int w = (int) img.getWidth();
        WritableImage output = new WritableImage(w, (int) img.getHeight());

        PixelReader reader = img.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                Color color = reader.getColor(x, y);
                writer.setColor(w - 1 - x, y, color);
            }
        }

        return output;
    }


    /**
     * Recolorie une image avec une couleur donnée. Tous les pixels
     * non-transparents de l'image img se font attribuer la couleur color passée
     * en paramètre.
     * @param img   L'image originale
     * @param color La nouvelle couleur à utiliser
     * @return Une nouvelle image contenant une version re-coloriée de l'image
     * originale
     */
    public static Image colorize(Image img, Color color) {
        WritableImage output = new WritableImage((int) img.getWidth(), (int) img.getHeight());

        PixelReader reader = img.getPixelReader();
        PixelWriter writer = output.getPixelWriter();

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                if (reader.getColor(x, y).getOpacity() > 0) {
                    writer.setColor(x, y, color);
                }
            }
        }

        return output;
    }


    //getters et setters:

    public void setNiveau(int niveau) {
        this.niveau = niveau;

        if (this.vx > 0) {
            this.vx = 100 * Math.cbrt(this.niveau) + 200;//vitesse horizontale
        } else {
            this.vx = -100 * Math.cbrt(this.niveau) - 200;//vitesse horizontale
        }

    }

    public void setCapture(boolean bool) {
        this.capture = bool;
    }

    public boolean isCapture() {
        return capture;
    }
}