import os
import json

from flask import Flask, request
from flask_restful import Resource, Api, abort

app = Flask(__name__)
api = Api(app)

with open(os.path.join(app.root_path, 'votings.json'), 'r') as f:
    votings = json.load(f)

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

        with open(os.path.join(app.root_path, 'votings.json'), 'w') as f:
            json.dump(votings, f, indent=4)

        return voting


class Voting(Resource):
    def get(self, voting_id):
        try:
            return votings[voting_id - 1]
        except:
            abort(404, error="Voting {} doesn't exist".format(voting_id))

class Vote(Resource):
    def get(self):
        """
        Example URL Query:
        /vote?voting_id=<voting_id>&vote_id=<vote_id>
        """
        voting_id = int(request.args['voting_id'])
        vote_id = int(request.args['vote_id'])
        votings[voting_id - 1]['votes'][vote_id - 1]['votes'] += 1
        with open(os.path.join(app.root_path, 'votings.json'), 'w') as f:
            json.dump(votings, f, indent=4)

        return votings[voting_id - 1]

if __name__ == '__main__':
    api.add_resource(Votings, '/votings', '/votings/')
    api.add_resource(Voting, '/votings/<int:voting_id>', '/votings/<int:voting_id>/')
    api.add_resource(Vote, '/vote', '/vote/')

    app.run(host='0.0.0.0', port=5000)
