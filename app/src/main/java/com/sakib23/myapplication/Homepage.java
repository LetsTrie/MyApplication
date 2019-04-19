package com.sakib23.myapplication;

import android.content.res.ColorStateList;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RecyclerView recyclerView;
    AdapterRecyclerViewClass myadapter;
    ArrayList<UserPost> info;
   private DatabaseReference userPostInfoDatabase;

    // ****************
    //Navigation Drawer

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;

    // ****************

    private Button CreateNewArticleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        int cnt = 1;
        Log.d("Checc", " " + cnt); cnt++;


        // ****************
        //Navigation Drawer

        drawerLayout = findViewById(R.id.DrawerLayoutID);
        navigationView = findViewById(R.id.NavigationViewID);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);


        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);


        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //to display the navigation bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        navigationView.setItemTextColor(ColorStateList.valueOf(getResources().getColor(R.color.Blue)));
        navigationView.setItemIconTintList(ColorStateList.valueOf(getResources().getColor(R.color.Blue)));


        navigationView.setCheckedItem(R.id.homepageID);


        // ****************
        Log.d("Checc", " " + cnt); cnt++;

        recyclerView = findViewById(R.id.RecyclerViewID);
        userPostInfoDatabase = FirebaseDatabase.getInstance().getReference("userPostCollection");
        info = new ArrayList<>();

        userPostInfoDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                info.clear();
                for (DataSnapshot X : dataSnapshot.getChildren()) info.add(X.getValue(UserPost.class));
                Collections.reverse(info);
                Log.d("mychecking", " " + info.size() );
                myadapter = new AdapterRecyclerViewClass(Homepage.this, info);
                recyclerView.setAdapter(myadapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(Homepage.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Log.d("Checc", " " + cnt); cnt++;

        CreateNewArticleButton = findViewById(R.id.CreateArticleID);
        CreateNewArticleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Homepage.this, CreateNewArticle.class);
                intent.putExtra("Title", "");
                intent.putExtra("Timee", "");
                intent.putExtra("Description", "");
                intent.putExtra("Tag", "");
                intent.putExtra("Chosen", "");
                intent.putExtra("Ext", "");
                intent.putExtra("UID", "");
                intent.putExtra("Name", "");
                intent.putExtra("PostID", "");
                intent.putExtra("vis", "From Homepage");
                startActivity(intent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        finishAffinity();
    }

    // Navigation Drawer...
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(actionBarDrawerToggle.onOptionsItemSelected(item)) return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId()==R.id.myProfileID) {
            finish();
            Intent intent = new Intent(Homepage.this,MyProfile.class );
            startActivity(intent);
        }

        if (menuItem.getItemId()==R.id.forumID) {
            finish();
            Intent intent = new Intent(Homepage.this,Forum.class );
            startActivity(intent);
        }
        if(menuItem.getItemId() == R.id.logoutID) {
            finish();
            Intent intent = new Intent(Homepage.this,MainActivity.class );
            if(FirebaseAuth.getInstance().getCurrentUser() != null) FirebaseAuth.getInstance().signOut();
            startActivity(intent);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
