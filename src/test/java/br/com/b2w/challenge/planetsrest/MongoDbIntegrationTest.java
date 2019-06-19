package br.com.b2w.challenge.planetsrest;

import br.com.b2w.challenge.planetsrest.model.Planet;
import br.com.b2w.challenge.planetsrest.model.PlanetRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataMongoTest
public class MongoDbIntegrationTest {

    private static String collectionName;
    private static Planet planetToInsert;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private PlanetRepository planetRepository;

    @BeforeAll
    public static void before() {
        collectionName = "planet";
        planetToInsert = new Planet();
        planetToInsert.setId(1);
        planetToInsert.setName("Endor");
        planetToInsert.setClimate("temperate");
        planetToInsert.setTerrain("forests");
    }

    @AfterEach
    public void after() {
        mongoTemplate.dropCollection(collectionName);
    }

    @Test
    public void checkMongoTemplate() {
        assertNotNull(mongoTemplate);
        mongoTemplate.createCollection(collectionName);
        assertTrue(mongoTemplate.collectionExists(collectionName));
    }

    @Test
    public void checkDocumentAndQuery() {
        mongoTemplate.save(planetToInsert, collectionName);
        Query query = new Query(new Criteria()
                .andOperator(Criteria.where("name").regex(planetToInsert.getName()),
                        Criteria.where("climate").regex(planetToInsert.getClimate()),
                        Criteria.where("terrain").regex(planetToInsert.getTerrain())));

        Planet retrievedLogRecord = mongoTemplate.findOne(query, Planet.class, collectionName);
        assertNotNull(retrievedLogRecord);
    }

    @Test
    public void checkPlanetRepository() {
        assertNotNull(planetRepository);
        Planet savedLogRecord = planetRepository.save(planetToInsert);
        assertNotNull(planetRepository.findById(savedLogRecord.getId()));
    }

}