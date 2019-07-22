from urllib.request import urlopen, Request
from urllib.parse import urlencode
import cv2
import json

AI_IP = '192.168.2.252'
cam = cv.VideoCapture('http://10.42.0.151:8080/?action=stream')


while True:
    ret, img = cam.read()

    img = cv2.resize(img, (1280, 960))
    
    url = 'https://{}:5001/ai'.format(AI_IP)
    _, buffer = cv2.imencode('.jpg', img)

    post_fields = {'img': base64.b64encode(buffer).decode('ascii'), "type": "damage"}

    request = Request(url, urlencode(post_fields).encode())
    output_dict = json.loads(json.loads(urlopen(request, context=context).read()))

    cv2.imshow('Image', img)
    cv2.waitKey(0)