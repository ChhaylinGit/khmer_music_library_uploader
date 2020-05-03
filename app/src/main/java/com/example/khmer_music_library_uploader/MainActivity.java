package com.example.khmer_music_library_uploader;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.khmer_music_library_uploader.fragment.AlbumUploadFragment;
import com.example.khmer_music_library_uploader.fragment.MusicTypeFragment;
import com.example.khmer_music_library_uploader.fragment.MusicUploadFragment;
import com.example.khmer_music_library_uploader.fragment.ProductionFragment;
import com.example.khmer_music_library_uploader.fragment.SingerFragment;
import com.example.khmer_music_library_uploader.fragment.SingerTypeFragment;
import com.example.khmer_music_library_uploader.fragment.SongUploadFragment;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setUpviewPager();
        setCustomFont();
    }

    private void initView()
    {
        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void  setUpviewPager()
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),1);
        adapter.addFragment(new MusicUploadFragment(),getResources().getText(R.string.tab_add_song).toString());
        adapter.addFragment(new ProductionFragment(),getResources().getText(R.string.tab_add_production).toString());
        adapter.addFragment(new AlbumUploadFragment(),getResources().getText(R.string.tab_add_album).toString());
        adapter.addFragment(new MusicTypeFragment(),getResources().getText(R.string.tab_add_music_type).toString());
        adapter.addFragment(new SingerTypeFragment(),getResources().getText(R.string.tab_add_singer_type).toString());
        adapter.addFragment(new SingerFragment(),getResources().getText(R.string.tab_add_singer).toString());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter{

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment,String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void setCustomFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    //Put your font in assests folder
                    //assign name of the font here (Must be case sensitive)
                    ((TextView) tabViewChild).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/khmer_os_battambang.ttf"));
                }
            }
        }
    }
}
