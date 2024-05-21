package player.game.handlers;

import com.example.grpc.Election;
import com.example.grpc.ElectionServiceGrpc;
import com.example.grpc.Information;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;
import player.domain.enums.GameState;
import player.domain.enums.Role;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ElectionHandler extends Thread {

    private static ElectionHandler instance;

    private final Set<String> positiveVote;

    private ElectionHandler() {
        positiveVote = new HashSet<>();
    }

    public static ElectionHandler getInstance() {
        if (instance == null) {
            instance = new ElectionHandler();
        }
        return instance;
    }

    @Override
    public void run() {
        startElection();
    }

    public void startElection() {
        for (Participant participant : Player.getInstance().getParticipants()) {
            sendElectionMessage(participant);

            /* Slow down election by 10 seconds
            try { Thread.sleep(10000); } catch (InterruptedException e) { throw new RuntimeException(e); } */
        }
    }

    public void sendElectionMessage(Participant participant) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        ElectionServiceGrpc.ElectionServiceStub stub = ElectionServiceGrpc.newStub(channel);

        Election.ElectionMessage electionMessage = Election.ElectionMessage.newBuilder()
                                                                           .setPlayerId(Player.getInstance().getId())
                                                                           .setPlayerCoordinate(Information.Coordinate.newBuilder()
                                                                                   .setX(Player.getInstance().getCoordinate().getX())
                                                                                   .setY(Player.getInstance().getCoordinate().getY())
                                                                                   .build()).build();

        stub.election(electionMessage, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) {
                if (ack.getText().equals("YES")) {
                    positiveVote.add(participant.getId());
                    /* System.out.println("Positive vote from " + participant.getId() + ", positive vote count: " + positiveVoteReceived + " / " + Player.getInstance().getParticipantsCount()); */
                }

                if (positiveVote.size() == Player.getInstance().getParticipantsCount()) {
                    System.out.println("You are the seeker!");
                    Player.getInstance().setState(GameState.IN_GAME);
                    Player.getInstance().setRole(Role.SEEKER);
                    startElected();
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

    public void startElected() {
        System.out.println();
        System.out.println("1. Game phase!");

        for (Participant participant : Player.getInstance().getParticipants()) {
            sendElectedMessage(participant);

            /* Slow down elected messages by 10 seconds
            try { Thread.sleep(10000); } catch (InterruptedException e) { throw new RuntimeException(e); } */
        }
    }

    public void sendElectedMessage(Participant participant) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        ElectionServiceGrpc.ElectionServiceStub stub = ElectionServiceGrpc.newStub(channel);

        Election.ElectedMessage electedMessage = Election.ElectedMessage.newBuilder()
                                                                        .setPlayerId(Player.getInstance().getId())
                                                                        .build();

        stub.elected(electedMessage, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) { }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}