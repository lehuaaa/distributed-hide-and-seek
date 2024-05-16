package player.grpc.implementations;

import com.example.grpc.Hider;
import com.example.grpc.HiderServiceGrpc;
import io.grpc.stub.StreamObserver;
import player.domain.Player;
import player.grpc.handlers.HiderHandler;

public class HiderServiceImplementation extends HiderServiceGrpc.HiderServiceImplBase {

    @Override
    public void requestBaseAccess(Hider.BaseRequest baseRequest, StreamObserver<Hider.AckHider> responseObserver) {

        /* System.out.println("Player " + baseAccessMessage.getPlayerId() + " requested access to the base with a timestamp " + baseAccessMessage.getTimestamp()); */

        if (HiderHandler.getInstance().isCaught || HiderHandler.getInstance().isSafe || baseRequest.getTimestamp() < HiderHandler.getInstance().getBaseRequest().getTimestamp()) {
            /* System.out.println("Player " + Player.getInstance().getId() + " respond OK to the player " + baseAccessMessage.getPlayerId()); */
            responseObserver.onNext(Hider.AckHider.newBuilder().setText("OK").build());
        } else {
            /* System.out.println("Player " + Player.getInstance().getId() + " respond NO to the player " + baseAccessMessage.getPlayerId()); */
            HiderHandler.getInstance().storePlayerId(baseRequest.getPlayerId());
            responseObserver.onNext(Hider.AckHider.newBuilder().setText("NO").build());
        }

        responseObserver.onCompleted();
    }

    @Override
    public void sendBackAck(Hider.AckHider ackHider, StreamObserver<Hider.AckHider> responseObserver) {
        HiderHandler.getInstance().increaseAckCount();

        if (HiderHandler.getInstance().getAckCount() == Player.getInstance().getParticipantsCount() - 1 && !HiderHandler.getInstance().isCaught) {
            System.out.println("Player has access to the base!");
            HiderHandler.getInstance().hasAccessToTheBaseAndHeIsWaiting = true;

            try {
                Thread.sleep(Math.round(Player.getInstance().getCoordinate().getDistanceFromBase()) * 1000L + 10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Player is safe!");
            HiderHandler.getInstance().hasAccessToTheBaseAndHeIsWaiting = false;
            HiderHandler.getInstance().isSafe = true;
            HiderHandler.getInstance().emptyQueue();
        }

        responseObserver.onNext(Hider.AckHider.newBuilder().setText("OK").build());
        responseObserver.onCompleted();
    }
}