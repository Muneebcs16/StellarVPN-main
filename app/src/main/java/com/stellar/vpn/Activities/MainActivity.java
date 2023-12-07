package com.stellar.vpn.Activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.VpnService;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.bumptech.glide.Glide;
import com.codemybrainsout.ratingdialog.RatingDialog;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.stellar.vpn.Config;
import com.stellar.vpn.R;
import com.stellar.vpn.Utils.ActiveServer;
import com.stellar.vpn.Utils.Constants;
import com.stellar.vpn.Utils.Countries;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import top.oneconnectapi.app.OpenVpnApi;
import top.oneconnectapi.app.core.OpenVPNThread;

public class MainActivity extends ContentsActivity  {

    //one connect
    private OpenVPNThread vpnThread = new OpenVPNThread();
    private boolean isFirst = true;

    public static Countries selectedCountry = null;

    private static final String CHANNEL_ID = "vpn";
    public static String will_dev_33223327_facebook_reward_id;
    public static String will_dev_33223327_official_dont_change_value;
    private Locale locale;

    public static String type = "ad";
    public static String will_dev_33223327_admob_id = "";

    public static String admob_banner_id = "ca-app-pub-3940256099942544/6300978111";
    public static String admob_interstitial_id = "ca-app-pub-3940256099942544/1033173712";
    public static String admob_native_id = "ca-app-pub-3940256099942544/2247696110";

    public static String will_dev_33223327_admob_reward = "ca-app-pub-3940256099942544/5224354917";
    public static String will_dev_33223327_admob_app_open = "ca-app-pub-3940256099942544/3419835294";

    public static String will_dev_33223327_fb_banner_id = "";
    public static String will_dev_33223327_fb_interstitial_id = "";
    public static boolean will_dev_33223327_all_ads_on_off = true;
    public static boolean will_dev_33223327_remove_premium = true;
    public static boolean will_dev_33223327_remove_all_video_ads_button = true;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter("connectionState"));

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
                Log.e("REWARDED INITIALIZ", initializationStatus.getAdapterStatusMap().toString());
            }
        });

        if (TextUtils.isEmpty(type)) {
            type = "";
        }

        final RatingDialog ratingDialog = new RatingDialog.Builder(this)
                .session(7)
                .threshold(4)
                .onThresholdFailed(new RatingDialog.Builder.RatingThresholdFailedListener() {
                    @Override
                    public void onThresholdFailed(RatingDialog ratingDialog, float rating, boolean thresholdCleared) {
                        Toast.makeText(MainActivity.this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show();
                        ratingDialog.dismiss();
                    }
                })
                .negativeButtonText("Never")
                .negativeButtonTextColor(R.color.grey_500)
                .playstoreUrl("https://play.google.com/store/apps/details?id=" + this.getPackageName())
                .onRatingBarFormSumbit(feedback -> {}).build();

        ratingDialog.show();

        Intent intent = getIntent();

        if(getIntent().getExtras() != null) {
            selectedCountry = getIntent().getExtras().getParcelable("c");
            if (!Utility.isOnline(getApplicationContext())) {
                showMessage("No Internet Connection");
            } else {
                prepareVpn();
             //   showInterstitialAndConnect();
           //    updateUI("LOAD");

            }
        }
        else {
            if(selectedCountry != null) {
                updateUI("CONNECTED");

                Glide.with(this)
                        .load(selectedCountry.getFlagUrl())
                        .into(imgFlag);
                flagName.setText(selectedCountry.getCountry());
            }
            else {
                ArrayList<Countries> servers = new ArrayList<>();

                try {
                    JSONArray jsonArray = new JSONArray(Constants.FREE_SERVERS);
                    for (int i=0; i < 1;i++){
                        JSONObject object = (JSONObject) jsonArray.get(i);
                        servers.add(new Countries(object.getString("serverName"),
                                object.getString("flag_url"),
                                object.getString("ovpnConfiguration"),
                                object.getString("vpnUserName"),
                                object.getString("vpnPassword")
                        ));
                        selectedCountry = servers.get(0);
                        Glide.with(this)
                                .load(selectedCountry.getFlagUrl())
                                .into(imgFlag);
                        flagName.setText(selectedCountry.getCountry());
                        Toast.makeText(this, ""+selectedCountry.getOvpn(), Toast.LENGTH_SHORT).show();
                        updateUI("DISCONNECTED");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }

        if (intent.getStringExtra("type") != null) {
            type = intent.getStringExtra("type");
            admob_banner_id = intent.getStringExtra("admob_banner");
            admob_interstitial_id = intent.getStringExtra("admob_interstitial");
            will_dev_33223327_fb_banner_id = intent.getStringExtra("will_dev_33223327_fb_banner");
            will_dev_33223327_fb_interstitial_id = intent.getStringExtra("will_dev_33223327_fb_interstitial");
        }

        if(TextUtils.isEmpty(type)) {
            type = "";
            Log.v("AD_TYPE", " null");
        }

        if (type.equals("ad")) {
            RequestConfiguration.Builder requestBuilder = new RequestConfiguration.Builder();
            MobileAds.setRequestConfiguration(requestBuilder.build());
        } else {
            AdSettings.setIntegrationErrorMode(AdSettings.IntegrationErrorMode.INTEGRATION_ERROR_CALLBACK_MODE);

            //Initialize facebook ads
            AudienceNetworkAds.initialize(this);
        }


    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        super.onCreate(savedInstanceState, persistentState);
        inAppUpdate();
    }

    protected void startVpn() {
        try {
            ActiveServer.saveServer(MainActivity.selectedCountry, MainActivity.this);
            OpenVpnApi.startVpn(this, selectedCountry.getOvpn(), selectedCountry.getCountry(), selectedCountry.getOvpnUserName(), selectedCountry.getOvpnUserPassword());
          //  showNotif();
           // startUIUpdateTask();

        } catch (RemoteException e) {
            e.printStackTrace();
            updateUI("WAIT");
          //  handleError(e);
        }
    }



    @Override
    protected void disconnectFromVpn() {
        try {
            vpnThread.stop();
           // stopUIUpdateTask();
            updateUI("DISCONNECTED");
        } catch (Exception e) {
            e.printStackTrace();
            updateUI("LOAD");
          //  handleError(e);
        }
    }

    @Override
    protected void checkRemainingTraffic() {

    }


    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main_will_dev;
    }

    private void inAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        && result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    try {
                        appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, MainActivity.this, 11);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11) {
            Toast.makeText(this, "Start Downloand", Toast.LENGTH_SHORT).show();
            if (resultCode != RESULT_OK) {
                Log.d("Update", "Update failed" + resultCode);
            }
            if (resultCode == RESULT_OK) {
                startVpn();
            }
        }
    }
    public void prepareVpn() {

        Glide.with(this)
                .load(selectedCountry.getFlagUrl())
                .into(imgFlag);
        flagName.setText(selectedCountry.getCountry());

        if (Utility.isOnline(getApplicationContext())) {

            if(selectedCountry != null) {
                Intent intent = VpnService.prepare(this);
                Log.v("CHECKSTATE", "start");

                if (intent != null) {
                    startActivityForResult(intent, 1);
                } else
                    startVpn(); //have already permission
            } else {
               showMessage("Please select a server first");
            }

        } else {
            showMessage("No Internet Connection");
        }
    }

//    public void showNotif() {
//
//        createNotificationChannel();
//
//        String country = "";
//
//        if(!selectedCountry.getCountry().equalsIgnoreCase("")) {
//            locale = new Locale("", selectedCountry.getCountry());
//            country =  " - " + locale.getDisplayCountry();
//        }
//
//        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
//                .title(getResources().getString(R.string.app_name2) + country)
//                .channelId(CHANNEL_ID)
//                .build();
//
//        UnifiedSDK.update(notificationConfig);
//    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "VPN";
            String description = "VPN notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                updateUI(intent.getStringExtra("state"));
                Log.v("CHECKSTATE", intent.getStringExtra("state"));

                if (isFirst) {
                    if (ActiveServer.getSavedServer(MainActivity.this).getCountry() != null) {
                        selectedCountry = ActiveServer.getSavedServer(MainActivity.this);

                        Glide.with(MainActivity.this)
                                .load(selectedCountry.getFlagUrl())
                                .into(imgFlag);
                        flagName.setText(selectedCountry.getCountry());
                    }

                    isFirst = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

                String duration = intent.getStringExtra("duration");
                String lastPacketReceive = intent.getStringExtra("lastPacketReceive");
                String byteIn = intent.getStringExtra("byteIn");
                String byteOut = intent.getStringExtra("byteOut");

                if (duration == null) duration = "00:00:00";
                if (lastPacketReceive == null) lastPacketReceive = "0";
                if (byteIn == null) byteIn = " ";
                if (byteOut == null) byteOut = " ";

                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    public void checkSelectedCountry() {
        if (selectedCountry == null) {
            updateUI("DISCONNECTED");
            showMessage("Please select a server first");
        } else {
            prepareVpn();
         //   showInterstitialAndConnect();
            updateUI("LOAD");
        }
    }
}
