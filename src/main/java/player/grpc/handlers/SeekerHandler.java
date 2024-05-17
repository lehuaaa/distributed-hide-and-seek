package player.grpc.handlers;

import com.example.grpc.Information;
import com.example.grpc.SeekerServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import player.domain.Participant;
import player.domain.Player;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SeekerHandler {

    private static SeekerHandler instance;

    public Map<String, Participant> hiders;

    private SeekerHandler() {
        hiders = Player.getInstance().getParticipants().stream().collect(Collectors.toMap(Participant::getId, participant -> participant));
    }

    public static SeekerHandler getInstance() {
        if (instance == null) {
            instance = new SeekerHandler();
        }
        return instance;
    }

    public synchronized Participant removeHider(String hiderId) {
        return hiders.remove(hiderId);
    }

    public synchronized void addNewHider(Participant newHider) {
        hiders.put(newHider.getId(), newHider);
    }

    public synchronized boolean isHidersEmpty() {
        return hiders.isEmpty();
    }

    public void startHunting() {
        while (!isHidersEmpty()) {
            Participant nearestParticipant = getNearestHider();
            sendCatchMessage(nearestParticipant);
        }
    }

    private Participant getNearestHider() {
        double minDistance = Double.MAX_VALUE;
        Participant hider = new Participant();

        for (Participant p : hiders.values()) {
            double distance = Player.getInstance().getCoordinate().getDistanceFromSecondPoint(p.getCoordinate());
            if (distance < minDistance) {
                minDistance = distance;
                hider = p;
            }
        }


        System.out.println("Your Waiting " + Math.round(minDistance) + " to reach player " + hider.getId() );

        /* The seeker is moving to the nearest hider
        try {
            Thread.sleep(Math.round(minDistance) * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        */

        Player.getInstance().setCoordinate(hider.getCoordinate());
        return removeHider(hider.getId());
    }

    private void sendCatchMessage(Participant hider) {

        ManagedChannel channel = ManagedChannelBuilder.forTarget(hider.getAddress() + ":" + hider.getPort()).usePlaintext().build();
        SeekerServiceGrpc.SeekerServiceStub stub = SeekerServiceGrpc.newStub(channel);

        Information.Ack ackSeeker = Information.Ack.newBuilder()
                .setText("CATCH")
                .build();

        stub.catchHider(ackSeeker, new StreamObserver<Information.Ack>() {

            @Override
            public void onNext(Information.Ack ack) {
                if (ackSeeker.getText().equals("CATCH")) {
                    System.out.println("You catch the player : " + hider.getId());
                } else {
                    System.out.println("Player " + hider.getId() + " was safe ");
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
