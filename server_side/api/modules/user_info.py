import os
import json

from api.modules import utils

from flask import Flask, request
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
            'password': utils.md5( args[ 'password' ] ),
            'stats': {
                'bus_usage_week': 0,
                'bus_usage_month': 0,
                'bus_usage_year': 0
            },
            'points': 0
        }

        users.append(user)

        with open(db_path, 'w') as f:
            json.dump(users, f, indent=4)

        return user

class User(Resource):
    def get(self, user_id):
        try:
            user = utils.find_by_id( users, user_id )
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
        passsword = utils.md5( args[ 'password' ] )

        if not username in users:
            return [False,{}]

        user = (users[username]).copy()
        if user['password'] == passsword:
            user.pop("password")
            return [True,json.dumps(user)]
        else:
            return [False,{}]




if __name__ == '__main__':
    api.add_resource(Users, '/users', '/users/')
    api.add_resource(User, '/users/<path:user_id>', '/users/<path:user_id>/')
    api.add_resource(Login, '/login', '/login/')

    app.run(host='0.0.0.0', port=5000)