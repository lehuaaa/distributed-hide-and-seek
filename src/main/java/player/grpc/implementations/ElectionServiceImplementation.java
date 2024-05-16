package player.grpc.implementations;

import com.example.grpc.Election;
import com.example.grpc.ElectionServiceGrpc;
import io.grpc.stub.StreamObserver;
import player.domain.Player;
import player.grpc.handlers.ElectionHandler;
import player.grpc.handlers.HiderHandler;
import player.grpc.handlers.SeekerHandler;

public class ElectionServiceImplementation extends ElectionServiceGrpc.ElectionServiceImplBase {

    @Override
    public void election(Election.ElectionMessage electionMessage, StreamObserver<Election.ElectionAck> responseObserver) {

        String playerId = Player.getInstance().getId();
        String messagePlayerId = electionMessage.getPlayerId();
        double playerDistanceFromBase = Player.getInstance().getCoordinate().getDistanceFromBase();
        double messageDistanceFromBase = electionMessage.getDistanceFromBase();

        /* ELECTION message.proto case */
        if (electionMessage.getType().equals("ELECTION")) {

            /* Player receives ELECTION with his own ID */
            if (Player.getInstance().getId().equals(messagePlayerId)) {

                /* Player send ELECTED message.proto */
                if (!Player.getInstance().isSeeker) {
                    Player.getInstance().isSeeker = true;
                    ElectionHandler.getInstance().forwardMessage("ELECTED", playerId, playerDistanceFromBase);
                }

                /* Player replaces message.proto parameters with his own ID and his distance from the base */
            } else if ((playerDistanceFromBase < messageDistanceFromBase || (playerDistanceFromBase == messageDistanceFromBase && playerId.compareTo(messagePlayerId) > 0)) && !Player.getInstance().hasParticipatedToElection) {
                ElectionHandler.getInstance().forwardMessage("ELECTION", playerId, playerDistanceFromBase);
                Player.getInstance().hasParticipatedToElection = true;

                /* Player forwards message.proto without modifying the parameters */
            } else if (messageDistanceFromBase < playerDistanceFromBase || (playerDistanceFromBase == messageDistanceFromBase && messagePlayerId.compareTo(playerId) > 0)) {
                ElectionHandler.getInstance().forwardMessage("ELECTION", messagePlayerId, messageDistanceFromBase);
                Player.getInstance().hasParticipatedToElection = true;
            }

            /* ELECTED message.proto case */
        } else {

            /* Player receives his ELECTED message.proto */
            if (Player.getInstance().getId().equals(messagePlayerId)) {
                System.out.println("You are the seeker");
                SeekerHandler.getInstance().startHunting();

            } else {
                Player.getInstance().hasParticipatedToElection = false;
                System.out.println("The seeker is the player " + messagePlayerId);
                Player.getInstance().setSeekerId(messagePlayerId);
                ElectionHandler.getInstance().forwardMessage("ELECTED", messagePlayerId, messageDistanceFromBase);
                HiderHandler.getInstance().requestAccessBase();
            }
        }

        responseObserver.onNext(Election.ElectionAck.newBuilder().setText("OK").build());
        responseObserver.onCompleted();
    }
}
