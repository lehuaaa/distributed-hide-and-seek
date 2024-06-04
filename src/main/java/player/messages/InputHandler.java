package player.messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputHandler extends Thread {

    private final BufferedReader inputReader;

    public InputHandler() {
        this.inputReader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            try {
                inputReader.readLine();
            } catch ( IOException e) {
                break;
            }
        }
    }
}