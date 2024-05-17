package player.grpc.handlers;

import administration.server.beans.Node;
import com.example.grpc.Hider;
import com.example.grpc.HiderServiceGrpc;
import com.example.grpc.Information;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;
import player.domain.enums.State;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class HiderHandler {

    private static HiderHandler instance;

    private final Queue<String> playerIds;

    private Hider.BaseRequest baseRequest;

    private int ackCount = 0;

    private HiderHandler() {
        playerIds = new LinkedList<>();
    }

    public synchronized static HiderHandler getInstance() {
        if (instance == null) {
            instance = new HiderHandler();
        }
        return instance;
    }

    public int getAckCount() { return ackCount; }

    public void increaseAckCount() { ackCount++; }

    public long getBaseRequestTimestamp() {
        if (baseRequest == null) {
            return Long.MAX_VALUE;
        }
        return baseRequest.getTimestamp();
    }

    public synchronized void storeHiderId(String message) {
        playerIds.add(message);
    }

    private void generateBaseRequest() {
        baseRequest = Hider.BaseRequest.newBuilder()
                .setPlayerId(Player.getInstance().getId())
                .setTimestamp(System.currentTimeMillis())
                .build();
    }

    public void requestBaseAccess() {
        generateBaseRequest();
        for (Participant p: Player.getInstance().getParticipants())
            sendBaseRequest(p);
    }

    private void sendBaseRequest(Node node) {
        /* System.out.println("Request base access to player " + node.getId()); */
        ManagedChannel channel = ManagedChannelBuilder.forTarget(node.getAddress() + ":" + node.getPort()).usePlaintext().build();
        HiderServiceGrpc.HiderServiceStub stub = HiderServiceGrpc.newStub(channel);

        stub.requestBaseAccess(baseRequest, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) {
                if (ack.getText().equals("YES") ) {
                    ackCount++;
                }

                if (ackCount == Player.getInstance().getParticipantsCount() && Player.getInstance().getState() != State.TAGGED) {
                    ackCount = 0;
                    moveToBase();
                }
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

    public void moveToBase() {
        System.out.println("Player has access to the base!");
        Player.getInstance().setState(State.TOWARDS_TO_BASE);
        /*
        try {
            Thread.sleep(Math.round(Player.getInstance().getCoordinate().getDistanceFromBase()) * 1000L + 10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        */
        System.out.println("Waiting " + Math.round(Player.getInstance().getCoordinate().getDistanceFromBase()) + 10 + " seconds to get to the base");
        Player.getInstance().setState(State.SAFE);
        System.out.println("Player is safe!");
        sendAckToStoredHiders();
    }

    public void sendAckToStoredHiders() {
        /* System.out.println("Player is safe and start send back the ack"); */
        while (!playerIds.isEmpty()) {
            Node node = Player.getInstance().getParticipant(playerIds.poll());
            sendAck(node);
        }
    }

    private void sendAck(Node node) {
        /* System.out.println("Send back Ack to player " + node.getId()); */
        ManagedChannel channel = ManagedChannelBuilder.forTarget(node.getAddress() + ":" + node.getPort()).usePlaintext().build();
        HiderServiceGrpc.HiderServiceStub stub = HiderServiceGrpc.newStub(channel);
        Information.Ack ack = Information.Ack.newBuilder().setText("YES").build();

        stub.sendBackAck(ack, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) { /* Am I interested in the response */ }

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