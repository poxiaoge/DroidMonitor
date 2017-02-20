package com.ss.android.article.webmonitor;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.ss.android.article.webmonitor.BaseApplication.GET_LIST_OK;

public class MainActivity extends FragmentActivity {

    private ViewPager  viewPager;
    private List<Fragment> fragmentList;
    private FragmentPagerAdapter pagerAdapter;

    private final int numHome = 0;
    private final int numSetting = 1;
    private final int numAbout = 2;

    private HorizontalScrollView bottomView;

    private LinearLayout tabHome;
    private LinearLayout tabSetting;
    private LinearLayout tabAbout;

    HomeFragment homeFragment = null;
    SettingFragment settingFragment = null;
    AboutFragment aboutFragment = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new HttpHandleThread("get_list", "", Utils.createGetListURLString(),myHandler).start();
        initView();
        bindListener();
        setSelected(numHome);
        Intent i = new Intent(MainActivity.this, WebService.class);
        startService(i);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setSelected(numHome);
        if (homeFragment != null) {
            homeFragment.updateData();
        }
    }

    private void initView() {
        bottomView = (HorizontalScrollView) findViewById(R.id.bottom_view);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        tabHome = (LinearLayout) findViewById(R.id.tab_home);
        tabSetting = (LinearLayout) findViewById(R.id.tab_setting);
        tabAbout = (LinearLayout) findViewById(R.id.tab_about);

        fragmentList = new ArrayList<>();
        homeFragment = new HomeFragment();
        settingFragment = new SettingFragment();
        aboutFragment = new AboutFragment();
        fragmentList.add(homeFragment);
        fragmentList.add(settingFragment);
        fragmentList.add(aboutFragment);

        pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                super.destroyItem(container, position, object);
            }
        };

        viewPager.setAdapter(pagerAdapter);

    }

    private void bindListener() {
        tabHome.setOnClickListener(clickListener1);
        tabSetting.setOnClickListener(clickListener1);
        tabAbout.setOnClickListener(clickListener1);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                setSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

    }

    View.OnClickListener clickListener1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tab_home:
                    setSelected(numHome);
                    break;
                case R.id.tab_setting:
                    setSelected(numSetting);
                    break;
                case R.id.tab_about:
                    setSelected(numAbout);
                    break;
            }
        }
    };

    private void setSelected(int num) {
        viewPager.setCurrentItem(num);
        clearSelected();
        switch (num) {
            case numHome:
                tabHome.setBackgroundResource(R.color.colorFocus);
                break;
            case numSetting:
                tabSetting.setBackgroundResource(R.color.colorFocus);
                break;
            case numAbout:
                tabAbout.setBackgroundResource(R.color.colorFocus);
                break;
        }
    }

    private void clearSelected() {
        tabHome.setBackgroundResource(R.color.colorUnfocus);
        tabSetting.setBackgroundResource(R.color.colorUnfocus);
        tabAbout.setBackgroundResource(R.color.colorUnfocus);
    }

    public Handler myHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_LIST_OK:
                    Toast.makeText(MainActivity.this,"GET_LIST_OK",Toast.LENGTH_SHORT).show();
            }
        }
    };







}
