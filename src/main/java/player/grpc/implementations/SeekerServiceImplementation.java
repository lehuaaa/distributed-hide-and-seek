package player.grpc.implementations;

import com.example.grpc.Information;
import com.example.grpc.SeekerServiceGrpc;
import io.grpc.stub.StreamObserver;
import player.domain.Player;
import player.domain.enums.State;
import player.grpc.handlers.HiderHandler;

public class SeekerServiceImplementation extends SeekerServiceGrpc.SeekerServiceImplBase {

    @Override
    public void catchHider(Information.Ack ack, StreamObserver<Information.Ack> responseObserver) {

        if (Player.getInstance().getState() == State.SAFE || Player.getInstance().getState() == State.TOWARDS_TO_BASE) {
            responseObserver.onNext(Information.Ack.newBuilder().setText("NO").build());
            responseObserver.onCompleted();
        } else {
            System.out.println("Player caught by the seeker!");
            responseObserver.onNext(Information.Ack.newBuilder().setText("CATCH").build());
            responseObserver.onCompleted();
            Player.getInstance().setState(State.TAGGED);
            HiderHandler.getInstance().sendAckToStoredHiders();
        }
    }
}
