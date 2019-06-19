package br.com.b2w.challenge.planetsrest.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import br.com.b2w.challenge.planetsrest.controller.exception.ObjectNotFoundException;
import br.com.b2w.challenge.planetsrest.model.Planet;
import br.com.b2w.challenge.planetsrest.model.PlanetRepository;

@Service
public class PlanetService {

	@Autowired
	private PlanetRepository repository;
	@Autowired
	private CustomSequenceService sequenceService;
	@Autowired
	private SwapiService swapiService;
	
	public List<Planet> getPlanets() {
		List<Planet> planets = repository.findAll();
		
		for (Planet planet : planets) {
			planet.setNumberAppearancesInFilms(swapiService.getNumberAppearancesInFilms(planet.getName()));
		}
		
		return planets;
	}
	
	public Planet getPlanetById(Integer id) {
		Optional<Planet> planet = repository.findById(id);
		
		if(planet.isPresent()) {
			Planet p = planet.get();
			p.setNumberAppearancesInFilms(swapiService.getNumberAppearancesInFilms(p.getName()));
			
			return p;
		} else {
			throw new ObjectNotFoundException("Planet not found"); 
		}
	}
	
	public List<Planet> getPlanetsByName(String name) {
		List<Planet> planets = repository.findByName(name);
		
		for (Planet planet : planets) {
			planet.setNumberAppearancesInFilms(swapiService.getNumberAppearancesInFilms(planet.getName()));
		}
		
		return planets;
	}
	
	public Planet addPlanet(Planet planet) {
		Assert.isNull(planet.getId(), "The id must be null. It was not possible to add the planet.");
		planet.setId(sequenceService.getNextSequence(planet));
		
		return repository.save(planet);
	}
	
	public Planet update(Planet planet, Integer id) {
		Assert.notNull(id, "The id must not be null. It was not possible to update the planet.");
		
		Optional<Planet> optional = repository.findById(id);
		
		if(optional.isPresent()) {
			Planet planetDb = optional.get();
			planetDb.setName(planet.getName());
			planetDb.setClimate(planet.getClimate());
			planetDb.setTerrain(planet.getTerrain());
			
			planetDb = repository.save(planetDb);
			planetDb.setNumberAppearancesInFilms(swapiService.getNumberAppearancesInFilms(planetDb.getName()));			
			
			return planetDb;
		} else {
			throw new ObjectNotFoundException("Planet not found");
		}
	}
	
	public void delete(Integer id) {
		repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Planet not found"));
		repository.deleteById(id);
	}
	
}