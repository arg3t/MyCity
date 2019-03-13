import json
import requests
from stanford_parser.parser import Parser
import nltk
import nltk.corpus as corpus

def getData( searched, data, data_final ):
	if type( searched ) is dict:
		for key in searched:
			if len( searched[ key ] ) == 0:
				data_final[ key ] = data[ key ]
			else:
				getData( searched[ key ], data[ key ], data_final )

	elif type( searched ) is list:
		for key in range( len( searched ) ):
			if len( searched[ key ] ) == 0:
				data_final[ key ] = data[ key ]
			else:
				getData( searched[ key ], data[ key ], data_final )
while True:
	sentence = raw_input("You:")
	parser = Parser()

	#Parse the input
	tokenized = nltk.word_tokenize( sentence )
	stop_words = set( corpus.stopwords.words( 'english' ) )
	cleaned = [ w for w in tokenized if w not in stop_words ]
	tagged = nltk.pos_tag( cleaned )
	named = nltk.ne_chunk( tagged )
	cleaned_sentence = " ".join( cleaned )
	dependencies = parser.parseToStanfordDependencies( cleaned_sentence )
	tupleResult = [ (rel, gov.text, dep.text) for rel, gov, dep in dependencies.dependencies ]

	#Read the files
	files = [open("user_details.json","r"),open('keywords.json', 'r'),open('predefined.json', 'r')]
	defaults = json.loads(files[0].read())
	keywords = json.loads( files[1 ].read() )
	predefined = json.loads(files[2].read())

	print("[TAGGED]: " + str( tagged ) + "\n")
	print("[SPECIAL]: " + str( named ) + "\n")
	print("[CLEAN]: " + str( cleaned_sentence ) + "\n")
	print("[DEPENDENCIES]: " + str( tupleResult ) + "\n")

	wp_word = ""

	for i in range( len( tagged ) ):
		if tagged[ i ][ 1 ] == "WP" or tagged[ i ][ 1 ] == "WRB" or tagged[ i ][ 1 ] == "WP$":
			wp_word = tagged[ i ][ 0 ]

	if sentence in predefined:
		print(predefined[sentence])

	else:

		if not wp_word == "":
			key = ""
			understood = False
			key_connect = ""
			for dep in tupleResult:
				if dep[2] == wp_word:
					key = dep[1]

			for dep in tupleResult:
				if dep[1]==key: key_connect=dep[2]


			for keyword_id in keywords[0 ]:
				if key in keywords[0 ][keyword_id ]:
					key = keyword_id
					understood = True

			if(understood):
				api_details = keywords[ 1 ][ key ]
				params = api_details[ "parameters" ]

				for i in named:
					for j in params:
						try:
							i.label()
							if i.label() == params[ j ]:
								params[j] = i[0]
							else:
								params[j] = defaults[api_details["defaults"][j]]
						except Exception:
							if i[0]==key_connect:
								params[j] = defaults[api_details["defaults"][j]]



				params[ api_details[ 'apikey' ].keys()[ 0 ] ] = api_details[ 'apikey' ][ api_details[ 'apikey' ].keys()[ 0 ] ]
				r = requests.get( url=api_details[ "url" ], params=params )
				data = r.json()
				data_final = {}
				getData(api_details[ "data_to_receive" ], data, data_final)
				out_format = api_details[ "output_format" ]
				out_final = ""

				isvar = False
				for let in out_format:
					if (let == "{"):
						var = ""
						isvar = True
						continue
					if not isvar:
						out_final += let
					else:
						if let == "}":
							out_final += str( data_final[ var ] )
							isvar = False
						else:
							var += let

				print ("[KEYWORD]: " + key + "\n")
				print ("[KEYWORD_ID]: " + key + "\n")
				print (out_final)
			else:
				print("Sorry, I didn't quite get that")
		else:
			print("Sorry, I didn't quite get that")
