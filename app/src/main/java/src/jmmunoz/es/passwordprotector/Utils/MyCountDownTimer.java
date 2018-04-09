package src.jmmunoz.es.passwordprotector.Utils;

import android.os.CountDownTimer;

/**
 * Created by mgj30 on 09/04/2018.
 */

public class MyCountDownTimer  extends CountDownTimer {
    public MyCountDownTimer(long startTime, long interval) {
        super(startTime, interval);
    }

    @Override
    public void onFinish() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onTick(long millisUntilFinished) {
    }
}
