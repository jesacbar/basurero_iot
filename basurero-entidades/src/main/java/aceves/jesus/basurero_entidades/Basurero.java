package aceves.jesus.basurero_entidades;

/**
 * Basurero.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 * Representa un basurero que tiene un sensor que mide que tan lleno está. 
 */

public class Basurero {
	private int id;
	private double altura;
	private double volumen;
	
	public Basurero(int id, double altura, double volumen) {
		super();
		this.id = id;
		this.altura = altura;
		this.volumen = volumen;
	}

	public int getId() {
		return id;
	}

	public void setIdBasurero(int idBasurero) {
		this.id = idBasurero;
	}

	public double getAltura() {
		return altura;
	}

	public void setAltura(double altura) {
		this.altura = altura;
	}

	public double getVolumen() {
		return volumen;
	}

	public void setVolumen(double volumen) {
		this.volumen = volumen;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(altura);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + id;
		temp = Double.doubleToLongBits(volumen);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Basurero other = (Basurero) obj;
		if (Double.doubleToLongBits(altura) != Double.doubleToLongBits(other.altura))
			return false;
		if (id != other.id)
			return false;
		if (Double.doubleToLongBits(volumen) != Double.doubleToLongBits(other.volumen))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Basurero [idBasurero=" + id + ", altura=" + altura + ", volumen=" + volumen + "]";
	}
	
}
