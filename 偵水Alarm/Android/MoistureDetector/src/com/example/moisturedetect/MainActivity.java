package com.example.moisturedetect;

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

import android.graphics.Color;
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

import com.example.moisturealarm.R;
import com.hereapps.rainbowtree.android.RainbowtreeClient;
import com.hereapps.rainbowtree.android.listener.JsonArrayListener;

public class MainActivity extends ActionBarActivity {
	
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	private static SimpleDateFormat iso8601sdf= new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ROOT);

	Button btnIn;// =(Button)findViewById(R.id.btnIn);	
	
	private int dev_num=1;
    private TextView moisturevalue1;

    private Date[] previousDate = new Date[1];
	private int[] currentMoisture = new int[1];
	private int[] currentAlarm = new int[1];
	private int[] alarmFlag= new int[1]; 
	
		
    String[] dev_id={"45a9b73425904361"};    
	String[] ds_m={"vf7332aa0536711e4"};
	String[] ds_a={"vf12777b0536711e4"};
    
	
	HandlerThread hThread = new HandlerThread("HandlerThread");
    RainbowtreeClient client=new RainbowtreeClient("106.186.30.234","e095f4538bachere");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		alarmFlag[0]=0;
		iso8601sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		
		btnIn =(Button)findViewById(R.id.btnIn);	

		moisturevalue1 = (TextView)findViewById(R.id.moisturevalue1);

	}

	public void onStart() {
        super.onStart();
		btnIn.setOnClickListener(new OnClickListener(){			
			public void onClick(View arg0) {
			    Log.d("felix", "click");
			    //RainbowtreeClient client=new RainbowtreeClient("api.rainbowtree.here-apps.com","f91ad6fd406749b4");
			    
			    String tmp_m;
				Date date1=new Date();
				
				for(int i=0;i<dev_num;i++)
					previousDate[i]=date1;
				
				scheduler.scheduleAtFixedRate(new Runnable() {
				      public void run() {
				        monitoring();
				    	  
				        // If you need update UI, simply do this:
				        runOnUiThread(new Runnable() {
				          public void run() {
				        	  if(currentAlarm[0]==1)
				        	  {
					        	  Log.d("ball","here");
				        		  
				        		  moisturevalue1.setText("Yes");		
				        		  
									if(alarmFlag[0]==0)
									{	
										moisturevalue1.setTextColor(Color.BLACK);
								    	alarmFlag[0]=1;
									}else if(alarmFlag[0]<3)	//the same event
									{		
										moisturevalue1.setTextColor(Color.RED);
										Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
								    	Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);					    
								    	r.play();		
										
										alarmFlag[0]++;	
									}
				        	  }
				        	  else
				        	  {	  
								  moisturevalue1.setTextColor(Color.BLACK);				        		  
				        		  alarmFlag[0]=1;
				        		  moisturevalue1.setText("No");
				        	  }
				          }
	
				        });
				      }
				    }, 0, 1, TimeUnit.SECONDS);				
												
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
		Log.d("felix","RUN every 15 seconds...");
	

			client.getAllDatastreamStatusAsync(dev_id[0], new JsonArrayListener(){
			
				@Override
				public void onPostExecute(int statusCode, String rawString, JSONArray jsonArray) {
					String tmp_h, tmp_t;
					Date datetmp=new Date();	
					int i, j=0;
					
					Log.d("felix", "statusCode="+statusCode);						
					Log.d("felix", "ja="+jsonArray);
					
					
					for(i=0;i<2;i++)
					{
						try
						{						
							if( jsonArray.getJSONObject(i).getString("datastream_id").equals(ds_m[0]))
							{	
								j=i;
//								Log.d("felix", "dsid value"+jsonArray.getJSONObject(i).getInt("current_value"));
								currentMoisture[0]=jsonArray.getJSONObject(i).getInt("current_value");
								datetmp = iso8601sdf.parse(jsonArray.getJSONObject(i).getString("at"));
//								Log.d("felix", "at"+jsonArray.getJSONObject(i).getString("at"));	
							}
						}
						catch(JSONException e) {
							e.printStackTrace();
						}
						catch (ParseException e) {					
							e.printStackTrace();
						}					
					}
					
					
					
					i = 1-j;
					try{
						currentAlarm[0]=jsonArray.getJSONObject(i).getInt("current_value");
//						Log.d("felix", "dsid value"+jsonArray.getJSONObject(i).getInt("current_value"));							
					}
					catch(JSONException e){}
										
					
/*					Log.d("felix", "value=" + currentMoisture[0]);
					Log.d("felix", "alarm=" + currentAlarm[0]);
					Log.d("felix","online_time=" + datetmp);	
					Log.d("felix","pre_time=" + previousDate[0]);
					Log.d("felix", "state "+datetmp.compareTo(previousDate[0]))	;
*/
					if( datetmp.compareTo(previousDate[0]) ==1)
					{
						previousDate[0]=datetmp;
				
					}
				}});
	}
}
	
