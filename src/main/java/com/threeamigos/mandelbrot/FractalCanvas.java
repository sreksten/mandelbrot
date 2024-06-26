package com.threeamigos.mandelbrot;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.threeamigos.common.util.interfaces.ui.AboutWindow;
import com.threeamigos.mandelbrot.implementations.ui.ResolutionEnum;
import com.threeamigos.mandelbrot.interfaces.persister.PersistResult;
import com.threeamigos.mandelbrot.interfaces.service.CalculationParameters;
import com.threeamigos.mandelbrot.interfaces.service.FractalService;
import com.threeamigos.mandelbrot.interfaces.service.FractalType;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerService;
import com.threeamigos.mandelbrot.interfaces.service.ImageProducerServiceFactory;
import com.threeamigos.mandelbrot.interfaces.service.PointOfInterest;
import com.threeamigos.mandelbrot.interfaces.service.Points;
import com.threeamigos.mandelbrot.interfaces.service.PointsOfInterestService;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.InputConsumer;
import com.threeamigos.mandelbrot.interfaces.ui.MessageNotifier;
import com.threeamigos.mandelbrot.interfaces.ui.RenderableConsumer;
import com.threeamigos.mandelbrot.interfaces.ui.Resolution;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorService;

public class FractalCanvas extends JPanel implements Runnable, InputConsumer, MessageNotifier, PropertyChangeListener {

	private static final long serialVersionUID = 1L;

	private transient FractalService fractalService;
	private transient PointsOfInterestService pointsOfInterestService;
	private transient ImageProducerServiceFactory imageProducerServiceFactory;
	private transient ImageProducerService imageProducerService;
	private transient SnapshotService snapshotService;
	private transient Points points;
	private transient WindowDecoratorService windowDecoratorService;
	private transient AboutWindow aboutWindow;
	private transient Resolution resolution;
	private transient CalculationParameters calculationParameters;

	private Integer currentPointOfInterestIndex = null;

	private transient List<RenderableConsumer> renderableConsumers = new ArrayList<>();

	private boolean showProgress = true;
	private boolean showSnapshotProgress = true;

	private transient Image image;

	private JMenu fractalTypeMenu;
	private JMenu pointsOfInterestMenu;
	private JMenu colorModelsMenu;
	private JMenu threadsMenu;
	private JMenu iterationsMenu;
	private JCheckBoxMenuItem showProgressMenuItem;

	public FractalCanvas(FractalService fractalService, PointsOfInterestService pointsOfInterestService,
			ImageProducerServiceFactory imageProducerServiceFactory, SnapshotService snapshotService, Points points,
			WindowDecoratorService windowDecoratorService, AboutWindow aboutWindow, Resolution resolution,
			CalculationParameters calculationParameters) {
		super();
		this.fractalService = fractalService;
		this.pointsOfInterestService = pointsOfInterestService;
		this.imageProducerServiceFactory = imageProducerServiceFactory;
		this.snapshotService = snapshotService;
		this.points = points;
		this.windowDecoratorService = windowDecoratorService;
		this.aboutWindow = aboutWindow;
		this.resolution = resolution;
		this.calculationParameters = calculationParameters;

		imageProducerService = imageProducerServiceFactory.createInstance(calculationParameters.getMaxIterations());
		windowDecoratorService.setImageProducerService(imageProducerService);

		setSize(points.getWidth(), points.getHeight());
		setBackground(Color.YELLOW);
		setFocusable(true);
		setDoubleBuffered(true);

		addMouseWheelListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	public void addRenderableConsumer(RenderableConsumer inputConsumer) {
		renderableConsumers.add(inputConsumer);
	}

	public void startCalculationThread() {
		Thread paintingThread = new Thread(null, this, "UIThread");
		paintingThread.setDaemon(true);
		paintingThread.start();
	}

	@Override
	public void run() {
		fractalService.calculate(points, resolution, calculationParameters);
	}

	@Override
	public void paintComponent(Graphics gfx) {
		super.paintComponent(gfx);
		Graphics2D graphics = (Graphics2D) gfx;
		graphics.drawImage(image, 0, 0, null);
		int coordinate = resolution.getWidth() == ResolutionEnum.SD.getWidth() ? 20 : 50;
		windowDecoratorService.paint(graphics, coordinate, coordinate);
		renderableConsumers.stream().forEach(r -> r.paint(graphics));
	}

	private void setCurrentPointOfInterestIndex(Integer currentPointOfInterestIndex) {
		this.currentPointOfInterestIndex = currentPointOfInterestIndex;
		windowDecoratorService.setCurrentPointOfInterestIndex(currentPointOfInterestIndex);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		boolean shouldRepaint = false;
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.mouseWheelMoved(e);
			if (e.isConsumed()) {
				shouldRepaint = true;
				break;
			}
		}
		if (!e.isConsumed()) {
			setCurrentPointOfInterestIndex(null);
			boolean shouldRestart;
			if (e.getWheelRotation() < 0) {
				shouldRestart = points.zoomIn(e.getX(), e.getY());
			} else {
				shouldRestart = points.zoomOut(e.getX(), e.getY());
			}
			if (shouldRestart) {
				startCalculationThread();
				shouldRepaint = true;
			}
		}
		if (shouldRepaint) {
			repaint();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		boolean shouldRepaint = false;
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.mouseClicked(e);
			if (e.isConsumed()) {
				shouldRepaint = true;
				break;
			}
		}
		if (!e.isConsumed()) {
			setCurrentPointOfInterestIndex(null);
			if (e.getClickCount() == 1) {
				points.changeCenterTo(e.getX(), e.getY());
			} else {
				points.reset();
			}
			startCalculationThread();
			shouldRepaint = true;
		}
		if (shouldRepaint) {
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		boolean shouldRepaint = false;
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.mousePressed(e);
			if (e.isConsumed()) {
				shouldRepaint = true;
				break;
			}
		}
		if (shouldRepaint) {
			repaint();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.mouseDragged(e);
			if (e.isConsumed()) {
				break;
			}
		}
		updatePointerCoordinates(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		boolean shouldRepaint = false;
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.mouseReleased(e);
			if (e.isConsumed()) {
				shouldRepaint = true;
				break;
			}
		}
		if (shouldRepaint) {
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.mouseEntered(e);
			if (e.isConsumed()) {
				break;
			}
		}
		updatePointerCoordinates(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.mouseExited(e);
			if (e.isConsumed()) {
				break;
			}
		}
		points.updatePointerCoordinates(null, null);
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.mouseMoved(e);
			if (e.isConsumed()) {
				break;
			}
		}
		updatePointerCoordinates(e);
	}

	private void updatePointerCoordinates(MouseEvent e) {
		points.updatePointerCoordinates(e.getX(), e.getY());
		repaint();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		boolean shouldRepaint = false;
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.keyTyped(e);
			if (e.isConsumed()) {
				shouldRepaint = true;
				break;
			}
		}
		if (shouldRepaint) {
			repaint();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		boolean shouldRepaint = false;
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.keyPressed(e);
			if (e.isConsumed()) {
				shouldRepaint = true;
				break;
			}
		}
		if (!e.isConsumed()) {
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
			case KeyEvent.VK_J:
				setFractalType(FractalType.JULIA);
				break;
			case KeyEvent.VK_M:
				setFractalType(FractalType.MANDELBROT);
				break;
			case KeyEvent.VK_P:
				hideOrShowPointOfInterestName();
				break;
			case KeyEvent.VK_R:
				hideOrShowProgress();
				break;
			case KeyEvent.VK_S:
				saveSnapshot();
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
		if (shouldRepaint) {
			repaint();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		boolean shouldRepaint = false;
		for (RenderableConsumer consumer : renderableConsumers) {
			consumer.keyReleased(e);
			if (e.isConsumed()) {
				shouldRepaint = true;
				break;
			}
		}
		if (shouldRepaint) {
			repaint();
		}
	}

	private void setMaxIterations(int maxIterations) {
		if (calculationParameters.setMaxIterations(maxIterations)) {
			updateImageProducer(calculationParameters.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void doubleUpMaxIterations() {
		if (calculationParameters.doubleUpMaxIterations()) {
			updateImageProducer(calculationParameters.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void halveMaxIterations() {
		if (calculationParameters.halveMaxIterations()) {
			updateImageProducer(calculationParameters.getMaxIterations());
			updateIterationsMenu();
			startCalculationThread();
		}
	}

	private void switchColorModel(String colorModelName) {
		imageProducerService.switchColorModel(colorModelName);
		updateColorModelsMenu();
		image = imageProducerService.produceImage(points.getWidth(), points.getHeight(),
				fractalService.getIterations());
		repaint();
	}

	private void setNumberOfThreads(int numberOfThreads) {
		if (calculationParameters.setMaxThreads(numberOfThreads)) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void incrementThreads() {
		if (calculationParameters.incrementMaxThreads()) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void decrementThreads() {
		if (calculationParameters.decrementMaxThreads()) {
			updateThreadsMenu();
			startCalculationThread();
		}
	}

	private void addPointOfInterest() {
		PersistResult persistResult = pointsOfInterestService
				.add(points.getPointOfInterest(calculationParameters.getMaxIterations()));
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
				fractalService.getIterations());
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
		windowDecoratorService.toggleShowHelp();
		repaint();
	}

	private void hideOrShowSnapshotServiceStatus() {
		windowDecoratorService.toggleShowSnapshotServiceStatus();
		repaint();
	}

	private void hideOrShowInfo() {
		windowDecoratorService.toggleShowInfo();
		repaint();
	}

	private void hideOrShowPointOfInterestName() {
		windowDecoratorService.toggleShowPointOfInterestName();
		repaint();
	}

	private void hideOrShowProgress() {
		showProgress = !showProgress;
		showProgressMenuItem.setSelected(showProgress);
	}

	private void saveSnapshot() {
		snapshotService.saveSnapshot(points, calculationParameters.getMaxIterations(),
				imageProducerService.getCurrentColorModelName(), image, this);
	}

	private void setPointOfInterest(int pointIndex) {
		if (pointsOfInterestService.getCount() >= pointIndex) {
			setCurrentPointOfInterestIndex(pointIndex);
			PointOfInterest pointOfInterest = pointsOfInterestService.getElements().get(pointIndex - 1);
			points.setPointOfInterest(pointOfInterest);
			points.setFractalType(pointOfInterest.getFractalType());
			updateFractalTypeMenu();
			if (pointOfInterest.getMaxIterations() != calculationParameters.getMaxIterations()) {
				updateImageProducer(pointOfInterest.getMaxIterations());
				calculationParameters.setMaxIterations(pointOfInterest.getMaxIterations());
				updateIterationsMenu();
			}
			startCalculationThread();
		}
	}

	private void setFractalType(FractalType fractalType) {
		if (points.getFractalType() != fractalType) {
			points.setFractalType(fractalType);
			setCurrentPointOfInterestIndex(null);
			updateFractalTypeMenu();
			startCalculationThread();
		}
	}

	private void updateImageProducer(int maxIterations) {
		String currentColorModelName = imageProducerService.getCurrentColorModelName();
		imageProducerService = imageProducerServiceFactory.createInstance(maxIterations);
		imageProducerService.switchColorModel(currentColorModelName);
		windowDecoratorService.setImageProducerService(imageProducerService);
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
		boolean shouldRestartCalculation = false;
		if (FractalService.CALCULATION_IN_PROGRESS_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			windowDecoratorService.setPercentage((Integer) event.getNewValue());
			if (showProgress) {
				shouldRepaint = true;
				image = imageProducerService.produceImage(points.getWidth(), points.getHeight(),
						fractalService.getIterations());
			}
		} else if (FractalService.CALCULATION_COMPLETE_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			windowDecoratorService.setPercentage(null);
			shouldRepaint = true;
			image = imageProducerService.produceImage(points.getWidth(), points.getHeight(),
					fractalService.getIterations());
		} else if (FractalService.BACKGROUND_CALCULATION_IN_PROGRESS_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			shouldRepaint = showSnapshotProgress;
		} else if (FractalService.BACKGROUND_CALCULATION_COMPLETE_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			shouldRepaint = showSnapshotProgress;
		} else if (FractalService.CALCULATION_RESTART_REQUIRED_PROPERTY_CHANGE.equals(event.getPropertyName())) {
			setCurrentPointOfInterestIndex(null);
			shouldRestartCalculation = true;
		}
		if (shouldRestartCalculation) {
			startCalculationThread();
		}
		if (shouldRepaint) {
			repaint();
		}
	}

	public void addMenus(JMenuBar menuBar) {
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		addCheckboxMenuItem(fileMenu, "Show info", KeyEvent.VK_I, windowDecoratorService.isShowInfoActive(),
				event -> hideOrShowInfo());
		addCheckboxMenuItem(fileMenu, "Show help", KeyEvent.VK_H, windowDecoratorService.isShowHelpActive(),
				event -> hideOrShowHelp());
		fileMenu.addSeparator();
		addMenuItem(fileMenu, "Save snapshot", KeyEvent.VK_S, event -> saveSnapshot());
		addCheckboxMenuItem(fileMenu, "Show snapshot progress", KeyEvent.VK_H,
				windowDecoratorService.isShowSnapshotServiceStatusActive(), event -> hideOrShowSnapshotServiceStatus());

		fileMenu.addSeparator();
		addMenuItem(fileMenu, "About", KeyEvent.VK_S, event -> about());
		fileMenu.addSeparator();
		addMenuItem(fileMenu, "Quit", KeyEvent.VK_Q, event -> System.exit(0));

		// -----

		fractalTypeMenu = new JMenu("Fractal type");
		menuBar.add(fractalTypeMenu);

		addCheckboxMenuItem(fractalTypeMenu, FractalType.JULIA.getDescription(), KeyEvent.VK_J,
				points.getFractalType() == FractalType.JULIA, event -> setFractalType(FractalType.JULIA));
		addCheckboxMenuItem(fractalTypeMenu, FractalType.MANDELBROT.getDescription(), KeyEvent.VK_M,
				points.getFractalType() == FractalType.MANDELBROT, event -> setFractalType(FractalType.MANDELBROT));

		// -----

		pointsOfInterestMenu = new JMenu("Points of interest");
		menuBar.add(pointsOfInterestMenu);

		addMenuItem(pointsOfInterestMenu, "Add point of interest", KeyEvent.VK_A, event -> addPointOfInterest());
		addMenuItem(pointsOfInterestMenu, "Delete current point of interest", KeyEvent.VK_D,
				event -> deletePointOfInterest());
		pointsOfInterestMenu.addSeparator();
		addCheckboxMenuItem(pointsOfInterestMenu, "Show point of interest's name", KeyEvent.VK_P,
				windowDecoratorService.isShowPointOfInterestNameActive(), event -> hideOrShowPointOfInterestName());
		pointsOfInterestMenu.addSeparator();
		updatePointsOfInterestMenu();

		// -----

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
			addCheckboxMenuItem(threadsMenu, String.valueOf(i), -1, calculationParameters.getMaxThreads() == i,
					event -> setNumberOfThreads(threadsToUse));
		}
		calculationsMenu.addSeparator();
		iterationsMenu = new JMenu("Max iterations");
		for (int i = CalculationParameters.MIN_ITERATIONS_EXPONENT; i <= CalculationParameters.MAX_ITERATIONS_EXPONENT; i++) {
			final int maxIterations = 1 << i;
			addCheckboxMenuItem(iterationsMenu, String.valueOf(maxIterations), -1,
					calculationParameters.getMaxIterations() == maxIterations,
					event -> setMaxIterations(maxIterations));
		}
		calculationsMenu.add(iterationsMenu);

		calculationsMenu.addSeparator();
		showProgressMenuItem = new JCheckBoxMenuItem("Show progress", showProgress);
		showProgressMenuItem.setMnemonic(KeyEvent.VK_R);
		showProgressMenuItem.addActionListener(event -> hideOrShowProgress());
		calculationsMenu.add(showProgressMenuItem);

	}

	private void about() {
		aboutWindow.about(this);
	}

	private void updateFractalTypeMenu() {
		Component[] items = fractalTypeMenu.getMenuComponents();
		for (int i = 0; i < items.length; i++) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) items[i];
			item.setSelected(points.getFractalType().getDescription().equals(item.getText()));
		}
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
			item.setSelected(calculationParameters.getMaxThreads() == i + 1);
		}
	}

	private void updateIterationsMenu() {
		Component[] items = iterationsMenu.getMenuComponents();
		for (int i = 0; i < items.length; i++) {
			JCheckBoxMenuItem item = (JCheckBoxMenuItem) items[i];
			final int maxIterations = 1 << i + CalculationParameters.MIN_ITERATIONS_EXPONENT;
			item.setSelected(calculationParameters.getMaxIterations() == maxIterations);
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

}
