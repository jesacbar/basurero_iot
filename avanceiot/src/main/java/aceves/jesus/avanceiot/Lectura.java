package aceves.jesus.avanceiot;

import java.util.Date;

/**
 * Lectura.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 */

public class Lectura {
	private int idSensor;
	private Date fechahora;
	private int carga;
	private double altura;
	
	public Lectura() {
		this.idSensor = -1;
		this.fechahora = null;
		this.carga = -1;
		this.altura = -1;
	}
	
	public Lectura(int idSensor, Date fechahora, int carga, double altura) {
		this.idSensor = idSensor;
		this.fechahora = fechahora;
		this.carga = carga;
		this.altura = altura;
	}

	public int getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(int idSensor) {
		this.idSensor = idSensor;
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
		result = prime * result + idSensor;
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
		if (idSensor != other.idSensor)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Lectura [idSensor=" + idSensor + ", fechahora=" + fechahora + ", carga=" + carga + ", altura=" + altura
				+ "]";
	}
	
}
