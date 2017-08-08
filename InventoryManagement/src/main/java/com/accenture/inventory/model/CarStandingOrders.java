package com.accenture.inventory.model;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import org.apache.poi.xssf.usermodel.XSSFRow;

import com.accenture.inventory.exception.InventoryException;
import com.accenture.inventory.manager.CarOrdersProcessing;
import com.accenture.inventory.manager.CarStandingOrdersManager;

/**
 * Wrapper model class for the Car Standing Orders.
 * @author g.t.gupta
 *
 */
public class CarStandingOrders extends RecursiveTask<CarStandingOrders> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -936966851595010625L;
	private XSSFRow rows;
	CarOrdersProcessing carOrdersProcessing;

	public CarStandingOrders(XSSFRow rows,
			CarOrdersProcessing carOrdersProcessing) {
		this.rows = rows;
		this.carOrdersProcessing = carOrdersProcessing;
	}

	public CarStandingOrders() {

	}

	private String customerName;

	private String region;

	private String vendor;

	private String model;

	private String variant;

	private String color;

	private List<String> accessories;

	private String motorInsurance;

	private String personalProtectPlan;

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getVariant() {
		return variant;
	}

	public void setVariant(String variant) {
		this.variant = variant;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<String> getAccessories() {
		return accessories;
	}

	public void setAccessories(List<String> accessories) {
		this.accessories = accessories;
	}

	public String getMotorInsurance() {
		return motorInsurance;
	}

	public void setMotorInsurance(String motorInsurance) {
		this.motorInsurance = motorInsurance;
	}

	public String getPersonalProtectPlan() {
		return personalProtectPlan;
	}

	public void setPersonalProtectPlan(String personalProtectPlan) {
		this.personalProtectPlan = personalProtectPlan;
	}

	@Override
	protected CarStandingOrders compute() {
		
		CarStandingOrders carStandingOrders = null;
		try {
			carStandingOrders = new CarStandingOrdersManager()
					.readCarStandingOrders(rows, carOrdersProcessing);
		} catch (InventoryException e) {
			System.out.println(e.getMessage());
		}
		return carStandingOrders;
	}

	public String toString() {
		return "CustomerName: '" + this.customerName + "', region: '"
				+ this.region + "', vendor: '" + this.vendor + "'"
				+ "', model: '" + this.model + "'";
	}
}
