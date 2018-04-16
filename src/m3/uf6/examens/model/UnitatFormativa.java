package m3.uf6.examens.model;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@Entity
@NamedQueries({
	@NamedQuery(name="UnitatFormativa.findAll",
			query="SELECT u FROM UnitatFormativa u ORDER BY u.cicle, u.num"),
	@NamedQuery(name="UnitatFormativa.findByCicle",
			query="SELECT u FROM UnitatFormativa u WHERE u.cicle LIKE &#39;%:cicle%&#39;"),
})
public class UnitatFormativa {
	@Basic(optional=false) private String cicle;
	@Basic(optional=false) private String modul;
	private int num;
	@Basic(optional=false) private String titol;
	private int hores;

	public UnitatFormativa() { } /* XML */

	public UnitatFormativa(String cicle, String modul, int num, String titol, int hores) throws Excepcio {
		if ("".equals(cicle.trim())) throw new Excepcio("UnitatFormativa", "Cal indicar el nom del Cicle Formatiu");
		this.cicle = cicle;
		if ("".equals(modul.trim())) throw new Excepcio("UnitatFormativa", "Cal indicar el nom del Mòdul Professional");
		this.modul = modul;
		if (num<=0) throw new Excepcio("UnitatFormativa", "El número d'unitat formativa hauria de ser 1,2,3...: "+num);
		this.num = num;
		if ("".equals(titol)) throw new Excepcio("UnitatFormativa", "Cal indicar el titol de la Unitat Formativa");
		this.titol = titol;
		if (hores<=0) throw new Excepcio("UnitatFormativa", "El nombre d'hores de la Unitat Formativa és incorrecte: "+hores);
		this.hores = hores;
	}

	public String getCicle() {
		return cicle;
	}

	public void setCicle(String cicle) throws Excepcio {
		if ("".equals(cicle.trim())) throw new Excepcio("UnitatFormativa", "Cal indicar el nom del Cicle Formatiu");
		this.cicle = cicle;
	}

	public String getModul() {
		return modul;
	}

	public void setModul(String modul) throws Excepcio {
		if ("".equals(modul.trim())) throw new Excepcio("UnitatFormativa", "Cal indicar el nom del Mòdul Professional");
		this.modul = modul;
	}

	public String getTitol() {
		return titol;
	}

	public void setTitol(String titol) throws Excepcio {
		if ("".equals(titol)) throw new Excepcio("UnitatFormativa", "Cal indicar el titol de la Unitat Formativa");
		this.titol = titol;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) throws Excepcio {
		if (num<=0) throw new Excepcio("UnitatFormativa", "El número d'unitat formativa hauria de ser 1,2,3...: "+num);
		this.num = num;
	}

	public int getHores() {
		return hores;
	}

	public void setHores(int hores) throws Excepcio {
		if (hores<=0) throw new Excepcio("UnitatFormativa", "El nombre d'hores de la Unitat Formativa és incorrecte: "+hores);
		this.hores = hores;
	}

	// Properties
	public SimpleStringProperty getCicleProperty() {
		return new SimpleStringProperty(cicle);
	}

	public SimpleStringProperty getModulProperty() {
		return new SimpleStringProperty(modul);
	}

	public SimpleStringProperty getTitolProperty() {
		return new SimpleStringProperty(titol);
	}
	public SimpleIntegerProperty getNumProperty() {
		return new SimpleIntegerProperty(num);
	}
	public SimpleIntegerProperty getHoresProperty() {
		return new SimpleIntegerProperty(hores);
	}

	public String getTitolUf() {
		return "UF"+this.getNum()+": "+this.getTitol();
	}

	public String getModulUf() {
		return this.cicle+" - "+this.getModul();
	}

	@Override
	public String toString() {
		return this.getModulUf()+". "+getTitolUf();
	}


}
