package aceves.jesus.avanceiot;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

/**
 * ManejadorAlmacenaje.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 */

public class ManejadorAlmacenaje {
	MongoClient mongo;
	MongoDatabase database;
	
	public ManejadorAlmacenaje() {
		mongo = new MongoClient("localhost", 27017);
		database = mongo.getDatabase("basureroiot");
	}
	
	public void insertarLectura(Lectura lectura) {
		MongoCollection<Document> collection = database.getCollection("lecturas");
		
		System.out.println("Se intenta insertar lectura en BD");
		
		Document document = new Document("id_sensor", lectura.getIdSensor())
				.append("fechahora", lectura.getFechahora())
				.append("carga", lectura.getCarga())
				.append("altura", lectura.getAltura());
		
		collection.insertOne(document);
		
		System.out.println("Inserción exitosa");
	}
	
	public void insertarBasurero(Basurero basurero) {
		MongoCollection<Document> collection = database.getCollection("basureros");
		
		System.out.println("Se intenta insertar basurero en BD");
		
		Document document = new Document("id_sensor", basurero.getIdBasurero())
				.append("fechahora", basurero.getFechahora())
				.append("altura_max", basurero.getAlturaMax());
		
		collection.insertOne(document);
		
		System.out.println("Inserción exitosa");
	}
	
	public Basurero obtenerBasurero(int id) {
		MongoCollection<Document> collection = database.getCollection("basureros");
		return null;
	}
	
}
