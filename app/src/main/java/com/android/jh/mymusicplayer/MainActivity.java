package com.android.jh.mymusicplayer;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jh.mymusicplayer.Data.Domain.Music;
import com.android.jh.mymusicplayer.util.Adapter.FragmentAdapter;
import com.android.jh.mymusicplayer.util.Control.Controller;
import com.android.jh.mymusicplayer.util.Fragment.ListFragment;
import com.android.jh.mymusicplayer.util.Interfaces.ControlInterface;
import com.android.jh.mymusicplayer.util.Permission.PermissionControl;
import com.bumptech.glide.Glide;

import static com.android.jh.mymusicplayer.util.Control.Controller.ACTION;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_PAUSE;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_PLAY;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_STOP;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.datas;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.mMediaPlayer;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.position;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ControlInterface {

    private static final String TAG = "MAINACTIVITY" ;
    // 권한 요청 코드
    private final int REQ_PERMISSION = 100;
    DrawerLayout drawer;
    ImageView img_voiceToSearch,img_botton,img_bottom_pre,img_bottom_play,img_bottom_next,img_SearchBack;
    TextView text_playLIstName,text_bottom_title,text_bottom_artist,text_bottom_Time;
    SearchView searchView;
    ViewPager list_viewPager;
    TabLayout tabLayout;
    CardView list_cardView;
    Toolbar list_toolbar;
    Controller controller;

    @Override
    protected void onDestroy() {
        controller.deleteObservers(this);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = Controller.getInstance();
        controller.addObservers(this);
        Log.i(TAG,"========================="+controller.observers.size());
        layoutInit();
        checkPermission();
    }


    private void checkPermission() {
        //버전 체크해서 마시멜로우(6.0)보다 낮으면 런타임 권한 체크를 하지않는다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PermissionControl.checkPermssion(this, REQ_PERMISSION)) {
                init();
            }
        } else {
            init();
        }
    }

    //권한체크 후 콜백< 사용자가 확인후 시스템이 호출하는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION) {
            //배열에 넘긴 런타임 권한을 체크해서 승인이 됐으면
            if (PermissionControl.onCheckedResult(grantResults)) {
                init();
            } else {
                Toast.makeText(this, "권한을 사용하지 않으시면 프로그램을 실행시킬수 없습니다", Toast.LENGTH_SHORT).show();
                finish();
                // 선택 1.종료, 2. 권한체크 다시물어보기
                //PermissionControl.checkPermssion(this,REQ_PERMISSION);
            }
        }
    }

    private void init() {
        img_bottom_pre.setOnClickListener(this);
        img_bottom_next.setOnClickListener(this);
        img_bottom_play.setOnClickListener(this);
        list_cardView.setOnClickListener(this);
        searchView.setOnQueryTextListener(queryTextListener);
        text_playLIstName.setText("All");
        text_playLIstName.setOnClickListener(this);
    }

    SearchView.OnQueryTextListener queryTextListener =new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_SearchBack :
                break;
            case R.id.btn_bottom_play :
                playerActionCheck();
                break;
            case R.id.btn_bottom_next :
                controller.next();
                break;
            case R.id.btn_bottom_pre :
                controller.pre();
                break;
            case R.id.list_playControl_cardview :
                break;
        }
    }

    private void playerActionCheck() {
        switch (ACTION) {
            case ACTION_STOP :
            case ACTION_PAUSE :
                controller.play();
                break;
            case ACTION_PLAY :
                controller.pause();
                break;
        }
    }

    private void toolbarInit() {
        list_toolbar = (Toolbar) findViewById(R.id.list_toolbar);
        setSupportActionBar(list_toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, list_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void naviInit() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    private void tabBarInit() {
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Title"));
        tabLayout.addTab(tabLayout.newTab().setText("Artist"));
        tabLayout.addTab(tabLayout.newTab().setText("Album"));
    }

    private void layoutInit() {
        img_voiceToSearch = (ImageView)findViewById(R.id.img_voiceToSearch);
        img_bottom_next = (ImageView) findViewById(R.id.btn_bottom_next);
        img_bottom_play = (ImageView)findViewById(R.id.btn_bottom_play);
        img_bottom_pre = (ImageView) findViewById(R.id.btn_bottom_pre);
        img_botton = (ImageView) findViewById(R.id.img_bottom);
        text_bottom_artist = (TextView) findViewById(R.id.text_bottom_artist);
        text_bottom_Time = (TextView) findViewById(R.id.text_bottom_musictime);
        text_playLIstName = (TextView) findViewById(R.id.text_playerName);
        text_bottom_title = (TextView) findViewById(R.id.text_bottom_title);
        list_viewPager = (ViewPager) findViewById(R.id.viewPager_list);
        list_cardView = (CardView) findViewById(R.id.list_playControl_cardview);
        img_SearchBack = (ImageView) findViewById(R.id.img_SearchBack);
        searchView = (SearchView) findViewById(R.id.SearchView);
        toolbarInit();
        naviInit();
        tabBarInit();
        bottomPlayerInit();
        // fragment pager 작성
        list_viewPager = (ViewPager)findViewById(R.id.viewPager_list);
        // adapter 생성
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(ListFragment.newInstance(1, ListFragment.TYPE_SONG));
        adapter.addFragment(ListFragment.newInstance(2, ListFragment.TYPE_ARTIST));
        adapter.addFragment(ListFragment.newInstance(3, ListFragment.TYPE_ARTIST));
        list_viewPager.setAdapter(adapter);
        // 페이저 리스너 :  페이저가 변경 되었을대 탭을 바꿔주는 리스너
        list_viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // 탭 리스너 :  탭이 변경 되었을대 페이지를 바꿔주는 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(list_viewPager));
    }

    @Override
    protected void onResume() {
        super.onResume();
        bottomPlayerInit();
    }

    public void bottomPlayerInit() {
        if(mMediaPlayer == null && position == -1)
            list_cardView.setVisibility(View.GONE);
        else {
            list_cardView.setVisibility(View.VISIBLE);
            Music music= datas.get(position);
            Glide.with(this)
                    .load(music.getImageUri())
                    .placeholder(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(img_botton);
            text_bottom_title.setText(music.getTitle());
            text_bottom_Time.setText(music.getDurationText());
            text_bottom_artist.setText(music.getArtist());
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void playPlayer() {
        ACTION = ACTION_PLAY;
        img_bottom_play.setImageResource(android.R.drawable.ic_media_pause);
    }

    @Override
    public void pausePlayer() {
        ACTION = ACTION_PAUSE;
        img_bottom_play.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void prePlayer() {
        bottomPlayerInit();
    }

    @Override
    public void nextPlayer() {
        bottomPlayerInit();
    }
}
