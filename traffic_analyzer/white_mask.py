import cv2
import time
import numpy as np

cap = cv2.VideoCapture(1)

def threshold_slow(image):
    h = image.shape[0]
    w = image.shape[1]
    for y in range(0, h):
        for x in range(0, w):
            if np.any(image[y, x] != 0):
                return True

    return False

while True:
    ret, frame = cap.read()
    frame_org = frame
    frame = frame[185:230, 180:225]
    hsv = cv2.cvtColor(frame, cv2.COLOR_BGR2HSV)
    sensitivity = 50
    lower_white = np.array([0,0,255-sensitivity])
    upper_white = np.array([255,sensitivity,255])
    mask = cv2.inRange(hsv, lower_white, upper_white)
    res = cv2.bitwise_and(frame,frame, mask=mask)

    res = cv2.erode(res, None, iterations=2)
    res = cv2.dilate(res, None, iterations=4)

    mask = cv2.erode(mask, None, iterations=2)
    mask = cv2.dilate(mask, None, iterations=4)

    if threshold_slow(res):
        cv2.rectangle(frame_org, (100, 100), (300, 300), (255, 0, 0), 2)

    cv2.imshow('frame', frame)
    cv2.imshow('org', frame_org)
    cv2.imshow('mask', mask)
    cv2.imshow('res', res)

    k = cv2.waitKey(5) & 0xFF
    if k == ord('q'):
        break

cap.release()
cv2.destroyAllWindows()