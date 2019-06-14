import requests
import os
import tarfile
import sys

urls = [
	"https://s3-ap-northeast-1.amazonaws.com/mycityreport/trainedModels.tar.gz",
	"http://download.tensorflow.org/models/object_detection/rfcn_resnet101_coco_2018_01_28.tar.gz",
	"http://download.tensorflow.org/models/object_detection/faster_rcnn_resnet101_kitti_2018_01_28.tar.gz"
]

paths = ["../server_side/api/modules", ".", '../server_side/api/modules']

for i in range(len(urls)):
	if i == 0:
		continue
	url = urls[i]
	print("[INFO]: Downloadinng file: {} to temp.tar.gz!".format(url.split("/")[-1]))

	with open("temp.tar.gz", 'wb') as f:
		response = requests.get(url, stream=True)
		total = response.headers.get('content-length')

		if total is None:
			f.write(response.content)
		else:
			downloaded = 0
			total = int(total)
			for data in response.iter_content(chunk_size=max(int(total/1000), 1024*1024)):
				downloaded += len(data)
				f.write(data)
				done = int(50*downloaded/total)
				sys.stdout.write('\r[{}{}]'.format('*' * done, '.' * (50-done)))
				sys.stdout.flush()
	sys.stdout.write('\n')
	print("[INFO]: Done downloading, now extracting!")
	tar = tarfile.open("temp.tar.gz")
	tar.extractall(path=paths[i])
	tar.close()
	os.remove("temp.tar.gz")
	print("[INFO]: Done downloading, now deleting!")
	print("[INFO]: Done")

