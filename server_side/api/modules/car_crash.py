from modules import utils

from flask import Flask, request, Response
from flask_restful import Resource, Api

from PIL import Image
import cv2

import base64
import json
import sys
import os
import io

MIN_AREA_RATIO = 0.1

MIN_SCORE_THRESH = 0.6

if sys.platform == "win32":
    import tensorflow as tf
    import numpy as np
    import pickle

    sys.path.insert(0, r'C:\Users\Tednokent01\Downloads\MyCity\traffic_analyzer')
    from utils import label_map_util

    from utils import visualization_utils as vis_util

app = Flask(__name__)
api = Api(app)

db_path = os.path.join(app.root_path, 'databases', 'crashes.json')
with open(db_path, 'r') as f:
    crashes = json.load(f)

users_path = os.path.join(app.root_path, 'databases', 'users.json')
with open(users_path, 'r') as f:
    users = json.load(f)

if sys.platform == "win32":
    # PATH_TO_LABELS = '../../traffic_analyzer/object_detection/data/kitti_label_map.pbtxt'
    # PATH_TO_CKPT = 'modules/faster_rcnn_resnet101_kitti_2018_01_28/frozen_inference_graph.pb'

    PATH_TO_LABELS = '../../traffic_analyzer/object_detection/data/mscoco_label_map.pbtxt'
    PATH_TO_CKPT = '../../traffic_analyzer/rfcn_resnet101_coco_2018_01_28/frozen_inference_graph.pb'
    category_index = label_map_util.create_category_index_from_labelmap(PATH_TO_LABELS, use_display_name=True)

    detection_graph = tf.Graph()
    with detection_graph.as_default():
        od_graph_def = tf.GraphDef()
        with tf.gfile.GFile(PATH_TO_CKPT, 'rb') as fid:
            serialized_graph = fid.read()
            od_graph_def.ParseFromString(serialized_graph)
            tf.import_graph_def(od_graph_def, name='')

    def load_image_into_numpy_array(image):
        (im_width, im_height) = image.size
        return np.array(image.getdata()).reshape(
            (im_height, im_width, 3)).astype(np.uint8)

def process_img(img_base64):
    if sys.platform == "win32":
        img = Image.open(io.BytesIO(base64.b64decode(img_base64)))
        with detection_graph.as_default():
            with tf.Session(graph=detection_graph) as sess:
                # Definite input and output Tensors for detection_graph
                image_tensor = detection_graph.get_tensor_by_name('image_tensor:0')
                # Each box represents a part of the image where a particular object was detected.
                detection_boxes = detection_graph.get_tensor_by_name('detection_boxes:0')
                # Each score represent how level of confidence for each of the objects.
                # Score is shown on the result image, together with the class label.
                detection_scores = detection_graph.get_tensor_by_name('detection_scores:0')
                detection_classes = detection_graph.get_tensor_by_name('detection_classes:0')
                num_detections = detection_graph.get_tensor_by_name('num_detections:0')
                # the array based representation of the image will be used later in order to prepare the
                # result image with boxes and labels on it.
                image_np = load_image_into_numpy_array(img)
                # Expand dimensions since the model expects images to have shape: [1, None, None, 3]
                image_np_expanded = np.expand_dims(image_np, axis=0)
                # Actual detection.
                (boxes, scores, classes, num) = sess.run(
                    [detection_boxes, detection_scores, detection_classes, num_detections],
                    feed_dict={image_tensor: image_np_expanded})
                # Visualization of the results of a detection.
                vis_util.visualize_boxes_and_labels_on_image_array(
                    image_np,
                    np.squeeze(boxes),
                    np.squeeze(classes).astype(np.int32),
                    np.squeeze(scores),
                    category_index,
                    min_score_thresh=MIN_SCORE_THRESH,
                    use_normalized_coordinates=True,
                    line_thickness=8)

        output_dict = {'detection_classes': np.squeeze(classes).astype(np.int32), 'detection_scores': np.squeeze(scores), 'detection_boxes': np.squeeze(boxes)}
        with open('image_1_data.pkl', 'wb') as f:
            pickle.dump(output_dict, f)
        cv2.imwrite('image_1.jpg', image_np)
        im_height, im_width, _ = image_np.shape
        cars_involved = 0
        injured_people = 0
        prev_cars = []
        for index, i in enumerate(output_dict['detection_classes']):
            score = output_dict['detection_scores'][index]
            if score > MIN_SCORE_THRESH:
                if i in [3, 6, 8]:
                    box = output_dict['detection_boxes'][index]
                    (left, right, top, bottom) = (box[1] * im_width, box[3] * im_width,
                                                  box[0] * im_height, box[2] * im_height)

                    avg_x = left+right/2
                    avg_y = top+bottom/2
                    same = False
                    for prev_x, prev_y in prev_cars:
                        if abs(prev_x-avg_x) < 130 and abs(prev_y-avg_y) < 130:
                            same = True
                            break

                    if not same:
                        cars_involved += 1
                        prev_cars.append((avg_x, avg_y))

                elif i == 1:
                    box = output_dict['detection_boxes'][index]
                    (left, right, top, bottom) = (box[1] * im_width, box[3] * im_width,
                                                  box[0] * im_height, box[2] * im_height)

                    if right-left > bottom-top:
                        injured_people += 1

        _, buffer = cv2.imencode('.jpg', image_np)
        return base64.b64encode(buffer).decode('ascii'), cars_involved, injured_people

    return img_base64, 7, ["unprocessed"]

class Crash(Resource):
    def post(self):
        message = request.form['message']
        base64_img = request.form['img']
        id = request.form['id']
        lat, long = request.form['lat'], request.form['long']

        image, car_count, injured = process_img(base64_img)
        priority = car_count + injured
        if priority > 10:
            priority = 10

        crash = {
            'img': image,
            'message': message,
            'priority': priority,
            'stats': {
                'cars': car_count,
                'injured': injured
            },
            'location': {
                'latitude': lat,
                'longitude': long
            }
        }
        if id in crashes:
            crashes[id].append(crash)
        else:
            crashes[id] = [crash]

        with open(db_path, 'w') as f:
            json.dump(crashes, f, indent=4)

        cv2.imshow("a",load_image_into_numpy_array(Image.open(io.BytesIO(base64.b64decode(image)))))
        cv2.waitKey(0)


        return crash

class Box:
    def __init__(self,coords, type):
        self.x1 = coords[0]
        self.y1 = coords[2]
        self.x2 = coords[1]
        self.y2 = coords[3]
        self.area = (self.x2-self.x1) * (self.y2-self.y1)
        self.type = type

    def get_bigger(self,box):
        if box.type == self.type:
            return None
        left = max(box.x1, self.x1)
        right = min(box.x2, self.x2)
        bottom = max(box.y2, self.y2)
        top = min(box.y1, self.y1)

        if not left < right and bottom < top:
            return None

        if ((box.area * (box.area < self.area)) + (self.area * (box.area > self.area))) /  (right-left)*(top-bottom) < MIN_AREA_RATIO:
            return None

        if box.area > self.area:
            return box
        else:
            return self




