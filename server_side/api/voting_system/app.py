import os
import ssl
import json

from flask import Flask, send_from_directory
from flask_restful import Resource, Api, abort, reqparse

app = Flask(__name__)
api = Api(app)

parser = reqparse.RequestParser()
parser.add_argument('name')
parser.add_argument('desc')
parser.add_argument('img')
parser.add_argument('votes')

with open(os.path.join(app.root_path, 'votings.json'), 'r') as f:
	votings_json = json.load(f)
	votings = {
		v['id']: v
		for v in votings_json
	}

class Votings(Resource):
	def get(self):
		voting = [
			{
				'id'  : v['id'],
				'name': v['name'],
				'desc': v['desc'],
				'img' : v['img']
			}
			for k, v in votings.items()
		]
		return voting
		
	def post(self):
		args = parser.parse_args()
		voting_id = max(votings.keys()) + 1
		voting = {
			'id': voting_id,
			'name': args['name'],
			'desc': args['desc'],
			'img' : args['img'],
			'votes': [
				{
					'id': k,
					'name': vote['name'],
					'desc': vote['desc'],
					'votes': 0
				}
				for k, vote in enumerate(json.loads(args['votes']))
			]
		}
		
		votings[voting_id] = voting
		votings_json.append(voting)
		with open(os.path.join(app.root_path, 'votings.json'), 'w') as f:
			json.dump(votings_json, f, indent=4)
		
		return voting
		

class Voting(Resource):
	def get(self, voting_id):
		try:
			return votings[voting_id]
		except:
			abort(404, error="Voting {} doesn't exist".format(voting_id)) 


api.add_resource(Votings, '/votings', '/votings/')
api.add_resource(Voting, '/votings/<int:voting_id>')

@app.route('/img/<path:path>')
def send_img(path):
	return send_from_directory('images', path)

if __name__ == '__main__':
	app.run(host='0.0.0.0', port=5000, debug=True)

