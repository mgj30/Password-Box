package src.jmmunoz.es.passwordprotector;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;

import java.util.Random;

import src.jmmunoz.es.passwordprotector.Utils.Constants;

public class GenerateActivity extends AppCompatActivity {

    private Button cerrar;
    private Button guardar;
    private Button generate_btn;
    private CheckBox check1;
    private CheckBox check2;
    private CheckBox check3;
    private CheckBox check4;
    private SeekBar seek;

    private TextView textView2;
    private TextView textView5;

    private String password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        if(Constants.PUBLICIDAD) {
            MobileAds.initialize(this, "ca-app-pub-2198662666880421~4644250735");
        }
        iniciarControles();
    }

    public void iniciarControles(){
        textView2=(TextView)  findViewById(R.id.textView2);
        textView5=(TextView)  findViewById(R.id.textView5);

        check1=(CheckBox)findViewById(R.id.checkBox1);
        check1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!check2.isChecked() && !check3.isChecked() && check4.isChecked())
                    check4.setChecked(!check1.isChecked());

                generarPassword();
            }
        });
        check2=(CheckBox)findViewById(R.id.checkBox2);
        check2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!check1.isChecked() && !check3.isChecked() && check4.isChecked())
                    check4.setChecked(!check2.isChecked());

                generarPassword();
            }
        });
        check3=(CheckBox)findViewById(R.id.checkBox3);
        check3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!check1.isChecked() && !check2.isChecked() && check4.isChecked())
                    check4.setChecked(!check3.isChecked());

                generarPassword();
            }
        });
        check4=(CheckBox)findViewById(R.id.checkBox4);
        check4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(check4.isChecked()) {
                    check1.setChecked(!check4.isChecked());
                    check2.setChecked(!check4.isChecked());
                    check3.setChecked(!check4.isChecked());
                }


                generarPassword();
            }
        });


        cerrar=(Button)findViewById(R.id.btncerrar);
        cerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                password="";
                cerrar();
            }
        });


        guardar=(Button)findViewById(R.id.btngrabar);
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_password();
            }
        });

        generate_btn=(Button)findViewById(R.id.generate_btn);
        generate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generarPassword();
            }
        });

        seek=(SeekBar)findViewById(R.id.seekBar);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
                //textView2.setText(progress);
                textView2.setText(String.valueOf(new Integer(progress)));
                generarPassword();
            }
        });
        generarPassword();
    }

    public void save_password(){
        //generarPassword();
        cerrar();
    }

    public void generarPassword(){
        Random rn = new Random();

        int longitud = seek.getProgress();
        password="";
        for(int i=0;i<longitud;i++){
            String caracter="";
            if(check4.isChecked()){
                caracter= getNumber();
            }else {

                int tipo = rn.nextInt(4) + 1;
                if (tipo == 1 && check1.isChecked()) {
                    caracter= getLetter().toUpperCase();
                }
                if (tipo == 2 && check2.isChecked()) {
                    caracter= getSimbol();
                }
                if (tipo == 3 && check3.isChecked()) {
                    caracter= getNumber();
                }
                if (tipo == 4 || caracter.equalsIgnoreCase("")) {
                    caracter= getLetter().toLowerCase();
                }
            }
            password+=caracter;
        }
        textView5.setText(password);
    }

    public String getSimbol(){
        Random rn = new Random();
        String simbols="+-*/!|@#$%&/()=";
        int caracter = rn.nextInt(simbols.length()) ;
        return simbols.substring(caracter,caracter+1);
    }

    public String getLetter(){
        Random rn = new Random();
        String letras = "abcdefghijklmnñopqrstuvwxyz";
        int caracter = rn.nextInt(letras.length()) ;
        return letras.substring(caracter,caracter+1);
    }

    public String getNumber(){
        Random rn = new Random();
        String numeros = "0123456789";
        int caracter = rn.nextInt(numeros.length());
        return numeros.substring(caracter,caracter+1);
    }

    public void cerrar(){
        Intent data = new Intent();
        data.setData(Uri.parse(password));
        setResult(RESULT_OK, data);
        finish();
    }
}
