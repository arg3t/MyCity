import os

path = '/home/robot/image/'

prev_files = []
files = []
# r=root, d=directories, f = files
for r, d, f in os.walk(path):
    for file in f:
        if '.jpg' in file:
            files.append(os.path.join(r, file))

for f in files:
    print(f)
