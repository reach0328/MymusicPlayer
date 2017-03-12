package com.android.jh.mymusicplayer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.jh.mymusicplayer.Data.Domain.Music;
import com.android.jh.mymusicplayer.Data.Loader.DataLoader;
import com.android.jh.mymusicplayer.util.Adapter.FragmentAdapter;
import com.android.jh.mymusicplayer.util.Adapter.ListAdapter;
import com.android.jh.mymusicplayer.util.Control.Controller;
import com.android.jh.mymusicplayer.util.Fragment.ListFragment;
import com.android.jh.mymusicplayer.util.Interfaces.ControlInterface;
import com.android.jh.mymusicplayer.util.Permission.PermissionControl;
import com.android.jh.mymusicplayer.util.Services.PlayerService;
import com.bumptech.glide.Glide;

import java.sql.SQLException;
import java.util.Random;

import static com.android.jh.mymusicplayer.util.Control.Controller.ACTION;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_NEXT;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_PAUSE;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_PLAY;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_PREVIOUS;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_STARTSERVICE;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_STOP;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.getDatas;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.mMediaPlayer;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.position;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ControlInterface {

    private static final String TAG = "MAINACTIVITY" ;
    // 권한 요청 코드
    private final int REQ_PERMISSION = 100;
    DrawerLayout drawer;
    ImageView img_botton,img_bottom_pre,img_bottom_play,img_bottom_next,img_SearchBack;
    TextView text_playLIstName,text_bottom_title,text_bottom_artist,text_bottom_Time;
    SearchView searchView;
    ViewPager list_viewPager;
    TabLayout tabLayout;
    CardView list_cardView;
    Toolbar list_toolbar;
    Controller controller;
    FloatingActionButton fab;
    Dialog dialog;
    ListFragment myList;
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
        layoutInit();
        checkPermission();
        requestService(ACTION_STARTSERVICE);
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
            }
        }
    }

    private void requestService(String action) {
        Intent service = new Intent(this, PlayerService.class);
        service.setAction(action);
        service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(service);
    }

    private void init() {
        img_bottom_pre.setOnClickListener(this);
        img_bottom_next.setOnClickListener(this);
        img_bottom_play.setOnClickListener(this);
        list_cardView.setOnClickListener(this);
        searchView.setOnQueryTextListener(queryTextListener);
        text_playLIstName.setText("All");
        text_playLIstName.setOnClickListener(this);
        fab.setOnClickListener(this);
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
        Intent intent= new Intent(this,PlayerActivity.class);
        switch (view.getId()) {
            case R.id.fabButton :
                if(list_viewPager.getCurrentItem()==3){
                    makeAlert();
                } else {
                    Random random = new Random();
                    position = random.nextInt(getDatas().size());
                    startActivity(intent);
                }
                break;
            case R.id.btn_bottom_play :
                playerActionCheck();
                break;
            case R.id.btn_bottom_next :
                requestService(ACTION_NEXT);
                controller.next();
                break;
            case R.id.btn_bottom_pre :
                requestService(ACTION_PREVIOUS);
                controller.pre();
                break;
            case R.id.list_playControl_cardview :
                intent = new Intent(this,PlayerActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void playerActionCheck() {
        switch (ACTION) {
            case ACTION_STOP :
            case ACTION_PLAY :
                requestService(ACTION_PLAY);
                controller.play();
                break;
            case ACTION_PAUSE :
                requestService(ACTION_PAUSE);
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


    //TODO header부분을 줄여서 표현하면서 스크롤링 까지 되도록 그리고 나만의 리스트 만들기
    private void neviInit() {
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
        tabLayout.addTab(tabLayout.newTab().setText("MyList"));
    }

    private void layoutInit() {
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
        fab = (FloatingActionButton) findViewById(R.id.fabButton);
        toolbarInit();
        neviInit();
        tabBarInit();
        bottomPlayerInit();
        // fragment pager 작성
        list_viewPager = (ViewPager)findViewById(R.id.viewPager_list);
        // adapter 생성
        FragmentAdapter adapter = new FragmentAdapter(getSupportFragmentManager());
        adapter.addFragment(ListFragment.newInstance(1, ListFragment.TYPE_SONG));
        adapter.addFragment(ListFragment.newInstance(2, ListFragment.TYPE_ARTIST));
        adapter.addFragment(ListFragment.newInstance(1, ListFragment.TYPE_ALBUM));
        myList = ListFragment.newInstance(1, ListFragment.TYPE_MYLIST);
        adapter.addFragment(myList);
        list_viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==3){
                    fab.setImageResource(R.drawable.plus);
                } else {
                    fab.setImageResource(R.drawable.shuffle);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        list_viewPager.setAdapter(adapter);
        // 페이저 리스너 :  페이저가 변경 되었을대 탭을 바꿔주는 리스너
        list_viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        // 탭 리스너 :  탭이 변경 되었을대 페이지를 바꿔주는 리스너
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(list_viewPager));
    }

    public void bottomPlayerInit() {
        if(mMediaPlayer == null )
            list_cardView.setVisibility(View.GONE);
        else {
            list_cardView.setVisibility(View.VISIBLE);
            Music music = PlayerService.getDatas().get(position);
            Glide.with(this)
                    .load(music.getImageUri())
                    .placeholder(android.R.drawable.ic_menu_close_clear_cancel)
                    .into(img_botton);
            text_bottom_title.setText(music.getTitle());
            if(music.getTitle().length()>8) {
                text_bottom_title.setSingleLine(true);
                text_bottom_title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                text_bottom_title.setSelected(true);
            }
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
        bottomPlayerInit();
        ACTION = ACTION_PAUSE;
        img_bottom_play.setImageResource(R.drawable.pause);
    }

    @Override
    public void pausePlayer() {
        ACTION = ACTION_PLAY;
        img_bottom_play.setImageResource(R.drawable.play);
    }

    @Override
    public void prePlayer() {
        bottomPlayerInit();
    }

    @Override
    public void nextPlayer() {
        bottomPlayerInit();
    }

    @Override
    public void startService() {
        img_bottom_play.setImageResource(R.drawable.pause);
    }

    public void makeAlert() {
        // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View alert_view = inflater.inflate(R.layout.add_alert, null);
        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        AlertDialog.Builder buider = new AlertDialog.Builder(this);
        Button ok = (Button) alert_view.findViewById(R.id.alert_save);
        Button cancle = (Button) alert_view.findViewById(R.id.alert_cancle);
        RecyclerView recyclerView = (RecyclerView) alert_view.findViewById(R.id.addalertrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new ListAdapter(this, getDatas(), ListFragment.TYPE_ADD_ALERT));
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Integer position : ListAdapter.selected){
                    try {
                        if(!DataLoader.getMyListDatas(getApplicationContext()).contains(getDatas().get(position)))
                            DataLoader.saveToMyList(getApplicationContext(),getDatas().get(position));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                myList.notifyrecycler();
                dialog.dismiss();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        buider.setView(alert_view);
        dialog = buider.create();
        dialog.show();
    }

}
