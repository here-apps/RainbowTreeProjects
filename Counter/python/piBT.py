from datetime import datetime
import requests
import json
import time
import bluetooth
import sys
bd_addr = "98:D3:31:B2:32:27" #itade address

port = 1
device_id = "af58242e63ed4619"
datastream_id1 = "v626fa40245211e4"
param = {'apikey': 'e095f4538bac4870'}
api_key="e095f453here4870"
url = "http://106.186.30.234/api/devices/"+device_id+"/updatedata"
headers = {'content-type': 'application/json'}

def linesplit(socket):
    # untested
    print 'here'
    print socket
    buffer = socket.recv(128)
    done = False
    while not done:
        if "\r\n" in buffer:
            (line, buffer) = buffer.split("\r\n", 1)
            print type(line)
            print line
            timestr = datetime.utcnow().strftime("%Y-%m-%dT%H:%M:%S.%f%zZ")
            data = {'datapoints' : [{'at': datetime.utcnow().strftime("%Y-%m-%dTT%H:%M:%S.%f%zZ"),
                                      datastream_id1: line
                                      }]}

                                      res = requests.post(url, data=json.dumps(data), headers=headers, parrams=param)

            print res

        else:
            more = socket.recv(128)
            if not more:
                done = True
            else:
                buffer = buffer+more
    if buffer:
        print "test2"
        #yield buffer

def main():
    sock=bluetooth.BluetoothSocket( bluetooth.RFCOMM )
    sock.connect((bd_addr, port))
    print 'Connected'
    sock.settimeout(5.0)

    print "abc"
    linesplit(sock)
    print "def"

    sock.close()

if __name__ == "__main__":
    main()
