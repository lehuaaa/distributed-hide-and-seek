package player.game.implementations;

import com.example.grpc.Base;
import com.example.grpc.BaseAccessServiceGrpc;
import com.example.grpc.Information;
import io.grpc.stub.StreamObserver;
import player.game.domain.singletons.Hider;
import player.game.domain.singletons.Player;
import player.game.domain.enums.Role;
import player.game.domain.enums.GameState;

public class BaseAccessServiceImplementation extends BaseAccessServiceGrpc.BaseAccessServiceImplBase {

    @Override
    public void requestBaseAccess(Base.BaseRequest baseRequest, StreamObserver<Base.AckConfirmation> responseObserver) {

        if (Player.getInstance().getRole() == Role.SEEKER) {
            responseObserver.onNext(Base.AckConfirmation.newBuilder().setText("YES").setTimePassed(Hider.getInstance().getTimePassedToReachBase()).build());
        } else {
            if (Player.getInstance().getState() == GameState.REACHING_BASE ||
                    (Player.getInstance().getState() == GameState.IN_GAME && Hider.getInstance().getTimestampBaseRequest() < baseRequest.getTimestamp()))
            {
                Hider.getInstance().storeWaitingHider(baseRequest.getPlayerId());
                responseObserver.onNext(Base.AckConfirmation.newBuilder().setText("NO").setTimePassed(Hider.getInstance().getTimePassedToReachBase()).build());
            } else {
                responseObserver.onNext(Base.AckConfirmation.newBuilder().setText("YES").setTimePassed(Hider.getInstance().getTimePassedToReachBase()).build());
            }
        }

        responseObserver.onCompleted();
    }

    @Override
    public void sendBackConfirmation(Base.Confirmation confirmation, StreamObserver<Information.Ack> responseObserver) {

        responseObserver.onNext(Information.Ack.newBuilder().setText("OK").build());
        responseObserver.onCompleted();

        Hider.getInstance().addConfirmation(confirmation.getPlayerId());

        System.out.println("Confirmation BACK from " + confirmation.getPlayerId() + ", confirmation count: "
                + Hider.getInstance().getConfirmationsCount() + " / " + Player.getInstance().getParticipantsCount());

        Hider.getInstance().setTimePassed(confirmation.getTimePassed());

        if (Hider.getInstance().getConfirmationsCount() == Player.getInstance().getParticipantsCount()) {
            Player.getInstance().setState(GameState.REACHING_BASE);
            Hider.getInstance().start();
        }
    }
}