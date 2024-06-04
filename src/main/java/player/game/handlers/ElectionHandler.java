package player.game.handlers;

import com.example.grpc.ElectionServiceGrpc;
import com.example.grpc.Information;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import administration.server.beans.Participant;
import player.game.domain.singletons.Election;
import player.game.domain.singletons.Player;
import player.game.domain.enums.GameState;
import player.game.domain.enums.Role;

import java.util.concurrent.TimeUnit;

public class ElectionHandler extends Thread {

    private static ElectionHandler instance;

    public static ElectionHandler getInstance() {
        if (instance == null) {
            instance = new ElectionHandler();
        }
        return instance;
    }

    @Override
    public void run() {
        if (Player.getInstance().getState() == GameState.ELECTION) {
            System.out.println();
            System.out.println("\u001B[47m" + "\u001B[30m" + " ELECTION PHASE " + "\033[0m");
            startElection();
        }
    }

    public void startElection() {
        for (Participant participant : Player.getInstance().getParticipants()) {

            /* Slow down election by 10 seconds
            try { Thread.sleep(10000); } catch (InterruptedException e) { throw new RuntimeException(e); } */

            sendElectionMessage(participant);
        }
    }

    public void sendElectionMessage(Participant participant) {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        ElectionServiceGrpc.ElectionServiceStub stub = ElectionServiceGrpc.newStub(channel);

        com.example.grpc.Election.ElectionMessage electionMessage = com.example.grpc.Election.ElectionMessage.newBuilder()
                                                                           .setPlayerId(Player.getInstance().getId())
                                                                           .setPlayerCoordinate(Information.Coordinate.newBuilder()
                                                                                   .setX(Player.getInstance().getCoordinate().getX())
                                                                                   .setY(Player.getInstance().getCoordinate().getY())
                                                                                   .build()).build();

        stub.election(electionMessage, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) {
                if (ack.getText().equals("YES")) {
                    Election.getInstance().addVote(participant.getId());
                    System.out.println("Vote from player " + participant.getId() + ". Count: " + Election.getInstance().getVotesCount() + " / " + Player.getInstance().getParticipantsCount());
                }

                if (Election.getInstance().getVotesCount() == Player.getInstance().getParticipantsCount()) {
                    Player.getInstance().setState(GameState.IN_GAME);
                    Player.getInstance().setRole(Role.SEEKER);
                    System.out.println("\u001B[34m" + "You are the seeker!" + "\033[0m");
                    startElected();
                }
            }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error sendElectionMessage: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    public void startElected() {
        System.out.println();
        System.out.println("\u001B[47m" + "\u001B[30m" + " GAME PHASE " + "\033[0m");

        for (Participant participant : Player.getInstance().getParticipants()) {
            sendElectedMessage(participant);
        }

        SeekerHandler.getInstance().start();
    }

    public void sendElectedMessage(Participant participant) {
        final ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        ElectionServiceGrpc.ElectionServiceStub stub = ElectionServiceGrpc.newStub(channel);

        com.example.grpc.Election.ElectedMessage electedMessage = com.example.grpc.Election.ElectedMessage.newBuilder()
                                                                        .setPlayerId(Player.getInstance().getId())
                                                                        .build();

        stub.elected(electedMessage, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) { }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error sendElectedMessage: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}