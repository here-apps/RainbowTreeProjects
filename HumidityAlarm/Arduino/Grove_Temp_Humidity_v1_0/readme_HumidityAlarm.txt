Setup

1. 準備一個容器裝滿一半以上的水, 再密閉放置數分鐘, 製造高濕度的環境
2. 溼度提醒Device端:
	接上行動電源, 此時藍芽module閃爍(尚未連線)
	*每8秒上傳資料 
	*Datastring格式 溼度, 溫度, AlarmFlag:  ex:  54.00, 28.00, 1   0:normal 1:wet 2:dry
3. Rasperry Pi:(登入帳/密  pi/1243)
	(a)確認網路可連線上網  (wifi)
	(b)確認有接上藍芽dongle 
	(c)執行 python BTser.py, 待兩個Device端藍芽模組停止閃爍, 即表示連線成功 
若順利連線, 出現以下訊息
98:D3:31:B2:32:27
try reconnect
connected
[+] New thread started for 98:D3:31:B2:32:27:1
Thread : 0
<bluetooth.bluez.BluetoothSocket instance at 0x7d1ee0>
 98:D3:31:40:0B:1C
try reconnect
connected
[+] New thread started for 98:D3:31:40:0B:1C:1
Thread : 1
<bluetooth.bluez.BluetoothSocket instance at 0x7d1f80>
.................	
	
   *重新設定
		若需中斷Pi上BTser.py程式重新執行, 重新執行BTser.py前, 請重新給Device端斷電後通電, 
		讓藍芽模組閃爍並等待數秒後再執行 python BTser.py	
	
	
Demo	
	
1. 設置完畢後, 手機上點選MoistureAlarm APP:
2. 執行APP, 點選START按鈕, 待10秒後手機畫面會顯示兩個溼度計目前的狀態, 若溼度過高(>65%)或低(<30)會發出提示音
	* APP每10秒去雲端抓取一次資料值
3. 可以將一組裝置的濕度計放置於杯內，並密閉其杯子
4. 由於裝置反應時間較慢，故需等待一段時間後，手機才會傳出提示音
