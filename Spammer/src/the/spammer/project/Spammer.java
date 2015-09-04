
package the.spammer.project;

import android.app.Application;
import android.content.Context;

public class Spammer extends Application {
	
	@SuppressWarnings("unused")
	private static final String LOG_TAG = Spammer.class.getName();
	
	private static Context	    appContext;
	
	public static Context getAppContext() {
		
		return Spammer.appContext;
	}
	
	public Spammer() {}

	@Override
	public void onCreate() {
		
		super.onCreate();
		Spammer.appContext = this.getApplicationContext();
	}
}
