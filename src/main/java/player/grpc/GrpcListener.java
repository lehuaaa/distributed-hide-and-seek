package player.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import player.domain.Player;
import player.grpc.implamentation.GameServiceImpl;

import java.io.IOException;

public class GrpcListener {

    private static GrpcListener instance;

    public static GrpcListener getInstance() {
        if (instance == null) {
            instance = new GrpcListener();
        }
        return instance;
    }

    public void start(int port)
    {
        try {
            Server grpcServer = ServerBuilder.forPort(port).addService(new GameServiceImpl()).build();
            grpcServer.start();
            Player.getInstance().informParticipants();
            //grpcServer.awaitTermination();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}