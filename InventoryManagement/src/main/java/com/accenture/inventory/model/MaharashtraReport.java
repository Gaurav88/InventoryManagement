package com.accenture.inventory.model;

/**
 * Wrapper model class for the Maharashtra Report.
 * @author g.t.gupta
 *
 */
public class MaharashtraReport implements Report {

	private int unitsales = 0;

	private double totalSale = 0;

	private double netIncome = 0;

	private String name;

	public int getUnitsales() {
		return unitsales;
	}

	public void setUnitsales(int unitsales) {
		this.unitsales = unitsales;
	}

	public double getTotalSale() {
		return totalSale;
	}

	public void setTotalSale(double totalSale) {
		this.totalSale = totalSale;
	}

	public double getNetIncome() {
		return netIncome;
	}

	public void setNetIncome(double netIncome) {
		this.netIncome = netIncome;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

}
