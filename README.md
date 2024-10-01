# Chatter

![Static Badge](https://img.shields.io/badge/shirotame-Chatter-Desktop)
![GitHub top language](https://img.shields.io/github/languages/top/shirotame/Chatter-Desktop)

Chatter - приложение, реализуещее простейший способ связи между удаленными устройствами. Состоит из 3 элементов: [Chatter-Server](https://github.com/shirotame/Chatter-Server), [Chatter-Desktop](https://github.com/shirotame/Chatter-Desktop) и [Chatter-Android](https://github.com/shirotame/Chatter-Android)

## Как реализовано?

Подключение сделано на java.net.Socket, интерфейс на Swing, все подробности в исходниках проекта. В целом, клиент подключается к серверу, отправляет условный заголовок подключения (в данном случае, символ), отправляет свой никнейм и IP.

## Возможности

Общение с пользователями, подключение к одному серверу (может быть сделаю возможность сохранять подключения).

## Как запустить?

Необходимо скачать последний [релиз](https://github.com/shirotame/Chatter-Desktop/releases) и запустить его. Далее, вводим IP адрес и порт от сервера, к которому хотим подключиться, вводим ник и подключаемся.
