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
import com.threeamigos.mandelbrot.interfaces.service.Points;
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
	private transient Points points;

	private boolean showInfo = true;
	private boolean showHelp = true;
	private boolean showPointOfInterestName = true;
	private boolean showProgress = true;
	private Integer percentage = null;

	private Integer currentPointOfInterestIndex;

	private transient Image image;

	private JMenu pointsOfInterestMenu;
	private JMenu colorModelsMenu;
	private JMenu threadsMenu;
	private JMenu iterationsMenu;
	private JCheckBoxMenuItem showProgressMenuItem;

	public MandelbrotCanvas(MandelbrotService mandelbrotService, PointsOfInterestService pointsOfInterestService,
			ImageProducerServiceFactory imageProducerServiceFactory, SnapshotService snapshotService, Points points,
			CalculationParameters calculationParameters) {
		super();
		this.pointsOfInterestService = pointsOfInterestService;
		this.imageProducerServiceFactory = imageProducerServiceFactory;
		this.imageProducerService = imageProducerServiceFactory
				.createInstance(calculationParameters.getMaxIterations());
		this.snapshotService = snapshotService;
		this.points = points;

		setSize(points.getWidth(), points.getHeight());
		setBackground(Color.YELLOW);
		setFocusable(true);
		setDoubleBuffered(true);

		this.mandelbrotService = mandelbrotService;

		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	private void stopCalculationThread() {
		if (mandelbrotService.isCalculating()) {
			mandelbrotService.interruptPreviousCalculation();
		}
	}

	public void startCalculationThread() {
		Thread calculationThread = new Thread(this);
		calculationThread.setDaemon(true);
		calculationThread.start();
	}

	@Override
	public void run() {
		mandelbrotService.calculate(points);
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
		drawString(graphics,
				String.format("Zoom factor: %.2f - count: %d", 1.0d / points.getZoomFactor(), points.getZoomCount()),
				xCoord, yCoord);
		yCoord += vSpacing;
		if (percentage != null) {
			drawString(graphics,
					String.format("Percentage: %d (%d threads, %d iterations max)", percentage,
							mandelbrotService.getNumberOfThreads(), mandelbrotService.getMaxIterations()),
					xCoord, yCoord);
		} else {
			drawString(graphics,
					String.format("Draw time: %d ms (%d threads, %d iterations max)", mandelbrotService.getDrawTime(),
							mandelbrotService.getNumberOfThreads(), mandelbrotService.getMaxIterations()),
					xCoord, yCoord);
		}
		yCoord += vSpacing;
		drawString(graphics, String.format("Real interval: [%1.14f,%1.14f]", points.getMinX(), points.getMaxX()),
				xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, String.format("Imaginary interval: [%1.14f,%1.14f]", points.getMinY(), points.getMaxY()),
				xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, String.format("Optimizations: %s", getOptimizationsDescription()), xCoord, yCoord);
		yCoord += vSpacing;
		Double realCoordinateUnderPointer = points.getPointerRealcoordinate();
		if (realCoordinateUnderPointer != null) {
			Double imaginaryCoordinateUnderPointer = points.getPointerImaginaryCoordinate();
			if (imaginaryCoordinateUnderPointer != null) {
				drawString(graphics,
						String.format("Current point: [%d,%d] [%1.14f,%1.14f]", points.getPointerXCoordinate(),
								points.getPointerYCoordinate(), realCoordinateUnderPointer.floatValue(),
								imaginaryCoordinateUnderPointer.floatValue()),
						xCoord, yCoord);
				yCoord += vSpacing;
				drawString(graphics,
						String.format("Current point iterations: %d", mandelbrotService
								.getIterations(points.getPointerXCoordinate(), points.getPointerYCoordinate())),
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
			drawString(graphics, "P - show or hide point of interest's name", xCoord, yCoord);
			yCoord += vSpacing;
		}
		drawString(graphics, "H - hide or show help", xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, "I - hide or show info", xCoord, yCoord);
		yCoord += vSpacing;
		drawString(graphics, "S - save snapshot", xCoord, yCoord);
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
		if (points.isCardioidVisible()) {
			if (points.isPeriod2BulbVisible()) {
				optimizations = "Cardioid, Period2Bulb, Period";
			} else {
				optimizations = "Cardioid, Period";
			}
		} else {
			if (points.isPeriod2BulbVisible()) {
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
		stopCalculationThread();
		currentPointOfInterestIndex = null;
		boolean shouldRestart;
		if (e.getWheelRotation() < 0) {
			shouldRestart = points.zoom(e.getX(), e.getY(), 0.9d);
		} else {
			shouldRestart = points.zoom(e.getX(), e.getY(), 1.11111111111d);
		}
		if (shouldRestart) {
			startCalculationThread();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		stopCalculationThread();
		currentPointOfInterestIndex = null;
		if (e.getClickCount() == 1) {
			points.changeCenterTo(e.getX(), e.getY());
		} else {
			points.reset();
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
		points.updatePointerCoordinates(null, null);
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
		points.updatePointerCoordinates(e.getX(), e.getY());
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
		case KeyEvent.VK_R:
			hideOrShowProgress();
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
		stopCalculationThread();
		if (mandelbrotService.setMaxIterations(maxIterations)) {
			updateImageProducer(mandelbrotService.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void doubleUpMaxIterations() {
		stopCalculationThread();
		if (mandelbrotService.doubleUpMaxIterations()) {
			updateImageProducer(mandelbrotService.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void halveMaxIterations() {
		stopCalculationThread();
		if (mandelbrotService.halveMaxIterations()) {
			updateImageProducer(mandelbrotService.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void switchColorModel(String colorModelName) {
		imageProducerService.switchColorModel(colorModelName);
		updateColorModelsMenu();
		image = imageProducerService.produceImage(points.getWidth(), points.getHeight(),
				mandelbrotService.getIterations());
		repaint();
	}

	private void setNumberOfThreads(int numberOfThreads) {
		stopCalculationThread();
		if (mandelbrotService.setNumberOfThreads(numberOfThreads)) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void incrementThreads() {
		stopCalculationThread();
		if (mandelbrotService.incrementNumberOfThreads()) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void decrementThreads() {
		stopCalculationThread();
		if (mandelbrotService.decrementNumberOfThreads()) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void addPointOfInterest() {
		PersistResult persistResult = pointsOfInterestService
				.add(points.getPointOfInterest(mandelbrotService.getMaxIterations()));
		if (persistResult != null && persistResult.isSuccessful()) {
			currentPointOfInterestIndex = pointsOfInterestService.getCount();
			updatePointsOfInterestMenu();
			repaint();
		}
	}

	private void cycleColorModel() {
		imageProducerService.cycleColorModel();
		updateColorModelsMenu();
		image = imageProducerService.produceImage(points.getWidth(), points.getHeight(),
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

	private void hideOrShowProgress() {
		showProgress = !showProgress;
		showProgressMenuItem.setSelected(showProgress);
	}

	private void saveImage() {
		snapshotService.saveSnapshot(points, mandelbrotService.getMaxIterations(),
				imageProducerService.getCurrentColorModelName(), image, this);
	}

	private void setPointOfInterest(int pointIndex) {
		if (pointsOfInterestService.getCount() >= pointIndex) {
			stopCalculationThread();
			currentPointOfInterestIndex = pointIndex;
			PointOfInterest pointOfInterest = pointsOfInterestService.getElements().get(pointIndex - 1);
			points.setPointOfInterest(pointOfInterest);
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
		boolean shouldRepaint = false;
		if (MandelbrotService.CALCULATION_IN_PROGRESS_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			percentage = (Integer) event.getNewValue();
			shouldRepaint = showProgress;
		} else if (MandelbrotService.CALCULATION_COMPLETE_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			percentage = null;
			shouldRepaint = true;
		}
		if (shouldRepaint) {
			image = imageProducerService.produceImage(points.getWidth(), points.getHeight(),
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
		addMenuItem(fileMenu, "Save snapshot", KeyEvent.VK_S, event -> saveImage());
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

		calculationsMenu.addSeparator();
		showProgressMenuItem = new JCheckBoxMenuItem("Show progress", showProgress);
		showProgressMenuItem.setMnemonic(KeyEvent.VK_R);
		showProgressMenuItem.addActionListener(event -> hideOrShowProgress());
		calculationsMenu.add(showProgressMenuItem);

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
