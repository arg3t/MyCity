import cv2
import numpy as np
import json
from matplotlib import pyplot as plt


import cv2
import numpy as np
import json
import os
from matplotlib import pyplot as plt
import xml.etree.ElementTree as ET
import cv2
import numpy as np
import os
import json
from pysolar.solar import *
from datetime import datetime

def calcAvg(img,locations_xml):
	locations_extracted = []
	loc_images = {}
	average_values = {}


	for i in range( len( locations_xml ) ):
		try:
			locations_extracted.append([])
			for j in range(4):
				locations_extracted[i].append(locations_xml[i][1][j].attrib)
			locations_extracted[i].append(locations_xml[i].attrib['occupied'])
			locations_extracted[i].append(locations_xml[i].attrib['id'])
		except Exception:
			print("xml corrupt!")
			return {}

	for col in range(len(img)):
		for pix in range(len(img[col])):
			if (img[col][pix] == [255,255,255]).all():
				img[col][pix] == [255,255,254]

	for i in range(len(locations_extracted)):
		temp = locations_extracted[i]

		pts = np.array([[int(temp[0]['x']),int(temp[0]['y'])],
						[int(temp[1]['x']),int(temp[1]['y'])],
						[int(temp[2]['x']),int(temp[2]['y'])],
						[int(temp[3]['x']),int(temp[3]['y'])]])

		rect = cv2.boundingRect(pts)
		x,y,w,h = rect
		croped = img[y:y+h, x:x+w].copy()

		pts = pts - pts.min(axis=0)

		mask = np.zeros(croped.shape[:2], np.uint8)
		cv2.drawContours(mask, [pts], -1, (255, 255, 255), -1, cv2.LINE_AA)

		dst = cv2.bitwise_and(croped, croped, mask=mask)

		bg = np.ones_like(croped, np.uint8)*255
		cv2.bitwise_not(bg,bg, mask=mask)
		dst2 = bg+ dst
		blurred = cv2.GaussianBlur(dst2,(5,5),3)
		edges = cv2.Canny(blurred,100,100)

		if not temp[4] in loc_images:
				loc_images[temp[4]] = {}
		loc_images[temp[4]][temp[5]] = edges

	for state in loc_images:
		average_values[state] = {}
		for lot in loc_images[state]:
			reps = 0
			for col in loc_images[state][lot]:
				for pix in col:
					if(pix == 255):
						reps += 1
			average_values[state][lot] = reps

	print (average_values)
	return average_values


def generateAvg():

	dates = os.listdir('./PKLot/PKLot/PUCPR/Sunny')
	imgs = []
	averages={}
	back_file = open("backup.json","w")
	back_file.write("{")
	for i in range(len(dates)):
		imgs.append(os.listdir(os.path.join('./PKLot/PKLot/PUCPR/Sunny',dates[i])))

	for i in range(len(imgs)):
		for j in range(len(imgs[i])):
			if(imgs[i][j][-4:] == ".jpg"):
				try:
					img_location = os.path.join('./PKLot/PKLot/PUCPR/Sunny',dates[i],imgs[i][j])
					locs = ET.parse(img_location[:-4] + ".xml").getroot()
					print(img_location,end=" ")
					average = calcAvg(cv2.imread(img_location),locs)
					if not(average == {}):
						averages[img_location] = average
						back_file.write("'"+img_location + "':" + str(average))
				except Exception:
					continue

	back_file.write("}")
	js = json.dumps(averages)
	print(js)
	fp = open('data.json', 'w')
	fp.write(js)
	fp.close()

generateAvg()



