package ru.yandex.practicum.filmorate.web.controller;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

	private final String filmServerURL = "http://localhost:8080/films";

	private static HttpTestClient httpTestClient;

	private static ConfigurableApplicationContext context;

	@BeforeAll
	public static void beforeAll() {
		httpTestClient = new HttpTestClient();
	}

	@BeforeEach
	public void setUp() {
		context = SpringApplication.run(FilmorateApplication.class);
	}

	@AfterEach
	public void tearDown() {
		context.close();
	}

	static final String filmJsonValid = "{" +
			"\"name\":\"Ленин в Октябре\"," +
			"\"description\":\"Валидный пример фильма\"," +
			"\"releaseDate\":\"1937-06-07\"," +
			"\"duration\":90}";

	static final String filmJsonValidTestCreate = "{" +
			"\"id\":1," +
			"\"name\":\"Ленин в Октябре\"," +
			"\"description\":\"Валидный пример фильма\"," +
			"\"releaseDate\":\"1937-06-07\"," +
			"\"duration\":90}";

	static final String filmJsonUpdate = "{" +
			"\"id\":1," +
			"\"name\":\"Ленин в Ноябре, или в Декабре.\"," +
			"\"description\":\"Обновление фильма\"," +
			"\"releaseDate\":\"2021-06-07\"," +
			"\"duration\":120}";

	static final String filmJsonEmptyTitle = "{" +
			"\"description\":\"Во время работы над фильмом закончились все буквы\"," +
			"\"releaseDate\":\"1976-06-07\"," +
			"\"duration\":90}";

	static final String filmJsonBigDescription = "{" +
			"\"name\":\"Ленин на броневике\"," +
			"\"description\":\"По воспоминаниям очевидцев, которые все-таки смогли что-то сохранить в своей памяти, " +
			"Ленин говорил о прошедшей войне 1914 года, куда несправедливо втянули русский народ, " +
			"о будущем крахе европейского империализма, о новых возможностях для рабочих, о капиталистах, " +
			"что обдирают честных граждан. Единственным, что запомнилось почти всем присутствующим, " +
			"была последняя фраза, выкрикнутая вождем пролетариев: Да здравствует социалистическая революция!\"," +
			"\"releaseDate\":\"1967-04-24\"," +
			"\"duration\":100}";

	static final String filmJsonOldRelease = "{" +
			"\"name\":\"Политическая программа Ленина. Арест в 1895 году.\"," +
			"\"description\":\"Фильм вышедший на экраны ранее дня рождения кино\"," +
			"\"releaseDate\":\"1895-12-27\"," +
			"\"duration\":60}";

	static final String filmJsonNegativeDuration = "{" +
			"\"name\":\"Ленин в шалаше\"," +
			"\"description\":\"На съемки фильма ушло -1000 метров пленки\"," +
			"\"releaseDate\":\"1976-06-07\"," +
			"\"duration\":-90}";

	@DisplayName("Film create")
	@Test
	void test1_createFilm() {

		httpTestClient.post(filmServerURL, filmJsonValid);
		assertEquals(200, httpTestClient.getLastResponseStatusCode());

		// тест создание фильма
		String jsonData = httpTestClient.response.body();
		assertEquals(filmJsonValidTestCreate, jsonData);
	}

	@DisplayName("Film create Fail name")
	@Test
	void test2_createFilmFailName() {

		// название не может быть пустым
		httpTestClient.post(filmServerURL, filmJsonEmptyTitle);
		assertEquals(400, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("Film create Fail description")
	@Test
	void test3_createFilmFailDesc() {

		// максимальная длина описания — 200 символов
		httpTestClient.post(filmServerURL, filmJsonBigDescription);
		assertEquals(400, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("Film create Fail release data")
	@Test
	void test4_createFilmFailReleaseData() {

		// дата релиза — не раньше 28 декабря 1895 года;
		httpTestClient.post(filmServerURL, filmJsonOldRelease);
		assertEquals(500, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("Film create Fail duration")
	@Test
	void test5_createFilmFailDuration() {

		// продолжительность фильма должна быть положительной
		httpTestClient.post(filmServerURL, filmJsonNegativeDuration);
		assertEquals(400, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("Film update")
	@Test
	void test6_updateFilm() {

		httpTestClient.post(filmServerURL, filmJsonValid);

		httpTestClient.put(filmServerURL, filmJsonUpdate);
		assertEquals(200, httpTestClient.getLastResponseStatusCode());

		// тест обновления фильма
		String jsonData = httpTestClient.response.body();
		assertEquals(filmJsonUpdate, jsonData);
	}

	@DisplayName("Film update unknown")
	@Test
	void test7_updateFilmUnknown() {

		httpTestClient.put(filmServerURL, filmJsonUpdate);
		assertEquals(500, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("Film get All")
	@Test
	void test8_getFilmAll() {

		// пустой список
		assertEquals("[]", httpTestClient.get(filmServerURL));

		httpTestClient.post(filmServerURL, filmJsonValid);

		httpTestClient.get(filmServerURL);
		assertEquals(200, httpTestClient.getLastResponseStatusCode());

		// тест работы getAll
		String jsonData = httpTestClient.response.body().replace("[", "").replace("]", "");
		assertEquals(filmJsonValidTestCreate, jsonData);
	}



}