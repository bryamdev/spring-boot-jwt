package com.bolsadeideas.springboot.datajpa.app.view.xlxs;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import com.bolsadeideas.springboot.datajpa.app.models.entity.Factura;
import com.bolsadeideas.springboot.datajpa.app.models.entity.ItemFactura;

@Component("factura/ver.xlsx")
public class FacturaXlsxView extends AbstractXlsxView{

	@Override
	protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
	
		response.addHeader("Content-Disposition", "attachment; filename=\"factura_view.xlsx\"");
		
		MessageSourceAccessor messageAcessor = getMessageSourceAccessor();
		
		Factura factura = (Factura) model.get("factura"); 
		Sheet sheet = workbook.createSheet("Factura");
		
		Row row = sheet.createRow(0);
		Cell cell = row.createCell(0);
		cell.setCellValue(messageAcessor.getMessage("text.factura.ver.cliente.titulo"));
		
		row = sheet.createRow(1);
		cell = row.createCell(0);
		cell.setCellValue(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());
		
		row = sheet.createRow(2);
		cell = row.createCell(0);
		cell.setCellValue(factura.getCliente().getEmail());
		
		
		
		sheet.createRow(4).createCell(0).setCellValue(messageAcessor.getMessage("text.factura.ver.factura.titulo"));
		sheet.createRow(5).createCell(0).setCellValue(messageAcessor.getMessage("text.factura.ver.factura.folio") + factura.getId());
		sheet.createRow(6).createCell(0).setCellValue(messageAcessor.getMessage("text.factura.ver.factura.descripcion") + factura.getDescripcion());
		sheet.createRow(7).createCell(0).setCellValue(messageAcessor.getMessage("text.factura.ver.factura.fecha") + factura.getCreateAt());
		
		row = sheet.createRow(9);
		row.createCell(0).setCellValue(messageAcessor.getMessage("text.factura.ver.list.nombre"));
		row.createCell(1).setCellValue(messageAcessor.getMessage("text.factura.ver.list.precio"));
		row.createCell(2).setCellValue(messageAcessor.getMessage("text.factura.ver.list.cantidad"));
		row.createCell(3).setCellValue(messageAcessor.getMessage("text.factura.ver.list.subtotal"));
		
		
		int indexRow = 10;
		for(ItemFactura item : factura.getItems()) {
			row = sheet.createRow(indexRow);
			row.createCell(0).setCellValue(item.getProducto().getNombre());
			row.createCell(1).setCellValue(item.getProducto().getPrecio());
			row.createCell(2).setCellValue(item.getCantidad());
			row.createCell(3).setCellValue(item.calcularSubTotal());
			
			indexRow++;
		}
		
		row = sheet.createRow(indexRow);
		row.createCell(2).setCellValue("TOTAL:");;
		row.createCell(3).setCellValue(factura.calcularTotal());
				
		
	}

}
