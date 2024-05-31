package player.game.handlers;

import com.example.grpc.Base;
import com.example.grpc.BaseAccessServiceGrpc;
import com.example.grpc.Information;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import administration.server.beans.Participant;
import player.game.domain.enums.GameState;
import player.game.domain.singletons.Hider;
import player.game.domain.singletons.Player;

import java.util.concurrent.TimeUnit;

public class BaseAccessHandler extends Thread {

    private static BaseAccessHandler instance;

    public synchronized static BaseAccessHandler getInstance() {
        if (instance == null) {
            instance = new BaseAccessHandler();
        }
        return instance;
    }

    @Override
    public void run() {
        System.out.println();
        System.out.println(" *** GAME PHASE! *** ");

        Hider.getInstance().generateTimestampBaseAccessRequest();
        System.out.println("Timestamp base access request: " + Hider.getInstance().getTimestampBaseAccessRequest());

        for (Participant p: Player.getInstance().getParticipants()) {
            sendBaseRequest(p);
        }
    }

    public void sendBaseRequest(Participant participant) {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        BaseAccessServiceGrpc.BaseAccessServiceStub stub = BaseAccessServiceGrpc.newStub(channel);
        Base.BaseRequest baseRequest = Base.BaseRequest.newBuilder().setPlayerId(Player.getInstance().getId()).setTimestamp(Hider.getInstance().getTimestampBaseAccessRequest()).build();

        stub.requestBaseAccess(baseRequest, new StreamObserver<Base.AckConfirmation>() {

            @Override
            public void onNext(Base.AckConfirmation ackConfirmation) {
                if (ackConfirmation.getText().equals("YES") ) {
                    Hider.getInstance().addConfirmation(participant.getId());
                    Hider.getInstance().setTimeWaitedToObtainBaseAccess(ackConfirmation.getTimePassed());
                    System.out.println("Confirmation from player " + participant.getId() + ". Total count: " + Hider.getInstance().getConfirmationsCount() + " / " + Player.getInstance().getParticipantsCount());
                }

                if (Hider.getInstance().getConfirmationsCount() == Player.getInstance().getParticipantsCount()) {
                    Player.getInstance().setState(GameState.REACHING_BASE);
                    Hider.getInstance().start();
                }
            }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error sendBaseRequest: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(30, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    public void returnConfirmations() {
        while (!Hider.getInstance().waitingHidersIsEmpty()) {
            sendConfirmation(Player.getInstance().getParticipant(Hider.getInstance().getFirstWaitingHider()));
        }
    }

    private void sendConfirmation(Participant hider) {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(hider.getAddress() + ":" + hider.getPort()).usePlaintext().build();
        BaseAccessServiceGrpc.BaseAccessServiceStub stub = BaseAccessServiceGrpc.newStub(channel);

        Base.Confirmation confirmation = Base.Confirmation.newBuilder().setTimePassed(Hider.getInstance().getTimePassedAfterReachingBase())
                                                                       .setPlayerId(Player.getInstance().getId()).build();

        stub.sendBackConfirmation(confirmation, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) { }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error sendBackConfirmation: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }

        });

        /* await response */
        try { channel.awaitTermination(30, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}