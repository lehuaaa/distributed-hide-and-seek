package player.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import player.grpc.service.GameServiceImplementation;

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}