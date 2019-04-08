from flask import Flask, request
from flask_restful import Resource, Api, abort

import requests
import json

app = Flask( __name__ )
api = Api( app )

bus_data = open("databases/bus.json","a")

def sendRequest( url, raw ):
	headers = {
		"User-Agent": "EGO Genel Mudurlugu-EGO Cepte-3.1.0 GT-I9500 7.1.2",
		"Content-Type": "application/x-www-form-urlencoded",
		"Content-Length": "0" }

	headers[ "Content-Length" ] = str( len( raw ) )

	r = requests.post( url, headers=headers, data=raw )
	content = r.content.decode( "cp1252" )
	content = content.replace( "Ý", "I" )
	content = content.replace( "ý", "i" )
	content = content.replace( "ð", "g" )
	content = content.replace( "þ", "s" )
	content = content.replace( "Þ", "S" )
	return content


conn1 = sendRequest(
	'http://88.255.141.70/mbl/android/connect.asp?SID=0.9672804113380772&VER=3.1.0&LAN=tr&UID=%7BACB78701' \
	'-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=Connect',
	"UID=%7BACB78701-2727-4E9A-AE62-28491D671A7D%7D-130570234&UPS=TRUE" )
conn2 = sendRequest(
	'http://88.255.141.66/mbl/android/connect.asp?SID=0.6654049014198404&VER=3.1.0&LAN=tr&UID=%7BACB78701' \
'-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=Start', "" )
hatlar = sendRequest( 'http://88.255.141.66/mbl/android/action.asp?SID=0.8328642811845514&VER=3.1.0&LAN=tr&UID'
					  '=%7BACB78701-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=Hatlar', "QUERY=" )
hatlar = hatlar.replace( "'", '"' )
hatlar = json.loads( hatlar )

class Transit( Resource ):

	def post( self ):
		args = request.form

		durak = sendRequest(
			"http://88.255.141.66/mbl/android/service.asp?SID=0.09912588645045828&VER=3.1.0&LAN=tr&UID=%7BACB78701" \
			"-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=Otobusler", "DURAK=" + str( args[ "stop" ] ) )
		durak = durak.replace( "'", '"' )
		durak = json.loads( durak )





		return [durak,hatlar]


if __name__ == '__main__':
	api.add_resource( Transit, '/transit', '/transit/' )

	app.run( host='0.0.0.0', port=5000 )
