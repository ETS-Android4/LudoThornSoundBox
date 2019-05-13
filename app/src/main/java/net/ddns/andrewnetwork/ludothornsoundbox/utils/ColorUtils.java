package net.ddns.andrewnetwork.ludothornsoundbox.utils;

import android.content.Context;
import android.util.Pair;


import androidx.annotation.ColorRes;

import net.ddns.andrewnetwork.ludothornsoundbox.R;

import java.util.ArrayList;
import java.util.List;

public final class ColorUtils {

    public static final List<Pair<String, String>> colorList = new ArrayList<>();
    public static final String DEFAULT_COLOR = "ORANGE";
    static {
        colorList.add(new Pair<>(DEFAULT_COLOR, "background"));
        colorList.add(new Pair<>("YELLOW", "yellow_background"));
        colorList.add(new Pair<>("BLUE", "blue_light"));
        colorList.add(new Pair<>("RED", "red"));
    }

    public static @ColorRes int getByName(Context context, String name) {
        for(Pair<String, String> colorPair : colorList) {
            if(colorPair.first.toLowerCase().equals(name.toLowerCase())) {
                return context.getResources().getIdentifier(colorPair.second, "color", context.getPackageName());
            }
        }

        return -1;
    }

    public static String getByColorResource(Context context, @ColorRes int colorRes) {
        for(Pair<String, String> colorPair : colorList) {
            if(context.getResources().getIdentifier(colorPair.second, "color", context.getPackageName()) == colorRes) {
                return colorPair.first;
            }
        }

        return "NULL";
    }
}
