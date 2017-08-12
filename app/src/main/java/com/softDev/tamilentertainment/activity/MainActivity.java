package com.softDev.tamilentertainment.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.content.Context;

import com.softDev.tamilentertainment.R;
import com.softDev.tamilentertainment.fragment.FavouritesFragment;
import com.softDev.tamilentertainment.fragment.LibraryFragment;
import com.softDev.tamilentertainment.fragment.StationsFragment;
import com.softDev.tamilentertainment.service.RadioService;
import com.softDev.tamilentertainment.util.RateThisApp;

import static com.softDev.tamilentertainment.fragment.PlayerFragment.radioService;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    final Context context = this;
    public static String setTime=null;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new LibraryFragment();
        fragmentTransaction.replace(R.id.main_container_wrapper, fragment);
        fragmentTransaction.commit();
        startService(new Intent(getApplicationContext(), RadioService.class));
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        RateThisApp.init(new RateThisApp.Config(3, 5));
        // Set callback (optional)
        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
               // Toast.makeText(MainActivity.this, "Opening playstore", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoClicked() {
               // Toast.makeText(MainActivity.this, "Please rate us later ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked() {
                //Toast.makeText(MainActivity.this, "Cancel event", Toast.LENGTH_SHORT).show();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    fragment = new LibraryFragment();
                } else if (id == R.id.nav_favourites) {
                    fragment = new FavouritesFragment();
                } else if (id == R.id.nav_exit) {
                        radioService.exitNotification();
                        System.exit(0);
                }else if (id == R.id.nav_review) {
                    RateThisApp.showRateDialog(MainActivity.this, R.style.MyAlertDialogStyle2);
                }else if (id == R.id.nav_share) {
                    shareApplink();
                }
                else if (id == R.id.nav_stations) {
                   fragment=new StationsFragment();
                }
                else if (id == R.id.nav_setsleep) {
                    promptTime();
                }

                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container_wrapper, fragment);
                transaction.commit();

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                assert drawer != null;
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    public void shareApplink(){
        try{
            Intent i = new Intent (Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT,"App name");
            String sAux="\nCheck out the online radio app at \n\n";
            sAux=sAux+"https://play.google.com/store/apps/details?id=com.softDev.tamilentertainment\n\n";
            i.putExtra(Intent.EXTRA_TEXT,sAux);
            startActivity(Intent.createChooser(i,"Share with:"));
        }catch (Exception e){

        }



    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences Gp = getSharedPreferences("setting", MODE_PRIVATE);
            SharedPreferences.Editor e = Gp.edit();
            e.putString("time",setTime);
            e.commit();

    }
    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences Gp = getSharedPreferences("setting", MODE_PRIVATE);
        // SharedPreferences Gptxt = getSharedPreferences("setting", MODE_PRIVATE);
          setTime=Gp.getString("time", null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public  void promptTime(){

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompts, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

       final  EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);
        if(setTime!=null){
            userInput.setText(setTime);
        }
        else{
            userInput.setText("15");
        }

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                // get user input and set it to result
                                // edit text
                                setTime=userInput.getText().toString();
                                SharedPreferences Gp = getSharedPreferences("setting", MODE_PRIVATE);
                                SharedPreferences.Editor e = Gp.edit();
                                e.putString("time",setTime);
                                e.commit();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
