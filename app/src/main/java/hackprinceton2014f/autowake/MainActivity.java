package hackprinceton2014f.autowake;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.UUID;

import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private static final UUID WATCH_APP_UUID = UUID.fromString("5e15e66b-fcc3-4546-8ec2-826c5265b08d");

    private PebbleDictionary data = new PebbleDictionary();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Gets the interval/accel switch
        Switch aSwitch = (Switch) findViewById(R.id.aSwitch);

        aSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // Set switch to interval
                    data.addUint32('w', 1);
                    // Send the data to the pebble
                    PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);
                    Log.d(TAG, "Switch is set to interval");
                } else {
                    // Set switch to accelerometer
                    data.addUint32('w', 0);
                    // Send the data to the pebble
                    PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);
                    Log.d(TAG, "Switch is set to accelerometer");
                }
            }
        });
    }

    public void onVibrateClick(View view) {
        // Gets the vibrate toggle
        ToggleButton vibrateToggle = (ToggleButton) findViewById(R.id.vibrateToggle);
        // If the vibrate toggle is checked
        if (vibrateToggle.isChecked()) {
            // Set vibrate toggle to 1
            data.addUint32('v', 1);
            // Send the data to the pebble
            PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);
            Log.d(TAG, "Vibrate toggle is on");
        } else {
            // Set vibrate toggle to 0
            data.addUint32('v', 0);
            // Send the data to the pebble
            PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);
            Log.d(TAG, "Vibrate toggle is off");
        }
    }

    public void onSoundClick(View view) {
        // Gets the sound toggle
        ToggleButton soundToggle = (ToggleButton) findViewById(R.id.vibrateToggle);
        // If the sound toggle is checked
        if (soundToggle.isChecked()) {
            // Set sound toggle to 1
            data.addUint32('s', 1);
            // Send the data to the pebble
            PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);
            Log.d(TAG, "Sound toggle is on");
        } else {
            // Set sound toggle to 0
            data.addUint32('s', 0);
            // Send the data to the pebble
            PebbleKit.sendDataToPebble(getApplicationContext(), WATCH_APP_UUID, data);
            Log.d(TAG, "Sound toggle is off");
        }
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
