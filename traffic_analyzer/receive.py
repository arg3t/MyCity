import socket
import cv2
import pickle
import struct ## new

HOST=''
PORT=8485

s=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
print('Socket created')

s.bind((HOST,PORT))
print('Socket bind complete')
s.listen(10)
print('Socket now listening')


data = b""
payload_size = struct.calcsize(">L")
print("payload_size: {}".format(payload_size))
switch = True

while True:
	while switch:
		conn,addr = s.accept()
		switch = False
	try:
		while len(data) < payload_size:
			print("Recv: {}".format(len(data)))
			data += conn.recv(4096)


		print("Done Recv: {}".format(len(data)))
		packed_msg_size = data[:payload_size]
		data = data[payload_size:]
		msg_size = struct.unpack(">L", packed_msg_size)[0]
		print("msg_size: {}".format(msg_size))
		while len(data) < msg_size:
			try:
				data += conn.recv(4096)
			except:
				pass
		frame_data = data[:msg_size]
		data = data[msg_size:]

		frame=pickle.loads(frame_data, fix_imports=True, encoding="bytes")
		frame = cv2.imdecode(frame, cv2.IMREAD_COLOR)
		cv2.imshow('ImageWindow',frame)
		ex_c = [27, ord("q"), ord("Q")]
		if cv2.waitKey(1) & 0xFF in ex_c:
			break
	except:
		switch=True