from modules import utils

from flask import Flask, request, Response
from flask_restful import Resource, Api

from PIL import Image

import base64
import json
import sys
import os
import io

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
    PATH_TO_LABELS = '../../traffic_analyzer/object_detection/data/kitti_label_map.pbtxt'
    PATH_TO_CKPT = 'modules/faster_rcnn_resnet101_kitti_2018_01_28/frozen_inference_graph.pb'

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
                    min_score_thresh=0.3,
                    use_normalized_coordinates=True,
                    line_thickness=8)

        output_dict = {'detection_classes': classes, 'detection_scores': scores[0], 'detection_boxes': boxes}
        cars_involved = 0
        injured_people = 0
        for i in output_dict['detection_classes']:
            index = np.where(output_dict['detection_classes'] == i)[0][0]
            score = output_dict['detection_scores'][index]
            if score > 0.3:
                if output_dict['detection_classes'] == 1:
                    cars_involved += 1
                else:
                    pass

        return base64.b64encode(pickle.dumps(image_np)).decode('ascii'), cars_involved, injured_people

    return img_base64, 7, ["unprocessed"]

class Crash(Resource):
    def post(self):
        message = request.form['message']
        base64_img = request.form['img']
        id = request.form['id']

        process_img(base64_img)


        return id
