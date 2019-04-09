from api.modules import utils

from flask import Flask, request
from flask_restful import Resource, Api, abort

import json
import os

app = Flask(__name__)
api = Api(app)

db_path = os.path.join(app.root_path, 'databases', 'denunciations.json')
with open(db_path, 'r') as f:
    denunciations = json.load(f)

users_path = os.path.join(app.root_path, 'databases', 'users.json')
with open(users_path, 'r') as f:
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


class Denounce(Resource):
    def post(self):
        args = request.form
        reporter = args['id']
        if utils.find_by_id(users.values(), reporter):
            denunciation_info = args['info']
            denunciation_priority = args['priority']
            denunciation_location = {
                "latitude": args['latitude'],
                "longitude": args['longitude']
            }

            denunciation = {
                'reporter': reporter,
                'info': denunciation_info,
                'priority': denunciation_priority,
                'location': denunciation_location
            }

            denunciations.append(denunciation)

            with open(db_path, 'w') as f:
                json.dump(denunciations, f, indent=4)

            return denunciation

        else:
            return {'error': 'User doesn\'t exists'}
