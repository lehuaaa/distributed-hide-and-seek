package player.grpc.implementations;

import com.example.grpc.Hider;
import com.example.grpc.HiderServiceGrpc;
import com.example.grpc.Information;
import io.grpc.stub.StreamObserver;
import player.domain.Player;
import player.domain.enums.Role;
import player.domain.enums.State;
import player.grpc.handlers.HiderHandler;

public class HiderServiceImplementation extends HiderServiceGrpc.HiderServiceImplBase {

    @Override
    public void requestBaseAccess(Hider.BaseRequest baseRequest, StreamObserver<Information.Ack> responseObserver) {

        /* System.out.println("Player " + baseAccessMessage.getPlayerId() + " requested access to the base with a timestamp " + baseAccessMessage.getTimestamp()); */

        if (Player.getInstance().getRole() == Role.SEEKER ||
                Player.getInstance().getState() == State.TAGGED ||
                Player.getInstance().getState() == State.SAFE ||
                baseRequest.getTimestamp() < HiderHandler.getInstance().getBaseRequestTimestamp())
        {
            /* System.out.println("Player " + Player.getInstance().getId() + " respond OK to the player " + baseAccessMessage.getPlayerId()); */
            responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        } else {
            /* System.out.println("Player " + Player.getInstance().getId() + " respond NO to the player " + baseAccessMessage.getPlayerId()); */
            HiderHandler.getInstance().storeHiderId(baseRequest.getPlayerId());
            responseObserver.onNext(Information.Ack.newBuilder().setText("NO").build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void sendBackAck(Information.Ack ack, StreamObserver<Information.Ack> responseObserver) {

        responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        responseObserver.onCompleted();

        HiderHandler.getInstance().increaseAckCount();

        if (HiderHandler.getInstance().getAckCount() == Player.getInstance().getParticipantsCount() && Player.getInstance().getState() != State.TAGGED) {
            HiderHandler.getInstance().moveToBase();
        }
    }
}