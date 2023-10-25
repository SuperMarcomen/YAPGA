package it.marcodemartino.yapga.common.json;

public class EmptyJsonObject implements JSONObject {

    private final JSONMethods method;

    public EmptyJsonObject(JSONMethods method) {
        this.method = method;
    }

    @Override
    public JSONMethods getMethod() {
        return method;
    }
}
