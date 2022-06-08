package ru.yandex.practicum.filmorate.web.controller;

import org.junit.jupiter.api.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.yandex.practicum.filmorate.FilmorateApplication;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {
	private final String userServerURL = "http://localhost:8080/users";
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

	static final String userJson = "{" +
			"\"email\":\"alex@pergushev.ru\"," +
			"\"login\":\"pergushev\"," +
			"\"name\":\"Alex\"," +
			"\"birthday\":\"1976-06-07\"}";

	static final String userJsonTestCreate = "{" +
			"\"id\":1," +
			"\"email\":\"alex@pergushev.ru\"," +
			"\"login\":\"pergushev\"," +
			"\"name\":\"Alex\"," +
			"\"birthday\":\"1976-06-07\"}";

	static final String userJsonFailLogin = "{" +
			"\"email\":\"alex@pergushev.ru\"," +
			"\"login\":\"alex pergushev\"," +
			"\"name\":\"Alex\"," +
			"\"birthday\":\"1976-06-07\"}";

	static final String userJsonFailEmail = "{" +
			"\"email\":\"alex-pergushev.ru\"," +
			"\"login\":\"pergushev\"," +
			"\"name\":\"Alex\"," +
			"\"birthday\":\"1976-06-07\"}";

	static final String userJsonFailBirthday = "{" +
			"\"email\":\"alex@pergushev.ru\"," +
			"\"login\":\"pergushev\"," +
			"\"name\":\"Alex\"," +
			"\"birthday\":\"2976-06-07\"}";

	static final String userJsonUpdate = "{" +
			"\"id\":1," +
			"\"email\":\"kirill@pergushev.ru\"," +
			"\"login\":\"pergushev\"," +
			"\"name\":\"Kirill\"," +
			"\"birthday\":\"2014-11-04\"}";

	static final String userJsonWithEmptyName = "{" +
			"\"email\":\"alex@pergushev.ru\"," +
			"\"login\":\"pergushev\"," +
			"\"name\":\"\"," +
			"\"birthday\":\"1976-06-07\"}";

	static final String userJsonWithEmptyNameTestCreate = "{" +
			"\"id\":1," +
			"\"email\":\"alex@pergushev.ru\"," +
			"\"login\":\"pergushev\"," +
			"\"name\":\"pergushev\"," +
			"\"birthday\":\"1976-06-07\"}";

	@DisplayName("User create")
	@Test
	void test9_createUser() {
		httpTestClient.post(userServerURL, userJson);
		assertEquals(200, httpTestClient.getLastResponseStatusCode());
		// тест создание пользователя
		String jsonData = httpTestClient.getResponse().body();
		assertEquals(userJsonTestCreate, jsonData);
	}

	@DisplayName("User create Fail login")
	@Test
	void test10_createUserFailLogin() {
		httpTestClient.post(userServerURL, userJsonFailLogin);
		assertEquals(400, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("User create Fail email")
	@Test
	void test11_createUserFailEmail() {
		httpTestClient.post(userServerURL, userJsonFailEmail);
		assertEquals(400, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("User create Fail birthday")
	@Test
	void test12_createUserFailBirthday() {
		httpTestClient.post(userServerURL, userJsonFailBirthday);
		assertEquals(400, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("User update")
	@Test
	void test13_updateUser() {
		httpTestClient.post(userServerURL, userJson);
		httpTestClient.put(userServerURL, userJsonUpdate);
		assertEquals(200, httpTestClient.getLastResponseStatusCode());
		// тест обновления фильма
		String jsonData = httpTestClient.getResponse().body();
		assertEquals(userJsonUpdate, jsonData);
	}

	@DisplayName("User update unknown")
	@Test
	void test14_updateUserUnknown() {
		httpTestClient.put(userServerURL, userJsonUpdate);
		assertEquals(500, httpTestClient.getLastResponseStatusCode());
	}

	@DisplayName("User get All")
	@Test
	void test15_getUserAll() {
		// пустой список
		assertEquals("[]", httpTestClient.get(userServerURL));
		httpTestClient.post(userServerURL, userJson);
		httpTestClient.get(userServerURL);
		assertEquals(200, httpTestClient.getLastResponseStatusCode());
		// тест работы getAll
		String jsonData = httpTestClient.getResponse().body().replace("[", "").replace("]", "");
		assertEquals(userJsonTestCreate, jsonData);
	}

	@DisplayName("Create user with empty name")
	@Test
	void test16_createUserWithEmptyName() {
		httpTestClient.post(userServerURL, userJsonWithEmptyName);
		assertEquals(200, httpTestClient.getLastResponseStatusCode());
		// тест создание пользователя
		String jsonData = httpTestClient.getResponse().body();
		assertEquals(userJsonWithEmptyNameTestCreate, jsonData);
	}
}