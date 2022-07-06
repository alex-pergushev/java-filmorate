# *Filmorate*
___
### Проект фильмотеки с возможностью оценки фильмов пользователями и объединением пользователей дружескими связями.
___

Схема базы данных:

![Схема базы данных.](/src/main/resources/static/QuickDBDiagram.png?raw=true "Схема базы данных.")



| Таблица          | Описание                                                            |
|------------------|---------------------------------------------------------------------|
| **_user_**       | Пользователи ресурса                                                |
| **_film_**       | Информация о фильмах                                                |
| **_likes_**      | Таблица связи для хранения лайков пользователей на фильм            |
| **_friends_**    | Таблица связи для хранения информации о дружбе между пользователями |
| **_film_genre_** | Таблица связи для хранения информации о жанрах фильма               |
| **_genre_**      | Таблица жанров фильма                                               |

Примеры обращения к данным.

Получение списка друзей пользователя t_user:
````SQL
SELECT *
  FROM user AS u 
 WHERE u.user_id in (SELECT friend_id 
                       FROM friends AS f
                      WHERE f.user_id = :t_user
                        AND f.status = 'CONFIRMED');
````

Получение списка пользователей, отправивших запрос на добавление в друзья пользователя t_user:
````SQL
SELECT *
  FROM user AS u 
 WHERE u.user_id in (SELECT friend_id 
                       FROM friends AS f
                      WHERE f.user_id = :t_user
                        AND f.status = 'UNCONFIRMED');
````

Получение жанров фильмов, которые нравятся пользователю t_user:
````SQL
SELECT DISTINCT g.name AS genre_name
  FROM film AS f
  JOIN film_ganre AS fg ON (f.film_id = fg.film_id) 
  JOIN genre AS g ON (fg.genre_id = g.genre_id)
 WHERE f.film_id in (SELECT friend_id
                       FROM likes AS l
                      WHERE l.user_id = :t_user);
````

Получение общих друзей пользователей t_user_1 и t_user_2:
````SQL
SELECT *
  FROM user AS u
 WHERE u.user_id in (SELECT friend_id
                       FROM friends AS f
                      WHERE f.user_id = :t_user_1
                        AND f.status = 'CONFIRMED')
   AND u.user_id in (SELECT friend_id
                       FROM friends f
                      WHERE f.user_id = :t_user_2
                        AND f.status = 'CONFIRMED');
````
