package com.aranteknoloji.aranmusic;

import android.database.Cursor;
import android.provider.MediaStore;

public class PlayerTasksHelper {

    private static Cursor cursorData;
    public static int currentPosition = -1;

    public class PlayerUtils {
        public static final String ACTION_PLAYER_START = "action.start";
        public static final String ACTION_PLAYER_STOP = "action.stop";
        public static final String ACTION_PLAYER_PAUSE = "action.pause";
        public static final String ACTION_PLAYER_FORWARD = "action.forward";
        public static final String ACTION_PLAYER_BACKWARD = "action.backward";
    }

    public static void setCursorData(Cursor cursor){
        cursorData = cursor;
    }

    public static Cursor getCursorData() {
        return cursorData;
    }

    public static String getSongTitle(){
        if (currentPosition != -1){
            int titleIndex = cursorData.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
            cursorData.moveToPosition(currentPosition);
            return cursorData.getString(titleIndex);
        }
        return "there is no song";
    }

    public static String getSongPath(){
        if (currentPosition != -1){
            int pathIndex = cursorData.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            cursorData.moveToPosition(currentPosition);
            return cursorData.getString(pathIndex);
        }
        return "";
    }
}
