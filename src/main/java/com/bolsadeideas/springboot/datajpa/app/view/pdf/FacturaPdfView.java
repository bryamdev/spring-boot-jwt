package com.bolsadeideas.springboot.datajpa.app.view.pdf;

import java.awt.Color;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Factura;
import com.bolsadeideas.springboot.datajpa.app.models.entity.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

//Es el nombre de la vista que retorna el metodo ver del controlador
//Esta vista se usa si en el request va el parametro 'format' y valor pdf
//Nota: toda vista representada en una clase debe implementar la interfaz View
@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView{
	
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private LocaleResolver localeResolver;

	@Override
	protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		Locale locale = localeResolver.resolveLocale(request);
		
		//Otra forma de acceder a los mensajes mutiidoma.
		MessageSourceAccessor messageAccesor = getMessageSourceAccessor();
		
		Factura factura = (Factura) model.get("factura");
		
		PdfPCell cell = null;
		
		PdfPTable table = new PdfPTable(1);
		table.setSpacingAfter(20);
	
		
		cell = new PdfPCell(new Paragraph(messageSource.getMessage("text.factura.ver.cliente.titulo", null, locale)));
		cell.setBackgroundColor(new Color(0, 191, 255));
		cell.setPadding(8f);
		table.addCell(cell);
		
		table.addCell(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		table.addCell(factura.getCliente().getEmail());
		
		
		
		PdfPTable table2 = new PdfPTable(1);
		table2.setSpacingAfter(20);
		
		cell = new PdfPCell(new Paragraph(messageSource.getMessage("text.factura.ver.factura.titulo", null, locale)));
		cell.setBackgroundColor(new Color(128, 255, 0));
		cell.setPadding(8f);
		table2.addCell(cell);
		
		table2.addCell(messageSource.getMessage("text.factura.ver.factura.folio", null, locale) + factura.getId());
		table2.addCell(messageSource.getMessage("text.factura.ver.factura.descripcion", null, locale) + factura.getDescripcion());
		table2.addCell(messageSource.getMessage("text.factura.ver.factura.fecha", null, locale) + factura.getCreateAt());
		
		
		
		PdfPTable table3 = new PdfPTable(4);
		table3.setSpacingAfter(20);
		table3.setWidths(new float[] {2.5F, 1, 1, 1});
		
		table3.addCell(messageAccesor.getMessage("text.factura.ver.list.nombre"));
		table3.addCell(messageAccesor.getMessage("text.factura.ver.list.precio"));
		table3.addCell(messageAccesor.getMessage("text.factura.ver.list.cantidad"));
		table3.addCell(messageAccesor.getMessage("text.factura.ver.list.subtotal"));
		
		for(ItemFactura item : factura.getItems()) {
			
			table3.addCell(item.getProducto().getNombre());			
			table3.addCell(item.getProducto().getPrecio().toString());
			
			cell = new PdfPCell(new Paragraph(item.getCantidad().toString()));
			cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
			
			table3.addCell(cell);
			table3.addCell(Double.toString(item.calcularSubTotal()));
		}
		
		cell = new PdfPCell(new Phrase("Total: "));
		cell.setColspan(3);
		cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
		table3.addCell(cell);
		table3.addCell(factura.calcularTotal().toString());
		
		
		
		document.add(table);
		document.add(table2);
		document.add(table3);
		document.addTitle(factura.getDescripcion());
		
		
	}

}
