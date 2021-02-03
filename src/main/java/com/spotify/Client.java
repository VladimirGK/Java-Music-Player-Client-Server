package com.spotify;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Client {
    private static final Logger logger = LogManager.getLogger(Client.class);
    private static final int SERVER_PORT = 6666;
    private static final String SERVER_HOST = "localhost";
    private static ByteBuffer buffer = ByteBuffer.allocateDirect(512);

    public static void main(String[] args) {

        try (SocketChannel socketChannel = SocketChannel.open(); Scanner scanner = new Scanner(System.in)) {

            socketChannel.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT));

            while (true) {
                String message = scanner.nextLine(); // read a line from the console

                if ("quit".equals(message)) {
                    break;
                }

                buffer.clear(); // switch to writing mode
                buffer.put(message.getBytes()); // buffer fill
                buffer.flip(); // switch to reading mode
                socketChannel.write(buffer); // buffer drain

                buffer.clear(); // switch to writing mode
                socketChannel.read(buffer); // buffer fill
                buffer.flip(); // switch to reading mode

                byte[] byteArray = new byte[buffer.remaining()];
                buffer.get(byteArray);
                String reply = new String(byteArray, StandardCharsets.UTF_8); // buffer drain

                // if buffer is a non-direct one, is has a wrapped array and we can get it
                //String reply = new String(buffer.array(), 0, buffer.position(), "UTF-8"); // buffer drain

                System.out.println(reply);
            }

        } catch (IOException e) {
            logger.error("There is a problem with the network communication");
            e.printStackTrace();
        }
    }
}
