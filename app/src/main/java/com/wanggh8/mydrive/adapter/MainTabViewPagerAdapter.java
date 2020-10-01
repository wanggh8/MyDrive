package com.wanggh8.mydrive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.wanggh8.mydrive.R;

import java.util.List;

/**
 * 主页tab ViewPager适配器
 * 每次切换销毁重建
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/30
 */
public class MainTabViewPagerAdapter extends FragmentStatePagerAdapter{
    private String[] mTitles;
    private int[] imageRes;
    private List<Fragment> mFragments;
    private Context mContext;

    public MainTabViewPagerAdapter(Context context, FragmentManager fm, List<Fragment> fragments
            , String[] mTitles, int[] imageRes) {
        super(fm);
        this.mContext = context;
        this.mTitles = mTitles;
        this.mFragments = fragments;
        this.imageRes = imageRes;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mTitles.length == mFragments.size() ? mFragments.size() : 0;
    }

    /**
     * 要使用我们自定义的布局，这里返回null
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    /**
     * 定义一个方法，来返回Tab的内容
     */
    public View getTabView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_main_table, null);
        TextView tv = view.findViewById(R.id.tvTabTitle);
        ImageView iv = view.findViewById(R.id.ivTabIcon);
        tv.setText(mTitles[position]);
        iv.setImageResource(imageRes[position]);
        return view;
    }
}
