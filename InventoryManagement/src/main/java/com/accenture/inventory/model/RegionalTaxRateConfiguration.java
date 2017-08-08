package com.accenture.inventory.model;

/**
 * Wrapper model class for the Regional Tax Rate Configuration.
 * @author g.t.gupta
 *
 */
public class RegionalTaxRateConfiguration {

	private String state;

	private int taxRate;

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(int taxRate) {
		this.taxRate = taxRate;
	}
}
