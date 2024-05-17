package player.grpc.handlers;

import com.example.grpc.Election;
import com.example.grpc.ElectionServiceGrpc;
import com.example.grpc.Information;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;
import player.domain.enums.Role;
import player.domain.enums.State;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ElectionHandler {

    private static ElectionHandler instance;

    private int positiveVoteReceived = 0;

    public static ElectionHandler getInstance() {
        if (instance == null) {
            instance = new ElectionHandler();
        }
        return instance;
    }

    public void startElection() {
        List<Participant> participants = Player.getInstance().getParticipants();
        for (Participant participant : participants) {
            sendElectionMessage(participant);
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
                                                                                   .build())
                                                                           .build();

        stub.election(electionMessage, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) {
                if (ack.getText().equals("YES")) {
                    positiveVoteReceived++;
                }

                if (positiveVoteReceived == Player.getInstance().getParticipantsCount()) {
                    Player.getInstance().setState(State.IN_GAME);
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
        List<Participant> participants = Player.getInstance().getParticipants();
        for (Participant participant : participants) {
            sendElectedMessage(participant);
        }
        SeekerHandler.getInstance().startHunting();
    }

    public void sendElectedMessage(Participant participant) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        ElectionServiceGrpc.ElectionServiceStub stub = ElectionServiceGrpc.newStub(channel);

        Election.ElectedMessage electedMessage = Election.ElectedMessage.newBuilder()
                                                                        .setPlayerId(Player.getInstance().getId())
                                                                        .build();

        stub.elected(electedMessage, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) { /* do I need to handle this case */ }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}