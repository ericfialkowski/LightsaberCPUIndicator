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
	private Timer timer;

	public void init() throws IOException
	{
		this.setUndecorated(true);
        this.setType(javax.swing.JFrame.Type.UTILITY);
		URL url = this.getClass().getResource("/img/hilt.png");
		BufferedImage image = ImageIO.read(url);
		ImageIcon icon = new ImageIcon(image);
		JLabel hiltLabel = new JLabel(icon, JLabel.CENTER);
		setBackground(new Color(0,0,0,0));
//		this.setOpacity(0.5f);
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

		timer = new Timer(5000, (ActionEvent e) ->
		{
			blade.setBladePercent(CPU.SystemCpuLoad());
		});
		timer.setInitialDelay(15000);
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

	private final class JBlade extends JPanel
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
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			g.clearRect(0, 0,getWidth(), getHeight());
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			if ( bladePercent > .95)
				g2d.setColor(new Color(200, 0, 0));
			else if ( bladePercent > .90)
				g2d.setColor(new Color(200, 200, 0));
			else
				g2d.setColor(new Color(0, 200, 0));
			int bladeHeight = (int) (getHeight() * bladePercent);
			g2d.fillRect(24, getHeight() - bladeHeight+30, 26, bladeHeight);
			g2d.fillOval(24, getHeight() - bladeHeight+15, 26, 26);

			g2d.setColor(g2d.getColor().brighter());
			g2d.fillRect(30, getHeight() - bladeHeight+30, 14, bladeHeight);
			g2d.fillOval(30, getHeight() - bladeHeight+22, 14, 14);

			g2d.setColor(Color.WHITE);
			g2d.fillRect(36, getHeight() - bladeHeight+30, 2, bladeHeight);
			g2d.fillOval(36, getHeight() - bladeHeight+28, 2, 2);
			g2d.dispose();
		}
	}
}
