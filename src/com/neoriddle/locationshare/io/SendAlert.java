package com.neoriddle.locationshare.io;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Toast;

public class SendAlert extends Activity {

	private static final String SENT_SIGNAL = "SENT";
	private static final String DELIVERED_SIGNAL = "DELIVERED";

	private final String name;
	private final String address;
	private final String localizationUrl;
	private final String phoneNumber;

	public SendAlert(String name, String address, String localizationUrl, String phoneNumber) {
		super();
		this.name = name;
		this.address = address;
		this.localizationUrl = localizationUrl;
		this.phoneNumber = phoneNumber;
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.main);
    }

    public void send() {

    	PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT_SIGNAL), 0);
    	PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED_SIGNAL), 0);

    	String message = name + address + localizationUrl;

    	// Alert after message sent
    	registerReceiver(new BroadcastReceiver(){
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS sent", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU", Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT_SIGNAL));

    	// Alert after message delivered
    	registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered", Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED_SIGNAL));

    	// Get sms manager and send message
    	SmsManager manager = SmsManager.getDefault();
    	manager.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

    }
}