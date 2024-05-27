package player.game.handlers;

import com.example.grpc.Information;
import com.example.grpc.InformationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import administration.server.beans.Participant;
import player.game.domain.enums.GameState;
import player.game.domain.enums.Role;
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
        List<Participant> participants = Player.getInstance().getParticipants();
        for (Participant participant : participants) {
            sendPlayerInfo(participant);
        }
    }

    private void sendPlayerInfo(Participant participant) {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        InformationServiceGrpc.InformationServiceStub stub = InformationServiceGrpc.newStub(channel);

        Information.PlayerGameInfo playerGameInfo = Information.PlayerGameInfo.newBuilder()
                .setId(Player.getInstance().getId())
                .setAddress(Player.getInstance().getAddress())
                .setPort(Player.getInstance().getPort())
                .setCoordinate(Information.Coordinate.newBuilder().setX(Player.getInstance().getCoordinate().getX()).setY(Player.getInstance().getCoordinate().getY()).build())
                .build();

        stub.playerPresentation(playerGameInfo, new StreamObserver<Information.AckPlayerInfo>() {

            @Override
            public void onNext(Information.AckPlayerInfo ackPlayerInfo) {
                if (ackPlayerInfo.getRole().equals(Role.SEEKER.name())) {
                    if (ackPlayerInfo.getState().equals(GameState.GAME_END.name())) {
                        Player.getInstance().setState(GameState.GAME_END);
                        System.out.println();
                        System.out.println("The game is over!");
                    } else {
                        Player.getInstance().setState(GameState.IN_GAME);
                        System.out.println("The seeker is the player " + participant.getId());
                        BaseAccessHandler.getInstance().start();
                    }
                }

                if (ackPlayerInfo.getState().equals(GameState.FINISHED.name())) {
                    Hider.getInstance().addFinishedHiders(participant.getId());
                }
            }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error sendPlayerInfo: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(30, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    public void informPlayersOfTheObtainedAccess() {
        List<Participant> participants = Player.getInstance().getParticipants();
        for (Participant participant : participants) {
            sendObtainedAccessInfo(participant);
        }
    }

    private void sendObtainedAccessInfo(Participant participant) {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        InformationServiceGrpc.InformationServiceStub stub = InformationServiceGrpc.newStub(channel);
        Information.ObtainedAccessInfo obtainedAccessInfo = Information.ObtainedAccessInfo.newBuilder().setPlayerId(Player.getInstance().getId()).setTimeWaited(Hider.getInstance().getTimeWaitedToObtainBaseAccess()).build();

        stub.playerObtainAccess(obtainedAccessInfo, new StreamObserver<Information.Ack>() {

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
            public void onError(Throwable throwable) { System.out.println("Error sendPlayerSaving: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(30, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}