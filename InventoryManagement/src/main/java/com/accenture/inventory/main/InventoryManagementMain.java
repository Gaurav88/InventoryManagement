package com.accenture.inventory.main;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import com.accenture.inventory.manager.AccessoryInventoryManager;
import com.accenture.inventory.manager.CarInventoryManager;
import com.accenture.inventory.manager.CarOrdersProcessing;
import com.accenture.inventory.manager.CarStandingOrdersManager;
import com.accenture.inventory.manager.MotorInsuranceProviderManager;
import com.accenture.inventory.manager.RegionalTaxRateManager;
import com.accenture.inventory.model.CarStandingOrders;

/**
 * Main File for Inventory Management
 * @author g.t.gupta
 *
 */

public class InventoryManagementMain {

	private static final ForkJoinPool FORKJOINPOOL = new ForkJoinPool();

	/**
	 * Main method for running the application
	 * @param args
	 */
	public static void main(String args[]) {

		try {
			long startTime = System.currentTimeMillis();

			ExecutorService executor = Executors.newFixedThreadPool(5);
			CarOrdersProcessing carOrdersProcessing = new CarOrdersProcessing();
			Callable<CarOrdersProcessing> carInventoryManager = new CarInventoryManager(
					carOrdersProcessing);
			Callable<CarOrdersProcessing> accessoryInventoryManager = new AccessoryInventoryManager(
					carOrdersProcessing);
			Callable<CarOrdersProcessing> motorInsuranceProviderManager = new MotorInsuranceProviderManager(
					carOrdersProcessing);
			Callable<CarOrdersProcessing> regionalTaxRateManager = new RegionalTaxRateManager(
					carOrdersProcessing);

			Future<CarOrdersProcessing> future1 = executor
					.submit(carInventoryManager);
			future1 = executor.submit(accessoryInventoryManager);
			future1 = executor.submit(motorInsuranceProviderManager);
			future1 = executor.submit(regionalTaxRateManager);

			executor.shutdown();
			while (!executor.isTerminated()) {
		      }

			countOccurrencesInParallel(future1.get());
			carOrdersProcessing.errorCSV();
			carOrdersProcessing.regionalReportGeneration();

			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println("Total time in Milli-Second: " + totalTime);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}

	}

	static List<CarStandingOrders> countOccurrencesInParallel(
			CarOrdersProcessing carOrdersProcessing) {
		return FORKJOINPOOL.invoke(new CarStandingOrdersManager(
				carOrdersProcessing));
	}
}
