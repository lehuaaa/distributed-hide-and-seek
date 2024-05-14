package player.grpc.handlers;


import administration.server.beans.Coordinate;
import com.example.grpc.Game;
import com.example.grpc.GameServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class IntroductionHandler {

    private static IntroductionHandler instance;

    public static IntroductionHandler getInstance() {
        if (instance == null) {
            instance = new IntroductionHandler();
        }
        return instance;
    }

    public void introduceNode() {

        Player player = Player.getInstance();
        List<Participant> participants = player.getParticipants();

        for (int i = 0; i < participants.size(); i++) {

            final int index = i;
            final ManagedChannel channel =
                    ManagedChannelBuilder.forTarget(
                            participants.get(index).getAddress() + ":" + participants.get(index).getPort()).usePlaintext().build();

            GameServiceGrpc.GameServiceStub stub = GameServiceGrpc.newStub(channel);

            Game.IntroductionMessage introductionMessage = Game.IntroductionMessage.newBuilder()
                    .setId(player.getId())
                    .setAddress(player.getAddress())
                    .setPort(player.getPort())
                    .setCoordinate(Game.Coordinate.newBuilder().setX(player.getCoordinate().getX()).setY(player.getCoordinate().getY()).build())
                    .setIsNextNode(i == participants.size() - 1)
                    .build();

            stub.introduction(introductionMessage, new StreamObserver<Game.CoordinateResponse>() {

                @Override
                public void onNext(Game.CoordinateResponse coordinateResponse) {
                    Game.Coordinate result = coordinateResponse.getCoordinate();
                    player.setParticipantCoordinate(index, new Coordinate(result.getX(), result.getY()));
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
}