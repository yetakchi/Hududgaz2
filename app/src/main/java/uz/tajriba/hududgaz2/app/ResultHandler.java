package uz.tajriba.hududgaz2.app;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ResultHandler {

    private boolean SYSTEM_SCANNED = false;
    private boolean DOCUMENT_SCANNED = false;

    private final OkHttpClient client;
    private JSONObject data;

    private OnResultListener resultListener;

    public ResultHandler() {
        this.client = new OkHttpClient();
        this.data = new JSONObject();
    }

    public void setOnClickListener(OnResultListener l) {
        this.resultListener = l;
    }

    public void onReset() {
        DOCUMENT_SCANNED = false;
        SYSTEM_SCANNED = false;

        data = new JSONObject();
    }

    /*  */
    public void onDocumentClick() {
        DOCUMENT_SCANNED = true;
    }

    public void onSystemClick() {
        SYSTEM_SCANNED = true;
    }

    public void onReceiveResult(String result) {
        try {
            String url = null;
            JSONObject json = new JSONObject(result);

            if (SYSTEM_SCANNED && json.has("url") && json.has("token")) {
                data.put("token", json.getString("token"));
                url = json.getString("url");
            }

            if (DOCUMENT_SCANNED && json.has("code")) {
                data.put("code", result);
                DOCUMENT_SCANNED = true;
            }

            if (data.has("token") && data.has("code")) {
                createDocument(url, data.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void createDocument(String url, String data) {
        resultListener.onStart();

        MediaType mediaType = MediaType.get("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(data, mediaType);

        Request request = new Request.Builder().url(url).post(body).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                assert response.body() != null;
                String str = response.body().string();
                resultListener.onSuccess(str);
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                resultListener.onFailure(e.getMessage());
            }
        });
    }

    public boolean getSystemState() {
        return data.has("token");
    }

    public boolean getDocumentState() {
        return data.has("code");
    }

    /**
     * OnClickListener
     */
    public interface OnResultListener {

        default void onStart() {

        }

        void onSuccess(String response);

        default void onFailure(String error) {

        }
    }
}
