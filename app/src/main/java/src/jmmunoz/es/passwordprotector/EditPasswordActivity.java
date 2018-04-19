package src.jmmunoz.es.passwordprotector;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import java.util.Random;
import src.jmmunoz.es.passwordprotector.Model.Password;
import src.jmmunoz.es.passwordprotector.Model.PasswordRepository;
import src.jmmunoz.es.passwordprotector.Utils.Constants;
import src.jmmunoz.es.passwordprotector.Utils.EncodeDecode;
import src.jmmunoz.es.passwordprotector.Utils.FilePasswordManager;
import src.jmmunoz.es.passwordprotector.Utils.MyCountDownTimer;

public class EditPasswordActivity extends AppCompatActivity {

    private PasswordRepository rep;
    private FilePasswordManager fm;
    private EncodeDecode decoder;
    private String repositori_pass;
    private String repositori_user;
    private String repositori_file;
    public EditText nombre_text;
    public EditText url_text;
    public EditText usuario_text;
    public TextInputEditText password_text;
    public ImageButton name_copy;
    public ImageButton url_copy;
    public ImageButton user_copy;
    public ImageButton password_copy;
    public Button button_save ;
    public Button delete_button ;
    public Button cancel_button ;
    public Button generate ;
    public Password p;
    public Password p_param;
    private AdView mAdView;

    private MyCountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_password);

        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        if(Constants.PUBLICIDAD) {
            MobileAds.initialize(this, "ca-app-pub-2198662666880421~4644250735");


        }

        mAdView = findViewById(R.id.adView_password);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        rep = new PasswordRepository();
        fm = new FilePasswordManager(getBaseContext());
        decoder = new EncodeDecode();
        Bundle b = getIntent().getExtras();
        if(b != null) {
            repositori_pass = b.getString("repositori_pass");
            repositori_user = b.getString("repositori_user");
            repositori_file = b.getString("repositori_file");
            p_param = (Password) b.getSerializable("password");

        }
        CargarRepositorio();


        if(p_param!=null && p_param.getId_padre()==0 && p_param.getPassword_type()==Password.TYPE_ITEM) {
            p = rep.getPasswordById(p_param.getPassword_id());
        }

        if(p_param!=null && p_param.getId_padre()!=0 && p_param.getPassword_type()==Password.TYPE_ITEM) {
            p = rep.getPasswordInGroupById(p_param.getId_padre(), p_param.getPassword_id());
        }


        nombre_text = (EditText) findViewById(R.id.nombre_text);
        url_text = (EditText) findViewById(R.id.url_text);
        usuario_text = (EditText) findViewById(R.id.usuario_text);
        password_text = (TextInputEditText) findViewById(R.id.password_text);

        name_copy = (ImageButton)findViewById(R.id.name_copy);
        name_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copiar(p.getPassword_name(),getResources().getString(R.string.name_label));
            }
        });
        url_copy = (ImageButton)findViewById(R.id.url_copy);
        url_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copiar(p.getPassword_url(),getResources().getString(R.string.url_label));
            }
        });
        user_copy = (ImageButton)findViewById(R.id.user_copy);
        user_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copiar(p.getPassword_user(),getResources().getString(R.string.user_label));
            }
        });
        password_copy = (ImageButton)findViewById(R.id.password_copy);
        password_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                copiar(p.getPassword_value(),getResources().getString(R.string.password_label));
            }
        });


        if(p!=null) {
            nombre_text.setText(p.getPassword_name());
            url_text.setText(p.getPassword_url());
            usuario_text.setText(p.getPassword_user());
            password_text.setText(p.getPassword_value());
        }


        cancel_button = (Button) findViewById(R.id.button_cancel);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        generate= (Button) findViewById(R.id.random_password);
        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditPasswordActivity.this, GenerateActivity.class);
                startActivityForResult(intent,1);
            }
        });

        button_save =(Button) findViewById(R.id.button_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Password p=null;

                    if(p_param!=null && p_param.getId_padre()==0 && p_param.getPassword_type()==Password.TYPE_ITEM) {
                        p = rep.getPasswordById(p_param.getPassword_id());
                    }

                    if(p_param!=null && p_param.getId_padre()!=0 && p_param.getPassword_type()==Password.TYPE_ITEM) {
                        p = rep.getPasswordInGroupById(p_param.getId_padre(), p_param.getPassword_id());
                    }


                    if(p==null) {
                        p = new Password();


                        if(p_param!=null) {
                            p.setId_padre(p_param.getPassword_id());
                            p.setPassword_id(rep.getNewIdFromGroup(p_param.getPassword_id()));
                        }else{
                            p.setPassword_id(rep.getNewId());
                        }
                    }


                    p.setPassword_type(Password.TYPE_ITEM);

                    p.setPassword_name(nombre_text.getText().toString());
                    p.setPassword_url(url_text.getText().toString());
                    p.setPassword_user(usuario_text.getText().toString());
                    if(!password_text.getText().toString().equalsIgnoreCase("**********"))
                        p.setPassword_value(password_text.getText().toString());




                    if(p.getId_padre()==0) {
                        rep.updatePassword(p);
                    }else {
                        rep.updatePasswordInGroup(p.getId_padre(),p);
                    }

                    fm.setFileContent(
                            rep.getRepository_user() + ".keys",
                            decoder.encrypt(rep.toJson(), rep.getRepositoryCode())
                    );
                    Intent intent = new Intent(EditPasswordActivity.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putString("repositori_pass", rep.getRepositoryCode().toString());
                    b.putString("repositori_user", rep.getRepository_user().toString());
                    b.putString("repositori_file", rep.getRepository_user().toString()+ ".keys");

                    if(p.getId_padre()!=0){
                        Password padre = rep.getPasswordById(p_param.getId_padre());
                        b.putSerializable("password", padre);
                    }


                    intent.putExtras(b); //Put your id to your next Intent
                    startActivity(intent);
                    setResult(RESULT_OK,intent);
                    finish();



                }catch (Exception e){
                    e.printStackTrace();
                }


            }
        });

        delete_button =(Button) findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    borrarPassword();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        countDownTimer = new MyCountDownTimer(Constants.END_TIME, Constants.INTERVAL,this,rep);

    }



    private void borrarPassword() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.delete_title);
        builder.setMessage(R.string.delete_message);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                try {

                    if(p_param.getId_padre()==0) {
                        rep.deleteItem(p_param.getPassword_id());
                    }else {
                        rep.deleteItemInGroup(p_param.getId_padre(),p.getPassword_id());
                    }

                    fm.setFileContent(
                            rep.getRepository_user() + ".keys",
                            decoder.encrypt(rep.toJson(), rep.getRepositoryCode())
                    );

                    Intent intent = new Intent(EditPasswordActivity.this, MainActivity.class);
                    Bundle b = new Bundle();
                    b.putString("repositori_pass", rep.getRepositoryCode().toString());
                    b.putString("repositori_user", rep.getRepository_user().toString());
                    b.putString("repositori_file", rep.getRepository_user().toString()+ ".keys");
                    intent.putExtras(b);
                    setResult(RESULT_OK,intent);
                    startActivity(intent);
                    finish();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void copiar(String texto,String mensaje){
        ClipData clip = ClipData.newPlainText(texto,texto);
        ClipboardManager clipboard = (ClipboardManager)this.getSystemService(CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this, mensaje+" "+getResources().getString(R.string.clip_text), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
            cerrar();
    }

    public void cerrar(){
        Intent intent = new Intent(EditPasswordActivity.this, MainActivity.class);
        Bundle b = new Bundle();
        b.putString("repositori_pass", rep.getRepositoryCode().toString());
        b.putString("repositori_user", rep.getRepository_user().toString());
        b.putString("repositori_file", rep.getRepository_user().toString()+ ".keys");
        if(p_param!=null && p_param.getId_padre()!=0){
            Password padre = rep.getPasswordById(p_param.getId_padre());
            b.putSerializable("password", padre);
        }
        intent.putExtras(b);
        setResult(RESULT_OK,intent);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUserInteraction(){
        super.onUserInteraction();

        //Reset the timer on user interaction...
        countDownTimer.cancel();
        countDownTimer.start();
    }

    private void CargarRepositorio(){
        try {
            Gson gson = new Gson(); // Or use new GsonBuilder().create();
            rep = gson.fromJson(decoder.decrypt(
                    fm.getFileContent(repositori_file),
                    repositori_pass), PasswordRepository.class);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String returnedResult = data.getData().toString();
                password_text.setText(returnedResult);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
