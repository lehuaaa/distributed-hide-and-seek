package player.game.implementations;

import administration.server.beans.Coordinate;
import com.example.grpc.Information;
import com.example.grpc.InformationServiceGrpc;
import io.grpc.stub.StreamObserver;
import administration.server.beans.Participant;
import player.game.domain.singletons.Hider;
import player.game.domain.singletons.Player;
import player.game.domain.enums.Role;
import player.game.domain.enums.GameState;
import player.game.domain.singletons.Seeker;
import player.game.handlers.ElectionHandler;
import player.game.handlers.BaseAccessHandler;

public class InformationServiceImplementation extends InformationServiceGrpc.InformationServiceImplBase {

    @Override
    public void playerPresentation(Information.PlayerGameInfo playerGameInfo, StreamObserver<Information.AckPlayerInfo> responseObserver) {

        System.out.println("Player " + playerGameInfo.getId() + " joined in position " + new Coordinate(playerGameInfo.getCoordinate().getX(), playerGameInfo.getCoordinate().getY()));

        Participant participant = new Participant(playerGameInfo.getId(),
                playerGameInfo.getAddress(),
                playerGameInfo.getPort(),
                new Coordinate(playerGameInfo.getCoordinate().getX(), playerGameInfo.getCoordinate().getY()));

        Player.getInstance().addNewParticipant(participant);

        if (Player.getInstance().getState() == GameState.ELECTION) {
            ElectionHandler.getInstance().sendElectionMessage(participant);
        }

        if (Player.getInstance().getState() == GameState.IN_GAME) {
            if (Player.getInstance().getRole() == Role.SEEKER) {
                Seeker.getInstance().storeNewHider(participant);
            } else {
                BaseAccessHandler.getInstance().sendBaseRequest(participant);
            }
        }

        responseObserver.onNext(Information.AckPlayerInfo.newBuilder()
                .setState(Player.getInstance().getState().name())
                .setRole(Player.getInstance().getRole().name())
                .build());

        responseObserver.onCompleted();
    }

    @Override
    public void playerObtainAccess(Information.ObtainedAccessInfo obtainedAccessInfo, StreamObserver<Information.Ack> responseObserver) {

        if (Player.getInstance().getRole() == Role.HIDER) {
            Hider.getInstance().addFinishedHiders(obtainedAccessInfo.getPlayerId());

            if (Hider.getInstance().getFinishedHidersCount() == Player.getInstance().getParticipantsCount() - 1 && Player.getInstance().getState() == GameState.FINISHED) {
                Player.getInstance().setState(GameState.GAME_END);

                System.out.println();
                System.out.println(" *** THE END! *** ");
            }
            responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());

        } else {

            Seeker.getInstance().incrementFinishedHiders();
            double taggingTime = Seeker.getInstance().getHiderTaggingTime(obtainedAccessInfo.getPlayerId());

            if (taggingTime == -1 || taggingTime > obtainedAccessInfo.getTimeWaited()) {
                responseObserver.onNext(Information.Ack.newBuilder().setText("YES").build());
            } else {
                responseObserver.onNext(Information.Ack.newBuilder().setText("NO").build());
            }

            if (Seeker.getInstance().getFinishedHiders() == Player.getInstance().getParticipantsCount()) {
                Player.getInstance().setState(GameState.GAME_END);

                System.out.println();
                System.out.println(" *** THE END! *** ");
            }
        }

        responseObserver.onCompleted();
    }
}