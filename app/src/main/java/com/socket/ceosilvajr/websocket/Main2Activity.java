package com.socket.ceosilvajr.websocket;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hosopy.actioncable.ActionCable;
import com.hosopy.actioncable.ActionCableException;
import com.hosopy.actioncable.Channel;
import com.hosopy.actioncable.Consumer;
import com.hosopy.actioncable.Subscription;

import java.net.URI;
import java.net.URISyntaxException;

public class Main2Activity extends AppCompatActivity {

    private static final String TAG = "Main2Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        try {
            connectSocket();
        } catch (URISyntaxException e) {
            Log.d(TAG, "URISyntaxException: " + e.getMessage());
        }
    }

    private void connectSocket() throws URISyntaxException {
        // 1. Setup
        URI uri = new URI("ws://www.manwin5.com/cable?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFkbWluQGdmLmNvbSIsImV4cGlyZV9hdCI6MTUwODU1NzIwNCwiaWQiOjN9.3tN8IROtVmcMkpuujpl54xqPH8fIDMn7MA2ZXn5zM7Q");
        Consumer consumer = ActionCable.createConsumer(uri);

        // 2. Create subscription
        Channel appearanceChannel = new Channel("ChatLinesChannel");
        appearanceChannel.addParam("chat_id", "1");

        Subscription subscription = consumer.getSubscriptions().create(appearanceChannel);
        subscription.onConnected(new Subscription.ConnectedCallback() {
            @Override
            public void call() {
                // Called when the subscription has been successfully completed
                Log.d(TAG, "Successfully connected to ChatLinesChannel");
            }
        }).onRejected(new Subscription.RejectedCallback() {
            @Override
            public void call() {
                // Called when the subscription is rejected by the server
                Log.d(TAG, "ChatLinesChannel rejected to connect");
            }
        }).onReceived(new Subscription.ReceivedCallback() {
            @Override
            public void call(JsonElement data) {
                // Called when the subscription receives data from the server
                Log.d(TAG, "Receives data:" + data.toString());
            }
        }).onDisconnected(new Subscription.DisconnectedCallback() {
            @Override
            public void call() {
                // Called when the subscription has been closed
                Log.d(TAG, "Subscription has been disconnected.");
            }
        }).onFailed(new Subscription.FailedCallback() {
            @Override
            public void call(ActionCableException e) {
                // Called when the subscription encounters any error
                Log.d(TAG, "Error found: " + e.getMessage());
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
