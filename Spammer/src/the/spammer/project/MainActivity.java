
package the.spammer.project;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String CLASS_NAME = MainActivity.class.getName();
	
	private static final String LOG_TAG    = MainActivity.CLASS_NAME;
	
	@Override
	public void onConfigurationChanged(final Configuration newConfig) {

		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.main);
		//
		Log.i(MainActivity.LOG_TAG, "Spammer is ready to spam...");
		//
		final EditText p = ((EditText) this.findViewById(R.id.phone));
		final EditText m = ((EditText) this.findViewById(R.id.message));
		//
		final Button start = (Button) this.findViewById(R.id.start);
		start.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {

				final String victim = p.getText().toString();
				final String message = m.getText().toString();
				//
				if (TextUtils.isEmpty(victim) || TextUtils.isEmpty(message)) {
					Toast.makeText(Spammer.getAppContext(), "Phone and message shouldn't be empty.", Toast.LENGTH_SHORT).show();
				} else {
					Engine.getInstance().rezume(victim, message);
				}
			}
		});
		//
		final Button stop = (Button) this.findViewById(R.id.stop);
		stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(final View view) {

				Engine.getInstance().pause();
			}
		});
	}
}
