package sd.akka;

import java.sql.*;
import java.time.Duration;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.Scanner;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.routing.RoundRobinPool;
import sd.akka.actor.*;
import java.io.*;
import java.lang.Thread;

import static java.lang.Integer.parseInt;

public class App {


    public static String[] RequêteBD(Connection con,String requete){

        String [] strResult = new String[5];
        try {

            Statement statement = con.createStatement();
            ResultSet resultat = statement.executeQuery(requete);


            String nom = "";
            String prenom = "";
            String solde = "";
            String idBanquier ="";
            String idClient ="";
            while (resultat.next()) {
                nom = resultat.getString("nom");
                prenom = resultat.getString("prenom");
                solde = resultat.getString("solde");
                idBanquier = resultat.getString("idbanquier");
                idClient = resultat.getString("idclient");
                strResult[0]= nom;
                strResult[1]= prenom;
                strResult[2]= solde;
                strResult[3]=idBanquier;
                strResult[4]=idClient;

            }
            statement.close();
            resultat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return strResult;
    }


    public static void main(String[] args) throws InterruptedException {



        //Banque Banque Banque Banque Banque Banque Banque Banque Banque Banque Banque Banque Banque Banque Banque Banque


        //TEST

        String url = "jdbc:oracle:thin:@butor.iem:1521/ENSB2021";
        String user = "gc885074";
        String mdp = "gc885074";
        Connection con = null;
        Connection con1 = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Impossible de charger le pilote jdbc:odbc");
            System.exit(99);
        }
        try {
            con = DriverManager.getConnection(url, user, mdp);
            con1 = DriverManager.getConnection(url, user, mdp);
        } catch (SQLException e) {
            System.err.println("Connection a la base de donnees impossible");
            System.exit(99);
        }


        ActorSystem actorSystem = ActorSystem.create();//création du système d'acteurs
        long startTime = System.currentTimeMillis(); //mesure du temps d'execution

        //String [] resultat1 = RequêteBD(con,"SELECT * FROM CLIENT WHERE IDCLIENT="+idClient1);
        //int idClient1 =((int) (Math.random()*(7 - 1))) + 1;
        int idClient1 =5;
        String [] resultat1 = RequêteBD(con,"SELECT * FROM CLIENT WHERE IDCLIENT="+idClient1);


        int idBanquier1 = parseInt(resultat1[3]); //initialisation des pramètres
        int idC1 = parseInt(resultat1[4]);
        int soldeClient1 = parseInt(resultat1[2]);

        ActorRef banque = actorSystem.actorOf(BanqueActor.props(idBanquier1,idC1,soldeClient1,con1),"banque");


        for(int i=0;i<100;i++){

            banque.tell(new BanqueActor.GetInfos(),ActorRef.noSender());
            banque.tell(new BanqueActor.RetraitClient(),ActorRef.noSender());

        }

        long stopTime = System.currentTimeMillis();
        System.out.println((double)(stopTime - startTime)/1000+" secondes ");
        Thread.sleep(10000000);
        actorSystem.terminate();

        System.exit(0);
        // Arrêt du système d'acteurs



	}


}
