package com.example.ming.easytablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by ming on 2018/2/9.
 * EasyTabLayout是为了满足充当ViewPager的指示器，部分参照原生控件TabLayout，优点是每一个Item都是自适应大小，整个控件可以左右充满屏幕，并且每一个Item之间Margin是相同的
 * 目前只支持title为String，Item为TextView，TextView之间插入空白View充当margin作用
 */

public class EasyTabLayout extends RelativeLayout implements View.OnClickListener {
    private int tabIndicatorColor;
    private int tabIndicatorHeight;
    private int tabSelectedTextColor;
    private int tabTextColor;
    private int textSize;
    private LinearLayout content;
    private View indicator;

    private ViewPager mViewPager;
    private TabLayoutOnPageChangeListener mPageChangeListener;
    private AdapterChangeListener mAdapterChangeListener;
    private PagerAdapter mPagerAdapter;
    private DataSetObserver mPagerAdapterObserver;
    private final ArrayList<TabView> tabs = new ArrayList<>();
    private final ArrayList<View> emptyViews = new ArrayList<>();

    public EasyTabLayout(Context context) {
        this(context, null);
    }

    public EasyTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EasyTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr){
        tabIndicatorColor = Color.BLUE;
        tabIndicatorHeight = 2;
        tabSelectedTextColor = Color.BLUE;
        tabTextColor = Color.BLACK;
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.EasyTabLayout, defStyleAttr, 0);
        final int N = a.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.EasyTabLayout_tabIndicatorColor:
                    tabIndicatorColor = a.getColor(attr, Color.BLUE);
                    break;
                case R.styleable.EasyTabLayout_tabIndicatorHeight:
                    tabIndicatorHeight = a.getDimensionPixelOffset(attr, 0);
                    break;
                case R.styleable.EasyTabLayout_tabSelectedTextColor:
                    tabSelectedTextColor = a.getColor(attr, Color.BLUE);
                    break;
                case R.styleable.EasyTabLayout_tabTextColor:
                    tabTextColor = a.getColor(attr, Color.BLUE);
                    break;
                case R.styleable.EasyTabLayout_textSize:
                    textSize = a.getDimensionPixelSize(attr, 0);
                    break;
                default:
                    break;
            }
        }
        a.recycle();

        removeAllViews();
        content = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        addView(content, layoutParams);
        indicator = new View(context);
        indicator.setBackgroundColor(tabIndicatorColor);
        RelativeLayout.LayoutParams indicatorParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, tabIndicatorHeight);
        indicatorParams.addRule(ALIGN_PARENT_BOTTOM);
        addView(indicator, indicatorParams);
    }

    public void setupWithViewPager(@Nullable ViewPager viewPager) {
        if(mViewPager != null){
            if (mPageChangeListener != null) {
                mViewPager.removeOnPageChangeListener(mPageChangeListener);
            }
            if (mAdapterChangeListener != null) {
                mViewPager.removeOnAdapterChangeListener(mAdapterChangeListener);
            }
        }
        if(viewPager != null){
            mViewPager = viewPager;
            // Add our custom OnPageChangeListener to the ViewPager
            if (mPageChangeListener == null) {
                mPageChangeListener = new TabLayoutOnPageChangeListener(this);
            }
            //添加ViewPager滑动监听
            viewPager.addOnPageChangeListener(mPageChangeListener);

            final PagerAdapter adapter = viewPager.getAdapter();
            if (adapter != null) {
                // Now we'll populate ourselves from the pager adapter, adding an observer if
                // autoRefresh is enabled
                setPagerAdapter(adapter, true);
            }

            // Add a listener so that we're notified of any adapter changes
            if (mAdapterChangeListener == null) {
                mAdapterChangeListener = new AdapterChangeListener();
            }
            //添加adapter监听，当viewPager的adapter改变时(不是adapter中的数据改变)，会调用mAdapterChangeListener
            viewPager.addOnAdapterChangeListener(mAdapterChangeListener);

            // Now update the scroll position to match the ViewPager's current item
//            setScrollPosition(viewPager.getCurrentItem(), 0f, true);

        }else {
            // We've been given a null ViewPager so we need to clear out the internal state,
            // listeners and observers
            mViewPager = null;
        }

    }

    /**
     * 设置ViewPager的adapter
     * @param adapter adapter
     * @param addObserver boolean
     */
    private void setPagerAdapter(@Nullable final PagerAdapter adapter, final boolean addObserver) {
        if (mPagerAdapter != null && mPagerAdapterObserver != null) {
            // If we already have a PagerAdapter, unregister our observer
            mPagerAdapter.unregisterDataSetObserver(mPagerAdapterObserver);
        }

        mPagerAdapter = adapter;

        if (addObserver && adapter != null) {
            // Register our observer on the new adapter
            if (mPagerAdapterObserver == null) {
                mPagerAdapterObserver = new PagerAdapterObserver();
            }
            //添加adapter数据监听，当adapter中的数据改变时，会调用mPagerAdapterObserver
            adapter.registerDataSetObserver(mPagerAdapterObserver);
        }

        // Finally make sure we reflect the new adapter
        populateFromPagerAdapter();
    }

    /**
     * 更新控件内容
     */
    private void populateFromPagerAdapter() {
        removeAllTabs();

        if (mPagerAdapter != null) {
            final int adapterCount = mPagerAdapter.getCount();
            for (int i = 0; i < adapterCount; i++) {
                TabView tabView = new TabView();
                TextView textView = new TextView(getContext());
                textView.setText(mPagerAdapter.getPageTitle(i));
                textView.setTextColor(tabTextColor);
                if(textSize > 0){
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
                }
                textView.setGravity(Gravity.CENTER);
                tabView.setTextView(textView);
                textView.setText(mPagerAdapter.getPageTitle(i));
                if(content != null){
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    addEmptyView(i);
                    textView.setTag(i);
                    content.addView(textView, params);
                    addEmptyView(i);
                    textView.setOnClickListener(this);
                }
                tabs.add(tabView);
            }
            // Make sure we reflect the currently set ViewPager item
            if (mViewPager != null && adapterCount > 0) {
                final int curItem = mViewPager.getCurrentItem();
                selectTab(curItem);
            }
        }
    }

    private void removeAllTabs(){
        if(content != null){
            content.removeAllViews();
            tabs.clear();
            emptyViews.clear();
        }
    }

    private void selectTab(int position){
        if(position < 0 || position >= tabs.size()){
            return;
        }
        for(int i=0; i < tabs.size(); i++){
            TabView tabView = tabs.get(i);
            if(position == i){
                tabView.setSelected(true);
            }else {
                tabView.setSelected(false);
            }
        }
    }

    /**
     * emptyView是用来填充TextView左右空白的
     * @param position 用来充当点击事件识别Id
     */
    private void addEmptyView(int position){
        View view = new View(getContext());
        view.setTag(position);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, 1);
        if(content != null){
            content.addView(view, params);
            view.setOnClickListener(this);
            emptyViews.add(view);
        }
    }

    /**
     * 根据ViewPager的滑动状态，更新指示器
     * @param position ViewPager当前页
     * @param positionOffset ViewPager当前页滑动百分比
     */
    private void setScrollPosition(int position, float positionOffset){
        if(tabs == null)
            return;
        TextView currentView = tabs.get(position).getTextView();
        TextView nextView = null;
        if(position < tabs.size()-1){
            nextView = tabs.get(position + 1).getTextView();
        }
        int currentViewWidth = currentView.getWidth();
        int nextViewWidth = nextView == null ? 0 : nextView.getWidth();
        int emptyWidth = 0;
        int preTotalWidth = 0;
        if(emptyViews.size() > 0){
            View emptyView = emptyViews.get(0);
            emptyWidth = emptyView.getWidth();
        }
        for(int i=0; i<position; i++){
            preTotalWidth += emptyWidth * 2;
            preTotalWidth += tabs.get(i).getTextView().getWidth();
        }
        RelativeLayout.LayoutParams indicatorParams = (LayoutParams) indicator.getLayoutParams();
        indicatorParams.width = (int) (emptyWidth * 2 + currentViewWidth * (1 - positionOffset) + nextViewWidth * positionOffset);
        indicatorParams.leftMargin = (int) (getPaddingLeft() + preTotalWidth + (currentViewWidth + emptyWidth * 2) * positionOffset);
        indicator.setLayoutParams(indicatorParams);
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        mViewPager.setCurrentItem(position);
    }


    public static class TabLayoutOnPageChangeListener implements ViewPager.OnPageChangeListener {
        private final WeakReference<EasyTabLayout> mTabLayoutRef;

        public TabLayoutOnPageChangeListener(EasyTabLayout tabLayout) {
            mTabLayoutRef = new WeakReference<>(tabLayout);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }

        @Override
        public void onPageScrolled(final int position, final float positionOffset,
                                   final int positionOffsetPixels) {
            final EasyTabLayout tabLayout = mTabLayoutRef.get();
            if (tabLayout != null) {
                tabLayout.setScrollPosition(position, positionOffset);
            }
        }

        @Override
        public void onPageSelected(final int position) {
            final EasyTabLayout tabLayout = mTabLayoutRef.get();
            tabLayout.selectTab(position);
        }
    }

    private class AdapterChangeListener implements ViewPager.OnAdapterChangeListener {

        AdapterChangeListener() {
        }

        @Override
        public void onAdapterChanged(@NonNull ViewPager viewPager,
                                     @Nullable PagerAdapter oldAdapter, @Nullable PagerAdapter newAdapter) {
            if (mViewPager == viewPager) {
                setPagerAdapter(newAdapter, true);
            }
        }
    }

    private class PagerAdapterObserver extends DataSetObserver {
        PagerAdapterObserver() {
        }

        @Override
        public void onChanged() {
            populateFromPagerAdapter();
        }

        @Override
        public void onInvalidated() {
            populateFromPagerAdapter();
        }
    }

    private class TabView{
        TextView textView;
        boolean isSelected;

        public TextView getTextView() {
            return textView;
        }

        private void setTextView(TextView textView) {
            this.textView = textView;
        }

        public boolean isSelected() {
            return isSelected;
        }

        private void setSelected(boolean selected) {
            isSelected = selected;
            if(textView != null){
                if(selected){
                    textView.setTextColor(tabSelectedTextColor);
                }else {
                    textView.setTextColor(tabTextColor);
                }
            }
        }
    }
}
