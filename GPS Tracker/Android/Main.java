/*
 * Copyright 2014 Akexorcist
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
package app.akexorcist.bluetoothsppdevicelist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import app.akexorcist.bluetoothspp.BluetoothSPP;
import app.akexorcist.bluetoothspp.BluetoothSPP.OnDataReceivedListener;
import app.akexorcist.bluetoothspp.BluetoothState;
import app.akexorcist.bluetoothspp.DeviceList;

import com.hereapps.rainbowtree.android.PostExecuteListener;
import com.hereapps.rainbowtree.android.RainbowtreeClient;



public class Main extends Activity {
	

    private FileOutputStream fos = null;
//    BufferedReader buf = new BufferedReader(new FileReader("file.java"));
    String fpath = Environment.getExternalStorageDirectory().getPath();
    private String filename;
    private int filesize;
    private int cnt;			//count for transferred bytes
    private int rxStatus=0;		//0:INIT_COMMAND file name, size	1:FILE_CONTENT 
       
		
	private TextView Statusvalu1;

	private static SimpleDateFormat sdf;
		
	BluetoothSPP bt;
	RainbowtreeClient client=new RainbowtreeClient("106.186.30.234","e095f4538bachere");
	HashMap map=new HashMap();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		bt = new BluetoothSPP(this);

		if(!bt.isBluetoothAvailable()) {
			Toast.makeText(getApplicationContext()
					, "Bluetooth is not available"
					, Toast.LENGTH_SHORT).show();
            finish();
		}
		
		Button btnConnect = (Button)findViewById(R.id.btnConnect);
		btnConnect.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		if(bt.getServiceState() == BluetoothState.STATE_CONNECTED) {
        			bt.disconnect();
        			//Log.d("ball", "in ");
        		} else {
                    Intent intent = new Intent(Main.this, DeviceList.class);
                    intent.putExtra("bluetooth_devices", "Bluetooth devices");
                    intent.putExtra("no_devices_found", "No device");
                    intent.putExtra("scanning", "scanning");
                    intent.putExtra("scan_for_devices", "Search");
                    intent.putExtra("select_device", "Select");
                    intent.putExtra("layout_list", R.layout.bluetooth_layout_list);
                    intent.putExtra("layout_text", R.layout.bluetooth_layout_text);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE); 
        		}
        	}
        });
		
		Statusvalu1 = (TextView)findViewById(R.id.Statusvalu1);
		
		//ISO 8601 datetime
		sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	public void onDestroy() {
        super.onDestroy();
        bt.stopService();
    }
	
	public void onStart() {
        super.onStart();
        if(!bt.isBluetoothEnabled()) {
        	bt.enable();
        } else {
            if(!bt.isServiceAvailable()) { 
                bt.setupService();
//                bt.startService(BluetoothState.DEVICE_ANDROID);
                bt.startService(BluetoothState.DEVICE_OTHER);
                
                setup();
            }
        }
    }
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
			if(resultCode == Activity.RESULT_OK){
                bt.connect(data);
                
                Log.d("ball", "connected:"+data.toString());
			}
			    
		} else if(requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if(resultCode == Activity.RESULT_OK) {
                bt.setupService();
            } else {
                Toast.makeText(getApplicationContext()
                		, "Bluetooth was not enabled."
                		, Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
	
	public void setup() {
		Button btnSend = (Button)findViewById(R.id.btnSend);
		btnSend.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
        		bt.send("Text");
        	}
        });
		
		bt.setOnDataReceivedListener(new OnDataReceivedListener() {
		    @SuppressWarnings("null")
			public void onDataReceived(int control_byte,byte[] data, String message) {		    			    
		        
		        if(control_byte==0)			//msg rx mode
		        {		        	
		        	String[] sensordata= message.split(",");
			        if(sensordata.length==2){	
		        		filename = sensordata[0];
		        		filesize = Integer.parseInt(sensordata[1]);
		        		//open file	
		        		
		                File file = new File(fpath +"/"+ filename);
		                fpath=fpath+"/"+ filename;
		                
		        		cnt=filesize;

		        		try{
		        			fos = new FileOutputStream(file);	//open file		        			
		        		}
		                catch (FileNotFoundException e) {
		                	System.out.println("File not found" + e);
		                }		        			
		        				        		
		        		bt.send("GO");		//arduino starts transferring file when receive bt serial 
		        	}else
		        	{		        		
		        	}			    		        	
		        }else if(control_byte==1)	//file rx mode
		        {
		        	if(cnt!=0)
		        	{
		                try{	//one byte per writing
		        			fos.write(data,0,data.length);	        			
		        			cnt=cnt-data.length;
		        		}
		        		catch (IOException ioe) {
		                }
		                if(cnt<=0)
		                {
			        		try {
			        			if (fos != null) {
			        				fos.close();
			        			}
			        		}
			        		catch (IOException ioe) {
			        			System.out.println("Error while closing stream: " + ioe);
			        		}			    
					
				            String inputLine;

					        try {
								BufferedReader br = new BufferedReader(new FileReader(fpath));
			                    List<HashMap<String, Object>> valueMapList=new ArrayList<HashMap<String, Object>>();
			                    List<Date> atList=new ArrayList<Date>();
						        
								while ((inputLine = br.readLine()) != null) 
								{
						        	String[] gpsdata= inputLine.split(";");

						        	if(gpsdata.length==3){
					                    map.put("sf4d36a802e8d11e4", gpsdata[0]);
						        		map.put("tfa82a5902e8d11e4", gpsdata[1]);
					                    map.put("vedcd5202e8d11e4", gpsdata[2]);
					                    					                    
					                    valueMapList.add(map);
					                    atList.add(new Date());
					                    
					                    client.postMultiDataAsync("b5f38c29a2e6472", atList, valueMapList, new PostExecuteListener(){
					                        @Override
					                        public void onPostExecute(int arg0, String arg1) {
					                            Log.d("debug", "statusCode="+arg0);
					                            Log.d("debug", "resultMessage="+arg1);
					                        }});					
					                    
/*					                    client.postDataAsync("b5f38c29a2e6472", new Date(), map, new PostExecuteListener(){
					                        @Override
					                        public void onPostExecute(int arg0, String arg1) {
					                            Log.d("debug", "statusCode="+arg0);
					                            Log.d("debug", "resultMessage="+arg1);					                        
					                        }});
*/
						        	}
						        	
								}								
						        Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
						        Statusvalu1.setText("Done Uploading");

							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
							}
					        catch (IOException e) {
								// TODO Auto-generated catch block
					        }
		                }
		        	}
		        }else	;		        			        			        
		    }
		});
	}
}