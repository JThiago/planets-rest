package br.com.b2w.challenge.planetsrest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.b2w.challenge.planetsrest.config.TestConfig;
import br.com.b2w.challenge.planetsrest.controller.exception.ObjectNotFoundException;
import br.com.b2w.challenge.planetsrest.model.Planet;
import br.com.b2w.challenge.planetsrest.service.PlanetService;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PlanetServiceTest {

	@Autowired
	private PlanetService service;
	
	@BeforeAll
	public static void seUp(@Autowired MongoTemplate mongoTemplate) {
		TestConfig.dropCollections(mongoTemplate);
		TestConfig.createAndPopulateCollections(mongoTemplate);
	}
	
	@AfterAll
	public static void tearDown(@Autowired MongoTemplate mongoTemplate) {
		TestConfig.dropCollections(mongoTemplate);
	}
	
	@Test
	public void testGetPlanets() {
		List<Planet> planets = service.getPlanets();
		
		assertEquals(20, planets.size());
	}
	
	@Test
	public void testGetPlanetById() {
		Planet planet = service.getPlanetById(1);
		
		assertNotNull(planet);
		assertEquals("Alderaan", planet.getName());
	    assertEquals("temperate", planet.getClimate());
	    assertEquals("grasslands, mountains", planet.getTerrain());
	}
	
	@Test
	public void testGetPlanetByIdNull() {
		Integer id = null;

        try {
        	service.getPlanetById(id);
            fail("The method getPlanetById must throw an IllegalArgumentException in case the given id is null!");
        } catch (IllegalArgumentException e) {
            // OK
        }
	}
	
	@Test
	public void testGetPlanetByIdAbsent() {
		Integer id = 1000;
        try {
            service.getPlanetById(id);
            fail("The planet with id="+id+" should not exist!");
        } catch (ObjectNotFoundException e) {
            // OK
        }
	}

	@Test
	public void testGetPlanetsByName() {
		List<Planet> planets = service.getPlanetsByName("Bespin");
		
		assertNotNull(planets);
		assertEquals(1, planets.size());
		
		Planet planet = planets.get(0);
		assertEquals(new Integer(5), planet.getId());
		assertEquals("Bespin", planet.getName());
	    assertEquals("temperate", planet.getClimate());
	    assertEquals("gas giant", planet.getTerrain());
	}
	
	@Test
	public void testGetPlanetsByNameAbsent() {
		List<Planet> planets = service.getPlanetsByName("Mars");
		
		assertEquals(0, planets.size());
	}
	
	@Test
	public void testGetPlanetsByNameNull() {
		List<Planet> planets = service.getPlanetsByName(null);
		
		assertEquals(0, planets.size());
	}
	
	@Test
    public void testSaveAndDeletePlanet() {

        Planet planet = new Planet();
        planet.setName("Jupiter");
        planet.setClimate("Frigid");
        planet.setTerrain("Gas");

        Planet planetDb = service.addPlanet(planet);

        assertNotNull(planetDb);

        Integer id = planetDb.getId();
        assertNotNull(id);

        planetDb = service.getPlanetById(id);
        assertNotNull(planetDb);

        assertEquals("Jupiter", planetDb.getName());
        assertEquals("Frigid", planetDb.getClimate());
        assertEquals("Gas", planetDb.getTerrain());

        service.delete(id);

        // Check if it was deleted
        try {
            service.getPlanetById(id);
            fail("The planet (id="+id+") was not deleted!");
        } catch (ObjectNotFoundException e) {
            // OK
        }
    }
	
	@Test
    public void testSavePlanetIdNotNull() {

        Planet planet = new Planet();
        planet.setId(11);
        planet.setName("Jupiter");
        planet.setClimate("Frigid");
        planet.setTerrain("Gases");

        try {
            service.addPlanet(planet);
            fail("The planet with id not null should not be saved! The id must be null!");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }
	
	@Test
    public void testUpdatePlanet() {

        Planet planet = new Planet();
        planet.setName("Jupiter");
        planet.setClimate("Frigid");
        planet.setTerrain("Gases");
        
        Integer id = 1;
        
        Planet planetUpdated = service.update(planet, id);
        
        assertEquals(id, planetUpdated.getId());
        assertEquals("Jupiter", planetUpdated.getName());
        assertEquals("Frigid", planetUpdated.getClimate());
        assertEquals("Gases", planetUpdated.getTerrain());
    }
	
	
	@Test
    public void testUpdatePlanetAbsent() {

        Planet planet = new Planet();
        planet.setName("Jupiter");
        planet.setClimate("Frigid");
        planet.setTerrain("Gases");
        
        Integer id = 1000;
        try {
            service.update(planet, id);
            fail("The update method should not update a planet when there isn't a planet with the given id!");
        } catch (ObjectNotFoundException e) {
            // OK
        }
    }
	
	@Test
    public void testUpdatePlanetIdNull() {

        Planet planet = new Planet();
        planet.setName("Jupiter");
        planet.setClimate("Frigid");
        planet.setTerrain("Gases");
        
        Integer id = null;
        try {
            service.update(planet, id);
            fail("The update method should not update a planet when the parameter id is null!");
        } catch (IllegalArgumentException e) {
            // OK
        }
    }
	
	@Test
	public void testDeletePlanetAbsent() {
		Integer id = 1000;
        try {
        	service.delete(id);
        	fail("The method delete must throw an ObjectNotFoundException because there is no planet with id="+id);
    	} catch (ObjectNotFoundException e) {
            // OK
        }
	}
	
	@Test
	public void testDeletePlanetIdNull() {
		Integer id = null;
        try {
        	service.delete(id);
            fail("The method delete must throw an IllegalArgumentException in case the given id is null!");
        } catch (IllegalArgumentException e) {
            // OK
        }
	}
	
}