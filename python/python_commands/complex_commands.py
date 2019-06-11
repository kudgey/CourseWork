import json
import requests

from pymongo import MongoClient
from pymongo.errors import ServerSelectionTimeoutError


commands = {}

commands['minmax'] = {
    "complexCommand": {
        'name': 'minmax',
        'phrase': "сделай окно меньше"
    },
    'commands': ['mouse_move_corner', 'mouse_shift', 'mouse_shift', 'mouse_shift', 'mouse_shift', 'make_click'],
    'args': {
        'horizontal': "правый",
        'vertical': "верхний",
        "direction": "левее"
    }
}


if __name__ == '__main__':
    SERVER = 'http://127.0.0.1:8080/api/'

    with open('commands.json', encoding='utf-8') as fh:
        coms = json.load(fh)
    
    with open('args.json', encoding='utf-8') as fh:
        args = json.load(fh)

    c = False

    for key, command in commands.items():
        if 'commands' in command:
            command['commands_ids'] = [coms[name]['_id'] for name in command['commands']]
        if 'args' in command:
            command['readyArgs'] = {args[k]['_id']: v for k, v in command['args'].items()}

        del command['commands']
        del command['args']
        resp = requests.post(SERVER+"complexcommand/", json=command)
        if resp.status_code in [200, 201]:
            command['_id'] = resp.json()['id']
            print(resp.json()['id'])
            c = True
        else:
            print(resp.status_code, command['complexCommand']['name'], resp.text)

    if c:
        with open('ccommands.json', 'w', encoding='utf-8') as outfile:
            json.dump(commands, outfile, ensure_ascii=False)
