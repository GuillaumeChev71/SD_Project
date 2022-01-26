package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.sql.*;

public class BanquierActor extends AbstractActor {

    private int id;
    private boolean demandeDepot;
    private boolean demandeRetrait;
    private int idClient;
    private int soldeDepot;
    private Connection con;

    private BanquierActor(int id, Connection con){

        this.id = id;
        this.demandeDepot=true;
        this.demandeRetrait = false;
        this.idClient =0;
        this.soldeDepot=0;
        this.con = con;

    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetId.class, message -> sendId(getSender()))
                .match(GetDemandeDepot.class, message -> sendDemandeDepot(getSender()))
                .match(GetDemandeRetrait.class, message -> sendDemandeRetrait(getSender()))
                .match(VerifSoldeClient.class, message ->verifSoldeClient(message))
                .match(GetIdClient.class, message -> sendIdClient(getSender()))
                .match(SetIdClient.class,message ->setIdClient(message))
                .match(SetSoldeClient.class, message->setSoldeClient(message))
                .build();
    }

    public void sendId(ActorRef actor) {
        actor.tell(this.id, this.getSelf());
    }

    public void sendDemandeDepot(ActorRef actor){actor.tell(this.demandeDepot, this.getSelf());}

    public void sendDemandeRetrait(ActorRef actor){actor.tell(this.demandeRetrait, this.getSelf());}

    public void sendIdClient(ActorRef actor){actor.tell(this.idClient, this.getSelf());}

    public void setIdClient(final SetIdClient message){

        this.idClient = message.id;
    }

    public void setSoldeClient(final SetSoldeClient message){

            this.soldeDepot = message.solde;

            updateSoldeBD("UPDATE Client SET solde = solde+"+this.soldeDepot+" WHERE idClient = "+this.idClient,this.con);


            System.out.println("Dépot ou Retrait effectué ");

    }

    public void verifSoldeClient(final VerifSoldeClient message){

            int montantRetrait = message.montantRetait;

            int soldeClient = RequêteBD("SELECT SOLDE FROM CLIENT WHERE IDCLIENT="+this.idClient,this.con);
            //System.out.println("soldeClient");

            if(soldeClient > montantRetrait){
                this.demandeRetrait = true;
            }

    }

    public static void updateSoldeBD(String requete, Connection con){

        try {

            Statement statement = con.createStatement();
            statement.executeQuery(requete);

            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static int RequêteBD(String requete ,Connection con){

        int soldeClient = 0;
        try {

            Statement statement = con.createStatement();
            ResultSet resultat = statement.executeQuery(requete);

            int solde = 0;

            while (resultat.next()) {

                solde = resultat.getInt("solde");
                soldeClient=solde;

            }
            statement.close();
            resultat.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return soldeClient;
    }

    public static int getSoldeBD(String requete, Connection con){
        int result = 0;

        try {

            Statement statement = con.createStatement();
            ResultSet resultat = statement.executeQuery(requete);


            String solde = "";

            while (resultat.next()) {
                solde = resultat.getString("solde");

                result= Integer.parseInt(solde);

            }
            statement.close();
            resultat.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }



    public static Props props(int id, Connection con) {
        return Props.create(BanquierActor.class,id,con);
    }

    // Définition des messages en inner classes
    public interface Message {}

    public static class GetId implements Message {
        public GetId() {}
    }

    public static class GetDemandeDepot implements Message {
        public GetDemandeDepot() {}
    }

    public static class GetDemandeRetrait implements Message {
        public GetDemandeRetrait() {}
    }

    public static class GetIdClient implements Message {
        public GetIdClient() {}
    }

    public static class SetIdClient implements Message {
        public int id;
        public SetIdClient(int id) {
            this.id = id;
        }
    }

    public static class SetSoldeClient implements Message {

        public int solde;

        public SetSoldeClient(int solde){
            this.solde = solde;
        }

    }

    public static class VerifSoldeClient implements Message {
        public int montantRetait;

        public VerifSoldeClient(int retrait) {this.montantRetait = retrait;}

    }
}
