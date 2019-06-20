from flask import Flask, request, Response
from flask_restful import Resource, Api

import os
from object_detection.utils import label_map_util
from object_detection.utils import visualization_utils as vis_util
from object_detection.utils import ops as utils_ops
from PIL import Image
import base64
import io
import json

import tensorflow as tf
import sys
import numpy as np

switches = {"coco":1, "damage":1}
COCO_MODEL_NAME = "rfcn_resnet101_coco_2018_01_28"
PATH_TO_FROZEN_COCO_GRAPH = 'modules/'+COCO_MODEL_NAME + '/frozen_inference_graph.pb'
PATH_TO_FROZEN_DAMAGE_GRAPH =  'modules/trainedModels/ssd_mobilenet_RoadDamageDetector.pb'


if sys.platform == "win32":
    detection_graph_coco = tf.Graph()
    detection_graph_damage = tf.Graph()
    with detection_graph_coco.as_default():
        od_graph_def = tf.GraphDef()
        with tf.gfile.GFile(PATH_TO_FROZEN_COCO_GRAPH, 'rb') as fid:
            serialized_graph = fid.read()
            od_graph_def.ParseFromString(serialized_graph)
            tf.import_graph_def(od_graph_def, name='')
    with detection_graph_damage.as_default():
        od_graph_def = tf.GraphDef()
        with tf.gfile.GFile(PATH_TO_FROZEN_DAMAGE_GRAPH, 'rb') as fid:
            serialized_graph = fid.read()
            od_graph_def.ParseFromString(serialized_graph)
            tf.import_graph_def(od_graph_def, name='')

    def load_image_into_numpy_array(image):
        (im_width, im_height) = image.size
        return np.array(image.getdata()).reshape(
            (im_height, im_width, 3)).astype(np.uint8)

def run_inference_for_single_image(image, graph,type):
    global switches
    global sess_coco
    global sess_damage
    with graph.as_default():
        if(switches[type]):
            if type == "coco":
                sess_coco = tf.Session()
            elif type == "damage":
                sess_damage = tf.Session()
            switches[type] = 0
        if type == "coco":
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
            output_dict = sess_coco.run(tensor_dict,
                                   feed_dict={image_tensor: image})
            # all outputs are float32 numpy arrays, so convert types as appropriate
            output_dict['num_detections'] = int(output_dict['num_detections'][0])
            output_dict['detection_classes'] = output_dict[
                'detection_classes'][0].astype(np.int64)
            output_dict['detection_boxes'] = output_dict['detection_boxes'][0]
            output_dict['detection_scores'] = output_dict['detection_scores'][0]
            if 'detection_masks' in output_dict:
                output_dict['detection_masks'] = output_dict['detection_masks'][0]
        elif type=="damage":
            image_tensor = graph.get_tensor_by_name('image_tensor:0')
            # Each box represents a part of the image where a particular object was detected.
            detection_boxes = graph.get_tensor_by_name('detection_boxes:0')
            # Each score represent how level of confidence for each of the objects.
            # Score is shown on the result image, together with the class label.
            detection_scores = graph.get_tensor_by_name('detection_scores:0')
            detection_classes = graph.get_tensor_by_name('detection_classes:0')
            num_detections = graph.get_tensor_by_name('num_detections:0')
            # Actual detection.
            (boxes, scores, classes, num) = sess_damage.run(
                [detection_boxes, detection_scores, detection_classes, num_detections],
                feed_dict={image_tensor: image})

            output_dict = {'detection_classes': np.squeeze(classes).astype(np.int32), 'detection_scores': np.squeeze(scores)}

    return output_dict


class Process(Resource):
    def post(self):
        base64_img = request.form['img']
        image = Image.open(io.BytesIO(base64.b64decode(base64_img)))
        type = request.form["type"]
        image_np = load_image_into_numpy_array(image)
        image_np_expanded = np.expand_dims(image_np, axis=0)
        if type == "coco":
            output_dict = run_inference_for_single_image(image_np_expanded, detection_graph_coco,type)
        elif type == "damage":
            output_dict = run_inference_for_single_image(image_np_expanded, detection_graph_damage,type)


        return json.dumps(output_dict,cls=NumpyEncoder)


class NumpyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, np.ndarray):
            return obj.tolist()
        return json.JSONEncoder.default(self, obj)
