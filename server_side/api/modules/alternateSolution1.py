import xml.etree.ElementTree as ET
import cv2
import numpy as np
import os
import json
from pysolar.solar import *
from datetime import datetime


	
	
def timeAverage():
	data_file = open("data.json","r")
	time_file = open("timely.json","w")
	averages = json.loads(data_file.read())
	timely = {}
	altitudes = {}
	for key in averages:
		date = key[37:-13]
		time = key[49:-4].replace("_",":")
		averages[key]["time"]=time
		averages[key]["date"]=date

	data_file.close()
	data_file = open("data.json","w")
	data_file.write(json.dumps(averages))
	for key in averages:
		time = datetime.strptime(averages[key]["date"] + " " + averages[key]["time"] + " -0300","%Y-%m-%d %H:%M:%S %z")
		altitude = int(get_altitude(-25.4269081,-49.3318036,time))
		altitudes[averages[key]["date"] + " " + averages[key]["time"] ] = altitude
		if not (altitude in timely):
			timely[altitude] = {}
		for spot in averages[key]:
			if(spot == "time" or spot == "date"):
				continue
			if not spot in timely[altitude]:
				timely[altitude][spot]=[]
			timely[altitude][spot].append({"r":averages[key][spot]["r"],"b":averages[key][spot]["b"],"g":averages[key][spot]["g"]})
	print(altitudes)
	for key in timely:

		for id in timely[key]:
			total = {"r":0,"g":0,"b":0}
			for i in range(len(timely[key][id])):
				for color in total:
					total[color] += timely[key][id][i][color]

			for color in total:
				total[color] = total[color] / len(timely[key][id])

			timely[key][id] = total
	print(timely)
	timely_json = json.dumps(timely)
	time_file.write(timely_json)

	data_file.close()
	time_file.close()
	
	
def generateData(locations,image,show,averages):
	
	loc_images = {}
	average_values = {}
	average_total=[0,0,0]

	for col in range(len(image)):
		for pix in range(len(image[col])):
			if (image[col][pix] == [255,255,255]).all():
				image[col][pix] == [255,255,254]

	for i in locations:
		temp = locations[i]

		pts = np.array([[int(temp[0]['x']),int(temp[0]['y'])],
						[int(temp[1]['x']),int(temp[1]['y'])],
						[int(temp[2]['x']),int(temp[2]['y'])],
						[int(temp[3]['x']),int(temp[3]['y'])]])

		rect = cv2.boundingRect(pts)
		x,y,w,h = rect
		croped = image[y:y+h, x:x+w].copy()

		pts = pts - pts.min(axis=0)

		mask = np.zeros(croped.shape[:2], np.uint8)
		cv2.drawContours(mask, [pts], -1, (255, 255, 255), -1, cv2.LINE_AA)

		dst = cv2.bitwise_and(croped, croped, mask=mask)
		bg = np.ones_like(croped, np.uint8)*255
		cv2.bitwise_not(bg,bg, mask=mask)
		dst2 = bg+ dst

		split_len = len(dst2)//3

		splitted = [dst2[:split_len,:],dst2[split_len:split_len*2,:],dst2[split_len*2:,:]]

		loc_images[i]=[dst2]



	for lot in loc_images:
		average_values[lot] = []

		for i in range(1):
			diff_pix = 0
			reps = 0
			avg_rgb = [0,0,0]
			rgb = ["b","g","r"]
			for col in loc_images[lot][i]:
				for pix in col:
					if (pix == [255,255,255]).all():
						continue
					different = False
					print("[",end="")
					for j in range(3):
						print(abs(int(averages[lot][rgb[i]]-pix[i])) , end=",")
						if abs(averages[lot][rgb[i]]-pix[i]) > 59:
							different = True
					reps += 1
					diff_pix += different
					print("]",end=" ")
				print("")

			print("\n\n")

		average_values[lot] = (diff_pix/reps)*100
		if lot in show:
			cv2.imshow("a" , loc_images[lot][0])
			cv2.waitKey(0)
	return average_values

			
def findSpot(img_loc,locations):
	empty = []
	timely_values = time_file = open("timely.json","r")
	timely_averages = json.loads(timely_values.read())
	img = cv2.imread(img_loc)

	date = img_loc[37:-13]
	time = img_loc[49:-4]
	time = time.replace("_",":")

	time = datetime.strptime(date + " " + time + " -0300","%Y-%m-%d %H:%M:%S %z")
	altitude = int(get_altitude(-25.4269081,-49.3318036,time))
	base_value = timely_averages[str(altitude)]
	print(altitude)
	averages = generateData(locations,img,[],base_value)

	for i in averages:
		print(i + " " + str(averages[i]))

	for i in locations:
		if(averages[i] > 32):
			color=[0,0,255]
			empty.append(i)
		else:
			color=[0,255,0]
		corners = [[],[]]
		for j in range(0,4):
			val2 = j+1
			if(val2 == 4):
				val2 = 0
			pt1 = (int(locations[i][j]["x"]),int(locations[i][j]["y"]))
			pt2 = (int(locations[i][val2]["x"]),int(locations[i][val2]["y"]))
			cv2.line(img,pt1,pt2,color)
			corners[0].append(int(locations[i][j]["x"]))
			corners[1].append(int(locations[i][j]["y"]))

		x1=min(corners[0][0],corners[0][1],corners[0][2],corners[0][3]);
		x2=max(corners[0][0],corners[0][1],corners[0][2],corners[0][3]);
		y1=min(corners[1][0],corners[1][1],corners[1][2],corners[1][3]);
		y2=max(corners[1][0],corners[1][1],corners[1][2],corners[1][3]);

		pt = (int((x1+x2)/2),int((y1+y2)/2))

		cv2.putText(img,str(i), pt, cv2.FONT_HERSHEY_SIMPLEX, 0.5, 255)


	cv2.imshow("Parking Lot",img)
	cv2.waitKey(0)
	return empty

location_file = open("locations.json","r")
location_json = json.loads(location_file.read())
empty_locs = findSpot('./PKLot/PKLot/PUCPR/Sunny/2012-09-11/2012-09-11_18_08_41.jpg',location_json)
closest = 1000
for i in empty_locs:
	print (location_json[i][4])
	if location_json[i][4] < closest:
		closest = int(i)

print("The closest spot is " + str(closest))
print("done")



