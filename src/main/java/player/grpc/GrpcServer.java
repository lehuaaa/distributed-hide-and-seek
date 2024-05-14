package player.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import player.grpc.handlers.PresentationMessagesHandler;
import player.grpc.services.GameServiceImplementation;

import java.io.IOException;

public class GrpcServer {

    private static GrpcServer instance;

    public static GrpcServer getInstance() {
        if (instance == null) {
            instance = new GrpcServer();
        }
        return instance;
    }

    public void start(int port)
    {
        try {
            Server grpcServer = ServerBuilder.forPort(port).addService(new GameServiceImplementation()).build();
            grpcServer.start();
            PresentationMessagesHandler.getInstance().start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}