from modules import utils

from flask import Flask, request,Response
from flask_restful import Resource, Api

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
        reporter = args['id']
        user = utils.find_by_id(users.values(), reporter)
        trust = int(user["trustability"])
        if args["accepted"] == "true" or trust > 20:
            photo = args["photo"]
            if utils.find_by_id(users.values(), reporter):
                denunciation_info = args['note']
                denunciation_priority = 5
                denunciation_location = {
                    "latitude": float(args['latitude']),
                    "longitude": float(args['longitude'])
                }

                denunciation = {
                    'id': len(denunciations) + 1,
                    'reporter': reporter,
                    'emergency': args['emergency'],
                    'info': denunciation_info,
                    'photo': photo,
                    'priority': denunciation_priority,
                    'location': denunciation_location
                }

                denunciations.append(denunciation)

                with open(db_path, 'w') as f:
                    json.dump(denunciations, f, indent=2)

                return {'success': True}
            else:
                return {'error': 'User doesn\'t exists'}
        else:
            return {"success": False, "penalty": "{}".format(100*(20-trust))}


class Denunciations(Resource):
    def get(self):
        resp = Response(json.dumps([
            {
                'id': v['id'],
                "reporter": v["reporter"],
                'info': v['info'],
                'priority': v['priority'],
                'emergency': v['emergency'],
                'photo': v['photo'],
                'location': v['location']
            }
            for v in denunciations
        ]
        ))
        resp.headers['Access-Control-Allow-Origin'] = '*'

        return resp
