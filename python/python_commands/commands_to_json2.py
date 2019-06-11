import json
import uuid

from pymongo import MongoClient
from pymongo.errors import ServerSelectionTimeoutError

import mongo_config


commands = {}

commands['make_click'] = {
    'name': 'click',
    'phrase': "сделай клик",
    'paramType': {
        'xy': {
            "regex_pattern": "x (\\d+) y (\\d+)",
            "type": "array int"
        },
        'type': {
            "regex_pattern": "тип ([а-я]+)",
            "type": "enum двойной,одинарный"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['open_browser'] = {
    'name': 'open_browser',
    'phrase': 'открой браузер'
}

commands['detect_image'] = {
    'name': 'detect_image',
    'phrase': 'найди изображение',
    'paramType': {
        'file': 'path',
        'russian': {
            'file': "путь к изображению"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['mouse_shift'] = {
    'name': 'mouse_shift',
    'phrase': 'сдвинь мышку',
    'paramType': {
        'direction': {
            "regex_pattern": "([а-я]+)",
            "type": "enum выше,ниже,левее,правее"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['mouse_move_corner'] = {
    'name': 'mouse_move_corner',
    'phrase': 'перемести мышку в угол',
    'paramType': {
        'horizontal': {
            "regex_pattern": "по горизонтали ([а-я]+)",
            "type": "enum правый,левый"
        },
        'vertical': {
            "regex_pattern": "по вертикали ([а-я]+)",
            "type": "enum верхний,нижний"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['mouse_move'] = {
    'name': 'mouse_move',
    'phrase': 'перемести мышку',
    'paramType': {
        'xy': {
            "regex_pattern": "x (\\d+) y (\\d+)",
            "type": "array int"
        }
    },
    'returnType': {
        'xy': 'int[2'
    }
}

commands['press_enter'] = {
    'name': 'press_enter',
    'phrase': 'нажми enter'
}

commands['write_phrase'] = {
    'name': 'write_phrase',
    'phrase': 'напиши',
    'paramType': {
        'text': {
            "regex_pattern": "([а-яa-z]+)",
            "type": "str"
        }
    }
}

commands['detect_shapes'] = {
    'name': 'detect_shapes',
    'phrase': 'найди объекты',
    'paramType': {
        'color': {
            "regex_pattern": "([ёа-я]+) цвет",
            "type": "str"
        },
        'shape': {
            "regex_pattern": "форма ([а-я]+)",
            "type": "enum линия,прямоугольник,все"
        },
        'width_low': {
            "regex_pattern": "минимальная ширина (\\d+)",
            "type": "int"
        },
        'width_up': {
            "regex_pattern": "максимальная ширина (\\d+)",
            "type": "int"
        },
        'height_low': {
            "regex_pattern": "минимальная высота (\\d+)",
            "type": "int"
        },
        'height_up': {
            "regex_pattern": "максимальная высота (\\d+)",
            "type": "int"
        }
    },
    'returnType': {
        'shapes': 'int[][2',
        'img': 'path'
    }
}

commands['detect_text'] = {
    'name': 'detect_text',
    'phrase': 'найди текст',
    'paramType': {
        'text': {
            "regex_pattern": "текст ([а-яa-z]+)",
            "type": "str"
        },
        'lang': {
            "regex_pattern": "язык ([а-я]+)",
            "type": "enum русский,английский"
        }
    },
    'returnType': {
        'shapes': 'int[][2',
        'img': 'path'
    }
}

commands['show_shapes'] = {
    'name': 'show_shapes',
    'phrase': 'покажи найденное',
    'historyArgs': {
        'img': 'path'
    },
    'returnType': {
        'img_pid': 'int'
    }
}

commands['choose_shape'] = {
    'name': 'choose_shape',
    'phrase': 'выбираю объект',
    'paramType': {
        'shape_num': {        
            "regex_pattern": "(\\d+)",
            "type": "int"
        }
    },
    'historyArgs': {
        'shapes': 'int[][2',
        'img_pid': 'int'
    },
    'returnType': {
        'xy': 'int[2'
    }
}


if __name__ == '__main__':
    host = mongo_config.host
    port = mongo_config.port
    db_name = mongo_config.database
    collection_name = mongo_config.collection

    mongo = MongoClient(f'mongodb://{host}:{port}/')
    db = mongo[db_name]
    collection = db[collection_name]

    conn = True
    for key, command in commands.items():
        try:
            file_name = key + ".py"
            with open(file_name, 'r', encoding='utf-8') as file:
                command['code'] = file.read()
                command['language'] = "PYTHON"
        except FileNotFoundError:
            pass
        command['_id'] = str(uuid.uuid4())
        try:
            if conn and not collection.count_documents({"name": command['name']}):
                collection.insert_one(command)
        except ServerSelectionTimeoutError:
            conn = False

    # with open('commands.json', 'w', encoding='utf-8') as outfile:
    #     json.dump(commands, outfile, ensure_ascii=False)

    if not conn:
        print("Проверьте работу MongoDB. Соединение не было установлено.")
