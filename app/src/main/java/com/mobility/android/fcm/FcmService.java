package com.mobility.android.fcm;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mobility.android.fcm.command.FcmCommand;
import com.mobility.android.fcm.command.TestCommand;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import timber.log.Timber;

public class FcmService extends FirebaseMessagingService {

    private static final Map<String, FcmCommand> MESSAGE_RECEIVERS;

    static {
        Map<String, FcmCommand> receivers = new HashMap<>();
        receivers.put("test", new TestCommand());

        MESSAGE_RECEIVERS = Collections.unmodifiableMap(receivers);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Timber.e("onMessageReceived()");

        String receiver = message.getData().get("receiver");

        FcmCommand command = MESSAGE_RECEIVERS.get(receiver);
        if (command == null) {
            Timber.e("Unknown command received: %s", receiver);
        } else {
            command.execute(this, message.getData());
        }
    }
}