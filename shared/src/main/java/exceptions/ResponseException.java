package exceptions;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    final private int code;

    public ResponseException(int code, String message) {
        super(message);
        this.code = code;
    }
    public ResponseException(int code, String message, Throwable ex) {
        super(message, ex);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        Object statusObj = map.get("status");
        // not sure why it returns a Double but it does
        int status = 520; // code for unknown
        if (statusObj instanceof Double) {
            status = ((Double) statusObj).intValue();
        }
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public int code() {
        return code;
    }
}