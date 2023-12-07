package com.stellar.vpn.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.stellar.vpn.Fragments.FragmentFree;
import com.stellar.vpn.R;
import com.google.android.material.tabs.TabLayout;
import com.stellar.vpn.AdapterWrappers.TabAdapter;
import com.stellar.vpn.Fragments.FragmentVip;

public class Servers extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serverswill_dev);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarold);
        toolbar.setTitle("Premium Servers");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        adapter = new TabAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentFree(), "Free Server");
        adapter.addFragment(new FragmentVip(), "Vip Server");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }
}
