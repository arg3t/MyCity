import os
import copy
import json
import base64

import pyDes
import qrcode

from api.modules import utils

from flask import Flask, request
from flask_restful import Resource, Api, abort

enc = pyDes.triple_des(b'Kz\n\x1a\xc1~\x05#\xf9\xad\xc8\xa2\x15\xd5J\x89\xe4RT\x8d\xb3?\x93\x1c')
app = Flask(__name__)
api = Api(app)
db_path = os.path.join(app.root_path, 'databases', 'users.json')

with open(db_path, 'r') as f:
    users = json.load(f)

class Users(Resource):
    def post(self):
        """
        Example POST Data:
        username=<username>&
        password=<password>&
        realname=<realname>& # OPTIONAL
        avatar=<avatar_url>& # OPTIONAL
        """
        args = request.form
        user_id = utils.generate_id()
        user = {
            'id': user_id,
            'username': args['username'],
            'realname': args.get('realname'),
            'avatar' : args.get('avatar'),
            'password': utils.md5( args[ 'password' ] ),
            'stats': {
                'bus_usage_week': 0,
                'bus_usage_month': 0,
                'bus_usage_year': 0
            },
            'daily_electricity_usage': [],
            'points': 0
        }

        users.append(user)

        with open(db_path, 'w') as f:
            json.dump(users, f, indent=4)

        return user

class User(Resource):
    def get(self, user_id):
        try:
            user = utils.find_by_id( users.values(), user_id )
            if not user:
                raise Exception('User not found!')
            del user['password']
            return user
        except:
            abort(404, error="User {} doesn't exist".format(user_id))

class Login(Resource):
    def post(self):
        """
        Example POST Data:
        username=<username>&
        password=<password>
        """
        #Password for efe is 12345
        args = request.form
        username = args['username']
        password = utils.md5( args[ 'password' ] )

        if not username in users:
            return [False, {}]

        user = copy.deepcopy(users[username])
        if user['password'] == password:
            del user["password"]
            return [True, json.dumps(user)]
        else:
            return [False, {}]

class QRCode(Resource):
    def post(self):
        """
        POST Data:
        id=<user_id>
        """
        user_id = request.form['id']
        if utils.find_by_id(users.values(), user_id):
            image_path = os.path.join(app.root_path, '..', 'images', user_id + '_qr' + '.png')
            if not os.path.exists(image_path):
                encrypted_id = enc.encrypt(user_id, padmode=2)
                img = qrcode.make(base64.b64encode(encrypted_id).decode('utf-8'))
                img.save(image_path)

            return '/img/' + user_id + '_qr' + '.png'
        else:
            abort(404, error="User {} doesn't exist".format(user_id))

class QRRead(Resource):
    def post(self):
        """
        POST Data:
        qr_data=<qr_data>
        """

        qr_data = base64.b64decode(request.form['qr_data'])
        user_id = enc.decrypt(qr_data, padmode=2)
        return utils.find_by_id(users.values(), user_id.decode())

if __name__ == '__main__':
    api.add_resource(Users, '/users', '/users/')
    api.add_resource(User, '/users/<path:user_id>', '/users/<path:user_id>/')
    api.add_resource(Login, '/login', '/login/')

    app.run(host='0.0.0.0', port=5000)
