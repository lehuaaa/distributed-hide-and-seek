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

public class InformationServiceImplementation extends InformationServiceGrpc.InformationServiceImplBase {

    @Override
    public void playerPresentation(Information.PlayerInfo playerInfo, StreamObserver<Information.Ack> responseObserver) {

        System.out.println("Player " + playerInfo.getId() + " joined the game in position " + new Coordinate(playerInfo.getCoordinate().getX(), playerInfo.getCoordinate().getY()));

        Participant participant = new Participant(playerInfo.getId(),
                                                  playerInfo.getAddress(),
                                                  playerInfo.getPort(),
                                                  playerInfo.getCoordinate().getX(),
                                                  playerInfo.getCoordinate().getY());

        Player.getInstance().storeNewParticipant(participant);

        if (Player.getInstance().getState() == GameState.ELECTION) {
            ElectionHandler.getInstance().sendElectionMessage(participant);
        }

        if (Player.getInstance().getState() == GameState.IN_GAME) {
            if (Player.getInstance().getRole() == Role.SEEKER) {
                ElectionHandler.getInstance().sendElectedMessage(participant);
                Seeker.getInstance().storeNewHider(participant);
            } else {
                BaseAccessHandler.getInstance().sendBaseRequest(participant, Hider.getInstance().getTimestampBaseRequest());
            }
        }

        if (Player.getInstance().getState() == GameState.FINISHED) {
            /* The new player is not interested in the time you saved yourself, so you simply return 0 */
            InformationHandler.getInstance().sendPlayerSaving(participant, 0.0);
        }

        responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        responseObserver.onCompleted();
    }

    @Override
    public void playerSaving(Information.SavingEvent savingEvent, StreamObserver<Information.Ack> responseObserver) {

        if (Player.getInstance().getRole() == Role.HIDER){

            Hider.getInstance().increaseFinishedHiders();
            if (Hider.getInstance().getFinishedHidersCount() == Player.getInstance().getParticipantsCount() - 1 && (Player.getInstance().getState() == GameState.FINISHED)) {
                Player.getInstance().setState(GameState.GAME_END);
                System.out.println();
                System.out.println(" *** THE END! *** ");
            }
            responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());

        } else {

            System.out.println("The player " + savingEvent.getPlayerId() + " obtains the access to base after " + savingEvent.getTime() + " seconds");

            Seeker.getInstance().incrementFinishedHidersCount();
            double taggingTime = Seeker.getInstance().checkTaggingTime(savingEvent.getPlayerId());
            System.out.print("You tag the player " + savingEvent.getPlayerId() + " in " + taggingTime + " seconds, so ");

            if (taggingTime < savingEvent.getTime()) {
                System.out.println("he can be considered tagged");
                responseObserver.onNext(Information.Ack.newBuilder().setText("NO").build());
            } else {
                System.out.println("he can be considered safe");
                responseObserver.onNext(Information.Ack.newBuilder().setText("YES").build());
            }

            if (Seeker.getInstance().getFinishedHidersCount() == Player.getInstance().getParticipantsCount()) {
                Player.getInstance().setState(GameState.GAME_END);
                System.out.println();
                System.out.println(" *** THE END! *** ");
            }
        }

        responseObserver.onCompleted();
    }
}