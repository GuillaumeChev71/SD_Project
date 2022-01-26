package sd.akka.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

public class ClientActor extends AbstractActor {

    private int id;
    private int solde;

    private ClientActor(int id, int solde){
        this.id = id;
        this.solde = solde;
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GetId.class, message -> sendId(getSender()))
                .match(GetSolde.class, message -> sendSolde(getSender()))
                .build();
    }

    public void sendId(ActorRef actor) {
        actor.tell(this.id, this.getSelf());
    }

    public void sendSolde(ActorRef actor) {
        actor.tell(this.solde, this.getSelf());
    }


    public static Props props(int id, int solde) {
        return Props.create(ClientActor.class,id,solde);
    }

    // Définition des messages en inner classes
    public interface Message {}

    public static class GetId implements Message {
        public GetId() {}
    }

    public static class GetSolde implements Message {
        public GetSolde() {}
    }






}
