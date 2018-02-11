package com.example.ming.easytablayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager mViewPager;
    EasyTabLayout mTabLayout;

    private static final String[] TAB_TITLE_OWNER = {
            "淘宝",
            "京东",
            "网易云音乐",
            "饿了么",
            "汽车之家"
};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.activity_easyTabLayout);
        List<SimpleFragment> fragmentList = new ArrayList<>();
        SimpleFragment fragment1 = SimpleFragment.newInstance("fragment 1");
        fragmentList.add(fragment1);
        SimpleFragment fragment2 = SimpleFragment.newInstance("fragment 2");
        fragmentList.add(fragment2);
        SimpleFragment fragment3 = SimpleFragment.newInstance("fragment 3");
        fragmentList.add(fragment3);
        SimpleFragment fragment4 = SimpleFragment.newInstance("fragment 4");
        fragmentList.add(fragment4);
        SimpleFragment fragment5= SimpleFragment.newInstance("fragment 5");
        fragmentList.add(fragment5);
        SimplePagerAdapter adapter = new SimplePagerAdapter(getSupportFragmentManager());
        adapter.setFragmentList(fragmentList);
        adapter.setTabTitle(TAB_TITLE_OWNER);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }
}
