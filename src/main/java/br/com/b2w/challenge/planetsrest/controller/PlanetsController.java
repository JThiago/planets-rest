package br.com.b2w.challenge.planetsrest.controller;

import java.net.URI;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.b2w.challenge.planetsrest.model.Planet;
import br.com.b2w.challenge.planetsrest.service.PlanetService;

@RestController
@RequestMapping("/api/v1/planets")
public class PlanetsController {

	@Autowired
	private PlanetService service;
	
	@PostMapping
	public ResponseEntity<?> addPlanet(@Valid @RequestBody Planet planet) {
		Planet planetSaved = service.addPlanet(planet);
		URI location = getUri(planetSaved.getId());
		
		return ResponseEntity.created(location).build();
	}
	
	@GetMapping
	public ResponseEntity<?> getAllPlanets() {
		List<Planet> planets = service.getPlanets();
		
		return ResponseEntity.ok(planets);
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<?> getPlanetsByName(@PathVariable("name") String name) {
		List<Planet> planets = service.getPlanetsByName(name);
		
		return planets.isEmpty() ? 
				ResponseEntity.noContent().build() : 
				ResponseEntity.ok(planets);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<?> getPlanetById(@PathVariable("id") Integer id) {
		Planet planet = service.getPlanetById(id);
		
		return ResponseEntity.ok(planet);
	}	
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deletePlanet(@PathVariable("id") Integer id) {
		service.delete(id);
		
		return ResponseEntity.ok().build();
	}	
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updatePlanet(@PathVariable("id") Integer id, @Valid @RequestBody Planet planet) {
		Planet planetUpdated = service.update(planet, id);
		
		return ResponseEntity.ok(planetUpdated); 
	}
	
	private URI getUri(Integer id) {
		return ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
	}
	
}