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
            BufferedInputStream inputStream = new BufferedInputStream(clientSocket.getInputStream());

            RequestResponse request = RequestParser.parse(inputStream);
            RequestResponse response = requestResponseService.getResponse(request);
            byte[] responseBytes = ResponseParser.parse(response);
            clientSocket.getOutputStream().write(responseBytes);

        } catch (IOException e) {
            log.error("Error handling client communication", e);
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
