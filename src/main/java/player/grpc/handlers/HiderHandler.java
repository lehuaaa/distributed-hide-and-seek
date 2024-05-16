package player.grpc.handlers;

import administration.server.beans.Node;
import com.example.grpc.Hider;
import com.example.grpc.HiderServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class HiderHandler {

    private static HiderHandler instance;

    private final Hider.BaseRequest baseRequest;

    private final Queue<String> playerIds;
    public boolean isCaught = false;

    private int ackCount = 0;

    public boolean hasAccessToTheBaseAndHeIsWaiting = false;

    public boolean isSafe = false;

    private HiderHandler() {
        playerIds = new LinkedList<>();
        baseRequest = Hider.BaseRequest.newBuilder()
                                                  .setPlayerId(Player.getInstance().getId())
                                                  .setTimestamp(System.currentTimeMillis())
                                                  .build();
        System.out.println("The base access message.proto of player " + Player.getInstance().getId() + " has a timestamp " + baseRequest.getTimestamp());
    }

    public synchronized static HiderHandler getInstance() {
        if (instance == null) {
            instance = new HiderHandler();
        }
        return instance;
    }

    public Hider.BaseRequest getBaseRequest() {
        return baseRequest;
    }


    public int getAckCount() {
        return ackCount;
    }

    public synchronized void storePlayerId(String message) {
        playerIds.add(message);
    }

    public void requestAccessBase() {
        for  (Participant p: Player.getInstance().getParticipants()) {
            if (!p.getId().equals(Player.getInstance().getSeekerId()))
                sendBaseAccessRequest(p);
        }
    }

    public void emptyQueue() {
        /* System.out.println("Player is safe and start send back the ack"); */
        while (!playerIds.isEmpty()) {
            Node node = Player.getInstance().getParticipantCommunicationInfo(playerIds.poll());
            if (node != null) {
                sendAck(node);
            }
        }
    }

    public void increaseAckCount() {
        ackCount++;
    }

    private void sendAck(Node node) {
        /* System.out.println("Send back Ack to player " + node.getId()); */
        ManagedChannel channel = ManagedChannelBuilder.forTarget(node.getAddress() + ":" + node.getPort()).usePlaintext().build();
        HiderServiceGrpc.HiderServiceStub stub = HiderServiceGrpc.newStub(channel);
        Hider.AckHider ackHider = Hider.AckHider.newBuilder().setText("OK").build();

        stub.sendBackAck(ackHider, new StreamObserver<Hider.AckHider>() {

            @Override
            public void onNext(Hider.AckHider ackHider) {
                /* Am I interested in the response ??? */
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

    private void sendBaseAccessRequest(Node node) {

        /* System.out.println("Request base access to player " + node.getId()); */
        ManagedChannel channel = ManagedChannelBuilder.forTarget(node.getAddress() + ":" + node.getPort()).usePlaintext().build();
        HiderServiceGrpc.HiderServiceStub stub = HiderServiceGrpc.newStub(channel);

        stub.requestBaseAccess(baseRequest, new StreamObserver<Hider.AckHider>() {

            @Override
            public void onNext(Hider.AckHider ackHider) {
                if (ackHider.getText().equals("OK") ) {
                    ackCount++;
                }

                if (ackCount == Player.getInstance().getParticipantsCount() - 1 && !HiderHandler.getInstance().isCaught) {
                    System.out.println("Player has access to the base!");
                    hasAccessToTheBaseAndHeIsWaiting = true;

                    try {
                        Thread.sleep(Math.round(Player.getInstance().getCoordinate().getDistanceFromBase()) * 1000L + 10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println("Player is safe!");
                    hasAccessToTheBaseAndHeIsWaiting = false;
                    isSafe = true;
                    emptyQueue();
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
}