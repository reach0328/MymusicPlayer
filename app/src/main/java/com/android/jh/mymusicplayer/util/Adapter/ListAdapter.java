package com.android.jh.mymusicplayer.util.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jh.mymusicplayer.Data.Domain.Common;
import com.android.jh.mymusicplayer.PlayerActivity;
import com.android.jh.mymusicplayer.R;
import com.android.jh.mymusicplayer.util.Fragment.ListFragment;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.Holder>{

    private List<?> datas;
    private String flag;
    private int item_layout_id;
    private Context context;

    public ListAdapter(Context context, List<?> datas, String flag) {
            this.context = context;
            this.datas = datas;
            this.flag = flag;
            switch(flag){
                case ListFragment.TYPE_SONG:
                    item_layout_id = R.layout.title_list;
                    break;
                case ListFragment.TYPE_ALBUM:
                case ListFragment.TYPE_GENRE:
                case ListFragment.TYPE_ARTIST:
                    item_layout_id = R.layout.aritist_list;
                    break;
            }
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(item_layout_id,parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Common common = (Common) datas.get(position);
        holder.txtTitle.setText(common.getTitle());
        if(common.getTitle().length() > 8) {
            holder.txtTitle.setSingleLine();
            holder.txtTitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.txtTitle.setSelected(true);
        }
        holder.txtArtist.setText(common.getArtist());
        holder.position = position;
        Glide.with(context)              // 0. 글라이드 사용
                .load(common.getImageUri()) // 1. 로드할 대상 Uri
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.image);     // 2. 입력될 이미지뷰
        switch(flag){
            case ListFragment.TYPE_SONG:
                holder.txtTime.setText(common.getDurationText());
                break;
            case ListFragment.TYPE_ALBUM:
            case ListFragment.TYPE_GENRE:
            case ListFragment.TYPE_ARTIST:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView txtTitle, txtArtist,txtTime;
        ImageView image;
        int position;

        public Holder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.listitem_cardView);
            txtTitle = (TextView) itemView.findViewById(R.id.text_list_title);
            txtArtist = (TextView) itemView.findViewById(R.id.text_list_artist);
            image = (ImageView) itemView.findViewById(R.id.img_list);
            switch(flag){
                case ListFragment.TYPE_SONG:
                    txtTime = (TextView) itemView.findViewById(R.id.text_list_Time);
                    cardView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, PlayerActivity.class);
                            intent.putExtra(ListFragment.ARG_POSITION, position);
                            intent.putExtra(ListFragment.ARG_LIST_TYPE, flag);
                            context.startActivity(intent);
                        }
                    });
                    break;
                default :
                    // nothing
                    break;
            }
        }
    }

}

