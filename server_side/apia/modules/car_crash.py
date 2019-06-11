from modules import utils

from flask import Flask, request, Response
from flask_restful import Resource, Api

import json
import os

app = Flask(__name__)
api = Api(app)

db_path = os.path.join(app.root_path, 'databases', 'crashes.json')
with open(db_path, 'r') as f:
    crashes = json.load(f)

users_path = os.path.join(app.root_path, 'databases', 'users.json')
with open(users_path, 'r') as f:
    users = json.load(f)

class Crash(Resource):
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
                    'id': len(crashes) + 1,
                    'reporter': reporter,
                    'emergency': args['emergency'],
                    'info': denunciation_info,
                    'photo': photo,
                    'plates': args.get('plates'),
                    'injuries': args.get('injuries'),
                    'lines_blocked': args.get('lines_blocked'),
                    'priority': denunciation_priority,
                    'location': denunciation_location
                }

                crashes.append(denunciation)

                with open(db_path, 'w') as f:
                    json.dump(crashes, f, indent=4)

                return {'success': True}
            else:
                return {'error': 'User doesn\'t exists'}
        else:
            return {"success": False, "penalty": "{}".format(100*(20-trust))}

