package player.grpc.implementations;

import administration.server.beans.Coordinate;
import com.example.grpc.Election;
import com.example.grpc.ElectionServiceGrpc;
import com.example.grpc.Information;
import io.grpc.stub.StreamObserver;
import player.domain.Player;
import player.domain.enums.Role;
import player.domain.enums.State;
import player.grpc.handlers.HiderHandler;

public class ElectionServiceImplementation extends ElectionServiceGrpc.ElectionServiceImplBase {

    @Override
    public void election(Election.ElectionMessage electionMessage, StreamObserver<Information.Ack> responseObserver) {

        String messagePlayerId = electionMessage.getPlayerId();
        Coordinate messagePlayerCoordinate = new Coordinate(electionMessage.getPlayerCoordinate().getX(),
                                                            electionMessage.getPlayerCoordinate().getY());

        Player.getInstance().setParticipantCoordinate(messagePlayerId, messagePlayerCoordinate);

        double playerDistanceFromBase = Player.getInstance().getCoordinate().getDistanceFromBase();
        double messageDistanceFromBase = messagePlayerCoordinate.getDistanceFromBase();
        Information.Ack response;

        if (messageDistanceFromBase > playerDistanceFromBase ||
                (messageDistanceFromBase == playerDistanceFromBase && Player.getInstance().getId().compareTo(messagePlayerId) > 0)) {
            response = Information.Ack.newBuilder().setText("NO").build();
        } else {
            response = Information.Ack.newBuilder().setText("YES").build();
        }

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void elected(Election.ElectedMessage electedMessage, StreamObserver<Information.Ack> responseObserver) {
        System.out.println("The seeker is the player " + electedMessage.getPlayerId());
        Player.getInstance().setState(State.IN_GAME);
        Player.getInstance().setRole(Role.HIDER);
        HiderHandler.getInstance().requestBaseAccess();
        responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        responseObserver.onCompleted();
    }
}
