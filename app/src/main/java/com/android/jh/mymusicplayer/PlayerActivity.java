package com.android.jh.mymusicplayer;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.jh.mymusicplayer.Data.Domain.Music;
import com.android.jh.mymusicplayer.Data.Loader.DataLoader;
import com.android.jh.mymusicplayer.util.Adapter.PlayerAdapter;
import com.android.jh.mymusicplayer.util.Fragment.ListFragment;
import com.android.jh.mymusicplayer.util.Interfaces.ControlInterface;
import com.android.jh.mymusicplayer.util.Control.Controller;
import com.android.jh.mymusicplayer.util.Services.PlayerService;

import java.util.List;

import static com.android.jh.mymusicplayer.util.Control.Controller.ACTION;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_PAUSE;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_PLAY;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.ACTION_STOP;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.listType;
import static com.android.jh.mymusicplayer.util.Services.PlayerService.position;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener, ControlInterface {

    private static final String TAG = "PLAYACTIVITY" ;

    TextView player_text_title, player_text_artist;
    ViewPager viewPager;
    SeekBar volum_seekBar;
    ImageView img_player_back, img_player_play, img_player_pre,img_player_next, img_player_whole, img_player_suffle;
    List<Music> datas;
    PlayerAdapter playerAdapter;
    Controller controller;
    private int player_position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        controller = Controller.getInstance();
        controller.addObservers(this);
        layoutInit();
        returnPage();
    }

    private void returnPage() {
        // 특정 페이지 호출
        Intent intent = getIntent();
        if(intent != null){
            Bundle bundle = intent.getExtras();
            listType = bundle.getString( ListFragment.ARG_LIST_TYPE );
            position = bundle.getInt( ListFragment.ARG_POSITION );
            //실제 페이지 값 게산 처리
            //페이지 이동
            viewPager.setCurrentItem(position);
            // 첫페이지 일 경우 만 init 호출
            // 이유 : 첫페이지가 아닐경우 위에 setCurrentItem 에 의해서 ViewPagerdml onPageSelected가 호출된다.
            if(position == 0) {
                init();
            } else {
                viewPager.setCurrentItem(position);
            }
        }
    }
    private void play() {
        Intent intent = new Intent(this, PlayerService.class);
        intent.setAction(PlayerService.ACTION_PLAY);
        intent.putExtra(ListFragment.ARG_POSITION, position);
        intent.putExtra(ListFragment.ARG_LIST_TYPE,listType);
        startService(intent);
    }

    private void controllerInit() {
        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int nMax = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int nCurrentVolumn = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volum_seekBar.setMax(nMax);
        volum_seekBar.setProgress(nCurrentVolumn);
        volum_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            }
        });
    }

//    class TimerThread extends Thread {
//        // sub thread 를 생성해서 mediaplayer 의 현재 포지션 값으로 seekbar 를 변경해준다. 매 1초마다
//        // sub thread 에서 동작할 로직 정의
//        @Override
//        public void run() {
//            while (playStatus < STOP) {
//                if(player != null) {
//                    // 이 부분은 메인쓰레드에서 동작하도록 Runnable 객체를 메인쓰레드에 던져준다
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                seekBar.setProgress(player.getCurrentPosition());
//                                txtCurrent.setText(convertMiliToTime(player.getCurrentPosition()) + "");
//                            } catch (Exception e){ }
//                        }
//                    });
//                }
//                try { Thread.sleep(1000); } catch (InterruptedException e) {}
//            }
//        }
//    }

    private void prev() {
        if(position > 0)
            viewPager.setCurrentItem(position-1);
    }

    private void next() {
        if(position < datas.size())
            viewPager.setCurrentItem(position+1);
    }

    @Override
    protected void onDestroy() {
        controller.deleteObservers(this);
        super.onDestroy();
    }

    //viewpage 체인지 리스너
    ViewPager.OnPageChangeListener viewPagerListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }
        @Override
        public void onPageSelected(int position) {
            PlayerService.position = position;
            init();
        }
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void init() {
        player_position = position;
        if(datas.get(player_position).getTitle().length() > 8) {
            String title = String.format("%10S", datas.get(player_position).getTitle()+"..");
            player_text_title.setText(title);
        } else {
            player_text_title.setText(datas.get(player_position).getTitle());
        }
        player_text_artist.setText(datas.get(player_position).getArtist());

        // 뷰페이저로 이동할 경우 플레이어 세팅된 값을 해제한 후 로직을 실행한다
        img_player_play.setImageResource(android.R.drawable.ic_media_play);
        // 컨트롤러 세팅
        controllerInit();
        play();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_player_back:
                backToPage();
                break;
            case R.id.btn_player_next :
                next();
                break;
            case R.id.btn_player_pre :
                prev();
                break;
            case R.id.btn_player_play :
                playActioncheck();
                break;
            case R.id.btn_player_whole :
                break;
            case R.id.btn_player_suffle :
                break;
        }
    }

    private void playActioncheck() {
        switch (ACTION) {
            case ACTION_PAUSE :
            case ACTION_STOP :
                controller.play();
                break;
            case ACTION_PLAY :
                controller.pause();
                break;
        }
    }

    private void backToPage() {
        onBackPressed();
    }

    private void layoutInit() {
        player_text_title = (TextView) findViewById(R.id.player_text_title);
        player_text_artist = (TextView) findViewById(R.id.player_text_artist);
        viewPager = (ViewPager) findViewById(R.id.player_viewpager);
        volum_seekBar = (SeekBar) findViewById(R.id.seekbar_volum);
        img_player_back = (ImageView) findViewById(R.id.btn_player_back);
        img_player_next = (ImageView) findViewById(R.id.btn_player_next);
        img_player_play = (ImageView) findViewById(R.id.btn_player_play);
        img_player_whole = (ImageView) findViewById(R.id.btn_player_whole);
        img_player_suffle = (ImageView) findViewById(R.id.btn_player_suffle);
        img_player_pre = (ImageView) findViewById(R.id.btn_player_pre);
        // 0. 데이터 가져오기
        datas = DataLoader.getMusics(this);
        // 2. 뷰페이저용 아답터 생성
        playerAdapter = new PlayerAdapter(datas ,this);
        // 3. 뷰페이저 아답터 연결
        viewPager.setAdapter( playerAdapter );
        // 4. 뷰 페이저 리스너 연결
        viewPager.addOnPageChangeListener(viewPagerListener);
        // 4-1. 페이지 트렌스 포머 연결
        viewPager.setPageTransformer(false,pageTransformer);
        viewPager.setPageMargin(100);
        img_player_pre.setOnClickListener(this);
        img_player_next.setOnClickListener(this);
        img_player_suffle.setOnClickListener(this);
        img_player_pre.setOnClickListener(this);
        img_player_back.setOnClickListener(this);
        img_player_play.setOnClickListener(this);
    }

    // 페이지 트렌스포머
    ViewPager.PageTransformer pageTransformer = new ViewPager.PageTransformer() {
        private float MIN_TRAN = 100f;
        private int MIN_ROTATE = 100;
        @Override
        public void transformPage(View page, float position) {
            if (position <= 1) {
                float rotateFactor = MIN_ROTATE * position;
                float tranFactor = MIN_TRAN * position;
                if (position < 0) {
                    page.setTranslationY(-tranFactor);
                } else {
                    page.setTranslationY(tranFactor);
                }
                page.setRotation(rotateFactor);
            }
        }
    };


    @Override
    public void playPlayer() {
        ACTION = ACTION_PLAY;
        img_player_play.setImageResource(android.R.drawable.ic_media_pause);
    }

    @Override
    public void pausePlayer() {
        ACTION = ACTION_PAUSE;
        img_player_play.setImageResource(android.R.drawable.ic_media_play);
    }

    @Override
    public void prePlayer() {

    }

    @Override
    public void nextPlayer() {

    }
}
