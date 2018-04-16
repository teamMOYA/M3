package m3.uf6.examens.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

@Entity
@NamedQueries({
	@NamedQuery(name="Lliurament.findAll",
			query="SELECT l FROM Lliurament l"),
	@NamedQuery(name="Examen.findByExamen",
			query="SELECT l FROM Lliurament l WHERE l.examen = :examen"),
})
public class Lliurament {
	private double nota;
	@Basic(optional=false) private Estudiant estudiant;
	@Basic(optional=false) private Examen examen;

	public Lliurament() {} /* XML */

	public Lliurament(Estudiant estudiant, Examen examen) throws Excepcio {
		this.estudiant = estudiant;
		this.examen = examen;
		this.nota = 0;
	}

	public double getNota() {
		return nota;
	}

	public void setNota(double nota) {
		this.nota = nota;
	}

	public Estudiant getEstudiant() {
		return estudiant;
	}

	public void setEstudiant(Estudiant estudiant) throws Excepcio {
		this.estudiant = estudiant;
	}

	public Examen getExamen() {
		return examen;
	}

	public void setExamen(Examen examen) throws Excepcio {
		this.examen = examen;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj==null) return false;
		return this.estudiant.equals(((Lliurament) obj).getEstudiant());
	}

	// Properties
	public SimpleStringProperty getNomEstudiantProperty() {
		return new SimpleStringProperty(estudiant.getCognomsNom());
	}

	public SimpleDoubleProperty getNotaProperty() {
		return new SimpleDoubleProperty(this.getNota());
	}
}
