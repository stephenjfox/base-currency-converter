package com.stephenjfox.simplecurrencyconverter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Root for the easy USD -> SKW (South Korean Won)
 *
 * Created by Stephen on 9/24/2016.
 */

public class MainActivity extends AppCompatActivity {
  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    final FragmentManager manager = getSupportFragmentManager();
    Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

    if (fragment == null) {
      fragment = new ConverterFragment();
      manager.beginTransaction()
             .add(R.id.fragmentContainer, fragment)
             .commit();
    }
  }
}
