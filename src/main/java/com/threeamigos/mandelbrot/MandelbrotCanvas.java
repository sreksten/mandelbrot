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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.threeamigos.mandelbrot.implementations.DirectColorModelImageProducer;
import com.threeamigos.mandelbrot.implementations.IndexColorModelImageProducer;
import com.threeamigos.mandelbrot.interfaces.DataBuffer;
import com.threeamigos.mandelbrot.interfaces.ImageProducer;
import com.threeamigos.mandelbrot.interfaces.MandelbrotCalculator;
import com.threeamigos.mandelbrot.interfaces.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.Registry;

public class MandelbrotCanvas extends JPanel
		implements Runnable, MouseWheelListener, MouseInputListener, MouseMotionListener, KeyListener {

	private static final long serialVersionUID = 1L;

	private transient Registry registry;

	private transient PointsInfo pointsInfo;
	private transient DataBuffer dataBuffer;
	private transient MandelbrotCalculator calculator;
	private transient DirectColorModelImageProducer directColorModelImageProducer;
	private transient IndexColorModelImageProducer indexColorModelImageProducer;
	private transient ImageProducer imageProducer;

	private boolean showInfo = true;
	private boolean showHelp = true;

	private transient Thread calculationThread;
	private boolean calculationThreadRunning = false;

	private transient Image image;
	private long lastDrawTime;

	public MandelbrotCanvas(Registry registry) {
		super();
		this.registry = registry;
		setSize(registry.getWidth(), registry.getHeight());
		setBackground(Color.YELLOW);
		setFocusable(true);
		setDoubleBuffered(true);

		pointsInfo = registry.getPointsInfo();
		dataBuffer = registry.getDataBuffer();
		calculator = registry.getCalculator();
		directColorModelImageProducer = registry.getDirectColorModelImageProducer();
		indexColorModelImageProducer = registry.getIndexColorModelImageProducer();

		imageProducer = registry.getDirectColorModelImageProducer();

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

		calculator.calculate(pointsInfo, dataBuffer);
		lastDrawTime = calculator.getDrawTime();
		image = imageProducer.produceImage(dataBuffer);

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
							String.format("Current point iterations: %d", dataBuffer
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
			drawString(graphics, "H - hide or show help", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "I - hide or show info", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "S - save image", xCoord, yCoord);
			yCoord += vSpacing;
			drawString(graphics, "C - switch between indexed and direct color model", xCoord, yCoord);
			yCoord += vSpacing;

			int index = 1;
			for (PointOfInterest pointOfInterest : registry.getPointsOfInterest().getPointsOfInterest()) {
				drawString(graphics, String.format("%d - %s", index, pointOfInterest.getName()), xCoord, yCoord);
				index++;
				yCoord += vSpacing;
			}
		}

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

	@Override
	public void keyReleased(KeyEvent e) {
		// We won't follow this
	}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_C:
			switchColorModels();
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
		}

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

	private void hideOrShowHelp() {
		showHelp = !showHelp;
		repaint();
	}

	private void hideOrShowInfo() {
		showInfo = !showInfo;
		repaint();
	}

	private void saveImage() {
		try {
			String filename = System.getProperty("java.io.tmpdir") + File.separatorChar + "3AM_Mandelbrot.png";
			File outputfile = new File(filename);

			final BufferedImage buffImg = new BufferedImage(image.getWidth(null), image.getHeight(null),
					BufferedImage.TYPE_3BYTE_BGR);
			final Graphics2D g2 = buffImg.createGraphics();
			g2.drawImage(image, null, null);
			g2.dispose();

			ImageIO.write(buffImg, "png", outputfile);
			JOptionPane.showMessageDialog(this, "File saved in " + filename);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Error while saving image: " + e.getMessage());
		}
	}

	private void setPointOfInterest(int pointIndex) {
		List<PointOfInterest> pointsOfInterest = registry.getPointsOfInterest().getPointsOfInterest();
		if (pointsOfInterest.size() >= pointIndex) {
			pointsInfo.setPointOfInterest(pointsOfInterest.get(pointIndex - 1));
			startCalculationThread();
		}
	}

}
