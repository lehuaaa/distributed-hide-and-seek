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
            Server server = ServerBuilder.forPort(port).addService(new GameServiceImpl()).build();
            server.start();
            server.awaitTermination();
            Player.getInstance().contactOtherPLayers();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}