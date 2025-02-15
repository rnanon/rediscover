package com.rahul.rediscover.socket.server;

import com.rahul.rediscover.service.RequestResponseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

@RequiredArgsConstructor
@Slf4j
public class SocketServer {

    private final int port;
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private final RequestResponseService requestResponseService;

    public void start() {
        log.info("Starting socket server on port {}", port);
        running = true;
        new Thread(this::acceptConnections).start();
    }

    private void acceptConnections() {
        try {
            serverSocket = new ServerSocket(port);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    log.info("New client connected: {}", clientSocket.getInetAddress());
                    new Thread(() -> new ClientHandler(clientSocket, requestResponseService).run()).start();
                } catch (IOException e) {
                    if (running) {
                        log.error("Exception while accepting connection", e);
                    }
                }
            }
        } catch (IOException e) {
            log.error("Could not start server socket", e);
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        running = false;
        if (serverSocket != null) {
            try {
                log.info("Closing server on port {}", serverSocket.getLocalPort());
                serverSocket.close();
            } catch (IOException e) {
                log.error("Error closing server socket", e);
            }
        }
    }


}
