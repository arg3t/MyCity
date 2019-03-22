import os
import json

from flask import Flask, request
from flask_restful import Resource, Api, abort

app = Flask(__name__)
api = Api(app)

with open(os.path.join(app.root_path, 'users.json'), 'r') as f:
    users = json.load(f)

class Users(Resource):
    def get(self):
        user = [
            {
                'id'  : v['id'],
                'username': v['username']
            }
            for v in users
        ]
        return user

    def post(self):
        """
        Example POST Data:
        username=<username>&
        realname=<realname>& # OPTIONAL
        avatar=<avatar_url>& # OPTIONAL
        """
        args = request.form
        user_id = len(users) + 1
        user = {
            'id': user_id,
            'username': args['username'],
            'realname': args.get('realname'),
            'avatar' : args.get('avatar'),
            'stats': {
                'bus_usage_week': 0,
                'bus_usage_month': 0,
                'bus_usage_year': 0
            },
            'points': 0
        }

        users.append(user)

        with open(os.path.join(app.root_path, 'users.json'), 'w') as f:
            json.dump(users, f, indent=4)

        return user

class User(Resource):
    def get(self, user_id):
        try:
            return users[user_id - 1]
        except:
            abort(404, error="User {} doesn't exist".format(voting_id))

if __name__ == '__main__':
    api.add_resource(Users, '/users', '/users/')
    api.add_resource(User, '/users/<int:user_id>', '/users/<int:user_id>/')

    app.run(host='0.0.0.0', port=5000)