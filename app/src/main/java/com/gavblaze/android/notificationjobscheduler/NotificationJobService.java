package com.gavblaze.android.notificationjobscheduler;

import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;
import android.widget.Toast;

public class NotificationJobService extends JobService {

    private static final String LOG_TAG = NotificationJobService.class.getSimpleName();

    NotificationManager mNotifyManager;
    // Notification channel ID.
    private static final String CHANNEL_ID =
            "primary_notification_channel";

    private static final int NOTIFICATION_ID = 333;

    private MyAsyncTask task = null;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.i(LOG_TAG, "TEST......... onStartJob() called");

        task = new MyAsyncTask(this) {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                jobFinished(jobParameters, false);
                Log.i(LOG_TAG, "TEST.........Job finished!");
            }
        };

        task.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {

        if (task != null) {
            task.cancel(true);
        }

        Log.i(LOG_TAG, "TEST.........onStopJob() called");
        Toast.makeText(this, "Job Failed!", Toast.LENGTH_SHORT).show();
        return true;
    }
}
