TODO-list
-
* подготовить ядро сервера на базе Netty. Успешно подключиться telnet
* ~~подключить библиотеку тестирования~~
* найти и реализовать управление параметрами сервера через файл
  * ~~порт~~
  * корневая папка для файлов
  * добавлять по мере необходимости
* поддержать регистрацию и авторизациюна
  * ~~на сервере подключить БД~~
    * ~~добавить проперти в файл для подключения~~
    * БД будт хранить как пользователей, так и данные файлов. Возможно, стоит выбрать что-то покруче SQLite
    * реализовать программный интерфейс для регистрации и авторизации
  * на клиенте создать базовую логику общения с сервером (Netty?..)
  * на клиенте создать простой CLI-клиент