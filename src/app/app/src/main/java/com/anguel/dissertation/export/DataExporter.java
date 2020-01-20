package com.anguel.dissertation.export;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import io.sentry.Sentry;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class DataExporter {
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient client;

    public DataExporter() {
        this.client = new OkHttpClient();
    }

    public void run() throws Exception {
        RequestBody requestBody = new RequestBody() {
            @Nullable
            @Override
            public MediaType contentType() {
                return MEDIA_TYPE_JSON;
            }

            @Override
            public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {
                bufferedSink.writeUtf8(getJSON());
            }

            private String getJSON() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("key", "U7DflOeV7GlIOBJUWWWlZC_TTbjnHX0O-y0aXaLxhsQ=");
                    jsonObject.put("type", "user");
                    jsonObject.put("uid", "3hunnedtestuid");
                    jsonObject.put("sias", "20");
                } catch (JSONException e) {
                    Sentry.capture(e);
                }

                return jsonObject.toString();
            }
        };

        Request request = new Request.Builder()
                .url("https://dissertation.anguel.co.uk/d")
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(Objects.requireNonNull(response.body()).string());
        }
    }

}
