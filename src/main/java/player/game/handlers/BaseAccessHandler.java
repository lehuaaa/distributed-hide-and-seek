package player.game.handlers;

import com.example.grpc.Base;
import com.example.grpc.BaseAccessServiceGrpc;
import com.example.grpc.Information;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import administration.server.beans.Participant;
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
        requestBaseAccess();
    }

    public void requestBaseAccess() {
        long timestampBaseRequest = Hider.getInstance().generateBaseRequest();
        System.out.println("Timestamp base request: " + timestampBaseRequest);
        for (Participant p: Player.getInstance().getParticipants()) {

            /* Slow down request base access by 10 seconds
            try { Thread.sleep(10000); } catch (InterruptedException e) { throw new RuntimeException(e); } */

            sendBaseRequest(p, timestampBaseRequest);
        }
    }

    public void sendBaseRequest(Participant participant, long timestampBaseRequest) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        BaseAccessServiceGrpc.BaseAccessServiceStub stub = BaseAccessServiceGrpc.newStub(channel);
        Base.BaseRequest baseRequest = Base.BaseRequest.newBuilder().setPlayerId(Player.getInstance().getId()).setTimestamp(timestampBaseRequest).build();

        stub.requestBaseAccess(baseRequest, new StreamObserver<Base.AckConfirmation>() {

            @Override
            public void onNext(Base.AckConfirmation ackConfirmation) {
                if (ackConfirmation.getText().equals("YES") ) {
                    Hider.getInstance().addConfirmation(participant.getId());
                    Hider.getInstance().setTimePassed(ackConfirmation.getTimePassed());
                    System.out.println("Confirmation access from " + participant.getId() + ", confirmation count: " + Hider.getInstance().getConfirmationsCount() + " / " + Player.getInstance().getParticipantsCount());
                }

                if (Hider.getInstance().getConfirmationsCount() == Player.getInstance().getParticipantsCount()) {
                    Hider.getInstance().moveToTheBase();
                }
            }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    public void sendBackConfirmationsToStoredHiders() {
        while (!Hider.getInstance().waitingHidersIsEmpty()) {

            /* Slow down confirmation message by 10 seconds
            try { Thread.sleep(10000); } catch (InterruptedException e) { throw new RuntimeException(e); } */

            sendBackConfirmation(Player.getInstance().getParticipant(Hider.getInstance().getFirstWaitingHider()));
        }
    }

    private void sendBackConfirmation(Participant hider) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(hider.getAddress() + ":" + hider.getPort()).usePlaintext().build();
        BaseAccessServiceGrpc.BaseAccessServiceStub stub = BaseAccessServiceGrpc.newStub(channel);

        Base.Confirmation confirmation = Base.Confirmation.newBuilder().setTimePassed(Hider.getInstance().getTimePassedToReachBase())
                                                                       .setPlayerId(Player.getInstance().getId()).build();

        stub.sendBackConfirmation(confirmation, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) { /* Am I interested in the response */ }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}