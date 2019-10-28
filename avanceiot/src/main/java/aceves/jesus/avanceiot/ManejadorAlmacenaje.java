package aceves.jesus.avanceiot;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.Date;

/**
 * ManejadorAlmacenaje.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 * Contiene métodos del almacenaje de la base de datos no relacional de los basureros IoT.
 */

public class ManejadorAlmacenaje {
	MongoClient mongo;
	MongoDatabase database;
	
	/**
	 * Constructor por defecto de la clase.
	 */
	public ManejadorAlmacenaje() {
		mongo = new MongoClient("localhost", 27017);
		database = mongo.getDatabase("basureros_iot");
	}
	
	/**
	 * Inserta la lectura que se le pasa como parámetro a la colección "lecturas".
	 * @param lectura Lectura que se quiere insertar en la colección.
	 */
	public void insertarLectura(Lectura lectura) {
		MongoCollection<Document> collection = database.getCollection("lecturas");
		
		Document document = new Document("id_sensor", lectura.getIdBasurero())
				.append("fechahora", lectura.getFechahora())
				.append("carga", lectura.getCarga())
				.append("altura", lectura.getAltura());
		
		collection.insertOne(document);
		
		System.out.println("MANEJADOR ALMACENAJE: Inserción de lectura exitosa.");
	}
	
	/**
	 * Inserta el basurero que se pasa como parámetro a la colección "basureros".
	 * @param basurero Basurero que se quiere insertar en la colección.
	 */
	public void insertarBasurero(Basurero basurero) {
		MongoCollection<Document> collection = database.getCollection("basureros");
		
		Document document = new Document("id_basurero", basurero.getIdBasurero())
				.append("fechahora",  basurero.getFechahora())
				.append("altura_max", basurero.getAlturaMax())
				.append("estado_llenado", basurero.getEstadoLlenado())
				.append("estado_carga", basurero.getEstadoCarga());
		
		collection.insertOne(document);
		
		System.out.println("MANEJADOR ALMACENAJE: Inserción de basurero exitosa.");
	}
	
	/**
	 * Actualiza el basurero que se pasa como parámetro en la colección "basureros".
	 * @param basurero Basurero que se quiere actualizar en la colección.
	 */
	public void actualizarBasurero(Basurero basurero) {
		MongoCollection<Document> collection = database.getCollection("basureros");
		
		Document query = new Document();
        query.append("id_basurero", basurero.getIdBasurero());
		
		Document update = new Document("id_basurero", basurero.getIdBasurero())
				.append("fechahora",  basurero.getFechahora())
				.append("altura_max", basurero.getAlturaMax())
				.append("estado_llenado", basurero.getEstadoLlenado())
				.append("estado_carga", basurero.getEstadoCarga());
        
		Document updateOp = new Document("$set", update);
		
		collection.updateOne(query, updateOp);
	}
	
	/**
	 * Obtiene el basurero que tiene la id de basurero que se pase como parámetro.
	 * @param id Id del basurero que se quiere obtener.
	 * @return Basurero cuya id coincide con la que se pasó como parámetro. Regresa null si no se encontró.
	 */
	public Basurero obtenerBasurero(int id) {
		MongoCollection<Document> collection = database.getCollection("basureros");
		Document document = collection.find(eq("id_basurero", id)).first();
		
		if (document == null) {
			return null;
		} else {
			int idBasurero = document.getInteger("id_basurero");
			Date fechahora = document.getDate("fechahora");
			Double alturaMax = document.getDouble("altura_max");
			String estadoLlenado = document.getString("estado_llenado");
			String estadoCarga = document.getString("estado_carga");
			Basurero basurero = new Basurero(idBasurero, fechahora, alturaMax, estadoLlenado, estadoCarga);
			return basurero;
		}
	}
	
}
