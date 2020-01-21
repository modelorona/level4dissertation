package com.anguel.dissertation.export;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.anguel.dissertation.BuildConfig;
import com.anguel.dissertation.R;
import com.anguel.dissertation.persistence.database.appcategory.AppCategory;
import com.anguel.dissertation.persistence.database.logevent.LogEvent;
import com.anguel.dissertation.persistence.database.userdata.UserData;
import com.anguel.dissertation.persistence.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import io.sentry.Sentry;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ExportService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && Objects.requireNonNull(intent.getAction()).equals(getString(R.string.upload_data_service))) {
            Thread thread = new Thread(this::startDataUpload);
            thread.start();
        } else stopSelf();
        return START_STICKY;
    }

    private void startDataUpload() {
        final OkHttpClient client = new OkHttpClient();

        Handler handler = new Handler(Looper.getMainLooper());
        if (canUpload(client)) {
            handler.post(() -> Toast.makeText(getApplicationContext(), getString(R.string.data_upload_started), Toast.LENGTH_LONG).show());
            run(client);
            handler.post(() -> Toast.makeText(getApplicationContext(), getString(R.string.data_upload_finished), Toast.LENGTH_LONG).show());
        } else {
            handler.post(() -> Toast.makeText(getApplicationContext(), getString(R.string.data_upload_unavailable), Toast.LENGTH_LONG).show());
        }

        stopSelf();
    }

    private boolean canUpload(OkHttpClient client) {
        Request request = new Request.Builder()
                .url(BuildConfig.db_url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            JSONObject object = new JSONObject(Objects.requireNonNull(response.body()).string());

            return String.valueOf(object.get(getString(R.string.data_can_upload_code))).equals("0");

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

        return false;
    }

    private void run(OkHttpClient client) {
        Request.Builder requestBuild = new Request.Builder()
                .url(BuildConfig.db_url);

//        first send categories
        List<String> appCategories = getAppCategoriesJSON();
        for (String app : appCategories) {
            RequestBody exportCategoryDataReqBody = RequestBody.create(app, MediaType.parse(getString(R.string.media_type)));

            Request exportCategoryDataRequest = requestBuild.post(exportCategoryDataReqBody).build();

            try (Response response = client.newCall(exportCategoryDataRequest).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                System.out.println(Objects.requireNonNull(response.body()).string());

            } catch (IOException e) {
                e.printStackTrace();
                Sentry.capture(e);
            }
        }

//        now send user info
        String userData = getUserDataJSON();
        RequestBody exportUserDataReqBody = RequestBody.create(userData, MediaType.parse(getString(R.string.media_type)));

        Request exportUserDataRequest = requestBuild.post(exportUserDataReqBody).build();

        try (Response response = client.newCall(exportUserDataRequest).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            System.out.println(Objects.requireNonNull(response.body()).string());

        } catch (IOException e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

//        finally send session data
        List<String> sessionData = getSessionDataJSON();
        for (String session : sessionData) {
            RequestBody exportSessionDataReqBody = RequestBody.create(session, MediaType.parse(getString(R.string.media_type)));

            Request exportSessionDataRequest = requestBuild.post(exportSessionDataReqBody).build();

            try (Response response = client.newCall(exportSessionDataRequest).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                System.out.println(Objects.requireNonNull(response.body()).string());
            } catch (IOException e) {
                e.printStackTrace();
                Sentry.capture(e);
            }
        }
    }

    private List<String> getSessionDataJSON() {
        Logger logger = new Logger();
        List<String> result = new LinkedList<>();

        try {
            List<LogEvent> logEvents = logger.getLogData(getApplicationContext());
            UserData userData = logger.getUserData(getApplicationContext()).get(0);
            for (LogEvent logEvent : logEvents) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(getString(R.string.data_upload_key), BuildConfig.app_key);
                jsonObject.put(getString(R.string.data_upload_type), getString(R.string.data_upload_type_session));
                jsonObject.put(getString(R.string.data_upload_uid), userData.getUserId());
                jsonObject.put(getString(R.string.data_upload_sd), logEvent.getData().toString());
                jsonObject.put(getString(R.string.data_upload_ss), String.valueOf(logEvent.getSessionStart()));
                jsonObject.put(getString(R.string.data_upload_se), String.valueOf(logEvent.getSessionEnd()));

                result.add(jsonObject.toString());
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

        return result;
    }

    private String getUserDataJSON() {
        Logger logger = new Logger();
        JSONObject jsonObject = new JSONObject();

        try {
            UserData userData = logger.getUserData(getApplicationContext()).get(0);
            jsonObject.put(getString(R.string.data_upload_key), BuildConfig.app_key);
            jsonObject.put(getString(R.string.data_upload_type), getString(R.string.data_upload_type_user));
            jsonObject.put(getString(R.string.data_upload_uid), userData.getUserId());
            jsonObject.put(getString(R.string.data_upload_sias), userData.getSias());
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

        return jsonObject.toString();
    }

    private List<String> getAppCategoriesJSON() {
        Logger logger = new Logger();
        List<String> result = new LinkedList<>();

        try {
            List<AppCategory> appCategories = logger.getAppCategories(getApplicationContext());
            for (AppCategory category : appCategories) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(getString(R.string.data_upload_key), BuildConfig.app_key);
                jsonObject.put(getString(R.string.data_upload_type), getString(R.string.data_upload_type_category));
                jsonObject.put(getString(R.string.data_upload_app_name), category.getAppName());
                jsonObject.put(getString(R.string.data_upload_category), category.getCategory());
                jsonObject.put(getString(R.string.data_upload_app_package), category.getPackageName());

                result.add(jsonObject.toString());
            }
        } catch (ExecutionException | InterruptedException | JSONException e) {
            e.printStackTrace();
            Sentry.capture(e);
        }
        return result;
    }

    @Override
    public void onDestroy() {
        stopMyService();
        super.onDestroy();
    }

    private void stopMyService() {
        stopForeground(true);
        stopSelf();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
