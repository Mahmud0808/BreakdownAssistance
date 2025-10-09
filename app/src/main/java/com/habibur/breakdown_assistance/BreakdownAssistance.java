package com.habibur.breakdown_assistance;

import android.app.Application;
import android.content.Context;

import com.google.firebase.firestore.FirebaseFirestore;
import com.habibur.breakdown_assistance.utils.LocaleHelper;

import java.lang.ref.WeakReference;

public class BreakdownAssistance extends Application {

    private static BreakdownAssistance instance;
    private static WeakReference<Context> contextReference;
    private static WeakReference<FirebaseFirestore> firestoreReference;

    public void onCreate() {
        super.onCreate();
        instance = this;
        contextReference = new WeakReference<>(getApplicationContext());
        FirebaseFirestore.setLoggingEnabled(BuildConfig.DEBUG);
    }

    public static Context getAppContext() {
        if (contextReference == null || contextReference.get() == null) {
            contextReference = new WeakReference<>(BreakdownAssistance.getInstance().getApplicationContext());
        }
        return LocaleHelper.setLocale(contextReference.get());
    }

    public static FirebaseFirestore getFirestore() {
        if (firestoreReference == null || firestoreReference.get() == null) {
            firestoreReference = new WeakReference<>(FirebaseFirestore.getInstance());
        }
        return firestoreReference.get();
    }

    private static BreakdownAssistance getInstance() {
        if (instance == null) {
            instance = new BreakdownAssistance();
        }
        return instance;
    }
}