package fatec.rest.qdaclient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.BeforeClass;
import org.junit.Test;

import fatec.rest.http.Requester;

public class App {

	private static String config;
	private static String token = "1853MHmEFI41JK1iHC4pIJM712Bw2BHKxJp7E5wEmH3EIylE2IAB1SS4C3T8UUtx6LbDRewPf1FyLC5yGNXIkpeVMRNCHJ8D2dMV";
	private static final String CONFIG_ENDPOINT = "http://localhost:8080/qdacore/api/config";
	private static final String RESULT_ENDPOINT = "http://localhost:8080/qdacore/api/result";

	@BeforeClass
	public static void setup() {
		try {
			config = new String(Files.readAllBytes(Paths.get("config/apis.json")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void request_with_config_file() throws IOException {
		String result = Requester.post(CONFIG_ENDPOINT, config);
		String expected = "\\{ \"token\": \"\\w{100}\" \\}";
				
		assertTrue(result.matches(expected));
	}

	@Test
	public void authentication_success() throws IOException {	
		String result = Requester.get(RESULT_ENDPOINT, token);
		String expected = "{\"requestErrors\":[],\"bulkResponse\":true,\"providedBy\":\"QDA Core Service\",\"creators\":\"Guilherme Vasconcellos and Vitor Garcia\"}";
		
		assertEquals(expected, result);
	}

	@Test(expected = IOException.class)
	public void authentication_fail() throws IOException {
		Requester.get(RESULT_ENDPOINT, "fail");
	}

	@Test
	public void request_all_endpoints_passing_fields() throws IOException {
		String url = RESULT_ENDPOINT + "?serieId=4&title=Arrow&seasonNumber=5&episodeNumber=1";
		String result = Requester.get(url, token);
		String regex = ".*\"bulkResponse\":([true|false]+).*";
		String expected = result.replaceAll(regex, "$1");
		
		assertTrue(new Boolean(expected));
	}

	@Test
	public void request_partial_endpoints_passing_fields() throws IOException {
		String url = RESULT_ENDPOINT + "?serieId=4&title=Arrow";
		String result = Requester.get(url, token);
		
		String regex = ".*\"requestErrors\":(\\[\\]).*";
		String expected = result.replaceAll(regex, "$1");
		
		assertEquals(expected, "[]");
	}
}
