package player.grpc.implamentation;

import administration.server.beans.Coordinate;
import com.example.grpc.Game;
import com.example.grpc.GameServiceGrpc;
import io.grpc.stub.StreamObserver;
import player.domain.Player;

public class GameServiceImpl extends GameServiceGrpc.GameServiceImplBase {

    @Override
    public void storePLayer(Game.PresentationMessage presentationMessage, StreamObserver<Game.CoordinateResponse> responseObserver) {
        Player.getInstance().storeNewParticipant(presentationMessage.getId(), presentationMessage.getAddress(), presentationMessage.getPort(), presentationMessage.getCoordinate().getX(), presentationMessage.getCoordinate().getY());

        System.out.println("A new Player with id " + presentationMessage.getId() + " joined the game in position " + new Coordinate(presentationMessage.getCoordinate().getX(), presentationMessage.getCoordinate().getY()));
        Coordinate coordinate = Player.getInstance().getCoordinate();

        Game.CoordinateResponse coordinateResponse = Game.CoordinateResponse.newBuilder()
                                                        .setCoordinate(Game.Coordinate.newBuilder()
                                                                .setX(coordinate.getX())
                                                                .setY(coordinate.getY())
                                                                .build()).build();

        responseObserver.onNext(coordinateResponse);
        responseObserver.onCompleted();
    }
}
