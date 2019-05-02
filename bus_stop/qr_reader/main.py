from imutils.video import VideoStream
from pyzbar import pyzbar
import requests
import imutils
import cv2

vs = VideoStream().start()
dates = []
reps = 0
barcodes = None
prevcode = None


def main(host):
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
                cv2.imshow("Image", frame)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
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
                    requests.post('https://' + host + '/reduce', data={'id': barcodeData, 'reduce': 2.5})
                    reps += 1

                prevcode = barcodeData
                barcodes = None
                cv2.imshow("Image", frame)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
        except KeyboardInterrupt:
            break
main(input('Host << '))
print("[INFO] cleaning up...")
cv2.destroyAllWindows()
vs.stop()