package player.grpc.implementations;

import administration.server.beans.Coordinate;
import administration.server.beans.Node;
import com.example.grpc.Information;
import com.example.grpc.InformationServiceGrpc;
import io.grpc.stub.StreamObserver;
import player.domain.Player;

public class InformationServiceImplementation extends InformationServiceGrpc.InformationServiceImplBase {

    @Override
    public void playerPresentation(Information.PlayerInfo playerInfo, StreamObserver<Information.Coordinate> responseObserver) {

        Player.getInstance().storeNewParticipant(
                playerInfo.getId(),
                playerInfo.getAddress(),
                playerInfo.getPort(),
                playerInfo.getCoordinate().getX(),
                playerInfo.getCoordinate().getY());

        if (playerInfo.getIsNextNode()) {
            Player.getInstance().setNextNode(
                    new Node(playerInfo.getId(), playerInfo.getAddress(), playerInfo.getPort()));
        }

        System.out.println("Player " + playerInfo.getId() + " joined the game in position " + new Coordinate(playerInfo.getCoordinate().getX(), playerInfo.getCoordinate().getY()));

        Coordinate playerCoordinate = Player.getInstance().getCoordinate();
        responseObserver.onNext(Information.Coordinate.newBuilder().setX(playerCoordinate.getX()).setY(playerCoordinate.getY()).build());
        responseObserver.onCompleted();
    }

    @Override
    public void playerRescueOrElimination(Information.PlayerEvent playerEvent, StreamObserver<Information.Ack> responseObserver) {
        /* Do something */
    }
}
