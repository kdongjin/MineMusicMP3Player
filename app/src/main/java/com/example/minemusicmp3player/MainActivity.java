package com.example.minemusicmp3player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    //activity_main id
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private RecyclerView recyclerView;
    private RecyclerView recyclerLike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find id activity_main
        findViewByIdFunc();

        //현제 액티비티있는 프레임레이아웃에 프래그먼트 지정
        replaceFrag();
    }

    //현제 액티비티있는 프레임레이아웃에 프래그먼트 지정
    private void replaceFrag() {
        //프래그먼트 생성
        Player player = new Player();
        FragmentTransaction ft  =getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, player);
        ft.commit();
    }

    private void findViewByIdFunc() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerLike = (RecyclerView) findViewById(R.id.recyclerLike);
    }
}