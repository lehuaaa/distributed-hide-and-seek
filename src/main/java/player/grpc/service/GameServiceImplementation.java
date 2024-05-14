package player.grpc.service;

import administration.server.beans.Coordinate;
import administration.server.beans.Node;
import com.example.grpc.Game;
import com.example.grpc.GameServiceGrpc;
import io.grpc.stub.StreamObserver;
import player.domain.Player;
import player.grpc.handlers.ElectionHandler;

public class GameServiceImplementation extends GameServiceGrpc.GameServiceImplBase {

    @Override
    public void introduction(Game.IntroductionMessage presentationMessage, StreamObserver<Game.CoordinateResponse> responseObserver) {
        Player.getInstance().storeNewParticipant(
                presentationMessage.getId(),
                presentationMessage.getAddress(),
                presentationMessage.getPort(),
                presentationMessage.getCoordinate().getX(),
                presentationMessage.getCoordinate().getY());

        if (presentationMessage.getIsNextNode()) {
            Player.getInstance().setNextNode(
                    new Node(presentationMessage.getId(), presentationMessage.getAddress(), presentationMessage.getPort()));
        }

        System.out.println("Player " + presentationMessage.getId() + " joined the game in position " + new Coordinate(presentationMessage.getCoordinate().getX(), presentationMessage.getCoordinate().getY()));

        Coordinate coordinate = Player.getInstance().getCoordinate();
        Game.CoordinateResponse coordinateResponse = Game.CoordinateResponse.newBuilder().setCoordinate(Game.Coordinate.newBuilder().setX(coordinate.getX()).setY(coordinate.getY()).build()).build();
        responseObserver.onNext(coordinateResponse);
        responseObserver.onCompleted();
    }

    @Override
    public StreamObserver<Game.ElectionMessage> election(final StreamObserver<Game.AckMessage> responseObserver) {
        return new StreamObserver<Game.ElectionMessage>() {

            @Override
            public void onNext(Game.ElectionMessage electionMessage) {

                responseObserver.onNext(Game.AckMessage.newBuilder().setAck("OK").build());

                String playerId = Player.getInstance().getId();
                String messagePlayerId = electionMessage.getPlayerId();
                double playerDistanceFromBase = Player.getInstance().getCoordinate().getDistanceFromBase();
                double messageDistanceFromBase = electionMessage.getDistanceFromBase();

                /* ELECTION message case */
                if (electionMessage.getType().equals("ELECTION")) {

                    /* Player receives ELECTION with his own ID */
                    if (Player.getInstance().getId().equals(messagePlayerId)) {

                        /* Player send ELECTED message */
                        if (!Player.getInstance().isSeeker) {
                            Player.getInstance().isSeeker = true;
                            ElectionHandler.getInstance().sendMessage("ELECTED", playerId, playerDistanceFromBase);
                        }

                    /* Player replaces message parameters with his own ID and his distance from the base */
                    } else if ((playerDistanceFromBase < messageDistanceFromBase || (playerDistanceFromBase == messageDistanceFromBase && playerId.compareTo(messagePlayerId) > 0)) && !Player.getInstance().hasParticipatedToElection) {
                        ElectionHandler.getInstance().sendMessage("ELECTION", playerId, playerDistanceFromBase);
                        Player.getInstance().hasParticipatedToElection = true;

                    /* Player forwards message without modifying the parameters */
                    } else if (messageDistanceFromBase < playerDistanceFromBase || (playerDistanceFromBase == messageDistanceFromBase && messagePlayerId.compareTo(playerId) > 0)) {
                        ElectionHandler.getInstance().sendMessage("ELECTION", messagePlayerId, messageDistanceFromBase);
                        Player.getInstance().hasParticipatedToElection = true;
                    }

                /* ELECTED message case */
                } else {

                    /* Player receives his ELECTED message */
                    if (Player.getInstance().getId().equals(messagePlayerId)) {
                        System.out.println("You are the seeker");

                    /* Player forwards ELECTED message*/
                    } else {
                        Player.getInstance().hasParticipatedToElection = false;
                        System.out.println("The seeker is the player " + messagePlayerId);
                        ElectionHandler.getInstance().sendMessage("ELECTED", messagePlayerId, messageDistanceFromBase);
                    }

                    /* Stop the message streaming from the previous node in the overlay network */
                    responseObserver.onCompleted();
                }
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() { }

        };
    }
}