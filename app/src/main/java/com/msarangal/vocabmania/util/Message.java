package com.msarangal.vocabmania.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Mandeep on 22/6/2015.
 */
public class Message {

    public static void message(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void LongMessage(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }
}
