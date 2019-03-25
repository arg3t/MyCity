import os
import json
import hashlib

from flask import Flask, request
from flask_restful import Resource, Api, abort

app = Flask(__name__)
api = Api(app)

with open(os.path.join(app.root_path, 'users.json'), 'r') as f:
    users = json.load(f)

def md5(s):
    return hashlib.md5(s.encode()).hexdigest()

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
        password=<password>&
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
            'password': md5(args['password']),
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
            user = users[user_id - 1]
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
        args = request.form
        username = args['username']
        passsword = md5(args['password'])
        for user in users:
            if user['username'] == username:
                if user['password'] == passsword:
                    return {'message': 'Login successful!'}

                return {'error': 'Wrong password!'}

        return {'error': 'User not found!'}

if __name__ == '__main__':
    api.add_resource(Users, '/users', '/users/')
    api.add_resource(User, '/users/<int:user_id>', '/users/<int:user_id>/')
    api.add_resource(Login, '/login', '/login/')

    app.run(host='0.0.0.0', port=5000)