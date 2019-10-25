package aceves.jesus.avanceiot;

import java.util.Date;

/**
 * Basurero.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 */

public class Basurero {
	private int idBasurero;
	private Date fechahora;
	private double alturaMax;
	
	public Basurero(int idBasurero, Date fechahora, double alturaMax) {
		super();
		this.idBasurero = idBasurero;
		this.fechahora = fechahora;
		this.alturaMax = alturaMax;
	}

	public int getIdBasurero() {
		return idBasurero;
	}

	public void setIdBasurero(int idBasurero) {
		this.idBasurero = idBasurero;
	}

	public Date getFechahora() {
		return fechahora;
	}

	public void setFechahora(Date fechahora) {
		this.fechahora = fechahora;
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
		return "Basurero [idBasurero=" + idBasurero + ", fechahora=" + fechahora + ", alturaMax=" + alturaMax + "]";
	}
	
}
