package it.marcodemartino.yapga.common.json;

public class SendImageObject implements JSONObject {

    private final JSONMethods method = JSONMethods.SEND_IMAGE;
    private final String fileName;
    private final long fileSize;

    public SendImageObject(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
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
}
