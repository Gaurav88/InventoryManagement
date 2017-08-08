package com.accenture.inventory.model;

/**
 * Wrapper model class for the Motor Insurance Provider Inventory.
 * @author g.t.gupta
 *
 */
public class MotorInsuranceProviderInventory {

	private String motorInsuranceProvider;

	private String personalProtectPlanOffered;

	private int firstYearPremium;

	public String getMotorInsuranceProvider() {
		return motorInsuranceProvider;
	}

	public void setMotorInsuranceProvider(String motorInsuranceProvider) {
		this.motorInsuranceProvider = motorInsuranceProvider;
	}

	public String getPersonalProtectPlanOffered() {
		return personalProtectPlanOffered;
	}

	public void setPersonalProtectPlanOffered(String personalProtectPlanOffered) {
		this.personalProtectPlanOffered = personalProtectPlanOffered;
	}

	public int getFirstYearPremium() {
		return firstYearPremium;
	}

	public void setFirstYearPremium(int firstYearPremium) {
		this.firstYearPremium = firstYearPremium;
	}

}
