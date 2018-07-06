package com.aranteknoloji.aranmusic;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CustomCursorAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Cursor mData;
    private Activity activity;

    public CustomCursorAdapter(Activity activity) {
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.cursor_adapter_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        //getting indexes
//        int index_title = mData.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
//
//        //getting actual data
//        mData.moveToPosition(position);
//        String song_title = mData.getString(index_title);
//
//        //setting data
//        MyViewHolder viewHolder = (MyViewHolder) holder;
//        viewHolder.songName.setText(song_title);
        PlayerTasksHelper.currentPosition = position;
        MyViewHolder viewHolder = (MyViewHolder) holder;
        viewHolder.songName.setText(PlayerTasksHelper.getSongTitle());
    }

    @Override
    public int getItemCount() {
        if (mData == null) return 0;
        return mData.getCount();
    }

    public void swapData(Cursor c) {
        this.mData = c;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView songName;

        private MyViewHolder(View itemView) {
            super(itemView);
            songName = itemView.findViewById(R.id.text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    int index_path = mData.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
//                    mData.moveToPosition(getAdapterPosition());
//                    String path = mData.getString(index_path);

                    PlayerTasksHelper.currentPosition = getAdapterPosition();
                    Intent startServiceIntent = new Intent(activity, MyService.class);
//                    startServiceIntent.putExtra("path", path);
                    startServiceIntent.setAction(PlayerTasksHelper.PlayerUtils.ACTION_PLAYER_START);
                    activity.startService(startServiceIntent);
                }
            });
        }
    }
}
