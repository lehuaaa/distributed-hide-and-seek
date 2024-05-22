package player.game.handlers;

import com.example.grpc.Information;
import com.example.grpc.InformationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import administration.server.beans.Participant;
import player.game.domain.singletons.Hider;
import player.game.domain.singletons.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class InformationHandler extends Thread {

    private static InformationHandler instance;

    public static InformationHandler getInstance() {
        if (instance == null) {
            instance = new InformationHandler();
        }
        return instance;
    }

    @Override
    public void run() {
        presentPlayerToOthers();
    }

    public void presentPlayerToOthers() {
        List<Participant> participants = Player.getInstance().getParticipants();
        for (Participant participant : participants) {
            sendPlayerInfo(participant);
        }
    }

    private void sendPlayerInfo(Participant participant) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        InformationServiceGrpc.InformationServiceStub stub = InformationServiceGrpc.newStub(channel);

        Information.PlayerInfo playerInfo = Information.PlayerInfo.newBuilder()
                .setId(Player.getInstance().getId())
                .setAddress(Player.getInstance().getAddress())
                .setPort(Player.getInstance().getPort())
                .setCoordinate(Information.Coordinate.newBuilder().setX(Player.getInstance().getCoordinate().getX()).setY(Player.getInstance().getCoordinate().getY()).build())
                .build();

        stub.playerPresentation(playerInfo, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) { /* Successful registration  */ }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }


    public void informPlayersOfSaving(double timeWaitedToGetBaseAccess) {
        List<Participant> participants = Player.getInstance().getParticipants();
        for (Participant participant : participants) {
            sendPlayerSaving(participant, timeWaitedToGetBaseAccess);
        }
    }

    public void sendPlayerSaving(Participant participant, Double timeWaitedToGetBaseAccess) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        InformationServiceGrpc.InformationServiceStub stub = InformationServiceGrpc.newStub(channel);
        Information.SavingEvent savingEvent = Information.SavingEvent.newBuilder().setPlayerId(Player.getInstance().getId()).setTime(timeWaitedToGetBaseAccess).build();

        stub.playerSaving(savingEvent, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) {
                if (ack.getText().equals("YES")) {
                    System.out.println("Congratulations you saved yourself in time!");
                } else if (ack.getText().equals("NO")) {
                    System.out.println("Unfortunately you have been tagged by the seeker!");
                }

                if (Hider.getInstance().getFinishedHidersCount() == Player.getInstance().getParticipantsCount() - 1 && !ack.getText().equals("OK")) {
                    System.out.println();
                    System.out.println(" *** THE END! *** ");
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
}