package br.com.b2w.challenge.planetsrest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.json.JSONException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.b2w.challenge.planetsrest.config.TestConfig;
import br.com.b2w.challenge.planetsrest.model.Planet;


@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PlanetsRestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlanetsControllerTest {

	private final String PLANET_NAME = "Jupiter";
	private final String PLANET_CLIMATE = "Frigid";
	private final String PLANET_TERRAIN = "Gases";
	
	@Autowired
	protected TestRestTemplate rest;
	
	@BeforeAll
	public static void seUp(@Autowired MongoTemplate mongoTemplate) {
		TestConfig.dropCollections(mongoTemplate);
		TestConfig.createAndPopulateCollections(mongoTemplate);
		System.out.println("Dados criados");
	}
	
	@AfterAll
	public static void tearDown(@Autowired MongoTemplate mongoTemplate) {
		TestConfig.dropCollections(mongoTemplate);
		System.out.println("Dados apagados");
	}
	
	private ResponseEntity<Planet> getPlanet(String url) {
		return rest.getForEntity(url, Planet.class);
	}
	
	private ResponseEntity<List<Planet>> getPlanets(String url) {
		return rest.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<Planet>>() {});
	}
	
	@Test
    public void testAddPlanetAndDeleteOk() {

        Planet planet = new Planet();
        planet.setName(PLANET_NAME);
        planet.setClimate(PLANET_CLIMATE);
        planet.setTerrain(PLANET_TERRAIN);

        // Insert the planet
        ResponseEntity<?> response = rest.postForEntity("/api/v1/planets", planet, null);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        String location = response.getHeaders().get("location").get(0);
        
        // Search the planet
        Planet p = getPlanet(location).getBody();

        assertNotNull(p);
        assertEquals(PLANET_NAME, p.getName());
        assertEquals(PLANET_CLIMATE, p.getClimate());
        assertEquals(PLANET_TERRAIN, p.getTerrain());

        // Delete the planet
        ResponseEntity<String> responseDelete = rest.exchange(location, HttpMethod.DELETE, null, String.class);
		assertEquals(HttpStatus.OK, responseDelete.getStatusCode());

		// Check if the planet was deleted
        assertEquals(HttpStatus.NOT_FOUND, getPlanet(location).getStatusCode());
    }
	
	@Test
    public void testAddPlanetNoNameBadRequest() throws JSONException {
		
		String planetInJson = "{\"name\":\"\", \"climate\":\"abc\",\"terrain\":\"abc\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(planetInJson, headers);

        ResponseEntity<String> response = rest.exchange("/api/v1/planets", HttpMethod.POST, entity, String.class);

        String expectedJson = "{\"status\":400,\"errors\":[\"Please provide a name\"]}";
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }
	
	@Test
    public void testAddPlanetWithNumberAppearancesInFilmsBadRequest() throws JSONException {
		
		String planetInJson = "{\"name\":\"Mars\",\"climate\":\"abc\",\"terrain\":\"abc\",\"numberAppearancesInFilms\":\"1\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(planetInJson, headers);

        ResponseEntity<String> response = rest.exchange("/api/v1/planets", HttpMethod.POST, entity, String.class);

        String expectedJson = "{\"status\":400,\"errors\":[\"The field 'numberAppearancesInFilms' must not be provided\"]}";
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }
	
	@Test
    public void testAddPlanetEmptyBadRequest() throws JSONException {
		
		String planetInJson = "{}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(planetInJson, headers);

        ResponseEntity<String> response = rest.exchange("/api/v1/planets", HttpMethod.POST, entity, String.class);

        String expectedJson = "{\"status\":400,\"errors\":[\"Please provide a climate\",\"Please provide a name\",\"Please provide a terrain\"]}";
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }
	
	@Test
    public void testUpdatePlanetOk() {

        Planet planet = new Planet();
        planet.setName(PLANET_NAME);
        planet.setClimate(PLANET_CLIMATE);
        planet.setTerrain(PLANET_TERRAIN);
        
        Integer id = 1;
        
        HttpEntity<Planet> entity = new HttpEntity<Planet>(planet);
        ResponseEntity<Planet> response = rest.exchange("/api/v1/planets/"+id, HttpMethod.PUT, entity, new ParameterizedTypeReference<Planet>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Planet planetUpdated = response.getBody();
        
        assertEquals(id, planetUpdated.getId());
        assertEquals(PLANET_NAME, planetUpdated.getName());
        assertEquals(PLANET_CLIMATE, planetUpdated.getClimate());
        assertEquals(PLANET_TERRAIN, planetUpdated.getTerrain());
    }
	
	@Test
    public void testUpdatePlanetNotFound() {

        Planet planet = new Planet();
        planet.setName(PLANET_NAME);
        planet.setClimate(PLANET_CLIMATE);
        planet.setTerrain(PLANET_TERRAIN);
        
        Integer id = 1111;
        
        HttpEntity<Planet> entity = new HttpEntity<Planet>(planet);
        ResponseEntity<Planet> response = rest.exchange("/api/v1/planets/"+id, HttpMethod.PUT, entity, new ParameterizedTypeReference<Planet>() {});
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
	
	@Test
    public void testUpdatePlanetBadRequest() {

        Planet planet = new Planet();
        planet.setName(PLANET_NAME);
        planet.setClimate(PLANET_CLIMATE);
        planet.setTerrain(PLANET_TERRAIN);
        
        Integer id = null;
        
        ResponseEntity<Planet> response = rest.exchange("/api/v1/planets/"+id, HttpMethod.PUT, null, new ParameterizedTypeReference<Planet>() {});
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
	
	@Test
    public void testUpdatePlanetEmptyBadRequest() throws JSONException {
		
		String planetInJson = "{}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(planetInJson, headers);

        ResponseEntity<String> response = rest.exchange("/api/v1/planets/1", HttpMethod.PUT, entity, String.class);

        String expectedJson = "{\"status\":400,\"errors\":[\"Please provide a climate\",\"Please provide a name\",\"Please provide a terrain\"]}";
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }
	
	@Test
    public void testUpdatePlanetWithNumberAppearancesInFilmsBadRequest() throws JSONException {
		
		String planetInJson = "{\"name\":\"Mars\",\"climate\":\"abc\",\"terrain\":\"abc\",\"numberAppearancesInFilms\":\"1\"}";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(planetInJson, headers);

        ResponseEntity<String> response = rest.exchange("/api/v1/planets/1", HttpMethod.PUT, entity, String.class);

        String expectedJson = "{\"status\":400,\"errors\":[\"The field 'numberAppearancesInFilms' must not be provided\"]}";
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals(expectedJson, response.getBody(), false);
    }
	
	@Test
    public void testGetAllPlanets() {
        ResponseEntity<List<Planet>> response = getPlanets("/api/v1/planets");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        List<Planet> planets = response.getBody();
        assertNotNull(planets);
        assertEquals(20, planets.size());
    }
	
	@Test
    public void testGetPlanetByIdOk() {
		Integer id = 1;
        ResponseEntity<Planet> response = getPlanet("/api/v1/planets/"+id);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Planet planet = response.getBody();
        assertEquals(id, planet.getId());
        assertEquals("Alderaan", planet.getName());
        assertEquals("temperate", planet.getClimate());
        assertEquals("grasslands, mountains", planet.getTerrain());
    }
	
	@Test
    public void testGetPlanetByIdNotFound() {
        ResponseEntity<?> response = getPlanet("/api/v1/planets/1100");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
	
	@Test
    public void testGetPlanetByIdBadRequest() {
        ResponseEntity<?> response = getPlanet("/api/v1/planets/xyz");
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
	
	@Test
    public void testGetPlanetsByNameOk() {
		String name = "Hoth";
        ResponseEntity<List<Planet>> response = getPlanets("/api/v1/planets/name/"+name);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Planet> planets = response.getBody();
        assertEquals(1, planets.size());
       
        Planet planet = planets.get(0);
        assertNotNull(planet.getId());
        assertEquals(name, planet.getName());
        assertEquals("frozen", planet.getClimate());
        assertEquals("tundra, ice caves, mountain ranges", planet.getTerrain());
    }
	
	@Test
    public void testGetPlanetsByNameNoContent() {
		String name = "XYZ";
        ResponseEntity<List<Planet>> response = getPlanets("/api/v1/planets/name/"+name);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
	
	@Test
    public void testGetPlanetsByNameBadRequest() {
		String name = "";
        ResponseEntity<List<Planet>> response = getPlanets("/api/v1/planets/name/"+name);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
	
	@Test
    public void testDeleteNotFound() {
		Integer id = 1111;
		ResponseEntity<String> response = rest.exchange("/api/v1/planets/"+id, HttpMethod.DELETE, null, String.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
	
	@Test
    public void testDeleteBadRequest() {
		String id = "XYZ";
		ResponseEntity<String> response = rest.exchange("/api/v1/planets/"+id, HttpMethod.DELETE, null, String.class);
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
	
}