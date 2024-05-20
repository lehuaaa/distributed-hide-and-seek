package player.game.handlers;

import administration.server.beans.Node;
import com.example.grpc.Base;
import com.example.grpc.BaseAccessServiceGrpc;
import com.example.grpc.Information;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;
import player.domain.enums.GameState;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class HiderHandler extends Thread{

    private long timestampBaseRequest = Long.MAX_VALUE;

    private final Queue<String> playerIds;

    private int confirmationCount = 0;

    private double timePassed = 0;

    private int finishedHidersCount = 0;

    private static HiderHandler instance;

    private HiderHandler() {
        playerIds = new LinkedList<>();
    }

    public synchronized static HiderHandler getInstance() {
        if (instance == null) {
            instance = new HiderHandler();
        }
        return instance;
    }

    public long getTimestampBaseRequest() { return timestampBaseRequest; }

    private void generateTimestampBaseRequest() { timestampBaseRequest = System.currentTimeMillis(); }

    public int getConfirmationCount() { return confirmationCount; }

    public void increaseConfirmationCount() { confirmationCount++; }

    public int getFinishedHidersCount() { return finishedHidersCount; }

    public void increaseFinishedHidersCount() { finishedHidersCount++; }

    public double getTimePassed() { return timePassed; }

    public void setTimePassed(double timePassed) { this.timePassed = timePassed; }

    public synchronized void storeHiderId(String message) { playerIds.add(message); }

    @Override
    public void run() {
        requestBaseAccess();
    }

    public void requestBaseAccess() {
        generateTimestampBaseRequest();
        for (Participant p: Player.getInstance().getParticipants())
            sendBaseRequest(p);
    }

    public void sendBaseRequest(Participant participant) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(participant.getAddress() + ":" + participant.getPort()).usePlaintext().build();
        BaseAccessServiceGrpc.BaseAccessServiceStub stub = BaseAccessServiceGrpc.newStub(channel);
        Base.BaseRequest baseRequest = Base.BaseRequest.newBuilder().setPlayerId(Player.getInstance().getId()).setTimestamp(timestampBaseRequest).build();

        stub.requestBaseAccess(baseRequest, new StreamObserver<Base.AckConfirmation>() {

            @Override
            public void onNext(Base.AckConfirmation ackConfirmation) {
                if (ackConfirmation.getText().equals("YES") ) {
                    confirmationCount++;
                    timePassed = Math.max(timePassed, ackConfirmation.getTimePassed());
                    /* System.out.println("Confirmation access from " + participant.getId() + ", confirmation count: " + confirmationCount + " / " + Player.getInstance().getParticipantsCount()); */
                }

                if (confirmationCount == Player.getInstance().getParticipantsCount()) {
                    moveToBase();
                }
            }

            @Override
            public void onError(Throwable throwable) { System.out.println("Error: " + throwable.getMessage()); }

            @Override
            public void onCompleted() { channel.shutdownNow(); }

        });

        /* await response */
        try { channel.awaitTermination(10, TimeUnit.SECONDS); } catch (InterruptedException e) { throw new RuntimeException(e); }
    }

    public void moveToBase() {
        double timeWaited = timePassed;
        System.out.println("You obtain the access to the base after " + timeWaited + " seconds");

        Player.getInstance().setState(GameState.SAFE);
        timePassed += Player.getInstance().getCoordinate().getDistanceFromBase() + 10;

        System.out.println("You can be considered safe after " + timePassed + " seconds");

        InformationHandler.getInstance().informPlayersOfSaving(timeWaited);
        sendBackConfirmationsToStoredHiders();
    }

    public void sendBackConfirmationsToStoredHiders() {
        while (!playerIds.isEmpty()) {
            Node node = Player.getInstance().getParticipant(playerIds.poll());
            sendBackConfirmation(node);
        }
    }

    private void sendBackConfirmation(Node node) {
        ManagedChannel channel = ManagedChannelBuilder.forTarget(node.getAddress() + ":" + node.getPort()).usePlaintext().build();
        BaseAccessServiceGrpc.BaseAccessServiceStub stub = BaseAccessServiceGrpc.newStub(channel);
        Base.AckConfirmation confirmation = Base.AckConfirmation.newBuilder().setTimePassed(timePassed).setText("YES").build();

        stub.sendBackConfirmation(confirmation, new StreamObserver<Information.Ack>() {

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