--Таблица жанров фильмов
CREATE TABLE IF NOT EXISTS genres (
    genre_id  int PRIMARY KEY NOT NULL,
    name      varchar NOT NULL
);

--Таблица рейтингов фильмов
CREATE TABLE IF NOT EXISTS mpa (
    mpa_id    int PRIMARY KEY NOT NULL,
    name      varchar NOT NULL
);

--Таблица фильмов
CREATE TABLE IF NOT EXISTS films (
    film_id       int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name          varchar  NOT NULL,
    description   varchar,
    release_date  date  NOT NULL,
    duration      int,
    mpa_id        int REFERENCES mpa (mpa_id) ON DELETE CASCADE,
    CONSTRAINT pk_films PRIMARY KEY (film_id)
);

--Таблица пользователей
CREATE TABLE IF NOT EXISTS users (
    user_id   int GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    email     varchar NOT NULL,
    login     varchar NOT NULL,
    name      varchar,
    birthday  date,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);

--Таблица лайков фильмов пользователями
CREATE TABLE IF NOT EXISTS likes (
    film_id int REFERENCES films (film_id) ON DELETE CASCADE NOT NULL,
    user_id int REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    CONSTRAINT pk_likes PRIMARY KEY (film_id, user_id)
);

--Таблица жанров, которым принадлежит фильм
CREATE TABLE IF NOT EXISTS film_genres (
    film_id   int REFERENCES films (film_id) ON DELETE CASCADE NOT NULL,
    genre_id  int REFERENCES genres (genre_id) ON DELETE CASCADE NOT NULL,
    CONSTRAINT pk_film_genres PRIMARY KEY (film_id, genre_id)
);

--Таблица дружеских связей между пользователями
CREATE TABLE IF NOT EXISTS friends (
    user_id   int REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    friend_id int REFERENCES users (user_id) ON DELETE CASCADE NOT NULL,
    CONSTRAINT pk_friends PRIMARY KEY (user_id, friend_id)
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_user_email ON users(email);
CREATE UNIQUE INDEX IF NOT EXISTS idx_user_login ON users(login);
CREATE UNIQUE INDEX IF NOT EXISTS idx_film_name_date ON films (name, release_date);

--Представление для отображения имени рейтинга
CREATE OR REPLACE VIEW v_film_mpa AS
SELECT f.*, m.name mpa_name
FROM films AS f, mpa AS m
WHERE f.mpa_id = m.mpa_id;