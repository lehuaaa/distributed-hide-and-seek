package player.game;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import player.game.implementations.ElectionServiceImplementation;
import player.game.implementations.BaseAccessServiceImplementation;
import player.game.implementations.InformationServiceImplementation;

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
            Server grpcServer = ServerBuilder.forPort(port).addService(new BaseAccessServiceImplementation())
                                                           .addService(new InformationServiceImplementation())
                                                           .addService(new ElectionServiceImplementation())
                                                           .build();
            grpcServer.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}