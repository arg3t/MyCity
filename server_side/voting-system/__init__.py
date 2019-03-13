import os
from flask import Flask

def create_app():
	app = Flask(__name__)
	app.config.from_mapping(
		SECRET_KEY='dev',
		DATABASE=os.path.join(app.instance_path, 'voting-system.sqlite')
	)
	
	app.config.from_pyfile('config.py', silent=True)
	try:
		os.makedirs(app.instance_path)
	except OSError:
		pass # Already exists
		
	@app.route('/votings')
	def hello():
		return 'Hello, world!'
		
	return app