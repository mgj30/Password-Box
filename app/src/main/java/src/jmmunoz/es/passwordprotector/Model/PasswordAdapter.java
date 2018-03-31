package src.jmmunoz.es.passwordprotector.Model;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import src.jmmunoz.es.passwordprotector.EditPasswordActivity;
import src.jmmunoz.es.passwordprotector.LoginActivity;
import src.jmmunoz.es.passwordprotector.MainActivity;
import src.jmmunoz.es.passwordprotector.R;
import src.jmmunoz.es.passwordprotector.Utils.EncodeDecode;
import src.jmmunoz.es.passwordprotector.Utils.FilePasswordManager;

/**
 * Created by mgj30 on 19/03/2018.
 */



public class PasswordAdapter extends ArrayAdapter<Password> {

    private ViewHolder viewHolder;
    private Context ctx;
    private FilePasswordManager fm;
    private EncodeDecode decoder;
    private PasswordRepository rep;
    private int id_view;


    private static class ViewHolder {
        private TextView nombre;
    }

    public PasswordAdapter(Context context, int textViewResourceId, FilePasswordManager fm,PasswordRepository rep) {
        super(context, textViewResourceId, rep.getPasswordList());
        this.ctx=context;
        this.fm=fm;
        this.decoder= new EncodeDecode();
        this.rep=rep;
        this.id_view=textViewResourceId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nombre = (TextView) convertView.findViewById(R.id.nombre);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Password item = getItem(position);
        if (item!= null) {

            SpannableString content_nombre = new SpannableString(item.getPassword_name());
            content_nombre.setSpan(new UnderlineSpan(), 0, content_nombre.length(), 0);
            viewHolder.nombre.setText(content_nombre);
            viewHolder.nombre.setId(item.getPassword_id());
            viewHolder.nombre.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    TextView texto = (TextView) view;
                    Intent intent = new Intent(ctx, EditPasswordActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Bundle b = new Bundle();
                    b.putString("repositori_pass", rep.getRepositoryCode()); //Your id
                    b.putString("repositori_user", rep.getRepository_user()); //Your id
                    b.putString("repositori_file", rep.getRepository_user()+ ".keys"); //Your id
                    b.putInt("item_edit", texto.getId()); //Your id
                    intent.putExtras(b); //Put your id to your next Intent
                    ctx.startActivity(intent);
                    ((Activity)ctx).finish();
                }
            });
        }
        return convertView;
    }
}