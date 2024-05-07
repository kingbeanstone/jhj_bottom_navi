package com.example.bottomnavi;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private Frag1 frag1;
    private Frag2 frag2;
    private Frag3 frag3;
    private Frag4 frag4;
    private Frag5 frag5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bottomNavigationView = findViewById((R.id.bottomNavi));
        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int n = menuItem.getItemId();
                if(n==R.id.action_airplane)
                        setFrag(0);

                if(n==R.id.action_airport)
                        setFrag(1);

                if(n==R.id.action_bt)
                        setFrag(2);

                if(n==R.id.action_call)
                        setFrag(3);

                if(n==R.id.action_run)
                        setFrag(4);


                return true;
            }
        });

        frag1 = new Frag1();
        frag2 = new Frag2();
        frag3 = new Frag3();
        frag4 = new Frag4();
        frag5 = new Frag5();
        setFrag(0); //첫 프래그먼트 화면을 무엇으로 지정해줄 것인지 선택
    }

    //프래그먼트 교체가 일어나는 실행문이다.
    private void setFrag(int n){
        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();
        switch(n){
            case 0:
                ft.replace(R.id.main_frame,frag1);
                ft.commit();
                break;

            case 1:
                ft.replace(R.id.main_frame,frag2);
                ft.commit();
                break;

            case 2:
                ft.replace(R.id.main_frame,frag3);
                ft.commit();
                break;

            case 3:
                ft.replace(R.id.main_frame,frag4);
                ft.commit();
                break;

            case 4:
                ft.replace(R.id.main_frame,frag5);
                ft.commit();
                break;
        }

    }


}