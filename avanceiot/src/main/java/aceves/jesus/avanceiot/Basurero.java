package aceves.jesus.avanceiot;

import java.util.Date;

/**
 * Basurero.java
 * Autores: Karla Castro, María Germán, Jesús Aceves
 */

public class Basurero {
	private int idBasurero;
	private double alturaMax;
	
	public Basurero(int idBasurero, double alturaMax) {
		super();
		this.idBasurero = idBasurero;
		this.alturaMax = alturaMax;
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
		if (idBasurero != other.idBasurero)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Basurero [idBasurero=" + idBasurero + ", alturaMax=" + alturaMax + "]";
	}
	
}
