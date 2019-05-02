from imutils.video import VideoStream
from pyzbar import pyzbar
import requests
import imutils
import cv2
import urllib3

import socket
import struct
import zlib
import pickle

vs = VideoStream().start()
dates = []
reps = 0
barcodes = None
prevcode = None
host = '10.10.26.141'

client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
client_socket.connect((host, 8485))
connection = client_socket.makefile('wb')

def main():
    global vs
    global dates
    global reps
    global barcodes
    global prevcode
    while True:
        try:
            while (barcodes == None or barcodes == []):
                frame = vs.read()
                frame = imutils.resize(frame, width=400)
                barcodes = pyzbar.decode(frame)
            res, frm = cv2.imencode('.jpg', frame, [int(cv2.IMWRITE_JPEG_QUALITY), 90])
            send_data = zlib.compress(pickle.dumps(frm, 0))
            size = len(send_data)
            client_socket.sendall(struct.pack(">L", size) + data)
            barcodes = pyzbar.decode(frame)
            # loop over the detected barcodes
            for barcode in barcodes:
                (x, y, w, h) = barcode.rect
                cv2.rectangle(frame, (x, y), (x + w, y + h), (0, 0, 255), 2)

                barcodeData = barcode.data.decode("utf-8")
                barcodeType = barcode.type

                if (barcodeData != prevcode):
                    text = "{} ({})".format(barcodeData, barcodeType)
                    cv2.putText(frame, text, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX,
                                0.5, (0, 0, 255), 2)

                    print("[INFO] Found {} barcode: {}".format(barcodeType, barcodeData))
                    requests.post('https://' + host + ':5000/reduce', data={'id': barcodeData, 'reduce': 5}, verify=False)
                    reps += 1

                prevcode = barcodeData
                barcodes = None
        except KeyboardInterrupt:
            break
urllib3.disable_warnings(urllib3.exceptions.InsecureRequestWarning)
main()
print("[INFO] cleaning up...")
cv2.destroyAllWindows()
vs.stop()
