package it.marcodemartino.yapga.client.logic.results;

import java.util.*;

public class ResultBroadcaster {

    private final Map<Result, List<Runnable>> toBeCalled;

    public ResultBroadcaster() {
        toBeCalled = new HashMap<>();
        for (Result result : Result.values()) {
            toBeCalled.put(result, new ArrayList<>());
        }
    }

    public void registerListener(Result result, Runnable runnable) {
        toBeCalled.get(result).add(runnable);
    }

    public void notify(Result result) {
        for (Runnable runnable : toBeCalled.get(result)) {
            runnable.run();
        }
    }
}
