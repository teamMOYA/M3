package m3.uf6.examens.model;

import java.util.Arrays;
import java.util.HashSet;

import javax.persistence.Basic;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

@Entity
public class PreguntaOpcions extends Pregunta {
	@Basic(optional=false) private HashSet<String> opcions;

	public PreguntaOpcions() {	/* XML */
		this.opcions = new HashSet<String>();
	}

	public PreguntaOpcions(String text, double puntuacio, String[] opcions) throws Excepcio {
		super(text, puntuacio);
		this.opcions = new HashSet<String>(Arrays.asList(opcions));
	}

	public String[] getOpcions() {
		return this.opcions.toArray(new String[]{});
	}

	public int getTotalOpcions() {
		return this.opcions.size();
	}

	public void setOpcions(String[] opcions) {
		this.opcions.clear();
		this.opcions.addAll(Arrays.asList(opcions));
	}

	public boolean esOpcions() { return true; }

	@Override
	public String getEnunciatPregunta(int num) {
		String resposta = System.lineSeparator();
		for (String opcio : opcions) resposta += StringUtils.rightPad("  "+Examen.CHECK_SQUARE+"  "+opcio, Examen.AMPLE_ENUNCIAT+2, ".")+System.lineSeparator();
		//resposta += System.lineSeparator();
		return super.crearEnunciatPregunta(num, resposta);
	}

	@Override
	public String getEnunciatPreguntaHtml(int num) {
		String resposta = "<ul class='options-response'>";
		for (String opcio : opcions) resposta += "<li>"+opcio+"</li>";
		resposta += "</ul>";
		return super.crearEnunciatPreguntaHtml(num, resposta);
	}
}
