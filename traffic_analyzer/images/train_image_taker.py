import cv2
import time
import os
import numpy as np
import json

vehicles = ["ambulance", "car1"]
sides = [str(x) for x in range(0,361,30)]
distances =["close_high","far_high","close_low","far_low"]

def select_rect(im):
    rects=[]
    scale_percent = 100
    width = int(im.shape[1] * scale_percent / 100)
    height = int(im.shape[0] * scale_percent / 100)
    # Select ROI
    fromCenter = False
    r = cv2.selectROI(cv2.resize(im,(width,height)))
    # Crop image
    if(r == (0,0,0,0)):
        cv2.destroyAllWindows()
    imCrop = im[int(r[1]*100/scale_percent):int(r[1]*100/scale_percent+r[3]*100/scale_percent), int(r[0]*100/scale_percent):int(r[0]*100/scale_percent+r[2]*100/scale_percent)]
    # Display cropped image
    cv2.imshow("Image", imCrop)
    cv2.waitKey(0)
    cv2.destroyAllWindows()
    for i in r:
        rects.append(int(i)*int(100/scale_percent))

    print(rects)
    locs = {
        "x1": rects[0],
        "y1": rects[1],
        "x2": rects[0]+rects[2],
        "y2": rects[1]+rects[3],
    }
    return locs

def take_image():
    cam_no = 0

    while 1:
        cam = cv2.VideoCapture(cam_no)
        if not (cam is None or not cam.isOpened()):
            break
        cam_no+=1

    for vehicle in vehicles:
        if vehicle == "ambulance":
            continue
        if not os.path.exists(vehicle):
            os.makedirs(vehicle)
        for distance in distances:
            for side in sides:
                for i in range(1,3):
                    ret,img= cam.read()
                    cv2.imwrite("{}/{}-{}({}).jpg".format(vehicle,distance,side,i),img)
                    cv2.imshow("current",img)
                    ex_c = [27, ord("q"), ord("Q")]
                    if cv2.waitKey(1) & 0xFF in ex_c:
                        break
                print("Took side {}:distance:{}, waiting 7 seconds".format(side,distance))
                time.sleep(7)
            print("Finished distance:"+distance)
            while not cv2.waitKey(1) & 0xFF in ex_c:
                ret,img= cam.read()
                cv2.imshow("current",img)
        print("Finished vehicle:"+vehicle)
        while not cv2.waitKey(1) & 0xFF in ex_c:
            ret,img= cam.read()
            cv2.imshow("current",img)



def cut_image():
    cut_rects = {}
    for vehicle in vehicles:

        if not os.path.exists(vehicle):
            os.makedirs(vehicle)
        for distance in distances:
            for side in sides:
                img = cv2.imread("{}/{}-{}({}).jpg".format(vehicle,distance,side,1))
                cut_rects[vehicle + "-" + distance + "-" + side] = select_rect(img)
            cv2.destroyAllWindows()

    return cut_rects

coordinates = cut_image()
print(json.dumps(coordinates,indent=4))
with open("coordinates.json","w") as file:
    file.write(json.dumps(coordinates,indent=4))



