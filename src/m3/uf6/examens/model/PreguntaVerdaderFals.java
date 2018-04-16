package m3.uf6.examens.model;

import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;

@Entity
public class PreguntaVerdaderFals extends Pregunta {
	public PreguntaVerdaderFals() { }  /* XML */

	public PreguntaVerdaderFals(String text, double puntuacio) throws Excepcio {
		super(text, puntuacio);
	}

	public boolean esVerdaderFals() { return true; }

	@Override
	public String getEnunciatPregunta(int num) {
		return super.crearEnunciatPregunta(num, System.lineSeparator()+
												StringUtils.leftPad(Examen.CHECK_SQUARE+"  Cert      "+Examen.CHECK_SQUARE+"  Fals", Examen.AMPLE_ENUNCIAT+2)+
												StringUtils.repeat(System.lineSeparator(), 2));
	}

	@Override
	public String getEnunciatPreguntaHtml(int num) {
		return super.crearEnunciatPreguntaHtml(num, "<ul class='truefalse-response'>"+
													"<li>Cert</li><li>Fals</li></ul>");
	}
}
