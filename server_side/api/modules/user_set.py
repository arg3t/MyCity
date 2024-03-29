import os
import sys,getpass
import json
import base64
import face_recognition

import utils

with open('databases/users.json') as f:
	users = json.load(f)

for file in os.listdir("../images"):
	if file.endswith(".png") or file.endswith(".jpg"):
		uid = file.split('.')[0]
		if len(uid) == 32 and utils.find_by_id(users.values(), uid):
			full_path = os.path.join("../images", file)
			image = face_recognition.load_image_file(full_path)
			with open(full_path, 'rb') as f:
				base64_image = base64.b64encode(f.read())

			if getpass.getuser() == "tedankara":
				face_locations = face_recognition.face_locations(image)[0]

			face_encoding = face_recognition.face_encodings(image)[0]
			for k in users.keys():
				if users[k]['id'] == uid:
					users[k]['image'] = base64_image.decode()
					users[k]['face_locations'] = face_locations
					users[k]['face_encoding'] = list(face_encoding)

			with open('databases/users.json', 'w') as f:
				users = json.dump(users, f, indent=2)

			os.remove(full_path)
