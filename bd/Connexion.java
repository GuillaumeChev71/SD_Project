package sd.akka.bd;

import java.sql.*;

public class Connexion {

        public String url;
        public String user;
        public String mdp;
        public Connection con;



    public void main(String[] args) {

        url = "jdbc:oracle:thin:@butor.iem:1521/ENSB2021";
        user = "gc885074";
        mdp = "ch098407";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Impossible de charger le pilote jdbc:odbc");
            System.exit(99);
        }
        try {
            con = DriverManager.getConnection(url, user, mdp);
        } catch (SQLException e) {
            System.err.println("Connection a la base de donnees impossible");
            System.exit(99);
        }

        try {

            Statement statement = con.createStatement();
            ResultSet resultat = statement.executeQuery("SELECT * FROM Banquier");
            String nom = "";
            String prenom = "";
            while (resultat.next()) {
                nom = resultat.getString("nom");
                prenom = resultat.getString("prenom");
                System.out.println("Prenom : " + prenom + " Nom : " + nom);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
