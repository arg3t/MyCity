import os
import json

from api.modules import utils

from flask import Flask, request
from flask_restful import Resource, Api, abort

app = Flask(__name__)
api = Api(app)
db_path = os.path.join(app.root_path, 'databases', 'votings.json')
user_db = os.path.join(app.root_path, 'databases', 'users.json')

with open(db_path, 'r') as f:
    votings = json.load(f)

with open(user_db, 'r') as f:
    users = json.load(f)

class Votings(Resource):
    def get(self):
        voting = [
            {
                'id'  : v['id'],
                'name': v['name'],
                'desc': v['desc'],
                'img' : v['img']
            }
            for v in votings
        ]
        return voting

    def post(self):
        """
        Example POST Data:
        name=<voting_name>&
        desc=<voting_desc>& # OPTIONAL
        img=<voting_img>&   # OPTIONAL
        votes=[
            {
                "name": "<vote_name>",
                "desc": "<vote_desc>" # OPTIONAL
            },
            (...)
        ]

        """
        args = request.form
        voting_id = len(votings) + 1
        voting = {
            'id': voting_id,
            'name': args['name'],
            'desc': args.get('desc'),
            'img' : args.get('img'),
            'voters': [],
            'votes': [
                {
                    'id'  : k + 1,
                    'name': vote['name'],
                    'desc': vote.get('desc'),
                    'votes': 0
                }
                for k, vote in enumerate(json.loads(args['votes']))
            ]
        }

        votings.append(voting)

        with open(db_path, 'w') as f:
            json.dump(votings, f, indent=4)

        return {'message': 'Success'}


class Voting(Resource):
    def get(self, voting_id):
        try:
            voting = votings[voting_id - 1].copy()
            for i in range(len(voting['votes'])):
                voting['votes'][str(i + 1)]['votes'] = None
            voting['voters'] = None

            return voting
        except:
            abort(404, error="Voting {} doesn't exist".format(voting_id))

class Vote(Resource):
    def post(self):
        """
        Example URL Query:
        /vote?voting_id=<voting_id>&vote_id=<vote_id>&voter_id=<user_id>
        """

        voter_id = request.form['voter_id']
        voting_id = int(request.form['voting_id']) - 1
        if utils.find_by_id( users, voter_id ):
            if voter_id not in votings[voting_id]['voters']:
                vote_id = int(request.args['vote_id'])
                votings[voting_id]['votes'][str(vote_id)]['votes'] += 1
                votings[voting_id]['voters'].append(voter_id)
                with open(db_path, 'w') as f:
                    json.dump(votings, f, indent=4)

                return {'message': 'Success'}

            return {'error': 'Already voted'}

        return {'error': 'User doesn\'t exists'}

if __name__ == '__main__':
    api.add_resource(Votings, '/votings', '/votings/')
    api.add_resource(Voting, '/votings/<int:voting_id>', '/votings/<int:voting_id>/')
    api.add_resource(Vote, '/vote', '/vote/')

    app.run(host='0.0.0.0', port=5000)
