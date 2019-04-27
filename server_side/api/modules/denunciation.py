from api.modules import utils

from flask import Flask, request,Response
from flask_restful import Resource, Api, abort

import json
import os
import base64
from PIL import Image
import cv2
from io import BytesIO
import numpy as np

app = Flask(__name__)
api = Api(app)

db_path = os.path.join(app.root_path, 'databases', 'denunciations.json')
with open(db_path, 'r') as f:
    denunciations = json.load(f)

users_path = os.path.join(app.root_path, 'databases', 'users.json')
with open(users_path, 'r') as f:
    users = json.load(f)

def readb64(base64_string):
    sbuf = BytesIO()

    sbuf.write(base64.b64decode(base64_string))
    pimg = Image.open(sbuf)
    return cv2.cvtColor(np.array(pimg), cv2.COLOR_RGB2BGR)

class Alert(Resource):

    def post(self):
        args = request.form

        cvimg = readb64(args["photo"])
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


class Denunciations(Resource):
    def get(self):
        resp = Response(json.dumps([
            {
                'id'  : v['id'],
                'info': v['info'],
                'priority': v['priority'],
                'location' : v['location']
            }
            for v in denunciations
        ]
        ))
        resp.headers['Access-Control-Allow-Origin'] = '*'

        return resp

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
                'id': len(denunciations) + 1,
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
