import random, string

import numpy as np
import cv2

def generate_id(length=32):
    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=length))

cap = cv2.VideoCapture(1)
cars = [
    [(420, 375), (500, 480)],
    [(505, 340), (590, 440)],
    [(415, 240), (495, 330)],
    [(485, 235), (550, 320)]
]
ambulance = [(250, 130), (400, 240)]

while(True):
    # Capture frame-by-frame
    ret, frame = cap.read()

    key = cv2.waitKey(1) & 0xFF
    if key == ord('s'):
        cv2.imwrite(generate_id(12) + ".jpg", frame[100:600, 240:600])

    for i in cars:
        cv2.rectangle(frame, *i, (0, 0, 255), 2)

    cv2.rectangle(frame, *ambulance, (255, 0, 0), 2)
    frame = frame[100:600, 240:600]

    cv2.imshow('frame', frame)

    if key == ord('q'):
        break
# When everything done, release the capture
cap.release()
cv2.destroyAllWindows()
