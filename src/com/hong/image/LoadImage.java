package com.hong.image;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/*
 * https://docs.oracle.com/javase/tutorial/2d/images/loadimage.html
 * https://stackoverflow.com/questions/6524196/java-get-pixel-array-from-image
 */

public class LoadImage extends Component {
	BufferedImage img;
	byte[] pixels;
	int pixelLength;
	boolean hasAlphaChannel;

	public void paint(Graphics g) {
		g.drawImage(img, 0, 0, null);
	}

	public LoadImage() {
		try {
			img = ImageIO.read(new File("src/lenna-lg.jpg"));
			getInfo();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public Dimension getPreferredSize() {
		if (img == null) {
			return new Dimension(100, 100);
		} else {
			return new Dimension(img.getWidth(null), img.getHeight(null));
		}
	}
	
	public void getInfo() {
		pixels = ((DataBufferByte)(img.getRaster().getDataBuffer())).getData();
		pixelLength = 3;
		hasAlphaChannel = img.getAlphaRaster() != null;
        if (hasAlphaChannel)
        {
            pixelLength = 4;
        }
	}
	
	public void getPointColor(int x, int y) {
		int argb = img.getRGB(x, y);
		Color c = new Color(argb);
		System.out.println("ARGB : " + argb + " R:" + c.getRed() + " G:" + c.getGreen() + "B :" + c.getBlue() + " Alpha :" + c.getAlpha());
		getRGB(x, y);
	}
	
	protected int getRGB(int x, int y)
    {
        int pos = (y * 3 * img.getWidth()) + (x * 3);

        int argb = -16777216; // 255 alpha
        if (hasAlphaChannel)
        {
            argb = (((int) pixels[pos++] & 0xff) << 24); // alpha
        }

        argb += ((int) pixels[pos++] & 0xff); // blue
        argb += (((int) pixels[pos++] & 0xff) << 8); // green
        argb += (((int) pixels[pos++] & 0xff) << 16); // red
        System.out.println("RGB : " + argb);
        return argb;
    }

	public static void main(String[] args) {

		JFrame f = new JFrame("Load Image Sample");

		LoadImage img = new LoadImage();
		
		f.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
			}
			@Override
			public void windowActivated(WindowEvent e) {
				super.windowActivated(e);
			}
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}
			@Override
			public void windowStateChanged(WindowEvent e) {
				super.windowStateChanged(e);
			}			
		});
		
		f.addMouseListener(new MouseAdapter() {
		    public void mouseClicked(MouseEvent e) {
		        System.out.println(e.getPoint());
		        System.out.println(e.getX() + ", " + e.getY());
		        System.out.println(e.getXOnScreen() + ", " + e.getYOnScreen());
		        img.getPointColor(e.getX(), e.getY());
		    }
		});

		
		f.add(img);
		f.pack();
		f.setVisible(true);
	}
}
