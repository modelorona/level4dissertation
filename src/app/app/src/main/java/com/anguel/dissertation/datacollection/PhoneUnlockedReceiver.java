package com.anguel.dissertation.datacollection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.jakewharton.threetenabp.AndroidThreeTen;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class PhoneUnlockedReceiver extends BroadcastReceiver {

    private long TIMEOUT_THRESHOLD = 1000L; // 1 second timeout between screen off and on
//    initial action is screen on
    private List<BroadcastEvent> broadcasts = Collections.synchronizedList(new LinkedList<>(
            Arrays.asList(new BroadcastEvent(Intent.ACTION_SCREEN_ON, org.threeten.bp.Instant.now().getEpochSecond()))));
//    private static Deque<>

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private class BroadcastEvent {
        private String action;
        private long timeOccuredAt;

        @NonNull
        @Override
        public String toString() {
            return action;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AndroidThreeTen.init(context);
        Log.d("yeeeet", intent.getAction());
        Log.d("yeeeet", "-----------");
        BroadcastEvent event = new BroadcastEvent();
        event.setAction(intent.getAction());
        event.setTimeOccuredAt(getOccuredAt());

        broadcasts.add(event);
        determineAction();
    }

//    current actions observed
//    on -> off (actually locked)
//    off -> screen on (not unlocked yet)

//    off -> screen on -> user present (unlocked)
//    off -> user present -> screen on (unlocked)
//    on -> off -> on (with some delay, this is if the screen turned off and the user quickly tapped it to turn it back on) *IGNORED FOR NOW*
    private void determineAction() {
        if (!broadcasts.isEmpty()) {
//            simple checks on size first based on the criteria above
            if (broadcasts.size() == 2) {
                BroadcastEvent first = broadcasts.get(0);
                BroadcastEvent second = broadcasts.get(1);

                if (first.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                    if (second.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//                    on -> off (actually locked)
//                    timestamp should be recorded here, as session is finished
//                        todo: implement timestamp recording
                        Log.d("YEET", "ON -> OFF");
                        broadcasts.clear();
                        broadcasts.add(new BroadcastEvent(Intent.ACTION_SCREEN_OFF, getOccuredAt()));
                    }
                } else if (first.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//                    do nothing
                    if (second.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                        //                    off -> screen on (not unlocked yet)
                        Log.d("YEET", "OFF -> SCREEN ON");
                    }
                }
            } else if (broadcasts.size() == 3) {
                BroadcastEvent first = broadcasts.get(0);
                BroadcastEvent second = broadcasts.get(1);
                BroadcastEvent third = broadcasts.get(2);

                if (first.getAction().equals(Intent.ACTION_SCREEN_OFF) && second.getAction().equals(Intent.ACTION_SCREEN_ON) && third.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    //    off -> screen on -> user present (unlocked)
                    Log.d("YEET", "OFF -> SCREEN ON -> USER PRESENT");
                    broadcasts.clear();
                    broadcasts.add(new BroadcastEvent(Intent.ACTION_SCREEN_ON, getOccuredAt()));
//                    todo: implement timestamp start
                } else if (first.getAction().equals(Intent.ACTION_SCREEN_OFF) && second.getAction().equals(Intent.ACTION_USER_PRESENT) && third.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//                    off -> user present -> screen on
                    Log.d("YEET", "OFF -> USER PRESENT -> SCREEN ON");
                    broadcasts.clear();
                    broadcasts.add(new BroadcastEvent(Intent.ACTION_SCREEN_ON, getOccuredAt()));
//                    todo: implement timestamp start
                } /*else if (first.getAction().equals(Intent.ACTION_SCREEN_ON) && second.getAction().equals(Intent.ACTION_SCREEN_OFF) && third.getAction().equals(Intent.ACTION_SCREEN_ON)) {
//                    on -> off -> on (with some delay, this is if the screen turned off and the user quickly tapped it to turn it back on)
                    Log.d("YEET", "SCREEN ON -> SCREEN OFF -> SCREEN ON");
                    if (third.getTimeOccuredAt() - second.getTimeOccuredAt() <= TIMEOUT_THRESHOLD) {
                        broadcasts.clear();
                        broadcasts.add(new BroadcastEvent(Intent.ACTION_SCREEN_ON, getOccuredAt()));
                    }
                    broadcasts.clear();
                    broadcasts.add(new BroadcastEvent(Intent.ACTION_SCREEN_OFF, getOccuredAt()));
                }*/
            } else if (broadcasts.size() > 3) {
                broadcasts.clear(); // for now
            }
            Log.d("Broadcasts", Arrays.toString(broadcasts.toArray()));
        } else {
            Log.d("BROADCAST_SIZE", "empty");
        }
    }

    private long getOccuredAt() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return Instant.now().toEpochMilli();
        } return org.threeten.bp.Instant.now().toEpochMilli();
    }
}
