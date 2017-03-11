package com.android.jh.mymusicplayer.util.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.jh.mymusicplayer.Data.Domain.Album;
import com.android.jh.mymusicplayer.Data.Domain.Common;
import com.android.jh.mymusicplayer.Data.Domain.Music;
import com.android.jh.mymusicplayer.PlayerActivity;
import com.android.jh.mymusicplayer.R;
import com.android.jh.mymusicplayer.util.Services.PlayerService;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ExpandAdapter extends BaseExpandableListAdapter {

    private List<?> datas;
    private LayoutInflater inflater;
    private List<Music> inDatas;
    private String flag;
    private int item_layout_id;
    private Context context;

    public ExpandAdapter(Context context, List<?> datas) {
        this.context = context;
        this.datas = datas;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inDatas = new ArrayList<>();
        item_layout_id = R.layout.album_list;
    }

    @Override
    public int getGroupCount() {
        return datas.size() ;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((Common)datas.get(groupPosition)).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return datas.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((Common)datas.get(groupPosition)).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Album album = (Album) datas.get(groupPosition);

        GroupHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.album_list,parent,false);
            holder = new GroupHolder();

            holder.img = (ImageView) convertView.findViewById(R.id.img_grouplist);
            holder.text_grouptitle = (TextView) convertView.findViewById(R.id.text_grouplist_title);
            holder.text_groupyear = (TextView) convertView.findViewById(R.id.text_grouplist_year);
            holder.text_groupnum = (TextView) convertView.findViewById(R.id.text_grouplist_num);
            convertView.setTag(holder);
        } else
            holder =(GroupHolder) convertView.getTag();

        Glide.with(context).load(album.getImageUri())
                .placeholder(R.mipmap.ic_launcher)
                .placeholder(android.R.drawable.ic_menu_close_clear_cancel)
                .into(holder.img);
        holder.text_grouptitle.setText(album.getTitle());
        if(album.getTitle().length() > 8) {
            holder.text_grouptitle.setSingleLine();
            holder.text_grouptitle.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            holder.text_grouptitle.setSelected(true);
        }
        holder.text_groupnum.setText("Tracks : " + album.getNumberofsong());
        if(album.getYaer() == null){
            holder.text_groupyear.setText("");
        } else {
            holder.text_groupyear.setText(album.getYaer());
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final List<Music> childDatas = ((Common)datas.get(groupPosition)).getList();
        Music music = childDatas.get(childPosition);
        ChildHolder holder;

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.album_list_child,parent,false);
            holder = new ChildHolder();
            holder.child_layout = (ConstraintLayout) convertView.findViewById(R.id.child_layout);
            holder.text_childtitle = (TextView) convertView.findViewById(R.id.child_title);
            holder.text_childartist = (TextView) convertView.findViewById(R.id.child_artist);
            holder.text_childtime = (TextView) convertView.findViewById(R.id.child_time);
            convertView.setTag(holder);
        } else
            holder =(ChildHolder) convertView.getTag();

        holder.text_childtitle.setText(music.getTitle());
        holder.text_childtitle.setSingleLine();
        holder.text_childartist.setText(music.getArtist());
        holder.text_childartist.setSingleLine();
        holder.text_childtime.setText(music.getDurationText());
        holder.child_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PlayerActivity.class);
                PlayerService.position = PlayerService.getDatas().indexOf(childDatas.get(childPosition));
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupHolder {
        TextView text_grouptitle, text_groupyear,text_groupnum;
        ImageView img;
    }

    class ChildHolder {
        ConstraintLayout child_layout;
        TextView text_childtitle, text_childartist, text_childtime;
    }
}
