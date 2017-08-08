package com.accenture.inventory.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.accenture.inventory.exception.InventoryException;
import com.accenture.inventory.model.RegionalTaxRateConfiguration;
import com.accenture.inventory.utils.Settings;

/**
 * Used for processing of Regional Tax
 * @author g.t.gupta
 *
 */
public class RegionalTaxRateManager implements Callable<CarOrdersProcessing> {

	CarOrdersProcessing carOrdersProcessing;

	public RegionalTaxRateManager(CarOrdersProcessing carOrdersProcessing) {
		this.carOrdersProcessing = carOrdersProcessing;
	}

	/**
	 * Reading Regional Tax Rate excel file
	 * @return CarOrdersProcessing
	 * @throws InventoryException
	 */
	public CarOrdersProcessing readRegionalTaxRate() throws InventoryException {

		System.out.println("Inside readRegionalTaxRate");
		File regionalTaxRateExcel = new File(Settings.REGIONALTAX_EXCELPATH);
		FileInputStream fStream = null;
		List<RegionalTaxRateConfiguration> regionalTaxRateList = new ArrayList<>();

		try {
			fStream = new FileInputStream(regionalTaxRateExcel);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fStream);
			XSSFSheet sheet = xssfWorkbook
					.getSheet("regionalTaxRateConfiguration");
			if (sheet == null) {
				throw new InventoryException(
						"Excel format is not correct. Excel must contain a sheet named carInventory");
			}
			Iterator rows = sheet.rowIterator();
			if (!rows.hasNext()) {
				throw new InventoryException("Excel does not contain any data.");
			}
			int inx = 0;
			int stateCell = -1, taxRateCell = -1;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				if (inx == 0) {
					Iterator cells = row.cellIterator();
					int cellCount = 0;
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						if (("state").equalsIgnoreCase(cell
								.getStringCellValue())) {
							stateCell = cellCount;
						} else if (("taxRate").equalsIgnoreCase(cell
								.getStringCellValue())) {
							taxRateCell = cellCount;
						}
						cellCount++;
					}
					if (stateCell == -1 || taxRateCell == -1) {
						throw new InventoryException(
								"excel does not contain the correct header/headers");
					}
					inx++;
					continue;
				}
				RegionalTaxRateConfiguration regionalTaxRateConfiguration = new RegionalTaxRateConfiguration();
				Cell stateCellVal = row.getCell(stateCell);
				if (!(stateCellVal == null || stateCellVal.getCellType() == stateCellVal.CELL_TYPE_BLANK)) {
					regionalTaxRateConfiguration.setState(row
							.getCell(stateCell).getStringCellValue());
				} else {
					continue;
				}
				Cell taxRateCellVal = row.getCell(taxRateCell);
				if (!(taxRateCellVal == null || taxRateCellVal.getCellType() == taxRateCellVal.CELL_TYPE_BLANK)) {
					regionalTaxRateConfiguration.setTaxRate((int) row.getCell(
							taxRateCell).getNumericCellValue());
				} else {
					continue;
				}

				regionalTaxRateList.add(regionalTaxRateConfiguration);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		carOrdersProcessing.setRegionalTaxRateList(regionalTaxRateList);
		System.out.println("Exiting readRegionalTaxRate");
		return carOrdersProcessing;

	}

	@Override
	public CarOrdersProcessing call() throws Exception {
		// TODO Auto-generated method stub
		CarOrdersProcessing regionalTaxRateList = readRegionalTaxRate();
		return regionalTaxRateList;
	}
}
