package it.marcodemartino.yapga.common.io.emitters;

import it.marcodemartino.yapga.common.Startable;
import it.marcodemartino.yapga.common.json.JSONObject;

public interface OutputEmitter extends Startable {

    void sendOutput(JSONObject object);
    void sendRaw(byte[] bytes, int position, int length);

}
