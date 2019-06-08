#!/usr/bin/python3

import pickle
import threading
import sys
import cv2
import os
import numpy as np

from utils import label_map_util
from utils import visualization_utils as vis_util

if sys.platform == "win32":

    import tensorflow as tf
    from distutils.version import StrictVersion

    if StrictVersion(tf.__version__) < StrictVersion('1.12.0'):
        raise ImportError('Please upgrade your TensorFlow installation to v1.12.*.')
else:
    import psutil

import json

import base64
from PIL import Image
from io import BytesIO

switch = 1

import socket

# This is needed since the notebook is stored in the object_detection folder.
sys.path.append("..")
import time
from object_detection.utils import ops as utils_ops



# What model to download.


encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]

#MODEL_NAME = 'ssd_mobilenet_v1_coco_2017_11_17' #not even worth trying
#MODEL_NAME = "ssd_inception_v2_coco_2017_11_17" # not bad and fast
MODEL_NAME = "rfcn_resnet101_coco_11_06_2017" # WORKS BEST BUT takes 4 times longer per image
#MODEL_NAME = "faster_rcnn_resnet101_coco_11_06_2017" # too slow
#MODEL_NAME = "ssd_resnet101_v1_fpn_shared_box_predictor_oid_512x512_sync_2019_01_20"
MODEL_FILE = MODEL_NAME + '.tar.gz'
DOWNLOAD_BASE = 'http://download.tensorflow.org/models/object_detection/'

# Path to frozen detection graph. This is the actual model that is used for the object detection.
PATH_TO_FROZEN_GRAPH = MODEL_NAME + '/frozen_inference_graph.pb'

# List of the strings that is used to add correct label for each box.
PATH_TO_LABELS = os.path.join('object_detection/data', 'mscoco_label_map.pbtxt')


category_index = label_map_util.create_category_index_from_labelmap(PATH_TO_LABELS, use_display_name=True)

if sys.platform == "win32":
    detection_graph = tf.Graph()
    with detection_graph.as_default():
      od_graph_def = tf.GraphDef()
      with tf.gfile.GFile(PATH_TO_FROZEN_GRAPH, 'rb') as fid:
        serialized_graph = fid.read()
        od_graph_def.ParseFromString(serialized_graph)
        tf.import_graph_def(od_graph_def, name='')


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

data = {"gpu_temp":"10C","gpu_load":"15%","cpu_temp":"47C","cpu_load":"15%","mem_temp":"NaN","mem_load":"17%","fan_speed":"10000RPM"}

def get_temps():
    global data
    if not sys.platform == "win32":
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

kill = True

def listener(port=8385):
    serversocket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    serversocket.bind((socket.gethostname(), port))
    serversocket.listen(5)
    while kill:
        serversocket.accept()

    print('Bye!')

cut = [115, 100, 400, 150]
cut_send = [0, 0, 0, 0]
img_counter = 0
socket_switch = True

thread = threading.Thread(target=listener)
thread.start()

if sys.platform == "win32":
    with detection_graph.as_default():
        sess = tf.Session()
    cam = cv2.VideoCapture(0)
else:
    cam = cv2.VideoCapture('debug_data/amb_1.mp4')
    with open("debug_data/frame_data.pkl","rb") as pkl_file:
        frame_data = pickle.load(pkl_file)

switch = 0
get_temps()
amb_center = {'x': (400 + 550)/2, 'y': (115+215)/2}

reps = -1
reps_vid = 0

while 1:
    ret,image = cam.read()
    reps_vid += 1
    if not sys.platform == "win32" and not reps_vid % 2 == 0:
        continue
    reps += 1
    try:  # Kavşak
        t1 = time.time()
        image_np = image
        image_np_expanded = np.expand_dims(image_np, axis=0)
        if sys.platform == "win32":
            output_dict = run_inference_for_single_image(image_np_expanded, detection_graph)
        else:
            output_dict = frame_data[reps]

        height, width, channels = image_np.shape

        out_dict = {'detection_boxes': [], 'detection_classes': [], 'detection_scores': []}
        for i in output_dict['detection_classes']:
            cont = False
            if i in [3, 6, 8]:  # Car, bus, truck
                index = np.where(output_dict['detection_classes'] == i)[0][0]
                score = output_dict['detection_scores'][index]
                if score > 0.3:
                    if not any((output_dict['detection_boxes'][index] == b).all() for b in out_dict['detection_boxes']):
                        avg_x = (output_dict['detection_boxes'][index][0] + output_dict['detection_boxes'][index][2])/2
                        avg_y = (output_dict['detection_boxes'][index][1] + output_dict['detection_boxes'][index][3])/2
                        for box in out_dict['detection_boxes']:
                            avg_box_x = (box[0] + box[2])/2
                            avg_box_y = (box[1] + box[3])/2
                            if abs(avg_x-avg_box_x) < 0.1 and abs(avg_y-avg_box_y) < 0.1:
                                cont = True
                                continue

                        out_dict['detection_classes'].append(i)
                        out_dict['detection_boxes'].append(output_dict['detection_boxes'][index])
                        out_dict['detection_scores'].append(output_dict['detection_scores'][index])

        out_dict['detection_classes'] = np.array(out_dict['detection_classes'])
        out_dict['detection_boxes'] = np.array(out_dict['detection_boxes'])
        out_dict['detection_scores'] = np.array(out_dict['detection_scores'])

        print(len(out_dict['detection_classes']), ' cars.')

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
        cv2.imshow('frame', image_np)
        ex_c = [27, ord("q"), ord("Q")]
        if cv2.waitKey(1) & 0xFF in ex_c:
            break

        t2 = time.time()
        print("time taken for {}".format(t2-t1))
        if not sys.platform == "win32":
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
            client_socket.sendall(json.dumps({"image_full":img,"image_sizes":{"x":cut_send[2],"y":cut_send[0],"width":cut_send[3],"height":cut_send[1]},"load":data}).encode('gbk')+b"\n")
            img_counter += 1
        except:
            socket_switch = True

        if img_counter % 10 == 0:
            get_temps()

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
cam.release()
kill = False
thread.join()
