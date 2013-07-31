/* ReservedCellData.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2013/7/25, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zss.undo.impl;

import org.zkoss.zss.api.IllegalFormulaException;
import org.zkoss.zss.api.Range;
import org.zkoss.zss.api.model.CellData;

/**
 * a helper class to keet cell data, e.g editText, comment, hyperlink
 * @author dennis
 *
 */
public class ReservedCellData {

	boolean _blank = false;;
	String _editText;
	
	public ReservedCellData(){
		this._blank = true;
	}
	
	public ReservedCellData(String editText){
		this._editText = editText;
	}
	
	public void apply(Range range){
		if(_blank){
			range.clearContents();
		}else{
			try{
				range.setCellEditText(_editText);
			}catch(IllegalFormulaException x){};//eat in this mode
		}
	}
	
	public static ReservedCellData reserve(Range range){
		CellData d = range.getCellData();
		
		if(d.isBlank()){
			return new ReservedCellData();
		}
		
		String editText = d.getEditText();
		//TODO handle other data someday(hyperlink, comment)
		return new ReservedCellData(editText);
	}
}