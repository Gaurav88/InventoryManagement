package com.accenture.inventory.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.accenture.inventory.exception.InventoryException;
import com.accenture.inventory.model.CarStandingOrders;
import com.accenture.inventory.utils.Settings;

/**
 * Used for processing of Car orders processing
 * @author g.t.gupta
 *
 */
public class CarStandingOrdersManager extends
		RecursiveTask<List<CarStandingOrders>> {

	private static final long serialVersionUID = 8938940715215828480L;
	static int customerNameCell = -1, regionCell = -1, vendorCell = -1,
			modelCell = -1, variantCell = -1, colorCell = -1,
			accessoriesCell = -1, motorInsuranceCell = -1,
			personalProtectPlanCell = -1;
	CarOrdersProcessing carOrdersProcessing;

	public CarStandingOrdersManager(CarOrdersProcessing carOrdersProcessing) {
		this.carOrdersProcessing = carOrdersProcessing;
	}

	public CarStandingOrdersManager() {

	}

	/**
	 * Reading Car standing orders excel file
	 * @param row
	 * @param carOrdersProcessing
	 * @return
	 * @throws InventoryException
	 */
	public CarStandingOrders readCarStandingOrders(XSSFRow row,
			CarOrdersProcessing carOrdersProcessing) throws InventoryException {

		// System.out.println("Inside "+Thread.currentThread().getName());

		CarStandingOrders carStandingOrders = new CarStandingOrders(row,
				carOrdersProcessing);
		Cell customerNameeCellVal = row.getCell(customerNameCell);
		if (!(customerNameeCellVal == null || customerNameeCellVal
				.getCellType() == customerNameeCellVal.CELL_TYPE_BLANK)) {
			carStandingOrders.setCustomerName(row.getCell(customerNameCell)
					.getStringCellValue());
		} else {
			carStandingOrders.setCustomerName("");
		}
		Cell regionCellVal = row.getCell(regionCell);
		if (!(regionCellVal == null || regionCellVal.getCellType() == regionCellVal.CELL_TYPE_BLANK)) {
			carStandingOrders.setRegion(row.getCell(regionCell)
					.getStringCellValue());
		} else {
			carStandingOrders.setCustomerName("");
		}
		Cell vendorCellVal = row.getCell(vendorCell);
		if (!(vendorCellVal == null || vendorCellVal.getCellType() == vendorCellVal.CELL_TYPE_BLANK)) {
			carStandingOrders.setVendor(row.getCell(vendorCell)
					.getStringCellValue());
		} else {
			carStandingOrders.setCustomerName("");
		}
		Cell modelCellVal = row.getCell(modelCell);
		if (!(modelCellVal == null || modelCellVal.getCellType() == modelCellVal.CELL_TYPE_BLANK)) {
			carStandingOrders.setModel(row.getCell(modelCell)
					.getStringCellValue());
		} else {
			carStandingOrders.setCustomerName("");
		}
		Cell variantCellVal = row.getCell(variantCell);
		if (!(variantCellVal == null || variantCellVal.getCellType() == variantCellVal.CELL_TYPE_BLANK)) {
			carStandingOrders.setVariant(row.getCell(variantCell)
					.getStringCellValue());
		} else {
			carStandingOrders.setCustomerName("");
		}
		Cell colorVal = row.getCell(colorCell);
		if (!(colorVal == null || colorVal.getCellType() == colorVal.CELL_TYPE_BLANK)) {
			carStandingOrders.setColor(row.getCell(colorCell)
					.getStringCellValue());
		} else {
			carStandingOrders.setCustomerName("");
		}
		Cell accessoriesCellVal = row.getCell(accessoriesCell);
		if (!(accessoriesCellVal == null || accessoriesCellVal.getCellType() == accessoriesCellVal.CELL_TYPE_BLANK)) {
			String accessories = row.getCell(accessoriesCell)
					.getStringCellValue();
			carStandingOrders.setAccessories(Arrays.asList(accessories
					.split(": ")));
		} else {
			carStandingOrders.setAccessories(null);
		}
		Cell motorInsuranceCellVal = row.getCell(motorInsuranceCell);
		if (!(motorInsuranceCellVal == null || motorInsuranceCellVal
				.getCellType() == motorInsuranceCellVal.CELL_TYPE_BLANK)) {
			carStandingOrders.setMotorInsurance(row.getCell(motorInsuranceCell)
					.getStringCellValue());
		} else {
			carStandingOrders.setCustomerName("");
		}
		Cell personalProtectPlanCellVal = row.getCell(personalProtectPlanCell);
		if (!(personalProtectPlanCellVal == null || personalProtectPlanCellVal
				.getCellType() == personalProtectPlanCellVal.CELL_TYPE_BLANK)) {
			carStandingOrders.setPersonalProtectPlan(row.getCell(
					personalProtectPlanCell).getStringCellValue());
		} else {
			carStandingOrders.setCustomerName("");
		}

		new CarOrdersProcessing().orderProcessing(carStandingOrders,
				carOrdersProcessing);
		// System.out.println("Exit "+Thread.currentThread().getName());
		return carStandingOrders;
	}

	@Override
	protected List<CarStandingOrders> compute() {
		// TODO Auto-generated method stub
		System.out.println("Inside readCarStandingOrders");
		File carInventoryExcel = new File(Settings.CARORDERS_EXCELPATH);
		FileInputStream fStream = null;
		List<CarStandingOrders> carStandingOrdersList = new ArrayList<>();
		List<RecursiveTask<CarStandingOrders>> forks = new LinkedList<>();
		try {
			fStream = new FileInputStream(carInventoryExcel);
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fStream);
			XSSFSheet sheet = xssfWorkbook.getSheet("carStandingOrders");
			if (sheet == null) {
				throw new InventoryException(
						"Excel format is not correct. Excel must contain a sheet named carInventory");
			}
			Iterator rows = sheet.rowIterator();
			if (!rows.hasNext()) {
				throw new InventoryException("Excel does not contain any data.");
			}
			int inx = 0;
			while (rows.hasNext()) {
				XSSFRow row = (XSSFRow) rows.next();
				if (inx == 0) {
					Iterator cells = row.cellIterator();
					int cellCount = 0;
					while (cells.hasNext()) {
						XSSFCell cell = (XSSFCell) cells.next();
						if (("customerName").equalsIgnoreCase(cell
								.getStringCellValue())) {
							customerNameCell = cellCount;
						} else if (("region").equalsIgnoreCase(cell
								.getStringCellValue())) {
							regionCell = cellCount;
						} else if (("vendor").equalsIgnoreCase(cell
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
						} else if (("accessories").equalsIgnoreCase(cell
								.getStringCellValue())) {
							accessoriesCell = cellCount;
						} else if (("motorInsurance").equalsIgnoreCase(cell
								.getStringCellValue())) {
							motorInsuranceCell = cellCount;
						} else if (("personalProtectPlan")
								.equalsIgnoreCase(cell.getStringCellValue())) {
							personalProtectPlanCell = cellCount;
						}
						cellCount++;
					}
					if (customerNameCell == -1 || regionCell == -1
							|| vendorCell == -1 || modelCell == -1
							|| variantCell == -1 || colorCell == -1
							|| accessoriesCell == -1
							|| motorInsuranceCell == -1
							|| personalProtectPlanCell == -1) {
						throw new InventoryException(
								"excel does not contain the correct header/headers");
					}
					inx++;
					continue;
				}
				CarStandingOrders carStandingOrders = new CarStandingOrders(
						row, carOrdersProcessing);
				forks.add(carStandingOrders);
				carStandingOrders.fork();
			}
		} catch (IOException | InventoryException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
		for (RecursiveTask<CarStandingOrders> task : forks) {
			carStandingOrdersList.add(task.join());
		}
		System.out.println("Exiting readCarStandingOrders");
		return carStandingOrdersList;
	}
}
