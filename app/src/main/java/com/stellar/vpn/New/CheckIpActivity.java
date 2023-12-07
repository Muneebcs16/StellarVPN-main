package com.stellar.vpn.New;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.gson.Gson;
import com.stellar.vpn.Activities.MainActivity;
import com.stellar.vpn.R;


public class CheckIpActivity extends AppCompatActivity {

    ImageView back_btn;
    TextView title;
    private NativeAd nativeAd;
    private Activity activity;


    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;

    TextView countIpAddress,txtCountry,txtCity,txtRegion,txtLatitude,txtLongitude,txtType,txtContinent,txtIsp;
    ImageView imgFlag;

    ProgressBar progressIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = CheckIpActivity.this;

        setContentView(R.layout.activity_check_ip);

        countIpAddress = findViewById(R.id.count_ip_address);
        progressIp  =findViewById(R.id.progress_ip);

        txtCountry  =findViewById(R.id.txt_country);
        txtCity  =findViewById(R.id.txt_country);
        txtRegion  =findViewById(R.id.txt_region);
        txtLatitude  =findViewById(R.id.txt_latitude);
        txtLongitude  =findViewById(R.id.txt_longitude);
        txtType  =findViewById(R.id.txt_type);
        txtContinent  =findViewById(R.id.txt_continent);
        txtIsp  =findViewById(R.id.txt_isp);
        imgFlag  =findViewById(R.id.img_flag);





        back_btn = findViewById(R.id.back_btn);
        title = findViewById(R.id.all_title);
        title.setText("Check Ip");
        back_btn.setOnClickListener(v -> onBackPressed());

        findViewById(R.id.back_refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkIp();
            }
        });

        findViewById(R.id.copy_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyText();
            }
        });

        checkIp();





      /*  os_version = findViewById(R.id.txt_os_version);
        phone_model = findViewById(R.id.txt_phone_model);
        brand = findViewById(R.id.txt_brand);
        cpu1 = findViewById(R.id.txt_cpu1);
        cpu2 = findViewById(R.id.txt_cpu2);
        cpu2_title = findViewById(R.id.cpu2_title);

        try {
            os_version.setText(String.valueOf(Build.VERSION.RELEASE));
            phone_model.setText(String.valueOf(Build.MODEL));
            brand.setText(String.valueOf(Build.BRAND));
            cpu1.setText(getAbi(0));
            cpu2.setText(getAbi(1));
        }catch (Exception e){
            e.printStackTrace();
        }*/


    }


    private void checkIp(){
        try {
            NetInfo netInfo = new NetInfo(this);
            String ipAddress = "";
            if(netInfo.getCurrentNetworkType() == 1){
                ipAddress = netInfo.getWifiIpAddress();
            }else {
                //ipAddress = netInfo.getDeviceIPAddress();
                ipAddress = "";
            }
            countIpAddress.setText(ipAddress);
        }catch (Exception e){
            e.printStackTrace();
        }

        progressIp.setVisibility(View.VISIBLE);
        NetInfo netInfo = new NetInfo(this);
        String url = "https://www.ip-api.com/json/";
        mRequestQueue = Volley.newRequestQueue(activity);
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("TAG", "onResponse: "+response);

                progressIp.setVisibility(View.GONE);
                GetIpLocation getIpLocation = new Gson().fromJson(response,GetIpLocation.class);
                txtCountry.setText(getIpLocation.getCountry());
                txtCity.setText(getIpLocation.getCity());
                txtRegion.setText(getIpLocation.getRegionName());
                txtLatitude.setText(getIpLocation.getLat());
                txtLongitude.setText(getIpLocation.getLon());
                txtType.setText("Ipv4");
                txtContinent.setText("N/A");
                countIpAddress.setText(getIpLocation.getQuery());
                txtIsp.setText(getIpLocation.getIsp());
                //Glide.with(CheckIpActivity.this,)
                imgFlag.setImageResource(getResources().getIdentifier("drawable/"+getIpLocation.getCountryCode().toLowerCase()+"aaaa",null,getPackageName()));
                findViewById(R.id.btn_map).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        double latitude = Double.parseDouble(getIpLocation.getLat());
                        double longitude = Double.parseDouble(getIpLocation.getLon());
                        String label = getIpLocation.getIsp();
                        String uriBegin = "geo:" + latitude + "," + longitude;
                        String query = latitude + "," + longitude + "(" + label + ")";
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(activity, ""+error.toString(), Toast.LENGTH_SHORT).show();
                Log.i("TAG","Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);

    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (getResources().getBoolean(R.bool.ads_switch)) {
//            if(!Config.all_subscription) {
                refreshAd();
           // }
        //}
    }

    private void copyText(){
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("CopyIp", countIpAddress.getText().toString());
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getApplicationContext(), "Ip Copied", Toast.LENGTH_SHORT).show();
    }


    /* public String getAbi(int i) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // on newer Android versions, we'll return only the most important Abi version
                return Build.SUPPORTED_ABIS[i];
            }
            else {
                // on pre-Lollip versions, we got only one Abi
                return Build.CPU_ABI;
            }
        }catch (Exception e){
            e.printStackTrace();
            cpu2.setVisibility(View.GONE);
            cpu2_title.setVisibility(View.GONE);
            return "";
        }

    }*/




    //loading native ad
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
            })
            .build();

            adLoader.loadAd(new AdRequest.Builder().build());
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
