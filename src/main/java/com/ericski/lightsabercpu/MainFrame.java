package com.ericski.lightsabercpu;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainFrame extends JFrame
{
	private JBlade blade;

    public void init() throws IOException
	{
		this.setUndecorated(true);
        this.setType(javax.swing.JFrame.Type.UTILITY);
		URL url = this.getClass().getResource("/img/hilt.png");
		if (url == null) {
			System.err.println("Couldn't find the embedded image, the is a corrupt binary");
			System.exit(100);
		}
		BufferedImage image = ImageIO.read(url);
		ImageIcon icon = new ImageIcon(image);
		JLabel hiltLabel = new JLabel(icon, JLabel.CENTER);
		setBackground(new Color(0,0,0,0));

		this.getRootPane().setLayout(new BoxLayout(this.getRootPane(), BoxLayout.Y_AXIS));
		this.getRootPane().add(blade = new JBlade(), CENTER_ALIGNMENT);
		this.getRootPane().add(hiltLabel, CENTER_ALIGNMENT);
		this.setResizable(false);

		MouseAdapter ma = new MouseAdapter()
		{
			int lastX, lastY;

			@Override
			public void mousePressed(MouseEvent e)
			{
				lastX = e.getXOnScreen();
				lastY = e.getYOnScreen();
			}

			@Override
			public void mouseDragged(MouseEvent e)
			{
				int x = e.getXOnScreen();
				int y = e.getYOnScreen();
				// Move frame by the mouse delta
				setLocation(getLocationOnScreen().x + x - lastX,
							getLocationOnScreen().y + y - lastY);
				lastX = x;
				lastY = y;
			}
		};
		addMouseListener(ma);
		addMouseMotionListener(ma);

		addKeyListener(new KeyAdapter()
		{

			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					System.exit(0);
				}
			}
		});

        Timer timer = new Timer(1000, (ActionEvent e) ->
        {
            blade.setBladePercent(CPU.SystemCpuLoad());
        });
		timer.setInitialDelay(10000);
		timer.start();
		this.pack();
	}

	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(75, 900);
	}

	@Override
	public Dimension getMinimumSize()
	{
		return new Dimension(75, 900);
	}

	@Override
	public Dimension getMaximumSize()
	{
		return new Dimension(75, 900);
	}

	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(()
			->
			{
				try
				{
					JFrame.setDefaultLookAndFeelDecorated(true);
					try
					{
						// Set System L&F
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
					catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
					{
						// handle exception
					}
					MainFrame frame = new MainFrame();
					frame.init();
					frame.toFront();
					frame.setLocationRelativeTo(null);
					frame.setVisible(true);
					frame.setAlwaysOnTop(true);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				}
				catch (IOException ex)
				{
					Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
					// handle exception
				}
		});
	}

	private static final class JBlade extends JPanel
	{
		private double bladePercent = .70;

		public JBlade()
		{
			setOpaque(false);
		}

		public void setBladePercent(double bladePercent)
		{
			this.bladePercent = .70 + (.30 * bladePercent);
			repaint();
		}

		@Override
		public Dimension getPreferredSize()
		{
			return new Dimension(75, 500);
		}

		@Override
		public void paintComponent(Graphics graphics)
		{
			super.paintComponent(graphics);
			graphics.setColor(new Color(0,0,0,0));
			graphics.fillRect(0, 0,getWidth(), getHeight());
			Graphics2D g2d = (Graphics2D) graphics.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int r = 0;
			int ri = 0;
			int g = 0;
			int gi = 0;

			if ( bladePercent > .95)
			{
				r = 200;
				ri = 7;
			}
			else if ( bladePercent > .90)
			{
				r = 200;
				ri = 7;
				g = 200;
				gi = 7;
			}
			else
			{
				g = 200;
				gi = 7;
			}
			g2d.setColor(new Color(r, g, 0));
			int bladeHeight = (int) (getHeight() * bladePercent);

			for (int i = 0; i < 7; i++)
			{
				g2d.fillRect(24+i, getHeight() - bladeHeight+30, 26-(i*2), bladeHeight);
				g2d.fillOval(24+i, getHeight() - bladeHeight+15+i, 26-(i*2), 26-(i*2));
				r += ri;
				g += gi;
				g2d.setColor(new Color(r, g, 0));
			}

			g2d.setColor(new Color(255-ri,255-gi,255));
			g2d.fillRect(36, getHeight() - bladeHeight+30, 2, bladeHeight);
			g2d.fillOval(36, getHeight() - bladeHeight+28, 2, 2);
			g2d.dispose();
		}
	}
}
