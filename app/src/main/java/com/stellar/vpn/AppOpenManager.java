package com.stellar.vpn;

import static androidx.lifecycle.Lifecycle.Event.ON_START;

import android.app.Activity;
import android.app.Application;

import androidx.lifecycle.OnLifecycleEvent;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.stellar.vpn.Activities.MainActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;

public class AppOpenManager implements Application.ActivityLifecycleCallbacks, androidx.lifecycle.LifecycleObserver {
    private static final String LOG_TAG = "AppOpenManager";
    private static String AD_UNIT_ID = "ca-app-pub-3940256099942544/3419835294";
    private AppOpenAd appOpenAd = null;

    private AppOpenAd.AppOpenAdLoadCallback loadCallback;
    private Activity currentActivity;
    private static boolean isShowingAd = false;

    private final Application myApplication;

    /**
     * Constructor
     */
    public AppOpenManager(Application myApplication) {
        //AD_UNIT_ID = MainActivity.will_dev_33223327_admob_app_open;
        this.myApplication = myApplication;
        this.myApplication.registerActivityLifecycleCallbacks(this);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);

    }

    /**
     * Request an ad
     */
    public void fetchAd() {

        loadCallback =
                new AppOpenAd.AppOpenAdLoadCallback() {

                    /**
                     * Called when an app open ad has loaded.
                     *
                     * @param ad the loaded app open ad.
                     */
                    @Override
                    public void onAdLoaded(AppOpenAd ad) {
                        appOpenAd = ad;
                        //Toast.makeText(currentActivity, "Ad Loaded", Toast.LENGTH_SHORT).show();
                    }

                    /**
                     * Called when an app open ad has failed to load.
                     *
                     * @param loadAdError the error.
                     */
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        // Handle the error.
                        android.util.Log.d(LOG_TAG, "error in loading");
                        //Toast.makeText(currentActivity, "" + loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                };
        AdRequest request = getAdRequest();
        AppOpenAd.load(
                myApplication, AD_UNIT_ID, request,
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT, loadCallback);
    }

    /**
     * Shows the ad if one isn't already showing.
     */
    public void showAdIfAvailable() {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (!isShowingAd && isAdAvailable()) {
            //Toast.makeText(currentActivity, "will show ad", Toast.LENGTH_SHORT).show();
            android.util.Log.d(LOG_TAG, "Will show ad.");

            FullScreenContentCallback fullScreenContentCallback =
                    new FullScreenContentCallback() {
                        @Override
                        public void onAdDismissedFullScreenContent() {
                            // Set the reference to null so isAdAvailable() returns false.
                            appOpenAd = null;
                            isShowingAd = false;
                            fetchAd();
                        }

                        @Override
                        public void onAdFailedToShowFullScreenContent(AdError adError) {
                        }

                        @Override
                        public void onAdShowedFullScreenContent() {
                            isShowingAd = true;
                        }
                    };

            appOpenAd.setFullScreenContentCallback(fullScreenContentCallback);
            appOpenAd.show(currentActivity);

        } else {
            android.util.Log.d(LOG_TAG, "Can not show ad.");
            fetchAd();
        }
    }

    /**
     * Creates and returns ad request.
     */
    private AdRequest getAdRequest() {
        return new AdRequest.Builder().build();
    }

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    public boolean isAdAvailable() {
        return appOpenAd != null;
    }




    @Override
    public void onActivityCreated(@androidx.annotation.NonNull Activity activity, @androidx.annotation.Nullable android.os.Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@androidx.annotation.NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityResumed(@androidx.annotation.NonNull Activity activity) {
        currentActivity = activity;

    }

    @Override
    public void onActivityPaused(@androidx.annotation.NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@androidx.annotation.NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@androidx.annotation.NonNull Activity activity, @androidx.annotation.NonNull android.os.Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@androidx.annotation.NonNull Activity activity) {
        currentActivity = null;

    }
    /** LifecycleObserver methods */
    @OnLifecycleEvent(ON_START)
    public void onStart() {
        showAdIfAvailable();
        android.util.Log.d(LOG_TAG, "onStart");
    }
}