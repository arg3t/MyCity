import os
from PIL import Image
from PIL.ImageOps import grayscale
from watchdog.events import RegexMatchingEventHandler

class FilesEventHandler(RegexMatchingEventHandler):
	FILES_REGEX = [r".*[^_thumbnail]\.jpg$"]
	socketio = None
	def __init__(self,socketio):
		self.socketio = socketio
		super().__init__(self.FILES_REGEX)

	def on_any_event(self, event):
		print("File change occured")
		filename, ext = os.path.splitext(event.src_path)
		print("{}_socket".format(filename.split(".")[0]))
		self.socketio.emit("new", "new", namespace="{}_socket".format(filename.split(".")[0]))


