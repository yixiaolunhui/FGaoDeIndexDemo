package com.zwl.mybehaviordemo.utils;

import android.content.Context;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.zwl.mybehaviordemo.adapter.StringAdapter;

import java.util.ArrayList;
import java.util.List;

public class RecyclerDataUtils {

    private static List<String> getListString(String tag, int size) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            list.add(new StringBuilder(tag).append(":").append(i).toString());
        }
        return list;
    }


    public static void setRecyclerAdater(Context context, RecyclerView recycler, String tag, int size) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recycler.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
        recycler.addItemDecoration(dividerItemDecoration);
        StringAdapter stringAdapter = new StringAdapter(RecyclerDataUtils.getListString(tag, size));
        recycler.setAdapter(stringAdapter);
    }
}
