package com.stellar.vpn.Activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;
import com.stellar.vpn.R;
import com.stellar.vpn.Utils.Constants;

import java.io.IOException;

import top.oneconnectapi.app.api.OneConnect;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    OneConnect oneConnect = new OneConnect();
                    oneConnect.initialize(IntroActivity.this, "hoTHWnjPuuoP6cZeyDJ.3EImcUS1.NvLdt8MVxWeOJFvomjSec");  // Put Your OneConnect API key
                    try {
                        Constants.FREE_SERVERS = oneConnect.fetch(true);
                        Constants.PREMIUM_SERVERS = oneConnect.fetch(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        super.onCreate(savedInstanceState);

        SliderPage slider1 = new SliderPage();
        slider1.setTitle("Be More Safe And Secure");
        slider1.setDescription("Now you can browse as much as you want. We are fast, secure and unlimited!");
        slider1.setImageDrawable(R.drawable.screen_one);
        slider1.setBgColor(getResources().getColor(R.color.primary_dark));
        slider1.setTitleColor(getResources().getColor(R.color.white));
        slider1.setDescColor(getResources().getColor(R.color.white));

        SliderPage slider2 = new SliderPage();
        slider2.setTitle("Lifetime Premium Access");
        slider2.setDescription("Lifetime premium access to 20+ servers. No need pay every month!");
        slider2.setImageDrawable(R.drawable.screen_two);
        slider2.setBgColor(getResources().getColor(R.color.primary_dark));
        slider2.setTitleColor(getResources().getColor(R.color.white));
        slider2.setDescColor(getResources().getColor(R.color.white));

        addSlide(AppIntroFragment.newInstance(slider1));
        addSlide(AppIntroFragment.newInstance(slider2));


        setIndicatorColor(getResources().getColor(R.color.white), getResources().getColor(R.color.grayishblue)) ;
        setFadeAnimation();
    }

    @Override
    public void onSkipPressed(Fragment currenFragment) {
        startActivity(new Intent(getApplicationContext(), AcceptPrivacyPolicy.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currenFragment) {
        startActivity(new Intent(getApplicationContext(), AcceptPrivacyPolicy.class));
        finish();
    }
}

