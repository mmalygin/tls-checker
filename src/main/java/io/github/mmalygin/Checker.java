package io.github.mmalygin;

import lombok.RequiredArgsConstructor;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.util.Arrays;

@RequiredArgsConstructor
public class Checker {
    private final String hostname;
    private final int port;
    private final int timeoutSecs;

    public void check() {
        try {
            SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            System.out.println("--------------------------------------------------------------");
            System.out.println("SSLSocketFactory.DefaultCipherSuites: " + Arrays.toString(
                    sslSocketFactory.getDefaultCipherSuites()));
            System.out.println("SSLSocketFactory.SupportedCipherSuites: " + Arrays.toString(
                    sslSocketFactory.getSupportedCipherSuites()));
            System.out.println("SSLSocketFactory.SupportedCipherSuites: " + Arrays.toString(
                    sslSocketFactory.getSupportedCipherSuites()));

            SSLContext context = SSLContext.getDefault();
            System.out.println("--------------------------------------------------------------");
            System.out.println("SSLContext.Protocol: " + context.getProtocol());
            System.out.println("SSLContext.Provider: " + context.getProvider());
            SocketAddress addr = new InetSocketAddress(hostname, port);
            try (SSLSocket socket = (SSLSocket) sslSocketFactory.createSocket()) {
                System.out.println("--------------------------------------------------------------");
                System.out.println("Trying to connect to " + hostname + ":" + port + "...");
                socket.connect(addr, timeoutSecs * 1000);
                System.out.println("Successfully connected!\n");
                SSLSession session = socket.getSession();
                System.out.println("--------------------------------------------------------------");
                System.out.println("SSLSession.PeerHost: " + session.getPeerHost());
                System.out.println("SSLSession.PeerPort: " + session.getPeerPort());
                System.out.println("SSLSession.Protocol: " + session.getProtocol());
                System.out.println("SSLSession.CipherSuite: " + session.getCipherSuite());
                System.out.println("SSLSession.LocalCertificates: " + Arrays.toString(session.getLocalCertificates()));
                System.out.println("SSLSession.PeerCertificates: " + Arrays.toString(session.getPeerCertificates()));
                System.out.println("SSLSession.ValueNames: " + Arrays.toString(session.getValueNames()));
                //System.out.println("Handshake Application Protocol: " + socket.getHandshakeApplicationProtocol());
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("--------------------------------------------------------------");
        try {
            URL url = new URL("https://" + hostname + ":" + port);
            System.out.println("Trying to connect to " + url + "...");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            System.out.println("Response Code: " + con.getResponseCode());
            System.out.println("Cipher Suite: " + con.getCipherSuite());
            System.out.println();
            Certificate[] certs = con.getServerCertificates();
            int i = 0;
            for (Certificate cert : certs) {
                System.out.println("-------- Cert " + ++i + " -------");
                System.out.println("Cert Type: " + cert.getType());
                System.out.println("Cert Hash Code: " + cert.hashCode());
                System.out.println("Cert Public Key Algorithm: " + cert.getPublicKey().getAlgorithm());
                System.out.println("Cert Public Key Format: " + cert.getPublicKey().getFormat());
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e);
            e.printStackTrace(System.out);
        }
    }
}
