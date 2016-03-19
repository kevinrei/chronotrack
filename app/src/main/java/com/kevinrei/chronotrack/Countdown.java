package com.kevinrei.chronotrack;

import android.os.CountDownTimer;
import android.widget.TextView;

public class Countdown {

    /** Day & Time */
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private TextView mTextView;
    private CountDownTimer mCountdown;

    public Countdown(TextView mTextView) {
        this.mTextView = mTextView;
    }

    public void updateTextView(Alarm alarm) {
        cleanCountdown();
        long endTime = alarm.getCountdown();

        processCountdownAndUpdate(mTextView, endTime);
    }

    private void processCountdownAndUpdate(final TextView mTextView, long endTime) {
        if (endTime > System.currentTimeMillis()) {
            long duration = getDuration(endTime);
            if (duration != 0) {
                startCountdown(mTextView, duration);
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

    private void startCountdown(final TextView mTextView, long duration) {
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

                // Only show seconds when less than a minute
                else if (millisUntilFinished >= SECOND) {
                    value = millisUntilFinished / SECOND;
                    if (value == 1) {
                        trigger.append(millisUntilFinished / SECOND).append( "second" );
                    } else {
                        trigger.append(millisUntilFinished / SECOND).append(" seconds ");
                    }
                }

                mTextView.setText(trigger.toString());
            }

            @Override
            public void onFinish() {
                mTextView.setText("Finished!");
            }
        }.start();
    }

    public void cleanCountdown() {
        if (mCountdown != null) {
            mCountdown.cancel();
        }
    }
}