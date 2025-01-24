package com.na21k.schedulenotes.ui.settings;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.na21k.schedulenotes.R;
import com.na21k.schedulenotes.databinding.ActivitySettingsBinding;
import com.na21k.schedulenotes.helpers.UiHelper;

public class SettingsActivity extends AppCompatActivity {

    private static final String SETTINGS_FRAGMENT_KEY = "SETTINGS_FRAGMENT_KEY";
    private ActivitySettingsBinding mBinding;
    private SettingsFragment mSettingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setSupportActionBar(mBinding.appBar.appBar);

        if (savedInstanceState == null) {
            mSettingsFragment = new SettingsFragment();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, mSettingsFragment)
                    .commit();
        } else {
            mSettingsFragment = (SettingsFragment) getSupportFragmentManager()
                    .getFragment(savedInstanceState, SETTINGS_FRAGMENT_KEY);
        }

        handleWindowInsets();

        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void handleWindowInsets() {
        UiHelper.handleWindowInsets(getWindow(), mBinding.getRoot(),
                mBinding.container, null, null, false);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, SETTINGS_FRAGMENT_KEY, mSettingsFragment);
    }
}
