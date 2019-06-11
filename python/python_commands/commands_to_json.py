import json
import requests

from pymongo import MongoClient
from pymongo.errors import ServerSelectionTimeoutError


commands = {}

commands['make_click'] = {
    "command": {
        'name': 'click',
        'phrase': "кликни",
        'price': 0
    },
    'params': ['xy', 'type']
}

commands['open_browser'] = {
    "command": {
        'name': 'open_browser',
        'phrase': "открой браузер",
        'price': 0
    }
}

# commands['detect_image'] = {
#     "command": {
#         'name': 'detect_image',
#         'phrase': "найди изображение",
#         'price': 0
#     },
#     'params': ['file']
# }

commands['mouse_shift'] = {
    "command": {
        'name': 'mouse_shift',
        'phrase': "передвинь мышку",
        'price': 0
    },
    'params': ['direction']
}

commands['mouse_move_corner'] = {
    "command": {
        'name': 'mouse_move_corner',
        'phrase': "перемести мышку в угол",
        'price': 0
    },
    'params': ['horizontal', 'vertical']
}

commands['mouse_move'] = {
    "command": {
        'name': 'mouse_move',
        'phrase': "перемести мышку",
        'price': 0
    },
    'params': ['xy']
}

commands['press_enter'] = {
    "command": {
        'name': 'press_enter',
        'phrase': "нажми enter",
        'price': 0
    }
}

commands['write_phrase'] = {
    "command": {
        'name': 'write_phrase',
        'phrase': "напиши",
        'price': 0
    },
    'params': ['text']
}

commands['detect_shapes'] = {
    "command": {
        'name': 'detect_shapes',
        'phrase': "найди объекты",
        'price': 0
    },
    'params': ['color', 'shape', 'width_low', 'width_up', 'height_low', 'height_up']
}

commands['detect_text'] = {
    "command": {
        'name': 'detect_text',
        'phrase': "найди текст",
        'price': 0
    },
    'params': ['text', 'lang']
}

commands['show_shapes'] = {
    "command": {
        'name': 'show_shapes',
        'phrase': "покажи",
        'price': 0
    }
}

commands['choose_shape'] = {
    "command": {
        'name': 'choose_shape',
        'phrase': "выбираю",
        'price': 0
    },
    'params': ['shape_num']
}


if __name__ == '__main__':
    SERVER = 'http://127.0.0.1:8080/api/'

    with open('args.json', encoding='utf-8') as fh:
        args = json.load(fh)

    with open('langs.json', encoding='utf-8') as fh:
        langs = json.load(fh)

    c = False

    for key, command in commands.items():
        if 'params' in command:
            command['args_ids'] = [args[name]['_id'] for name in command['params']]
        command['lang_id'] = langs['python']['_id']
        try:
            file_name = key + ".py"
            with open(file_name, 'r', encoding='utf-8') as file:
                command['command']['code'] = file.read()
        except FileNotFoundError:
            print("Not found file", command['command']['name'])
        resp = requests.post(SERVER+"commands/", json=command)
        if resp.status_code in [200, 201]:
            command['_id'] = resp.json()['id']
            print(resp.json()['id'])
            c = True
        else:
            print(resp.status_code, command['command']['name'], resp.text)

    if c:
        with open('commands.json', 'w', encoding='utf-8') as outfile:
            json.dump(commands, outfile, ensure_ascii=False)
