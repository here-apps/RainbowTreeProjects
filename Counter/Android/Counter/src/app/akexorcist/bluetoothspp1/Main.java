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
 
package app.akexorcist.bluetoothspp1;

import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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

import com.hereapps.rainbowtree.android.RainbowtreeClient;
import com.hereapps.rainbowtree.android.listener.RawStringListener;

public class Main extends Activity {
//	private int counter;
	private TextView countervalue1;	
		
	BluetoothSPP bt;
	//modify corresponding apikey here
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
		
		countervalue1 = (TextView)findViewById(R.id.countervalue1);
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
		    public void onDataReceived( byte[] data, String message) {		    			    
		        String[] sensordata= message.split(",");
		        if(sensordata.length==1){	        	

		        	//modify corresponding datastream id here
		        	map.put("sf4d36a802e8d11e4", sensordata[0]);
                    
			        countervalue1.setText(message);			        	
				    
			        //modify corresponding Device id here
			        
				    client.postDataAsync("af58242e63ed4619", new Date(), map, new RawStringListener(){
                    @Override
                    public void onPostExecute(int arg0, String arg1) {
                        Log.d("debug", "statusCode="+arg0);
                        Log.d("debug", "resultMessage="+arg1);					                        
                    }});				    
		        }
		    }
		});
	}
}