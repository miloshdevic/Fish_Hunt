import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Player extends Entity {

    public Image imgVie;//affiche le nbr de vies restants (en images)
    private int score = 0;
    private Image cible;
    private int vie = 3;

    private boolean hasShot;
    private boolean shoot;
    private double radius;

    private double shootX, shootY;

    public Player() {
        this.imgVie = new Image("00.png");
        this.cible = new Image("cible.png");
        this.largeur = 50;
        this.hauteur = 50;
    }

    /**
     * suit la souris et agit comme viseur pour le joueur
     * @param x position de la souris au moment du clique sur l'axe des x
     * @param y pareil mais sur l'axe des y
     */
    public void viser(double x, double y) {
        this.x = x - this.largeur/2;
        this.y = y - this.hauteur/2;
    }


    /**
     * mettre à jour les fonctionnalités du joueur
     * @param dt Temps écoulé depuis le dernier update() en secondes
     */
    @Override
    public void update(double dt) {
        if (this.hasShot) {
            this.radius -= dt * 350;
        }
    }

    /**
     * "tirer" des "balles" pour capturer les poissons
     * @param x position de la souris au moment du clique sur l'axe des x
     * @param y pareil mais sur l'axe des y
     */
    public void tirer(double x, double y) {
        this.hasShot = !this.hasShot;
        this.shoot = true;
        this.radius = 70;

        this.shootX = x;
        this.shootY = y;
    }

    /**
     * dessiner le viseur, la balle tirée et les vies
     * @param context
     * @param fenetreY
     */
    @Override
    public void draw(GraphicsContext context, double fenetreY) {
        double yAffiche = this.y - fenetreY;

        //dessiner la balle
        if (this.hasShot) {
            context.setFill(Color.BLACK.desaturate());
            context.fillOval(x + this.largeur/2 - this.radius, yAffiche + this.hauteur/2 - this.radius, this.radius*2,
                    this.radius*2);

            if (this.radius < 0.1)
                this.hasShot = false;
        }

        //dessiner le viseur
        context.drawImage(this.cible, this.x, this.y, this.largeur, this.hauteur);

        //dessiner les vies
        for (int i = 0; i < this.vie; i++){
            context.drawImage(this.imgVie, (double) FishHunt.largeur / 2 - this.imgVie.getHeight() / 20 - 75 + 75*i,
                    (double) FishHunt.hauteur /7 - this.imgVie.getWidth()/20, this.imgVie.getHeight()/10,
                    this.imgVie.getWidth()/10);
        }

        context.setFill(Color.WHITE);
        context.setFont(Font.font("Verdana", 20));
        context.fillText(String.valueOf(this.score),
                (double) FishHunt.largeur / 2 - (double) String.valueOf(this.score).length()/2, 25);
    }

    /**
     * incrémenter le score de 1
     * @param plus
     */
    public void addScore(int plus) {
        this.score += plus;
    }



    //getters et setters:


    public int getScore( ){
        return score;
    }

    public int getVie() {
        return vie;
    }

    public double getShootX() {
        return shootX;
    }

    public double getShootY() {
        return shootY;
    }

    public void setVie(int vie) {
        this.vie = vie;
    }

    public boolean isHasShot() {
        return hasShot;
    }
}