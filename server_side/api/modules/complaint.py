from flask import Flask, request
from flask_restful import Resource, Api, abort

import json
import io
import base64
from PIL import Image
import sys
import datetime
import cv2
import ssl
from urllib.parse import urlencode
from urllib.request import Request, urlopen


if sys.platform == "win32":
	import tensorflow as tf
	import numpy as np
	import pickle

	sys.path.insert(0, r'C:\Users\Tednokent01\Downloads\MyCity\traffic_analyzer')
	from object_detection.utils import label_map_util

	from object_detection.utils import visualization_utils as vis_util

app = Flask(__name__)
api = Api(app)

context = ssl._create_unverified_context()

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

	# List of the strings that is used to add correct label for each box.
	PATH_TO_LABELS = 'modules/trainedModels/crack_label_map.pbtxt'

	NUM_CLASSES = 8
	label_map = label_map_util.load_labelmap(PATH_TO_LABELS)
	categories = label_map_util.convert_label_map_to_categories(label_map, max_num_classes=NUM_CLASSES, use_display_name=True)
	category_index = label_map_util.create_category_index(categories)

def load_image_into_numpy_array(image):
	(im_width, im_height) = image.size
	return np.array(image.getdata()).reshape(
			(im_height, im_width, 3)).astype(np.uint8)

def process_img(img_base64):

	if sys.platform == "win32":

		url = 'https://127.0.0.1:5000/ai' # Set destination URL here
		post_fields = {'img': img_base64,"type":"damage"}     # Set POST fields here

		request = Request(url, urlencode(post_fields).encode())
		img = load_image_into_numpy_array(Image.open(io.BytesIO(base64.b64decode(img_base64))))

		output_dict = json.loads(json.loads(urlopen(request, context=context).read()))
		print(output_dict)
		vis_util.visualize_boxes_and_labels_on_image_array(
				img,
				np.array(output_dict['detection_boxes']),
				output_dict['detection_classes'],
				output_dict['detection_scores'],
				category_index,
				instance_masks=output_dict.get('detection_masks'),
				use_normalized_coordinates=True,
				line_thickness=8,
				min_score_thresh=0.3
		)
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

		buffered = io.BytesIO()
		img = Image.fromarray(img, 'RGB')
		img.save(buffered, format="JPEG")
		img_str = base64.b64encode(buffered.getvalue())
		return img_str.decode("ascii"),priority,defects

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
			json.dump(complaints, complaints_file, indent=2)


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
			json.dump(complaints, complaints_file, indent=2)
		return
