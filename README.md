//////**Проблема решена, что была в комментарии**//////

<h1>Дипломная работа</h1>
Задача — разработать REST-сервис. Сервис должен предоставить REST-интерфейс для загрузки файлов и вывода списка уже загруженных файлов пользователя.
Все запросы к сервису должны быть авторизованы.

Заранее подготовленное веб-приложение (FRONT) должно подключаться к разработанному сервису без доработок, а также использовать функционал FRONT для авторизации, загрузки и вывода списка файлов пользователя.

<h2>*Описание*</h2>

- Приложение разработано с использованием Spring Boot;

- Использован сборщик пакетов Maven;

- Использована база данных PostgresSql;

- Для запуска используется docker, docker-compose;

- Код размещен на github;

- Код покрыт unit тестами с использованием mockito;

- Информация о пользователях сервиса хранится в базе данных;

- Информация о файлах пользователей сервиса хранится в базе данных.
***

<h2>**Используемые пользователи**</h2>
1. Логин: string, пароль: string ( $2a$10$PeNz5ZqtTjWAOe4vZF8VyuPtuZ9MEJhE7.EG62unJ5GEBJJ6fA5vq)

2. Логин: user,  пароль: user  ($2a$10$9sMioSuSBOhBqu1ktW.93eSq4r0BiE7aaOG/C2qJTbfFHWa.QTk7e)
        

<h2>**Запускающие команды**</h2>

Запуск FRONT через консоль с помощью node.js  

    npm install
    npm run serve
    
заходим на localhost:8081
 
Запуск BECEND

*основные команды:*

    mvn clean package
    docker-compose build
    docker-compose up -d

Так как отсутствует регистрация пользователь, их можно добавить в CloudApplication:

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String rawPassword = "user";
        String encodedPassword = passwordEncoder.encode(rawPassword);
        System.out.println(encodedPassword);
        
Добавить пользователя с помощью sql запроса нужно в init.sql


<h2>**Порты:**</h2>

Becend 8080

FRONT 8081

PostgresSQL 5432
***
<h2>**Стартовая страница**</h2>

![image](https://github.com/user-attachments/assets/83dd5732-0f3e-47fc-a84f-555b38b2037f)
Регистрируемся и нас перекидывает на следующую старницу.

*Можем добавлять файлы*
![image](https://github.com/user-attachments/assets/a7394eef-ef9e-43ad-8c35-5cc766fb918c)

*Редактировать файлы*
![image](https://github.com/user-attachments/assets/9babe48e-7866-49ab-a0b4-8f7bddcf424b)

*Скачивать файлы*
![image](https://github.com/user-attachments/assets/1dbc1607-d82f-4f4c-80ac-17cdbff0dba4)

*Переименовывать файлы*
![image](https://github.com/user-attachments/assets/8d8e93f3-92df-4ae3-8301-c042a4b0a8b0)

*А так же удалять*







