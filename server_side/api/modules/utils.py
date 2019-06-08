import string
import random
import hashlib

def md5(s):
    return hashlib.md5(s.encode()).hexdigest()

def find_by_id(iterable, _id):
    for i in iterable:
        if i['id'] == _id:
            return i

    return None

def generate_id(length=32):
    return ''.join(random.choices(string.ascii_lowercase + string.digits, k=length))