package com.example.roshan.musicapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;


//import android.support.annotation.RequiresApi;

public class PlayerActivity extends Activity {
    private TextView songTitleView;
    private TextView artistView;
    private TextView albumView;
    private ImageView albumArtView;
    private Button repeatButton;
    private Button previousButton;
    private Button nextButton;
    private Button playToggle;

    Drawable pauseBG;
    Drawable playBG;
    Drawable loopPinkBG;
    Drawable loopGreyBG;


    private TextView timer;
    private SeekBar seekBar;
    private Handler handler;
    private MediaPlayer mp;
    private String songPath;
    private Song song;
    private Boolean repeat;
    private int songItemPosition;
    // Boolean controleRunFlag;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);

        pauseBG = getResources().getDrawable(R.drawable.ic_pause_circle_outline_black_24dp);
        playBG = getResources().getDrawable(R.drawable.ic_play_circle_outline_black_24dp);
        loopPinkBG = getResources().getDrawable(R.drawable.ic_replay_pink_24dp);
        loopGreyBG = getResources().getDrawable(R.drawable.ic_replay_black_24dp);

        songTitleView = findViewById(R.id.title);
        artistView = findViewById(R.id.artists);
        albumView = findViewById(R.id.album);
        albumArtView = findViewById(R.id.albumArt);
        repeatButton = findViewById(R.id.loop_button);
        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        timer = findViewById(R.id.time);
        playToggle = findViewById(R.id.playToggle);
        seekBar = findViewById(R.id.seeker);

        Intent intent = getIntent();
        song = (Song) intent.getSerializableExtra("songObject");
        songItemPosition = intent.getExtras().getInt("SongItemIndex");
        play(song);
/*
        songPath = song.getPath();
        repeat = PlayerContainer.loop;
        repeatButton.setBackground(repeat ? loopPinkBG : loopGreyBG);

        try {
            if (PlayerContainer.player == null) {
                mp = new MediaPlayer();
                PlayerContainer.player = mp;
            } else {
                mp = PlayerContainer.player;
                mp.reset();
            }
            File file = new File(songPath);
            FileInputStream fis = new FileInputStream(file);
            mp.setDataSource(fis.getFD());
            fis.close();
            mp.prepare();
            mp.start();
            mp.setLooping(repeat);
            playToggle.setBackground(pauseBG);
            initialiseSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
       */
        playToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying()) {
                    mp.pause();
                } else {
                    mp.start();
                }
                playToggle.setBackground(mp.isPlaying() ? pauseBG : playBG);

            }
        });

        /*
        songTitleView.setText(song.getTitle());
        artistView.setText(song.getArtists());
        albumView.setText(song.getAlbum());
        */

        repeatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!repeat) {
                    PlayerContainer.loop = true;
                    repeat = true;
                    mp.setLooping(true);
                    v.setBackground(loopPinkBG);
                } else {
                    PlayerContainer.loop = false;
                    repeat = false;
                    mp.setLooping(false);
                    v.setBackground(loopGreyBG);
                }
            }
        });


        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (!repeat) {
                    playnext();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playnext();
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playprevious();
            }
        });
    }

    volatile boolean seeking = false;

    private void initialiseSeekBar() {
        seekBar.setMax(mp.getDuration());
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!seeking) {
                    seekBar.setProgress(mp.getCurrentPosition());
                }
                int seconds = (mp.getCurrentPosition() % 60000) / 1000;
                int minutes = mp.getCurrentPosition() / 60000;
                String time = String.format("%02d:%02d", minutes, seconds);
                timer.setText(time);
                handler.postDelayed(this, 100);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              /*
                if (seeking) {
                    mp.seekTo(progress);
                    // seeking = false;
                }
              */
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seeking = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mp.seekTo(seekBar.getProgress());
                seeking = false;
            }
        });
    }

    private void playnext() {
        if (songItemPosition < ListActivity.songList.size() - 1) {
            Song song = ListActivity.songList.get(songItemPosition + 1);
            songItemPosition++;
            play(song);
        } else {
            Toast toast = Toast.makeText(this, "You have reached END of the list!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void playprevious() {
        if (songItemPosition > 0) {
            Song song = ListActivity.songList.get(songItemPosition - 1);
            songItemPosition--;
            play(song);
        } else {
            Toast toast = Toast.makeText(this, "You have reached START of the list!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void play(Song song) {
        songPath = song.path;
        repeat = PlayerContainer.loop;
        repeatButton.setBackground(repeat ? loopPinkBG : loopGreyBG);
        try {
            if (PlayerContainer.player == null) {
                mp = new MediaPlayer();
                PlayerContainer.player = mp;
            } else {
                mp = PlayerContainer.player;
                mp.reset();
            }
            File file = new File(songPath);
            FileInputStream fis = new FileInputStream(file);
            mp.setDataSource(fis.getFD());
            fis.close();
            mp.prepare();
            mp.start();
            mp.setLooping(repeat);
            playToggle.setBackground(pauseBG);
            initialiseSeekBar();
        } catch (IOException e) {
            e.printStackTrace();
        }
        songTitleView.setText(song.title);
        artistView.setText(song.artists);
        albumView.setText(song.album);
        if (song.albumArtPath == null) {
            albumArtView.setImageBitmap(null);
        } else
            albumArtView.setImageBitmap(BitmapFactory.decodeFile(song.albumArtPath));

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}


























        /*
        //asking for users permission
        songTitleView = findViewById(R.id.title);
        artistView = findViewById(R.id.artist);
        albumView = findViewById(R.id.album);
        songList = findViewById(R.id.song_list);
        String [] projecton = {MediaStore.Audio.Media._ID,MediaStore.Audio.Media.DISPLAY_NAME,MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM};

        setContentView(R.layout.activity_list);
        cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projecton,null,null,null);

        //noinspection deprecation
        CursorAdapter listAdapter = new SimpleCursorAdapter(this,R.layout.layout_for_list,cursor,new String[] {"DISPLAY_NAME","ARTIST","ALBUM"},new int[] {R.id.title,R.id.artist, R.id.album},0);

        songList.setAdapter(listAdapter);

    }
    public void onDestroy() {
        super.onDestroy();
        cursor.close();
    }

}
*/
/*
class SongDatabase extends SQLiteOpenHelper {

    private static final int version = 1;
    private static final String name = "SONG_DATABASE";

    public SongDatabase(Context context) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE SONG_TABLE (_ID INTEGER PRIMARY KEY AUTOINCREMENT, SONG TITLE, ARTIST, ALBUM )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //"/sounds/jaane kya tune kahi.mp3"
}
*/