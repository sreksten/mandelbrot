package com.threeamigos.mandelbrot;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.threeamigos.mandelbrot.implementations.DataBufferImpl;
import com.threeamigos.mandelbrot.implementations.DirectColorModelImageProducer;
import com.threeamigos.mandelbrot.implementations.IndexColorModelImageProducer;
import com.threeamigos.mandelbrot.implementations.MultithreadedMandelbrotCalculator;
import com.threeamigos.mandelbrot.implementations.PointsInfoImpl;
import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.ImageProducer;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;

public class MandelbrotCanvas extends JPanel
		implements Runnable, MouseWheelListener, MouseInputListener, MouseMotionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private transient PointsInfo pointsInfo;
	private transient DataBuffer dataBuffer;
	private transient MandelbrotCalculator calculator;
	private transient ImageProducer imageProducer;
	private transient DirectColorModelImageProducer directColorModelImageProducer;
	private transient IndexColorModelImageProducer indexColorModelImageProducer;

	private transient Thread calculationThread;
	private boolean calculationThreadRunning = false;

	private transient Image image;
	private long lastDrawTime;

	public MandelbrotCanvas(int width, int height) {
		super();
		setSize(width, height);
		setBackground(Color.YELLOW);
		setFocusable(true);

		pointsInfo = new PointsInfoImpl();
		pointsInfo.setDimensions(width, height);

		dataBuffer = new DataBufferImpl();
		dataBuffer.setDimensions(width, height);

		calculator = new MultithreadedMandelbrotCalculator();

		directColorModelImageProducer = new DirectColorModelImageProducer();
		indexColorModelImageProducer = new IndexColorModelImageProducer();

		imageProducer = directColorModelImageProducer;

		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);

		startCalculationThread();
	}

	private void startCalculationThread() {
		if (calculationThread == null || !calculationThreadRunning) {
			calculationThreadRunning = true;
			calculationThread = new Thread(this);
			calculationThread.setDaemon(true);
			calculationThread.start();

			calculator.calculate(pointsInfo, dataBuffer);
			lastDrawTime = calculator.getDrawTime();
			image = imageProducer.produceImage(dataBuffer);

			calculationThreadRunning = false;
		}
	}

	@Override
	public void run() {
		while (calculationThreadRunning) {
			repaint();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);

		Graphics2D graphics = (Graphics2D) gfx;
		graphics.drawImage(image, 0, 0, null);

		int fontHeight = 24;
		Font font = new Font("Serif", Font.BOLD, fontHeight);
		graphics.setFont(font);

		int xCoord = 50;
		int yCoord = 50;
		int vSpacing = fontHeight + 4;
		drawString(graphics,
				String.format("Zoom factor: %f - count: %d", pointsInfo.getZoomFactor(), pointsInfo.getZoomCount()),
				xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, String.format("Draw time: %d ms", lastDrawTime), xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics,
				String.format("Real interval: [%1.14f,%1.14f]", pointsInfo.getMinX(), pointsInfo.getMaxX()), xCoord,
				yCoord);
		yCoord += vSpacing;
		drawString(graphics,
				String.format("Imaginary interval: [%1.14f,%1.14f]", pointsInfo.getMinY(), pointsInfo.getMaxY()),
				xCoord, yCoord);
		yCoord += vSpacing;
		String optimizations;
		if (pointsInfo.isCardioidVisible()) {
			if (pointsInfo.isPeriod2BulbVisible()) {
				optimizations = "Cardioid, Period2Bulb, Period";
			} else {
				optimizations = "Cardioid, Period";
			}
		} else {
			if (pointsInfo.isPeriod2BulbVisible()) {
				optimizations = "Period2Bulb, Period";
			} else {
				optimizations = "Period";
			}
		}
		drawString(graphics, String.format("Optimizations: %s", optimizations), xCoord, yCoord);
		yCoord += vSpacing;
		Double realCoordinateUnderPointer = pointsInfo.getPointerRealcoordinate();
		if (realCoordinateUnderPointer != null) {
			Double imaginaryCoordinateUnderPointer = pointsInfo.getPointerImaginaryCoordinate();
			if (imaginaryCoordinateUnderPointer != null) {
				drawString(graphics, String.format("Current point: %1.14f,%1.14f",
						realCoordinateUnderPointer.floatValue(), imaginaryCoordinateUnderPointer.floatValue()), xCoord,
						yCoord);
				yCoord += vSpacing;
				drawString(graphics,
						String.format("Current point iterations: %d", dataBuffer
								.getPixel(pointsInfo.getPointerXCoordinate(), pointsInfo.getPointerYCoordinate())),
						xCoord, yCoord);
				yCoord += vSpacing;
			}
		}

		fontHeight = 16;
		font = new Font("Serif", Font.BOLD, fontHeight);
		graphics.setFont(font);
		drawString(graphics, "Press 'C' to switch between indexed and direct color model", xCoord, yCoord);

	}

	private void drawString(Graphics2D graphics, String s, int x, int y) {
		graphics.setColor(Color.BLACK);
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				graphics.drawString(s, i, j);
			}
		}
		graphics.setColor(Color.WHITE);
		graphics.drawString(s, x, y);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		calculator.interruptPreviousCalculation();
		if (e.getWheelRotation() < 0) {
			pointsInfo.zoom(e.getX(), e.getY(), 0.9d);
		} else {
			pointsInfo.zoom(e.getX(), e.getY(), 1.10d);
		}
		startCalculationThread();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		calculator.interruptPreviousCalculation();
		if (e.getClickCount() == 1) {
			pointsInfo.changeCenterTo(e.getX(), e.getY());
		} else {
			pointsInfo.reset();
		}
		startCalculationThread();
		requestFocusInWindow();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// We won't follow this
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// We won't follow this
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		updatePointerCoordinates(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		pointsInfo.updatePointerCoordinates(null, null);
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		updatePointerCoordinates(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		updatePointerCoordinates(e);
	}

	private void updatePointerCoordinates(MouseEvent e) {
		pointsInfo.updatePointerCoordinates(e.getX(), e.getY());
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// We won't follow this
	}

	private void switchColorModels() {
		if (imageProducer == directColorModelImageProducer) {
			imageProducer = indexColorModelImageProducer;
		} else {
			imageProducer = directColorModelImageProducer;
		}
		image = imageProducer.produceImage(dataBuffer);
		repaint();
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_C:
			switchColorModels();
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// We won't follow this
	}

}
