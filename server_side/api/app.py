from flask import Flask, send_from_directory
from flask_restful import Resource, Api

from voting_system import voting_system
from rating_system import rating_system
from user_info import user_info

app = Flask(__name__)
api = Api(app)



@app.route('/img/<path:path>')
def send_img(path):
    return send_from_directory('images', path)

if __name__ == '__main__':

    context = ('encryption/mycity.crt','encryption/mycity-decrypted.key')
    api.add_resource(voting_system.Votings, '/votings', '/votings/')
    api.add_resource(voting_system.Voting, '/votings/<int:voting_id>')
    api.add_resource(voting_system.Vote, '/vote', '/vote/')

    api.add_resource(rating_system.Ratings, '/ratings', '/ratings/')
    api.add_resource(rating_system.Rating, '/ratings/<int:rating_id>', '/ratings/<int:rating_id>/')
    api.add_resource(rating_system.Rate, '/rate', '/rate/')

    api.add_resource(user_info.Users, '/users', '/users/')
    api.add_resource(user_info.User, '/users/<int:user_id>', '/users/<int:user_id>/')
    api.add_resource(user_info.Login, '/login', '/login/')
    app.run(host='0.0.0.0', port=5000, ssl_context=context)
