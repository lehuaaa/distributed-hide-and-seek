package player.grpc.handlers;

import com.example.grpc.Election;
import com.example.grpc.ElectionServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Player;

import java.util.concurrent.TimeUnit;

public class ElectionHandler {

    private static ElectionHandler instance;

    public static ElectionHandler getInstance() {
        if (instance == null) {
            instance = new ElectionHandler();
        }
        return instance;
    }

    public void forwardMessage(String type, String playerId, Double distanceFromBase) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(Player.getInstance().getNextNode().getAddress() + ":" + Player.getInstance().getNextNode().getPort()).usePlaintext().build();
        ElectionServiceGrpc.ElectionServiceStub stub = ElectionServiceGrpc.newStub(channel);

        Election.ElectionMessage electionMessage = Election.ElectionMessage.newBuilder()
                                                                           .setType(type)
                                                                           .setPlayerId(playerId)
                                                                           .setDistanceFromBase(distanceFromBase)
                                                                           .build();

        stub.election(electionMessage, new StreamObserver<Election.ElectionAck>() {

            @Override
            public void onNext(Election.ElectionAck electionAck) {
                /* Implement code in case the receiver node crash */
            }

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