package com.kevinrei.chronotrack;

import android.os.CountDownTimer;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Countdown {

    /** Day & Time */
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private TextView mText;
    private CountDownTimer mCountdown;

    public Countdown(TextView mText) {
        this.mText = mText;
    }

    public void updateTextAndProgress(Alarm alarm) {
        cleanCountdown();
        long endTime = alarm.getCountdown();

        processCountdownAndUpdate(mText, endTime);
    }

    private void processCountdownAndUpdate(final TextView mText,long endTime) {
        if (endTime > System.currentTimeMillis()) {
            long duration = getDuration(endTime);

            if (duration != 0) {
                startCountdown(mText, duration);
            }
        }
    }

    private long getDuration(long endTime) {
        long duration = 0;

        if (endTime > System.currentTimeMillis()) {
            duration = endTime - System.currentTimeMillis();
        }

        return duration;
    }

    private void startCountdown(final TextView mText, long duration) {
        mCountdown = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                StringBuilder trigger = new StringBuilder("");
                long value;

                if (millisUntilFinished >= DAY) {
                    value = millisUntilFinished / DAY;
                    if (value == 1) {
                        trigger.append(millisUntilFinished / DAY).append(" day ");
                    } else {
                        trigger.append(millisUntilFinished / DAY).append(" days ");
                    }
                    millisUntilFinished %= DAY;
                }

                if (millisUntilFinished >= HOUR) {
                    value = millisUntilFinished / HOUR;
                    if (value == 1) {
                        trigger.append(millisUntilFinished / HOUR).append(" hour ");
                    } else {
                        trigger.append(millisUntilFinished / HOUR).append(" hours ");
                    }
                    millisUntilFinished %= HOUR;
                }

                if (millisUntilFinished >= MINUTE) {
                    value = millisUntilFinished / MINUTE;
                    if (value == 1) {
                        trigger.append(millisUntilFinished / MINUTE).append(" minute ");
                    } else {
                        trigger.append(millisUntilFinished / MINUTE).append(" minutes ");
                    }
                }

                if (millisUntilFinished >= SECOND) {
                    value = millisUntilFinished / SECOND;
                    if (value == 1) {
                        trigger.append(millisUntilFinished / SECOND).append( "second" );
                    } else {
                        trigger.append(millisUntilFinished / SECOND).append(" seconds ");
                    }
                }

                mText.setText(trigger.toString());
            }

            @Override
            public void onFinish() {
                mText.setText("Finished!");
            }
        }.start();
    }

    public void cleanCountdown() {
        if (mCountdown != null) {
            mCountdown.cancel();
        }
    }
}