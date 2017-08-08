package com.accenture.inventory.manager;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.junit.Test;

import com.accenture.inventory.exception.InventoryException;
import com.accenture.inventory.model.CarStandingOrders;

/**
 * Junit test class for Inventory managemenet
 * @author g.t.gupta
 *
 */
public class CarInventoryManagerTest {

	private final ForkJoinPool forkJoinPool = new ForkJoinPool();
	 CarOrdersProcessing carOrdersProcessing;
	 
	 public  CarInventoryManagerTest() {
		 carOrdersProcessing = new CarOrdersProcessing();
	}

	@Test
	public void readCarInventoryTest() {

		CarInventoryManager carInventoryManager = new CarInventoryManager(
				carOrdersProcessing);
		try {
			carOrdersProcessing = carInventoryManager.readCarInventory();
			assertEquals(24, carOrdersProcessing.getCarInventoryList().size());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	@Test
	public void readAccessoryInventoryTest() {


		AccessoryInventoryManager accessoryInventoryManager = new AccessoryInventoryManager(
				carOrdersProcessing);
		try {
			carOrdersProcessing = accessoryInventoryManager
					.readAccessoryInventory();
			assertEquals(30, carOrdersProcessing.getAccessoryInventoryList()
					.size());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void readMotorInsuranceTest() {

		MotorInsuranceProviderManager motorInsuranceProviderManager = new MotorInsuranceProviderManager(
				carOrdersProcessing);
		try {
			carOrdersProcessing = motorInsuranceProviderManager
					.readMotorInsurance();
			assertEquals(2, carOrdersProcessing.getMotorInsuranceProviderList()
					.size());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}

	@Test
	public void readRegionalTaxRateTest() {

		RegionalTaxRateManager regionalTaxRateManager = new RegionalTaxRateManager(
				carOrdersProcessing);
		try {
			carOrdersProcessing = regionalTaxRateManager.readRegionalTaxRate();
			assertEquals(5, carOrdersProcessing.getRegionalTaxRateList().size());
		} catch (InventoryException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println(e.getMessage());
		}

	}

	 @Test
	 public void readCarStandingOrdersTest(){
	
	 readCarInventoryTest();
	 readAccessoryInventoryTest();
	 readMotorInsuranceTest();
	 readRegionalTaxRateTest();
	 List<CarStandingOrders> list;
	 list = countOccurrencesInParallel(carOrdersProcessing);
	 assertEquals(60, list.size());
	 try {
		carOrdersProcessing.errorCSV();
		carOrdersProcessing.regionalReportGeneration();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
	 	
	 }
	
	 List<CarStandingOrders> countOccurrencesInParallel(
	 CarOrdersProcessing carOrdersProcessing) {
	 return forkJoinPool.invoke(new CarStandingOrdersManager(
	 carOrdersProcessing));
	 }

}
