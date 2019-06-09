from flask import Flask, render_template, send_from_directory
from flask_socketio import SocketIO, emit

import os
import json
app = Flask(__name__)
app.config['SECRET_KEY'] = 'yigit007'
socketio = SocketIO(app)

src_path , file_list = "../server_side/api/modules/databases/",["denunciations","complaints"]
changed = {}
for file in file_list:
	changed[file] = os.stat(os.path.join(src_path,file+".json"))

def file_check(file):
	src = os.path.join(src_path,file+".json")
	if changed[file] != os.stat(src):
		print("[INFO]: Changed " + file)
		changed[file] = os.stat(src)
		with open(src,"r") as f:
			json_data = json.loads(f.read())
		return True,json.dumps(json_data)
	return False,""

@socketio.on("check",namespace="/denunciations_socket")
def denunciation_handle(msg):

	change,data = file_check("denunciations")
	if change:
		emit("new", data, namespace="/denunciations_socket")


@socketio.on('connect', namespace='/denunciations_socket')
def handle_my_custom_namespace_event():
	print("[INFO]: Received socket connection!")

	src = os.path.join(src_path,"denunciations.json")
	with open(src,"r") as f:
		json_data = json.loads(f.read())

	emit("new", json.dumps(json_data), namespace="/denunciations_socket")

@app.route('/gui/<path:path>')
def send_img(path):
	return send_from_directory('interface/UserData', path)

if __name__ == '__main__':
	socketio.run(app,port=4000,debug=True)