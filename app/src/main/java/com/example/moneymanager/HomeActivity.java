package com.example.moneymanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.example.moneymanager.Model.UpdateAppInfo;
import com.example.moneymanager.service.ApiService;
import com.example.moneymanager.service.AppInnerDownLoader;
import com.example.moneymanager.service.ServiceFactory;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;

//    Fragment
    private DashboardFragment dashboardFragment;
    private IncomeFragment incomeFragment;
    private ExpenseFragment expenseFragment;
    private FirebaseAuth mAuth;

    public interface CheckCallBack{
        // Related interfaces to detect success or failure
        void onSuccess(UpdateAppInfo updateInfo);
        void onError();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Money Manager");
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        bottomNavigationView = findViewById(R.id.bottomNavigationbar);
        bottomNavigationView.setItemIconTintList(null);
        frameLayout = findViewById(R.id.main_frame);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.naView);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);

        dashboardFragment = new DashboardFragment();
        incomeFragment = new IncomeFragment();
        expenseFragment = new ExpenseFragment();

        setFragment(dashboardFragment);
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.dashboard) {
                    setFragment(dashboardFragment);
                } else if (item.getItemId() == R.id.income) {
                    setFragment(incomeFragment);
                } else if (item.getItemId() == R.id.expense) {
                    setFragment(expenseFragment);
                }

                return false;
            }
        });

        checkUpdate(BuildConfig.VERSION_CODE, new CheckCallBack() {
            @Override
            public void onSuccess(UpdateAppInfo updateInfo) {
                String downUrl= updateInfo.getApkUrl();
                String updateMessage = updateInfo.getReleaseNotes();
                String appName = updateInfo.getLatestVersion();

                showUpdateDialog(HomeActivity.this, appName, downUrl, updateMessage);
            }
            @Override
            public void onError() {
//                Handle callback error here...
                Log.d("CheckUpdate", "something wrong here");
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }

    public void displaySelectedListener(int itemId) {
        Fragment fragment = null;

        if (itemId == R.id.dashboard) {
            fragment = new DashboardFragment();
        } else if (itemId == R.id.income) {
            fragment = new IncomeFragment();
        } else if (itemId == R.id.expense) {
            fragment = new ExpenseFragment();
        } else if (itemId == R.id.logout) {
            mAuth.signOut();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_frame, fragment);
            ft.commit();
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displaySelectedListener(item.getItemId());
        return true;
    }

    /**
     * Check for updates
     */
    @SuppressWarnings("unused")
    public static void checkUpdate(int curVersion, final CheckCallBack updateCallback) {

        ApiService apiService = ServiceFactory.createServiceFrom(ApiService.class);
        apiService.getUpdateInfo()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UpdateAppInfo>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {
                        Log.e("CheckUpdate", e.toString());
                    }
                    @Override
                    public void onNext(UpdateAppInfo updateAppInfo) {
                        if (updateAppInfo == null ||
                                updateAppInfo.getApkUrl() == null) {
                            updateCallback.onError(); // Fail
                        } else if (curVersion >= updateAppInfo.getLatestVersionCode()) {
                            Log.i("CheckUpdate", "No update required");
                        } else {
                            updateCallback.onSuccess(updateAppInfo);
                        }
                    }
                });
    }

    private void showUpdateDialog(final Context context, final String appName, final String downUrl, final String updateMessage) {
        final AlertDialog mDialog = new AlertDialog.Builder(context)
                .setTitle("Update is available!")
                .setMessage(updateMessage)
                .setPositiveButton("Update Now", null)
                .setNegativeButton("Dismiss", null)
                .setCancelable(false)
                .show();

        Button updateBtn = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        Button dismissBtn = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!canDownloadState()) {
                    return;
                }

                AppInnerDownLoader.downLoadApk(HomeActivity.this,downUrl,appName);
                mDialog.dismiss();
            }
        });

        dismissBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }
    private boolean canDownloadState() {
        try {
            int state = this.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}