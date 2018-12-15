package com.miniplat.app;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

public class Container {
    private static List<Activity> container = new ArrayList();

    public static Activity getTopActivity() {
        return container.get(container.size() - 1);
    }

    public static void addActivity(Activity a) {
        container.add(a);
    }

    public static void removeActivity(Activity a) {
        container.remove(a);
    }

    public static void finish() {
        getTopActivity().finish();
    }

    public static void finishWithResult(int result) {
        Activity activity = getTopActivity();
        activity.setResult(result);
        activity.finish();
    }

    public static void finishAll(){
        for (Activity a : container){
            if (!a.isFinishing()){
                a.finish();
            }
        }
    }
}
