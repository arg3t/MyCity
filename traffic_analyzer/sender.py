import cv2
import socket
import json
import base64
from PIL import Image
from io import BytesIO
import psutil

cam = cv2.VideoCapture(0)


img_counter = 0

encode_param = [int(cv2.IMWRITE_JPEG_QUALITY), 90]

switch = True
cut=[-175,-1,-175,-1]
cut_send = [0,0,0,0]
data = {"gpu_temp":"10C","gpu_load":"15%","cpu_temp":"47C","cpu_load":"15%","mem_temp":"NaN","mem_load":"17%","fan_speed":"10000RPM"}

def get_temps():
    global data
    temps = psutil.sensors_temperatures()
    data["cpu_temp"] = str(int(temps["dell_smm"][0][1]))+"°C"
    data["cpu_load"] = str(psutil.cpu_percent())+"%"
    data["mem_load"] = str(dict(psutil.virtual_memory()._asdict())["percent"])+"%"
    data["fan_speed"] = str(psutil.sensors_fans()["dell_smm"][0][1])+"RPM"

while True:
    try:

        ret, frame = cam.read()
        lens = [len(frame),0,len(frame[0])]
        for i in range(0,len(cut),2):
            if cut[i]<0:
                cut_send[i] = lens[i] + cut[i]
            cut_send[i+1] = abs(cut[i])-abs(cut[i+1])
        frame = cv2.cvtColor(frame,cv2.COLOR_BGR2RGB)
        crop_img = frame.copy(order='C')

        crop_img = Image.fromarray(crop_img,"RGB")
        buffered = BytesIO()
        crop_img.save(buffered, format="JPEG")
        img = base64.b64encode(buffered.getvalue()).decode("ascii")
        frame_cut=frame[cut[0]:cut[1],cut[2]:cut[3]]
        cv2.imshow("base",frame)
        cv2.imshow("cut",frame_cut)
        cv2.imshow("test",        frame[cut_send[0]:cut_send[0]+cut_send[1],cut_send[2]:cut_send[2]+cut_send[3]]
                   )
        ex_c = [27, ord("q"), ord("Q")]
        if cv2.waitKey(1) & 0xFF in ex_c:
            break
        if switch:
            try:
                client_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                client_socket.connect(('127.0.0.1', 8485))
                connection = client_socket.makefile('wb')
                switch = False
            except:
                switch=True
                continue
        try:
            client_socket.sendall(json.dumps({"image_full":img,"image_sizes":{"x":cut_send[2],"y":cut_send[0],"width":cut_send[3],"height":cut_send[1]},"load":data}).encode('gbk')+b"\n")
            print(img)

        except:
            switch=True
        img_counter += 1
        if img_counter % 30 ==0:
            get_temps()
    except KeyboardInterrupt:
        if not switch:
            client_socket.sendall(b"Bye")
        cam.release()
        exit(0)




