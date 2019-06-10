from flask import Flask, send_from_directory
from flask_restful import Api

from modules import user_info, voting_system, rating_system, denunciation, navigation, bus_stops, announcements
from modules import utility

app = Flask(__name__)
api = Api(app)

@app.route('/img/<path:path>')
def send_img(path):
    return send_from_directory('images', path)

if __name__ == '__main__':

    context = ('encryption/mycity.crt', 'encryption/mycity-decrypted.key')
    api.add_resource(voting_system.Votings, '/votings', '/votings/')
    api.add_resource(voting_system.Voting, '/votings/<int:voting_id>')
    api.add_resource(voting_system.Vote, '/vote', '/vote/')

    api.add_resource(rating_system.Ratings, '/ratings', '/ratings/')
    api.add_resource(rating_system.Rating, '/ratings/<int:rating_id>', '/ratings/<int:rating_id>/')
    api.add_resource(rating_system.Rate, '/rate', '/rate/')

    api.add_resource(user_info.Users, '/users', '/users/')
    api.add_resource(user_info.User, '/users/<path:user_id>', '/users/<path:user_id>/')
    api.add_resource(user_info.Login, '/login', '/login/')
    api.add_resource(user_info.ReducePoints, '/reduce', '/reduce/')

    # api.add_resource(utility.Resources, '/resources', '/resources/')

    api.add_resource(denunciation.Alert, '/denunciation', '/denunciation/')
    api.add_resource(denunciation.Denunciations, '/denunciations', '/denunciations/')

    api.add_resource(navigation.Transit, '/transit', '/transit/')

    api.add_resource(bus_stops.Bus, '/bus', '/bus/')

    api.add_resource(announcements.Announcement, '/announcements', '/announcements/')

    # api.add_resource(smart_park.Empty, '/parking', '/parking/')

    app.run(host='0.0.0.0', port=5000, ssl_context=context)
