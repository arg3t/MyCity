from flask import Flask, jsonify, request, abort,Response
from multiprocessing import Process
from PIL import Image
from io import BytesIO
from imutils.video import VideoStream

import requests
import cv2
import pickle
import base64
import json
from pyzbar import pyzbar
import subprocess
import urllib3
import imutils

urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

app = Flask(__name__)
user = {}
image = None

def im2str(im):
    crop_img = Image.fromarray(im, "RGB")
    buffered = BytesIO()
    crop_img.save(buffered, format="JPEG")
    img = base64.b64encode(buffered.getvalue()).decode("ascii")
    return img

vs=VideoStream().start()
barcodeData_prev = 0

@app.route('/get')
def get_qr():
	global user
	global image
	global barcodeData_prev

	frame = vs.read()
	img = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
	gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
	barcode = pyzbar.decode(gray)
	if len(barcode) > 0:
		barcode = barcode[0]
		(x, y, w, h) = barcode.rect
		barcodeData = barcode.data.decode("utf-8")
		barcodeType = barcode.type
		if barcodeData_prev == 0 or barcodeData_prev != barcodeData:
			barcodeData_prev = barcodeData
			r = requests.get('https://192.168.2.203:5000/users/{}'.format(barcodeData), verify=False)
			user = json.loads(r.text)
		text = "{} ({})".format(barcodeData, barcodeType)
		cv2.putText(img, text, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX,
					0.5, (255, 0, 0), 2)
		cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
	else:
		user = {}
	result = subprocess.check_output(['vcgencmd', 'measure_temp'])
	temp = result.split("=")[1][:-2] +"C"
	image = im2str(img)
	resp = Response(json.dumps({"user":user,"img":image,"temp":temp}))
	resp.headers['Access-Control-Allow-Origin'] = '*'

	return resp


app.run(host='0.0.0.0', port=3000)