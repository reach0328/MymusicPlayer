package com.android.jh.mymusicplayer.util.Services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.Rating;
import android.media.session.MediaController;
import android.media.session.MediaSession;
import android.media.session.MediaSessionManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;

import com.android.jh.mymusicplayer.Data.Domain.Music;
import com.android.jh.mymusicplayer.Data.Loader.DataLoader;
import com.android.jh.mymusicplayer.R;
import com.android.jh.mymusicplayer.util.Control.Controller;
import com.android.jh.mymusicplayer.util.UtilSharedPreferences;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static android.app.PendingIntent.getService;

public class PlayerService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private static final String TAG_SERVICES = "SERVICES";
    private static final String TAG_NOTI = "NOTIFICATION";
    public static final String ACTION_PLAY = "action_play";
    public static final String ACTION_PAUSE = "action_pause";
    public static final String ACTION_NEXT = "action_next";
    public static final String ACTION_PREVIOUS = "action_previous";
    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_PAGE = "action_page";

    public static boolean isPlaying = false;

    // 1. 미디어플레이어 사용 API 세팅
    public static MediaPlayer mMediaPlayer = null;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;
    public static String listType = "";
    public static int position = -1;

    private static List<Music> datas = new ArrayList<>();
    private Controller controller;

    public static List<Music> getDatas() {
        return datas;
    }

    @Override
    public void onCreate() {
        position = UtilSharedPreferences.loadInt(this,"position",0);
        initMedia();
        initMediaSessions();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleAction(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        UtilSharedPreferences.saveInt(this,"position",position);
//        super.onDestroy();
    }

    private void initMedia() {
        if (datas.size() < 1) {
            controller = Controller.getInstance();
            datas = DataLoader.getMusics(getBaseContext());
        }
        // 음원 uri
        Uri musicUri = datas.get(position).music_uri;
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.reset();
        }
        // 플레이어에 음원 세팅
        mMediaPlayer = MediaPlayer.create(this, musicUri);
        mMediaPlayer.setLooping(false); // 반복여부
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //TODO 완료시 호출
            }
        });
    }

    // 2. 명령어 실행
    private void handleAction( Intent intent ) {
        if( intent == null || intent.getAction() == null )
            return;
        String action = intent.getAction();
        if(action.equalsIgnoreCase(ACTION_PAGE)) {
            initMedia();
        } else if( action.equalsIgnoreCase( ACTION_PLAY ) ) {
            playerStart();
        } else if( action.equalsIgnoreCase( ACTION_PAUSE ) ) {
            playerPause();
        } else if( action.equalsIgnoreCase( ACTION_PREVIOUS ) ) {
            playerPre();
        } else if( action.equalsIgnoreCase( ACTION_NEXT ) ) {
            playerNext();
        } else if(action.equalsIgnoreCase( ACTION_STOP )) {
            playerStop();
        }
    }

    public void playerNext() {
        mController.getTransportControls().skipToNext();
    }

    public void playerPre() {
        mController.getTransportControls().skipToPrevious();
    }

    private void playerPause() {
        mController.getTransportControls().pause();
    }

    private NotificationCompat.Action generateAction(int icon, String title, String intentAction ) {
        Intent intent = new Intent( getApplicationContext(), PlayerService.class );
        intent.setAction( intentAction );
        PendingIntent pendingIntent = PendingIntent.getService(getApplicationContext(), 1, intent, 0);
        return new NotificationCompat.Action.Builder(icon, title, pendingIntent).build();
    }

    private void buildNotification(NotificationCompat.Action action , String action_flag ) {
        Music music = datas.get(position);
        NotificationCompat.MediaStyle style = new NotificationCompat.MediaStyle();
        // Stop intent
        Intent intentStop = new Intent( getApplicationContext(), PlayerService.class );
        intentStop.setAction( ACTION_STOP );
        PendingIntent stopIntent = getService(getApplicationContext(), 1, intentStop, 0);

        // 노티바 생성
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this );

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle( music.getTitle() )
                .setContentText( music.getArtist() )
                .setStyle(style);

        // 퍼즈일 경우만 노티 삭제 가능
        if(ACTION_PAUSE.equals(action_flag)) {
            //panding intent를 통하여 stop
            builder.setDeleteIntent(stopIntent);
            builder.setOngoing(false);
        }
        builder.setSmallIcon(R.drawable.ic_launcher);
        Bitmap bitmap=null;
        try {
            bitmap =BitmapFactory.decodeStream(getContentResolver().openInputStream(music.getImageUri()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if(bitmap!=null)
            builder.setLargeIcon(bitmap);
        builder.addAction(generateAction(android.R.drawable.ic_media_previous, "Prev", ACTION_PREVIOUS));
        builder.addAction(action);
        builder.addAction(generateAction(android.R.drawable.ic_media_next, "Next", ACTION_NEXT));
        style.setShowActionsInCompactView(0,1,2);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 노티바를 화면에 보여준다
        notificationManager.notify(NOTIFICATION_ID , builder.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void playerStart(){
        mController.getTransportControls().play();
    }

    private void playerStop(){
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel( NOTIFICATION_ID );
        Intent intent = new Intent( getApplicationContext(), PlayerService.class );
        stopService( intent );
    }

    private void initMediaSessions() {
        mSession = new MediaSession(getApplicationContext(), "Media Player Session");
        mController =new MediaController(getApplicationContext(), mSession.getSessionToken());
        mSession.setCallback(new MediaSession.Callback(){
            @Override
            public void onPlay() {
                super.onPlay();
                playPlayer();
                controller.play();
            }

            @Override
            public void onPause() {
                super.onPause();
                pausePlayer();
                if(controller!=null)
                    controller.pause();
            }

            @Override
            public void onSkipToNext() {
                nextPlayer();
                controller.next();
            }

            @Override
            public void onSkipToPrevious() {
                prePlayer();
                controller.pre();
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
            }

            @Override
            public void onSetRating(Rating rating) {
                super.onSetRating(rating);
            }
        });
    }


    public void playPlayer() {
        buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ),ACTION_PAUSE );
        mMediaPlayer.start();
    }

    public void pausePlayer() {
        buildNotification(generateAction(android.R.drawable.ic_media_play, "Play", ACTION_PLAY),ACTION_PLAY);
        mMediaPlayer.pause();
    }

    public void nextPlayer() {
        if(position+1 <datas.size())
            position = position + 1;
        initMedia();
        mMediaPlayer.start();
        buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ),ACTION_PAUSE );
    }

    public void prePlayer() {
        if(position -1 > 0)
            position = position -1;
        buildNotification( generateAction( android.R.drawable.ic_media_pause, "Pause", ACTION_PAUSE ),ACTION_PAUSE );
        initMedia();
        mMediaPlayer.start();
    }
}
