package src.jmmunoz.es.passwordprotector.Utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.IntentCompat;

import src.jmmunoz.es.passwordprotector.EditGroupActivity;
import src.jmmunoz.es.passwordprotector.EditPasswordActivity;
import src.jmmunoz.es.passwordprotector.LoginActivity;
import src.jmmunoz.es.passwordprotector.MainActivity;
import src.jmmunoz.es.passwordprotector.Model.PasswordRepository;

/**
 * Created by mgj30 on 09/04/2018.
 */

public class MyCountDownTimer  extends CountDownTimer {
    public Context ctx;
    public PasswordRepository rep;
    public MyCountDownTimer(long startTime, long interval, Context ctx, PasswordRepository rep) {
        super(startTime, interval);
        this.ctx=ctx;
        this.rep=rep;
    }

    @Override
    public void onFinish() {

       //
        /*Intent intent = new Intent(ctx.getApplicationContext(), LoginActivity.class);
        Bundle b = new Bundle();
        b.putString("close_app", "S");
        intent.putExtras(b);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        ctx.startActivity(mainIntent);*/


        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onTick(long millisUntilFinished) {
    }
}
