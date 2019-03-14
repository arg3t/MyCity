import ssl

from flask import Flask
from flask_restful import Resource, Api, reqparse, abort

from pymongo import MongoClient
from bson.objectid import ObjectId

app = Flask(__name__)
api = Api(app)

client = MongoClient("mongodb+srv://mycity:mycity123@mycity-3v9y3.mongodb.net/test?retryWrites=true", ssl_cert_reqs=ssl.CERT_NONE)
db = client.voting_system
collection = db.votings

class Votings(Resource):
	def get(self):
		votings = [
			{
				'id'  : str(doc['_id']),
				'name': doc['name'],
				'desc': doc['desc'],
				'img' : doc['img']
			}
			for doc in collection.find({})
		]
		return votings

class Vote(Resource):
	def get(self, voting_id):
		try:
			doc = collection.find_one({'_id': ObjectId(voting_id)})
			doc['_id'] = str(doc['_id'])
			return {
				k.replace('_id', 'id'): v
				for k, v in doc.items()
			}
		except:
			abort(404, error="Voting {} doesn't exist".format(voting_id)) 


api.add_resource(Votings, '/votings')
api.add_resource(Vote, '/votings/<voting_id>')

if __name__ == '__main__':
	app.run(debug=True)
