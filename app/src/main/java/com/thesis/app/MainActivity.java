package com.thesis.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // UI Components
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    // Tab Buttons
    private Button btnTabAll, btnTabSports, btnTabEvents, btnTabFaculty;

    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Session & User Data
    private SessionManager sessionManager;
    private String currentUsername, currentEmail, currentUserId;

    // Fragment Management
    private static final String CURRENT_FRAGMENT_TAG = "current_fragment";
    private String currentFragmentTag = "";

    // Fragment Tags
    private static final String TAG_ALL_NEWS = "all_news";
    private static final String TAG_SPORTS = "sports";
    private static final String TAG_EVENTS = "events";
    private static final String TAG_FACULTY = "faculty";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFirebase();
        initializeViews();
        setupToolbar();
        setupNavigationDrawer();
        initializeTabs();
        setupBackPressedHandler();

        // Restore fragment state or load default
        if (savedInstanceState != null) {
            currentFragmentTag = savedInstanceState.getString(CURRENT_FRAGMENT_TAG, TAG_ALL_NEWS);
            restoreFragmentState();
        } else {
            navigateToFragment(TAG_ALL_NEWS);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAGMENT_TAG, currentFragmentTag);
    }

    private void initializeFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        sessionManager = new SessionManager(this);
        checkSessionValidity();
    }

    private void initializeViews() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);

        // Tab buttons
        btnTabAll = findViewById(R.id.btnTabAll);
        btnTabSports = findViewById(R.id.btnTabSports);
        btnTabEvents = findViewById(R.id.btnTabEvents);
        btnTabFaculty = findViewById(R.id.btnTabFaculty);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Thesis News");
        }
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeTabs() {
        btnTabAll.setOnClickListener(v -> navigateToFragment(TAG_ALL_NEWS));
        btnTabSports.setOnClickListener(v -> navigateToFragment(TAG_SPORTS));
        btnTabEvents.setOnClickListener(v -> navigateToFragment(TAG_EVENTS));
        btnTabFaculty.setOnClickListener(v -> navigateToFragment(TAG_FACULTY));
    }

    private void setupBackPressedHandler() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    /**
     * Main navigation method that handles fragment switching
     */
    private void navigateToFragment(String fragmentTag) {
        // Don't recreate if already showing the same fragment
        if (currentFragmentTag.equals(fragmentTag)) {
            return;
        }

        Fragment fragment = createFragmentByTag(fragmentTag);
        if (fragment == null) {
            return;
        }

        // Update UI states
        updateTabStates(fragmentTag);
        updateNavigationDrawerState(fragmentTag);

        // Replace fragment
        replaceFragment(fragment, fragmentTag);

        // Update current fragment tag
        currentFragmentTag = fragmentTag;
    }

    /**
     * Creates fragment instance based on tag
     */
    private Fragment createFragmentByTag(String tag) {
        switch (tag) {
            case TAG_ALL_NEWS:
                return new AllNewsFragment();
            case TAG_SPORTS:
                return new SportsFragment();
            case TAG_EVENTS:
                return new EventsFragment();
            case TAG_FACULTY:
                return new FacultyNewsFragment();
            default:
                return new AllNewsFragment();
        }
    }

    /**
     * Safely replaces fragment with proper transaction handling
     */
    private void replaceFragment(Fragment fragment, String tag) {
        try {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, tag);
            transaction.commit();
        } catch (Exception e) {
            // Handle fragment transaction exceptions
            Toast.makeText(this, "Error loading content", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Updates tab button states
     */
    private void updateTabStates(String activeTag) {
        // Reset all tabs
        btnTabAll.setSelected(false);
        btnTabSports.setSelected(false);
        btnTabEvents.setSelected(false);
        btnTabFaculty.setSelected(false);

        // Set active tab
        switch (activeTag) {
            case TAG_ALL_NEWS:
                btnTabAll.setSelected(true);
                break;
            case TAG_SPORTS:
                btnTabSports.setSelected(true);
                break;
            case TAG_EVENTS:
                btnTabEvents.setSelected(true);
                break;
            case TAG_FACULTY:
                btnTabFaculty.setSelected(true);
                break;
        }
    }

    /**
     * Updates navigation drawer checked state
     */
    private void updateNavigationDrawerState(String activeTag) {
        switch (activeTag) {
            case TAG_ALL_NEWS:
                navigationView.setCheckedItem(R.id.navallnews);
                break;
            case TAG_SPORTS:
                navigationView.setCheckedItem(R.id.navsports);
                break;
            case TAG_EVENTS:
                navigationView.setCheckedItem(R.id.navevents);
                break;
            case TAG_FACULTY:
                navigationView.setCheckedItem(R.id.navfacultynews);
                break;
        }
    }

    /**
     * Restores fragment state after configuration changes
     */
    private void restoreFragmentState() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(currentFragmentTag);
        if (fragment == null) {
            // Fragment not found, create new one
            navigateToFragment(currentFragmentTag);
        } else {
            // Fragment exists, just update UI states
            updateTabStates(currentFragmentTag);
            updateNavigationDrawerState(currentFragmentTag);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.navallnews) {
            navigateToFragment(TAG_ALL_NEWS);
        } else if (id == R.id.navsports) {
            navigateToFragment(TAG_SPORTS);
        } else if (id == R.id.navevents) {
            navigateToFragment(TAG_EVENTS);
        } else if (id == R.id.navfacultynews) {
            navigateToFragment(TAG_FACULTY);
        } else if (id == R.id.navprofile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("username", currentUsername);
            intent.putExtra("email", currentEmail);
            intent.putExtra("userId", currentUserId);
            startActivity(intent);
        } else if (id == R.id.navsettings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.navlogout) {
            showLogoutDialog();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkSessionValidity() {
        if (!sessionManager.isSessionValid()) {
            redirectToLogin();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sessionManager.logoutUser();
            redirectToLogin();
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void showLogoutDialog() {
        AlertHelper.showLogoutDialog(this, new AlertHelper.LogoutCallback() {
            @Override
            public void onConfirm() {
                mAuth.signOut();
                AlertHelper.showSuccessSnackbar(findViewById(android.R.id.content),
                        "Logged out successfully");

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancel() {
                AlertHelper.showInfoSnackbar(findViewById(android.R.id.content),
                        "Logout cancelled");
            }
        });
    }
}
