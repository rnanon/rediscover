package com.rahul.rediscover.socket.server;

import com.rahul.rediscover.service.RequestResponseService;
import com.rahul.rediscover.socket.protocol.RequestParser;
import com.rahul.rediscover.socket.protocol.ResponseParser;
import com.rahul.rediscover.socket.protocol.dto.RequestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;

@RequiredArgsConstructor
@Slf4j
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final RequestResponseService requestResponseService;

    @Override
    public void run() {
        try {
            log.info("New client connected: {}", clientSocket);
            BufferedInputStream inputStream = new BufferedInputStream(clientSocket.getInputStream());

            while (!clientSocket.isClosed() && clientSocket.isConnected()) {
                if (inputStream.available() > 0) {
                    log.debug("Data available: {} bytes", inputStream.available());

                    inputStream.mark(65536);

                    // Read the available data for debugging
                    byte[] debugBuffer = new byte[inputStream.available()];
                    inputStream.read(debugBuffer);

                    // Print the data for debugging
                    StringBuilder hexString = new StringBuilder();
                    for (byte b : debugBuffer) {
                        hexString.append(String.format("%02X ", b));
                    }
                    log.debug("Received data: {}", hexString);

                    inputStream.reset();

                    RequestResponse request = RequestParser.parse(inputStream);
                    RequestResponse response = requestResponseService.getResponse(request);
                    byte[] responseBytes = ResponseParser.parse(response);
                    clientSocket.getOutputStream().write(responseBytes);
                } else {
                    // No data available yet, sleep briefly to avoid CPU spinning
                    Thread.sleep(10);
                }
            }
        } catch (IOException e) {
            log.error("Error handling client communication", e);
        } catch (InterruptedException e) {
            log.error("Thread interrupted", e);
            Thread.currentThread().interrupt();
        } finally {
            try {
                log.info("Closing connection with {}", clientSocket.getInetAddress());
                clientSocket.close();
            } catch (IOException e) {
                log.error("Error closing client socket", e);
            }
        }
    }
}
