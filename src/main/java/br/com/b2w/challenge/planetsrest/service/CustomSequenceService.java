package br.com.b2w.challenge.planetsrest.service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import br.com.b2w.challenge.planetsrest.model.CustomSequence;

@Service
public class CustomSequenceService {
	
	@Autowired
	private MongoOperations mongoOp;

	public int getNextSequence(Object object) {
		CustomSequence counter = mongoOp.findAndModify(
				query(where("_id").is(object.getClass().getName())),
				new Update().inc("seq", 1),
				options().returnNew(true).upsert(true),
				CustomSequence.class);
		
		return counter.getSeq();
	}
	
}