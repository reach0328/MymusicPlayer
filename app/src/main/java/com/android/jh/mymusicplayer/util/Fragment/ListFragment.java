package com.android.jh.mymusicplayer.util.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.jh.mymusicplayer.Data.Loader.DataLoader;
import com.android.jh.mymusicplayer.R;
import com.android.jh.mymusicplayer.util.Adapter.ListAdapter;

import java.util.List;

public class ListFragment extends Fragment {
    Context context;
    View view;
    private static final String ARG_COLUMN_COUNT = "column-count";
    public static final String ARG_LIST_TYPE = "list-type";
    public static final String ARG_POSITION = "position";

    public static final String TYPE_SONG = "SONG";
    public static final String TYPE_ARTIST = "ARTIST";
    public static final String TYPE_ALBUM = "ALBUM";
    public static final String TYPE_GENRE = "GENRE";

    private int mColumnCount = 1;
    private String mListType = "";

    private List<?> datas;

    public ListFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            Log.i("mColumnCount","=========================="+mColumnCount);
            mListType = getArguments().getString(ARG_LIST_TYPE);
            if(TYPE_SONG.equals(mListType))
                datas = DataLoader.getMusics(getContext());
            else if(TYPE_ARTIST.equals(mListType))
                datas = DataLoader.getArtist(getContext());
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
        view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = (RecyclerView) view;
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }
        recyclerView.setAdapter(new ListAdapter(context, datas, mListType));
        return view;
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
