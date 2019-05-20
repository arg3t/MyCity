#!/usr/bin/python3

import numpy as np
import os
import sys
import tensorflow as tf
import cv2
from distutils.version import StrictVersion
import socket
from utils import label_map_util

from utils import visualization_utils as vis_util
import psutil
import json

import base64
from PIL import Image
from io import BytesIO
import psutil

switch = 1

import io
import socket
import struct
import time
import pickle
import zlib

# This is needed since the notebook is stored in the object_detection folder.
sys.path.append("..")
import time
from object_detection.utils import ops as utils_ops

if StrictVersion(tf.__version__) < StrictVersion('1.12.0'):
  raise ImportError('Please upgrade your TensorFlow installation to v1.12.*.')

# What model to download.


encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]

MODEL_NAME = 'ssd_mobilenet_v1_coco_2017_11_17' #not even worth trying
#MODEL_NAME="ssd_inception_v2_coco_11_06_2017" # not bad and fast
#MODEL_NAME="rfcn_resnet101_coco_11_06_2017" # WORKS BEST BUT takes 4 times longer per image
#MODEL_NAME = "faster_rcnn_resnet101_coco_11_06_2017" # too slow
MODEL_FILE = MODEL_NAME + '.tar.gz'
DOWNLOAD_BASE = 'http://download.tensorflow.org/models/object_detection/'

# Path to frozen detection graph. This is the actual model that is used for the object detection.
PATH_TO_FROZEN_GRAPH = MODEL_NAME + '/frozen_inference_graph.pb'

# List of the strings that is used to add correct label for each box.
PATH_TO_LABELS = os.path.join('object_detection/data', 'mscoco_label_map.pbtxt')

detection_graph = tf.Graph()
with detection_graph.as_default():
  od_graph_def = tf.GraphDef()
  with tf.gfile.GFile(PATH_TO_FROZEN_GRAPH, 'rb') as fid:
    serialized_graph = fid.read()
    od_graph_def.ParseFromString(serialized_graph)
    tf.import_graph_def(od_graph_def, name='')
    
category_index = label_map_util.create_category_index_from_labelmap(PATH_TO_LABELS, use_display_name=True)

def load_image_into_numpy_array(image):
  (im_width, im_height) = image.size
  return np.array(image.getdata()).reshape(
      (im_height, im_width, 3)).astype(np.uint8)
      
# For the sake of simplicity we will use only 2 images:
# image1.jpg
# image2.jpg
# If you want to test the code with your images, just add path to the images to the TEST_IMAGE_PATHS.
PATH_TO_TEST_IMAGES_DIR = 'object_detection/test_images'
TEST_IMAGE_PATHS = [ os.path.join(PATH_TO_TEST_IMAGES_DIR, 'image{}.jpg'.format(i)) for i in range(3, 6) ]

# Size, in inches, of the output images.
sess = 0
switch = 1
data = {"gpu_temp":"10C","gpu_load":"15%","cpu_temp":"47C","cpu_load":"15%","mem_temp":"NaN","mem_load":"17%","fan_speed":"10000RPM"}

def get_temps():
    global data
    temps = psutil.sensors_temperatures()
    data["cpu_temp"] = str(int(temps["dell_smm"][0][1]))+"°C"
    data["cpu_load"] = str(psutil.cpu_percent())+"%"
    data["mem_load"] = str(dict(psutil.virtual_memory()._asdict())["percent"])+"%"
    data["fan_speed"] = str(psutil.sensors_fans()["dell_smm"][0][1])+"RPM"

def run_inference_for_single_image(image, graph):
  global switch
  global sess
  with graph.as_default():
    if(switch):
        sess = tf.Session()
        switch = 0
      # Get handles to input and output tensors
    ops = tf.get_default_graph().get_operations()
    all_tensor_names = {output.name for op in ops for output in op.outputs}
    tensor_dict = {}
    for key in [
        'num_detections', 'detection_boxes', 'detection_scores',
        'detection_classes', 'detection_masks'
    ]:
      tensor_name = key + ':0'
      if tensor_name in all_tensor_names:
        tensor_dict[key] = tf.get_default_graph().get_tensor_by_name(
            tensor_name)
    if 'detection_masks' in tensor_dict:
      # The following processing is only for single image
      detection_boxes = tf.squeeze(tensor_dict['detection_boxes'], [0])
      detection_masks = tf.squeeze(tensor_dict['detection_masks'], [0])
      # Reframe is required to translate mask from box coordinates to image coordinates and fit the image size.
      real_num_detection = tf.cast(tensor_dict['num_detections'][0], tf.int32)
      detection_boxes = tf.slice(detection_boxes, [0, 0], [real_num_detection, -1])
      detection_masks = tf.slice(detection_masks, [0, 0, 0], [real_num_detection, -1, -1])
      detection_masks_reframed = utils_ops.reframe_box_masks_to_image_masks(
          detection_masks, detection_boxes, image.shape[1], image.shape[2])
      detection_masks_reframed = tf.cast(
          tf.greater(detection_masks_reframed, 0.5), tf.uint8)
      # Follow the convention by adding back the batch dimension
      tensor_dict['detection_masks'] = tf.expand_dims(
          detection_masks_reframed, 0)
    image_tensor = tf.get_default_graph().get_tensor_by_name('image_tensor:0')
    # Run inference
    output_dict = sess.run(tensor_dict,
                           feed_dict={image_tensor: image})
    # all outputs are float32 numpy arrays, so convert types as appropriate
    output_dict['num_detections'] = int(output_dict['num_detections'][0])
    output_dict['detection_classes'] = output_dict[
        'detection_classes'][0].astype(np.int64)
    output_dict['detection_boxes'] = output_dict['detection_boxes'][0]
    output_dict['detection_scores'] = output_dict['detection_scores'][0]
    if 'detection_masks' in output_dict:
      output_dict['detection_masks'] = output_dict['detection_masks'][0]


  return output_dict
cut=[-175,-1,-175,-1]
cut_send = [0,0,0,0]
a = 1
img_counter = 0
socket_switch = True
cam = cv2.VideoCapture(0)
with detection_graph.as_default():
    sess = tf.Session()
switch = 0
get_temps()
while 1:
    if(True):
        try:
            ret,image = cam.read()
            image_np = image[cut[0]:cut[1],cut[2]:cut[3]]
            #image_np = image_np[int(r[1]):int(r[1]+r[3]),int(r[0]):int(r[0]+r[2])]
            # the array based representation of the image will be used later in order to prepare the
            # result image with boxes and labels on it.

            # Expand dimensions since the model expects images to have shape: [1, None, None, 3]
            image_np_expanded = np.expand_dims(image_np, axis=0)
            t1 = time.time()
            # Actual detection.
            output_dict = run_inference_for_single_image(image_np_expanded, detection_graph)
            # Visualization of the results of a detection.
            vis_util.visualize_boxes_and_labels_on_image_array(
                image_np,
                output_dict['detection_boxes'],
                output_dict['detection_classes'],
                output_dict['detection_scores'],
                category_index,
                instance_masks=output_dict.get('detection_masks'),
                use_normalized_coordinates=True,
                line_thickness=8)
            image[cut[0]:cut[1],cut[2]:cut[3]] = image_np
            send_image = cv2.cvtColor(image,cv2.COLOR_BGR2RGB)
            cv2.imshow("Cam",image)
            cv2.imshow("Cut",image_np)

            if socket_switch:
                try:
                    client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                    client_socket.connect(('127.0.0.1', 8485))
                    connection = client_socket.makefile('wb')
                    socket_switch = False
                except:
                    socket_switch=True
                    continue
                try:
                    crop_img = send_image.copy(order='C')

                    crop_img = Image.fromarray(crop_img,"RGB")
                    buffered = BytesIO()
                    crop_img.save(buffered, format="JPEG")
                    img = base64.b64encode(buffered.getvalue()).decode("ascii")
                    client_socket.sendall(json.dumps({"image_full":img,"image_sizes":{"x":cut_send[2],"y":cut_send[0],"width":cut_send[3],"height":cut_send[1]},"load":data}).encode('gbk')+b"\n")
                    img_counter += 1

                except:
                    socket_switch=True

            if img_counter % 10 ==0:
                get_temps()
            t2 = time.time()
            print("time taken for {}".format(t2-t1))

            ex_c = [27, ord("q"), ord("Q")]
            if cv2.waitKey(1) & 0xFF in ex_c:
                break
        except KeyboardInterrupt:
            if not socket_switch:
                client_socket.sendall(b"Bye\n")
                cam.release()
                exit(0)

cv2.destroyAllWindows()
cam.release()
