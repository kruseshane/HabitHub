package com.example.shane_kruse.habbithub;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

    public class MessageService extends WearableListenerService {

        @Override
        public void onMessageReceived(MessageEvent messageEvent) {

            //If the message’s path equals "/my_path"...//
            if (messageEvent.getPath().equals("/my_path")) {
                //...retrieve the message//
                final String message = new String(messageEvent.getData());
                System.out.println("Received " + message + " from phone");
                Intent messageIntent = new Intent();
                messageIntent.setAction(Intent.ACTION_SEND);
                messageIntent.putExtra("message", message);

                //Broadcast the received Data Layer messages locally//
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
            }
            else {
                super.onMessageReceived(messageEvent);
            }
        }
    }