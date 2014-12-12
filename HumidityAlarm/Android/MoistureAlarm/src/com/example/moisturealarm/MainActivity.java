package com.example.moisturealarm;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.hereapps.rainbowtree.android.RainbowtreeClient;
import com.hereapps.rainbowtree.android.listener.JsonArrayListener;

public class MainActivity extends ActionBarActivity {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static SimpleDateFormat iso8601sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);

	Button btnIn;// =(Button)findViewById(R.id.btnIn);	
	
	private int dev_num=2;
    private TextView hygrovalue1;
    private TextView hygrovalue2;
    private TextView tempvalue1;
    private TextView tempvalue2;
    private Date[] previousDate = new Date[2];
	private double[] currentMoisture = new double[2];
	private double[] currentTemperature = new double[2];
	private int[] currentAlarm = new int[2];
		
    String[] dev_id={"942a4292eec4fc3", "ff7dc74b33a645f8"};    
	String[] ds_h={"vac38b904f7c11e4","vc4be6ab04f7c11e4"};
	String[] ds_t={"va3c78804f7c11e4","vc04650104f7c11e4"};
	String[] ds_a={"vb55dcd404f7c11e4","vc89cdd604f7c11e4"};
    
	
	HandlerThread hThread = new HandlerThread("HandlerThread");
    RainbowtreeClient client=new RainbowtreeClient("106.186.30.234","e095f4538bachere");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		iso8601sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		
		btnIn =(Button)findViewById(R.id.btnIn);	

		hygrovalue1 = (TextView)findViewById(R.id.hygrovalue1);
		hygrovalue2 = (TextView)findViewById(R.id.hygrovalue2);

		tempvalue1 = (TextView)findViewById(R.id.tempvalue1);
		tempvalue2 = (TextView)findViewById(R.id.tempvalue2);
	}

	public void onStart() {
        super.onStart();
		btnIn.setOnClickListener(new OnClickListener(){			
			public void onClick(View arg0) {
			    Log.d("felix", "click");
			    //RainbowtreeClient client=new RainbowtreeClient("api.rainbowtree.here-apps.com","f91ad6fd406749b4");
			    
			    String tmp_h, tmp_t;
				Date date1=new Date();
				
				for(int i=0;i<dev_num;i++)
					previousDate[i]=date1;
				
				scheduler.scheduleAtFixedRate(new Runnable() {
				      public void run() {
				        monitoring();

				        // If you need update UI, simply do this:
				        runOnUiThread(new Runnable() {
				          public void run() {
								tempvalue1.setText(String.valueOf(currentTemperature[0]));	
								hygrovalue1.setText(String.valueOf(currentMoisture[0]));
								tempvalue2.setText(String.valueOf(currentTemperature[1]));	
								hygrovalue2.setText(String.valueOf(currentMoisture[1]));
								tempvalue1.invalidate();
				          }
				        });
				        
				      }
				    }, 0, 10, TimeUnit.SECONDS);				
				
								
				final Date datetmp;						
			}	
		});	        
    }	
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
	
	public void monitoring()
	{
		Log.d("felix","RUN every 10 seconds...");
	

			client.getAllDatastreamStatusAsync(dev_id[0], new JsonArrayListener(){
			
				@Override
				public void onPostExecute(int statusCode, String rawString, JSONArray jsonArray) {
					String tmp_h, tmp_t;
					Date datetmp=new Date();
					int i=0, j=0;
					
					Log.d("felix", "statusCode="+statusCode);
					Log.d("felix", "ja="+jsonArray);
					
					for(i=0;i<3;i++)
					{		
						try
						{						
							if(jsonArray.getJSONObject(i).getString("datastream_id").equals(ds_h[0]))
							{								
								currentMoisture[0]=jsonArray.getJSONObject(i).getDouble("current_value");
								datetmp = iso8601sdf.parse(jsonArray.getJSONObject(i).getString("at"));
							}
							if(jsonArray.getJSONObject(i).getString("datastream_id").equals(ds_t[0]))
							{
								currentTemperature[0]=jsonArray.getJSONObject(i).getDouble("current_value");
							}
							if(jsonArray.getJSONObject(i).getString("datastream_id").equals(ds_a[0]))
							{									
								currentAlarm[0]=jsonArray.getJSONObject(i).getInt("current_value");
							}
						}
						catch(JSONException e) {
							e.printStackTrace();
						}
						catch (ParseException e) {
						
							e.printStackTrace();
						}														
					}
																					
					Log.d("felix", "h"+currentMoisture[0]);
					Log.d("felix", "t"+currentTemperature[0]);
					Log.d("felix", "a"+currentAlarm[0]);
					Log.d("felix","online_time=" + datetmp);	
					Log.d("felix","pre_time=" + previousDate[0]);
					Log.d("felix", "state "+datetmp.compareTo(previousDate[0]))	;
					
					
					if( datetmp.compareTo(previousDate[0]) ==1)
					{
						previousDate[1] = datetmp;
						if((currentAlarm[0]==1)||(currentAlarm[0]==2))
						{
							Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					    	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);					    
					    	r.play();						
						}					
					}
				}});
			client.getAllDatastreamStatusAsync(dev_id[1], new JsonArrayListener(){
				
				@Override
				public void onPostExecute(int statusCode, String rawString, JSONArray jsonArray) {
					String tmp_h, tmp_t;
					int i=0;
					Date datetmp=new Date();	
					
					Log.d("felix", "statusCode="+statusCode);
					Log.d("felix", "ja="+jsonArray);
					
					for(i=0;i<3;i++)
					{		
						try
						{						
							if(jsonArray.getJSONObject(i).getString("datastream_id").equals(ds_h[1]))
							{								
								currentMoisture[1]=jsonArray.getJSONObject(i).getDouble("current_value");
								datetmp = iso8601sdf.parse(jsonArray.getJSONObject(i).getString("at"));
							}
							if(jsonArray.getJSONObject(i).getString("datastream_id").equals(ds_t[1]))
							{
								currentTemperature[1]=jsonArray.getJSONObject(i).getDouble("current_value");
							}
							if(jsonArray.getJSONObject(i).getString("datastream_id").equals(ds_a[1]))
							{									
								currentAlarm[1]=jsonArray.getJSONObject(i).getInt("current_value");
							}
						}
						catch(JSONException e) {
							e.printStackTrace();
						}
						catch (ParseException e) {
						
							e.printStackTrace();
						}														
					}
																					
					Log.d("felix", "h"+currentMoisture[1]);
					Log.d("felix", "t"+currentTemperature[1]);
					Log.d("felix", "a"+currentAlarm[1]);
					Log.d("felix","online_time=" + datetmp);	
					Log.d("felix","pre_time=" + previousDate[1]);
					Log.d("felix", "state "+datetmp.compareTo(previousDate[1]))	;
					
					if( datetmp.compareTo(previousDate[1]) ==1)
					{
						previousDate[1] = datetmp;
						if((currentAlarm[1]==1)||(currentAlarm[1]==2))
						{
							Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
					    	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);					    
					    	r.play();						
						}					
					}
				}});	
	}
}
	
