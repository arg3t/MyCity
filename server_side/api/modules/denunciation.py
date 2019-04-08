from flask import Flask, request
from flask_restful import Resource, Api, abort

import json
import os

app = Flask(__name__)
api = Api(app)

db_path = os.path.join(app.root_path, 'databases', 'users.json')
with open(db_path, 'r') as f:
	users = json.load(f)


class Alert(Resource):

	def post(self):
		args = request.form
		username= ""
		for user in users:
			if users[user]["id"] == args["id"]:
				username=user
				break
		trust = int(users[username]["trustability"])
		if trust > 20 or args["accepted"] == "true":
			return {"success":True}
		else:
			return {"success":False,"penalty":"{}".format(100*(20-trust))}
