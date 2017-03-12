package com.android.jh.mymusicplayer.util.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.android.jh.mymusicplayer.Data.Loader.DataLoader;
import com.android.jh.mymusicplayer.R;
import com.android.jh.mymusicplayer.util.Adapter.ExpandAdapter;
import com.android.jh.mymusicplayer.util.Adapter.ListAdapter;

import java.sql.SQLException;
import java.util.List;

public class ListFragment extends Fragment {
    Context context;
    View view;
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_LIST_TYPE = "list-type";

    public static final String TYPE_SONG = "SONG";
    public static final String TYPE_ARTIST = "ARTIST";
    public static final String TYPE_ALBUM = "ALBUM";
    public static final String TYPE_ARTIST_ALERT = "ALERT";
    public static final String TYPE_ADD_ALERT = "ADD";
    public static final String TYPE_MYLIST = "MYLIST";

    private int mColumnCount = 1;
    private String mListType = "";
    RecyclerView recyclerView;
    ListAdapter adapter;
    private List<?> datas;

    public ListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            mListType = getArguments().getString(ARG_LIST_TYPE);
            if(TYPE_SONG.equals(mListType))
                datas = DataLoader.getMusics(getContext());
            else if(TYPE_ARTIST.equals(mListType))
                datas = DataLoader.getArtist(getContext());
            else if(TYPE_ALBUM.equals(mListType))
                datas = DataLoader.getAlbum(getContext());
            else if(TYPE_MYLIST.equals(mListType)) {
                try {
                    datas = DataLoader.getMyListDatas(getContext());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    public static ListFragment newInstance(int columnCount, String listType) {
        Bundle args = new Bundle();
        ListFragment fragment = new ListFragment();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        args.putString(ARG_LIST_TYPE, listType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(TYPE_ALBUM.equals(mListType)) {
            view = inflater.inflate(R.layout.expand_list,container,false);
            ExpandableListView expandableListView = (ExpandableListView) view;
            expandableListView.setAdapter(new ExpandAdapter(context,datas));
        }
        else {
            view = inflater.inflate(R.layout.fragment_list, container, false);
            recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            adapter = new ListAdapter(context,datas,mListType);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }

    public void notifyrecycler(){
        try {
            adapter.setDatas(DataLoader.getMyListDatas(context));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
