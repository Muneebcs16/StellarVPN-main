package com.stellar.vpn.New;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.stellar.vpn.Activities.MainActivity;
import com.stellar.vpn.R;


public class NetworkProtectActivity extends AppCompatActivity {

    private static boolean CLICK_CHECK_PROTECTOR = true;
    ImageView back_btn;
    TextView title;
    private Activity activity;
    private boolean check;

    private NetInfo netInfowork;
    private NativeAd nativeAd;

    TextView wifiProtected,btnSecureWifi,messageUnprotected,wifiSsidName,txtLinkSpeed,txtIpAddress;
    ImageView wifiPN,wifiP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_network_protect);
        activity = NetworkProtectActivity.this;

        wifiProtected = findViewById(R.id.wifiProtected);
        btnSecureWifi = findViewById(R.id.btnSecureWifi);
        messageUnprotected = findViewById(R.id.messageUnprotected);
        wifiPN = findViewById(R.id.wifiPN);
        wifiP = findViewById(R.id.wifi_p);
        wifiSsidName = findViewById(R.id.wifi_ssid_name);
        txtLinkSpeed = findViewById(R.id.txt_link_speed);
        txtIpAddress = findViewById(R.id.txt_ip_address);

        back_btn = findViewById(R.id.back_btn);
        title = findViewById(R.id.all_title);
        title.setText("Wifi Protector");
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        check = true;

        if(check){
            wifiProtected.setText("PROTECTED");
            btnSecureWifi.setText("Discover More");
            wifiPN.setVisibility(View.VISIBLE);
            messageUnprotected.setText("Your WI-FI network and data are secured by Wild VPN from online threats.");
        }else {
            wifiP.setVisibility(View.VISIBLE);
            wifiProtected.setText("UNPROTECTED");
            wifiProtected.setTextColor(Color.parseColor("#FF0000"));
        }

        findViewById(R.id.btnSecureWifi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  startActivity(new Intent(activity, Server.class));
            }
        });

        netInfowork = new NetInfo(this);
        wifiSsidName.setText(netInfowork.getWifiSSID());
        getWifiLevel();
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        CLICK_CHECK_PROTECTOR = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        CLICK_CHECK_PROTECTOR = false;
    }

    private void getWifiLevel()
    {
        try {
            if(netInfowork.getCurrentNetworkType() == 1){
                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                int linkSpeed = wifiManager.getConnectionInfo().getRssi();
                int level = WifiManager.calculateSignalLevel(linkSpeed, 3);
                txtLinkSpeed.setText(level + " Mbps");
                txtIpAddress.setText(netInfowork.getWifiIpAddress());
            }else {
                txtLinkSpeed.setText("-1 Mbps");
                txtIpAddress.setText("0.0.0.0");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

                refreshAd();


    }

    private void populateUnifiedNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        try {
            // Set the media view. Media content will be automatically populated in the media view once
            MediaView mediaView = adView.findViewById(R.id.ad_media);
            adView.setMediaView(mediaView);

            // Set other ad assets.
            adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
            adView.setBodyView(adView.findViewById(R.id.ad_body));
            adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
            adView.setIconView(adView.findViewById(R.id.ad_app_icon));
            adView.setPriceView(adView.findViewById(R.id.ad_price));
            adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
            adView.setStoreView(adView.findViewById(R.id.ad_store));
            adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

            // The headline is guaranteed to be in every UnifiedNativeAd.
            ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.getBody() == null) {
                adView.getBodyView().setVisibility(View.INVISIBLE);
            } else {
                adView.getBodyView().setVisibility(View.VISIBLE);
                ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
            }

            if (nativeAd.getCallToAction() == null) {
                adView.getCallToActionView().setVisibility(View.INVISIBLE);
            } else {
                adView.getCallToActionView().setVisibility(View.VISIBLE);
                ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
            }

            if (nativeAd.getIcon() == null) {
                adView.getIconView().setVisibility(View.GONE);
            } else {
                ((ImageView) adView.getIconView()).setImageDrawable(
                        nativeAd.getIcon().getDrawable());
                adView.getIconView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getPrice() == null) {
                adView.getPriceView().setVisibility(View.INVISIBLE);
            } else {
                adView.getPriceView().setVisibility(View.VISIBLE);
                ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
            }

            if (nativeAd.getStore() == null) {
                adView.getStoreView().setVisibility(View.INVISIBLE);
            } else {
                adView.getStoreView().setVisibility(View.VISIBLE);
                ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
            }

            if (nativeAd.getStarRating() == null) {
                adView.getStarRatingView().setVisibility(View.INVISIBLE);
            } else {
                ((RatingBar) adView.getStarRatingView())
                        .setRating(nativeAd.getStarRating().floatValue());
                adView.getStarRatingView().setVisibility(View.VISIBLE);
            }

            if (nativeAd.getAdvertiser() == null) {
                adView.getAdvertiserView().setVisibility(View.INVISIBLE);
            } else {
                ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                adView.getAdvertiserView().setVisibility(View.VISIBLE);
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad. The SDK will populate the adView's MediaView
            // with the media content from this native ad.
            adView.setNativeAd(nativeAd);

            // Get the video controller for the ad. One will always be provided, even if the ad doesn't
            // have a video asset.
            VideoController vc = nativeAd.getMediaContent().getVideoController();

            // Updates the UI to say whether or not this ad has a video asset.
            if (vc.hasVideoContent()) {

                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.

                        super.onVideoEnd();
                    }
                });
            } else {
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    private void refreshAd() {

        try {
            AdLoader.Builder builder = new AdLoader.Builder(this, MainActivity.admob_native_id);

            builder.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                @Override
                public void onNativeAdLoaded(NativeAd unifiedNativeAd) {
                    // You must call destroy on old ads when you are done with them,
                    // otherwise you will have a memory leak.
                    if (nativeAd != null) {
                        nativeAd.destroy();
                    }
                    nativeAd = unifiedNativeAd;
                    RelativeLayout frameLayout =
                            findViewById(R.id.fl_adplaceholder);
                    frameLayout.setBackgroundResource(R.drawable.border);
                    NativeAdView adView = (NativeAdView) getLayoutInflater()
                            .inflate(R.layout.ad_unified, null);
                    populateUnifiedNativeAdView(unifiedNativeAd, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }

            });

            VideoOptions videoOptions = new VideoOptions.Builder()
                    .build();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(videoOptions)
                    .build();

            builder.withNativeAdOptions(adOptions);

            AdLoader adLoader = builder.withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    super.onAdFailedToLoad(loadAdError);
                }


            }).build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
