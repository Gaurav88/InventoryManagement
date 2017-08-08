package com.accenture.inventory.model;

public interface Report {

	public void setUnitsales(int unitsales);

	public int getUnitsales();

	public double getTotalSale();

	public void setTotalSale(double totalSale);

	public double getNetIncome();

	public void setNetIncome(double netIncome);

	public String getName();

	public void setName(String name);

}
