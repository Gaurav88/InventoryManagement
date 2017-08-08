package com.accenture.inventory.exception;

/**
 * Base exception class .
 * 
 * @author g.t.gupta
 * @version 1.0
 */
public class InventoryException extends Exception {

	/**
		 * 
		 */
	private static final long serialVersionUID = -6269794474561722583L;

	public InventoryException() {
		super("Error occurred in application.");
	}

	/**
	 * @param message
	 *            .
	 */
	public InventoryException(String message) {
		super(message);
	}

	/**
	 * @param e
	 *            .
	 */
	public InventoryException(Throwable inThrowable) {
		super(inThrowable);
	}

	/**
	 * @param errorCode
	 *            .
	 * @param inThrowable
	 *            .
	 */
	public InventoryException(String errorCode, Throwable inThrowable) {
		super(errorCode, inThrowable);
	}
}
