package player.game.implementations;

import com.example.grpc.Base;
import com.example.grpc.BaseAccessServiceGrpc;
import com.example.grpc.Information;
import io.grpc.stub.StreamObserver;
import player.domain.Player;
import player.domain.enums.Role;
import player.domain.enums.GameState;
import player.game.handlers.HiderHandler;

public class BaseAccessServiceImplementation extends BaseAccessServiceGrpc.BaseAccessServiceImplBase {

    @Override
    public void requestBaseAccess(Base.BaseRequest baseRequest, StreamObserver<Base.AckConfirmation> responseObserver) {

        if ((Player.getInstance().getState() != GameState.IN_GAME && Player.getInstance().getState() != GameState.GAME_END) ||
                Player.getInstance().getRole() == Role.SEEKER || baseRequest.getTimestamp() < HiderHandler.getInstance().getTimestampBaseRequest())
        {
            responseObserver.onNext(Base.AckConfirmation.newBuilder().setText("YES").setTimePassed(HiderHandler.getInstance().getTimePassed()).build());
        } else {
            HiderHandler.getInstance().storeHiderId(baseRequest.getPlayerId());
            responseObserver.onNext(Base.AckConfirmation.newBuilder().setText("NO").setTimePassed(HiderHandler.getInstance().getTimePassed()).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void sendBackConfirmation(Base.Confirmation confirmation, StreamObserver<Information.Ack> responseObserver) {

        responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        responseObserver.onCompleted();

        HiderHandler.getInstance().addConfirmationCount(confirmation.getPlayerId());
        HiderHandler.getInstance().setTimePassed(confirmation.getTimePassed());

        if (HiderHandler.getInstance().getConfirmationCount() == Player.getInstance().getParticipantsCount()) {
            HiderHandler.getInstance().moveToBase();
        }
    }
}