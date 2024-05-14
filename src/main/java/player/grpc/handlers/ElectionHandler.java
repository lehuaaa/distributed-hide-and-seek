package player.grpc.handlers;

import com.example.grpc.Game;
import com.example.grpc.GameServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Player;

public class ElectionHandler {

    private static ElectionHandler instance;

    private StreamObserver<Game.ElectionMessage> messagesStream;

    private ElectionHandler() {
        initializeStream();
    }

    public static ElectionHandler getInstance() {
        if (instance == null) {
            instance = new ElectionHandler();
        }
        return instance;
    }

    private void initializeStream() {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(Player.getInstance().getNextNode().getAddress() + ":" + Player.getInstance().getNextNode().getPort()).usePlaintext().build();
        GameServiceGrpc.GameServiceStub stub = GameServiceGrpc.newStub(channel);

        this.messagesStream = stub.election(new StreamObserver<Game.AckMessage>() {

            @Override
            public void onNext(Game.AckMessage ackMessage) {
                /* Implement code in case the receiver node crash */
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {}

        });
    }

    public void sendMessage(String type, String playerId, Double distanceFromBase) {
        messagesStream.onNext(Game.ElectionMessage.newBuilder()
                .setType(type)
                .setPlayerId(playerId)
                .setDistanceFromBase(distanceFromBase)
                .build());

        if (type.equals("ELECTED"))
            messagesStream.onCompleted();
    }
}