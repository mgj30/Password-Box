package src.jmmunoz.es.passwordprotector;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import src.jmmunoz.es.passwordprotector.Model.Password;
import src.jmmunoz.es.passwordprotector.Model.PasswordRepository;
import src.jmmunoz.es.passwordprotector.Utils.EncodeDecode;
import src.jmmunoz.es.passwordprotector.Utils.FilePasswordManager;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {


    private FilePasswordManager fm;
    private EncodeDecode decoder;
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private String usuario;
    private String password;

    public Button login_button;
    public Button register_button;
    public Button how_work;

    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MobileAds.initialize(this, "ca-app-pub-2198662666880421~4644250735");
        LoadPreferences();
        fm= new FilePasswordManager(getApplicationContext());
        decoder=new EncodeDecode();
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mEmailView.setText(usuario);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setText(password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        login_button = (Button) findViewById(R.id.login_button);
        login_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        register_button = (Button) findViewById(R.id.register_button);
        register_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                registrar();
            }
        });
        how_work= (Button) findViewById(R.id.how_work);
        how_work.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, HowWorkActivity.class);
                startActivity(intent);
            }
        });


        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        mAdView = findViewById(R.id.adView_login);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    private void LoadPreferences(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //SharedPreferences sharedPref = getApplicationContext().getSharedPreferences("mis_preferencias",Context.MODE_PRIVATE);

        usuario = preferences.getString("usuario", "");
        //password = sharedPref.getString("password", "");

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password,getApplicationContext());
            mAuthTask.execute((Void) null);
        }
    }

    private void registrar(){
        try {
            //comprobamos si no existe un archivo de repositorio
            if (!fm.existFile(mEmailView.getText().toString() + ".keys")) {
                PasswordRepository rep = new PasswordRepository();
                rep.setRepository_user(mEmailView.getText().toString());
                rep.setRepositoryCode(mPasswordView.getText().toString());

                rep.addPasswordToRepository(
                        fm.getPath(mEmailView.getText().toString() + ".keys"),
                        getResources().getString(R.string.default_password_name),
                        getResources().getString(R.string.default_password_name),
                        mEmailView.getText().toString(),
                        mPasswordView.getText().toString());

                fm.setFileContent(
                        mEmailView.getText().toString() + ".keys",
                        decoder.encrypt(rep.toJson(), mPasswordView.getText().toString()));

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("repositori_pass", mPasswordView.getText().toString()); //Your id
                b.putString("repositori_user", mEmailView.getText().toString()); //Your id
                b.putString("repositori_file", mEmailView.getText().toString()+ ".keys"); //Your id

                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();

            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getResources().getString(R.string.dialog_login_message))
                        .setTitle(getResources().getString(R.string.dialog_login_title));
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }catch (Exception e){

        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private FilePasswordManager fm;
        private EncodeDecode decoder;
        private final String mEmail;
        private final String mPassword;
        private Context ctx;

        UserLoginTask(String email, String password, Context context) {
            mEmail = email;
            mPassword = password;
            fm=new FilePasswordManager(context);
            decoder=new EncodeDecode();
            ctx=context;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            try {
                if (fm.existFile(mEmail + ".keys")) {
                    Gson g = new Gson();
                    PasswordRepository rep = g.fromJson(
                            decoder.decrypt(fm.getFileContent(mEmail + ".keys"), mPassword),PasswordRepository.class);

                    if(rep==null) {
                        return false;
                    }else {

                        //reiniciamos los id
                        int i=1;
                        for(Password p:rep.getPasswordList()){
                            p.setPassword_id(i);
                            i++;
                        }

                        fm.setFileContent(
                                mEmail + ".keys",
                                decoder.encrypt(rep.toJson(), mPasswordView.getText().toString()));



                        return true;
                    }

                } else {
                    return false;
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                //SharedPreferences sharedPref = ctx.getSharedPreferences("mis_preferencias",Context.MODE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("usuario", mEmail);
                //editor.putString("password", mPassword);
                editor.apply();

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putString("repositori_pass", mPassword); //Your id
                b.putString("repositori_user", mEmail); //Your id
                b.putString("repositori_file", mEmail+ ".keys"); //Your id
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

