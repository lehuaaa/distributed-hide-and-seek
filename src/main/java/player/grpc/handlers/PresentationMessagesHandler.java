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

public class PresentationMessagesHandler {

    private static PresentationMessagesHandler instance;

    private PresentationMessagesHandler() {}

    public static PresentationMessagesHandler getInstance() {
        if (instance == null) {
            instance = new PresentationMessagesHandler();
        }
        return instance;
    }

    public void start() {

        Player player = Player.getInstance();
        List<Participant> participants = player.getParticipants();

        for (int i = 0; i < participants.size(); i++) {

            final int index = i;
            final ManagedChannel channel =
                    ManagedChannelBuilder.forTarget(
                            participants.get(index).getAddress() + ":" + participants.get(index).getPort()).usePlaintext().build();

            GameServiceGrpc.GameServiceStub stub = GameServiceGrpc.newStub(channel);

            Game.PresentationMessage presentationMessage = Game.PresentationMessage.newBuilder()
                    .setId(player.getId())
                    .setAddress(player.getAddress())
                    .setPort(player.getPort())
                    .setCoordinate(Game.Coordinate.newBuilder().setX(player.getCoordinate().getX()).setY(player.getCoordinate().getY()).build())
                    .build();

            stub.storePLayer(presentationMessage, new StreamObserver<Game.CoordinateResponse>() {

                @Override
                public void onNext(Game.CoordinateResponse coordinateResponse) {
                    Game.Coordinate result = coordinateResponse.getCoordinate();
                    player.setParticipantCoordinate(index, new Coordinate(result.getX(), result.getY()));
                }

                public void onError(Throwable throwable) {
                    System.out.println("Error: " + throwable.getMessage());
                }

                public void onCompleted() {
                    channel.shutdownNow();
                }
            });

            /* await response */
            try {
                channel.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
