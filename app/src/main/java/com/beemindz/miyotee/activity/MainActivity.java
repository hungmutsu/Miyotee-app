package com.beemindz.miyotee.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.beemindz.miyotee.R;
import com.beemindz.miyotee.authentication.AccountAuthenticatorActivity;
import com.beemindz.miyotee.dao.TaskContentProvider;
import com.beemindz.miyotee.util.Constant;
import com.beemindz.miyotee.util.NetworkUtils;
import com.beemindz.miyotee.util.ToastUtils;
import com.facebook.FacebookException;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.analytics.tracking.android.EasyTracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AccountAuthenticatorActivity {

  private static final String TAG = "LOGIN";
  private static final String URL_HOST = "http://192.168.1.77/mytodo-service/";

  // private static final String URL_HOST =
  // "http://192.168.1.77/mytodo-service/";
  public static final String JSON_TAG_ERROR = "error";
  // Sync interval constants
  public static final long SECONDS_PER_MINUTE = 60L;
  public static final long SYNC_INTERVAL_IN_MINUTES = 5L;
  public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

  EditText loginName, loginPassword;
  TextView loginError;
  Button btnLogin;
  LoginButton btnFbLogin;

  // Progress Dialog
  private ProgressDialog pDialog;
  String fbAccessToken = "";

  // A content resolver for accessing the provider

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setDisplayShowHomeEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    // Check exist account
    AccountManager accountManager = AccountManager.get(this);
    Account[] accounts = accountManager.getAccountsByType(Constant.ACCOUNT_TYPE);

    if (accounts.length > 0) {
      // Chuyen den trang TaskList
      Intent i = new Intent(MainActivity.this, TaskListActivity.class);
      startActivity(i);
      finish();
    }

    loginName = (EditText) findViewById(R.id.loginName);
    loginPassword = (EditText) findViewById(R.id.loginPassword);
    loginError = (TextView) findViewById(R.id.loginError);

    // Đăng nhập
    btnFbLogin = (LoginButton) findViewById(R.id.btnFbLogin);
    btnLogin = (Button) findViewById(R.id.btnLogin);
    fbLogin();
    btnLogin.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        String userName = loginName.getText().toString().trim();
        String pass = loginPassword.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
          ToastUtils.toast(MainActivity.this, R.string.toast_err_login_username_required);
          return;
        }
        if (TextUtils.isEmpty(pass)) {
          ToastUtils.toast(MainActivity.this, R.string.toast_err_login_pass_required);
          return;
        }

        Login login = new Login(MainActivity.this, null, userName, pass, null);
        login.execute();
      }
    });

    // Chuyển sang trang đăng ký
    Button btnLinkToRegisterScreen = (Button) findViewById(R.id.btnLinkToRegisterScreen);
    btnLinkToRegisterScreen.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        // pDialog = ProgressDialog.show(MainActivity.this,
        // "Loading...", "Please wait...", false, true);
        Intent i = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(i);
        // finish();
      }

    });
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    switch (menuItem.getItemId()) {
      case android.R.id.home:
        finish();
        return true;
    }
    return (super.onOptionsItemSelected(menuItem));
  }

  @Override
  protected void onStart() {
    // TODO Auto-generated method stub
    super.onStart();

    EasyTracker.getInstance(this).activityStart(this);
    Log.i(TAG, "==onStart==");
  }

  @Override
  protected void onStop() {
    // TODO Auto-generated method stub
    super.onStop();
    EasyTracker.getInstance(this).activityStop(this);
    Log.i(TAG, "==onStop==");
  }

  private void fbLogin() {
    btnFbLogin.setOnErrorListener(new LoginButton.OnErrorListener() {

      @Override
      public void onError(FacebookException error) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Error : " + error.getMessage());
      }
    });

    // set permission list, Don't foeget to add email
    btnFbLogin.setReadPermissions(Arrays.asList("email"));
    // session state call back event
    btnFbLogin.setSessionStatusCallback(new Session.StatusCallback() {

      @SuppressWarnings("deprecation")
      @Override
      public void call(Session session, SessionState state, Exception exception) {
        // TODO Auto-generated method stub
        if (session.isOpened()) {
          fbAccessToken = session.getAccessToken();
          com.facebook.Request.executeMeRequestAsync(session, new com.facebook.Request.GraphUserCallback() {

            @Override
            public void onCompleted(GraphUser user, Response response) {
              // TODO Auto-generated method stub
              if (user != null) {
                Login login = new Login(MainActivity.this, user.getName(), user.asMap().get("email").toString(), "",
                    fbAccessToken);
                login.execute();

                Log.i(TAG, "User ID " + user.getId());
                Log.i(TAG, "Email " + user.asMap().get("email"));
                Log.i(TAG, "Username" + user.getUsername());
                Log.i(TAG, "Name" + user.getName());
                Log.i(TAG, "First name " + user.getFirstName());
                Log.i(TAG, "Last name " + user.getLastName());
              }
            }
          });
        }
      }
    });
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
  }

  /**
   * Background Async Task to Get complete user login
   */
  private class Login extends AsyncTask<String, Void, Boolean> {
    Context mContext;
    String username;
    String password;
    String accessToken;
    String fullName;

    public Login(Context context, String fullname, String username, String password, String accessToken) {
      mContext = context;
      this.fullName = fullname;
      this.username = username;
      this.password = password;
      this.accessToken = accessToken;

      btnLogin.setEnabled(false);
      pDialog = ProgressDialog.show(context, "Loading...", "Please wait...", false, true);
    }

    /**
     * Getting user details in background thread
     */
    @Override
    protected Boolean doInBackground(String... params) {

      // Check for success tag
      boolean error;
      /*
       * String deviceId = Secure.getString(getActivity().getContentResolver(),
       * Secure.ANDROID_ID); Log.d("android_id:", deviceId);
       */

      try {
        // Building Parameters
        String[] keys = new String[]{"username", "password", "fbAccessToken"};
        String[] values = new String[]{username, password, accessToken};

        JSONObject json = NetworkUtils.postJSONObjFromUrl(URL_HOST + "login.php", keys, values);
        // json error tagc
        error = json.getBoolean(JSON_TAG_ERROR);

        if (!error) {

          Bundle result = null;
          Account account = new Account(username, Constant.ACCOUNT_TYPE);
          AccountManager acountManager = AccountManager.get(mContext);

          if (acountManager.addAccountExplicitly(account, password, null)) {
            result = new Bundle();
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            setAccountAuthenticatorResult(result);

            /*
             * Turn on periodic syncing
             */
            ContentResolver.setSyncAutomatically(account, TaskContentProvider.AUTHORITY, true);

            ContentResolver.addPeriodicSync(account, TaskContentProvider.AUTHORITY, new Bundle(), SYNC_INTERVAL);
            return true;
          } else {
            return false;
          }
        } else {
          Log.i(TAG, json.getString("message"));
          // loginError.setText(json.getString("message"));
          return false;
        }
      } catch (JSONException e) {
        e.printStackTrace();
        return false;
      }

    }

    /**
     * After completing background task Dismiss the progress dialog
     */
    @Override
    protected void onPostExecute(Boolean result) {
      // dismiss the dialog once got all details
      btnLogin.setEnabled(true);
      pDialog.dismiss();
      Log.i(TAG, "Access Token : " + accessToken);
      if (result) {
        // Intent i = new Intent(MainActivity.this, TaskListActivity.class);
        // startActivity(i);
        finish();
      } else {
        if (!TextUtils.isEmpty(this.accessToken)) {
          Log.d(TAG, "accessToken != null");
          Intent i = new Intent(MainActivity.this, SignUpActivity.class);
          i.putExtra(Constant.FULL_NAME, this.fullName);
          i.putExtra(Constant.USERNAME, this.username);
          i.putExtra(Constant.FB_ACCESSTOKEN, this.accessToken);
          startActivity(i);
          finish();
        } else {
          loginError.setText("Login failed. Incorrect username/password.");
        }
      }
    }
  }
}
