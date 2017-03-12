package com.android.jh.mymusicplayer.util.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jh.mymusicplayer.Data.Domain.Artist;
import com.android.jh.mymusicplayer.Data.Domain.Common;
import com.android.jh.mymusicplayer.Data.Domain.Music;
import com.android.jh.mymusicplayer.Data.Loader.DataLoader;
import com.android.jh.mymusicplayer.PlayerActivity;
import com.android.jh.mymusicplayer.R;
import com.android.jh.mymusicplayer.util.Fragment.ListFragment;
import com.android.jh.mymusicplayer.util.Services.PlayerService;
import com.bumptech.glide.Glide;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder>{

    private List<?> datas;
    private String flag;
    private int item_layout_id;
    private Context context;
    public static List<Integer> selected;

    public ListAdapter(Context context, List<?> datas, String flag) {
            this.context = context;
            this.datas = datas;
            this.flag = flag;
            switch(flag){
                case ListFragment.TYPE_MYLIST:
                case ListFragment.TYPE_ARTIST_ALERT:
                case ListFragment.TYPE_SONG:
                    item_layout_id = R.layout.title_list;
                    break;
                case ListFragment.TYPE_ARTIST:
                    item_layout_id = R.layout.aritist_list;
                    break;
                case ListFragment.TYPE_ADD_ALERT:
                    selected = new ArrayList<>();
                    item_layout_id = R.layout.add_list;
                    break;
            }
    }

    public void setDatas(List<?> datas) {
        this.datas = datas;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(item_layout_id,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final Holder holder, final int position) {
        Common common = (Common) datas.get(position);
        String title = common.getTitle();
        String artist = common.getArtist();
        String duration = common.getDurationText();
        Uri imgUri = common.getImageUri();
        holder.txtTitle.setText(title);
        if(title.length() > 8) {
            holder.txtTitle.setSingleLine();
            holder.txtTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.txtTitle.setSelected(true);
        }
        holder.txtArtist.setText(artist);
        holder.txtTitle.setSingleLine();
        holder.position = position;
        switch(flag){
            case ListFragment.TYPE_ADD_ALERT:
                holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked && !selected.contains(position))
                            selected.add(position);
                    }
                });
            case ListFragment.TYPE_MYLIST:
            case ListFragment.TYPE_ARTIST_ALERT:
            case ListFragment.TYPE_SONG:
                holder.txtTime.setText(duration);
                Glide.with(context)              // 0. 글라이드 사용
                        .load(imgUri) // 1. 로드할 대상 Uri
                        .bitmapTransform(new CropCircleTransformation(context))
                        .into(holder.image);     // 2. 입력될 이미지뷰
                break;
            case ListFragment.TYPE_ARTIST:
                Glide.with(context)              // 0. 글라이드 사용
                        .load(imgUri) // 1. 로드할 대상 Uri
                        .into(holder.image);     // 2. 입력될 이미지뷰
                break;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle,txtArtist,txtTime;
        ImageView image;
        CheckBox checkBox;
        int position;

        public Holder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.listitem_cardView);
            txtTitle = (TextView) itemView.findViewById(R.id.text_list_title);
            txtArtist = (TextView) itemView.findViewById(R.id.text_list_artist);
            image = (ImageView) itemView.findViewById(R.id.img_list);
            switch(flag){
                case ListFragment.TYPE_MYLIST:
                    cardView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(context);
                            // 팝업창 제목
                            alertDialog.setTitle("삭제 하시겠습니까?");
                            // YES 버튼 기능
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        DataLoader.deleteToMyList(context,position);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            // NO 버튼 기능
                            alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                            alertDialog.show();
                            return true;
                        }
                    });
                case ListFragment.TYPE_ARTIST_ALERT:
                case ListFragment.TYPE_SONG:
                    txtTime = (TextView) itemView.findViewById(R.id.text_list_Time);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, PlayerActivity.class);
                            if(flag==ListFragment.TYPE_SONG)
                                PlayerService.position = position;
                            else {
                                Music temp = (Music) datas.get(position);
                                for(int i =0; i <PlayerService.getDatas().size();i++) {
                                    if(PlayerService.getDatas().get(i).music_uri_string.equals(temp.music_uri_string))
                                        PlayerService.position = i;
                                }
                            }
                            context.startActivity(intent);
                        }
                    });
                    break;
                case ListFragment.TYPE_ARTIST:
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Artist artist = (Artist) datas.get(position);
                            if(artist.musics != null)
                                makeAlert(artist);
                        }
                    });
                    break;
                case ListFragment.TYPE_ADD_ALERT:
                    this.setIsRecyclable(false);
                    txtTime = (TextView) itemView.findViewById(R.id.text_list_Time);
                    checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
                    break;
            }
        }
    }

    public void makeAlert(Artist artist) {
        // LayoutInflater를 통해 위의 custom layout을 AlertDialog에 반영. 이 외에는 거의 동일하다.
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        View alert_view = inflater.inflate(R.layout.artist_alertlist, null);
        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        AlertDialog.Builder buider = new AlertDialog.Builder(context); //AlertDialog.Builder 객체 생성
        ImageView img = (ImageView) alert_view.findViewById(R.id.img_listalert);
        TextView txt = (TextView) alert_view.findViewById(R.id.tv_listalert);
        RecyclerView recyclerView = (RecyclerView) alert_view.findViewById(R.id.alert_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(new ListAdapter(context, artist.musics, ListFragment.TYPE_ARTIST_ALERT));
        txt.setText(artist.getTitle());
        Glide.with(context)              // 0. 글라이드 사용
                .load(artist.getImageUri()) // 1. 로드할 대상 Uri
                .into(img);     // 2. 입력될 이미지뷰
        buider.setView(alert_view);
        Dialog dialog = buider.create();
        dialog.show();
    }
}