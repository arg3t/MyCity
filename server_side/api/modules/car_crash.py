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

def process_img(img):
    pass

class Crash(Resource):
    def post(self):
        message = request.form['message']
        base64_img = request.form['img']
        id = request.form['id']

        process_img(Image.open(io.BytesIO(base64.b64decode(base64_img))))


        return id
