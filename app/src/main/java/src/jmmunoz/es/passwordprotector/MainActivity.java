package src.jmmunoz.es.passwordprotector;


import android.app.KeyguardManager;
import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.gson.Gson;
import src.jmmunoz.es.passwordprotector.Model.Password;
import src.jmmunoz.es.passwordprotector.Model.PasswordAdapter;
import src.jmmunoz.es.passwordprotector.Model.PasswordRepository;
import src.jmmunoz.es.passwordprotector.Utils.Constants;
import src.jmmunoz.es.passwordprotector.Utils.EncodeDecode;
import src.jmmunoz.es.passwordprotector.Utils.FilePasswordManager;
import src.jmmunoz.es.passwordprotector.Utils.MyCountDownTimer;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private PasswordRepository rep;
    private FilePasswordManager fm;
    private EncodeDecode decoder;
    private String repositori_pass;
    private String repositori_user;
    private String repositori_file;
    private PasswordAdapter arrayAdapter;

    private Password p_param;
    private MyCountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        countDownTimer = new MyCountDownTimer(Constants.END_TIME, Constants.INTERVAL);
        IniciarVariables();
        CargarRepositorio();
        InicilizarComponentes();


    }


    private void IniciarVariables(){

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

    private void InicilizarComponentes(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(p_param!=null && p_param.getPassword_type()==Password.TYPE_GROUP){
            Menu menuNav=navigationView.getMenu();
            MenuItem nav_item2 = menuNav.findItem(R.id.new_group);
            nav_item2.setEnabled(false);
        }

        cargarLista();

    }

    public void cargarLista(){
        try {

            int id_group=0;

            if(p_param!=null && p_param.getPassword_type()==Password.TYPE_GROUP) {
                id_group = p_param.getPassword_id();
                //actualizar ids de la lista interna del grupo
                for(Password p:rep.getPasswordList()){
                    if(p.getPassword_id()==id_group){
                        for(Password j:p.getLista_password()){
                            j.setId_padre(p.getPassword_id());
                        }
                    }

                }
                fm.setFileContent(
                        repositori_user + ".keys",
                        decoder.encrypt(rep.toJson(), repositori_pass));
            }

            arrayAdapter = new PasswordAdapter(this,R.id.lista_password,fm,rep, id_group);
            ListView lista = (ListView) findViewById(R.id.lista_password);
            lista.setAdapter(arrayAdapter);
            arrayAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(p_param!=null && p_param.getPassword_type()==Password.TYPE_GROUP){
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            Bundle b = new Bundle();
            b.putString("repositori_pass", rep.getRepositoryCode()); //Your id
            b.putString("repositori_user", rep.getRepository_user()); //Your id
            b.putString("repositori_file", rep.getRepository_user()+ ".keys"); //Your id
            //b.putSerializable("password", p_param);
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        }


        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {
            try {
                CargarRepositorio();
                arrayAdapter.clear();

                for (Password p : rep.getPasswordList())
                    arrayAdapter.add(p);

                arrayAdapter.notifyDataSetChanged();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.new_group) {
            // Handle the camera action
            Intent intent = new Intent(MainActivity.this, EditGroupActivity.class);
            Bundle b = new Bundle();
            b.putString("repositori_pass", rep.getRepositoryCode()); //Your id
            b.putString("repositori_user", rep.getRepository_user()); //Your id
            b.putString("repositori_file", rep.getRepository_user()+ ".keys"); //Your id
            //b.putSerializable("password", p_param);
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        } else if (id == R.id.home) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            Bundle b = new Bundle();
            b.putString("repositori_pass", rep.getRepositoryCode()); //Your id
            b.putString("repositori_user", rep.getRepository_user()); //Your id
            b.putString("repositori_file", rep.getRepository_user()+ ".keys"); //Your id
            //b.putSerializable("password", p_param);
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        }else if (id == R.id.new_password) {
            Intent intent = new Intent(MainActivity.this, EditPasswordActivity.class);
            Bundle b = new Bundle();
            b.putString("repositori_pass", rep.getRepositoryCode()); //Your id
            b.putString("repositori_user", rep.getRepository_user()); //Your id
            b.putString("repositori_file", rep.getRepository_user()+ ".keys"); //Your id
            b.putSerializable("password", p_param);

            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
            finish();
        }/* else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    @Override
    public void onUserInteraction(){
        super.onUserInteraction();

        //Reset the timer on user interaction...
        countDownTimer.cancel();
        countDownTimer.start();
    }
}
