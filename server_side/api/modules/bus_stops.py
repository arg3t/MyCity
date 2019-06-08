from flask import Flask, request
from flask_restful import Resource, Api, abort

app = Flask(__name__)
api = Api(app)

buses = {}

class Bus(Resource):
	def get(self):
		args = request.args
		if not args["line"] in buses:
			buses[args["line"]] = []
		if not args["stop"] in buses[args["line"]]:
			buses[args["line"]].append(args["stop"])

		return "OK"
