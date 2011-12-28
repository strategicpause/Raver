package com.raver;
import android.app.Activity;
import android.os.Bundle;

public class Raver extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
    
    @Override
	protected void onPause() {
		super.onPause();
	}
}