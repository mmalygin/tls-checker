package io.github.mmalygin;

import lombok.RequiredArgsConstructor;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;

@RequiredArgsConstructor
public class Checker {
    private final String hostname;
    private final int port;
    private final int timeoutSecs;

    public void check() {
        try {
            var sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SocketAddress addr = new InetSocketAddress(hostname, port);
            try (var socket = (SSLSocket) sslSocketFactory.createSocket()) {
                System.out.println("Trying to connect to " + hostname + ":" + port + "...");
                socket.connect(addr, timeoutSecs * 1000);
                System.out.println("Successfully connected!\n");
                var session = socket.getSession();
                System.out.println("Protocol: " + session.getProtocol());
                System.out.println("Cipher Suite: " + session.getCipherSuite());
                System.out.println("Local Certificates: " + Arrays.toString(session.getLocalCertificates()));
                System.out.println("Local Certificates: " + Arrays.toString(session.getLocalCertificates()));
                System.out.println("Handshake Application Protocol: " + socket.getHandshakeApplicationProtocol());

            }
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace(System.out);
        }
    }

}
