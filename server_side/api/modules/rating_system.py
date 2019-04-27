import os
import copy
import json

from api.modules import utils

from flask import Flask, request, Response
from flask_restful import Resource, Api, abort

app = Flask(__name__)
api = Api(app)
db_path = os.path.join(app.root_path, 'databases', 'ratings.json')
user_db = os.path.join(app.root_path, 'databases', 'users.json')

with open(db_path, 'r') as f:
    ratings = json.load(f)

with open(user_db, 'r') as f:
    users = json.load(f)


class Ratings(Resource):
    def get(self):

        resp = Response(json.dumps([
            {
                'id'  : v['id'],
                'name': v['name'],
                'desc': v['desc'],
                'img' : v['img']
            }
            for v in ratings
        ]))
        resp.headers['Access-Control-Allow-Origin'] = '*'

        return resp

    def post(self):
        """
        Example POST Data:
        latitude=<latitude>&
        longitude=<longitude>
        """
        latitude = float(request.form['latitude'])
        longitude = float(request.form['longitude'])
        ret_data = []

        for rating in ratings:
            diff = rating['location']['diff']
            rating_latitude = rating['location']['latitude']
            rating_longitude = rating['location']['longitude']

            if (rating_latitude - diff) < latitude < (rating_latitude + diff) and \
               (rating_longitude - diff) < longitude < (rating_longitude + diff):
                ret_data.append({
                    'id': rating['id'],
                    'name': rating['name'],
                    'desc': rating['desc'],
                    'img': rating['img']
                })

        return ret_data

    # def post(self):
    #    """
    #    Example POST Data:
    #    name=<rating_name>&
    #    desc=<rating_desc>& # OPTIONAL
    #    img=<rating_img>&   # OPTIONAL
    #    """
    #    args = request.form
    #    rating_id = len(ratings) + 1
    #    rating = {
    #        'id': rating_id,
    #        'name': args['name'],
    #        'desc': args.get('desc'),
    #       'img' : args.get('img'),
    #        'rates': []
    #    }
    #
    #    ratings.append(rating)
    #
    #    with open(db_path, 'w') as f:
    #        json.dump(ratings, f, indent=4)
    #
    #    return rating


class Rating(Resource):
    def get(self, rating_id):
        try:
            resp = Response(json.dumps(ratings[rating_id - 1]))
            resp.headers['Access-Control-Allow-Origin'] = '*'

            return resp

        except:
            abort(404, error="Rating {} doesn't exist".format(rating_id))


class Rate(Resource):
    def post(self):
        """
        Example POST Data:
        rating_id=<rating_id>&
        score=<score>&
        note=<note>& # ADDITIONAL
        rater_id=<user_id>
        """
        if utils.find_by_id( users.values(), request.form[ 'rater_id' ] ):
            rating_id = int(request.form['rating_id'])
            score = int(request.form['score'])
            if 0 >= score >= 10:
                abort(500, 'Score should be between 0 and 10')
            note = request.form.get('note')
            ratings[rating_id - 1]['rates'][request.form['rater_id']] = {
                'id': len(ratings[rating_id - 1]['rates']) + 1,
                'rater': request.form['rater_id'],
                'score': score,
                'note': note
            }
            with open(db_path, 'w') as f:
                json.dump(ratings, f, indent=4)

            return {'message': 'Success'}

        return {'error': 'User doesn\'t exists'}


if __name__ == '__main__':
    api.add_resource(Ratings, '/ratings', '/ratings/')
    api.add_resource(Rating, '/ratings/<int:rating_id>', '/ratings/<int:rating_id>/')
    api.add_resource(Rate, '/rate', '/rate/')

    app.run(host='0.0.0.0', port=5000)
