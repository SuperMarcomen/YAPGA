package it.marcodemartino.yapga.common.json;

import java.util.UUID;

public class SendImageObject implements JSONObject {

    private final JSONMethods method = JSONMethods.SEND_IMAGE;
    private final String fileName;
    private final long millisCreationDate;
    private final long fileSize;
    private final UUID senderUUID;

    public SendImageObject(String fileName, long millisCreationDate, long fileSize, UUID senderUUID) {
        this.fileName = fileName;
        this.millisCreationDate = millisCreationDate;
        this.fileSize = fileSize;
        this.senderUUID = senderUUID;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public UUID getSenderUUID() {
        return senderUUID;
    }

    public long getMillisCreationDate() {
        return millisCreationDate;
    }
}
