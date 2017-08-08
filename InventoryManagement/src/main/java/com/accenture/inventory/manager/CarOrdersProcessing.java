package com.accenture.inventory.manager;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.accenture.inventory.model.AccessoryInventory;
import com.accenture.inventory.model.CarInventory;
import com.accenture.inventory.model.CarStandingOrders;
import com.accenture.inventory.model.GoaReport;
import com.accenture.inventory.model.GujratReport;
import com.accenture.inventory.model.KarnatakaReport;
import com.accenture.inventory.model.MPReport;
import com.accenture.inventory.model.MaharashtraReport;
import com.accenture.inventory.model.MotorInsuranceProviderInventory;
import com.accenture.inventory.model.RegionalTaxRateConfiguration;
import com.accenture.inventory.model.Report;
import com.accenture.inventory.utils.Settings;

/**
 * Used for processing of all the order
 * placed by various cutomers
 * @author g.t.gupta
 *
 */
public class CarOrdersProcessing {

	static Map<CarStandingOrders, String> errorData = new HashMap<>();
	static Set<Report> regionalEstimatedSales = new HashSet<Report>();
	static Report gujratReport = new GujratReport();
	static Report karnatakaReport = new KarnatakaReport();
	static Report mpReport = new MPReport();
	static Report goaReport = new GoaReport();
	static Report maharashtraReport = new MaharashtraReport();

	String errorMessage;
	private List<AccessoryInventory> accessoryInventoryList;
	private List<CarInventory> carInventoryList;
	private List<MotorInsuranceProviderInventory> motorInsuranceProviderList;
	private List<RegionalTaxRateConfiguration> regionalTaxRateList;
	private HSSFWorkbook workbook;
	private HSSFWorkbook workbook2;

	public List<AccessoryInventory> getAccessoryInventoryList() {
		return accessoryInventoryList;
	}

	public void setAccessoryInventoryList(
			List<AccessoryInventory> accessoryInventoryList) {
		this.accessoryInventoryList = accessoryInventoryList;
	}

	public List<CarInventory> getCarInventoryList() {
		return carInventoryList;
	}

	public void setCarInventoryList(List<CarInventory> carInventoryList) {
		this.carInventoryList = carInventoryList;
	}

	public List<MotorInsuranceProviderInventory> getMotorInsuranceProviderList() {
		return motorInsuranceProviderList;
	}

	public void setMotorInsuranceProviderList(
			List<MotorInsuranceProviderInventory> motorInsuranceProviderList) {
		this.motorInsuranceProviderList = motorInsuranceProviderList;
	}

	public List<RegionalTaxRateConfiguration> getRegionalTaxRateList() {
		return regionalTaxRateList;
	}

	public void setRegionalTaxRateList(
			List<RegionalTaxRateConfiguration> regionalTaxRateList) {
		this.regionalTaxRateList = regionalTaxRateList;
	}

	public void orderProcessing(CarStandingOrders carStandingOrders,
			CarOrdersProcessing carOrdersProcessing) {

		double taxRate = 0, basePrice = 0, accessoryPrice = 0;
		double totalPrice = 0;

		int insuranceprice = motorInsurance(carStandingOrders,
				carOrdersProcessing);
		if (insuranceprice >= 0) {
			taxRate = calculateTaxRate(carStandingOrders, carOrdersProcessing);
			if (taxRate >= 0) {
				basePrice = carBasePrice(carStandingOrders, carOrdersProcessing);
				if (basePrice >= 0) {
					if (carStandingOrders.getAccessories() != null) {
						accessoryPrice = accessoryPrice(carStandingOrders,
								carOrdersProcessing);
						if (accessoryPrice == -1) {
							errorData.put(carStandingOrders, errorMessage);
						}

					} else {
						accessoryPrice = 0;
					}
				} else {
					errorData.put(carStandingOrders, errorMessage);
				}
			} else {
				errorData.put(carStandingOrders, errorMessage);
			}
		} else {
			errorData.put(carStandingOrders, errorMessage);
		}

		if (taxRate >= 0 && basePrice >= 0 && accessoryPrice >= 0) {
			double taxExpense = (basePrice + accessoryPrice) * taxRate / 100;
			totalPrice = basePrice + accessoryPrice + taxExpense;
			System.out.println(carStandingOrders.toString() + ":- Total Price : "
					+ totalPrice);
			double totalEstSales = totalPrice + insuranceprice;
			double netIncome = totalEstSales - taxExpense;
			reports.reportData(carStandingOrders, totalEstSales, netIncome);
		}

	}

	/**
	 * Processing Motor Insurance for every Customer
	 * @param carStandingOrders
	 * @param CarOrdersProcessing
	 * @return motorInsurance
	 */
	public int motorInsurance(CarStandingOrders carStandingOrders,
			CarOrdersProcessing CarOrdersProcessing) {

		if (("NA").equals(carStandingOrders.getMotorInsurance())) {
			return 0;
		}

		for (MotorInsuranceProviderInventory motorInsuranceProvider : CarOrdersProcessing
				.getMotorInsuranceProviderList()) {
			if (carStandingOrders.getMotorInsurance().equals(
					motorInsuranceProvider.getMotorInsuranceProvider())
					&& carStandingOrders.getPersonalProtectPlan().equals(
							motorInsuranceProvider
									.getPersonalProtectPlanOffered())) {
				return motorInsuranceProvider.getFirstYearPremium();
			} else {
				continue;
			}

		}
		errorMessage = "Motor insurance wrong";
		return -1;
	}

	/**
	 * Processing Tax Rate for every Customer in accordance to state
	 * @param carStandingOrders
	 * @param carOrdersProcessing
	 * @return taxRate
	 */
	public int calculateTaxRate(CarStandingOrders carStandingOrders,
			CarOrdersProcessing carOrdersProcessing) {
		for (RegionalTaxRateConfiguration regionalTaxRateConfiguration : carOrdersProcessing
				.getRegionalTaxRateList()) {
			if (carStandingOrders.getRegion().equals(
					regionalTaxRateConfiguration.getState())) {
				return regionalTaxRateConfiguration.getTaxRate();
			} else {
				continue;
			}

		}
		errorMessage = "Region not present";
		return -1;
	}

	/**
	 * Processing base price of car for every Customer
	 * @param carStandingOrders
	 * @param carOrdersProcessing
	 * @return carBasePrice
	 */
	public int carBasePrice(CarStandingOrders carStandingOrders,
			CarOrdersProcessing carOrdersProcessing) {
		for (CarInventory carInventory : carOrdersProcessing
				.getCarInventoryList()) {
			if (carStandingOrders.getVendor().equals(carInventory.getVendor())
					&& carStandingOrders.getModel().equals(
							carInventory.getModel())
					&& carStandingOrders.getVariant().equals(
							carInventory.getVariant())
					&& carStandingOrders.getColor().equals(
							carInventory.getColor())) {
				if ((carInventory.getQuantityAvailable() - 1) >= 0) {
					carInventory.setQuantityAvailable(carInventory
							.getQuantityAvailable() - 1);
					return carInventory.getBasePrice();
				} else {
					errorMessage = "Car Inventory unavailable";
					return -1;
				}
			} else {
				continue;
			}

		}
		errorMessage = "Mismatch with Car Inventory Available";
		return -1;
	}

	/**
	 * Processing various accessories for every Customer
	 * @param carStandingOrders
	 * @param carOrdersProcessing
	 * @return accessoryPrice
	 */
	public int accessoryPrice(CarStandingOrders carStandingOrders,
			CarOrdersProcessing carOrdersProcessing) {
		int countAccessory = 0, price = 0;
		for (AccessoryInventory accessoryInventory : carOrdersProcessing
				.getAccessoryInventoryList()) {
			if (carStandingOrders.getAccessories().contains(
					accessoryInventory.getAccessories())
					&& carStandingOrders.getVendor().equals(
							accessoryInventory.getVendor())
					&& carStandingOrders.getModel().equals(
							accessoryInventory.getModel())) {

				if ((accessoryInventory.getQuantityAvailable() - 1) >= 0) {
					accessoryInventory.setQuantityAvailable(accessoryInventory
							.getQuantityAvailable() - 1);
					price += accessoryInventory.getPrice();
					countAccessory++;
				} else {
					errorMessage = "Accessary unavailable";
					return -1;
				}

			} else {
				continue;
			}

		}
		if (carStandingOrders.getAccessories().size() == countAccessory) {
			return price;
		} else {
			errorMessage = "Mismatch with Accesories Inventory Available";
			return -1;
		}
	}

	/**
	 * Lmbda expression for creation of reports
	 * in accordance to various states
	 * Report includes Unit Sales, Total Sale and Net Income
	 */
	Reports reports = (CarStandingOrders carStandingOrders, double totalEstSales,
			double netIncome) -> {
		switch (carStandingOrders.getRegion()) {
		case "Gujarat":
			gujratReport.setName(carStandingOrders.getRegion());
			gujratReport.setUnitsales(gujratReport.getUnitsales() + 1);
			gujratReport.setTotalSale(gujratReport.getTotalSale()
					+ totalEstSales);
			gujratReport.setNetIncome(gujratReport.getNetIncome() + netIncome);
			regionalEstimatedSales.add(gujratReport);
			break;
		case "Maharashtra":
			maharashtraReport.setName(carStandingOrders.getRegion());
			maharashtraReport
					.setUnitsales(maharashtraReport.getUnitsales() + 1);
			maharashtraReport.setTotalSale(maharashtraReport.getTotalSale()
					+ totalEstSales);
			maharashtraReport.setNetIncome(maharashtraReport.getNetIncome()
					+ netIncome);
			regionalEstimatedSales.add(maharashtraReport);
			break;
		case "Karnataka":
			karnatakaReport.setName(carStandingOrders.getRegion());
			karnatakaReport.setUnitsales(karnatakaReport.getUnitsales() + 1);
			karnatakaReport.setTotalSale(karnatakaReport.getTotalSale()
					+ totalEstSales);
			karnatakaReport.setNetIncome(karnatakaReport.getNetIncome()
					+ netIncome);
			regionalEstimatedSales.add(karnatakaReport);
			break;
		case "Madhya Pradesh":
			mpReport.setName(carStandingOrders.getRegion());
			mpReport.setUnitsales(mpReport.getUnitsales() + 1);
			mpReport.setTotalSale(mpReport.getTotalSale() + totalEstSales);
			mpReport.setNetIncome(mpReport.getNetIncome() + netIncome);
			regionalEstimatedSales.add(mpReport);
			break;
		case "Goa":
			goaReport.setName(carStandingOrders.getRegion());
			goaReport.setUnitsales(goaReport.getUnitsales() + 1);
			goaReport.setTotalSale(goaReport.getTotalSale() + totalEstSales);
			goaReport.setNetIncome(goaReport.getNetIncome() + netIncome);
			regionalEstimatedSales.add(goaReport);
			break;
		default:
			break;
		}
	};

	/**
	 * Generates excel for the records failed during
	 * processing of orders
	 * @throws IOException
	 */
	public void errorCSV() throws IOException {

		String filename = Settings.ERROR_EXCELPATH;
		workbook2 = new HSSFWorkbook();
		HSSFSheet sheet = workbook2.createSheet("CarStandingOrdersError");
		int rownum = 0;
		HSSFRow rowhead = sheet.createRow(rownum++);
		rowhead.createCell(0).setCellValue("customer Name");
		rowhead.createCell(1).setCellValue("Region");
		rowhead.createCell(2).setCellValue("Vendor");
		rowhead.createCell(3).setCellValue("Model");
		rowhead.createCell(4).setCellValue("Variant");
		rowhead.createCell(5).setCellValue("Color");
		rowhead.createCell(6).setCellValue("accessories");
		rowhead.createCell(7).setCellValue("motorInsurance");
		rowhead.createCell(8).setCellValue("personalProtectPlan");
		rowhead.createCell(9).setCellValue("Error Message");

		for (CarStandingOrders carStandingOrders : errorData.keySet()) {
			HSSFRow rowData = sheet.createRow(rownum++);
			rowData.createCell(0).setCellValue(
					carStandingOrders.getCustomerName() + "");
			rowData.createCell(1).setCellValue(
					carStandingOrders.getRegion() + "");
			rowData.createCell(2).setCellValue(
					carStandingOrders.getVendor() + "");
			rowData.createCell(3).setCellValue(
					carStandingOrders.getModel() + "");
			rowData.createCell(4).setCellValue(
					carStandingOrders.getVariant() + "");
			rowData.createCell(5).setCellValue(
					carStandingOrders.getColor() + "");
			rowData.createCell(6).setCellValue(
					carStandingOrders.getAccessories().toString() + "");
			rowData.createCell(7).setCellValue(
					carStandingOrders.getMotorInsurance() + "");
			rowData.createCell(8).setCellValue(
					carStandingOrders.getPersonalProtectPlan() + "");
			rowData.createCell(9).setCellValue(
					errorData.get(carStandingOrders) + "");
		}

		FileOutputStream fileOut = new FileOutputStream(filename);
		workbook2.write(fileOut);
		fileOut.close();
		System.out.println("Your excel file has been generated!");

	}

	/**
	 * Generates report excel in accordance to various states. 
	 * Report includes Unit Sales, Total Sale and Net Income
	 * @throws IOException
	 */
	public void regionalReportGeneration() throws IOException {

		String filename = Settings.REPORT_EXCELPATH;
		workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("RegionalEstimatedSalesReport");
		int rownum = 0;
		HSSFRow rowhead = sheet.createRow(rownum++);
		rowhead.createCell(0).setCellValue("State ");
		rowhead.createCell(1).setCellValue("Estimated Sales in Units ");
		rowhead.createCell(2).setCellValue("Total Estimated Sales ");
		rowhead.createCell(3).setCellValue("Estimated Net Income ");

		for (Report report : regionalEstimatedSales) {
			HSSFRow rowData = sheet.createRow(rownum++);
			rowData.createCell(0).setCellValue(report.getName() + "");
			rowData.createCell(1).setCellValue(report.getUnitsales() + "");
			rowData.createCell(2).setCellValue(report.getTotalSale() + "");
			rowData.createCell(3).setCellValue(report.getNetIncome() + "");
		}

		FileOutputStream fileOut = new FileOutputStream(filename);
		workbook.write(fileOut);
		fileOut.close();
		System.out.println("Your excel file has been generated!");

	}

	@FunctionalInterface
	public interface Reports {

		public void reportData(CarStandingOrders carStandingOrders,
				double totalEstSales, double netIncome);

	}

}
