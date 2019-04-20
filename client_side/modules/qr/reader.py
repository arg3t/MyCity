import cv2
from pyzbar import pyzbar

class Read():
	instance = 0
	sid = 0
	def __init__(self,inst):
		self.instance = inst

	def detect(self):

		cam = cv2.VideoCapture(0)

		barcodeData_prev = 0
		while 1:

			ret , img = cam.read()
			gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
			barcode = pyzbar.decode(gray)
			if len(barcode) > 0:
				barcode = barcode[0]
				(x, y, w, h) = barcode.rect
				barcodeData = barcode.data.decode("utf-8")
				barcodeType = barcode.type
				if barcodeData_prev == 0 or barcodeData_prev != barcodeData:
					barcodeData_prev = barcodeData
					self.instance.received(barcodeData)
					continue
				text = "{} ({})".format(barcodeData, barcodeType)
				cv2.putText(img, text, (x, y - 10), cv2.FONT_HERSHEY_SIMPLEX,
							0.5, (255, 0, 0), 2)
				cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
			cv2.imshow("a", img)
			if cv2.waitKey(1) & 0xFF == ord('q'):
				break
		cam.release()
		cv2.destroyAllWindows()