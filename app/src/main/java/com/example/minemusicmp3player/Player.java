package com.example.minemusicmp3player;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

//implements View.OnClickListener 왜 썼을까?
public class Player extends Fragment implements View.OnClickListener{
    private ImageView ivAlbum;
    private TextView tvPlayCount, tvArtist, tvTitle, tvCurrentTime, tvDuration;
    private SeekBar seekBar;
    private ImageButton ibPlay,ibPrevious, ibNext, ibLike;

    //프래그먼트에서 장착된 액티비티를 가져올수 있다. getActivity()
    //노래를 등록하기 위해서 선언 객체변수
    private MainActivity mainActivity;
    private MediaPlayer mediaPlayer = new MediaPlayer();

    //노래들을 위치지정
    private int index;
    private MusicData musicData = new MusicData();

    //좋아요 리스트 만 가져왔을까, MusicAdapter ?
    private ArrayList<MusicData> likeArrayList = new ArrayList<>();
   // private MusicAdapter musicAdapter;


    //Context (화면 + 클래스)
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mainActivity = (MainActivity)context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.mainActivity = null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.player, container, false);
        findViewByIdFunc(view);

        return view;
    }

    private void findViewByIdFunc(View view) {
        ivAlbum = view.findViewById(R.id.ivAlbum);
        tvPlayCount = view.findViewById(R.id.tvPlayCount);
        tvArtist = view.findViewById(R.id.tvArtist);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCurrentTime = view.findViewById(R.id.tvCurrentTime);
        tvDuration = view.findViewById(R.id.tvDuration);
        seekBar = view.findViewById(R.id.seekBar);
        ibPlay = view.findViewById(R.id.ibPlay);
        ibPrevious = view.findViewById(R.id.ibPrevious);
        ibNext = view.findViewById(R.id.ibNext);
        ibLike = view.findViewById(R.id.ibLike);


        ibPlay.setOnClickListener(this);
        ibPrevious.setOnClickListener(this);
        ibNext.setOnClickListener(this);
        ibLike.setOnClickListener(this);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) mediaPlayer.seekTo(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibPlay  :
                if(ibPlay.isActivated() == true){
                    mediaPlayer.pause();
                    ibPlay.setActivated(false);
                }else{
                    mediaPlayer.start();
                    ibPlay.setActivated(true);
                    //시크바를 스레드방식으로 진행해주는 함수
                    setSeekBarThread();
                }
                break;
            case R.id.ibPrevious  :
                mediaPlayer.stop();
                mediaPlayer.reset();
                index = (index == 0)? mainActivity.getMusicDataArrayList().size()-1 : index -1;
                setPlayerData(index, true);
                break;
            case R.id.ibNext  :
                mediaPlayer.stop();
                mediaPlayer.reset();
                index = (index ==  mainActivity.getMusicDataArrayList().size()-1) ? 0 : index+1;
                setPlayerData(index, true);
                break;
            case R.id.ibLike  :
                if(ibLike.isActivated()== true){
                    ibLike.setActivated(false);
                    musicData.setLiked(0);
                    Toast.makeText(mainActivity, "좋아요취소", Toast.LENGTH_SHORT).show();
                }else{
                    ibLike.setActivated(true);
                    musicData.setLiked(1);
                    Toast.makeText(mainActivity, "좋아요", Toast.LENGTH_SHORT).show();
                }
                break;
            default:  break;
        }

    }

    private void setSeekBarThread() {
        Thread thread = new Thread(new Runnable() {
           SimpleDateFormat simpleDateFormat=new SimpleDateFormat("mm:ss");
            @Override
            public void run() {
                while(mediaPlayer.isPlaying()==true){
                    int timeData = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(timeData);

                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrentTime.setText(simpleDateFormat.format(timeData));
                        }
                    });
                    SystemClock.sleep(100);
                }//end of while
            }
        });

        thread.start();
    }

    //리사이클러뷰에서 아이템을 선택하면 해당된 위치와 좋아요음악(false), 일반음악(true) 선택 내용이 온다.
    public void setPlayerData(int position, boolean flag) {
        index = position;

        mediaPlayer.stop();
        mediaPlayer.reset();

        if(flag == true){
            musicData=mainActivity.getMusicDataArrayList().get(index);
        }else{

        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mm:ss");

        tvTitle.setText(musicData.getTitle());
        tvArtist.setText(musicData.getArtist());
        tvPlayCount.setText(String.valueOf(musicData.getPlayCount()));
        tvDuration.setText(simpleDateFormat.format(Integer.parseInt(musicData.getDuration())));

        if(musicData.getLiked() == 1){
            ibLike.setActivated(true);
        }else{
            ibLike.setActivated(false);
        }

        // 앨범 이미지 세팅
        Bitmap albumImg = getAlbumImg(mainActivity, Long.parseLong(musicData.getAlbumArt()), 200);
        if(albumImg != null){
            ivAlbum.setImageBitmap(albumImg);
        }else{
            ivAlbum.setImageResource(R.drawable.album_default);
        }

        // 음악 재생
        Uri musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,musicData.getId());
        try {
            mediaPlayer.setDataSource(mainActivity, musicURI);
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBar.setProgress(0);
            seekBar.setMax(Integer.parseInt(musicData.getDuration()));
            ibPlay.setActivated(true);

            setSeekBarThread();

            //한곡의 노래를 완료했을때 발생하는 이벤트 리스너
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    musicData.setPlayCount(musicData.getPlayCount() + 1);
                    ibNext.callOnClick();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //앨범사진 아이디와 앨범사이즈를 부여한다.
    private Bitmap getAlbumImg(Context context, long albumArt, int imgMaxSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        /*컨텐트 프로바이더(Content Provider)는 앱 간의 데이터 공유를 위해 사용됨.
        특정 앱이 다른 앱의 데이터를 직접 접근해서 사용할 수 없기 때문에
        무조건 컨텐트 프로바이더를 통해 다른 앱의 데이터를 사용해야만 한다.
        다른 앱의 데이터를 사용하고자 하는 앱에서는 URI를 이용하여 컨텐트 리졸버(Content Resolver)를 통해
        다른 앱의 컨텐트 프로바이더에게 데이터를 요청하게 되는데
        요청받은 컨텐트 프로바이더는 URI를 확인하고 내부에서 데이터를 꺼내어 컨텐트 리졸버에게 전달한다.
        */
        ContentResolver contentResolver = context.getContentResolver();

        // 앨범아트는 uri를 제공하지 않으므로, 별도로 생성.
        Uri uri = Uri.parse("content://media/external/audio/albumart/"+albumArt);
        if (uri != null){
            ParcelFileDescriptor fd = null;
            try{
                fd = contentResolver.openFileDescriptor(uri, "r");

                //true면 비트맵객체에 메모리를 할당하지 않아서 비트맵을 반환하지 않음.
                //다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.
                //93번 문항부터 98번까지는 체크안해도 되는 문장임. options.inJustDecodeBounds = false; 앞문장까지

                options.inJustDecodeBounds = false; // false 비트맵을 만들고 해당이미지의 가로, 세로, 중심으로 가져옴
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);

                if(bitmap != null){
                    // 정확하게 사이즈를 맞춤
                    if(options.outWidth != imgMaxSize || options.outHeight != imgMaxSize){
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, imgMaxSize, imgMaxSize, true);
                        bitmap.recycle();
                        bitmap = tmp;
                    }
                }
                return bitmap;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }

}
