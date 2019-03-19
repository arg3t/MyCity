import os
import json

from flask import Flask, request
<<<<<<< HEAD
from flask_restful import Resource, Api, abort
=======
<<<<<<< HEAD
from flask_restful import Resource, Api
=======
from flask_restful import Resource, Api, abort
>>>>>>> efe
>>>>>>> yigit

app = Flask(__name__)
api = Api(app)

with open(os.path.join(app.root_path, 'ratings.json'), 'r') as f:
    ratings = json.load(f)

class Ratings(Resource):
    def get(self):
        rating = [
            {
                'id'  : v['id'],
                'name': v['name'],
                'desc': v['desc'],
                'img' : v['img']
            }
            for v in ratings
        ]
        return rating

    def post(self):
        """
        Example POST Data:
        name=<rating_name>&
        desc=<rating_desc>& # OPTIONAL
        img=<rating_img>&   # OPTIONAL
        """
        args = request.form
        rating_id = len(ratings) + 1
        rating = {
            'id': rating_id,
            'name': args['name'],
            'desc': args.get('desc'),
            'img' : args.get('img'),
            'rates': []
        }

        ratings.append(rating)

        with open(os.path.join(app.root_path, 'ratings.json'), 'w') as f:
            json.dump(ratings, f, indent=4)

        return rating

class Rating(Resource):
    def get(self, rating_id):
        try:
            return ratings[rating_id - 1]
        except:
            abort(404, error="Rating {} doesn't exist".format(rating_id))

class Rate(Resource):
    def get(self):
        """
        Example URL Query:
        /rate?
        rating_id=<rating_id>>&
        score=<score>&
        note=<note> # ADDITIONAL
        """
        rating_id = int(request.args['rating_id'])
        score = int(request.args['score'])
        if 0 >= score >= 10:
            abort(500, 'Score should be between 0 and 10')
        note = request.args.get('note')
        ratings[rating_id - 1]['rates'].append({
            'id': len(ratings[rating_id - 1]['rates']) + 1,
            'score': score,
            'note': note
        })
        with open(os.path.join(app.root_path, 'ratings.json'), 'w') as f:
            json.dump(ratings, f, indent=4)

        return ratings[rating_id - 1]

if __name__ == '__main__':
    api.add_resource(Ratings, '/ratings', '/ratings/')
    api.add_resource(Rating, '/ratings/<int:rating_id>', '/ratings/<int:rating_id>/')
    api.add_resource(Rate, '/rate', '/rate/')

    app.run(host='0.0.0.0', port=5000)
