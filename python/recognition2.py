from ctypes import c_bool
from multiprocessing import Process, JoinableQueue, Value
import time
import requests
import json

import pyautogui
import pyttsx3
import psutil
from speech_recognition import (
    AudioData, Microphone, 
    Recognizer, UnknownValueError)

LANG = 'ru-RU'
# SERVER = 'http://10.241.129.135:8091/'
SERVER = 'http://127.0.0.1:8085/'
SERVER_AUTH = 'http://127.0.0.1:8081/'


def do():
    username = ""
    password = ""
    user_id = ""

    recognizer = Recognizer()
    engine = pyttsx3.init()
    # rate = engine.getProperty('rate')
    # engine.setProperty('rate', rate)
    voices = engine.getProperty('voices')
    engine.setProperty('voice', voices[2].id)
    data = {}

    engine.say("Слушаю")
    engine.runAndWait()
    # phrases = ["войти", "напиши . работает"]
    i = -1
    with Microphone() as source:
        while True:
            i+=1
            print("Слушаю Вас")
            try:            
                audio = recognizer.listen(source)
                engine.say("услышала")
                engine.runAndWait()
                phrase = recognizer.recognize_google(audio, language=LANG)
                # phrase = phrases[i]
                print("Ваша фраза:", phrase)
                phrase = phrase.lower().strip()
                if phrase == "завершить":
                    engine.say("Приятно было пообщаться")
                    engine.runAndWait()
                    break
                if user_id:
                    if phrase != "":
                        print("Обрабатываю")
                        engine.say("Слушаю и повинуюсь")
                        engine.runAndWait()
                        process_command(phrase, data, engine, user_id)
                        engine.say("Готова к новой задаче")
                        engine.runAndWait()
                else:
                    if phrase != "войти":
                        engine.say("Не знаю, кто вы")
                        engine.runAndWait()
                    else:
                        with open("auth.json", 'r') as auth:
                            creds = json.load(auth)
                        resp = requests.post(SERVER_AUTH+"login", json={
                            'username': creds['username'],
                            'password': creds['password']
                        })
                        if resp.status_code == 200:
                            user_id = resp.json()['id']
                            engine.say(f"Здравствуйте,  {creds['username']}")
                            engine.runAndWait()
                        else:
                            engine.say("Ошибка входа")
                            engine.runAndWait()
                            print(resp.text)
            except UnknownValueError:
                print("Говорите членораздельней")
                engine.say("Не понимаю вас")
                engine.runAndWait()
            except Exception as e:
                engine.say("Возникла ошибка с командой")
                engine.runAndWait()
                print(e)

def process_command(phrase, data, engine, user_id):
    data.pop("error", None)
    json = {
        'phrase': phrase,
        'user_id': user_id
    }
    resp = requests.post(SERVER+"command", json=json)
    if resp.status_code != 200:
        print(resp.text)
        if resp.text == "There is no such command":
            engine.say("Такой команды нет")
        engine.runAndWait()
    else:
        js = resp.json()
        if js['args'] is not None:
            data.update(js['args'])
        print(data)
        for code in js['codes']:
            exec(code, {'data': data, 'temp': temp})
            if 'error' in data:
                print("Ошибка:", data['error'])
                engine.say("Возникла ошибка")
                engine.runAndWait()
                break
            time.sleep(0.2)
            

def temp(data):
    exec(data['code'])

if __name__ == "__main__":
    do()