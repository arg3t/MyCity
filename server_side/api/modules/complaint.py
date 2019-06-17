from flask import Flask, request
from flask_restful import Resource, Api, abort

import json
import io
import base64
from PIL import Image
import sys
import datetime
import cv2

if sys.platform == "win32":
	import tensorflow as tf
	import numpy as np
	import pickle

	sys.path.insert(0, r'C:\Users\Tednokent01\Downloads\MyCity\traffic_analyzer')
	from utils import label_map_util

	from utils import visualization_utils as vis_util

app = Flask(__name__)
api = Api(app)

score_dict = {
	1: 1,
	2: 1,
	3: 1,
	4: 1,
	5: 1,
	6: 1,
	7: 1,
	8: 1
}

with open("modules/databases/complaints.json","r") as f:
	complaints = json.load(f)

if sys.platform == "win32":
	# Path to frozen detection graph. This is the actual model that is used for the object detection.
	PATH_TO_CKPT =  'modules/trainedModels/ssd_mobilenet_RoadDamageDetector.pb'

	# List of the strings that is used to add correct label for each box.
	PATH_TO_LABELS = 'modules/trainedModels/crack_label_map.pbtxt'

	NUM_CLASSES = 8

	detection_graph = tf.Graph()
	with detection_graph.as_default():
		od_graph_def = tf.GraphDef()
		with tf.gfile.GFile(PATH_TO_CKPT, 'rb') as fid:
			serialized_graph = fid.read()
			od_graph_def.ParseFromString(serialized_graph)
			tf.import_graph_def(od_graph_def, name='')

	label_map = label_map_util.load_labelmap(PATH_TO_LABELS)
	categories = label_map_util.convert_label_map_to_categories(label_map, max_num_classes=NUM_CLASSES, use_display_name=True)
	category_index = label_map_util.create_category_index(categories)

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

		output_dict = {'detection_classes': np.squeeze(classes).astype(np.int32), 'detection_scores': np.squeeze(scores)}
		defects = []
		for index, i in enumerate(output_dict['detection_classes']):
			score = output_dict['detection_scores'][index]
			if score > 0.3:
				defects.append(i)

		priority = 0
		for i in defects:
			priority += score_dict[i]

		if priority > 10:
			priority = 10

		_, buffer = cv2.imencode('.jpg', image_np)
		return base64.b64encode(buffer).decode('ascii'),priority,defects

	return img_base64, 7,["unprocessed"]

class Complaint(Resource):
	def post(self):
		args = request.form.to_dict()

		complaint = args

		complaint["response"] = {"status":False}

		img_process,priority,tags = process_img(complaint["img"])

		complaint["img"] = img_process
		complaint["response"]["priority"] = str(priority)
		complaint["tags"] = list(map(str, tags))
		complaint["datetime"] = datetime.datetime.now().strftime('%b-%d-%I:%M %p-%G')

		try:
			complaints[complaint["id"]].append(complaint)
		except KeyError:
			complaints[complaint["id"]] = [complaint]

		del complaints[complaint["id"]][-1]["id"]
		with open('modules/databases/complaints.json', 'w') as complaints_file:
			json.dump(complaints, complaints_file, indent=4)


class Complaints(Resource):
	def post(self):
		id = request.form["id"]
		return complaints[id]


class ComplaintsUpdate(Resource):
	def get(self):
		args = request.args
		complaints[args.get("id")][int(args.get("index"))]["response"]["message"] = args.get("message")
		complaints[args["id"]][int(args["index"])]["response"]["status"] = True
		with open('modules/databases/complaints.json', 'w') as complaints_file:
			json.dump(complaints, complaints_file, indent=4)
		return

