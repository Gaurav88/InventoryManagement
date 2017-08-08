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
import com.accenture.inventory.model.CarInventory;
import com.accenture.inventory.utils.Settings;

/**
 * Used for processing of Car Inventory
 * @author g.t.gupta
 *
 */
public class CarInventoryManager implements Callable<CarOrdersProcessing> {

	CarOrdersProcessing carOrdersProcessing;

	public CarInventoryManager(CarOrdersProcessing carOrdersProcessing) {
		this.carOrdersProcessing = carOrdersProcessing;
	}

	/**
	 * Reading Car Inventory excel file
	 * @return CarOrdersProcessing
	 * @throws InventoryException
	 */
	public CarOrdersProcessing readCarInventory() throws InventoryException {

		System.out.println("Inside readCarInventory");
		File carInventoryExcel = new File(Settings.CARINVENTORY_EXCELPATH);
		FileInputStream fStream = null;
		List<CarInventory> carInventoryList = new ArrayList<>();

		try {
			fStream = new FileInputStream(carInventoryExcel);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fStream);
			XSSFSheet sheet = xssfWorkbook.getSheet("carInventory");
			if (sheet == null) {
				throw new InventoryException(
						"Excel format is not correct. Excel must contain a sheet named carInventory");
			}
			Iterator rows = sheet.rowIterator();
			if (!rows.hasNext()) {
				throw new InventoryException("Excel does not contain any data.");
			}
			int inx = 0;
			int vendorCell = -1, modelCell = -1, variantCell = -1, colorCell = -1, basePriceCell = -1, quantityAvailableCell = -1;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				if (inx == 0) {
					Iterator cells = row.cellIterator();
					int cellCount = 0;
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						if (("vendor").equalsIgnoreCase(cell
								.getStringCellValue())) {
							vendorCell = cellCount;
						} else if (("model").equalsIgnoreCase(cell
								.getStringCellValue())) {
							modelCell = cellCount;
						} else if (("variant").equalsIgnoreCase(cell
								.getStringCellValue())) {
							variantCell = cellCount;
						} else if (("color").equalsIgnoreCase(cell
								.getStringCellValue())) {
							colorCell = cellCount;
						} else if (("basePrice").equalsIgnoreCase(cell
								.getStringCellValue())) {
							basePriceCell = cellCount;
						} else if (("quantityAvailable").equalsIgnoreCase(cell
								.getStringCellValue())) {
							quantityAvailableCell = cellCount;
						}
						cellCount++;
					}
					if (vendorCell == -1 || modelCell == -1
							|| variantCell == -1 || colorCell == -1
							|| basePriceCell == -1
							|| quantityAvailableCell == -1) {
						throw new InventoryException(
								"excel does not contain the correct header/headers");
					}
					inx++;
					continue;
				}
				CarInventory carInventory = new CarInventory();
				Cell vendoreCellVal = row.getCell(vendorCell);
				if (!(vendoreCellVal == null || vendoreCellVal.getCellType() == vendoreCellVal.CELL_TYPE_BLANK)) {
					carInventory.setVendor(row.getCell(vendorCell)
							.getStringCellValue());
				} else {
					continue;
				}
				Cell modelCellVal = row.getCell(modelCell);
				if (!(modelCellVal == null || modelCellVal.getCellType() == modelCellVal.CELL_TYPE_BLANK)) {
					carInventory.setModel(row.getCell(modelCell)
							.getStringCellValue());
				} else {
					continue;
				}
				Cell variantCellVal = row.getCell(variantCell);
				if (!(variantCellVal == null || variantCellVal.getCellType() == variantCellVal.CELL_TYPE_BLANK)) {
					carInventory.setVariant(row.getCell(variantCell)
							.getStringCellValue());
				} else {
					continue;
				}
				Cell colorCellVal = row.getCell(colorCell);
				if (!(colorCellVal == null || colorCellVal.getCellType() == colorCellVal.CELL_TYPE_BLANK)) {
					carInventory.setColor(row.getCell(colorCell)
							.getStringCellValue());
				} else {
					continue;
				}
				Cell basePriceCellVal = row.getCell(basePriceCell);
				if (!(basePriceCellVal == null || basePriceCellVal
						.getCellType() == basePriceCellVal.CELL_TYPE_BLANK)) {
					carInventory.setBasePrice((int) row.getCell(basePriceCell)
							.getNumericCellValue());
				} else {
					continue;
				}
				Cell quantityAvailableVal = row.getCell(quantityAvailableCell);
				if (!(quantityAvailableVal == null || quantityAvailableVal
						.getCellType() == quantityAvailableVal.CELL_TYPE_BLANK)) {
					carInventory.setQuantityAvailable((int) row.getCell(
							quantityAvailableCell).getNumericCellValue());
				} else {
					continue;
				}

				carInventoryList.add(carInventory);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		carOrdersProcessing.setCarInventoryList(carInventoryList);
		System.out.println("Exiting readCarInventory");
		return carOrdersProcessing;
	}

	@Override
	public CarOrdersProcessing call() throws Exception {
		// TODO Auto-generated method stub
		CarOrdersProcessing carInventoryList = readCarInventory();
		return carInventoryList;
	}

}
