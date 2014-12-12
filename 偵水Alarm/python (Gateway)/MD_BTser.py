import threading
from datetime import datetime
import requests
import json
import time
import bluetooth
import sys

dev_num = 1
port = 1

class MoistureDevice:
    __dev_addr =["98:D3:31:B3:EF:D9"]
    __dev_id=["45a9b7here5904361"]
    __ds_m=["vf7332aa0536711e4"]
    __ds_a=["vf12777b0536711e4"]
    __api_key={'apikey': 'e095f45herec4870'}
    __url= "http://106.186.30.234/api/devices/"
    __headers={'content-type': 'application/json'}
    __param={'apikey': 'e095f45herec4870'}
    
    def __init__(self, nodev):
       self.__num=nodev
    
    @classmethod
    def getdev(cls, id):
        return cls.__dev_id[id]
    def geturl(cls, id):
        str = cls.__url+cls.__dev_id[id]+"/updatedata"
        return str
    def getDevAddr(cls, id):
        return cls.__dev_addr[id]
    def getDS_m(cls, id):
        return cls.__ds_m[id]
    def getDS_a(cls, id):
        return cls.__ds_a[id]
    def getHeader(cls):
        return cls.__headers
    def getKey(cls):
        return cls.__api_key

class ClientThread(threading.Thread, MoistureDevice):

    def __init__(self, ip, port, index, ):
        threading.Thread.__init__(self)
        self.ip = ip
        self.port = port
        self.index = index
        self.socket = bluetooth.BluetoothSocket( bluetooth.RFCOMM )
        
        self.reConnect()
       
#        print "[+] New thread started for "+ip+":"+str(port)

    def reConnect(self):
	print "try reconnect"
        while True:
            try:
                self.socket.connect((self.ip, self.port))
                print "connected"
		return
            except bluetooth.btcommon.BluetoothError:
		time.sleep(5)
                continue
    
    def run(self):
        print "Thread : "+ str(self.index)
        self.linesplit()
        
    def linesplit(self):
        print self.socket
	try:
        	buffer = self.socket.recv(128)
	except bluetooth.btcommon.BluetoothError:
		print "disconnected"
		self.reConnect()
        done = False
        while not done:
            if "\r\n" in buffer:
                (line, buffer) = buffer.split("\r\n", 1)
                print type(line)
                print line
            
                if line !='':
                    out = line.split(',')
        
                timestr = datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S.%f%zZ")
                data = {'datapoints' : [{'at': datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S.%f%zZ"), MoistureDevice(dev_num).getDS_m(self.index): out[1], MoistureDevice(dev_num).getDS_a(self.index): out[0]}]}
                res = requests.post(MoistureDevice(dev_num).geturl(self.index), data=json.dumps(data), headers=MoistureDevice(dev_num).getHeader(), params=MoistureDevice(dev_num).getKey())
                print res
            else:
                try:
                	more = self.socket.recv(128)
                except  bluetooth.btcommon.BluetoothError:
			print "disconnected"
			self.reConnect()
			continue
		if not more:
                    done = True
                else:
                    buffer = buffer+more
        if buffer:
            print "test2"
            
def main():
    DeviceManager=MoistureDevice(dev_num)
    
    threads = []
    
    for x in range(dev_num):
	print DeviceManager.getDevAddr(x)
        newthread = ClientThread(DeviceManager.getDevAddr(x), port, x)
        newthread.start()
        threads.append(newthread)
    
if __name__ == "__main__":
    main()