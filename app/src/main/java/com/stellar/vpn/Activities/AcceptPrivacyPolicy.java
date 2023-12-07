package com.stellar.vpn.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.stellar.vpn.R;

public class AcceptPrivacyPolicy extends AppCompatActivity {

    CheckBox checkBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accept_privacy_policy_will_dev);

        TextView wc_msg = findViewById(R.id.welcome_msg);
        TextView accept_pp = findViewById(R.id.tv_accept_privacy_policy);
        TextView btnAccept = findViewById(R.id.btnAccept);
        checkBox = findViewById(R.id.check) ;


        Resources res = getResources();
        String msg = String.format(res.getString(R.string.welcome_message), getResources().getString(R.string.app_name2));
        wc_msg.setText(msg);

        SpannableString myString = new SpannableString(getResources().getString(R.string.privacy_policy_msg));

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.tab_background_selected));
                ds.setUnderlineText(false); //remove underline
                ds.setTypeface(Typeface.DEFAULT_BOLD);
            }

            @Override
            public void onClick(View textView) {

                Intent intent = new Intent(AcceptPrivacyPolicy.this, PrivacyPolicy.class);
                startActivity(intent);
            }
        };

        myString.setSpan(clickableSpan,38,59, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        accept_pp.setMovementMethod(LinkMovementMethod.getInstance());
        accept_pp.setText(myString);

        btnAccept.setOnClickListener(view -> {
            if(checkBox.isChecked()){
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("firstTime", false);
                editor.apply();
                finish();
            }
            else{

              //  Toasty.success(AcceptPrivacyPolicy.this,"Please read and accept Terms and Conditions", Toast.LENGTH_SHORT).show();
        }

        });
    }
}