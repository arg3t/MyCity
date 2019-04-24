from flask import Flask, jsonify, request, abort
from multiprocessing import Process

import requests
import reader
import json

import urllib3
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)

app = Flask(__name__)
user = {}

class Reader():
	def __init__(self):
		read = reader.Read(self)
		p1 = Process(target=read.detect)
		p1.start()

	def received(self, data):
		r = requests.get('https://0.0.0.0:5000/users/{}'.format(data), verify=False)
		requests.get('http://0.0.0.0:3000/set', data={'data': r.text})


qr_reader = Reader()

@app.route('/set')
def set_data():
	global user
	user = json.loads(request.form['data'])
	return ''

@app.route('/get')
def get_qr():
	if user == {}:
		abort(404)
	return jsonify(user)


app.run(host='0.0.0.0', port=3000)