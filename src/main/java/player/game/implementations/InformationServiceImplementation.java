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
import player.game.handlers.InformationHandler;

import java.text.DecimalFormat;

public class InformationServiceImplementation extends InformationServiceGrpc.InformationServiceImplBase {

    @Override
    public void playerPresentation(Information.PlayerInfo playerInfo, StreamObserver<Information.AckPlayerInfo> responseObserver) {

        System.out.println("Player " + playerInfo.getId() + " joined the game in position " + new Coordinate(playerInfo.getCoordinate().getX(), playerInfo.getCoordinate().getY()));

        Participant participant = new Participant(playerInfo.getId(),
                                                  playerInfo.getAddress(),
                                                  playerInfo.getPort(),
                                                  new Coordinate(playerInfo.getCoordinate().getX(), playerInfo.getCoordinate().getY()));

        Player.getInstance().storeNewParticipant(participant);

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
    public void playerObtainAccess(Information.SavingEvent savingEvent, StreamObserver<Information.Ack> responseObserver) {

        if (Player.getInstance().getRole() == Role.HIDER) {
            Hider.getInstance().addFinishedHiders(savingEvent.getPlayerId());

            if (Hider.getInstance().getFinishedHidersCount() == Player.getInstance().getParticipantsCount() - 1 && Player.getInstance().getState() == GameState.FINISHED) {
                Player.getInstance().setState(GameState.GAME_END);

                System.out.println();
                System.out.println(" *** THE END! *** ");
            }
            responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());

        } else {

            Seeker.getInstance().incrementFinishedHidersCount();
            double taggingTime = Seeker.getInstance().checkTaggingTime(savingEvent.getPlayerId());

            if (taggingTime == -1 || taggingTime > savingEvent.getTime()) {
                System.out.println("You did not tag the player " + savingEvent.getPlayerId() + " in time, so he is safe!");
                responseObserver.onNext(Information.Ack.newBuilder().setText("YES").build());
            } else {
                System.out.println("You tag the player " + savingEvent.getPlayerId() + " in time!");
                responseObserver.onNext(Information.Ack.newBuilder().setText("NO").build());
            }

            if (Seeker.getInstance().getFinishedHidersCount() == Player.getInstance().getParticipantsCount()) {
                Player.getInstance().setState(GameState.GAME_END);

                System.out.println();
                System.out.println(" *** THE END! *** ");

                Seeker.getInstance().ShowTaggingSummary();
            }
        }

        responseObserver.onCompleted();
    }
}