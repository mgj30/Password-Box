package src.jmmunoz.es.passwordprotector.Model;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import src.jmmunoz.es.passwordprotector.EditGroupActivity;
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
    private int item_group;


    private static class ViewHolder {
        private ImageView image;
        private ImageView edit;
        private TextView elements;
        private TextView nombre;
    }

    public PasswordAdapter(Context context, int textViewResourceId, FilePasswordManager fm,PasswordRepository rep, int item_group) {

        super(context, textViewResourceId, rep.getListOrGroup(item_group));
        this.item_group=item_group;
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
            viewHolder.image = (ImageView) convertView.findViewById(R.id.imagen);
            viewHolder.elements = (TextView) convertView.findViewById(R.id.elements);
            viewHolder.edit= (ImageView) convertView.findViewById(R.id.edit);
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

            String password_img = "@android:drawable/ic_lock_idle_lock";
            String group_img = "@android:drawable/ic_menu_sort_by_size";


            if(item.getPassword_type()==Password.TYPE_GROUP) {
                Drawable res = ctx.getResources().getDrawable(ctx.getResources().getIdentifier(group_img, null, ctx.getPackageName()));
                viewHolder.image.setImageDrawable(res);
                viewHolder.elements.setText(item.getLista_password().size()+" - " + ctx.getResources().getString(R.string.elementos));

            }
            if(item.getPassword_type()==Password.TYPE_ITEM){
                Drawable res = ctx.getResources().getDrawable(ctx.getResources().getIdentifier(password_img, null, ctx.getPackageName()));
                viewHolder.image.setImageDrawable(res);
                viewHolder.elements.setText("");
            }

            convertView.setId(item.getPassword_id());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    LinearLayout texto = (LinearLayout) view;

                    Password p = null;

                    if(item_group==0) {
                        p = rep.getPasswordById(texto.getId());

                        if(p.getPassword_type()==Password.TYPE_ITEM)
                            goToId(p);

                        if(p.getPassword_type()==Password.TYPE_GROUP)
                            goToGroup(p);

                    }else {
                        p = rep.getPasswordInGroupById(item_group, texto.getId());
                        goToId(p);
                    }

                }
            });

            viewHolder.edit.setId(item.getPassword_id());
            viewHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    ImageView texto = (ImageView) view;
                    Password p = null;
                    if(item_group==0) {
                        p = rep.getPasswordById(texto.getId());

                        if(p.getPassword_type()==Password.TYPE_GROUP)
                            editGroup(p);

                        if(p.getPassword_type()==Password.TYPE_ITEM)
                            goToId(p);

                    }else {
                        p = rep.getPasswordInGroupById(item_group, texto.getId());

                        if(p.getPassword_type()==Password.TYPE_GROUP)
                            editGroup(p);

                        if(p.getPassword_type()==Password.TYPE_ITEM)
                            goToId(p);
                    }

                }
            });
        }
        return convertView;
    }

    public void goToId(Password p){
        Intent intent = new Intent(ctx, EditPasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle b = new Bundle();
        b.putString("repositori_pass", rep.getRepositoryCode()); //Your id
        b.putString("repositori_user", rep.getRepository_user()); //Your id
        b.putString("repositori_file", rep.getRepository_user()+ ".keys"); //Your id
        b.putSerializable("password",p);
        intent.putExtras(b); //Put your id to your next Intent
        ctx.startActivity(intent);
        ((Activity)ctx).finish();
    }

    public void editGroup(Password p){
        Intent intent = new Intent(ctx, EditGroupActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle b = new Bundle();
        b.putString("repositori_pass", rep.getRepositoryCode()); //Your id
        b.putString("repositori_user", rep.getRepository_user()); //Your id
        b.putString("repositori_file", rep.getRepository_user() + ".keys"); //Your id
        b.putSerializable("password",p);
        intent.putExtras(b); //Put your id to your next Intent
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }

    public void goToGroup(Password p){
        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle b = new Bundle();
        b.putString("repositori_pass", rep.getRepositoryCode()); //Your id
        b.putString("repositori_user", rep.getRepository_user()); //Your id
        b.putString("repositori_file", rep.getRepository_user() + ".keys"); //Your id
        b.putSerializable("password",p);
        intent.putExtras(b); //Put your id to your next Intent
        ctx.startActivity(intent);
        ((Activity) ctx).finish();
    }
}