package com.aranteknoloji.aranmusic;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PERMISSION_REQUEST_CODE = 55;
    private CustomCursorAdapter adapter;
//    private RecyclerView recyclerView;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int LOADER_TASK_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if (getIntent() != null) {
//            if (getIntent().getAction().equals("stop_service")){
//                Intent intent = new Intent(this, MyService.class);
//                intent.setAction("stop_service");
//                stopService(intent);
//            }
//        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting recyclerview
        RecyclerView recyclerView = findViewById(R.id.recyclerList);
        adapter = new CustomCursorAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        //checking the permissions for loading the data from storage
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getSupportLoaderManager().initLoader(LOADER_TASK_ID, null, this);
        } else shouldIAskPermission();
    }

    private void shouldIAskPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            doSomething4Rationale();
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    private void doSomething4Rationale() {
        Snackbar.make(findViewById(R.id.layout), "Needed Perms to see the songs", Snackbar.LENGTH_INDEFINITE)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                })
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getSupportLoaderManager().initLoader(LOADER_TASK_ID, null, this);
        } else doSomething4Rationale();

    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            private Cursor mData = null;

            @Override
            protected void onStartLoading() {
                if (mData != null) {
                    Log.d(TAG, "onStartLoading: data is not null");
                    deliverResult(mData);
                } else if (PlayerTasksHelper.getCursorData() != null) {
                    Log.d(TAG, "onStartLoading: [static] getCursorData is not null");
                    deliverResult(PlayerTasksHelper.getCursorData());
                } else {
                    Log.e(TAG, "onStartLoading: data is null forceLoad() is running");
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
                String[] projection  = {
                        MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST,
                        MediaStore.Audio.Media.DATA,
                        MediaStore.Audio.Media.DISPLAY_NAME,
                        MediaStore.Audio.Media.DURATION
                };
//                String sortOrder = MediaStore.Audio.AudioColumns.TITLE + " COLLATE LOCALIZED ASC";

                try {
                    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    mData = getContentResolver().query(uri, projection,
                            selection, null, null);
                    PlayerTasksHelper.setCursorData(mData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return mData;
            }

            @Override
            public void deliverResult(@Nullable Cursor data) {
                super.deliverResult(data);
                mData = data;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        adapter.swapData(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
