package m3.uf6.examens.model;

import java.text.DecimalFormat;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import org.apache.commons.lang3.text.WordUtils;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

@Entity
@NamedQueries({
	@NamedQuery(name="Pregunta.findAll",
			query="SELECT p FROM Pregunta p"),
	@NamedQuery(name="Examen.findByText",
			query="SELECT p FROM Pregunta p WHERE p.text LIKE '%:term%'"),
})
public abstract class Pregunta {
	@Basic(optional=false) protected String text;
	protected double puntuacio;

	public Pregunta() { }  /* XML */

	public Pregunta(String text, double puntuacio) throws Excepcio {
		if ("".equals(text.trim())) throw new Excepcio("Pregunta", "Cal indicar un text per la pregunta");
		this.text = text;
		if (puntuacio<=0) throw new Excepcio("Pregunta", "La puntuació de la pregunta ha de ser major que 0");
		this.puntuacio = puntuacio;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) throws Excepcio {
		if ("".equals(text.trim())) throw new Excepcio("Pregunta", "Cal indicar un text per la pregunta");
		this.text = text;
	}

	public double getPuntuacio() {
		return puntuacio;
	}

	public void setPuntuacio(double puntuacio) throws Excepcio {
		if (puntuacio<=0) throw new Excepcio("Pregunta", "La puntuació de la pregunta ha de ser major que 0");
		this.puntuacio = puntuacio;
	}

	// Properties
	public SimpleStringProperty getTextProperty() {
		return new SimpleStringProperty(text);
	}
	public SimpleDoubleProperty getPuntuacioProperty() {
		return new SimpleDoubleProperty(puntuacio);
	}

	public SimpleStringProperty getTotalOpcionsProperty() {
		return new SimpleStringProperty(this.esOpcions()?this.getTotalOpcions()+"":"");
	};

	public boolean esOpcions() { return false; }

	public boolean esVerdaderFals() { return false; }

	public int getTotalOpcions() { return 0; }

	public abstract String getEnunciatPregunta(int num);

	public abstract String getEnunciatPreguntaHtml(int num);

	protected String crearEnunciatPregunta(int num, String resposta) {
		return WordUtils.wrap("Pregunta "+num+". ("+(new DecimalFormat("#0.0")).format(this.puntuacio)+" pts) "+this.text,
				Examen.AMPLE_ENUNCIAT, System.lineSeparator(), true)+
				System.lineSeparator()+resposta;
	}

	protected String crearEnunciatPreguntaHtml(int num, String resposta) {
		return "<p><b>Pregunta "+num+"</b>. <i>("+(new DecimalFormat("#0.0")).format(this.puntuacio)+" pts)</i> "+this.text+"</p>"
				+resposta;
	}

}
