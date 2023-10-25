package it.marcodemartino.yapga.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GsonInstance {

    private static Gson gson;

    public static Gson get() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        }
        return gson;
    }
}
