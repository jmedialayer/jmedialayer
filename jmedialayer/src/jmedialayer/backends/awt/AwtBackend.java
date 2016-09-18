package jmedialayer.backends.awt;

import jmedialayer.backends.Backend;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.G1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class AwtBackend extends Backend {
	private final int width;
	private final int height;

	JFrame frame = new JFrame("jmedialayer");
	BufferedImage front;
	BufferedImage image;
	Graphics2D frontg;
	Graphics2D g;
	boolean[] keys = new boolean[KeyEvent.KEY_LAST];

	public AwtBackend(int width, int height) {
		this.width = width;
		this.height = height;
		init();
	}

	public AwtBackend() {
		this(960, 544);
	}

	private void init() {
		front = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		frontg = front.createGraphics();
		g = image.createGraphics();

		JLabel label = new JLabel(new ImageIcon(front));
		label.setSize(width, height);
		frame.add(label);
		//frame.setSize(width, height);
		frame.pack();
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				super.keyTyped(e);
			}

			@Override
			public void keyPressed(KeyEvent e) {
				keys[e.getKeyCode() & 0x3FF] = true;
			}

			@Override
			public void keyReleased(KeyEvent e) {
				keys[e.getKeyCode() & 0x3FF] = false;
			}
		});
	}

	@Override
	public int getNativeWidth() {
		return width;
	}

	@Override
	public int getNativeHeight() {
		return height;
	}

	@Override
	protected G1 createG1() {
		return new G1() {
			@Override
			public void updateBitmap(Bitmap32 bmp) {
				int minwidth = Math.min(bmp.width, width);
				int minheight = Math.min(bmp.height, height);
				int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

				for (int y = 0; y < minheight; y++) {
					System.arraycopy(bmp.data, bmp.index(0, y), data, y * width, width);
				}

				frontg.drawImage(image, 0, 0, null);
				frame.repaint();
			}
		};
	}

	@Override
	protected void waitNextFrame() {
		super.waitNextFrame();
	}

	@Override
	protected void preEnd() {
		frame.setVisible(false);
		frame.dispose();
	}
}