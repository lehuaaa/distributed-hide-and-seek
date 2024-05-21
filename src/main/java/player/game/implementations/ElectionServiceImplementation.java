package player.game.implementations;

import administration.server.beans.Coordinate;
import com.example.grpc.Election;
import com.example.grpc.ElectionServiceGrpc;
import com.example.grpc.Information;
import io.grpc.stub.StreamObserver;
import player.game.domain.singletons.Player;
import player.game.domain.enums.Role;
import player.game.domain.enums.GameState;
import player.game.handlers.HiderHandler;

public class ElectionServiceImplementation extends ElectionServiceGrpc.ElectionServiceImplBase {

    @Override
    public void election(Election.ElectionMessage electionMessage, StreamObserver<Information.Ack> responseObserver) {

        String messagePlayerId = electionMessage.getPlayerId();
        Coordinate messagePlayerCoordinate = new Coordinate(electionMessage.getPlayerCoordinate().getX(),
                                                            electionMessage.getPlayerCoordinate().getY());

        Player.getInstance().setParticipantCoordinate(messagePlayerId, messagePlayerCoordinate);

        double playerDistanceFromBase = Player.getInstance().getCoordinate().getDistanceFromBase();
        double messageDistanceFromBase = messagePlayerCoordinate.getDistanceFromBase();

        if ((Player.getInstance().getState() != GameState.INIT && Player.getInstance().getState() != GameState.ELECTION) ||
                Player.getInstance().getRole() == Role.SEEKER || messageDistanceFromBase > playerDistanceFromBase ||
                (messageDistanceFromBase == playerDistanceFromBase && Player.getInstance().getId().compareTo(messagePlayerId) > 0))
        {
            responseObserver.onNext(Information.Ack.newBuilder().setText("NO").build());
        } else {
            responseObserver.onNext(Information.Ack.newBuilder().setText("YES").build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void elected(Election.ElectedMessage electedMessage, StreamObserver<Information.Ack> responseObserver) {

        Player.getInstance().setState(GameState.IN_GAME);
        Player.getInstance().setRole(Role.HIDER);

        System.out.println("The seeker is the player " + electedMessage.getPlayerId());
        System.out.println();
        System.out.println("1. Game phase!");

        HiderHandler.getInstance().start();

        responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        responseObserver.onCompleted();
    }
}
