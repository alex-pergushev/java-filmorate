package ru.yandex.practicum.filmorate.web.controller;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpTestClient {

	private int lastResponseStatusCode;
	private HttpResponse<String> response;

	public int getLastResponseStatusCode() {
		return lastResponseStatusCode;
	}

	public HttpResponse<String> getResponse() {
		return response;
	}

	//Вспомогательный метод для отправки GET запроса
	public String get(String url){
		lastResponseStatusCode = -1;
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.GET()
				.build();
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			lastResponseStatusCode = response.statusCode();
			return response.body();
		} catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
			System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
		}
		return "";
	}

	//Вспомогательный метод для отправки POST запроса
	public void post(String url, String body){
		lastResponseStatusCode = -1;
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.headers("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body))
				.build();
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			lastResponseStatusCode = response.statusCode();
		} catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
			System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
		}
	}

	//Вспомогательный метод для отправки DELETE запроса
	public void put(String url, String body){
		lastResponseStatusCode = -1;
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.headers("Content-Type", "application/json")
				.PUT(HttpRequest.BodyPublishers.ofString(body))
				.build();
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
			lastResponseStatusCode = response.statusCode();
		} catch (IOException | InterruptedException e) { // обрабатываем ошибки отправки запроса
			System.out.println("Во время выполнения запроса \"" + url + "\" возникла ошибка: " + e.getMessage());
		}
	}
}
