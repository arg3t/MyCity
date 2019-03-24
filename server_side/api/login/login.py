import os
import json
import hashlib

from flask import Flask, request
from flask_restful import Resource, Api, abort



app = Flask(__name__)
api = Api(app)

with open(os.path.join(app.root_path, 'userdata.json'), 'r') as f:
	data = json.load(f)


class Login( Resource ):
	def post(self):

		req = request.args
		print(req)



if __name__ == '__main__':
	api.add_resource(Login, '/login', '/login/')

	app.run(host='0.0.0.0', port=5000)