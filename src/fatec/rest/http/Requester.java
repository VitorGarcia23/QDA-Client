package fatec.rest.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Requester {

	public static String get(String urlName, String token) throws IOException {
		String jsonResponse = "";
		HttpURLConnection connection = null;

		URL url = createURL(urlName);

		connection = openConnection(url, connection, "GET", token);
		int responseCode = getResponseCode(connection);
		jsonResponse = readResponse(responseCode, connection);

		return jsonResponse;
	}

	public static String post(String urlName, String body) throws IOException {
		String jsonResponse = "";
		HttpURLConnection connection = null;

		URL url = createURL(urlName);
		connection = openConnection(url, connection, "POST", "");

		try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {			
			wr.writeBytes(body);
			wr.flush();
		}

		int responseCode = getResponseCode(connection);
		jsonResponse = readResponse(responseCode, connection);

		return jsonResponse;
	}
	
	private static URL createURL(String urlName) throws MalformedURLException {
		if (urlName != null && !urlName.equals(""))
			return new URL(urlName);

		throw new RuntimeException("Invalid Url.");
	}

	private static int getResponseCode(HttpURLConnection connection) throws IOException {
		if (connection != null)
			return connection.getResponseCode();
		throw new RuntimeException("Error on get response status.");
	}

	private static HttpURLConnection openConnection(URL url, HttpURLConnection connection, String method, String token)
			throws IOException {
		connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod(method);
		connection.setRequestProperty("Content-type", "application/json");
		connection.setRequestProperty("Authorization", token);
		connection.setDoOutput(true);

		return connection;
	}

	private static String readResponse(int responseCode, HttpURLConnection connection) throws IOException {
		if (responseCode >= 200 && responseCode < 300) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}

			reader.close();

			return response.toString();
		}

		throw new IOException("Some errors occurred. Status Code : " + responseCode);
	}
}
