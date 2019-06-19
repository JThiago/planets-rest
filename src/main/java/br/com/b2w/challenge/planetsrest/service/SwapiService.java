package br.com.b2w.challenge.planetsrest.service;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * This class is used to connect to the Star Wars public API (https://swapi.co/)
 * and retrieve the number of films that a given planet has appeared in.
 * 
 * @author Jorge Thiago
 */
@Service
public class SwapiService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SwapiService.class);
	
	private final RestTemplate restTemplate;

	public SwapiService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}
	
	public Integer getNumberAppearancesInFilms(String planetName) {
		
		Integer numberAppearancesInFilms = 0;
        try {
	        ResponseEntity<String> response = executeGetSwapiPlanetsByName(planetName);
	
	        if(HttpStatus.OK.equals(response.getStatusCode())) {
	        
		        ObjectMapper objectMapper = new ObjectMapper();
		        
				JsonNode root = objectMapper.readTree(response.getBody());
				
				JsonNode result = root.path("results").get(0);
				
				if(result != null) {
					JsonNode name = result.path("name");
					
					if(planetName.equals(name.asText())) {
					
						ArrayNode films = (ArrayNode) result.path("films");
						
						return films.size();
					}
				}
	        }
        } catch (Exception e) {
        	LOGGER.error("Error trying to retrieve the number of films that the planet = "+planetName+" has appeared in.", e);
        	numberAppearancesInFilms = null;
        }
		return numberAppearancesInFilms;
	}
	
	private ResponseEntity<String> executeGetSwapiPlanetsByName(String planetName) {
		final String uri = "https://swapi.co/api/planets/?search="+planetName;
	     
	    HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add(HttpHeaders.USER_AGENT, "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        
        HttpEntity<String> entity = new HttpEntity<String>(headers);

	    return restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
	}
	
}