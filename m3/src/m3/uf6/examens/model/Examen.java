package m3.uf6.examens.model;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeSet;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

@Entity
@NamedQueries({
	@NamedQuery(name="Examen.findAll",
			query="SELECT e FROM Examen e ORDER BY e.unitat"),
	@NamedQuery(name="Examen.findByUnitat",
			query="SELECT e FROM Examen e WHERE e.unitat = :unitat"),
})
public class Examen implements Avaluable {
	public static final int MAX_PREGUNTES = 10;
	public static final int AMPLE_ASSISTENCIA = 28;
	public static final int COL_LLIURAMENT = 28;
	public static final int GAP_LLIURAMENT = 10;
	public static final int COL_CORRECCIO = 35;
	public static final int AMPLE_ENUNCIAT = 80;
	public static final int AMPLE_PUNTUACIO = 15;
	// http://www.fileformat.info/info/unicode/char/search.htm?q=square
	public static final char NO_LLIURAT_SQUARE =  '\u25EF'; // '\u20DE'; '\u274F';
	public static final char LLIURAT_CHECK =  '\u2A02'; //'\u274E'; ;'\u2327'
	public static final char CHECK_SQUARE =  '\u20DE';
	@Basic(optional=false) private UnitatFormativa unitat;
	@OneToMany(cascade=CascadeType.ALL) private LinkedList<Pregunta> preguntes;		// Llista de preguntes
	@OneToMany(cascade=CascadeType.ALL) private TreeSet<Estudiant> alumnes;
	@OneToMany(cascade=CascadeType.ALL) private Stack<Lliurament> lliuraments;
	@OneToMany(cascade=CascadeType.ALL) private Stack<Lliurament> correccions;
	@OneToMany(cascade=CascadeType.ALL) private Queue<Lliurament> revisions;

	public Examen() {	/* XML */
		this.preguntes = new LinkedList<Pregunta>();
		this.alumnes = new TreeSet<Estudiant>();
		this.lliuraments = new Stack<Lliurament>();
		this.correccions = new Stack<Lliurament>();
		this.revisions = new LinkedList<Lliurament>();
	}

	public Examen(UnitatFormativa unitat) throws Excepcio {
		//if (unitat==null) throw new Excepcio("Examen", "Cal indicar la Unitat Formativa");
		this.unitat = unitat;
		this.preguntes = new LinkedList<Pregunta>();
		this.alumnes = new TreeSet<Estudiant>();
		this.lliuraments = new Stack<Lliurament>();
		this.correccions = new Stack<Lliurament>();
		this.revisions = new LinkedList<Lliurament>();
	}

	public UnitatFormativa getUnitat() {
		return unitat;
	}

	public void setUnitat(UnitatFormativa unitat) throws Excepcio {
		this.unitat = unitat;
	}

	public LinkedList<Pregunta> getPreguntes() {
		return preguntes;
	}

	public void setPreguntes(LinkedList<Pregunta> preguntes) {
		this.preguntes = preguntes;
	}

	public TreeSet<Estudiant> getAlumnes() {
		return alumnes;
	}

	public void setAlumnes(TreeSet<Estudiant> alumnes) {
		this.alumnes = alumnes;
	}

	public Stack<Lliurament> getLliuraments() {
		return lliuraments;
	}

	public void setLliuraments(Stack<Lliurament> lliuraments) {
		this.lliuraments = lliuraments;
	}

	public Stack<Lliurament> getCorreccions() {
		return correccions;
	}

	public void setCorreccions(Stack<Lliurament> correccions) {
		this.correccions = correccions;
	}

	public Queue<Lliurament> getRevisions() {
		return revisions;
	}

	public void setRevisions(Queue<Lliurament> revisions) {
		this.revisions = revisions;
	}

	public void inscriureEstudiant(Estudiant estudiant) throws Excepcio {
		if (estudiant == null) throw new Excepcio("Examen", "Cal indicar l'estudiant per poder inscriure'l");
		this.alumnes.add(estudiant);
	}

	public void inscriureEstudiants(Estudiant[] estudiants) throws Excepcio {
		if (estudiants == null) throw new Excepcio("Examen", "Cal indicar els estudiants per poder-los inscriure");
		this.alumnes.addAll(Arrays.asList(estudiants));
	}

	public void anularMatriculaEstudiant(Estudiant estudiant) throws Excepcio  {
		if (estudiant == null) throw new Excepcio("Examen", "Cal indicar l'estudiant per poder anul·lar la matrícula");
		this.alumnes.remove(estudiant);
	}

	public String generarLlistatAssistencia() throws Excepcio {
		// Marcar el checkbox en cas que l'estudiant estigui a lliuraments o a correccions
		String assistencia = "  "+StringUtils.center("Llistat assistència", Examen.AMPLE_ASSISTENCIA)+"  "+System.lineSeparator();
		assistencia += "  "+StringUtils.repeat(".", Examen.AMPLE_ASSISTENCIA)+System.lineSeparator()+"  "+System.lineSeparator();

		Iterator<Estudiant> it = this.alumnes.iterator();

		while (it.hasNext()) {
			Estudiant current = it.next();
			assistencia += "  "+StringUtils.rightPad((this.lliuraments.contains(new Lliurament(current, this))||
					this.correccions.contains(new Lliurament(current, this))?LLIURAT_CHECK:NO_LLIURAT_SQUARE)+
					"  "+StringUtils.abbreviate(current.getCognomsNom(), Examen.AMPLE_ASSISTENCIA - 3), Examen.AMPLE_ASSISTENCIA)+System.lineSeparator();
		}

		assistencia += System.lineSeparator()+"  "+StringUtils.repeat("_", Examen.AMPLE_ASSISTENCIA)+System.lineSeparator();
		return assistencia;
	}

	public void apilarLliurament(Estudiant estudiant) throws Excepcio {
		if (estudiant == null) throw new Excepcio("Examen", "Cal indicar l'estudiant per poder fer el lliurament");
		if (this.lliuraments.contains(new Lliurament(estudiant, this)) ||
				this.correccions.contains(new Lliurament(estudiant, this))) throw new Excepcio("Examen", "L'estudiant "+estudiant.getCognomsNom()+" ja ha fet un lliurament");

		this.lliuraments.push(new Lliurament(estudiant, this));
	}

	public void desapilarLliurament() {
		if (!this.lliuraments.empty()) this.lliuraments.pop();
	}

	public Lliurament darrerLliurament() {
		if (this.lliuraments.empty()) return null;
		return this.lliuraments.peek();
	}

	public void corregirLliurament(double nota) throws Excepcio {
		// Treure de lliuraments i afegir a correccions
		this.validarNota(nota);

		if (this.lliuraments.isEmpty()) throw new Excepcio("Examen", "No hi ha cap lliurament per corregir");
		Lliurament correccio = this.lliuraments.pop();
		correccio.setNota(this.notaNormalitzada(nota));
		this.correccions.push(correccio);
	}

	public String mostrarCorreccions() {
		// Mostra les dues piles: lliuraments i correccions respectant l'ordre de cadascuna

		String pila = "  "+StringUtils.repeat(" ", GAP_LLIURAMENT);
		pila += StringUtils.center("LLIURAMENTS", COL_LLIURAMENT);
		pila += StringUtils.repeat(" ", GAP_LLIURAMENT);
		pila += StringUtils.center("CORRECCIONS", COL_CORRECCIO)+System.lineSeparator();

		pila += "  "+StringUtils.repeat(" ", GAP_LLIURAMENT);
		pila += StringUtils.repeat(" ", COL_LLIURAMENT);
		pila += StringUtils.repeat(" ", GAP_LLIURAMENT);
		pila += StringUtils.repeat(" ", COL_CORRECCIO)+System.lineSeparator()+System.lineSeparator();

		Lliurament[] arrayLliuraments = this.lliuraments.toArray(new Lliurament[]{});
		Lliurament[] arrayCorreccions = this.correccions.toArray(new Lliurament[]{});

		int top = Math.max(arrayLliuraments.length, arrayCorreccions.length) - 1;

		while (top >= 0) {
			pila += "  ";
			// Primera columna. LLiuraments
			if (top <= arrayLliuraments.length - 1) {
				Estudiant current = arrayLliuraments[top].getEstudiant();

				if (top == arrayLliuraments.length - 1) pila += StringUtils.center("TOP ->", GAP_LLIURAMENT);
				else pila += StringUtils.repeat(" ", GAP_LLIURAMENT);
				pila += "[ "+StringUtils.center(StringUtils.abbreviate(current.getCognomsNom(), COL_LLIURAMENT-4), COL_LLIURAMENT-4, " ")+" ]";
			} else {
				pila += StringUtils.repeat(" ", GAP_LLIURAMENT+COL_LLIURAMENT);
			}

			// Segona columna. Correccions
			if (top <= arrayCorreccions.length - 1) {
				Lliurament correcio = arrayCorreccions[top];

				if (top == arrayCorreccions.length - 1) pila += StringUtils.center("TOP ->", GAP_LLIURAMENT);
				else pila += StringUtils.repeat(" ", GAP_LLIURAMENT);
				String avaluta = (new DecimalFormat("#0.00")).format(correcio.getNota())+" - "+correcio.getEstudiant().getCognomsNom();
				pila += "[ "+StringUtils.center(StringUtils.abbreviate(avaluta, COL_CORRECCIO-4), COL_CORRECCIO-4, " ")+" ]";
			} else {
				pila += StringUtils.repeat(" ", GAP_LLIURAMENT+COL_CORRECCIO);
			}

			pila += System.lineSeparator()+System.lineSeparator();
			top--;
		}

		pila += "  "+StringUtils.repeat(" ", GAP_LLIURAMENT);
		pila += StringUtils.center(" TAULA PROFESSOR ", COL_LLIURAMENT+GAP_LLIURAMENT+COL_CORRECCIO, "_");
		pila += System.lineSeparator()+System.lineSeparator();

		return pila;
	}

	// Només si el lliurament de l'estudiant està corregit i encara no s'ha demanat la revisió
	public void solicitarRevisio(Estudiant estudiant) throws Excepcio {
		if (!this.correccions.contains(new Lliurament(estudiant, this))) throw new Excepcio("Examen", "El lliurament de l'alumne "+estudiant.getCognomsNom()+" no està corregit");

		if (this.revisions.contains(new Lliurament(estudiant, this))) throw new Excepcio("Examen", "Ja s'ha demanat la revisió del lliurament de l'alumne "+estudiant.getCognomsNom());

		for (Lliurament lliurament : this.correccions) {
			if (lliurament.getEstudiant().equals(estudiant)) {
				this.revisions.offer(lliurament);
				return;
			}
		}
	}

	public void atendreRevisio(double nota) throws Excepcio {
		this.validarNota(nota);

		Lliurament correccio = this.revisions.poll();

		if (correccio != null) correccio.setNota(this.notaNormalitzada(nota));
	}

	public Lliurament primeraRevisio() {
		return this.revisions.peek();
	}

	public void validarNota(double nota) throws Excepcio {
		if (nota<0 || nota>this.getPuntuacio()) throw new Excepcio("Examen", "El valor de la nota és incorrecte: "+
				(new DecimalFormat("#0.00")).format(nota));
	}

	// Sobre 10
	public double notaNormalitzada(double nota) {
		if (this.getPuntuacio() == 0) return 0;
		return nota/this.getPuntuacio()*10;
	}

	// retorna false si no hi ha espai al vector, text eś null o puntuació < 0
	public boolean afegirPreguntaOberta(String text, double puntuacio) throws Excepcio {
		this.validarPregunta(text, puntuacio);

		return this.preguntes.add(new PreguntaOberta(text, puntuacio));
	}

	// retorna false si no hi ha espai al vector, text eś null, puntuació < 0 o opcions és null
	public boolean afegirPreguntaOpcions(String text, double puntuacio, String[] opcions) throws Excepcio {
		this.validarPregunta(text, puntuacio);

		return this.preguntes.add(new PreguntaOpcions(text, puntuacio, opcions));
	}

	// retorna false si no hi ha espai al vector, text eś null o puntuació < 0
	public boolean afegirPreguntaVerdaderFals(String text, double puntuacio) throws Excepcio {
		this.validarPregunta(text, puntuacio);

		return this.preguntes.add(new PreguntaVerdaderFals(text, puntuacio));
	}

	// Esborra la pregunta (canvia la instància per null)  num començant per 1 fins a 10 retorna false si l'índex és incorrecte
	public boolean esborrarPregunta(int num) throws Excepcio {
		if (num < 1 || num > this.preguntes.size()) throw new Excepcio("Examen", "No es pot esborrar la pregunta "+num+", no existeix ");

		this.preguntes.remove(num -1);

		return true;
	}

	private void validarPregunta(String text, double puntuacio) throws Excepcio {
		if (this.preguntes.size() >= MAX_PREGUNTES) throw new Excepcio("Examen", "L'examen ha arribat al límit de preguntes: "+MAX_PREGUNTES);

		if (text == null) throw new Excepcio("Examen", "Cal indicar el text de la pregunta");

		if (puntuacio <= 0) throw new Excepcio("Examen", "La puntuació de la pregunta hauria de ser major que 0");
	}

	// titol
	@Override
	public String getTitol() {
		return StringUtils.center(" Examen "+unitat.getModulUf()+" ",AMPLE_ENUNCIAT, "#")+System.lineSeparator()+System.lineSeparator()+
				StringUtils.rightPad(StringUtils.abbreviate(unitat.getTitolUf(), AMPLE_ENUNCIAT), AMPLE_ENUNCIAT)+
				System.lineSeparator();
	}

	public String getTitolHtml() {
		return "<h1>Examen "+unitat.getModulUf()+"</h1>"+"<h2>"+unitat.getTitolUf()+"</h2>";
	}

	// suma punts
	@Override
	public double getPuntuacio() {
		double punt = 0;
		for (Pregunta pregunta : preguntes) {
			if (pregunta != null) punt += pregunta.getPuntuacio();
		}
		return punt;
	}

	// enunciat
	@Override
	public String getEnunciat() {
		return this.getEnunciatIterator(this.preguntes.iterator());
	}

	public String getEnunciatDescendent() {
		return this.getEnunciatIterator(this.preguntes.descendingIterator());
	}

	private String getEnunciatIterator(Iterator<Pregunta> iterator) {
		String enunciat = 	StringUtils.rightPad("ENUNCIAT", AMPLE_ENUNCIAT-AMPLE_PUNTUACIO)+
				StringUtils.leftPad((new DecimalFormat("##0.0")).format(this.getPuntuacio())+" PUNTS", AMPLE_PUNTUACIO);
		enunciat +=			System.lineSeparator() + StringUtils.repeat("-", AMPLE_ENUNCIAT)+System.lineSeparator()+System.lineSeparator();

		int num = 1;
		while (iterator.hasNext()) {
			enunciat += iterator.next().getEnunciatPregunta(num)+StringUtils.repeat(System.lineSeparator(),3);
			num++;
		}
		return enunciat;
	}

	public String getEnunciatHtml() {
		String enunciat = 	"<h3>ENUNCIAT ("+(new DecimalFormat("##0.0")).format(this.getPuntuacio())+" PUNTS)</h3>";

		int num = 1;
		for (Pregunta pregunta : this.preguntes) {
			enunciat += pregunta.getEnunciatPreguntaHtml(num)+"<br/>";
			num++;
		}
		return enunciat;
	}


	@Override
	public boolean esAvaluable() {
		return this.getPuntuacio() > 0;
	}

	public void desarPreguntes(String fitxer) throws Excepcio {
		if (fitxer != null) {
			XMLEncoder encoder;
			try {
				encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fitxer)));
				encoder.writeObject(this.preguntes);
				encoder.close();
			} catch (FileNotFoundException e) {
				throw new Excepcio("Examen", "Problemes amb el fitxer desant la llista de preguntes: "+e.getMessage());
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void carregarPreguntes(String fitxer) throws Excepcio {
		if (fitxer != null) {
			XMLDecoder decoder;
			try {
				decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(fitxer)));
				this.preguntes = (LinkedList<Pregunta>) decoder.readObject();
				decoder.close();
			} catch (FileNotFoundException e) {
				throw new Excepcio("Examen", "El fitxer per carregar la llista de preguntes no existeix: "+e.getMessage());
			}
		}
	}

	public void desarAlumnes(String fitxer) throws Excepcio {
		if (fitxer != null) {
			ObjectOutputStream encoder;
			try {
				encoder = new ObjectOutputStream(new FileOutputStream(fitxer));
				encoder.writeObject(this.alumnes);
				encoder.close();
			} catch (IOException e) {
				throw new Excepcio("Examen", "Error al carregar els alumnes matriculats: "+e.getMessage());
			}
		}
	}

	@SuppressWarnings({ "unchecked", "resource" })
	public void carregarAlumnes(String fitxer) throws Excepcio {
		if (fitxer != null) {
			ObjectInputStream decoder;
			try {
				decoder = new ObjectInputStream(new FileInputStream(fitxer));
				this.alumnes = (TreeSet<Estudiant>) decoder.readObject();
				decoder.close();
			} catch (ClassNotFoundException e) {
				throw new Excepcio("Examen", "El fitxer per carregar els alumnes matriculats no existeix: "+e.getMessage());
			} catch (FileNotFoundException e) {
				throw new Excepcio("Examen", "Error d'entrada/sortida al carregar els alumnes matriculats: "+e.getMessage());
			} catch (IOException e) {
				throw new Excepcio("Examen", "Error al carregar els alumnes matriculats: "+e.getMessage());
			}
		}
	}

	public String getModulUf() {
		return this.unitat.getModulUf();
	}

	public String getTitolUf() {
		return this.unitat.getTitolUf();
	}

	public int getTotalPreguntes() {
		return preguntes.size();
	}

	// Properties
	public SimpleStringProperty getModulUfProperty() {
		return new SimpleStringProperty(this.getModulUf());
	}

	public SimpleStringProperty getTitolUfProperty() {
		return new SimpleStringProperty(this.getTitolUf());
	}

	public SimpleIntegerProperty getTotalPreguntesProperty() {
		return new SimpleIntegerProperty(this.getTotalPreguntes());
	}

	public SimpleDoubleProperty getPuntuacioProperty() {
		return new SimpleDoubleProperty(this.getPuntuacio());
	}
}
