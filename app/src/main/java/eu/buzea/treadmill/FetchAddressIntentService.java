package eu.buzea.treadmill;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class FetchAddressIntentService extends IntentService {
    public static final String LOCATION = "location";
    public static final String RECEIVER = "receiver";
    private ResultReceiver receiver;

    public static void startService(Context context, Location location, ResultReceiver receiver) {
        Intent intent = new Intent(context, FetchAddressIntentService.class);
        intent.putExtra(LOCATION, location);
        intent.putExtra(RECEIVER, receiver);
        context.startService(intent);
    }

    public FetchAddressIntentService() {
        super("FetchAddressIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        Location location = intent.getParcelableExtra(LOCATION);
        receiver = intent.getParcelableExtra(RECEIVER);
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread.
            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            deliverResultToReceiver(
                    TextUtils.join(System.getProperty("line.separator"),
                            addressFragments));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deliverResultToReceiver(String message) {
        Bundle bundle = new Bundle();
        bundle.putString(LOCATION, message);
        receiver.send(RESULT_OK, bundle);
    }
}
