package com.stellar.vpn;

import android.app.Application;
import android.content.ContextWrapper;

import com.stellar.vpn.Activities.MainActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pixplicity.easyprefs.library.Prefs;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobileAds.initialize(this);
        DatabaseReference will_dev_33223327_admob_app_open = FirebaseDatabase.getInstance().getReference("will_dev_33223327_admob_app_open");

        will_dev_33223327_admob_app_open.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                MainActivity.will_dev_33223327_admob_app_open = value;
                new AppOpenManager(App.this);
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });



        FirebaseApp.initializeApp(this);
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();


    }


}
