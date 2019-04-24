import os
import copy
import json


from flask import Flask, request
from flask_restful import Resource, Api, abort

app = Flask(__name__)
api = Api(app)






class Empty(Resource):
	def get(self):
		try:
			rating = copy.deepcopy(ratings[rating_id - 1])
			del rating['rates']
			return rating
		except:
			abort(404, error="Rating {} doesn't exist".format(rating_id))




