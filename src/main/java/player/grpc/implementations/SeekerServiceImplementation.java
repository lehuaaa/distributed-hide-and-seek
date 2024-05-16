package player.grpc.implementations;

import com.example.grpc.Seeker;
import com.example.grpc.SeekerServiceGrpc;
import io.grpc.stub.StreamObserver;
import player.grpc.handlers.HiderHandler;

public class SeekerServiceImplementation extends SeekerServiceGrpc.SeekerServiceImplBase {

    @Override
    public void catchHider(Seeker.AckSeeker ackSeeker, StreamObserver<Seeker.AckSeeker> responseObserver) {

        if (HiderHandler.getInstance().isSafe || HiderHandler.getInstance().hasAccessToTheBaseAndHeIsWaiting) {
            responseObserver.onNext(Seeker.AckSeeker.newBuilder().setText("NO").build());
        } else {
            System.out.println("Player caught by the seeker!");
            responseObserver.onNext(Seeker.AckSeeker.newBuilder().setText("CATCH").build());
            HiderHandler.getInstance().isCaught = true;
            HiderHandler.getInstance().emptyQueue();
        }

        responseObserver.onCompleted();
    }
}
