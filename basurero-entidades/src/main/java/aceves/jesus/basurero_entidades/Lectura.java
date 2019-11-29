package aceves.jesus.basurero_entidades;

import java.util.Date;

/**
 * Lectura.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 * Representa una lectura hecha por un sensor de altura dentro de un basurero para medir que tan lleno está.
 */

public class Lectura {
	private int idBasurero;
	private Date fechahora;
	private int carga;
	private double altura;
	
	public Lectura() {
		this.idBasurero = -1;
		this.fechahora = null;
		this.carga = -1;
		this.altura = -1;
	}
	
	public Lectura(int idBasurero, Date fechahora, int carga, double altura) {
		this.idBasurero = idBasurero;
		this.fechahora = fechahora;
		this.carga = carga;
		this.altura = altura;
	}

	public int getIdBasurero() {
		return idBasurero;
	}

	public void setIdSensor(int idBasurero) {
		this.idBasurero = idBasurero;
	}

	public Date getFechahora() {
		return fechahora;
	}

	public void setFechahora(Date fechahora) {
		this.fechahora = fechahora;
	}

	public int getCarga() {
		return carga;
	}

	public void setCarga(int carga) {
		this.carga = carga;
	}

	public double getAltura() {
		return altura;
	}

	public void setAltura(double altura) {
		this.altura = altura;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(altura);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + carga;
		result = prime * result + ((fechahora == null) ? 0 : fechahora.hashCode());
		result = prime * result + idBasurero;
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
		Lectura other = (Lectura) obj;
		if (Double.doubleToLongBits(altura) != Double.doubleToLongBits(other.altura))
			return false;
		if (carga != other.carga)
			return false;
		if (fechahora == null) {
			if (other.fechahora != null)
				return false;
		} else if (!fechahora.equals(other.fechahora))
			return false;
		if (idBasurero != other.idBasurero)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Lectura [idSensor=" + idBasurero + ", fechahora=" + fechahora + ", carga=" + carga + ", altura=" + altura
				+ "]";
	}
	
}
