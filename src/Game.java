import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;


public class Game {

    public static final int largeur = 640, hauteur = 480;
    private double tempsTotal = 0;
    private double fenetreY;
    private int niveau = 0;
    private double timer1 = 3000;//timer pour mettre un poisson normal à toutes les 3 secondes
    private double timer2 = 5000;//pareil mais à toutes les 5 secondes
    private int palier = 0;
    private int uneFois = 0;//pour empêcher de retirer 2 fois une vie pour un même poisson

    private boolean levelFinished = true;//pour les fins de niveaux
    private boolean fin = false;//pour la fin d'une partie (permet d'aller à la scène des scores)
    private boolean animationFin = false;//pour la fin d'une partie (permet de faire l'animation)
    private double timerAnimationFin = 0;//copmteur pour l'animation de "Game Over"

    //les entités du jeu
    private Player player;
    private Poisson poisson;
    private PoissonSpecial poissonSpecial;
    private Bulles[][] bulles;


    public Game() {

        this.player = new Player();

        this.poisson = new Poisson();
        this.poisson.setNiveau(1);

        this.poissonSpecial = new PoissonSpecial();
        this.poissonSpecial.setNiveau(2);

        this.bulles = new Bulles[3][5];
    }

    /**
     * mise à jour des entités du jeu
     * @param dt
     */
    public void update(double dt) {

        this.tempsTotal += dt;

        gameManager(dt);

        //toutes les 3 secondes
        if (((int) (this.tempsTotal%3)) == 0) {
            for (int i = 0; i < 3; i++) {
                double baseX = Math.random()*largeur;

                for (int j = 0; j < 5; j++) {
                    double aleatoire = Math.random()*3;//entre 0 et 2
                    double bulleX;


                    //choisir aléatoirement baseX +/- 20 (1 chance sur 2)
                    if (aleatoire <= 1) {
                        bulleX = baseX + 20;
                    } else {
                        bulleX = baseX - 20;
                    }

                    //pour rester dans les limites
                    if (bulleX < largeur)
                        bulleX = baseX + 20;
                    else if (bulleX + 25 > largeur)
                        bulleX = baseX - 20;

                    this.bulles[i][j] = new Bulles(bulleX, this.fenetreY + hauteur + 50);
                    this.bulles[i][j].update(dt);
                }
            }
        }

        //update chaque bulle
        for (Bulles[] b: this.bulles) {
            for (Bulles bulle: b) {
                bulle.update(dt);
            }
        }

        //pour la fin d'une partie
        if (this.player.getVie() == 0 && !this.levelFinished) {
            this.animationFin = true;
        }


        if (!this.levelFinished) {
            this.timer1 -= dt*1000;//c'est le décompte jusqu'à l'apparition du prochain poisson

            //générer un nouveau poisson à toutes les 3 secondes
            if (this.timer1 <= 0) {
                if (this.poisson.isCapture()) {
                    this.poisson = new Poisson();
                    this.poisson.setNiveau(this.niveau);
                    this.timer1 = 3000;
                } else { //retirer une vie si le poisson n'est pas capturé
                    this.player.setVie(this.player.getVie() - 1);
                    this.poisson = new Poisson();
                    this.poisson.setNiveau(this.niveau);
                    this.timer1 = 3000;
                }
            }

            //générer un nouveau poisson spécial à toutes les 5 secondes
            if (this.niveau >= 2) {
                this.timer2 -= dt*1000;//décompte pour les poissons spéciaux

                if (this.timer2 <= 0) {
                    if (this.poissonSpecial.isCapture()) {
                        this.poissonSpecial = new PoissonSpecial();
                        this.poissonSpecial.setNiveau(this.niveau);
                        this.timer2 = 5000;
                    } else if (!this.poissonSpecial.isCapture() && this.uneFois == 0) {
                        this.player.setVie(this.player.getVie() -1);
                        this.poissonSpecial = new PoissonSpecial();
                        this.poissonSpecial.setNiveau(this.niveau);
                        this.timer2 = 5000;
                        this.uneFois = 1;
                    } else if (!this.poissonSpecial.isCapture() && this.uneFois == 1) { //si une vie a déjà été retirée
                        this.poissonSpecial = new PoissonSpecial();
                        this.poissonSpecial.setNiveau(this.niveau);
                        this.timer2 = 5000;
                        this.uneFois = 0;
                    }

                } else if ((poissonSpecial.initialX == 640.0 && poissonSpecial.x < 0) ||
                        (poissonSpecial.initialX == -50.0 && poissonSpecial.x > largeur)) {

                    if (this.uneFois == 0) { //pour ne pas enlever 2 fois une vie pour un même poisson
                        this.player.setVie(this.player.getVie() - 1);
                        this.uneFois++;
                    }
                }
                this.poissonSpecial.update(dt);
            }
            this.poisson.update(dt);
        }

        this.player.update(dt);

        //vérifier pour les collisions avec les poissons
        if (this.player.isHasShot()) {
            collisionShoot(this.player, this.poisson);
            collisionShoot(this.player, this.poissonSpecial);
        }

        //regarder si le joueur a fait 5pts de plus depuis le dernier niveau
        if (this.player.getScore() - this.palier == 5 && !this.levelFinished) {
            this.levelFinished = true;
            this.tempsTotal = 0;
            this.palier = this.player.getScore();
        }

        if (this.animationFin) {
            this.timerAnimationFin += dt;
        }
    }


    /**
     * dessiner la surface visible de l'interface
     * @param context
     */
    public void draw(GraphicsContext context) {
        context.setFill(Color.DARKBLUE);
        context.fillRect(0, 0, largeur, hauteur);

        //dessiner bulles
        for (Bulles[] b: this.bulles) {
            for (Bulles bulle: b) {
                bulle.draw(context, this.fenetreY);
            }
        }

        if (!this.levelFinished)
            this.poisson.draw(context, this.fenetreY);

        if (!this.levelFinished && this.niveau >= 2)
            this.poissonSpecial.draw(context, this.fenetreY);

        this.player.draw(context, this.fenetreY);

        if (this.levelFinished) {
            context.setFill(Color.WHITE);
            context.setFont(Font.font("Verdana", 35));
            context.fillText("Niveau " + this.niveau, (double) largeur/2 - ("Niveau " +
                    this.niveau).length() * 10, (double) hauteur/2);
        }

        if (this.animationFin) {
            context.setFill(Color.RED);
            context.setFont(Font.font("Verdana", 80));
            context.fillText("GAME OVER", (double) largeur/4 - ("GAME OVER").length() * 10,
                    (double) hauteur/2);
        }
    }

    /**
     * gère les animations pour le deébut d'un niveau et la fin de la partie
     * @param dt
     */
    public void gameManager(double dt) {

        if (this.levelFinished && this.tempsTotal == dt) {
            this.niveau +=1;
            this.poisson.setNiveau(this.niveau);
            this.poissonSpecial.setNiveau(this.niveau);
        }

        if (this.levelFinished && this.tempsTotal > 3) {
            this.levelFinished = false;
            this.tempsTotal = 0;
            this.timer1 = 3000;
            this.timer2 = 5000;
            if(this.niveau == 1) {
                this.player.setVie(3);
            }
        }

        if (this.animationFin && this.timerAnimationFin > 3) {
            this.animationFin = false;
            this.fin = true;
        }
    }

    /**
     * même explication que dans "Player"
     * @param x
     * @param y
     */
    public void viser(double x, double y) {
        this.player.viser(x, y);
    }

    /**
     * même explication que dans "Player"
     * @param x
     * @param y
     */
    public void tirer(double x, double y) {
        this.player.tirer(x, y);
    }


    /**
     * capturer le poisson si le joueur tire dessus
     * @param player
     * @param poisson
     */
    public void collisionShoot(Player player, Poisson poisson) {
        double x = player.getShootX(), y = player.getShootY();

        if(x > poisson.x && x < poisson.x + poisson.largeur &&
                y > poisson.y && y < poisson.y + poisson.hauteur) {
            poisson.setCapture(true);
            poisson.x = -1000;
            poisson.y = -1000;
            player.addScore(1);
        }
    }

    /**
     * permet d'augmenter le niveau courant de 1
     * @param niv
     */
    public void augmenterNiveau(int niv) {
        this.setNiveau(this.niveau+1);
    }


    /**
     * permet d'augmenter le score de 1
     */
    public void augmenterScore() {
        this.player.addScore(1);
    }

    /**
     * permet d'augmenter le nombre de vie de 1 s'il en a moins que 3
     */
    public void augmenterVie() {
        if (this.player.getVie() < 3)
            this.player.setVie(this.player.getVie() + 1);
    }


    //getters et setters:

    /**
     * avoir le score
     * @return
     */
    public int getScore() {
        return this.player.getScore();
    }

    /**
     * finir la partie
     * @return
     */
    public boolean getFin() {
        return fin;
    }

    public int getNiveau() {
        return niveau;
    }

    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
}