import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class PoissonSpecial extends Poisson {

    private Image image;
    private boolean capture = false;
    private String type;
    private double timerCrabe1;//0.5s
    private double timerCrabe2;//pour le 0.25s
    private double timerEtoile = 500;//une période de 0.5s pour une amplitude de 50px

    public double initialX;

    public PoissonSpecial() {

        double choisirType = Math.random();//entre 0 et 1

        if (choisirType < 0.5) {
            this.image = new Image("crabe.png");//crabe
            this.type = "crabe";
            this.vx *= 1.3;
            this.vy = 0;
            this.ay = 0;
            this.timerCrabe1 = 500;
            this.timerCrabe2 = 20000;//intentionnellement grand
        } else {
            this.image = new Image("star.png");//étoile
            this.type = "star";
            this.vy = 50;//amplitude
        }

        this.initialX = x;
    }

    /**
     * dessiner le poisson spécial
     * @param context
     * @param fenetreY
     */
    @Override
    public void draw(GraphicsContext context, double fenetreY) {
        if (!capture)
            context.drawImage(image, x, y, this.largeur, this.hauteur);
    }


    /**
     * définir le déplacement de chacun
     */
    @Override
    public void update(double dt) {
        super.update(dt);

        if (this.type.equals("crabe")) { //défini le déplacement du crabe
            this.timerCrabe1 -= dt*1000;
            this.timerCrabe2 -= dt*1000;

            if (this.timerCrabe1 <= 0) {
                this.vx *= -1;
                this.timerCrabe2 = 250;
                this.timerCrabe1 = 20000;//intentionnllement élévé pour ne pas rentrer dans un "if" au mauvais moment
            } else if (this.timerCrabe2 <= 0) {
                this.vx *= -1;
                this.timerCrabe1 = 500;
                this.timerCrabe2 = 20000;
            }

        } else if (this.type.equals("star")) { //défini le déplacement de l'étoile
            this.timerEtoile -= dt*1000;

            if (this.timerEtoile <= 0) {
                this.vy *= -1;
                this.timerEtoile = 500;
            }
        }
    }
}