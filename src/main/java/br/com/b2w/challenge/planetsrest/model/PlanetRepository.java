package br.com.b2w.challenge.planetsrest.model;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlanetRepository extends MongoRepository<Planet, Integer> {

	List<Planet> findByName(String name);
	
}