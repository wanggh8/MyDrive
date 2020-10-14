package com.wanggh8.mydrive.activity;

import android.view.View;
import android.widget.TableLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.exception.MsalException;
import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.adapter.MainTabViewPagerAdapter;
import com.wanggh8.mydrive.base.BaseActivity;
import com.wanggh8.mydrive.fragment.main.MainDownloadFragment;
import com.wanggh8.mydrive.fragment.main.MainDriveFragment;
import com.wanggh8.mydrive.fragment.main.MainFileFragment;
import com.wanggh8.mydrive.fragment.main.MainPersonalFragment;
import com.wanggh8.mydrive.fragment.main.MainPlayFragment;
import com.wanggh8.mydrive.utils.AuthenticationHelper;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/30
 */
public class MainActivity extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MainTabViewPagerAdapter mainTabViewPagerAdapter;
    // tab信息
    private int[] tabIcons = {R.drawable.selector_bar_main_drive, R.drawable.selector_bar_main_file,
            R.drawable.selector_bar_main_play, R.drawable.selector_bar_main_download, R.drawable.selector_bar_main_personal};
    private String[] tabNames = {"云盘", "本地", "播放", "下载", "我的"};
    // 当前选中的tab
    private int mTabChoice = 0;
    // fragment
    private List<Fragment> fragmentList;
    private MainDriveFragment mainDriveFragment;
    private MainFileFragment mainFileFragment;
    private MainPlayFragment mainPlayFragment;
    private MainDownloadFragment mainDownloadFragment;
    private MainPersonalFragment mainPersonalFragment;

    // OneDrive
    private IAccount mIAccount;
    private List<IAccount> accountList;


    @Override
    public int getContentLayout() {
        return R.layout.act_main;
    }

    @Override
    public void beforeInitView() {
        initMSAL();
        initFragmentList();
    }

    private void initMSAL() {
        AuthenticationHelper.setInstance(this, new AuthenticationHelper.IAuthenticationHelperCreatedListener() {
            @Override
            public void onCreated(AuthenticationHelper authHelper) {

            }

            @Override
            public void onError(MsalException exception) {

            }
        });
    }

    private void initFragmentList() {
        fragmentList = new ArrayList<>();
        mainDriveFragment = new MainDriveFragment();
        mainFileFragment = new MainFileFragment();
        mainPlayFragment = new MainPlayFragment();
        mainDownloadFragment = new MainDownloadFragment();
        mainPersonalFragment = new MainPersonalFragment();
        fragmentList.add(mainDriveFragment);
        fragmentList.add(mainFileFragment);
        fragmentList.add(mainPlayFragment);
        fragmentList.add(mainDownloadFragment);
        fragmentList.add(mainPersonalFragment);
    }

    @Override
    public void initView() {
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
    }

    @Override
    public void afterInitView() {
        mainTabViewPagerAdapter = new MainTabViewPagerAdapter(this, getSupportFragmentManager(),
                fragmentList, tabNames, tabIcons);
        viewPager.setAdapter(mainTabViewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(mainTabViewPagerAdapter.getTabView(i));
        }
    }

    @Override
    public void bindListener() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mTabChoice = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onClickEvent(View v) {

    }
}
