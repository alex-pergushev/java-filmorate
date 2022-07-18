# *Filmorate*
___
### Проект фильмотеки с возможностью оценки фильмов пользователями и объединением пользователей дружескими связями.
___

Схема базы данных:

![Схема базы данных.](/src/main/resources/static/QuickDBDiagram.png?raw=true "Схема базы данных.")



| Таблица           | Описание                                                            |
|-------------------|---------------------------------------------------------------------|
| **_users_**       | Пользователи ресурса                                                |
| **_films_**       | Информация о фильмах                                                |
| **_likes_**       | Таблица связи для хранения лайков пользователей на фильм            |
| **_friends_**     | Таблица связи для хранения информации о дружбе между пользователями |
| **_film_genres_** | Таблица связи для хранения информации о жанрах фильма               |
| **_genres_**      | Таблица жанров фильма                                               |
| **_mpa_**         | Таблица рейтингов фильма                                            |

Примеры обращения к данным.

Получение списка друзей пользователя:
````SQL
SELECT *
  FROM user AS u 
 WHERE u.user_id in (SELECT friend_id 
                       FROM friends AS f
                      WHERE f.user_id = ?
                        AND f.status = 'CONFIRMED');
````

Получение списка пользователей, отправивших запрос на добавление в друзья пользователя:
````SQL
SELECT *
  FROM user AS u 
 WHERE u.user_id in (SELECT friend_id 
                       FROM friends AS f
                      WHERE f.user_id = ?
                        AND f.status = 'UNCONFIRMED');
````

Получение жанров фильмов, которые нравятся пользователю:
````SQL
SELECT DISTINCT g.name AS genre_name
  FROM film AS f
  JOIN film_ganre AS fg ON (f.film_id = fg.film_id) 
  JOIN genre AS g ON (fg.genre_id = g.genre_id)
 WHERE f.film_id in (SELECT film_id
                       FROM likes AS l
                      WHERE l.user_id = ?);
````

Получение общих друзей пользователей user1 и user2:
````SQL
SELECT *
  FROM user AS u
 WHERE u.user_id in (SELECT friend_id
                       FROM friends AS f
                      WHERE f.user_id = user1_id
                        AND f.status = 'CONFIRMED')
   AND u.user_id in (SELECT friend_id
                       FROM friends f
                      WHERE f.user_id = user2_id
                        AND f.status = 'CONFIRMED');
````
