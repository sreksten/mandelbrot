package com.threeamigos.mandelbrot;

import java.awt.Color;
import java.awt.Component;
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
import com.threeamigos.mandelbrot.interfaces.ui.ShowHelp;
import com.threeamigos.mandelbrot.interfaces.ui.ShowInfo;
import com.threeamigos.mandelbrot.interfaces.ui.ShowPointOfInterestName;
import com.threeamigos.mandelbrot.interfaces.ui.ZoomBox;

public class MandelbrotCanvas extends JPanel implements Runnable, MouseWheelListener, MouseInputListener,
		MouseMotionListener, KeyListener, MessageNotifier, PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private transient MandelbrotService mandelbrotService;
	private transient PointsOfInterestService pointsOfInterestService;
	private transient ImageProducerServiceFactory imageProducerServiceFactory;
	private transient ImageProducerService imageProducerService;
	private transient SnapshotService snapshotService;
	private transient Points points;

	private transient ShowInfo showInfo;
	private transient ShowHelp showHelp;
	private transient ShowPointOfInterestName showPointOfInterestName;
	private Integer currentPointOfInterestIndex = null;

	private boolean showProgress = true;

	private transient ZoomBox zoomBox;

	private transient Thread paintingThread;

	private transient Image image;

	private JMenu pointsOfInterestMenu;
	private JMenu colorModelsMenu;
	private JMenu threadsMenu;
	private JMenu iterationsMenu;
	private JCheckBoxMenuItem showProgressMenuItem;

	public MandelbrotCanvas(MandelbrotService mandelbrotService, PointsOfInterestService pointsOfInterestService,
			ImageProducerServiceFactory imageProducerServiceFactory, SnapshotService snapshotService, Points points,
			CalculationParameters calculationParameters, ZoomBox zoomBox, ShowInfo showInfo, ShowHelp showHelp,
			ShowPointOfInterestName showPointOfInterestName) {
		super();
		this.pointsOfInterestService = pointsOfInterestService;
		this.imageProducerServiceFactory = imageProducerServiceFactory;
		this.imageProducerService = imageProducerServiceFactory
				.createInstance(calculationParameters.getMaxIterations());
		showHelp.setImageProducerService(imageProducerService);
		this.snapshotService = snapshotService;
		this.points = points;

		setSize(points.getWidth(), points.getHeight());
		setBackground(Color.YELLOW);
		setFocusable(true);
		setDoubleBuffered(true);

		this.mandelbrotService = mandelbrotService;
		this.showInfo = showInfo;
		this.showHelp = showHelp;
		this.showPointOfInterestName = showPointOfInterestName;

		this.zoomBox = zoomBox;

		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	public void startCalculationThread() {
		paintingThread = new Thread(this);
		paintingThread.setDaemon(true);
		paintingThread.start();
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

		yCoord = showInfo.paint(graphics, xCoord, yCoord);
		showHelp.paint(graphics, xCoord, yCoord);
		showPointOfInterestName.paint(graphics, -1, -1);
		zoomBox.draw(graphics);
	}

	private void setCurrentPointOfInterestIndex(Integer currentPointOfInterestIndex) {
		this.currentPointOfInterestIndex = currentPointOfInterestIndex;
		showHelp.setCurrentPointOfInterestIndex(currentPointOfInterestIndex);
		showPointOfInterestName.setCurrentPointOfInterestIndex(currentPointOfInterestIndex);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		setCurrentPointOfInterestIndex(null);
		boolean shouldRestart;
		if (e.getWheelRotation() < 0) {
			shouldRestart = points.zoomIn(e.getX(), e.getY());
		} else {
			shouldRestart = points.zoomOut(e.getX(), e.getY());
		}
		if (shouldRestart) {
			startCalculationThread();
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		setCurrentPointOfInterestIndex(null);
		if (e.getClickCount() == 1) {
			points.changeCenterTo(e.getX(), e.getY());
		} else {
			points.reset();
		}
		startCalculationThread();
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (zoomBox.mousePressed(e)) {
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (zoomBox.mouseReleased(e)) {
			setCurrentPointOfInterestIndex(null);
			startCalculationThread();
		}
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
		zoomBox.mouseDragged(e);
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
		boolean needRepainting = zoomBox.keyTyped(e);
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
		if (needRepainting) {
			repaint();
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
		image = imageProducerService.produceImage(points.getWidth(), points.getHeight(),
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
				.add(points.getPointOfInterest(mandelbrotService.getMaxIterations()));
		if (persistResult != null && persistResult.isSuccessful()) {
			setCurrentPointOfInterestIndex(pointsOfInterestService.getCount());
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
			setCurrentPointOfInterestIndex(null);
			pointsOfInterestService.remove(backupOfCurrentPointOfInterestIndex - 1);
			PersistResult result = pointsOfInterestService.savePointsOfInterest();
			if (result.isSuccessful()) {
				updatePointsOfInterestMenu();
				repaint();
			} else {
				setCurrentPointOfInterestIndex(backupOfCurrentPointOfInterestIndex);
			}
		}
	}

	private void hideOrShowHelp() {
		showHelp.toggleActive();
		repaint();
	}

	private void hideOrShowInfo() {
		showInfo.toggleActive();
		repaint();
	}

	private void hideOrShowPointOfInterestName() {
		showPointOfInterestName.toggleActive();
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
			setCurrentPointOfInterestIndex(pointIndex);
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
			showInfo.setPercentage((Integer) event.getNewValue());
			shouldRepaint = showProgress;
		} else if (MandelbrotService.CALCULATION_COMPLETE_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			showInfo.setPercentage(null);
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

		addCheckboxMenuItem(fileMenu, "Show info", KeyEvent.VK_I, showInfo.isActive(), event -> hideOrShowInfo());
		addCheckboxMenuItem(fileMenu, "Show help", KeyEvent.VK_H, showHelp.isActive(), event -> hideOrShowHelp());
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
				showPointOfInterestName.isActive(), event -> hideOrShowPointOfInterestName());
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
		showHelp.setImageProducerService(imageProducerService);
	}
}
