#!/usr/bin/python3

import pickle
import threading
import sys,getpass
import cv2
import os
import numpy as np
import psutil
import subprocess

from telnetlib import Telnet

from utils import label_map_util
from utils import visualization_utils as vis_util

if getpass.getuser() == "tedankara":

    import tensorflow as tf
    from distutils.version import StrictVersion

    if StrictVersion(tf.__version__) < StrictVersion('1.12.0'):
        raise ImportError('Please upgrade your TensorFlow installation to v1.12.*.')
else:
    # import psutil
    pass

import json

import base64
from PIL import Image
from io import BytesIO
from urllib.parse import urlencode
from urllib.request import Request, urlopen
from imutils.video import VideoStream
import ssl

switch = 1

import socket

# This is needed since the notebook is stored in the object_detection folder.
sys.path.append("..")
import time
from object_detection.utils import ops as utils_ops

TELNET = True

AI_IP = '127.0.0.1'
LIGHT_IP = '192.168.2.174'
context = ssl._create_unverified_context()
if TELNET:
    tn = Telnet(LIGHT_IP, 31)
light_green = False

# What model to download.


encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]

PATH_TO_LABELS = os.path.join('object_detection/data', 'mscoco_label_map.pbtxt')


category_index = label_map_util.create_category_index_from_labelmap(PATH_TO_LABELS, use_display_name=True)



def load_image_into_numpy_array(image):
  (im_width, im_height) = image.size
  return np.array(image.getdata()).reshape(
      (im_height, im_width, 3)).astype(np.uint8)

data = {"gpu_temp":"10C","gpu_load":"15%","cpu_temp":"47C","cpu_load":"15%","mem_temp":"NaN","mem_load":"17%","fan_speed":"10000RPM"}


def get_temps():
    global data
    temps = psutil.sensors_temperatures()
    result = subprocess.run(['nvidia-smi', '--query-gpu=utilization.memory', '--format=csv'] , stdout=subprocess.PIPE)
    data["gpu_load"] = result.stdout.decode("utf-8").split("\n")[1]
    result = subprocess.run(['nvidia-smi', '--query-gpu=temperature.gpu', '--format=csv'] , stdout=subprocess.PIPE)
    data["gpu_temp"] = result.stdout.decode("utf-8").split("\n")[1]+"°C"
    data["cpu_temp"] = str(int(temps["coretemp"][0][1]))+"°C"
    data["cpu_load"] = str(psutil.cpu_percent())+"%"
    data["mem_load"] = str(dict(psutil.virtual_memory()._asdict())["percent"])+"%"
    data["fan_speed"] = str(psutil.sensors_fans()["dell_smm"][0][1])+"RPM"


def run_inference_for_single_image(image):
    _, buffer = cv2.imencode('.jpg', image)
    img_base64 =  base64.b64encode(buffer).decode('ascii')
    url = 'https://%s:5001/ai' % AI_IP # Set destination URL here
    post_fields = {'img': img_base64,"type":"coco"}     # Set POST fields here
    request = Request(url, urlencode(post_fields).encode())
    data = urlopen(request, context=context).read().decode("ascii")
    output_dict = json.loads(data)
    return output_dict

kill = True

def listener(port=8385):
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind((socket.gethostname(), port))
    serversocket.listen(5)
    while kill:
        serversocket.accept()

    print('Bye!')

def lights_on():
    global light_green
    light_green = True
    tn.write(b"1-0")
    time.sleep(1)
    tn.write(b"2-0")
    time.sleep(10)
    light_green = False
    tn.write(b"0-1")
    time.sleep(1)
    tn.write(b"0-2")

cut = (150, 250, 250, 150)
cut_send = [0, 0, 0, 0]
img_counter = 0
socket_switch = True

thread = threading.Thread(target=listener)
thread.start()

cam = VideoStream(src=0).start()


switch = 0
get_temps()
# (left, right, top, bottom)
ambulance_coordinates = (150, 400, 250, 400)

reps = -1
reps_vid = 0

while 1:
    image = cam.read()
    reps_vid += 1

    reps += 1
    try:  # Kavşak
        t1 = time.time()
        image_np = image
        output_dict = run_inference_for_single_image(image_np)

        height, width, channels = image_np.shape
        cv2.imshow('frmmi', image[ambulance_coordinates[2]:ambulance_coordinates[3], ambulance_coordinates[0]:ambulance_coordinates[1]])

        out_dict = {'detection_boxes': [], 'detection_classes': [], 'detection_scores': []}
        for index,i in enumerate(output_dict['detection_classes']):
            cont = False
            if i in [3, 6, 8,44,77]:  # Car, bus, truck
                score = output_dict['detection_scores'][index]
                if score > 0.3:
                    if not any((output_dict['detection_boxes'][index] == b) for b in out_dict['detection_boxes']):
                        avg_x = (output_dict['detection_boxes'][index][0] + output_dict['detection_boxes'][index][2])/2
                        avg_y = (output_dict['detection_boxes'][index][1] + output_dict['detection_boxes'][index][3])/2
                        for box in out_dict['detection_boxes']:
                            avg_box_x = (box[0] + box[2])/2
                            avg_box_y = (box[1] + box[3])/2
                            if abs(avg_x-avg_box_x) < 0.1 and abs(avg_y-avg_box_y) < 0.1:
                                cont = True
                                break
                        if cont:
                            continue
                        out_dict['detection_classes'].append(i)
                        out_dict['detection_boxes'].append(output_dict['detection_boxes'][index])
                        out_dict['detection_scores'].append(output_dict['detection_scores'][index])

        out_dict['detection_classes'] = np.array(out_dict['detection_classes'])
        out_dict['detection_boxes'] = np.array(out_dict['detection_boxes'])
        out_dict['detection_scores'] = np.array(out_dict['detection_scores'])

        im_height, im_width, _ = image_np.shape
        if not light_green and TELNET:
            for index, box in enumerate(out_dict['detection_boxes']):
                box = tuple(map(int, (box[1] * im_width, box[3] * im_width, box[0] * im_height, box[2] * im_height)))
                # (left, right, top, bottom)
                if abs((box[0] + box[1])/2 - (ambulance_coordinates[0] + ambulance_coordinates[1])/2) < 25 and \
                    abs((box[2] + box[3])/2 - (ambulance_coordinates[2] + ambulance_coordinates[3])/2) < 25:
                    print('ambulance')
                    threading.Thread(target=lights_on).start()

        vis_util.visualize_boxes_and_labels_on_image_array(
            image_np,
            out_dict['detection_boxes'],
            out_dict['detection_classes'],
            out_dict['detection_scores'],
            category_index,
            instance_masks=out_dict.get('detection_masks'),
            use_normalized_coordinates=True,
            line_thickness=8,
            min_score_thresh=0.3
        )
        #cv2.imshow('frame', image_np)
        #ex_c = [27, ord("q"), ord("Q")]
        #if cv2.waitKey(1) & 0xFF in ex_c:
        #    break

        t2 = time.time()
        print("time taken for {}".format(t2-t1))
        if not sys.platform == "win32" and t2-t1 < 0.1:
            time.sleep(0.1-(t2-t1))
        send_image = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        if socket_switch:
            try:
                client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                client_socket.settimeout(0.1)
                client_socket.connect(('127.0.0.1', 8485))
                connection = client_socket.makefile('wb')
                socket_switch = False
            except:
                socket_switch = True
                continue
        try:
            crop_img = send_image.copy(order='C')
            crop_img = Image.fromarray(crop_img, "RGB")
            buffered = BytesIO()
            crop_img.save(buffered, format="JPEG")
            img = base64.b64encode(buffered.getvalue()).decode("ascii")
            lens = [len(send_image), 0, len(send_image[0])]
            for i in range(0,len(cut), 2):
                if cut[i] < 0:
                    cut_send[i] = lens[i] + cut[i]
                cut_send[i+1] = abs(cut[i])-abs(cut[i+1])
            client_socket.sendall(json.dumps({"image_full":img,"image_sizes":{"x":90,"y":0,"width":140,"height":140},"load":data}).encode('gbk')+b"\n")
            img_counter += 1
        except:
            socket_switch = True

        if img_counter % 10 == 0:
            get_temps()
            pass

    except Exception as e:
        if hasattr(e, 'message'):
            print(e.message)
        else:
            print(e)
        break



if not socket_switch:
    client_socket.sendall(b"Bye\n")
    cam.release()


cv2.destroyAllWindows()
cam.stop()
kill = False
thread.join()
