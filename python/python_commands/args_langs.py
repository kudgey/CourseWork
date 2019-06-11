import json

import requests

from pymongo import MongoClient
from pymongo.errors import ServerSelectionTimeoutError

args = {}

args['xy'] = {
    "name": "xy",
    "description": "xy",
    "regex_pattern": "x (\\d+) y (\\d+)",
    "type": "array int"
}
args['type'] = {
    "name": "type",
    "description": "type",
    "regex_pattern": "тип ([а-я]+)",
    "type": "enum двойной,одинарный"
}
args['direction'] = {
    "name": "direction",
    "description": "direction",
    "regex_pattern": "([а-я]+)",
    "type": "enum выше,ниже,левее,правее"
}
args['horizontal'] = {
    "name": "horizontal",
    "description": "horizontal",
    "regex_pattern": "по горизонтали ([а-я]+)",
    "type": "enum правый,левый"
}
args['vertical'] = {
    "name": "vertical",
    "description": "vertical",
    "regex_pattern": "по вертикали ([а-я]+)",
    "type": "enum верхний,нижний"
}
args['text'] = {
    "name": "text",
    "description": "text",
    "regex_pattern": "текст ([а-яa-z]+)",
    "type": "str"
}
args['lang'] = {
    "name": "lang",
    "description": "lang",
    "regex_pattern": "язык ([а-я]+)",
    "type": "enum русский,английский"
}
args['shape_num'] = {
    "name": "shape_num",
    "description": "shape_num",
    "regex_pattern": "(\\d+)",
    "type": "int"
}

args['color'] = {
    "name": "color",
    "description": "color",
    "regex_pattern": "([ёа-я]+) цвет",
    "type": "str"
}
args['shape'] = {
    "name": "shape",
    "description": "shape",
    "regex_pattern": "форма ([а-я]+)",
    "type": "enum линия,прямоугольник,все"
}
args['width_low'] = {
    "name": "width_low",
    "description": "width_low",
    "regex_pattern": "минимальная ширина (\\d+)",
    "type": "int"
}
args['width_up'] = {
    "name": "width_up",
    "description": "width_up",
    "regex_pattern": "максимальная ширина (\\d+)",
    "type": "int"
}
args['height_low'] = {
    "name": "height_low",
    "description": "height_low",
    "regex_pattern": "минимальная высота (\\d+)",
    "type": "int"
}
args['height_up'] = {
    "name": "height_up",
    "description": "height_up",
    "regex_pattern": "максимальная высота (\\d+)",
    "type": "int"
}

langs = {}

langs['python'] = {
    "name": "Python",
    "shortName": "py",
    "description": "The best language :)"
}


if __name__ == '__main__':
    SERVER = 'http://127.0.0.1:8080/api/'

    a = False
    l = False

    for key, arg in args.items():
        resp = requests.post(SERVER+"args", json=arg)
        if resp.status_code in [200, 201]:
            arg['_id'] = resp.json()['id']
            print(resp.json()['id'])
            a = True
        else:
            print(resp.status_code)
    print()
    for key, lang in langs.items():
        resp = requests.post(SERVER+"lang", json=lang)
        if resp.status_code in [200, 201]:
            lang['_id'] = resp.json()['id']
            print(resp.json()['id'])
            l = True
        else:
            print(resp.status_code)
    if a:
        with open('args.json', 'w', encoding='utf-8') as outfile:
            json.dump(args, outfile, ensure_ascii=False)

    if l:
        with open('langs.json', 'w', encoding='utf-8') as outfile:
            json.dump(langs, outfile, ensure_ascii=False)

