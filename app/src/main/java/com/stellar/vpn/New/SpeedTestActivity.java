package com.stellar.vpn.New;

import android.app.Activity;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.example.internet_speed_testing.InternetSpeedBuilder;
import com.example.internet_speed_testing.ProgressionModel;
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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;


public class SpeedTestActivity extends AppCompatActivity {

    private static final String TAG = "SpeedTestActivity";
    ImageView back_btn;
    TextView title;
    private NativeAd nativeAd;
    private Activity activity;


    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;
    private String mUnits;
    private String mDownloadSpeedOutput;
    private String click_activity;
    private Animation anim_s;
    private RotateAnimation animation;
    private float angle;
    private boolean check = false;
    private int i = 0;
    long startTime;
    private boolean isTestFinish = false;
    private InternetSpeedBuilder builder;
    TextView btnTest,downloadTime,timingD,downloadTimeFirst,uploadTime,timingU,pingTime;
    RelativeLayout greenSign;
    LinearLayout speedTestLayout;
    ImageView doneSign,speed_meter_sign;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       setContentView(R.layout.activity_speed_test);
        activity = SpeedTestActivity.this;
        back_btn = findViewById(R.id.back_btn);
        title = findViewById(R.id.all_title);
        title.setText("Speed Test");
        back_btn.setOnClickListener(v -> onBackPressed());

        builder = new InternetSpeedBuilder(this);

        btnTest= findViewById(R.id.btnTest);
        greenSign= findViewById(R.id.greenSign);
        speedTestLayout= findViewById(R.id.speedTestLayout);
        doneSign= findViewById(R.id.doneSign);
        downloadTime= findViewById(R.id.downloadTime);
        timingD= findViewById(R.id.timingD);
        downloadTimeFirst= findViewById(R.id.downloadTimeFirst);
        uploadTime= findViewById(R.id.uploadTime);
        timingU= findViewById(R.id.timingU);
        pingTime= findViewById(R.id.pingTime);
        speed_meter_sign= findViewById(R.id.speed_meter_sign);


        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTestFinish = false;
                btnTest.setVisibility(View.GONE);
                getDownloadSpeed();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    if (activity == null) {
                        activity = SpeedTestActivity.this;
                    }
                    anim_s = AnimationUtils.loadAnimation(activity, R.anim.scal);
                    greenSign.setVisibility(View.VISIBLE);
                    greenSign.startAnimation(anim_s);
                    anim_s.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            greenSign.setVisibility(View.VISIBLE);

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                           greenSign.setVisibility(View.GONE);
                            speedTestLayout.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    if (anim_s != null)
                        anim_s.cancel();
                   greenSign.setVisibility(View.GONE);
                    speedTestLayout.setVisibility(View.VISIBLE);
                }
            }
        }, 200);





        new Handler().postDelayed(() -> {

            try {
                doneSign.setVisibility(View.VISIBLE);
                Drawable drawable = doneSign.getDrawable();
                if (drawable instanceof AnimatedVectorDrawableCompat) {
                    avd = (AnimatedVectorDrawableCompat) drawable;
                    if (avd.isRunning()) {
                        avd.stop();
                    }
                    avd.start();

                } else if (drawable instanceof AnimatedVectorDrawable) {
                    avd2 = (AnimatedVectorDrawable) drawable;
                    if (avd2.isRunning()) {
                        avd2.stop();
                    }
                    avd2.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }, 1000);


    }

    @Override
    protected void onStart() {
        super.onStart();
        try {

                    refreshAd();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


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
        } catch (Exception e) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        builder = null;
        super.onDestroy();
    }

    private void getDownloadSpeed() {
        try {
            startTime = System.currentTimeMillis();
            if (builder != null) {
                builder.setOnEventInternetSpeedListener(new InternetSpeedBuilder.OnEventInternetSpeedListener() {
                    @Override
                    public void onDownloadProgress(int count, ProgressionModel progressModel) {
                        Log.d(TAG, "onDownloadProgress: " + progressModel);
                        Log.d("ProgressCounter", "onDownloadProgress: " + count);
                        if (progressModel.getProgressDownload() > 99 && progressModel.getProgressUpload() > 99) {
                            testCompleted();
                        }
                    }

                    @Override
                    public void onUploadProgress(int count, ProgressionModel progressModel) {
                        Log.d(TAG, "onUploadProgress: " + progressModel);
                        Log.d("ProgressCounter", "onUploadProgress: " + count);
                        if (progressModel.getProgressDownload() > 99 && progressModel.getProgressUpload() > 99) {
                            testCompleted();
                        }
                    }

                    @Override
                    public void onTotalProgress(int count, ProgressionModel progressModel) {

                        if (count == 0) {
                            Log.d("SpeedTest", "onTotalProgress: 0");

                            java.math.BigDecimal bd = progressModel.getDownloadSpeed();
                            final double d = bd.doubleValue();
                           downloadTime.setText("" + formatFileSizeS(d));
                           timingD.setText("" + formatFileSize(d));
                           downloadTimeFirst.setText("" + formatFileSizeS(d));

                            java.math.BigDecimal bd2 = progressModel.getUploadSpeed();
                            final double d2 = bd2.doubleValue();
                           uploadTime.setText("" + formatFileSizeS(d2));
                           timingU.setText("" + formatFileSize(d2));

                            formatFileSizeS(d);
                            animation = new RotateAnimation(0, angle,
                                    Animation.RELATIVE_TO_SELF, 0.5f,
                                    Animation.RELATIVE_TO_SELF, 0.5f);
                            animation.setInterpolator(new AccelerateDecelerateInterpolator());
                            animation.setDuration(1000);
                            animation.setFillAfter(true);

                            if (!check) {
                                check = true;
                                long elapsedTime = System.currentTimeMillis() - startTime;
                                int secs = (int) (elapsedTime / 1000);
                                int mins = secs / 60;
                                int hrs = mins / 60;
                                secs = secs % 60;
                                pingTime.setText(String.valueOf(secs));
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                    /*binding.btnTest.setText("Test Again");
                                    binding.btnTest.setVisibility(View.VISIBLE);
                                    Log.d("spppppp", "run: " + angle);
                                    binding.speedMeterSign.startAnimation(animation);*/
                                    }
                                }, 3000);
                            }
                        }

                    /*if (count == 0) {
                        java.math.BigDecimal bd2 = progressModel.getUploadSpeed();
                        final double d2 = bd2.doubleValue();
                        binding.uploadTime.setText("" + formatFileSizeS(d2));
                        binding.timingU.setText("" + formatFileSize(d2));
                    }*/
                    }
                });
                builder.start("http://ipv4.ikoula.testdebit.info/1M.iso", 2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*
    private void getDownloadSpeed() {

        try {
            binding.btnTest.setVisibility(View.GONE);
            startTime = System.currentTimeMillis();
            InternetSpeedBuilder builder = new InternetSpeedBuilder(this);

            builder.setOnEventInternetSpeedListener(new InternetSpeedBuilder.OnEventInternetSpeedListener() {
                @Override
                public void onDownloadProgress(int count, ProgressionModel progressModel) {
                    Log.d("InternetSpeedCheck", "onDownloadProgress: " + progressModel);
                }

                @Override
                public void onUploadProgress(int count, ProgressionModel progressModel) {
                    Log.d("InternetSpeedCheck", "onUploadProgress: " + progressModel);
                }

                @Override
                public void onTotalProgress(int count, ProgressionModel progressModel) {
                    Log.d("InternetSpeedCheck", "onTotalProgress: " + progressModel);

                    if (count == 0) {
                        java.math.BigDecimal bd = progressModel.getDownloadSpeed();
                        final double d = bd.doubleValue();
                        binding.downloadTime.setText("" + formatFileSizeS(d));
                        binding.timingD.setText("" + formatFileSize(d));
                        binding.downloadTimeFirst.setText("" + formatFileSizeS(d));

                        java.math.BigDecimal bd2 = progressModel.getUploadSpeed();
                        final double d2 = bd2.doubleValue();
                        binding.uploadTime.setText("" + formatFileSizeS(d2));
                        binding.timingU.setText("" + formatFileSize(d2));

                        formatFileSizeS(d);
                        animation = new RotateAnimation(0, angle,
                                Animation.RELATIVE_TO_SELF, 0.5f,
                                Animation.RELATIVE_TO_SELF, 0.5f);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        animation.setDuration(1000);
                        animation.setFillAfter(true);

                        if (!check) {
                            check = true;
                            long elapsedTime = System.currentTimeMillis() - startTime;
                            int secs = (int) (elapsedTime / 1000);
                            int mins = secs / 60;
                            int hrs = mins / 60;
                            secs = secs % 60;
                            binding.pingTime.setText(String.valueOf(secs));
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    binding.btnTest.setVisibility(View.VISIBLE);
                                    Log.d("spppppp", "run: " + angle);
                                    binding.speedMeterSign.startAnimation(animation);
                                }
                            }, 3000);
                        }
                    }
                }
            });
            builder.start("ftp://speedtest.tele2.net/1MB.zip", 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
*/

    private void downloadSpeed() {
        SpeedTestSocket speedTestSocket = new SpeedTestSocket();

// add a listener to wait for speedtest completion and progress
        speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

            @Override
            public void onCompletion(SpeedTestReport report) {
                // called when download/upload is complete
                System.out.println("[COMPLETED] rate in octet/s : " + report.getTransferRateOctet());
                System.out.println("[COMPLETED] rate in bit/s   : " + report.getTransferRateBit());
            }

            @Override
            public void onError(SpeedTestError speedTestError, String errorMessage) {
                // called when a download/upload error occur
            }

            @Override
            public void onProgress(float percent, SpeedTestReport report) {
                // called to notify download/upload progress
                System.out.println("[PROGRESS] progress : " + percent + "%");
                System.out.println("[PROGRESS] rate in octet/s : " + report.getTransferRateOctet());
                System.out.println("[PROGRESS] rate in bit/s   : " + report.getTransferRateBit());
            }
        });
        speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso");
        speedTestSocket.startUpload("http://ipv4.ikoula.testdebit.info/", 1000000);
    }

    public static String formatFileSize(double size) {

        String hrSize;
        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = " ";
        } else if (g > 1) {
            hrSize = " gb/s";
        } else {
            hrSize = " mb/s";
        }/* else if ( k>1 ) {
            hrSize = dec.format(k).concat(" kb/s");
        } else {
            hrSize = dec.format(b);
        }*/

        return hrSize;
    }

    public String formatFileSizeS(double size) {

        String hrSize;
        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t);
            getAngle(hrSize);

        } else if (g > 1) {
            hrSize = dec.format(g);
            getAngle(hrSize);
        } else {
            hrSize = dec.format(m);
            getAngle(hrSize);
        }

        return hrSize;
    }

    private void getAngle(String hrSize) {
        if (Double.parseDouble(hrSize) < 1) {
            Random random = new Random();
            List<Integer> val = Arrays.asList(3, 5, 7, 9, 11, 13, 15, 18, 20);
            angle = val.get(random.nextInt(val.size()));
            //angle = 15;
        } else if (Double.parseDouble(hrSize) > 1 && Double.parseDouble(hrSize) < 5) {
            Random random = new Random();
            List<Integer> val = Arrays.asList(32, 35, 37, 39, 42, 45, 48, 50);
            angle = val.get(random.nextInt(val.size()));
            // angle = 39;
        } else if (Double.parseDouble(hrSize) > 5 && Double.parseDouble(hrSize) < 10) {
            Random random = new Random();
            List<Integer> val = Arrays.asList(60, 62, 64, 66, 69, 72, 75, 78, 80);
            angle = val.get(random.nextInt(val.size()));
            //angle = 69;
        } else if (Double.parseDouble(hrSize) > 10 && Double.parseDouble(hrSize) < 20) {
            Random random = new Random();
            List<Integer> val = Arrays.asList(94, 96, 98, 99, 102, 105, 109, 120, 125);
            angle = val.get(random.nextInt(val.size()));
            //angle = 105;
        } else if (Double.parseDouble(hrSize) > 20 && Double.parseDouble(hrSize) < 30) {
            Random random = new Random();
            List<Integer> val = Arrays.asList(130, 135, 138, 140, 145, 150, 153, 155, 159);
            angle = val.get(random.nextInt(val.size()));
            // angle = 135;
        } else if (Double.parseDouble(hrSize) > 30 && Double.parseDouble(hrSize) < 50) {
            Random random = new Random();
            List<Integer> val = Arrays.asList(170, 172, 175, 178, 185, 190);
            angle = val.get(random.nextInt(val.size()));
            //  angle = 175;
        } else if (Double.parseDouble(hrSize) > 50 && Double.parseDouble(hrSize) < 75) {
            Random random = new Random();
            List<Integer> val = Arrays.asList(195, 200, 205, 208, 215);
            angle = val.get(random.nextInt(val.size()));
            //  angle = 205;
        } else if (Double.parseDouble(hrSize) > 75) {
            Random random = new Random();
            List<Integer> givenList = Arrays.asList(225, 228, 235, 240, 245);
            angle = givenList.get(random.nextInt(givenList.size()));
            //angle = 230;
        }
    }

    private void testCompleted() {
        if (!isTestFinish) {
            isTestFinish = true;
            Log.d("SpeedTest", "onTotalProgress: 1");
            Log.d("spppppp", "run: " + angle);
            speed_meter_sign.startAnimation(animation);
        }
    }
}
