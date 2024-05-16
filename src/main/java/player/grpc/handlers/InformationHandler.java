package player.grpc.handlers;


import administration.server.beans.Coordinate;
import com.example.grpc.Information;
import com.example.grpc.InformationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class InformationHandler {

    private static InformationHandler instance;

    public static InformationHandler getInstance() {
        if (instance == null) {
            instance = new InformationHandler();
        }
        return instance;
    }

    public void presentPlayerToOthers() {
        List<Participant> participants = Player.getInstance().getParticipants();
        for (int i = 0; i < participants.size(); i++) {
            sendPlayerInfo(i, participants.get(i), i == participants.size() - 1);
        }
    }

    private void sendPlayerInfo(int index, Participant participant, boolean last) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        InformationServiceGrpc.InformationServiceStub stub = InformationServiceGrpc.newStub(channel);

        Information.PlayerInfo playerInfo = Information.PlayerInfo.newBuilder()
                .setId(Player.getInstance().getId())
                .setAddress(Player.getInstance().getAddress())
                .setPort(Player.getInstance().getPort())
                .setCoordinate(Information.Coordinate.newBuilder().setX(Player.getInstance().getCoordinate().getX()).setY(Player.getInstance().getCoordinate().getY()).build())
                .setIsNextNode(last)
                .build();

        stub.playerPresentation(playerInfo, new StreamObserver<Information.Coordinate>() {

            @Override
            public void onNext(Information.Coordinate coordinate) {
                Player.getInstance().setParticipantCoordinate(index, new Coordinate(coordinate.getX(), coordinate.getY()));
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                channel.shutdownNow();
            }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }
}