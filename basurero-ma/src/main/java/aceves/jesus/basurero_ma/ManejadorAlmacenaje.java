package aceves.jesus.basurero_ma;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import aceves.jesus.basurero_entidades.Basurero;
import aceves.jesus.basurero_entidades.CambioLlenado;
import aceves.jesus.basurero_entidades.Lectura;
import aceves.jesus.basurero_utilidades.Utilidades;

import static com.mongodb.client.model.Filters.eq;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * ManejadorAlmacenaje.java Autores: Karla Castro, María Germán, Jesús Aceves
 * Contiene métodos del almacenaje de la base de datos no relacional de los
 * basureros IoT.
 */

public class ManejadorAlmacenaje {
	MongoClient mongo;
	MongoDatabase database;

	/**
	 * Constructor por defecto de la clase.
	 */
	public ManejadorAlmacenaje() {
		mongo = new MongoClient("localhost", 27017);
		database = mongo.getDatabase("basurero-bd");
	}

	/**
	 * Inserta la lectura que se le pasa como parámetro a la colección "lecturas".
	 * 
	 * @param lectura Lectura que se quiere insertar en la colección.
	 */
	public void insertarLectura(Lectura lectura) {
		MongoCollection<Document> collection = database.getCollection("lecturas");

		Document document = new Document("idBasurero", lectura.getIdBasurero())
				.append("fechahora", lectura.getFechahora())
				.append("altura", lectura.getAltura());

		collection.insertOne(document);

		System.out.println("MANEJADOR ALMACENAJE: Inserción de lectura exitosa.");
		
		Basurero basurero = obtenerBasurero(lectura.getIdBasurero());
		CambioLlenado cambioLlenadoActual = obtenerUltimoCambioLlenado(basurero);
		double porcentajeLlenadoNuevo = Utilidades.calcularPorcentajeLlenado(basurero, lectura);
		
		// Que hacer si no se ha ingresado un cambio de llenado antes.
		if (cambioLlenadoActual == null) {
			System.out.println("PRIMERA INSERCIÓN DE CAMBIO DE LLENADO Y ESTADO");
			System.out.println(basurero);
			CambioLlenado cambioLlenadoNuevo = new CambioLlenado(
					lectura.getIdBasurero(),
					lectura.getFechahora(), 
					0,
					porcentajeLlenadoNuevo,
					0,
					(porcentajeLlenadoNuevo / 100) * basurero.getVolumen(),
					Utilidades.calcularEstado(basurero, lectura)
					);
			insertarCambioLlenado(cambioLlenadoNuevo);
			insertarCambioEstado(cambioLlenadoNuevo);
		}
		// Ver si ha ocurrido un cambio de porcentaje de llenado
		else if (cambioLlenadoActual != null && porcentajeLlenadoNuevo != cambioLlenadoActual.getLlenadoActual()) {
			System.out.println("SE DETECTÓ CAMBIO DE LLENADO");
			CambioLlenado cambioLlenadoNuevo = new CambioLlenado(
					lectura.getIdBasurero(),
					lectura.getFechahora(), 
					cambioLlenadoActual.getLlenadoActual(),
					porcentajeLlenadoNuevo,
					cambioLlenadoActual.getVolumenActual(),
					(porcentajeLlenadoNuevo / 100) * basurero.getVolumen(),
					Utilidades.calcularEstado(basurero, lectura)
					);
			insertarCambioLlenado(cambioLlenadoNuevo);
			// Ver si ha ocurrido un cambio de estados
			if (cambioLlenadoNuevo.getEstado() != cambioLlenadoActual.getEstado()) {
				System.out.println("SE DETECTÓ CAMBIO DE ESTADO");
				insertarCambioEstado(cambioLlenadoNuevo);
			}
		}
	}
	
	/**
	 * Inserta el cambio de llenado que se le pase como parámetro a la base de datos.
	 * @param cambioLlenado Cambio de llenado que se quiere registrar.
	 */
	public void insertarCambioLlenado(CambioLlenado cambioLlenado) {
		MongoCollection<Document> collection = database.getCollection("cambiosLlenado");

		Document document = new Document("idBasurero", cambioLlenado.getIdBasurero())
				.append("fechahora", cambioLlenado.getFechahora())
				.append("llenadoAnterior", cambioLlenado.getLlenadoAnterior())
				.append("llenadoActual", cambioLlenado.getLlenadoActual())
				.append("volumenAnterior", cambioLlenado.getVolumenAnterior())
				.append("volumenActual", cambioLlenado.getVolumenActual())
				.append("estado", cambioLlenado.getEstado());

		collection.insertOne(document);

		System.out.println("MANEJADOR ALMACENAJE: Inserción de cambio de llenado exitosa.");
	}
	
	/**
	 * Inserta el cambio de estado que se le pase como parámetro a la base de datos.
	 * @param cambioEstado Cambio de estado que se quiere registrar.
	 */
	public void insertarCambioEstado(CambioLlenado cambioEstado) {
		MongoCollection<Document> collection = database.getCollection("cambiosEstado");

		Document document = new Document("idBasurero", cambioEstado.getIdBasurero())
				.append("fechahora", cambioEstado.getFechahora())
				.append("llenadoAnterior", cambioEstado.getLlenadoAnterior())
				.append("llenadoActual", cambioEstado.getLlenadoActual())
				.append("volumenAnterior", cambioEstado.getVolumenAnterior())
				.append("volumenActual", cambioEstado.getVolumenActual())
				.append("estado", cambioEstado.getEstado());

		collection.insertOne(document);

		System.out.println("MANEJADOR ALMACENAJE: Inserción de cambio de estado exitosa.");
	}

	/**
	 * Inserta el basurero que se pasa como parámetro a la colección "basureros".
	 * 
	 * @param basurero Basurero que se quiere insertar en la colección.
	 */
	public void insertarBasurero(Basurero basurero) {
		MongoCollection<Document> collection = database.getCollection("basureros");

		Document document = new Document("id", basurero.getId())
				.append("altura", basurero.getAltura())
				.append("volumen", basurero.getVolumen());

		collection.insertOne(document);

		System.out.println("MANEJADOR ALMACENAJE: Inserción de basurero exitosa.");
		
		// Se agregan cambios de llenado y estado iniciales
		CambioLlenado cambioLlenadoNuevo = new CambioLlenado(
				basurero.getId(),
				Date.from(Instant.now()),
				0.0,
				0.0,
				0.0,
				0.0,
				"VACIO"
				);
		insertarCambioLlenado(cambioLlenadoNuevo);
		insertarCambioEstado(cambioLlenadoNuevo);
	}

	/**
	 * Actualiza el basurero que se pasa como parámetro en la colección "basureros".
	 * 
	 * @param basurero Basurero que se quiere actualizar en la colección.
	 */
	public void actualizarBasurero(Basurero basurero) {
		MongoCollection<Document> collection = database.getCollection("basureros");

		Document query = new Document();
		query.append("id", basurero.getId());

		Document update = new Document("id", basurero.getId())
				.append("altura", basurero.getAltura())
				.append("volumen", basurero.getVolumen());

		Document updateOp = new Document("$set", update);

		collection.updateOne(query, updateOp);
	}

	/**
	 * Obtiene el basurero que tiene la id de basurero que se pase como parámetro.
	 * 
	 * @param id Id del basurero que se quiere obtener.
	 * @return Basurero cuya id coincide con la que se pasó como parámetro. Regresa
	 *         null si no se encontró.
	 */
	public Basurero obtenerBasurero(int id) {
		MongoCollection<Document> collection = database.getCollection("basureros");
		Document document = collection.find(eq("id", id)).first();

		if (document == null) {
			return null;
		} else {
			Double altura = document.getDouble("altura");
			Double volumen = document.getDouble("volumen");
			Basurero basurero = new Basurero(id, altura, volumen);
			return basurero;
		}
	}

	/**
	 * Obtiene todos los basureros registrados en la colleción de basureros y los
	 * regresa en un ArrayList.
	 * 
	 * @return ArrayList con todos los basureros registrados en la colección de
	 *         basureros.
	 */
	public ArrayList<Basurero> obtenerBasureros() {
		ArrayList<Basurero> basureros = new ArrayList<Basurero>();

		MongoCollection<Document> collection = database.getCollection("basureros");
		MongoCursor<Document> cursor = collection.find().iterator();

		try {
			while (cursor.hasNext()) {
				Document document = cursor.next();
				int id = document.getInteger("id");
				Double altura = document.getDouble("altura");
				Double volumen = document.getDouble("volumen");
				Basurero basurero = new Basurero(id, altura, volumen);
				basureros.add(basurero);
			}
		} finally {
			cursor.close();
		}

		return basureros;
	}

	/**
	 * Regresa la última lectura registrada del basurero que se le pase como
	 * parámetro.
	 * 
	 * @param basurero Basurero del que se quiere obtener la última lectura
	 *                 registrada.
	 * @return Última lectura registrada del basurero pasado como parámetro.
	 */
	public Lectura obtenerUltimaLectura(Basurero basurero) {
		Lectura lectura = null;

		MongoCollection<Document> collection = database.getCollection("lecturas");
		Document document = collection.find(eq("idBasurero", basurero.getId())).sort(new Document("fechahora", -1))
				.first();

		if (document != null) {
			int idBasurero = document.getInteger("idBasurero");
			Date fechahora = document.getDate("fechahora");
			double altura = document.getDouble("altura");

			lectura = new Lectura(idBasurero, fechahora, altura);
		}

		return lectura;
	}
	
	/**
	 * Regresa el último cambio de llenado registrado en la base de datos 
	 * del basurero que se le pase como parámetro
	 * @param basurero Basurero del que se quiere obtener el último cambio de llenado.
	 * @return Último cambio de llenado del basurero que se le pase como parámetro.
	 */
	public CambioLlenado obtenerUltimoCambioLlenado(Basurero basurero) {
		CambioLlenado cambioLlenado = null;
		
		MongoCollection<Document> collection = database.getCollection("cambiosLlenado");
		Document document = collection.find(eq("idBasurero", basurero.getId())).sort(new Document("fechahora", -1))
				.first();
		
		if (document != null) {
			int idBasurero = document.getInteger("idBasurero");
			Date fechahora = document.getDate("fechahora");
			double llenadoAnterior = document.getDouble("llenadoAnterior");
			double llenadoActual = document.getDouble("llenadoActual");
			double volumenAnterior = document.getDouble("volumenAnterior");
			double volumenActual = document.getDouble("volumenActual");
			String estado = document.getString("estado");
			
			cambioLlenado = new CambioLlenado(idBasurero, fechahora, llenadoAnterior, llenadoActual, 
					volumenAnterior, volumenActual, estado);
		}
		
		return cambioLlenado;
	}
	
	/**
	 * Regrea el último cambio de estado del basurero que se le pase como parámetro
	 * que esté registrado en la base de datos.
	 * @param basurero Basurero del que se quiere obtener el último cambio de estado.
	 * @return Último cambio de estado registrado del basurero que se le pase como parámetro.
	 */
	public CambioLlenado obtenerUltimoCambioEstado(Basurero basurero) {
		CambioLlenado cambioEstado = null;
		
		MongoCollection<Document> collection = database.getCollection("cambiosEstado");
		Document document = collection.find(eq("idBasurero", basurero.getId())).sort(new Document("fechahora", -1))
				.first();
		
		if (document != null) {
			int idBasurero = document.getInteger("idBasurero");
			Date fechahora = document.getDate("fechahora");
			double llenadoAnterior = document.getDouble("llenadoAnterior");
			double llenadoActual = document.getDouble("llenadoActual");
			double volumenAnterior = document.getDouble("volumenAnterior");
			double volumenActual = document.getDouble("volumenActual");
			String estado = document.getString("estado");
			
			cambioEstado = new CambioLlenado(idBasurero, fechahora, llenadoAnterior, llenadoActual, 
					volumenAnterior, volumenActual, estado);
		}
		
		return cambioEstado;
	}

	/**
	 * Regresa un HashMap con las útlimas lecturas registradas de cada bote de basura.
	 * @return HashMap con los botes de basura como llaves, y sus últimas lecturas
	 * como valor.
	 */
	public HashMap<Basurero, Lectura> obtenerUltimasLecturas() {
		HashMap<Basurero, Lectura> ultimasLecturas = new HashMap<Basurero, Lectura>();
		ArrayList<Basurero> basureros = obtenerBasureros();
		
		for (Basurero basurero : basureros) {
			Lectura ultimaLectura = obtenerUltimaLectura(basurero);
			if (ultimaLectura != null) {
				ultimasLecturas.put(basurero, ultimaLectura);
			}
		}
		
		return ultimasLecturas;
	}
}
