package m3.uf6.examens.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@Entity
@NamedQueries({
	@NamedQuery(name="Estudiant.findAll",
			query="SELECT e FROM Estudiant e ORDER BY e.cognoms, e.nom"),
	@NamedQuery(name="Estudiant.findByNomCognoms",
			query="SELECT e FROM Estudiant e WHERE CONCAT(e.nom, ' ', e.cognoms) LIKE '%:text%'"),
})
public class Estudiant implements Comparable<Estudiant>, Serializable {

	private static final long serialVersionUID = 1L;
	public static final int MIN_EDAT = 18;
	@Basic(optional=false) private String nom;
	@Basic(optional=false) private String cognoms;
	private int edat;

	public Estudiant() { }	/* XML */

	public Estudiant(String nom, String cognoms, int edat) throws Excepcio {
		super();
		//if (nom==null || "".equals(nom.trim())) throw new Excepcio("Estudiant", "Cal indicar el nom de l'estudiant");
		if ("".equals(nom.trim())) throw new Excepcio("Estudiant", "Cal indicar el nom de l'estudiant");
		this.nom = nom;
		//if (cognoms==null || "".equals(cognoms.trim())) throw new Excepcio("Estudiant", "Cal indicar els cognoms de l'estudiant");
		if ("".equals(cognoms.trim())) throw new Excepcio("Estudiant", "Cal indicar els cognoms de l'estudiant");
		this.cognoms = cognoms;
		if (edat<MIN_EDAT) throw new Excepcio("Estudiant", "L'edat mínima dels estudiants és: "+MIN_EDAT);
		this.edat = edat;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) throws Excepcio {
		//if (nom==null || "".equals(nom.trim())) throw new Excepcio("Estudiant", "Cal indicar el nom de l'estudiant");
		if ("".equals(nom.trim())) throw new Excepcio("Estudiant", "Cal indicar el nom de l'estudiant");
		this.nom = nom;
	}

	public String getCognoms() {
		return cognoms;
	}

	public void setCognoms(String cognoms) throws Excepcio {
		//if (cognoms==null || "".equals(cognoms.trim())) throw new Excepcio("Estudiant", "Cal indicar els cognoms de l'estudiant");
		if ("".equals(cognoms.trim())) throw new Excepcio("Estudiant", "Cal indicar els cognoms de l'estudiant");
		this.cognoms = cognoms;
	}

	public int getEdat() {
		return edat;
	}

	public void setEdat(int edat) throws Excepcio {
		if (edat<MIN_EDAT) throw new Excepcio("Estudiant", "L'edat mínima dels estudiants és: "+MIN_EDAT);
		this.edat = edat;
	}

	public String getCognomsNom() {
		return this.cognoms+", "+this.nom;
	}

	@Override
	public int compareTo(Estudiant o) {
		return this.getCognomsNom().compareTo(o.getCognomsNom());
	}

	// Properties
	public SimpleStringProperty getNomProperty() {
		return new SimpleStringProperty(nom);
	}

	public SimpleStringProperty getCognomsProperty() {
		return new SimpleStringProperty(cognoms);
	}

	public SimpleIntegerProperty getEdatProperty() {
		return new SimpleIntegerProperty(edat);
	}

	public SimpleStringProperty getCognomsNomProperty() {
		return new SimpleStringProperty(getCognomsNom());
	}
}
