#!/usr/bin/env python3
print("[INFO]: Interpreter started")
from ev3dev.core import *
import ev3dev.ev3 as ev3
print("[INFO]: Imported ev3dev.ev3")
import socket
print("[INFO]: Imported socket")
import os
print("[INFO]: Imported os")
import json
print("[INFO]: Imported json")

state = True
moving = False
motor1 = ev3.LargeMotor("outA")
motor2 = ev3.LargeMotor("outD")
cam = ev3.MediumMotor('outB')
def move(speed):
    moving = True
    motor1.run_forever(speed_sp=speed)
    motor2.run_forever(speed_sp=speed)
    cam.run_to_abs_pos(position_sp=0, speed_sp=500)

def stop():
    moving = False
    motor1.stop()
    motor2.stop()
    cam.stop()

HOST = '0.0.0.0'
PORT = 3131
pid=os.fork()
if pid==0: # new process
    mjpeg_loc = "/home/robot/mjpg-streamer/"
    os.system('{0}mjpg_streamer -i "{0}input_uvc.so -f 5" -o "{0}output_http.so -w {0}www"'.format(mjpeg_loc))
    exit()
power = PowerSupply()
if power.measured_volts < 7.1:
    print("Change the battery.")
    exit()

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
                    if cam.speed == 0 and moving:
                        if state:
                            cam.run_to_abs_pos(position_sp=4000, speed_sp=500)
                        else:
                            cam.run_to_abs_pos(position_sp=0, speed_sp=500)
                        state = not state
                    try:
                        recieved = conn.recv(1024)
                        if not recieved:
                            raise BrokenPipeError
                        else:
                            recieved = recieved.decode().strip()[-1]
                            print(recieved)
                            if recieved == 's':
                                stop()
                            elif recieved == 'm':
                                move(500)
                            elif recieved == 'i':
                                data = {
                                    "battery_voltage":power.measured_volts,
                                    "current_drawn":power.measured_amps,
                                    "lat":31,
                                    "lng":39
                                }
                                conn.send((json.dumps(data, ensure_ascii=False)+"\n").encode('gbk'))
                            elif recieved == '+':
                                cam.run_timed(time_sp=100, speed_sp=750)
                            elif recieved == '-':
                                cam.run_timed(time_sp=100, speed_sp=-750)
                            elif recieved == 'f':
                                stop()
                                motor1.run_timed(time_sp=500, speed_sp=500)
                                motor2.run_timed(time_sp=500, speed_sp=500)
                            elif recieved == 'b':
                                stop()
                                motor1.run_timed(time_sp=500, speed_sp=-500)
                                motor2.run_timed(time_sp=500, speed_sp=-500)

                    except BrokenPipeError:
                        print("[INFO]: Client disconnected")
                        stop()
                        break
    except KeyboardInterrupt:
        stop()
        s.close()
