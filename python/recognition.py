from ctypes import c_bool
from multiprocessing import Process, JoinableQueue, Value
# import re
import requests
# import os

import pyautogui
import pyttsx3
import psutil
from speech_recognition import (
    AudioData, Microphone, 
    Recognizer, UnknownValueError)

LANG = 'ru-RU'
# SERVER = 'http://10.241.129.135:8091/'
SERVER = 'http://127.0.0.1:8085/'

def listen(audio_queue, recognize):
    engine = pyttsx3.init()
    rate = engine.getProperty('rate')
    engine.setProperty('rate', rate-40)
    engine.say("I am listening")
    engine.runAndWait()
    with Microphone() as source:
        while True:
            print("Слушаю Вас")
            audio = recognize.listen(source)
            audio_queue.put(audio)
            engine.say("Ok")
            engine.runAndWait()


def recognize(audio_queue, recognize, pid):
    engine = pyttsx3.init()
    rate = engine.getProperty('rate')
    engine.setProperty('rate', rate-60)
    data = {}
    while True:
        try:            
            audio = audio_queue.get()            
            phrase = recognize.recognize_google(audio, language=LANG)
            print("Ваша фраза:", phrase)
            phrase = phrase.lower().strip()
            if phrase == "стоп":
                psutil.Process(pid).kill()
                engine.say("Bye. It was cool!")
                engine.runAndWait()
                break
            if phrase != "":
                print("Обрабатываю")
                get_exec_command(phrase, data)
                print("Готово")
            audio_queue.task_done()
        except UnknownValueError:
            # engine.say("I didn't understand")
            print("Говорите членораздельней")
            # engine.runAndWait()
        except Exception as e:
            print(e)

def get_exec_command(phrase, data):
    resp = requests.post(SERVER+"command", data=str(phrase).encode('utf-8'))
    if resp.status_code != 200:
        print(resp.text)
    else:
        js = resp.json()
        # print(js)
        if js['args'] is not None:
            data.update(js['args'])
        for code in js['codes']:
            exec(code, {'data': data, 'temp': temp})
            if 'error' in data:
                print("Ошибка:", data['error'])
                break
            

def temp(data):
    exec(data['code'])

if __name__ == "__main__":
    r = Recognizer()
    audio_queue = JoinableQueue()

    listen_thread = Process(target=listen, args=(audio_queue, r))
    listen_thread.start()
    recognize_thread = Process(target=recognize, args=(audio_queue, r, listen_thread.pid))
    recognize_thread.start()


    print("Listen pid", listen_thread.pid)
    print("Recognize pid", recognize_thread.pid)
    recognize_thread.join()
    listen_thread.join()