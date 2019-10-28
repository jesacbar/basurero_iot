package aceves.jesus.avanceiot;

import java.util.Date;

/**
 * Basurero.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 * Representa un basurero que tiene un sensor que mide que tan lleno está. 
 */

public class Basurero {
	private int idBasurero;
	private Date fechahora;
	private double alturaMax;
	private String estadoLlenado;
	private String estadoCarga;
	
	public Basurero(int idBasurero, Date fechahora, double alturaMax, String estadoLlenado, String estadoCarga) {
		super();
		this.idBasurero = idBasurero;
		this.fechahora = fechahora;
		this.alturaMax = alturaMax;
		this.estadoLlenado = estadoLlenado;
		this.estadoCarga = estadoCarga;
	}

	public String getEstadoLlenado() {
		return estadoLlenado;
	}

	public void setEstadoLlenado(String estadoLlenado) {
		this.estadoLlenado = estadoLlenado;
	}

	public String getEstadoCarga() {
		return estadoCarga;
	}

	public void setEstadoCarga(String estadoCarga) {
		this.estadoCarga = estadoCarga;
	}

	public Date getFechahora() {
		return fechahora;
	}

	public void setFechahora(Date fechahora) {
		this.fechahora = fechahora;
	}

	public int getIdBasurero() {
		return idBasurero;
	}

	public void setIdBasurero(int idBasurero) {
		this.idBasurero = idBasurero;
	}

	public double getAlturaMax() {
		return alturaMax;
	}

	public void setAlturaMax(double alturaMax) {
		this.alturaMax = alturaMax;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(alturaMax);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((estadoCarga == null) ? 0 : estadoCarga.hashCode());
		result = prime * result + ((estadoLlenado == null) ? 0 : estadoLlenado.hashCode());
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
		Basurero other = (Basurero) obj;
		if (Double.doubleToLongBits(alturaMax) != Double.doubleToLongBits(other.alturaMax))
			return false;
		if (estadoCarga == null) {
			if (other.estadoCarga != null)
				return false;
		} else if (!estadoCarga.equals(other.estadoCarga))
			return false;
		if (estadoLlenado == null) {
			if (other.estadoLlenado != null)
				return false;
		} else if (!estadoLlenado.equals(other.estadoLlenado))
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
		return "Basurero [idBasurero=" + idBasurero + ", fechahora=" + fechahora + ", alturaMax=" + alturaMax
				+ ", estadoLlenado=" + estadoLlenado + ", estadoCarga=" + estadoCarga + "]";
	}
	
}
