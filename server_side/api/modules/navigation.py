import datetime
import json

import requests
from flask import Flask, request
from flask_restful import Api, Resource

app = Flask(__name__)
api = Api(app)


def send_request(url, raw):
	headers = {
		"User-Agent":  "EGO Genel Mudurlugu-EGO Cepte-3.1.0 GT-I9500 7.1.2",
		"Content-Type":"application/x-www-form-urlencoded", "Content-Length":str(len(raw))
	}

	r = requests.post(url, headers=headers, data=raw)
	content = r.content.decode("cp1252")
	replace_chars = {"Ý":"I", "ý":"i", "ð":"g", "þ":"s", "Þ":"S"}

	for key in replace_chars:
		content.replace(key, replace_chars[key])

	content = content.replace("'", '"')
	content = json.loads(content)
	return content


conn1 = send_request(
		'http://88.255.141.70/mbl/android/connect.asp?SID=0.392509188312098&VER=3.1.0&LAN=tr&UID=%7BACB78701'
		'-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=Connect',
		"UID=%7BACB78701-2727-4E9A-AE62-28491D671A7D%7D-130570234&UPS=TRUE")
conn2 = send_request(
		'http://88.255.141.66/mbl/android/connect.asp?SID=0.392509188312098&VER=3.1.0&LAN=tr&UID=%7BACB78701'
		'-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=Start', "")

bus_data = open("modules/databases/bus.json", "r")

bus_json = json.loads(bus_data.read())

routes = bus_json["stops"]
bus_data.close()
print('\n', conn1, '\n\n', conn2, '\n')


def time_small(t1, t2):
	time1 = datetime.datetime.strptime(t1, "%H:%M")
	time2 = datetime.datetime.strptime(t2, "%H:%M")

	if time1 < time2:
		return False
	else:
		return True


def time_diff(t1, t2):
	time1 = datetime.datetime.strptime(t1, "%H:%M")
	time2 = datetime.datetime.strptime(t2, "%H:%M")
	response = ""
	if abs(time2.hour - time1.hour) < 10:
		response += "0"
		if time2.minute - time1.minute >= 0:
			response += str(abs(time2.hour - time1.hour))
		else:
			response += str(abs(abs(time2.hour - time1.hour)-1))

	response += ":"

	if abs(time2.minute - time1.minute) < 10:
		response += "0"

	response += str(abs(time2.minute - time1.minute))

	return response


def time_sum(t1, t2):
	time1 = datetime.datetime.strptime(t1, "%H:%M")
	time2 = datetime.datetime.strptime(t2, "%H:%M")
	response = ""
	if time2.hour + time1.hour < 10:
		response += "0"
	if time2.minute + time1.minute > 60:
		response += str(time2.hour + time1.hour + 1)
	else:
		response += str(time2.hour + time1.hour)
	response += ":"

	if time2.minute + time1.minute % 60 < 10:
		response += "0"
	response += str((time2.minute + time1.minute) % 60)

	return response


def get_closest_time(times, time):
	closest = [0, 0]
	for i in times[0]:
		if i.hour < time.hour or (i.hour == time.hour and i.minute - 6 < time.minute):
			continue
		else:
			if closest[0] == 0:
				closest[0] = i
				continue
			if abs(i.hour - time.hour) < abs(closest[0].hour - time.hour):
				closest[0] = i
				continue
			elif abs(i.hour - time.hour) == abs(closest[0].hour - time.hour):
				if i.minute - time.minute < closest[0].minute - time.minute:
					closest[0] = i

	if closest[0] == 0:
		return []

	for i in times[1]:
		if i.hour < closest[0].hour or (i.hour == closest[0].hour and i.minute - 6 < closest[0].minute):
			continue
		else:
			if closest[1] == 0:
				closest[1] = i
				continue
			if abs(i.hour - time.hour) < abs(closest[1].hour - time.hour):
				closest[1] = i
				continue
			elif abs(i.hour - time.hour) == abs(closest[1].hour - time.hour):
				if i.minute - time.minute < closest[1].minute - time.minute:
					closest[1] = i
	if closest[1] == 0:
		return []
	return [closest[0].strftime("%H:%M"), closest[1].strftime("%H:%M")]


class Transit(Resource):

	def post(self):
		change = False
		args = request.form
		stops_raw = {"user":{}, "dest":{}}
		stops_arr = {"user":{}, "dest":{}}

		url_closestops = "http://88.255.141.66/mbl/android/action.asp?SID=0.392509188312098&VER=3.1.0&LAN=tr&UID"\
						 "=%7BACB78701"\
						 "-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=YakinDuraklar"

		closestops = send_request(url_closestops, "LAT={0}&LNG={1}&MSF=500".format(args["lat_usr"], args["lng_usr"]))
		for stop in closestops["data"][0]["table"]:
			stops_arr["user"][stop["kod"]] = stop["hatlar"].split(",")

		closestops = send_request(url_closestops, "LAT={0}&LNG={1}&MSF=500".format(args["lat_dest"], args["lng_dest"]))
		for stop in closestops["data"][0]["table"]:
			stops_arr["dest"][stop["kod"]] = stop["hatlar"].split(",")

		one_bus = {k.strip():
					   [dict({x:n for n in stops_arr["user"] for x in stops_arr["user"][n]})[k],
						dict({x:n for n in stops_arr["dest"] for x in stops_arr["dest"][n]})[k]]

				   for k in (list(set([x for n in stops_arr["user"] for x in stops_arr["user"][n]])
								  & set([x for n in stops_arr["dest"] for x in stops_arr["dest"][n]])))}

		travel = {"routes":[], "total":[]}
		time_cur = datetime.datetime.today().time()

		if len(one_bus) > 0:
			for i in one_bus:
				travel["routes"].append([{"name":i, "stops":[one_bus[i][0], one_bus[i][1]]}])

		else:
			routes_arr = {"user":{}, "dest":{}}
			stopno = {}

			for i in stops_arr["user"]:
				for j in stops_arr["user"][i]:
					try:
						routes_arr["user"][j.strip()] = bus_json["stops"][j.strip()]
					except KeyError:
						stops = send_request(
								"http://88.255.141.66/mbl/android/action.asp?SID=0.8737969480190279&VER=3.1.0&LAN=tr"
								"&UID"
								"=%7BACB78701-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=HatBilgileri",
								"KOD={}".format(j.strip()))["data"][0]["table_durak"]
						bus_json["stops"][j.strip()] = [x["kod"] for x in stops]
						routes_arr["user"][j.strip()] = bus_json["stops"][j.strip()]
						for x in stops:
							bus_json["stop_info"][x["kod"]] = {
								"name":    x["ad"],
								"location":x["konum"],
								"lat":     x["lat"],
								"lng":     x["lng"]
							}
							change = True
					stopno[j.strip()] = i

			for i in stops_arr["dest"]:
				for j in stops_arr["dest"][i]:
					try:
						routes_arr["dest"][j.strip()] = bus_json["stops"][j.strip()]
					except KeyError:
						stops = send_request(
								"http://88.255.141.66/mbl/android/action.asp?SID=0.8737969480190279&VER=3.1.0&LAN=tr"
								"&UID"
								"=%7BACB78701-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=HatBilgileri",
								"KOD={}".format(j.strip()))["data"][0]["table_durak"]
						bus_json["stops"][j.strip()] = [x["kod"] for x in stops]
						routes_arr["dest"][j.strip()] = bus_json["stops"][j.strip()]
						for x in stops:
							bus_json["stop_info"][x["kod"]] = {
								"name":    x["ad"],
								"location":x["konum"],
								"lat":     x["lat"],
								"lng":     x["lng"]
							}
							change = True
					stopno[j.strip()] = i

			for i in routes_arr["user"]:
				for j in routes_arr["user"][i]:
					for k in routes_arr["dest"]:
						for x in routes_arr["dest"][k]:
							if j.strip() == x.strip():
								travel["routes"].append([
									{"name":i.strip(), "stops":[stopno[i.strip()], x.strip()]},
									{"name":k.strip(), "stops":[x.strip(), stopno[k.strip()]]}])

		for i in range(len(travel["routes"])):
			for k in range(len(travel["routes"][i])):
				url = "http://88.255.141.66/mbl/android/action.asp?SID=0.4474110208361718&VER=3.1.0&LAN=tr&UID"\
					  "=%7BACB78701-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=DuraktanGecisSaatleri "

				weekno = datetime.datetime.today().weekday()
				times_anal = [[], []]
				for j in range(2):
					try:
						times_raw = bus_json["times"][travel["routes"][i][k]["name"]]
					except Exception:
						bus_json["times"][travel["routes"][i][k]["name"]] = {}

					try:
						times_raw = bus_json["times"][travel["routes"][i][k]["name"]][
							travel["routes"][i][k]["stops"][j]]
					except Exception:
						bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]] = {}

					if weekno < 5:
						try:
							weekday = "HAFTA+%C4%B0%C3%87%C4%B0"
							times_raw =\
								bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
									"weekday"]
						except KeyError:
							bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
								"weekday"] = send_request(url, "HAT={0}&DURAK={1}&TUR={2}".format(
									travel["routes"][i][k]["name"], travel["routes"][i][k]["stops"][j], weekday))[
								"data"][0]["table"]
							times_raw =\
								bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
									"weekday"]
							change = True
					elif weekno == 5:
						try:
							weekday = "CUMARTES%C4%B0"
							times_raw =\
								bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
									"saturday"]
						except KeyError:
							bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
								"saturday"] = send_request(url, "HAT={0}&DURAK={1}&TUR={2}".format(
									travel["routes"][i][k]["name"], travel["routes"][i][k]["stops"][j], weekday))[
								"data"][0]["table"]
							times_raw =\
								bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
									"saturday"]
							change = True

					else:
						try:
							weekday = "PAZAR"
							times_raw =\
								bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
									"sunday"]
						except KeyError:
							bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
								"sunday"] = send_request(url, "HAT={0}&DURAK={1}&TUR={2}".format(
									travel["routes"][i][k]["name"], travel["routes"][i][k]["stops"][j], weekday))[
								"data"][0]["table"]
							times_raw =\
								bus_json["times"][travel["routes"][i][k]["name"]][travel["routes"][i][k]["stops"][j]][
									"sunday"]
							change = True

					for time in times_raw:
						times_anal[j].append(
								datetime.datetime.strptime("{0}:{1}".format(time["saat"], time["dakika"]), "%H:%M"))
				closest_times = get_closest_time([times_anal[0], times_anal[1]], time_cur)
				travel["routes"][i][k]["time"] = closest_times

		empty = []
		for i in range(len(travel["routes"])):
			remove = True
			for k in range(len(travel["routes"][i])):
				if remove:
					if len(travel["routes"][i][k]["time"]) == 0:
						empty.append(i - len(empty))
						remove = False

		for i in empty:
			travel["routes"].pop(i)


		if change:
			bus_data = open("modules/databases/bus.json", "w")
			bus_data.write(json.dumps(bus_json, indent=4, sort_keys=True))
			bus_data.close()

		for route in travel["routes"]:
			start = route[0]["time"][0]
			end = route[-1]["time"][1]
			diff = time_diff(start, end)
			print (route)
			print (diff)
			diff = time_sum(diff, time_diff(time_cur.strftime("%H:%M"), start))
			diff = time_sum(diff, "00:05")
			travel["total"].append(diff)

		shortest = 0

		for i in range(len(travel["total"])):
			if shortest == 0:
				shortest = {"routes":travel["routes"][i], "time":travel["total"][i]}
				continue
			if time_small(shortest["time"], travel["total"][i]):
				shortest = {"routes":travel["routes"][i], "time":travel["total"][i]}

		if change:
			bus_data = open("modules/databases/bus.json", "w")
			bus_data.write(json.dumps(bus_json, indent=4, sort_keys=True))
			bus_data.close()

		for i in range(len(shortest["routes"])):
			try:
				shortest["routes"][i]["names"] = [bus_json["stop_info"][shortest["routes"][i][0]]["name"],bus_json["stop_info"][shortest["routes"][i][1]]["name"]]
			except KeyError:
				shortest["routes"][i]["names"] = []
				shortest["routes"][i]["locations"] = []
				shortest["routes"][i]["coordinates"] = []

				for j in range(2):
					data = send_request("http://88.255.141.66/mbl/android/action.asp?SID=0.21893997000802157&VER=3.1.0&LAN=tr&UID=%7BACB78701-2727-4E9A-AE62-28491D671A7D%7D-130570234&FNC=Duraklar",
										"QUERY={}".format(shortest["routes"][i]["stops"][j]))["data"][0]["table"][0]
					bus_json["stop_info"][shortest["routes"][i]["stops"][j]] ={
						"name":     data["ad"],
						"location": data["konum"],
						"lat":      data["lat"],
						"lng":      data["lng"]
					}
					shortest["routes"][i]["names"].append(bus_json["stop_info"][shortest["routes"][i]["stops"][j]]["name"])

					shortest["routes"][i]["locations"].append(bus_json["stop_info"][shortest["routes"][i]["stops"][j]]["location"])
					shortest["routes"][i]["coordinates"].append({"lat":bus_json["stop_info"][shortest["routes"][i]["stops"][j]]["lat"], "lng":bus_json["stop_info"][shortest["routes"][i]["stops"][j]]["lng"]})
			change=True

		if change:
			bus_data = open("modules/databases/bus.json", "w")
			bus_data.write(json.dumps(bus_json, indent=4, sort_keys=True))
			bus_data.close()
		return shortest


if __name__ == '__main__':
	api.add_resource(Transit, '/transit', '/transit/')

	app.run(host='0.0.0.0', port=5000)