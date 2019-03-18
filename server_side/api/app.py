from flask import Flask
from flask_restful import Resource, Api

from voting_system import voting_system
from rating_system import rating_system

app = Flask(__name__)
api = Api(app)

@app.route('/img/<path:path>')
def send_img(path):
    return send_from_directory('images', path)

if __name__ == '__main__':
    api.add_resource(voting_system.Votings, '/votings', '/votings/')
    api.add_resource(voting_system.Voting, '/votings/<int:voting_id>')
    api.add_resource(voting_system.Vote, '/vote', '/vote/')

    api.add_resource(rating_system.Ratings, '/ratings', '/ratings/')
    api.add_resource(rating_system.Rating, '/ratings/<int:rating_id>', '/ratings/<int:rating_id>/')
    api.add_resource(rating_system.Rate, '/rate', '/rate/')

    app.run(host='0.0.0.0', port=5000)