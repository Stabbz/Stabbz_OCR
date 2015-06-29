package com.tag.ddamianow;

import android.util.Log;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * Created by Damyan Damyanov on 24.6.2015 г..
 */
public class FindURL {

    private static final String TAG = "FindURL";

    public static String[] extractLinks(String text) {
        List<String> links = new ArrayList<String>();
        Matcher m = Patterns.WEB_URL.matcher(text);
        while (m.find()) {
            String url = m.group();
            Log.d(TAG, "URL extracted: " + url);
            links.add(url);
        }

        return links.toArray(new String[links.size()]);
    }
}
