package it.marcodemartino.yapga.server.handler;

import it.marcodemartino.yapga.common.application.Application;
import it.marcodemartino.yapga.common.application.ApplicationIO;
import it.marcodemartino.yapga.common.commands.JsonCommandManager;
import it.marcodemartino.yapga.common.images.STDImageResizer;
import it.marcodemartino.yapga.common.json.JSONMethods;
import it.marcodemartino.yapga.common.services.EncryptionService;
import it.marcodemartino.yapga.common.services.ImageService;
import it.marcodemartino.yapga.server.commands.*;
import it.marcodemartino.yapga.server.services.CertificatesService;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class ClientHandler implements Application {

    private final Socket socket;
    private final ApplicationIO applicationIO;

    public ClientHandler(Socket socket, EncryptionService encryptionService, CertificatesService certificatesService) throws IOException {
        this.socket = socket;
        this.applicationIO = new ClientHandlerIO(socket.getInputStream(), socket.getOutputStream());

        JsonCommandManager commandManager = new JsonCommandManager();
        commandManager.registerCommand(JSONMethods.REQUEST_REMOTE_PUBLIC_KEY, new SendPublicKeyCommand(applicationIO, encryptionService));
        commandManager.registerCommand(JSONMethods.REQUEST_IDENTITY_CERTIFICATE, new SendIdentityCertificateCommand(applicationIO, encryptionService, certificatesService));
        commandManager.registerCommand(JSONMethods.ENCRYPTED_MESSAGE, new EncryptedMessageCommand(applicationIO.getEventManager(), encryptionService));
        commandManager.registerCommand(JSONMethods.SEND_IMAGE, new ReceiveImageCommand(getInputStream()));
        commandManager.registerCommand(JSONMethods.REQUEST_IMAGES, new SendImagesCommand(new ImageService(new STDImageResizer(), applicationIO, encryptionService)));
        this.applicationIO.registerInputListener(commandManager);
    }

    @Override
    public InputStream getInputStream() {
        try {
            return socket.getInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public ApplicationIO getIO() {
        return applicationIO;
    }

    @Override
    public void stop() {
        applicationIO.stop();
        tryClose();
    }

    private void tryClose() {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        applicationIO.start();
    }
}
