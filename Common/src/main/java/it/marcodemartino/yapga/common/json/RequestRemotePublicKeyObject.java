package it.marcodemartino.yapga.common.json;

public class RequestRemotePublicKeyObject implements JSONObject{

    private final JSONMethods method = JSONMethods.REQUEST_REMOTE_PUBLIC_KEY;

    @Override
    public JSONMethods getMethod() {
        return method;
    }
}
