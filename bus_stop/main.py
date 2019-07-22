#!/usr/bin/env python3
'''Hello to the world from ev3dev.org'''

import os
import sys
import time
import json
import datetime
import urllib3
import requests

from ev3dev2.motor import LargeMotor, OUTPUT_A, OUTPUT_B, SpeedPercent, MoveTank
from ev3dev2.sensor import INPUT_1,INPUT_2
from ev3dev2.sensor.lego import TouchSensor
from ev3dev2.sensor.lego import UltrasonicSensor
from ev3dev2.sound import Sound
from ev3dev2.button import Button

ON = True
OFF = False


def debug_print(*args, **kwargs):
    print(*args, **kwargs, file=sys.stderr)


def reset_console():
    print('\x1Bc', end='')


def set_cursor(state):
    if state:
        print('\x1B[?25h', end='')
    else:
        print('\x1B[?25l', end='')


def set_font(name):
    '''Sets the console font

    A full list of fonts can be found with `ls /usr/share/consolefonts`
    '''
    os.system('setfont ' + name)

def raise_ramp():
    print("Raising ramp")

def get_closest_time(times, time):
    closest = 0
    times = [datetime.datetime.strptime(i,"%H:%M") for i in times]
    time = datetime.datetime.strptime(time,"%H:%M")
    for i in times:
        if i.hour < time.hour or (i.hour == time.hour and i.minute < time.minute):
            continue
        else:
            if closest == 0:
                closest = i
                continue
            if abs(i.hour - time.hour) < abs(closest.hour - time.hour):
                closest = i
                continue
            elif abs(i.hour - time.hour) == abs(closest.hour - time.hour):
                if i.minute - time.minute < closest.minute - time.minute:
                    closest = i

    return closest.strftime("%H:%M")


def time_diff(t1, t2):
    time1 = datetime.datetime.strptime(t1, "%H:%M")
    time2 = datetime.datetime.strptime(t2, "%H:%M")
    response = ""
    if abs(time2.hour - time1.hour) < 10:
        response += "0"
        if time2.minute - time1.minute >= 0:
            response += str(abs(time2.hour - time1.hour))
        else:
            response += str(abs(time2.hour - time1.hour) - 1)

    response += ":"

    if abs(time2.minute - time1.minute) < 10:
        response += "0"

    response += str(abs(time2.minute - time1.minute))

    return response


def raise_ramp(motor1):
    motor1.on_for_rotations(SpeedPercent(75), 5)

def main():

    start = time.time()
    file = open("times.json","r")
    time_data = json.loads(file.read())
    stop_data = [i for i in time_data.keys()]
    debug_print(stop_data)
    file.close()
    time_start = "10:02"
    debug_print('Started Code')
    reset_console()
    set_cursor(OFF)
    set_font('Lat15-Terminus24x12')
    alarms = [0,0,0]
    stop = "11222"

    urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

    print('AKILLI DURAK')
    
    sound = Sound()
    

    us = UltrasonicSensor(INPUT_2)
    us.MODE_US_DIST_CM
    btn = Button()
    m1 = LargeMotor(OUTPUT_A)

    ts = TouchSensor(INPUT_1)

    while 1:
        if us.distance_centimeters < 15:
            sound.speak("Please select a stop")
            i = 0
            reset_console()
            print(stop_data[i%len(stop_data)])
            while not btn.enter:
                if btn.up:
                    i += 1
                    reset_console()
                    print(stop_data[i%len(stop_data)])
                    time.sleep(0.1)
                if btn.down:
                    i -= 1
                    reset_console()
                    print(stop_data[i%len(stop_data)])
                    time.sleep(0.1)
            closest = datetime.datetime.strptime(time_diff(time_start,get_closest_time(time_data[stop_data[i%len(stop_data)]]["weekday"][:],time_start)),"%H:%M")
            difference = datetime.timedelta(hours=closest.hour,minutes=closest.minute).total_seconds()+start

            if not difference in alarms:
                alarms[i%len(stop_data)] = difference
                r = requests.get("https://192.168.44.50:5000/bus?line={}&stop={}".format(stop_data[i%len(stop_data)],stop), verify=False)

            print(difference-time.time())
            time.sleep(1)
            reset_console()
            print("AKILLI DURAK")

        for i in range(len(alarms)):
            if alarms[i] == 0:
                continue
            else:
                if alarms[i]-time.time()<0:
                    reset_console()
                    print("Bus no {} has arrived".format(stop_data[i%len(stop_data)]))
                    sound.speak("Bus no {} has arrived".format(stop_data[i%len(stop_data)]))
                    sound.speak("Would you like a ramp?")
                    waiter = time.time()
                    pressed = True
                    while time.time()-waiter < 10 and pressed:
                        if ts.is_pressed:
                            pressed = False
                            raise_ramp(m1)
                    reset_console()
                    print("AKILLI DURAK")
                    alarms[i] = 0
                            

if __name__ == '__main__':
    main()
