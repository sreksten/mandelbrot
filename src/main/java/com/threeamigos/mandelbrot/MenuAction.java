package com.threeamigos.mandelbrot;

import java.awt.event.ActionEvent;
import java.util.function.Function;

import javax.swing.AbstractAction;

public class MenuAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	private Function function;

	public MenuAction(String name, Function function) {
		this.function = function;
	}

	public MenuAction(String name, Object function2) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		function.apply(null);
	}

}
