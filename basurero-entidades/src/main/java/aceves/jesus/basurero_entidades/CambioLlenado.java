package aceves.jesus.basurero_entidades;

import java.util.Date;

public class CambioLlenado {
	
	private int idBasurero;
	private Date fechahora;
	private double llenadoAnterior;
	private double llenadoActual;
	private double volumenAnterior;
	private double volumenActual;
	private String estado;
	
	public CambioLlenado(int idBasurero, Date fechahora, double llenadoAnterior, double llenadoActual,
			double volumenAnterior, double volumenActual, String estado) {
		super();
		this.idBasurero = idBasurero;
		this.fechahora = fechahora;
		this.llenadoAnterior = llenadoAnterior;
		this.llenadoActual = llenadoActual;
		this.volumenAnterior = volumenAnterior;
		this.volumenActual = volumenActual;
		this.estado = estado;
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

	public double getLlenadoAnterior() {
		return llenadoAnterior;
	}

	public void setLlenadoAnterior(double llenadoAnterior) {
		this.llenadoAnterior = llenadoAnterior;
	}

	public double getLlenadoActual() {
		return llenadoActual;
	}

	public void setLlenadoActual(double llenadoActual) {
		this.llenadoActual = llenadoActual;
	}

	public double getVolumenAnterior() {
		return volumenAnterior;
	}

	public void setVolumenAnterior(double volumenAnterior) {
		this.volumenAnterior = volumenAnterior;
	}

	public double getVolumenActual() {
		return volumenActual;
	}

	public void setVolumenActual(double volumenActual) {
		this.volumenActual = volumenActual;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((estado == null) ? 0 : estado.hashCode());
		result = prime * result + ((fechahora == null) ? 0 : fechahora.hashCode());
		result = prime * result + idBasurero;
		long temp;
		temp = Double.doubleToLongBits(llenadoActual);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(llenadoAnterior);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(volumenActual);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(volumenAnterior);
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
		CambioLlenado other = (CambioLlenado) obj;
		if (estado == null) {
			if (other.estado != null)
				return false;
		} else if (!estado.equals(other.estado))
			return false;
		if (fechahora == null) {
			if (other.fechahora != null)
				return false;
		} else if (!fechahora.equals(other.fechahora))
			return false;
		if (idBasurero != other.idBasurero)
			return false;
		if (Double.doubleToLongBits(llenadoActual) != Double.doubleToLongBits(other.llenadoActual))
			return false;
		if (Double.doubleToLongBits(llenadoAnterior) != Double.doubleToLongBits(other.llenadoAnterior))
			return false;
		if (Double.doubleToLongBits(volumenActual) != Double.doubleToLongBits(other.volumenActual))
			return false;
		if (Double.doubleToLongBits(volumenAnterior) != Double.doubleToLongBits(other.volumenAnterior))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CambioLlenado [idBasurero=" + idBasurero + ", fechahora=" + fechahora + ", llenadoAnterior="
				+ llenadoAnterior + ", llenadoActual=" + llenadoActual + ", volumenAnterior=" + volumenAnterior
				+ ", volumenActual=" + volumenActual + ", estado=" + estado + "]";
	}
	
}
