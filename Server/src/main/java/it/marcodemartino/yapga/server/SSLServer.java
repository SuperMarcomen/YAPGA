package it.marcodemartino.yapga.server;

import it.marcodemartino.yapga.common.application.Application;
import it.marcodemartino.yapga.server.handler.ClientHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public class SSLServer implements Server {

    private final Logger logger = LogManager.getLogger(SSLServer.class);
    private final int port;
    private final List<Application> clients;
    private boolean running;
    private SSLServerSocket serverSocket;

    public SSLServer(int port) {
        this.port = port;
        this.running = true;
        clients = new ArrayList<>();
    }

    @Override
    public void run() {
        tryStart();
    }

    @Override
    public void stop() {
        running = false;
        tryStop();
        for (Application client : clients) {
            client.stop();
        }
    }

    private void start(int port) throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyManagementException {
        SSLServerSocketFactory serverSocketFactory = getSslServerSocketFactory();
        serverSocket = (SSLServerSocket) serverSocketFactory.createServerSocket(port);
        enableSSLProtocols(serverSocket);
        logger.info("Started the SSL socket server on the IP: {}", serverSocket.getInetAddress());

        while (running) {
            if (serverSocket.isClosed()) return;
            SSLSocket clientSocket = acceptNewConnection();
            if (clientSocket == null) return;

            logger.info("Received a connection with IP: {}", clientSocket.getInetAddress());
            Application clientHandler = new ClientHandler(clientSocket);
            new Thread(clientHandler).start();
        }
    }

    private SSLSocket acceptNewConnection() throws IOException {
        try {
            return (SSLSocket) serverSocket.accept();
        } catch (SocketException e) {
            if (!running && serverSocket.isClosed()) return null;
            throw new RuntimeException(e);
        }
    }

    private static void enableSSLProtocols(SSLServerSocket serverSocket) {
        // Enable the desired SSL protocols and ciphers
        serverSocket.setEnabledProtocols(new String[]{"TLSv1.2"});
        serverSocket.setEnabledCipherSuites(new String[]{"TLS_RSA_WITH_AES_256_CBC_SHA"});
    }

    private static SSLServerSocketFactory getSslServerSocketFactory() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        KeyManagerFactory keyManagerFactory = getKeyManagerFactoryAndLoadKey();
        SSLContext sslContext = getSslContext(keyManagerFactory);
        return sslContext.getServerSocketFactory();
    }

    private static SSLContext getSslContext(KeyManagerFactory keyManagerFactory) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(keyManagerFactory.getKeyManagers(), null, null);
        return sslContext;
    }

    private static KeyManagerFactory getKeyManagerFactoryAndLoadKey() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
        char[] keystorePassword = "123456".toCharArray();
        KeyStore keystore = KeyStore.getInstance("JKS");
        keystore.load(new FileInputStream("keystore.jks"), keystorePassword);

        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        keyManagerFactory.init(keystore, keystorePassword);
        return keyManagerFactory;
    }

    private void tryStart() {
        try {
            start(port);
        } catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException |
                 UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    private void tryStop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // Handle the exception gracefully, e.g., log it, but don't re-throw it.
            e.printStackTrace(); // You can replace this with your preferred logging mechanism.
        }
    }
}
