package br.com.b2w.challenge.planetsrest.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.b2w.challenge.planetsrest.model.CustomSequence;
import br.com.b2w.challenge.planetsrest.model.Planet;

public final class TestConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TestConfig.class);
	
	private TestConfig() {}
	
	public static void createAndPopulateCollections(MongoTemplate mongoTemplate) {
		int totalInsertedElments = initializePlanetCollection(mongoTemplate);
		initializeSequencePlanetCollection(mongoTemplate, totalInsertedElments);
	}

	public static void dropCollections(MongoTemplate mongoTemplate) {
		mongoTemplate.dropCollection(CustomSequence.class);
		mongoTemplate.dropCollection(Planet.class);
		LOGGER.info("Collections ("+CustomSequence.class.getSimpleName()+", "+Planet.class.getSimpleName()+") were successfully dropped!");
	}

	private static int initializePlanetCollection(MongoTemplate mongoTemplate) {
		int totalInsertedElments = 0;
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<List<Planet>> typeReference = new TypeReference<List<Planet>>(){};
		InputStream inputStream = TypeReference.class.getResourceAsStream("/data.json");
		try {
			List<Planet> planets = mapper.readValue(inputStream, typeReference);
			mongoTemplate.insertAll(planets);
			totalInsertedElments = planets.size();
			
			LOGGER.info("Collection ("+Planet.class.getSimpleName()+") was successfully created and populated! "+totalInsertedElments+" elements were inserted!");
		} catch (IOException e) {
			LOGGER.error("Error trying to populate the collection ("+Planet.class.getSimpleName()+") "+ e.getMessage(), e);
		}
		return totalInsertedElments;
	}

	private static void initializeSequencePlanetCollection(MongoTemplate mongoTemplate, int startValue) {
		CustomSequence sequence = new CustomSequence();
		sequence.setId(Planet.class.getName());
		sequence.setSeq(startValue);
		mongoTemplate.insert(sequence);
		
		LOGGER.info("Collection ("+CustomSequence.class.getSimpleName()+") was successfully created and populated! Initial value = "+ startValue);
	}

}