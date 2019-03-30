import os
import copy
import json

from api.modules import utils

from flask import Flask, request
from flask_restful import Resource, Api, abort

app = Flask(__name__)
api = Api(app)
db_path = os.path.join(app.root_path, 'databases', 'users.json')

with open(db_path, 'r') as f:
    users = json.load(f)

class Resources(Resource):
    def post(self):
        user = utils.find_by_id(users.values(), request.form['user_id'])
        return {
            'daily_electricity_usage': user['daily_electricity_usage'],
            'daily_water_usage': user['daily_water_usage']
        }

if __name__ == '__main__':
    api.add_resource(Resources, '/resources', '/resources/')

    app.run(host='0.0.0.0', port=5000)
