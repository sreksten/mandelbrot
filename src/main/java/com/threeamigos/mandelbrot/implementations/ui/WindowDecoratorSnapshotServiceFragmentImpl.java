package com.threeamigos.mandelbrot.implementations.ui;

import java.awt.Font;
import java.awt.Graphics2D;

import com.threeamigos.mandelbrot.Resolution;
import com.threeamigos.mandelbrot.interfaces.service.SnapshotService;
import com.threeamigos.mandelbrot.interfaces.ui.FontService;
import com.threeamigos.mandelbrot.interfaces.ui.WindowDecoratorSnapshotServiceFragment;

public class WindowDecoratorSnapshotServiceFragmentImpl extends WindowDecoratorFragmentImpl
		implements WindowDecoratorSnapshotServiceFragment {

	private final SnapshotService snapshotService;

	public WindowDecoratorSnapshotServiceFragmentImpl(Resolution resolution, FontService fontService,
			SnapshotService snapshotService) {
		super(resolution);
		this.snapshotService = snapshotService;

		fontHeight = getWidth() == Resolution.SD.getWidth() ? 12 : 16;
		vSpacing = fontHeight + 4;
		font = fontService.getFont(FONT_NAME, Font.BOLD, fontHeight);
	}

	@Override
	public int paint(Graphics2D graphics, int xCoord, int yCoord) {
		if (isActive()) {
			int queueSize = snapshotService.getQueuedSnapshots();
			Integer percentage = snapshotService.getCurrentSnapshotPercentage();
			if (queueSize > 0 || percentage != null) {
				graphics.setFont(font);

				StringBuilder sb = new StringBuilder("Snapshot service: ");
				if (queueSize > 0) {
					sb.append(queueSize);
					if (queueSize == 1) {
						sb.append(" job");
					} else {
						sb.append(" jobs");
					}
					if (percentage != null) {
						sb.append(", ");
					}
				}
				if (percentage != null) {
					sb.append(percentage).append("% of current snapshot");
					drawString(graphics, sb.toString(), xCoord, yCoord);
					yCoord += vSpacing + 4;
				}
			}
		}
		return yCoord;
	}

}
