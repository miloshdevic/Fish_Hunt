import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class HighScores {

    private ArrayList<String> username;
    private static File score = new File("Score.txt");

    public HighScores() {
        this.username = new ArrayList<>();
        username = lireFichier();
    }

    /**
     * ajouter le nom du joueur et son score à la fin d'une partie
     * @param player
     * @param score
     */
    public ArrayList<String> ajouterScores(String player, int score) {
        String nameEtScore = player + " - " + score;
        this.username.add(nameEtScore);
        username = trierListe(this.username);
        ecrireFichier(username);
        return username;
    }

    /**
     * trier les joueurs en fonction de leur score
     * @param list
     * @return
     */
    public ArrayList<String> trierListe(ArrayList<String> list) {
        ArrayList<Integer> secondList = new ArrayList<>();
        ArrayList<String> sorted = new ArrayList<>();

        for (String s: list) {
            String temp = s.split("-")[1].substring(1);
            secondList.add(Integer.parseInt(temp));
        }

        Collections.sort(secondList, Collections.reverseOrder());

        for (int s: secondList) {
            for(int i = 0; i < list.size(); i++){
                String temp = list.get(i);
                String checkScore = temp.split("-")[1];

                if(checkScore.contains(String.valueOf(s))){
                    sorted.add(temp);
                    list.remove(list.indexOf(temp));
                    continue;
                }
            }

            if(sorted.size() >= 10)
                break;
        }

        username = sorted;
        return sorted;
    }

    /**
     * lire le fichier contenant les informations des joueurs
     * @return
     */
    public ArrayList<String> lireFichier() {
        ArrayList<String> data = new ArrayList<>();

        try(BufferedReader br = new BufferedReader(new FileReader(score))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                data.add(line);
                sb.append(line);
                sb.append(System.lineSeparator());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * modifier le fichier contenant les informations des joueurs
     * @param data
     */
    public void ecrireFichier(ArrayList<String> data) {
        try {
            FileWriter fstream = new FileWriter(score);
            BufferedWriter out = new BufferedWriter(fstream);
            for (String s : data) {
                out.write(s);
                out.newLine();
            }
            out.close();
        } catch (Exception e ) { //Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }
}