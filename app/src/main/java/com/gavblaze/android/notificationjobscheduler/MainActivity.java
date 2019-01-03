package com.gavblaze.android.notificationjobscheduler;


import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private RadioGroup mRadioGroup;
    private JobScheduler mJobScheduler;
    private Switch mDeviceIdleSwitch;
    private Switch mDeviceChargingSwitch;
    //Override deadline seekbar
    private SeekBar mSeekBar;
    private static final int JOB_ID = 0;
    public static boolean constraintSet;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mRadioGroup = findViewById(R.id.networkOptions);

        mDeviceIdleSwitch = findViewById(R.id.deviceIdleSwitch);
        mDeviceChargingSwitch = findViewById(R.id.deviceChargingSwitch);
        mSeekBar = findViewById(R.id.seekBar);
        final TextView seekBarProgress = findViewById(R.id.seekBarProgress);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(LOG_TAG, "Progress: " + progress);

                // Check that a value has been set by the user first
                if (progress > 0) {
                    seekBarProgress.setText(progress + " s");
                } else {
                    seekBarProgress.setText("Not Set");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void scheduleJob(View view) {

        int selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;

        int seekBarInteger = mSeekBar.getProgress();
        boolean seekBarSet = seekBarInteger > 0;

        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.noNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_NONE;
                break;
            case R.id.anyNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_ANY;
                break;
            case R.id.wifiNetwork:
                selectedNetworkOption = JobInfo.NETWORK_TYPE_UNMETERED;
                break;
        }

        mJobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        ComponentName serviceName = new ComponentName(getPackageName(),
                NotificationJobService.class.getName());

        constraintSet = selectedNetworkOption
                != JobInfo.NETWORK_TYPE_NONE
                || mDeviceIdleSwitch.isChecked()
                || mDeviceChargingSwitch.isChecked()
                || seekBarSet;

        if (constraintSet) {

            JobInfo.Builder builder = new JobInfo.Builder(JOB_ID, serviceName)
                    .setRequiredNetworkType(selectedNetworkOption)
                    .setRequiresCharging(mDeviceChargingSwitch.isChecked())
                    .setRequiresDeviceIdle(mDeviceIdleSwitch.isChecked());

            if (seekBarSet) {
                builder.setOverrideDeadline(seekBarInteger * 1000);
            }


            JobInfo myJobInfo = builder.build();
            mJobScheduler.schedule(myJobInfo);

            Toast.makeText(this, "Job Scheduled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Please set a constraint.", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelJob(View view) {
        if (mJobScheduler != null) {
            mJobScheduler.cancelAll();
            mJobScheduler = null;
            Toast.makeText(this, "Job Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}
