package player.domain;

import administration.server.beans.Node;
import administration.server.beans.Coordinate;
import com.example.grpc.Game;
import com.example.grpc.GameServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Player extends Participant {

    private String serverAddress;

    private List<Participant> participants;

    private static Player instance;

    public static Player getInstance() {
        if(instance == null) {
            instance = new Player();
        }
        return instance;
    }

    public void init(Node node, String serverAddress, Coordinate coordinate, List<Node> participants) {
        this.id = node.getId();
        this.address = node.getAddress();
        this.port = node.getPort();
        this.serverAddress = serverAddress;
        this.coordinate = new Coordinate(coordinate.getX(), coordinate.getY());
        setParticipants(participants);
    }

    private void setParticipants(List<Node> participants) {
        if (participants == null) {
            this.participants = new ArrayList<>();
        } else {
            this.participants = new ArrayList<>();
            for (Node n : participants) {
                this.participants.add(new Participant(n));
            }
        }
    }

    public synchronized void storeNewParticipant(String id, String address, int port, int x, int y) {
        participants.add(new Participant(id, address, port, x, y));
    }

    public synchronized List<Node> getParticipants() {
        return new ArrayList<>(participants);
    }

    public void informParticipants() {
        for (int i = 0; i < participants.size(); i++) {

            final int index = i;
            final ManagedChannel channel =
                    ManagedChannelBuilder.forTarget(
                            participants.get(index).getAddress() + ":" + participants.get(index).getPort()).usePlaintext().build();

            GameServiceGrpc.GameServiceStub stub = GameServiceGrpc.newStub(channel);

            Game.PresentationMessage presentationMessage = Game.PresentationMessage.newBuilder()
                    .setId(id)
                    .setAddress(address)
                    .setPort(port)
                    .setCoordinate(Game.Coordinate.newBuilder().setX(coordinate.getX()).setY(coordinate.getY()).build())
                    .build();

            stub.storePLayer(presentationMessage, new StreamObserver<Game.CoordinateResponse>() {

                @Override
                public void onNext(Game.CoordinateResponse coordinateResponse) {
                    Game.Coordinate result = coordinateResponse.getCoordinate();
                    participants.get(index).setCoordinate(new Coordinate(result.getX(), result.getY()));
                }

                public void onError(Throwable throwable) {
                    System.out.println("Error: " + throwable.getMessage());
                }

                public void onCompleted() {
                    channel.shutdownNow();
                }
            });

            /* you need this. otherwise the method will terminate before that answers from the server are received */
            try {
                channel.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}