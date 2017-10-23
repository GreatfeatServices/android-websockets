package com.socket.ceosilvajr.websocket;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hosopy.actioncable.ActionCable;
import com.hosopy.actioncable.ActionCableException;
import com.hosopy.actioncable.Channel;
import com.hosopy.actioncable.Consumer;
import com.hosopy.actioncable.Subscription;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

/**
 * Created by ceosilvajr on 23/10/2017.
 */

public class WebsocketIo {

    private static final String TAG = "Main2Activity";

    public static void main(String[] args) {
        try {
            connectSocket();
        } catch (URISyntaxException e) {
            Log.d(TAG, "URISyntaxException: " + e.getMessage());
        }
    }

    private static void connectSocket() throws URISyntaxException {
        // 1. Setup
        final String baseUrl = "ws://www.manwin5.com/cable";
        final URI uri = new URI(baseUrl);
        final HashMap<String, String> query = new HashMap<>();
        query.put("token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFkbWluQGdmLmNvbSIsImV4cGlyZV9hdCI6MTUwODU1NzIwNCwiaWQiOjN9.3tN8IROtVmcMkpuujpl54xqPH8fIDMn7MA2ZXn5zM7Q");
        final Consumer.Options options = new Consumer.Options();
        options.reconnection = true;
        options.query = query;
        Consumer consumer = ActionCable.createConsumer(uri, options);

        // 2. Create subscription
        Channel appearanceChannel = new Channel("ChatLinesChannel");
        appearanceChannel.addParam("chat_id", "1");
        Subscription subscription = consumer.getSubscriptions().create(appearanceChannel);
        subscription.onConnected(new Subscription.ConnectedCallback() {
            @Override
            public void call() {
                // Called when the subscription has been successfully completed
                System.out.println(String.format("%s %s", TAG, "Successfully connected to ChatLinesChannel"));
            }
        }).onRejected(new Subscription.RejectedCallback() {
            @Override
            public void call() {
                // Called when the subscription is rejected by the server
                System.out.println(String.format("%s %s", TAG, "ChatLinesChannel rejected to connect"));
            }
        }).onReceived(new Subscription.ReceivedCallback() {
            @Override
            public void call(JsonElement data) {
                // Called when the subscription receives data from the server
                System.out.println(String.format("%s %s", TAG, "Receives data:" + data.toString()));
            }
        }).onDisconnected(new Subscription.DisconnectedCallback() {
            @Override
            public void call() {
                // Called when the subscription has been closed
                System.out.println(String.format("%s %s", TAG, "Subscription has been disconnected."));
            }
        }).onFailed(new Subscription.FailedCallback() {
            @Override
            public void call(ActionCableException e) {
                // Called when the subscription encounters any error
                System.out.println(String.format("%s %s", TAG, "Error found: " + e.getMessage()));
            }
        });

        // 3. Establish connection
        consumer.connect();

        // 4. Perform any action
        subscription.perform("away");

        // 5. Perform any action using JsonObject(GSON)
        JsonObject params = new JsonObject();
        params.addProperty("message", "this is my message");
        params.addProperty("chat_id", "1");
        params.addProperty("receiver_id", "2");

        subscription.perform("send_message", params);
    }
}
