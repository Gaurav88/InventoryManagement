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
import com.accenture.inventory.model.MotorInsuranceProviderInventory;
import com.accenture.inventory.utils.Settings;

/**
 * Used for processing of Motor Insurance
 * @author g.t.gupta
 *
 */
public class MotorInsuranceProviderManager implements
		Callable<CarOrdersProcessing> {

	CarOrdersProcessing carOrdersProcessing;

	public MotorInsuranceProviderManager(CarOrdersProcessing carOrdersProcessing) {
		this.carOrdersProcessing = carOrdersProcessing;
	}

	/**
	 * Reading Motor Insurance Provider excel file
	 * @return CarOrdersProcessing
	 * @throws InventoryException
	 */
	public CarOrdersProcessing readMotorInsurance() throws InventoryException {

		System.out.println("Inside readMotorInsurance");
		File motorInsuranceExcel = new File(
				Settings.INSURANCEINVENTORY_EXCELPATH);
		FileInputStream fStream = null;
		List<MotorInsuranceProviderInventory> motorInsuranceProviderList = new ArrayList<>();

		try {
			fStream = new FileInputStream(motorInsuranceExcel);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fStream);
			XSSFSheet sheet = xssfWorkbook
					.getSheet("motorInsuranceProviderInventory");
			if (sheet == null) {
				throw new InventoryException(
						"Excel format is not correct. Excel must contain a sheet named carInventory");
			}
			Iterator rows = sheet.rowIterator();
			if (!rows.hasNext()) {
				throw new InventoryException("Excel does not contain any data.");
			}
			int inx = 0;
			int motorInsuranceProviderCell = -1, personalProtectPlanOfferedCell = -1, firstYearPremiumCell = -1;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				if (inx == 0) {
					Iterator cells = row.cellIterator();
					int cellCount = 0;
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						if (("motorInsuranceProvider").equalsIgnoreCase(cell
								.getStringCellValue())) {
							motorInsuranceProviderCell = cellCount;
						} else if (("personalProtectPlanOffered")
								.equalsIgnoreCase(cell.getStringCellValue())) {
							personalProtectPlanOfferedCell = cellCount;
						} else if (("firstYearPremium").equalsIgnoreCase(cell
								.getStringCellValue())) {
							firstYearPremiumCell = cellCount;
						}
						cellCount++;
					}
					if (motorInsuranceProviderCell == -1
							|| personalProtectPlanOfferedCell == -1
							|| firstYearPremiumCell == -1) {
						throw new InventoryException(
								"excel does not contain the correct header/headers");
					}
					inx++;
					continue;
				}
				MotorInsuranceProviderInventory motorInsuranceProviderInventory = new MotorInsuranceProviderInventory();
				Cell motorInsuranceProviderCellVal = row
						.getCell(motorInsuranceProviderCell);
				if (!(motorInsuranceProviderCellVal == null || motorInsuranceProviderCellVal
						.getCellType() == motorInsuranceProviderCellVal.CELL_TYPE_BLANK)) {
					motorInsuranceProviderInventory
							.setMotorInsuranceProvider(row.getCell(
									motorInsuranceProviderCell)
									.getStringCellValue());
				} else {
					continue;
				}
				Cell personalProtectPlanOfferedCellVal = row
						.getCell(personalProtectPlanOfferedCell);
				if (!(personalProtectPlanOfferedCellVal == null || personalProtectPlanOfferedCellVal
						.getCellType() == personalProtectPlanOfferedCellVal.CELL_TYPE_BLANK)) {
					motorInsuranceProviderInventory
							.setPersonalProtectPlanOffered(row.getCell(
									personalProtectPlanOfferedCell)
									.getStringCellValue());
				} else {
					continue;
				}
				Cell firstYearPremiumCellVal = row
						.getCell(firstYearPremiumCell);
				if (!(firstYearPremiumCellVal == null || firstYearPremiumCellVal
						.getCellType() == firstYearPremiumCellVal.CELL_TYPE_BLANK)) {
					motorInsuranceProviderInventory
							.setFirstYearPremium((int) row.getCell(
									firstYearPremiumCell).getNumericCellValue());
				} else {
					continue;
				}

				motorInsuranceProviderList.add(motorInsuranceProviderInventory);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		carOrdersProcessing
				.setMotorInsuranceProviderList(motorInsuranceProviderList);
		System.out.println("Exiting readMotorInsurance");
		return carOrdersProcessing;
	}

	@Override
	public CarOrdersProcessing call() throws Exception {
		// TODO Auto-generated method stub
		CarOrdersProcessing motorInsuranceProviderList = readMotorInsurance();
		return motorInsuranceProviderList;
	}
}
