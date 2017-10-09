package fatec.rest.qdaclient;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import fatec.rest.http.Requester;

/*	ESTRATEGIAS DE TESTE
 * 		
 * 	POST - CLASSES DE EQUIVALENCIA (BODY INVALIDO,BODY VALIDO)
 *  POST BODY VALIDO - ANALISE DO VALOR LIMITE ( JSON DE NIVEIS 1, 2 E 3) SENDO 2 O LIMITE(CORRETO)
 *  AUTENTICACAO - CLASSES DE EQUIVALENCIA (COM TOKEN, SEM TOKEN)
 *  GET - ANALISE DO VALOR LIMITE (SEM PARAMETROS, COM PARAMETROS DE ENDPOINTS PARCIAIS, COM PARAMETROS DE TODOS ENDPOINTS)
 */
public class App {
	@Rule public TestName name = new TestName();
	private static String configL1,configL2,configL3;
	private static String token = "1853MHmEFI41JK1iHC4pIJM712Bw2BHKxJp7E5wEmH3EIylE2IAB1SS4C3T8UUtx6LbDRewPf1FyLC5yGNXIkpeVMRNCHJ8D2dMV";
	private static final String CONFIG_ENDPOINT = "http://localhost:8080/qdacore/api/config";
	private static final String RESULT_ENDPOINT = "http://localhost:8080/qdacore/api/result";

	@BeforeClass
	public static void setup() {
		try {
			configL2 = new String(Files.readAllBytes(Paths.get("config/apisL2.json")));
			configL1 = new String(Files.readAllBytes(Paths.get("config/apisL1.json")));
			configL3 = new String(Files.readAllBytes(Paths.get("config/apisL3.json")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Before
	public void setUpTest() throws IOException {
		if(name.getMethodName().equals("request_with_config_file_on_body") || name.getMethodName().equals("request_with_empty_body" ))
			Requester.post(CONFIG_ENDPOINT, configL2);
	} 

	/* POST TESTS*/
	
	@Test
	public void request_with_config_file_on_body() throws IOException {
		String result = Requester.post(CONFIG_ENDPOINT, configL2);
		String expected = "\\{ \"token\": \"\\w{100}\" \\}";
				
		assertTrue(result.matches(expected));
	}
	
	@Test(expected = IOException.class)
	public void request_with_empty_body() throws IOException {
		String result = Requester.post(CONFIG_ENDPOINT, "");
	}

/*	@Test
	public void request_with_level_three_json_on_body() throws IOException {
		String result = Requester.post(CONFIG_ENDPOINT, configL3);
	}
	
	@Test
	public void request_with_level_one_json_on_body() throws IOException {
		String result = Requester.post(CONFIG_ENDPOINT, configL1);
	}
	
	@Test
	public void request_with_no_endpoints_on_body() throws IOException {
		String result = Requester.post(CONFIG_ENDPOINT, "");
	}*/
	
	/* GET Tests*/

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
	
	@Test
	public void request_without_endpoints_passing_fields() throws IOException {
		String url = RESULT_ENDPOINT;
		String result = Requester.get(url, token);
		
		String regex = ".*\"requestErrors\":(\\[\\]).*";
		String expected = result.replaceAll(regex, "$1");
		
		assertEquals(expected, "[]");
	}
	
	/* 	AUTHENTICATION TESTS*/
	
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

}
