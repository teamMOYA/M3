package m3.uf6.examens.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class ModelQueryResult {
	private String titol;
	private int count;
	private Vector<String> names;
	private Vector<Integer> widths;
	private Vector<String> aligns;
	private List<Object[]> data;
	public ModelQueryResult(String titol, int count, Vector<String> names, Vector<Integer> widths, Vector<String> aligns, List<Object[]> data) {
		this.titol = titol; this.count = count; this.names = names; this.widths = widths; this.aligns = aligns; this.data = data;
	}
	public String getTitol() {return this.titol;}
	public int getCount() {return this.count;}
	public Vector<String> getNames() {return this.names;}
	public Vector<Integer> getWidths() {return this.widths;}
	public Vector<String> getAligns() {return this.aligns;}
	public List<Object[]> getData() {return this.data;}

	public List<Map<String, String>> getDataMap() {
		LinkedList<Map<String, String>> resultData = new LinkedList<Map<String, String>>();
		for (Object[] fila : this.data) {
			int col = 1;
			Map<String, String> dataRow = new HashMap<String, String>();
			for (Object dada : fila) {
				dataRow.put(this.names.get(col - 1), (dada!=null?dada.toString():"--"));
				col++;
			}
			resultData.add(dataRow);
		}
		return resultData;
	}
}

