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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.threeamigos.mandelbrot.interfaces.DataPersister;
import com.threeamigos.mandelbrot.interfaces.DataPersister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculatorProducer;
import com.threeamigos.mandelbrot.interfaces.MultipleVariantImageProducer;
import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.PointsOfInterest;
import com.threeamigos.mandelbrot.interfaces.ResolutionChooser;

public class MandelbrotCanvas extends JPanel
		implements Runnable, MouseWheelListener, MouseInputListener, MouseMotionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private transient ResolutionChooser resolutionChooser;
	private transient MandelbrotCalculatorProducer mandelbrotCalculatorProducer;
	private transient PointsInfo pointsInfo;
	private transient PointsOfInterest pointsOfInterest;
	private transient MultipleVariantImageProducer imageProducer;
	private transient DataPersister dataPersister;

	private transient MandelbrotCalculator calculator;

	private boolean showInfo = true;
	private boolean showHelp = true;

	private Integer currentPointOfInterestIndex;

	private transient Thread calculationThread;
	private boolean calculationThreadRunning = false;

	private transient Image image;
	private long lastDrawTime;

	public MandelbrotCanvas(int width, int height, ResolutionChooser resolutionChooser,
			MandelbrotCalculatorProducer mandelbrotCalculatorProducer, PointsInfo pointsInfo,
			MultipleVariantImageProducer imageProducer, PointsOfInterest pointsOfInterest,
			DataPersister dataPersister) {
		super();
		this.resolutionChooser = resolutionChooser;
		this.mandelbrotCalculatorProducer = mandelbrotCalculatorProducer;
		this.pointsInfo = pointsInfo;
		this.pointsOfInterest = pointsOfInterest;
		this.imageProducer = imageProducer;
		this.dataPersister = dataPersister;

		setSize(width, height);
		setBackground(Color.YELLOW);
		setFocusable(true);
		setDoubleBuffered(true);

		calculator = mandelbrotCalculatorProducer.createInstance();

		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);

		startCalculationThread();
	}

	private void startCalculationThread() {
		if (calculationThreadRunning) {
			calculationThread.interrupt();
			try {
				calculationThread.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}

		calculationThreadRunning = true;
		calculationThread = new Thread(this);
		calculationThread.setDaemon(true);
		calculationThread.start();

		calculator.calculate(pointsInfo, getWidth(), getHeight());
		lastDrawTime = calculator.getDrawTime();
		image = imageProducer.produceImage(calculator.getDataBuffer());

		calculationThreadRunning = false;
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

		int xCoord = 50;
		int yCoord = 50;

		if (showInfo) {
			int fontHeight = 24;
			Font font = new Font("Serif", Font.BOLD, fontHeight);
			graphics.setFont(font);

			int vSpacing = fontHeight + 4;
			drawString(graphics,
					String.format("Zoom factor: %f - count: %d", pointsInfo.getZoomFactor(), pointsInfo.getZoomCount()),
					xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, String.format("Draw time: %d ms using %d threads, max %d iterations", lastDrawTime,
					calculator.getNumberOfThreads(), MandelbrotCalculator.MAX_ITERATIONS), xCoord, yCoord);
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
							realCoordinateUnderPointer.floatValue(), imaginaryCoordinateUnderPointer.floatValue()),
							xCoord, yCoord);
					yCoord += vSpacing;
					drawString(graphics,
							String.format("Current point iterations: %d", calculator.getDataBuffer()
									.getPixel(pointsInfo.getPointerXCoordinate(), pointsInfo.getPointerYCoordinate())),
							xCoord, yCoord);
					yCoord += vSpacing;
				}
			}
		}

		if (showHelp) {
			int fontHeight = 16;
			int vSpacing = fontHeight + 4;
			Font font = new Font("Serif", Font.BOLD, fontHeight);
			graphics.setFont(font);
			if (pointsOfInterest.count() < 10) {
				drawString(graphics, "A - add point of interest", xCoord, yCoord);
				yCoord += vSpacing;
			}
			drawString(graphics, "C - switch between indexed and direct color model", xCoord, yCoord);
			yCoord += vSpacing;
			if (currentPointOfInterestIndex != null) {
				drawString(graphics, "D - delete current point of interest", xCoord, yCoord);
				yCoord += vSpacing;
			}
			drawString(graphics, "H - hide or show help", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "I - hide or show info", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "S - save image", xCoord, yCoord);
			yCoord += vSpacing;

			int index = 1;
			for (PointOfInterest pointOfInterest : pointsOfInterest.getElements()) {
				if (currentPointOfInterestIndex != null && currentPointOfInterestIndex == index) {
					drawString(graphics, String.format("%d - %s", index, pointOfInterest.getName()), xCoord, yCoord,
							Color.YELLOW);
				} else {
					drawString(graphics, String.format("%d - %s", index, pointOfInterest.getName()), xCoord, yCoord);
				}
				index++;
				yCoord += vSpacing;
			}
		}
	}

	private void drawString(Graphics2D graphics, String s, int x, int y) {
		drawString(graphics, s, x, y, Color.WHITE);
	}

	private void drawString(Graphics2D graphics, String s, int x, int y, Color color) {
		graphics.setColor(Color.BLACK);
		for (int i = x - 1; i <= x + 1; i++) {
			for (int j = y - 1; j <= y + 1; j++) {
				graphics.drawString(s, i, j);
			}
		}
		graphics.setColor(color);
		graphics.drawString(s, x, y);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		calculator.interruptPreviousCalculation();
		currentPointOfInterestIndex = null;
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

	@Override
	public void keyReleased(KeyEvent e) {
		// We won't follow this
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_A:
			addPointOfInterest();
			break;
		case KeyEvent.VK_C:
			switchColorModels();
			break;
		case KeyEvent.VK_D:
			deletePointOfInterest();
			break;
		case KeyEvent.VK_H:
			hideOrShowHelp();
			break;
		case KeyEvent.VK_I:
			hideOrShowInfo();
			break;
		case KeyEvent.VK_S:
			saveImage();
			break;
		case KeyEvent.VK_1:
			setPointOfInterest(1);
			break;
		case KeyEvent.VK_2:
			setPointOfInterest(2);
			break;
		case KeyEvent.VK_3:
			setPointOfInterest(3);
			break;
		case KeyEvent.VK_4:
			setPointOfInterest(4);
			break;
		case KeyEvent.VK_5:
			setPointOfInterest(5);
			break;
		case KeyEvent.VK_6:
			setPointOfInterest(6);
			break;
		case KeyEvent.VK_7:
			setPointOfInterest(7);
			break;
		case KeyEvent.VK_8:
			setPointOfInterest(8);
			break;
		case KeyEvent.VK_9:
			setPointOfInterest(9);
			break;
		case KeyEvent.VK_0:
			setPointOfInterest(10);
			break;
		default:
			break;
		}
	}

	private void addPointOfInterest() {
		if (pointsOfInterest.count() < 10) {
			String name = JOptionPane.showInputDialog(this, "Give it a name:");
			if (name != null && !name.isBlank()) {
				PointOfInterest newPoint = pointsInfo.getPointOfInterest(name);
				pointsOfInterest.add(newPoint);
				PersistResult result = dataPersister.savePointsOfInterest(pointsOfInterest);
				if (result.isSuccessful()) {
					notify(String.format("Point of interest '%s' added.", name));
					repaint();
				} else {
					notify("Error while saving points of interest: " + result.getError());
				}
			}
		}
	}

	private void switchColorModels() {
		if (imageProducer.isUsingDirectColorModel()) {
			imageProducer.useIndexColorModel();
		} else {
			imageProducer.useDirectColorModel();
		}
		image = imageProducer.produceImage(calculator.getDataBuffer());
		repaint();
	}

	private void deletePointOfInterest() {
		if (currentPointOfInterestIndex != null) {
			pointsOfInterest.remove(currentPointOfInterestIndex - 1);
			PersistResult result = dataPersister.savePointsOfInterest(pointsOfInterest);
			if (result.isSuccessful()) {
				currentPointOfInterestIndex = null;
				notify("Point of interest removed.");
				repaint();
			} else {
				notify("Error while saving points of interest: " + result.getError());
			}
		}
	}

	private void hideOrShowHelp() {
		showHelp = !showHelp;
		repaint();
	}

	private void hideOrShowInfo() {
		showInfo = !showInfo;
		repaint();
	}

	private void saveImage() {

		Resolution resolution = resolutionChooser.chooseResolution(this);
		if (resolution == null) {
			return;
		}

		Image imageToSave = image;

		int resWidth = resolution.getWidth();
		int resHeight = resolution.getHeight();
		if (resWidth != getWidth() || resHeight != getHeight()) {
			MandelbrotCalculator tempCalculator = mandelbrotCalculatorProducer.createInstance();
			PointsInfo tempPointsInfo = pointsInfo.adaptToDimensions(resWidth, resHeight);
			tempCalculator.calculate(tempPointsInfo, resWidth, resHeight);
			imageToSave = imageProducer.produceImage(tempCalculator.getDataBuffer());
		}

		String filename = new StringBuilder().append(System.getProperty("user.home")).append(File.separatorChar)
				.append("3AM_Mandelbrot_").append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()))
				.append(".png").toString();

		PersistResult result = dataPersister.saveImage(imageToSave, filename);
		if (result.isSuccessful()) {
			notify("File saved in " + filename);
		} else {
			notify("Error while saving image: " + result.getError());
		}
	}

	private void setPointOfInterest(int pointIndex) {
		List<PointOfInterest> points = pointsOfInterest.getElements();
		if (points.size() >= pointIndex) {
			currentPointOfInterestIndex = pointIndex;
			pointsInfo.setPointOfInterest(points.get(pointIndex - 1));
			startCalculationThread();
		}
	}

	private void notify(String message) {
		JOptionPane.showMessageDialog(this, message);
	}
}
