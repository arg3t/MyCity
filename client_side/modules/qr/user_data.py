from multiprocessing import Process

import reader

class Reader():
	def __init__(self):
		read = reader.Read(self)
		p1 = Process(target=read.detect)
		p1.start()

	def received(self,data):
		print(data)

qr_reader = Reader()

while 1:
	continue













