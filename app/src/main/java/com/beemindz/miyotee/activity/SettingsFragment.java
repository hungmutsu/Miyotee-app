package com.beemindz.miyotee.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

import com.beemindz.miyotee.R;
import com.beemindz.miyotee.util.CommonUtils;
import com.beemindz.miyotee.util.Constant;

public class SettingsFragment extends android.support.v4.preference.PreferenceFragment {

  public static final String TAG = "SettingsFragment";
  private Preference pref;
  private Account[] accounts;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Load the preferences from an XML resource
    addPreferencesFromResource(R.xml.setting);
    
    accounts = AccountManager.get(getActivity().getApplicationContext()).getAccountsByType(Constant.ACCOUNT_TYPE);
    
    pref = findPreference(SettingActivity.KEY_PREF_CATEGORY_SYNC);

    checkAccount();
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    checkOnline();
    checkAccount();
  }
  
  @Override
  public void onPause() {
    super.onPause();
    Log.d(TAG, "onPause");
    checkOnline();
    checkAccount();
  }

  private void checkOnline() {
    accounts = AccountManager.get(getActivity().getApplicationContext()).getAccountsByType(Constant.ACCOUNT_TYPE);
    Log.d(TAG, "checkOnline:====="+ accounts.length);
    if (accounts.length > 0) {
      pref.setSummary("You're online.");
    } else {
      pref.setSummary(R.string.pref_category_sync_summary);
    }
    
    Log.i(TAG, pref.getSummary().toString());
  }

  private void checkAccount() {
    accounts = AccountManager.get(getActivity().getApplicationContext()).getAccountsByType(Constant.ACCOUNT_TYPE);
    pref.setOnPreferenceClickListener(new OnPreferenceClickListener() {

      @Override
      public boolean onPreferenceClick(Preference preference) {
        Log.d(TAG, "checkAccount:====="+ accounts.length);
        if (accounts.length == 0) {
          Intent intent = new Intent(preference.getContext(), MainActivity.class);
          preference.getContext().startActivity(intent);
        } else {
          final Preference p = preference;
          // show dialog
          CommonUtils.confirm(preference.getContext(), R.string.setting_confirm_title_sign_out,
              R.string.setting_confirm_message_sign_out, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                  eventSignOut(p.getContext());
                  
                  dialog.cancel();
                }
              }, new OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              }).show();
        }
        return false;
      }
    });
  }

  private void eventSignOut(Context context) {
    if (accounts.length > 0) {
      for (Account account : accounts) {
        Log.i(TAG, "remove account : " + account.name);
        AccountManager.get(context).removeAccount(account, null, null);
      }
      accounts = new Account[]{};
      pref.setSummary(R.string.pref_category_sync_summary);
    }
  }
}
