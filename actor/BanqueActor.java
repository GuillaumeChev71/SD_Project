package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.io.Tcp;
import akka.pattern.Patterns;

import java.sql.Connection;
import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;


public class BanqueActor extends AbstractActor {

    private ActorRef client;
    private ActorRef banquier;
    private Connection con;


    private BanqueActor(int idBanquier, int idClient, int soldeClient, Connection con){
        this.con = con;
        // Création d'acteurs enfants le client et le banquier
        this.client = getContext().actorOf(ClientActor.props(idClient,soldeClient), "client");
        this.banquier = getContext().actorOf(BanquierActor.props(idBanquier,this.con), "banquier");

    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetInfos.class, message -> getInfos())
                .match(DepotClient.class, message ->depotClient())
                .match(RetraitClient.class, message ->retraitClient())
                .build();
    }

    //test de récupération de l'id du client et du banquier
    public void getInfos(){

        CompletionStage<Object> result1 = Patterns.ask(client, new ClientActor.GetId(), Duration.ofSeconds(10));

        int idClient=0;

        try {
            idClient = (int) result1.toCompletableFuture().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.banquier.tell(new BanquierActor.SetIdClient(idClient),ActorRef.noSender());

    }

    public void depotClient(){

        boolean demandeDepot = false;
        //System.out.println("Le client veut déposer de l'argent !");

        CompletionStage<Object> result1 = Patterns.ask(banquier, new BanquierActor.GetDemandeDepot(), Duration.ofSeconds(10));


        try {
            demandeDepot = (boolean) result1.toCompletableFuture().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(demandeDepot){
            //Scanner myObj = new Scanner(System.in);
            //System.out.println("Combien voulez vous déposer ? ");
            //int valeurDepot = myObj.nextInt();
            int valeurDepot = 1;

            //System.out.println(valeurDepot);
            this.banquier.tell(new BanquierActor.SetSoldeClient(valeurDepot),ActorRef.noSender());
        }

    }

    public void retraitClient(){

        this.banquier.tell(new BanquierActor.VerifSoldeClient(1),ActorRef.noSender());

        boolean demandeRetrait = false;

        CompletionStage<Object> result1 = Patterns.ask(banquier, new BanquierActor.GetDemandeRetrait(), Duration.ofSeconds(10));

        try {
            demandeRetrait = (boolean) result1.toCompletableFuture().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(demandeRetrait){
            this.banquier.tell(new BanquierActor.SetSoldeClient(-1),ActorRef.noSender());
        }

    }



    public static Props props(int idBanquier, int idClient, int soldeClient, Connection con) {
        return Props.create(BanqueActor.class,idBanquier,idClient,soldeClient,con);
    }

    // Définition des messages en inner classes
    public interface Message {}

    public static class GetInfos implements Message {
        public GetInfos() {}
    }

    public static class DepotClient implements Message {
        public DepotClient() {}
    }
    public static class RetraitClient implements Message {
        public RetraitClient() {}
    }



}
