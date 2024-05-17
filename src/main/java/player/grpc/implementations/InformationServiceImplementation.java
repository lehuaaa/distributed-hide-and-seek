package player.grpc.implementations;

import administration.server.beans.Coordinate;
import com.example.grpc.Information;
import com.example.grpc.InformationServiceGrpc;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;
import player.domain.enums.State;
import player.grpc.handlers.ElectionHandler;

public class InformationServiceImplementation extends InformationServiceGrpc.InformationServiceImplBase {

    @Override
    public void playerPresentation(Information.PlayerInfo playerInfo, StreamObserver<Information.Ack> responseObserver) {

        Participant participant = new Participant(playerInfo.getId(),
                                                  playerInfo.getAddress(),
                                                  playerInfo.getPort(),
                                                  playerInfo.getCoordinate().getX(),
                                                  playerInfo.getCoordinate().getY());

        Player.getInstance().storeNewParticipant(participant);

        if (Player.getInstance().getState() == State.ELECTION)
            ElectionHandler.getInstance().sendElectionMessage(participant);

        System.out.println("Player " + playerInfo.getId() + " joined the game in position " + new Coordinate(playerInfo.getCoordinate().getX(), playerInfo.getCoordinate().getY()));
        responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        responseObserver.onCompleted();
    }

    @Override
    public void playerRescueOrElimination(Information.PlayerEvent playerEvent, StreamObserver<Information.Ack> responseObserver) {
        /* Do something */
    }
}
