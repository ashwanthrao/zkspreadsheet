package org.zkoss.zss.api.impl;

import static org.junit.Assert.assertEquals;
import static org.zkoss.zss.api.CellOperationUtil.applyFontBoldweight;
import static org.zkoss.zss.api.Ranges.range;

import java.io.IOException;
import java.io.InputStream;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.zkoss.zss.Setup;
import org.zkoss.zss.api.CellOperationUtil;
import org.zkoss.zss.api.Exporter;
import org.zkoss.zss.api.Exporters;
import org.zkoss.zss.api.Importers;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.Range.InsertCopyOrigin;
import org.zkoss.zss.api.Ranges;
import org.zkoss.zss.api.Range.InsertShift;
import org.zkoss.zss.api.model.Book;
import org.zkoss.zss.api.model.Font;
import org.zkoss.zss.api.model.CellStyle.BorderType;
import org.zkoss.zss.api.model.Sheet;

/**
 * ZSS-408.
 * ZSS-414. ZSS-415.
 * ZSS-425. ZSS-426.
 * ZSS-435.
 * @author kuro
 *
 */
public class Issue400Test {
	
	@BeforeClass
	public static void setUpLibrary() throws Exception {
		Setup.touch();
	}

	@Ignore("ZSS-437")
	@Test
	public void testZSS437() throws IOException {
		final String filename = "book/blank.xlsx";
		final InputStream is =  getClass().getResourceAsStream(filename);
		Book workbook = Importers.getImporter().imports(is, filename);
		Util.export(workbook, "book/test.xlsx");
		
		Sheet sheet = workbook.getSheet("Sheet1");
		
		Range rA1 = range(sheet, "A1");
		rA1.setCellEditText("Bold");
		applyFontBoldweight(rA1, Font.Boldweight.BOLD);
		rA1.setColumnWidth(100);
	}
	
	/**
	 * Endless loop when export demo swineFlu.xls pdf
	 */
	@Test
	public void testZSS426() throws IOException {
		
		final String filename = "book/426-swineFlu.xls";
		final InputStream is =  getClass().getResourceAsStream(filename);
		Book workbook = Importers.getImporter().imports(is, filename);
		
		// export work book
    	Exporter exporter = Exporters.getExporter();
    	java.io.File temp = java.io.File.createTempFile("test",".xls");
    	java.io.FileOutputStream fos = new java.io.FileOutputStream(temp);
    	exporter.export(workbook,fos);
    	
    	// import book again
    	Importers.getImporter().imports(temp,"test");

	}
	
	/**
	 * insert whole row or column when overlap merge cell with border style will cause unexpected result
	 */
	@Ignore
	@Test
	public void testZSS435() throws IOException {
		final String filename = "book/blank.xlsx";
		final InputStream is =  getClass().getResourceAsStream(filename);
		Book workbook = Importers.getImporter().imports(is, filename);
		
		Sheet sheet = workbook.getSheet("Sheet1");
		
		Range range = Ranges.range(sheet, "B2:D4");
		range.merge(false);
		
		Range columnC = Ranges.range(sheet, "C1");
		columnC.toColumnRange().insert(InsertShift.DEFAULT, InsertCopyOrigin.FORMAT_NONE);
		
		assertEquals(BorderType.THIN, columnC.getCellStyle().getBorderBottom());
		
	}
	
	/**
	 * Cannot save 2003 format if the file contains auto filter configuration.
	 */
	@Test
	public void testZSS408() throws IOException {
		
		final String filename = "book/408-save-autofilter.xls";
		final InputStream is =  getClass().getResourceAsStream(filename);
		Book workbook = Importers.getImporter().imports(is, filename);
		
		// export work book
    	Exporter exporter = Exporters.getExporter();
    	java.io.File temp = java.io.File.createTempFile("test",".xls");
    	java.io.FileOutputStream fos = new java.io.FileOutputStream(temp);
    	exporter.export(workbook,fos);
    	
    	// import book again
    	Importers.getImporter().imports(temp,"test");

	}
	
	/**
	 * 1. load a blank sheet (excel 2003)
	 * 2. set cell empty string to any cell will cause exception
	 */
	@Test
	public void testZSS414() throws IOException {
		final String filename = "book/blank.xls";
		final InputStream is =  getClass().getResourceAsStream(filename);
		Book workbook = Importers.getImporter().imports(is, filename);
		Sheet sheet1 = workbook.getSheet("Sheet1");
		Range r = Ranges.range(sheet1, "C1");
		r.setCellEditText("");
	}
	
	/**
	 * 1. import a book with comment
	 * 2. export
	 * 3. import again will throw exception
	 */
	@Test
	public void testZSS415() throws IOException {
		
		final String filename = "book/415-commentUnsupport.xls";
		final InputStream is =  getClass().getResourceAsStream(filename);
		Book workbook = Importers.getImporter().imports(is, filename);
		
		// export work book
    	Exporter exporter = Exporters.getExporter();
    	java.io.File temp = java.io.File.createTempFile("test",".xls");
    	java.io.FileOutputStream fos = new java.io.FileOutputStream(temp);
    	exporter.export(workbook, fos);
    	
    	// import book again
    	Importers.getImporter().imports(temp, "test");
	}
	
	//not fix yet
	@Test
	public void testZSS425() throws IOException {
		
		final String filename = "book/425-updateStyle.xlsx";
		final InputStream is = getClass().getResourceAsStream(filename);
		Book book = Importers.getImporter().imports(is, filename);
		
		Range r = Ranges.range(book.getSheetAt(0),0,0);
		
		CellOperationUtil.applyBackgroundColor(r, "#f0f000");
		r = Ranges.range(book.getSheetAt(0),0,0);
		Assert.assertEquals("#f0f000", r.getCellStyle().getBackgroundColor().getHtmlColor());
		
		
		// export work book
    	Exporter exporter = Exporters.getExporter();
    	
    	java.io.File temp = java.io.File.createTempFile("test",".xlsx");
    	java.io.FileOutputStream fos = new java.io.FileOutputStream(temp);
    	//export first time
    	exporter.export(book, fos);
    	
		CellOperationUtil.applyBackgroundColor(r, "#00ff00");
		r = Ranges.range(book.getSheetAt(0),0,0);
		//change again
		Assert.assertEquals("#00ff00", r.getCellStyle().getBackgroundColor().getHtmlColor());
		
    	fos = new java.io.FileOutputStream(temp);
    	//export 2nd time
    	exporter.export(book, fos);
    	System.out.println(">>>write "+temp);
    	// import book again
    	book = Importers.getImporter().imports(temp, "test");
    	r = Ranges.range(book.getSheetAt(0),0,0);
    	//get #ff0000 if bug is not fixed
		Assert.assertEquals("#00ff00", r.getCellStyle().getBackgroundColor().getHtmlColor());
	}


}
