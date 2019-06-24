import os
import copy
import json

from modules import utils

from flask import Flask, request, Response
from flask_restful import Resource, Api, abort

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
            'password': utils.md5(args['password']),
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
            json.dump(users, f, indent=2)

        return user

class User(Resource):
    def get(self, user_id):
        try:
            user = copy.deepcopy(utils.find_by_id(users.values(), user_id))
            if not user:
                raise Exception('User not found!')
            del user['password']
            resp = Response(json.dumps(user))
            resp.headers['Access-Control-Allow-Origin'] = '*'
            return resp
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
        password = utils.md5(args['password'])

        if not username in users:
            return [False, {}]

        user = copy.deepcopy(users[username])
        if user['password'] == password:
            del user["password"]
            return [True, json.dumps(user)]
        else:
            return [False, {}]


class ReducePoints(Resource):
    def post(self):
            user_id = request.form['id']
            user = utils.find_by_id(users.values(), user_id)
            if user:
                username = ''
                for k, v in users.items():
                    if user_id == v['id']:
                        username = k

                users[username]['points'] -= int(request.form['reduce'])
                with open(db_path, 'w') as f:
                    json.dump(users, f, indent=2)
            else:
                abort(404, error="User {} doesn't exist".format(user_id))


if __name__ == '__main__':
    api.add_resource(Users, '/users', '/users/')
    api.add_resource(User, '/users/<path:user_id>', '/users/<path:user_id>/')
    api.add_resource(Login, '/login', '/login/')

    app.run(host='0.0.0.0', port=5000)
