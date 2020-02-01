package com.anguel.dissertation.export;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.anguel.dissertation.BuildConfig;
import com.anguel.dissertation.R;
import com.anguel.dissertation.persistence.entity.appcategory.AppCategory;
import com.anguel.dissertation.persistence.entity.calls.Call;
import com.anguel.dissertation.persistence.entity.location.Location;
import com.anguel.dissertation.persistence.entity.logevent.LogEvent;
import com.anguel.dissertation.persistence.entity.userdata.UserData;
import com.anguel.dissertation.persistence.DatabaseAPI;
import com.anguel.dissertation.utils.Utils;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import io.sentry.Sentry;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@SuppressWarnings("EmptyMethod")
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
        }
        return START_STICKY;
    }

    private void startDataUpload() {
        final OkHttpClient client = new OkHttpClient().newBuilder().build();
        final String url = BuildConfig.db_url;
        final Handler handler = new Handler(Looper.getMainLooper());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), getApplicationContext().getString(R.string.on_data_export_id))
                .setGroup(getString(R.string.data_export_group))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

        try {
            if (isOnline()) {
                if (canUpload(client, url)) {
                    handler.post(() -> Toast.makeText(getApplicationContext(), getString(R.string.data_upload_started), Toast.LENGTH_SHORT).show());
                    run(client, url);
//            show a small notification when the upload is done
                    handler.post(() -> {
                        builder.setSmallIcon(R.drawable.ic_cloud_done_black_24dp)
                                .setContentTitle(getString(R.string.data_upload_finished))
                                .setAutoCancel(true);
                        notificationManagerCompat.notify(4, builder.build());
                    });
                } else {
                    handler.post(() -> Toast.makeText(getApplicationContext(), getString(R.string.data_upload_unavailable), Toast.LENGTH_LONG).show());
                }
            } else {
                handler.post(() -> Toast.makeText(getApplicationContext(), getString(R.string.no_connection), Toast.LENGTH_LONG).show());
            }
        } catch (Exception e) {
            Sentry.capture(e);
            handler.post(() -> Toast.makeText(getApplicationContext(), getString(R.string.upload_error_occured), Toast.LENGTH_LONG).show());
        }

        stopSelf();
    }

    private boolean isOnline() {
        try {
            int timeoutMs = 1500;
            Socket sock = new Socket();
            SocketAddress socketAddress = new InetSocketAddress("8.8.8.8", 53);
            sock.connect(socketAddress, timeoutMs);
            sock.close();
            return true;
        } catch (Exception e) {
            Sentry.capture(e);
            e.printStackTrace();
            return false;
        }
    }

    private boolean canUpload(OkHttpClient client, String url) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful())
                throw new IOException(getString(R.string.unexpected_code) + response);

            JsonObject object = new Gson().fromJson(Objects.requireNonNull(response.body()).string(), JsonObject.class);

            return String.valueOf(object.get(getString(R.string.data_can_upload_code))).equals("0");

        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

        return false;
    }

    private void run(OkHttpClient client, String url) {
        DatabaseAPI databaseAPI = DatabaseAPI.getInstance();

        Request.Builder requestBuild = new Request.Builder()
                .url(url);

//        first send categories
        List<String> appCategories = getAppCategoriesJSON(databaseAPI);
        for (String app : appCategories) {
            RequestBody exportCategoryDataReqBody = RequestBody.create(app, MediaType.parse(getString(R.string.media_type)));

            Request exportCategoryDataRequest = requestBuild.post(exportCategoryDataReqBody).build();

            try (Response response = client.newCall(exportCategoryDataRequest).execute()) {
                if (!response.isSuccessful())
                    throw new IOException(getString(R.string.unexpected_code) + response);

                System.out.println(Objects.requireNonNull(response.body()).string());

            } catch (IOException e) {
                e.printStackTrace();
                Sentry.capture(e);
            }
        }

//        now send location data
        List<String> locationData = getLocationBatchJSON(databaseAPI);
        for (String location : locationData) {
            RequestBody exportLocationDataReqBody = RequestBody.create(location, MediaType.parse(getString(R.string.media_type)));

            Request exportLocationDataRequest = requestBuild.post(exportLocationDataReqBody).build();

            try (Response response = client.newCall(exportLocationDataRequest).execute()) {
                if (!response.isSuccessful())
                    throw new IOException(getString(R.string.unexpected_code) + response);

                System.out.println(Objects.requireNonNull(response.body()).string());
            } catch (IOException e) {
                e.printStackTrace();
                Sentry.capture(e);
            }
        }


//        now send user info
        String userData = getUserDataJSON(databaseAPI);
        RequestBody exportUserDataReqBody = RequestBody.create(userData, MediaType.parse(getString(R.string.media_type)));

        Request exportUserDataRequest = requestBuild.post(exportUserDataReqBody).build();

        try (Response response = client.newCall(exportUserDataRequest).execute()) {
            if (!response.isSuccessful())
                throw new IOException(getString(R.string.unexpected_code) + response);

            System.out.println(Objects.requireNonNull(response.body()).string());

        } catch (IOException e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

//        now send call data
        List<String> callData = getCallDataJSON(databaseAPI);
        for (String call : callData) {
            RequestBody exportCallDataReqBody = RequestBody.create(call, MediaType.parse(getString(R.string.media_type)));

            Request exportCallDataRequest = requestBuild.post(exportCallDataReqBody).build();

            try (Response response = client.newCall(exportCallDataRequest).execute()) {
                if (!response.isSuccessful())
                    throw new IOException(getString(R.string.unexpected_code) + response);

                System.out.println(Objects.requireNonNull(response.body()).string());
            } catch (IOException e) {
                e.printStackTrace();
                Sentry.capture(e);
            }
        }

//        finally send session data
        List<String> sessionData = getSessionDataJSON(databaseAPI);
        for (String session : sessionData) {
            RequestBody exportSessionDataReqBody = RequestBody.create(session, MediaType.parse(getString(R.string.media_type)));

            Request exportSessionDataRequest = requestBuild.post(exportSessionDataReqBody).build();

            try (Response response = client.newCall(exportSessionDataRequest).execute()) {
                if (!response.isSuccessful())
                    throw new IOException(getString(R.string.unexpected_code) + response);

                System.out.println(Objects.requireNonNull(response.body()).string());
            } catch (IOException e) {
                e.printStackTrace();
                Sentry.capture(e);
            }
        }
    }

    private JsonObject getBaseRequest(String type) {
        JsonObject request = new JsonObject();
        request.addProperty(getString(R.string.data_upload_key), BuildConfig.app_key);
        request.addProperty(getString(R.string.data_upload_type), type);
        request.addProperty(getString(R.string.data_upload_uid), Utils.getInstance().getUserID(getApplication()));
        return request;
    }

    private List<String> getLocationBatchJSON(DatabaseAPI databaseAPI) {
        List<String> result = new LinkedList<>(); // list of all the requests to send out

//        a request is of the form:
//        key, type, uid, data
//            data -> [jsonObject strings]
        try {
            List<Location> locations = databaseAPI.getLocationData(getApplicationContext());

            List<List<Location>> locationLists = Lists.partition(locations, Math.floorMod(locations.size(), 1000));

            for (List<Location> list : locationLists) {
                JsonObject request = getBaseRequest(getString(R.string.data_upload_type_location));
                JsonArray batch = new JsonArray();

                for (Location location : list) {
                    JsonObject loc = new JsonObject();
                    loc.addProperty(getString(R.string.data_upload_location_id), location.getId());
                    loc.addProperty(getString(R.string.data_upload_location_altitude), location.getAltitude());
                    loc.addProperty(getString(R.string.data_upload_location_haccuracy), location.getAltitude());
                    loc.addProperty(getString(R.string.data_upload_location_vaccuracy), location.getVAccuracy());
                    loc.addProperty(getString(R.string.data_upload_location_bearing), location.getBearing());
                    loc.addProperty(getString(R.string.data_upload_location_bearing_accuracy), location.getBearingAccuracy());
                    loc.addProperty(getString(R.string.data_upload_location_latitude), location.getLatitude());
                    loc.addProperty(getString(R.string.data_upload_location_longitude), location.getLongitude());
                    loc.addProperty(getString(R.string.data_upload_location_speed), location.getSpeed());
                    loc.addProperty(getString(R.string.data_upload_location_speed_accuracy), location.getSpeedAccuracy());
                    loc.addProperty(getString(R.string.data_upload_location_time_nanos), location.getTimeNanos());
                    loc.addProperty(getString(R.string.data_upload_location_provider), location.getProvider());
                    batch.add(loc);
                }
                request.add("data", batch);
                result.add(request.toString());
            }


        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

        return result;
    }

    private List<String> getCallDataJSON(DatabaseAPI databaseAPI) {
        List<String> result = new LinkedList<>();
        try {
            List<Call> calls = databaseAPI.getCallData(getApplicationContext());
            for (Call call : calls) {
                JsonObject jsonObject = getBaseRequest(getString(R.string.data_upload_type_call));
                jsonObject.addProperty(getString(R.string.data_upload_call_start), String.valueOf(call.getStartTime()));
                jsonObject.addProperty(getString(R.string.data_upload_call_end), String.valueOf(call.getEndTime()));
                result.add(jsonObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

        return result;
    }

    private List<String> getSessionDataJSON(DatabaseAPI databaseAPI) {
        List<String> result = new LinkedList<>();

        try {
            List<LogEvent> logEvents = databaseAPI.getLogData(getApplicationContext());
            for (LogEvent logEvent : logEvents) {
                JsonObject jsonObject = getBaseRequest(getString(R.string.data_upload_type_session));
                jsonObject.addProperty(getString(R.string.data_upload_sd), logEvent.getData().toString());
                jsonObject.addProperty(getString(R.string.data_upload_ss), String.valueOf(logEvent.getSessionStart()));
                jsonObject.addProperty(getString(R.string.data_upload_se), String.valueOf(logEvent.getSessionEnd()));

                result.add(jsonObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

        return result;
    }

    private String getUserDataJSON(DatabaseAPI databaseAPI) {
        JsonObject jsonObject = getBaseRequest(getString(R.string.data_upload_type_user));

        try {
            UserData userData = databaseAPI.getUserData(getApplicationContext()).get(0);
            jsonObject.addProperty(getString(R.string.data_upload_sias), userData.getSias());
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }

        return jsonObject.toString();
    }

    private List<String> getAppCategoriesJSON(DatabaseAPI databaseAPI) {
        List<String> result = new LinkedList<>();

        try {
            List<AppCategory> appCategories = databaseAPI.getAppCategories(getApplicationContext());
            for (AppCategory category : appCategories) {
                JsonObject jsonObject = getBaseRequest(getString(R.string.data_upload_type_category));
                jsonObject.addProperty(getString(R.string.data_upload_app_name), category.getAppName());
                jsonObject.addProperty(getString(R.string.data_upload_category), category.getCategory());
                jsonObject.addProperty(getString(R.string.data_upload_app_package), category.getPackageName());
                result.add(jsonObject.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.capture(e);
        }
        return result;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
