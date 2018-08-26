package com.example.roshan.musicapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ListActivity extends Activity {

    private MediaStore mediaStore;
    public static ArrayList<Song> songList;
    private String[] query = {MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM_ID};
    private ListView list;
    private MyAdapter adapter;

    private static String getColumnValue(Cursor cursor, int returnColumnValue, int matchAgainstColumn, String matchValue) {
        boolean moved;
        for (moved = cursor.moveToFirst(); moved; moved = cursor.moveToNext()) {
            if (cursor.getString(matchAgainstColumn).equals(matchValue)) {
                return cursor.getString(returnColumnValue);
            }
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_activity);

        list = findViewById(R.id.songs_list);
        songList = new ArrayList<Song>();
        adapter = new MyAdapter(this, R.layout.song_list_item, songList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = songList.get(position);
                //String songPath = song.getPath();
                Intent intent = new Intent(view.getContext(), PlayerActivity.class);
                intent.putExtra("songObject", song);
                intent.putExtra("SongItemIndex", position);
                startActivityForResult(intent, 0);
            }
        });
        Cursor songsCursor = this.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, query, null, null, null);
        if (songsCursor == null) {
            return;
        }
        Cursor albumsCursor = getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                null, null,
                null);

        int albumIdColumnIndex = albumsCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
        int albumArtColumnIndex = albumsCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);


        int pathColumnIndex = songsCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
        int titleColumnIndex = songsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
        int albumCoulumIndex = songsCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
        int artistColumnIndex = songsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
        int songAlbumIDColumnIndex = songsCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);

        boolean moved;
        for (moved = songsCursor.moveToFirst(); moved; moved = songsCursor.moveToNext()) {
            String path = songsCursor.getString(pathColumnIndex);
            String title = songsCursor.getString(titleColumnIndex);
            String album = songsCursor.getString(albumCoulumIndex);

            String artist = songsCursor.getString(artistColumnIndex);
//            here Android mediastore do not provide album art path directly. so we need a custom function
//            to get album art path..
            String albumArt = getColumnValue(albumsCursor, albumArtColumnIndex, albumIdColumnIndex, songsCursor.getString(songAlbumIDColumnIndex));
//            System.out.println("JITESH: " + albumArt);
//            System.out.println(artist);
            if (artist.equalsIgnoreCase("<unknown>")) {
                artist = "Unkonwn Artist";
            }
            songList.add(new Song(title, album, artist, path, albumArt));
        }
        songsCursor.close();
    }

    public class MyAdapter extends ArrayAdapter {
        public MyAdapter(Context context, int resource, java.util.List objects) {
            super(context, resource, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            Song song = songList.get(position);
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                convertView = inflater.inflate(R.layout.song_list_item, null);
                holder = new ViewHolder();

                holder.field1 = convertView.findViewById(R.id.title);
                holder.field2 = convertView.findViewById(R.id.artist);
                holder.field3 = convertView.findViewById(R.id.album);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.field1.setText(song.title);
            holder.field2.setText(song.album);
            holder.field3.setText(song.artists);

            return convertView;
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorWhite));
        getActionBar().hide();
    }

}


