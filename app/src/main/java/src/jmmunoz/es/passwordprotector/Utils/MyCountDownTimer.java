package src.jmmunoz.es.passwordprotector.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

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
        android.os.Process.killProcess(android.os.Process.myPid());
        Intent intent = new Intent(ctx.getApplicationContext(), LoginActivity.class);

        ctx.startActivity(intent);
        ((Activity)ctx).finish();
    }

    @Override
    public void onTick(long millisUntilFinished) {
    }
}
