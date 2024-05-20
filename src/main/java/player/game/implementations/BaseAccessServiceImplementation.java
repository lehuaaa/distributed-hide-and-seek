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
        if (Player.getInstance().getRole() == Role.SEEKER ||
                Player.getInstance().getState() == GameState.TAGGED ||
                Player.getInstance().getState() == GameState.SAFE ||
                Player.getInstance().getState() == GameState.ELECTION ||
                baseRequest.getTimestamp() < HiderHandler.getInstance().getTimestampBaseRequest())
        {
            responseObserver.onNext(Base.AckConfirmation.newBuilder().setText("YES").setTimePassed(HiderHandler.getInstance().getTimePassed()).build());
        } else {
            HiderHandler.getInstance().storeHiderId(baseRequest.getPlayerId());
            responseObserver.onNext(Base.AckConfirmation.newBuilder().setText("NO").setTimePassed(HiderHandler.getInstance().getTimePassed()).build());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void sendBackConfirmation(Base.AckConfirmation confirmation, StreamObserver<Information.Ack> responseObserver) {

        responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        responseObserver.onCompleted();

        HiderHandler.getInstance().increaseConfirmationCount();

        if (HiderHandler.getInstance().getConfirmationCount() == Player.getInstance().getParticipantsCount() && Player.getInstance().getState() != GameState.TAGGED) {
            HiderHandler.getInstance().setTimePassed(confirmation.getTimePassed());
            HiderHandler.getInstance().moveToBase();
        }
    }
}