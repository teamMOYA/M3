package m3.uf6.examens;

import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;

import m3.uf6.examens.model.Excepcio;

public class PdfManager implements IEventHandler {
	public static final float PAGECOUNT_BOXSIDE = 20.0f;
	public static final String HEADER_TEXT = "Generalitat de Catalunya\nDepartament d’Ensenyament\nINS Marianao";
	public static final String HEADER_IMAGE = "resources/logo-gene.jpg";

	public static final float MARGIN_LEFT = 50.0f;
	public static final float MARGIN_RIGHT = 50.0f;
	public static final float MARGIN_TOP = 80.0f;
	public static final float MARGIN_BOTTOM = 60.0f;

	public static final float LOGO_WIDTH = 25.0f;
	public static final float LOGO_HEIGHT = 29.0f;


	private String outputPdfFile;

	private PdfDocument pdfDocument;
	private PdfFont headerFont;
	//private PdfFont bodyFont;

	public PdfManager(String outputPdfFile) throws Excepcio {
		this.outputPdfFile = outputPdfFile;

		try {
			this.headerFont = PdfFontFactory.createFont(FontConstants.HELVETICA);

		    //this.bodyFont = PdfFontFactory.createFont(this.getClass().getResource("resources/TradeGothic.ttf").getFile(),  PdfEncodings.IDENTITY_H);  // Escriptura horitzontal
		    //this.bodyFont = PdfFontFactory.createFont(this.getClass().getResource("resources/MaximeStd-Regular.ttf").getFile(),  PdfEncodings.IDENTITY_H);
		    //this.bodyFont = PdfFontFactory.createFont(this.getClass().getResource("resources/Freehand591BT-RegularA.ttf").getFile(),  PdfEncodings.IDENTITY_H);
		} catch (IOException e) {
			//e.printStackTrace();
			throw new Excepcio("PdfManager", "Error creant el tipus de font del document. "+e.getMessage());
		}
	}


	public void generarPDF( String htmlContent )  throws Excepcio {
		//Create Document
		PdfWriter writer = null;

		try {
			writer = new PdfWriter(outputPdfFile);
			this.pdfDocument = new PdfDocument(writer);

			String htmlPre = "";
			htmlPre += "<!DOCTYPE html>";
			htmlPre += "<html lang='ca'>";
			htmlPre += "	<head>";
			htmlPre += "		<meta charset='UTF-8'>";
			htmlPre += "		<title>Examen</title>";
			htmlPre += "		<style type='text/css'>";
			htmlPre += "		@font-face {";
			htmlPre += "		    font-family: 'TradeGothic';";
			htmlPre += "		    src: url('"+this.getClass().getResource("resources/TradeGothic.woff")+"') format('woff');";
			//htmlPre += "		    font-family: 'Freehand-Regular';";
			//htmlPre += "		    src: url('"+this.getClass().getResource("resources/Freehand591BT-RegularA.woff")+"') format('woff');";
			//htmlPre += "		    font-family: 'MaximeStd-Regular';";
			//htmlPre += "		    src: url('"+this.getClass().getResource("resources/MaximeStd-Regular.woff")+"') format('woff');";
			htmlPre += "		}";
			htmlPre += "		* {";
			htmlPre += "		    font-family: 'FreeSans';";  // 'FreeSerif' o 'FreeMono'
			htmlPre += "		    font-size: 11pt;";
			htmlPre += "		}";
			htmlPre += "		@page{";
			htmlPre += " 			margin: "+MARGIN_TOP+"pt "+MARGIN_RIGHT+"pt "+MARGIN_BOTTOM+"pt "+MARGIN_LEFT+"pt;";
			htmlPre += " 			@bottom-center {";
			htmlPre += " 			    content: 'Pàgina ' counter(page) ' de ' counter(pages);";
			htmlPre += "		    	font-size: 9pt;";
			htmlPre += " 			}";
			htmlPre += "		}";
			htmlPre += "		h1, h2, h3 {";
			htmlPre += "		    font-family: 'TradeGothic';";
			htmlPre += "		}";
			htmlPre += "		h1 {";
			htmlPre += "		    font-size: 20pt;";
			htmlPre += "		}";
			htmlPre += "		h2 {";
			htmlPre += "		    font-size: 16pt;";
			htmlPre += "		}";
			htmlPre += "		h3 {";
			htmlPre += "		    font-size: 14pt;";
			htmlPre += "		    margin: 30pt 0 15pt;";
			htmlPre += "		}";
			htmlPre += "		.open-response {";
			htmlPre += "		    border: 1px solid black;";
			htmlPre += "		    padding: 60pt 0;";
			htmlPre += "		}";
			htmlPre += "		ul {";
			htmlPre += "		    list-style: none;";
			htmlPre += "		    padding: 0;";
			htmlPre += "		    margin: 0;";
			htmlPre += "		}";
			htmlPre += "		li {";
			htmlPre += "		    padding-left: 20pt;";
			htmlPre += "		    line-height: 0.8;";
			htmlPre += "		}";
			htmlPre += "		li:before {";
			htmlPre += "		    content: '\u25A2';";
			htmlPre += "		    padding-right: 5pt;";
			htmlPre += "		    padding-top: 1pt;";
			htmlPre += "		    font-size: 20pt;";
			htmlPre += "		}";
			htmlPre += "		ul.truefalse-response {";
			htmlPre += "		    text-align: right;";
			htmlPre += "		}";
			htmlPre += "		ul.truefalse-response li {";
			htmlPre += "		    display: inline;";
			htmlPre += "		    padding-left: 5pt;";
			htmlPre += "		    padding-right: 20pt;";
			htmlPre += "		}";
			htmlPre += "		</style>";
			htmlPre += "	</head>";
			htmlPre += "	<body>";

			String htmlPost = "";
			htmlPost += "	</body>";
			htmlPost += "</html>";

			//Assign event-handlers
			this.pdfDocument.addEventHandler(PdfDocumentEvent.START_PAGE, this);	// Després de crear la pàgina

			ConverterProperties converterProperties = new ConverterProperties();
			Document document = HtmlConverter.convertToDocument(htmlPre+htmlContent+htmlPost, this.pdfDocument, converterProperties);

			document.close();
			this.pdfDocument.close();

		} catch (IOException e) {
			//e.printStackTrace();
			throw new Excepcio("PdfManager", "Error d'entrada/sortida. "+e.getMessage());
		}
	}

	@Override
	public void handleEvent(Event event) {
		PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
		PdfDocument pdf = docEvent.getDocument();
		PdfPage page = docEvent.getPage();

		Rectangle pageSize = page.getPageSize();
		PdfCanvas pdfCanvas = new PdfCanvas(page.getLastContentStream(), page.getResources(), pdf);
		//PdfCanvas pdfCanvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdf);
		Canvas canvas = new Canvas(pdfCanvas, pdf, pageSize);

		float headerY = pageSize.getTop() - 25 - LOGO_HEIGHT;
		Image logo = null;
		try {
			logo = new Image(ImageDataFactory.create(this.getClass().getResource(HEADER_IMAGE).getFile()));
			logo.scaleToFit(LOGO_WIDTH, LOGO_HEIGHT);	// absolute size
			logo.setFixedPosition(MARGIN_LEFT-LOGO_WIDTH-3, headerY);

			canvas.add(logo);
		} catch (MalformedURLException e) {
			//e.printStackTrace();
		}

		Paragraph p = new Paragraph(HEADER_TEXT).setFont(headerFont).setFontSize(9f).setMultipliedLeading(1); // Line height 1
		canvas.showTextAligned(p, MARGIN_LEFT, headerY, TextAlignment.LEFT);

		pdfCanvas.release();
		canvas.close();
	}
}
