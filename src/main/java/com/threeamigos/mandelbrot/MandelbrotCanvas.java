package com.threeamigos.mandelbrot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import com.threeamigos.mandelbrot.implementations.ui.AboutWindow;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.MandelbrotService;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.PointsInfo;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.MessageNotifier;

public class MandelbrotCanvas extends JPanel implements Runnable, MouseWheelListener, MouseInputListener,
		MouseMotionListener, KeyListener, MessageNotifier, PropertyChangeListener {

	private static final String FONT_NAME = "Serif";

	private static final long serialVersionUID = 1L;

	private transient MandelbrotService mandelbrotService;
	private transient PointsOfInterestService pointsOfInterestService;
	private transient ImageProducerServiceFactory imageProducerServiceFactory;
	private transient ImageProducerService imageProducerService;
	private transient SnapshotService snapshotService;
	private transient PointsInfo pointsInfo;

	private boolean showInfo = true;
	private boolean showHelp = true;
	private boolean showPointOfInterestName = true;

	private Integer currentPointOfInterestIndex;

	private transient Thread calculationThread;
	private boolean calculationThreadRunning = false;

	private transient Image image;
	private long lastDrawTime;

	private JMenu pointsOfInterestMenu;
	private JMenu colorModelsMenu;
	private JMenu threadsMenu;
	private JMenu iterationsMenu;

	public MandelbrotCanvas(MandelbrotService mandelbrotService, PointsOfInterestService pointsOfInterestService,
			ImageProducerServiceFactory imageProducerServiceFactory, SnapshotService snapshotService,
			PointsInfo pointsInfo, CalculationParameters calculationParameters) {
		super();
		this.pointsOfInterestService = pointsOfInterestService;
		this.imageProducerServiceFactory = imageProducerServiceFactory;
		this.imageProducerService = imageProducerServiceFactory
				.createInstance(calculationParameters.getMaxIterations());
		this.snapshotService = snapshotService;
		this.pointsInfo = pointsInfo;

		setSize(pointsInfo.getWidth(), pointsInfo.getHeight());
		setBackground(Color.YELLOW);
		setFocusable(true);
		setDoubleBuffered(true);

		this.mandelbrotService = mandelbrotService;

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
	}

	@Override
	public void run() {
		mandelbrotService.calculate(pointsInfo);
		lastDrawTime = mandelbrotService.getDrawTime();
		calculationThreadRunning = false;
	}

	@Override
	public void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);

		Graphics2D graphics = (Graphics2D) gfx;
		graphics.drawImage(image, 0, 0, null);

		int xCoord = 50;
		int yCoord = 50;

		if (showInfo) {
			yCoord = showInfo(graphics, xCoord, yCoord);
		}

		if (showHelp) {
			showHelp(graphics, xCoord, yCoord);
		}

		if (showPointOfInterestName) {
			showPointOfInterestName(graphics);
		}

	}

	private int showInfo(Graphics2D graphics, int xCoord, int yCoord) {
		int fontHeight = getWidth() == Resolution.SD.getWidth() ? 16 : 24;
		Font font = new Font(FONT_NAME, Font.BOLD, fontHeight);
		graphics.setFont(font);

		int vSpacing = fontHeight + 4;
		drawString(graphics, String.format("Zoom factor: %.2f - count: %d", 1.0d / pointsInfo.getZoomFactor(),
				pointsInfo.getZoomCount()), xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, String.format("Draw time: %d ms (%d threads, %d iterations max)", lastDrawTime,
				mandelbrotService.getNumberOfThreads(), mandelbrotService.getMaxIterations()), xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics,
				String.format("Real interval: [%1.14f,%1.14f]", pointsInfo.getMinX(), pointsInfo.getMaxX()), xCoord,
				yCoord);
		yCoord += vSpacing;
		drawString(graphics,
				String.format("Imaginary interval: [%1.14f,%1.14f]", pointsInfo.getMinY(), pointsInfo.getMaxY()),
				xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, String.format("Optimizations: %s", getOptimizationsDescription()), xCoord, yCoord);
		yCoord += vSpacing;
		Double realCoordinateUnderPointer = pointsInfo.getPointerRealcoordinate();
		if (realCoordinateUnderPointer != null) {
			Double imaginaryCoordinateUnderPointer = pointsInfo.getPointerImaginaryCoordinate();
			if (imaginaryCoordinateUnderPointer != null) {
				drawString(graphics,
						String.format("Current point: [%d,%d] [%1.14f,%1.14f]", pointsInfo.getPointerXCoordinate(),
								pointsInfo.getPointerYCoordinate(), realCoordinateUnderPointer.floatValue(),
								imaginaryCoordinateUnderPointer.floatValue()),
						xCoord, yCoord);
				yCoord += vSpacing;
				drawString(graphics,
						String.format("Current point iterations: %d", mandelbrotService
								.getIterations(pointsInfo.getPointerXCoordinate(), pointsInfo.getPointerYCoordinate())),
						xCoord, yCoord);
				yCoord += vSpacing;
			}
		}
		return yCoord;
	}

	private int showHelp(Graphics2D graphics, int xCoord, int yCoord) {
		int fontHeight = getWidth() == Resolution.SD.getWidth() ? 12 : 16;
		int vSpacing = fontHeight + 4;
		Font font = new Font(FONT_NAME, Font.BOLD, fontHeight);
		graphics.setFont(font);
		if (pointsOfInterestService.getCount() < 10) {
			drawString(graphics, "A - add point of interest", xCoord, yCoord);
			yCoord += vSpacing;
		}
		drawString(graphics,
				String.format("C - change color model (current: %s)", imageProducerService.getCurrentColorModelName()),
				xCoord, yCoord);
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
		drawString(graphics, "Mouse wheel - zoom in/out", xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, "Mouse click - change center", xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, "Double click - back to zoom level 0", xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, "Arrow up/down - double/halve max iterations", xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, "Arrow left/right - more/less threads", xCoord, yCoord);
		yCoord += vSpacing;

		int index = 1;
		for (PointOfInterest pointOfInterest : pointsOfInterestService.getElements()) {
			String description = String.format("%d - %s (%d)", index == 10 ? 0 : index, pointOfInterest.getName(),
					pointOfInterest.getMaxIterations());
			if (currentPointOfInterestIndex != null && currentPointOfInterestIndex == index) {
				drawString(graphics, description, xCoord, yCoord, Color.YELLOW);
			} else {
				drawString(graphics, description, xCoord, yCoord);
			}
			index++;
			yCoord += vSpacing;
			if (index > 10) {
				// Since we ran out of numeric keys..
				break;
			}
		}
		return yCoord;
	}

	private void showPointOfInterestName(Graphics2D graphics) {
		if (currentPointOfInterestIndex != null) {
			PointOfInterest pointOfInterest = pointsOfInterestService.getElements()
					.get(currentPointOfInterestIndex - 1);
			int fontHeight = getHeight() / 20;
			Font font = new Font(FONT_NAME, Font.BOLD | Font.ITALIC, fontHeight);
			graphics.setFont(font);
			FontMetrics fontMetrics = graphics.getFontMetrics();
			drawString(graphics, pointOfInterest.getName(),
					getWidth() - 40 - fontMetrics.stringWidth(pointOfInterest.getName()), getHeight() - 40 - fontHeight,
					Color.YELLOW);
		}
	}

	private String getOptimizationsDescription() {
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
		return optimizations;
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
		if (mandelbrotService.isCalculating()) {
			mandelbrotService.interruptPreviousCalculation();
		}
		currentPointOfInterestIndex = null;
		if (e.getWheelRotation() < 0) {
			pointsInfo.zoom(e.getX(), e.getY(), 0.9d);
		} else {
			pointsInfo.zoom(e.getX(), e.getY(), 1.11111111111d);
		}
		startCalculationThread();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		mandelbrotService.interruptPreviousCalculation();
		currentPointOfInterestIndex = null;
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
			cycleColorModel();
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
		case KeyEvent.VK_P:
			hideOrShowPointOfInterestName();
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
		case KeyEvent.VK_UP:
			doubleUpMaxIterations();
			break;
		case KeyEvent.VK_DOWN:
			halveMaxIterations();
			break;
		case KeyEvent.VK_LEFT:
			decrementThreads();
			break;
		case KeyEvent.VK_RIGHT:
			incrementThreads();
			break;
		default:
			break;
		}
	}

	private void setMaxIterations(int maxIterations) {
		if (mandelbrotService.setMaxIterations(maxIterations)) {
			updateImageProducer(mandelbrotService.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void doubleUpMaxIterations() {
		if (mandelbrotService.doubleUpMaxIterations()) {
			updateImageProducer(mandelbrotService.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void halveMaxIterations() {
		if (mandelbrotService.halveMaxIterations()) {
			updateImageProducer(mandelbrotService.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void switchColorModel(String colorModelName) {
		imageProducerService.switchColorModel(colorModelName);
		updateColorModelsMenu();
		image = imageProducerService.produceImage(pointsInfo.getWidth(), pointsInfo.getHeight(),
				mandelbrotService.getIterations());
		repaint();
	}

	private void setNumberOfThreads(int numberOfThreads) {
		if (mandelbrotService.setNumberOfThreads(numberOfThreads)) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void incrementThreads() {
		if (mandelbrotService.incrementNumberOfThreads()) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void decrementThreads() {
		if (mandelbrotService.decrementNumberOfThreads()) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void addPointOfInterest() {
		PersistResult persistResult = pointsOfInterestService
				.add(pointsInfo.getPointOfInterest(mandelbrotService.getMaxIterations()));
		if (persistResult != null && persistResult.isSuccessful()) {
			currentPointOfInterestIndex = pointsOfInterestService.getCount();
			updatePointsOfInterestMenu();
			repaint();
		}
	}

	private void cycleColorModel() {
		imageProducerService.cycleColorModel();
		updateColorModelsMenu();
		image = imageProducerService.produceImage(pointsInfo.getWidth(), pointsInfo.getHeight(),
				mandelbrotService.getIterations());
		repaint();
	}

	private void deletePointOfInterest() {
		if (currentPointOfInterestIndex != null) {
			// We use a backup and operate using that because as soon as the notification
			// window appears, a repaint would be triggered, finding an invalid index when
			// deleting the last point of interest.
			Integer backupOfCurrentPointOfInterestIndex = currentPointOfInterestIndex;
			currentPointOfInterestIndex = null;
			pointsOfInterestService.remove(backupOfCurrentPointOfInterestIndex - 1);
			PersistResult result = pointsOfInterestService.savePointsOfInterest();
			if (result.isSuccessful()) {
				updatePointsOfInterestMenu();
				repaint();
			} else {
				currentPointOfInterestIndex = backupOfCurrentPointOfInterestIndex;
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

	private void hideOrShowPointOfInterestName() {
		showPointOfInterestName = !showPointOfInterestName;
		repaint();
	}

	private void saveImage() {
		snapshotService.saveSnapshot(pointsInfo, mandelbrotService.getMaxIterations(),
				imageProducerService.getCurrentColorModelName(), image, this);
	}

	private void setPointOfInterest(int pointIndex) {
		if (pointsOfInterestService.getCount() >= pointIndex) {
			currentPointOfInterestIndex = pointIndex;
			PointOfInterest pointOfInterest = pointsOfInterestService.getElements().get(pointIndex - 1);
			pointsInfo.setPointOfInterest(pointOfInterest);
			if (pointOfInterest.getMaxIterations() != mandelbrotService.getMaxIterations()) {
				updateImageProducer(pointOfInterest.getMaxIterations());
				mandelbrotService.setMaxIterations(pointOfInterest.getMaxIterations());
				updateIterationsMenu();
			}
			startCalculationThread();
		}
	}

	@Override
	public String request(String message) {
		return JOptionPane.showInputDialog(this, message);
	}

	@Override
	public void notify(String message) {
		JOptionPane.showMessageDialog(this, message);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (MandelbrotService.CALCULATION_COMPLETE_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			image = imageProducerService.produceImage(pointsInfo.getWidth(), pointsInfo.getHeight(),
					mandelbrotService.getIterations());
			repaint();
		}
	}

	public void addMenus(JMenuBar menuBar) {
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		addCheckboxMenuItem(fileMenu, "Show info", KeyEvent.VK_I, showInfo, event -> hideOrShowInfo());
		addCheckboxMenuItem(fileMenu, "Show help", KeyEvent.VK_H, showHelp, event -> hideOrShowHelp());
		fileMenu.addSeparator();
		addMenuItem(fileMenu, "Save image", KeyEvent.VK_S, event -> saveImage());
		fileMenu.addSeparator();
		addMenuItem(fileMenu, "About", KeyEvent.VK_S, event -> about());
		fileMenu.addSeparator();
		addMenuItem(fileMenu, "Quit", KeyEvent.VK_Q, event -> System.exit(0));

		pointsOfInterestMenu = new JMenu("Points of interest");
		menuBar.add(pointsOfInterestMenu);

		addMenuItem(pointsOfInterestMenu, "Add point of interest", KeyEvent.VK_A, event -> addPointOfInterest());
		addMenuItem(pointsOfInterestMenu, "Delete current point of interest", KeyEvent.VK_D,
				event -> deletePointOfInterest());
		pointsOfInterestMenu.addSeparator();
		addCheckboxMenuItem(pointsOfInterestMenu, "Show point of interest's name", KeyEvent.VK_P,
				showPointOfInterestName, event -> hideOrShowPointOfInterestName());
		pointsOfInterestMenu.addSeparator();
		updatePointsOfInterestMenu();

		JMenu calculationsMenu = new JMenu("Calculations");
		menuBar.add(calculationsMenu);

		addMenuItem(calculationsMenu, "Cycle color model", KeyEvent.VK_C, event -> cycleColorModel());
		colorModelsMenu = new JMenu("Use color model");
		for (String colorModelName : imageProducerService.getColorModeNames()) {
			addCheckboxMenuItem(colorModelsMenu, colorModelName, -1,
					imageProducerService.getCurrentColorModelName().equals(colorModelName),
					event -> switchColorModel(colorModelName));
		}
		calculationsMenu.add(colorModelsMenu);

		calculationsMenu.addSeparator();
		threadsMenu = new JMenu("Threads to use");
		calculationsMenu.add(threadsMenu);
		for (int i = 1; i <= Runtime.getRuntime().availableProcessors(); i++) {
			final int threadsToUse = i;
			addCheckboxMenuItem(threadsMenu, String.valueOf(i), -1, mandelbrotService.getNumberOfThreads() == i,
					event -> setNumberOfThreads(threadsToUse));
		}
		calculationsMenu.addSeparator();
		iterationsMenu = new JMenu("Max iterations");
		for (int i = MandelbrotService.MIN_ITERATIONS_EXPONENT; i <= MandelbrotService.MAX_ITERATIONS_EXPONENT; i++) {
			final int maxIterations = 1 << i;
			addCheckboxMenuItem(iterationsMenu, String.valueOf(maxIterations), -1,
					mandelbrotService.getMaxIterations() == maxIterations, event -> setMaxIterations(maxIterations));
		}
		calculationsMenu.add(iterationsMenu);
	}

	private void about() {
		new AboutWindow().about(this);
	}

	private void updateColorModelsMenu() {
		Component[] items = colorModelsMenu.getMenuComponents();
		for (int i = 0; i < items.length; i++) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) items[i];
			item.setSelected(imageProducerService.getCurrentColorModelName().equals(item.getText()));
		}
	}

	private void updateThreadsMenu() {
		Component[] items = threadsMenu.getMenuComponents();
		for (int i = 0; i < items.length; i++) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) items[i];
			item.setSelected(mandelbrotService.getNumberOfThreads() == i + 1);
		}
	}

	private void updateIterationsMenu() {
		Component[] items = iterationsMenu.getMenuComponents();
		for (int i = 0; i < items.length; i++) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) items[i];
			final int maxIterations = 1 << i + MandelbrotService.MIN_ITERATIONS_EXPONENT;
			item.setSelected(mandelbrotService.getMaxIterations() == maxIterations);
		}
	}

	private void updatePointsOfInterestMenu() {
		Component[] items = pointsOfInterestMenu.getMenuComponents();
		for (int i = 5; i < items.length; i++) {
			pointsOfInterestMenu.remove(items[i]);
		}
		List<PointOfInterest> elements = pointsOfInterestService.getElements();
		for (int i = 0; i < elements.size(); i++) {
			PointOfInterest pointOfInterest = elements.get(i);
			final int index = i + 1;
			int mnemonic = -1;
			if (index < 9) {
				mnemonic = KeyEvent.VK_0 + index;
			}
			if (index == 10) {
				mnemonic = KeyEvent.VK_0;
			}
			addMenuItem(pointsOfInterestMenu,
					index + " - " + pointOfInterest.getName() + " (" + pointOfInterest.getMaxIterations() + ")",
					mnemonic, event -> setPointOfInterest(index));
		}
	}

	private JMenuItem addCheckboxMenuItem(JMenu menu, String title, int mnemonic, boolean initialValue,
			java.awt.event.ActionListener actionListener) {
		JMenuItem menuItem = new JCheckBoxMenuItem(title);
		if (actionListener != null) {
			menuItem.addActionListener(actionListener);
		}
		if (mnemonic != -1) {
			menuItem.setMnemonic(mnemonic);
		}
		menuItem.setSelected(initialValue);
		menu.add(menuItem);
		return menuItem;
	}

	private JMenuItem addMenuItem(JMenu menu, String title, int mnemonic,
			java.awt.event.ActionListener actionListener) {
		JMenuItem menuItem = new JMenuItem(title);
		if (actionListener != null) {
			menuItem.addActionListener(actionListener);
		}
		if (mnemonic != -1) {
			menuItem.setMnemonic(mnemonic);
		}
		menu.add(menuItem);
		return menuItem;
	}

	private void updateImageProducer(int maxIterations) {
		String currentColorModelName = imageProducerService.getCurrentColorModelName();
		imageProducerService = imageProducerServiceFactory.createInstance(maxIterations);
		imageProducerService.switchColorModel(currentColorModelName);
	}
}
