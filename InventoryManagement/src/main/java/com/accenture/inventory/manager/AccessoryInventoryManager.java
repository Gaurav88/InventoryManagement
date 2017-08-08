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
import com.accenture.inventory.model.AccessoryInventory;
import com.accenture.inventory.utils.Settings;

/**
 * Used for processing of Accessories
 * @author g.t.gupta
 *
 */
public class AccessoryInventoryManager implements Callable<CarOrdersProcessing> {

	CarOrdersProcessing carOrdersProcessing;

	public AccessoryInventoryManager(CarOrdersProcessing carOrdersProcessing) {
		this.carOrdersProcessing = carOrdersProcessing;
	}

	/**
	 * Reading Accessory Inventory excel file
	 * @return CarOrdersProcessing
	 * @throws InventoryException
	 */
	public CarOrdersProcessing readAccessoryInventory()
			throws InventoryException {

		System.out.println("Inside readAccessoryInventory");
		File accessoryInventoryExcel = new File(
				Settings.ACCESSORYINVENTORY_EXCELPATH);
		FileInputStream fStream = null;
		List<AccessoryInventory> accessoryInventoryList = new ArrayList<>();

		try {
			fStream = new FileInputStream(accessoryInventoryExcel);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fStream);
			XSSFSheet sheet = xssfWorkbook.getSheet("accessoryInventory");
			if (sheet == null) {
				throw new InventoryException(
						"Excel format is not correct. Excel must contain a sheet named carInventory");
			}
			Iterator rows = sheet.rowIterator();
			if (!rows.hasNext()) {
				throw new InventoryException("Excel does not contain any data.");
			}
			int inx = 0;
			int vendoreCell = -1, modelCell = -1, accessoriesCell = -1, priceCell = -1, quantityAvailableCell = -1;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				if (inx == 0) {
					Iterator cells = row.cellIterator();
					int cellCount = 0;
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						if (("vendor").equalsIgnoreCase(cell
								.getStringCellValue())) {
							vendoreCell = cellCount;
						} else if (("model").equalsIgnoreCase(cell
								.getStringCellValue())) {
							modelCell = cellCount;
						} else if (("accessories").equalsIgnoreCase(cell
								.getStringCellValue())) {
							accessoriesCell = cellCount;
						} else if (("price").equalsIgnoreCase(cell
								.getStringCellValue())) {
							priceCell = cellCount;
						} else if (("quantityAvailable").equalsIgnoreCase(cell
								.getStringCellValue())) {
							quantityAvailableCell = cellCount;
						}
						cellCount++;
					}
					if (vendoreCell == -1 || modelCell == -1
							|| accessoriesCell == -1 || priceCell == -1
							|| quantityAvailableCell == -1) {
						throw new InventoryException(
								"excel does not contain the correct header/headers");
					}
					inx++;
					continue;
				}
				AccessoryInventory carInventory = new AccessoryInventory();
				Cell vendoreCellVal = row.getCell(vendoreCell);
				if (!(vendoreCellVal == null || vendoreCellVal.getCellType() == vendoreCellVal.CELL_TYPE_BLANK)) {
					carInventory.setVendor(row.getCell(vendoreCell)
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
				Cell variantCellVal = row.getCell(accessoriesCell);
				if (!(variantCellVal == null || variantCellVal.getCellType() == variantCellVal.CELL_TYPE_BLANK)) {
					carInventory.setAccessories(row.getCell(accessoriesCell)
							.getStringCellValue());
				} else {
					continue;
				}
				Cell basePriceCellVal = row.getCell(priceCell);
				if (!(basePriceCellVal == null || basePriceCellVal
						.getCellType() == basePriceCellVal.CELL_TYPE_BLANK)) {
					carInventory.setPrice((int) row.getCell(priceCell)
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

				accessoryInventoryList.add(carInventory);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		carOrdersProcessing.setAccessoryInventoryList(accessoryInventoryList);
		System.out.println("Exiting readAccessoryInventory");
		return carOrdersProcessing;
	}

	@Override
	public CarOrdersProcessing call() throws Exception {
		// TODO Auto-generated method stub
		CarOrdersProcessing accessoryInventoryList = readAccessoryInventory();
		return accessoryInventoryList;
	}

}
