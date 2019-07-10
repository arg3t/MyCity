import cv2

import json

rects = []

cam = cv2.VideoCapture(2)
ret,im = cam.read()
cam.release()
if __name__ == '__main__' :

	while(True):



		scale_percent = 50
		width = int(im.shape[1] * scale_percent / 100)
		height = int(im.shape[0] * scale_percent / 100)

		# Select ROI
		fromCenter = False
		r = cv2.selectROI(cv2.resize(im,(width,height)))
		# Crop image
		if(r == (0,0,0,0)):
			cv2.destroyAllWindows()
			break
		imCrop = im[int(r[1]*100/scale_percent):int(r[1]*100/scale_percent+r[3]*100/scale_percent), int(r[0]*100/scale_percent):int(r[0]*100/scale_percent+r[2]*100/scale_percent)]
		# Display cropped image
		cv2.imshow("Image", imCrop)
		cv2.waitKey(0)
		cv2.destroyAllWindows()

		rects.append([])
		print(rects[len(rects)-1])
		for i in r:
			rects[len(rects)-1].append(int(i)*int(100/scale_percent))

print(rects)
locs = {}
for i in range(len(rects)):
	loc = rects[i]
	locs[str(i)] = {
		"x1": loc[0],
		"y1": loc[1],
		"x2": loc[0]+loc[2],
		"y2": loc[1]+loc[3],
		"priority":i
	}

with open("databases/locations.json","w") as f:
	f.write(json.dumps(locs,indent=2))







