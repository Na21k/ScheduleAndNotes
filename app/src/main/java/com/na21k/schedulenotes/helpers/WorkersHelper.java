package com.na21k.schedulenotes.helpers;

import android.content.Context;

import androidx.work.WorkManager;

import java.util.UUID;

public class WorkersHelper {

    public static void cancelRequest(String requestId, Context context) {
        if (requestId != null) {
            UUID uuid = UUID.fromString(requestId);
            WorkManager.getInstance(context).cancelWorkById(uuid);
        }
    }
}
