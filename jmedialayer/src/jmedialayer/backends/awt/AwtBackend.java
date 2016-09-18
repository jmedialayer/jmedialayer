package jmedialayer.backends.awt;

import jmedialayer.backends.Backend;
import jmedialayer.graphics.Bitmap32;
import jmedialayer.graphics.G1;
import jmedialayer.input.Input;
import jmedialayer.input.Keys;

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

		frame.getContentPane().setBackground(Color.black);
	}

	@Override
	public int getNativeWidth() {
		return width;
	}

	@Override
	public int getNativeHeight() {
		return height;
	}

	static private int rgbaToBgra(int rgba) {
		return (rgba & 0xFF00FF00) | ((rgba >> 16) & 0xFF) | ((rgba & 0xFF) << 16);
	}

	@Override
	protected G1 createG1() {
		return new G1() {
			@Override
			public void updateBitmap(Bitmap32 bmp) {
				final int minwidth = Math.min(bmp.width, width);
				final int minheight = Math.min(bmp.height, height);
				final int image_width = image.getWidth();
				final int[] bmp_data = bmp.data;
				final int bmp_width = bmp.width;
				final int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
				int ioffset = 0;
				int ooffset = 0;

				for (int y = 0; y < minheight; y++) {
					for (int x = 0; x < minwidth; x++) data[ooffset + x] = rgbaToBgra(bmp_data[ioffset + x]);
					ioffset += bmp_width;
					ooffset += image_width;
				}

				frontg.setComposite(AlphaComposite.Src);
				frontg.drawImage(image, 0, 0, null);
				frame.repaint();
			}
		};
	}

	@Override
	protected Input createInput() {
		return new Input() {
			@Override
			public boolean isPressing(Keys key) {
				switch (key) {
					case UP:
						return keys[KeyEvent.VK_UP];
					case DOWN:
						return keys[KeyEvent.VK_DOWN];
					case LEFT:
						return keys[KeyEvent.VK_LEFT];
					case RIGHT:
						return keys[KeyEvent.VK_RIGHT];
					case START:
						return keys[KeyEvent.VK_ENTER];
				}
				return false;
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
