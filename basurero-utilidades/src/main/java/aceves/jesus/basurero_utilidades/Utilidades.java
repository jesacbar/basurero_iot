package aceves.jesus.basurero_utilidades;

import aceves.jesus.basurero_entidades.Basurero;
import aceves.jesus.basurero_entidades.Lectura;

public class Utilidades {
	public static double calcularPorcentajeLlenado(Basurero basurero, Lectura lectura) {
		double porcentajeLlenado = ((basurero.getAltura() - lectura.getAltura()) * 100) / basurero.getAltura();
		return porcentajeLlenado;
	}

	public static String calcularEstado(Basurero basurero, Lectura lectura) {
		double porcentajeLlenado = calcularPorcentajeLlenado(basurero, lectura);

		if (porcentajeLlenado > 90) {
			return "LLENO";
		} else if (porcentajeLlenado <= 90 && porcentajeLlenado >= 60) {
			return "CASILLENO";
		} else if (porcentajeLlenado < 60 && porcentajeLlenado > 40) {
			return "MEDIO";
		} else if (porcentajeLlenado <= 40 && porcentajeLlenado >= 10) {
			return "CASIVACIO";
		} else if (porcentajeLlenado < 10) {
			return "VACIO";
		} else {
			return "ERROR";
		}
	}
}
