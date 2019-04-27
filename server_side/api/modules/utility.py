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
        """
        POST Data:
        user_id=<id>&type=<electricity|water>
        :return:
        """
        user = utils.find_by_id(users.values(), request.form['user_id'])
        if request.form["type"] == "electricity":
            usage = user['daily_electricity_usage']
            ideal = user['ideal_electricity_usage']

            diff = [abs(a-b) for a in usage for b in ideal]

            sum_diff = sum(diff)

            point = 10 - (sum_diff * 0.0001)
            eff = point * 10
            bill = ( user['electricity_bill'] * eff)/100
            return {
                'daily_electricity_usage': usage,
                'ideal_electricity_usage': ideal,
                'points': point,
                'efficiency': eff,
                'bill': bill
            }

        elif request.form["type"] == "water":
            usage = user['daily_water_usage']
            ideal = user['ideal_water_usage']

            diff = [abs(a-b) for a in usage for b in ideal]

            sum_diff = sum(diff)

            point = 10 - (sum_diff * 0.0001)
            eff = point * 10
            bill = (user['water_bill'] * eff)/100

            return {
                'daily_water_usage': usage,
                'ideal_water_usage': ideal,
                'point': point,
                'efficiency': eff,
                'bill': bill
            }

if __name__ == '__main__':
    api.add_resource(Resources, '/resources', '/resources/')

    app.run(host='0.0.0.0', port=5000)
