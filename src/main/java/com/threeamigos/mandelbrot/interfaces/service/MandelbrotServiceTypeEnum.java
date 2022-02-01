package com.threeamigos.mandelbrot.interfaces.service;

public enum MandelbrotServiceTypeEnum {

	FOREGROUND(
			MandelbrotService.CALCULATION_IN_PROGRESS_PROPERTY_CHANGE,
			MandelbrotService.CALCULATION_COMPLETE_PROPERTY_CHANGE,
			10),
	BACKGROUND(
			MandelbrotService.BACKGROUND_CALCULATION_IN_PROGRESS_PROPERTY_CHANGE,
			MandelbrotService.BACKGROUND_CALCULATION_COMPLETE_PROPERTY_CHANGE,
			5);

	private String calculationInProgressEvent;
	private String calculationCompleteEvent;
	private int priority;

	private MandelbrotServiceTypeEnum(String calculationInProgressEvent, String calculationCompleteEvent,
			int priority) {
		this.calculationInProgressEvent = calculationInProgressEvent;
		this.calculationCompleteEvent = calculationCompleteEvent;
		this.priority = priority;
	}

	public String getCalculationInProgressEvent() {
		return calculationInProgressEvent;
	}

	public String getCalculationCompleteEvent() {
		return calculationCompleteEvent;
	}

	public int getPriority() {
		return priority;
	}

}
