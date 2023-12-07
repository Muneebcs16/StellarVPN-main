package com.stellar.vpn.AdapterWrappers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stellar.vpn.Fragments.FragmentFree;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.stellar.vpn.Config;
import com.stellar.vpn.R;
import com.stellar.vpn.Activities.MainActivity;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.stellar.vpn.Utils.Countries;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServerListAdapterFree extends RecyclerView.Adapter<ServerListAdapterFree.mViewhoder> {

    ArrayList<Countries> datalist = new ArrayList<>();
    com.facebook.ads.AdView facebookAdview;

    private final Context context;
   // RemainingTraffic remainingTrafficResponse;
    private final int AD_TYPE = 0;
    private final int CONTENT_TYPE = 1;
    InterstitialAd mInterstitialAd;
    FragmentFree fragment;
    public ServerListAdapterFree(Context ctx, FragmentFree fragment) {
        this.context=ctx;
        this.fragment = fragment;

    }

    @NonNull
    @Override
    public mViewhoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        AdView adview;

        if (viewType == AD_TYPE) {
            if(MainActivity.type.equals("ad")) {
                adview = new AdView(context);
                adview.setAdSize(AdSize.BANNER);
                adview.setAdUnitId(MainActivity.admob_banner_id);
                float density = context.getResources().getDisplayMetrics().density;
                int height = Math.round(AdSize.BANNER.getHeight() * density);
                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, height);
                adview.setLayoutParams(params);
                AdRequest request = new AdRequest.Builder().build();
                adview.loadAd(request);
                return new mViewhoder(adview);

            }



            else {

                /*

                LinearLayout adContainer = new LinearLayout(context);
                com.facebook.ads.AdView facebookAdview;

                AudienceNetworkAds.initialize(context);
                facebookAdview = new com.facebook.ads.AdView(context, MainActivity.will_dev_33223327_fb_banner_id, com.facebook.ads.AdSize.BANNER_HEIGHT_50);
                adContainer.addView(facebookAdview);

                com.facebook.ads.AdListener adListener = new
                        com.facebook.ads.AdListener() {
                            public void onError(Ad ad, AdError adError) {
                                Log.d("FACEBOOKAD", adError.getErrorMessage());
                            }

                            public void onAdLoaded(Ad ad) {

                            }

                            public void onAdClicked(Ad ad) {

                            }

                            public void onLoggingImpression(Ad ad) {

                            }
                        };

                facebookAdview.loadAd(facebookAdview.buildLoadAdConfig().withAdListener(adListener).build());

                return new mViewhoder(adContainer);
                */

                com.facebook.ads.NativeAdLayout nativeAdLayout = new com.facebook.ads.NativeAdLayout(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(20, 0, 20, 10);
                nativeAdLayout.setLayoutParams(params);

                NativeAd nativeAd = new NativeAd(context, MainActivity.will_dev_33223327_fb_banner_id);
                NativeAdListener nativeAdListener = new NativeAdListener() {
                    @Override
                    public void onMediaDownloaded(Ad ad)
                    {
                    }

                    @Override
                    public void onError(Ad ad, AdError adError)
                    {
                        Log.w("AdLoader", "onAdFailedToLoad" + adError.getErrorMessage());
                    }

                    @Override
                    public void onAdLoaded(Ad ad) {
                        if (nativeAd == null || nativeAd != ad) {
                            return;
                        }
                        nativeAd.unregisterView();

                        if ((!Config.vip_subscription && !Config.all_subscription))
                        {
                            nativeAdLayout.setVisibility(View.VISIBLE);
                        }
                        LayoutInflater inflater = LayoutInflater.from(context);
                        LinearLayout adView = (LinearLayout) inflater.inflate(R.layout.native_banner_ad_layout, nativeAdLayout, false);
                        nativeAdLayout.addView(adView);

                        LinearLayout adChoicesContainer = nativeAdLayout.findViewById(R.id.ad_choices_container);
                        AdOptionsView adOptionsView = new AdOptionsView(context, nativeAd, nativeAdLayout);
                        adChoicesContainer.removeAllViews();
                        adChoicesContainer.addView(adOptionsView, 0);

                        com.facebook.ads.MediaView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                        com.facebook.ads.MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                        nativeAdTitle.setText(nativeAd.getAdvertiserName());
                        nativeAdBody.setText(nativeAd.getAdBodyText());
                        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                        List<View> clickableViews = new ArrayList<>();
                        clickableViews.add(nativeAdTitle);
                        clickableViews.add(nativeAdCallToAction);

                        nativeAd.registerViewForInteraction(
                                adView, nativeAdMedia, nativeAdIcon, clickableViews);
                    }

                    @Override
                    public void onAdClicked(Ad ad)
                    {

                    }

                    @Override
                    public void onLoggingImpression(Ad ad)
                    {

                    }
                };
                nativeAd.loadAd(
                        nativeAd.buildLoadAdConfig()
                                .withAdListener(nativeAdListener)
                                .build());


                return new mViewhoder(nativeAdLayout);
            }
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.server_list_free_will_dev, parent, false);
            return new mViewhoder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final mViewhoder holder, int position) {
        if(getItemViewType(position) == CONTENT_TYPE){
           // remainingTrafficResponse=new RemainingTraffic();
            Countries data=datalist.get(position);
            Locale locale=new Locale("",data.getCountry());
            //holder.flag.setImageResource(context.getResources().getIdentifier("drawable/"+data.getCountry().toLowerCase(),null,context.getPackageName()));
            Glide.with(context)
                    .load(data.getFlagUrl())
                    .into(holder.flag);
            holder.app_name.setText(locale.getDisplayCountry());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                  //  fragment.showInterstitial(data);
                    Intent intent=new Intent(context, MainActivity.class);
                    intent.putExtra("c",data);
                    intent.putExtra("type",MainActivity.type);
                    intent.putExtra("admob_banner",MainActivity.admob_banner_id);
                    intent.putExtra("admob_interstitial",MainActivity.admob_interstitial_id);
                    intent.putExtra("will_dev_33223327_fb_banner",MainActivity.will_dev_33223327_fb_banner_id);
                    intent.putExtra("will_dev_33223327_fb_interstitial",MainActivity.will_dev_33223327_fb_interstitial_id);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(intent);


                }
            });

        }



    }

    @Override
    public int getItemCount() {
        return datalist.size();
    }
    @Override
    public int getItemViewType(int position) {
        return datalist.get(position) ==null? AD_TYPE:CONTENT_TYPE;
    }

    public static class mViewhoder extends RecyclerView.ViewHolder
    {
        TextView app_name;
        ImageView flag;

        public mViewhoder(View itemView) {
            super(itemView);
            app_name=itemView.findViewById(R.id.region_title);
             flag=itemView.findViewById(R.id.country_flag);
        }
    }

    public interface RegionListAdapterInterface {
        void onCountrySelected(Countries item);
    }
    public void setData(List<Countries> servers) {
        datalist.clear();
        datalist.addAll(servers);
        notifyDataSetChanged();
    }
}
