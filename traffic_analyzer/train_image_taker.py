import cv2
import time
import os
import numpy as np

vehicles = ["ambulance", "car1"]
sides = [str(x) for x in range(0,361,30)]
distances =["close_high","far_high","close_low","far_low"]
cam_no = 0
while 1:
    cam = cv2.VideoCapture(cam_no)
    if not (cam is None or not cam.isOpened()):
        break
    cam_no+=1

def take_image():
    for vehicle in vehicles:
        if vehicle == "ambulance":
            continue
        if not os.path.exists("images\\"+vehicle):
            os.makedirs("images\\"+vehicle)
        for distance in distances:
            for side in sides:
                for i in range(1,3):
                    ret,img= cam.read()
                    cv2.imwrite("images\\{}\\{}-{}({}).jpg".format(vehicle,distance,side,i),img)
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

    for vehicle in vehicles:
        images = []
        image_main = None
        if not os.path.exists("images\\"+vehicle):
            os.makedirs("images\\"+vehicle)
        for distance in distances:
            for side in sides:
                for i in range(1,3):
                    img = cv2.imread("images\\{}\\{}-{}({}).jpg".format(vehicle,distance,side,i))
                    images.append(img)
        image_main = np.zeros_like(images[0])
        sums = np.array(image_main, dtype='int64')
        for i in range(len(images)):
            sums += np.array(images[i],dtype='int64')
        image_main = np.array(sums/(len(images)+1)).astype(uint8)
        cv2.imshow("a",image_main)
        cv2.waitKey(0)

cut_image()


