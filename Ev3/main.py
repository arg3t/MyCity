#!/usr/bin/env python3
print("[INFO]: Interpreter started")
from ev3dev.core import *
print("[INFO]: Imported ev3dev.ev3")
import socket
print("[INFO]: Imported socket")
import os
print("[INFO]: Imported os")
import json
print("[INFO]: Imported json")


HOST = '0.0.0.0'
PORT = 3131
mjpeg_loc = "/home/robot/mjpeg-streamer/mjpeg-streamer-experimantal/mjpeg-streamer"
os.spawnl(os.P_DETACH, ' -i "./input_uvc.so -f 15 -r 640x480" -o "./output_http.so -w ./www"')
power = PowerSupply()

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    try:
        s.bind((HOST, PORT))
        while True:
            s.listen()
            print("[INFO]: Listening...")
            conn, addr = s.accept()
            with conn:
                print('[INFO]: Connected by', addr)
                while True:
                    try:
                        data = {
                            "battery_voltage":power.measured_volts,
                            "current_drawn":power.measured_amps,
                            "lat":31,
                            "lng":39
                        }
                        conn.send((json.dumps(data, ensure_ascii=False)+"\n").encode('gbk'))
                    except BrokenPipeError:
                        print("[INFO]: Client disconnected")
                        break
	            

    except KeyboardInterrupt:
        s.close()
