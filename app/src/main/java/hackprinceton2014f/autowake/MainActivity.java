package hackprinceton2014f.autowake;

import android.app.Activity;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final UUID WATCH_APP_UUID = UUID.fromString("5e15e66b-fcc3-4546-8ec2-826c5265b08d");

    private ListView listView;
    private PebbleDictionary data = new PebbleDictionary();
    private boolean pebbleAppIsOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets up the list view
        setupListView();

        // Sets up the drop down menu
        //setupSpinner();

        // Sets up the sensitivity seekbar
        setupSensitivityBar();

        // Sets up the time delay seekbar
        setupDelayBar();

        // Sets up receiving data from the Pebble
        setupReceiveData();
    }

    public void onLaunchClick(View view) {
        if (PebbleKit.isWatchConnected(getApplicationContext())) {
            Button launchButton = (Button) findViewById(R.id.launchButton);

            if (!pebbleAppIsOpen) {
                launchButton.setText("Close app");

                // Launching my app
                PebbleKit.startAppOnPebble(getApplicationContext(), WATCH_APP_UUID);

                pebbleAppIsOpen = true;
            } else {
                launchButton.setText("Open app");

                // Closing my app
                PebbleKit.closeAppOnPebble(getApplicationContext(), WATCH_APP_UUID);

                pebbleAppIsOpen = false;
            }
        } else {
            Context context = getApplicationContext();
            CharSequence text = "Please connect your Pebble smartwatch to your phone!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void setupListView() {
        listView = (ListView) findViewById(R.id.list);

        String[] values = {"Vibration", "Notification Sound"};

        // Context, layout for the row, ID of the TextView to which the data is written, the Array of data
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, values);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setItemChecked(0, true);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int wantedPosition = position; // Whatever position you're looking for
                int firstPosition = listView.getFirstVisiblePosition() - listView.getHeaderViewsCount(); // This is the same as child #0
                int wantedChild = wantedPosition - firstPosition;

                if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
                    Log.w(TAG, "Unable to get view for desired position, because it's not being displayed on screen.");
                    return;
                }
                // Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.
                View wantedView = listView.getChildAt(wantedChild);

                if (wantedPosition == 0) {
                    if (((CheckedTextView) wantedView).isChecked()) {
                        // Set vibrate toggle to 1
                        data.addUint32('v', 1);

                        Log.d(TAG, "Vibrate toggle is on");
                    } else {
                        // Set vibrate toggle to 0
                        data.addUint32('v', 0);

                        Log.d(TAG, "Vibrate toggle is off");
                    }
                } else if (wantedPosition == 1) {
                    if (((CheckedTextView) wantedView).isChecked()) {
                        // Set sound toggle to 1
                        data.addUint32('s', 1);

                        Log.d(TAG, "Sound toggle is on");
                    } else {
                        // Set sound toggle to 0
                        data.addUint32('s', 0);

                        Log.d(TAG, "Sound toggle is off");
                    }
                }

                // Send data to the pebble
                PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);
            }
        });
    }

    public void setupSensitivityBar() {
        SeekBar sensitivityBar = (SeekBar) findViewById(R.id.sensitivityBar);
        TextView sensitivityValue = (TextView) findViewById(R.id.sensitivityValue);
        sensitivityValue.setText("(8 units)");

        // Values are divided by 25
        sensitivityBar.setProgress(8);
        sensitivityBar.incrementProgressBy(1);
        sensitivityBar.setMax(16);

        sensitivityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update value of progress
                this.progress = progress;

                // Update the shown value of sensitivity value
                TextView sensitivityValue = (TextView) findViewById(R.id.sensitivityValue);
                int value = progress + 1;
                sensitivityValue.setText("(" + Integer.toString(value) + " units)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Set progress of sensitivity
                data.addUint32('e', progress * 25);

                // Send the data to the pebble
                PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);
                Log.d(TAG, "Sensitivity changed to " + (progress * 25));
            }
        });
    }

    public void setupDelayBar() {
        SeekBar delayBar = (SeekBar) findViewById(R.id.delayBar);
        TextView delayValue = (TextView) findViewById(R.id.delayValue);
        delayValue.setText("(20 seconds)");

        // Values are divided by 5
        delayBar.setProgress(4);
        delayBar.incrementProgressBy(1);
        delayBar.setMax(35);

        delayBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update value of progress
                this.progress = progress;

                // Update the shown value of delay value
                TextView delayValue = (TextView) findViewById(R.id.delayValue);
                int value = (progress + 1) * 5;
                delayValue.setText("(" + Integer.toString(value) + " seconds)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Set progress of delay bar
                data.addUint32('d', (progress + 1) * 5);

                // Send the data to the pebble
                PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);

                Log.d(TAG, "Time delay changed to " + ((progress + 1) * 5));
            }
        });
    }

    public void setupReceiveData() {
        PebbleKit.registerReceivedDataHandler(this, new PebbleKit.PebbleDataReceiver(WATCH_APP_UUID) {

            @Override
            public void receiveData(final Context context, final int transactionId, final PebbleDictionary data) {
                long duration = data.getInteger('r');
                Log.d(TAG, "Received value=" + duration + " for key: 0");

                // Acknowledge the message
                PebbleKit.sendAckToPebble(getApplicationContext(), transactionId);

                try {
                    Uri alarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    final Ringtone ring = RingtoneManager.getRingtone(getApplicationContext(), alarm);
                    ring.play();

                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            ring.stop();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, duration);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}