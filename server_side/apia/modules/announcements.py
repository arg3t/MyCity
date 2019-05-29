from flask import Flask, request
from flask_restful import Resource, Api
import json

app = Flask(__name__)
api = Api(app)

buses = {}

with open("modules/databases/announcements.json","r") as f:
	announcements = json.loads(f.read())


class Announcement(Resource):

	def get(self):
		return announcements