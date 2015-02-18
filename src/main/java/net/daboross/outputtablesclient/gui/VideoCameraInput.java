package net.daboross.outputtablesclient.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class VideoCameraInput extends JPanel {

    private static final int[] START_BYTES = new int[]{255, 216};
    private static final int[] END_BYTES = new int[]{255, 217};
    private boolean ipChanged;
    private String ipString;
    private long lastFPSCheck;
    private int lastFPS;
    private int fpsCounter;
    private BufferedImage imageToDraw;
    private BGThread bgThread;

    public VideoCameraInput() {
        super();
        this.ipChanged = true;
        this.ipString = null;
        this.lastFPSCheck = 0L;
        this.lastFPS = 0;
        this.fpsCounter = 0;
        this.bgThread = new BGThread();
        this.ipString = "axis1.local";
    }

    public void init() {
        this.setPreferredSize(new Dimension(100, 100));
        this.bgThread.start();
        this.revalidate();
        this.repaint();
    }

//    @Override
//    public void propertyChanged(final Property property) {
//        if (property == this.ipProperty) {
//            this.ipString = this.ipProperty.getSaveValue();
//            this.ipChanged = true;
//        }
//    }

//    @Override
//    public void disconnect() {
//        this.bgThread.destroy();
//    }

    @Override
    protected void paintComponent(final Graphics g) {
        final BufferedImage drawnImage = this.imageToDraw;
        if (drawnImage != null) {
            final int width = this.getBounds().width;
            final int height = this.getBounds().height;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            final double scale = Math.min(width / drawnImage.getWidth(), height / drawnImage.getHeight());
            g.drawImage(drawnImage, (int) (width - scale * drawnImage.getWidth()) / 2, (int) (height - scale * drawnImage.getHeight()) / 2, (int) ((width + scale * drawnImage.getWidth()) / 2.0), (int) (height + scale * drawnImage.getHeight()) / 2, 0, 0, drawnImage.getWidth(), drawnImage.getHeight(), null);
            g.setColor(Color.PINK);
            g.drawString("FPS: " + this.lastFPS, 10, 10);
        } else {
            g.setColor(Color.PINK);
            g.fillRect(0, 0, this.getBounds().width, this.getBounds().height);
            g.setColor(Color.BLACK);
            g.drawString("NO CONNECTION", 10, 10);
        }
    }

    public class BGThread extends Thread {

        boolean destroyed;
        long lastRepaint;

        public BGThread() {
            super("Camera Viewer Background");
            this.destroyed = false;
            this.lastRepaint = 0L;
        }

        @Override
        public void run() {
            URLConnection connection;
            InputStream stream;
            final ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
            while (!this.destroyed) {
                System.out.println("Connecting...");
                out_of_try:
                try {
                    ipChanged = false;
                    final URL url = new URL("http://" + ipString + "/mjpg/video.mjpg");
                    connection = url.openConnection();
                    connection.setReadTimeout(250);
                    try {
                        stream = connection.getInputStream();
                    } catch (IOException ex) {
                        System.err.printf("Failed to connect to `%s`: %s%n", url, ex);
                        break out_of_try;
                    }
                    while (!this.destroyed && !ipChanged) {
                        while (System.currentTimeMillis() - this.lastRepaint < 10L) {
                            stream.skip(stream.available());
                            Thread.sleep(1L);
                        }
                        stream.skip(stream.available());
                        imageBuffer.reset();
                        int i = 0;
                        while (i < START_BYTES.length) {
                            final int b = stream.read();
                            if (b == START_BYTES[i]) {
                                ++i;
                            } else {
                                i = 0;
                            }
                        }
                        for (i = 0; i < START_BYTES.length; ++i) {
                            imageBuffer.write(START_BYTES[i]);
                        }
                        i = 0;
                        while (i < END_BYTES.length) {
                            final int b = stream.read();
                            imageBuffer.write(b);
                            if (b == END_BYTES[i]) {
                                ++i;
                            } else {
                                i = 0;
                            }
                        }
                        fpsCounter++;
                        if (System.currentTimeMillis() - lastFPSCheck > 500L) {
                            lastFPSCheck = System.currentTimeMillis();
                            lastFPS = fpsCounter * 2;
                            fpsCounter = 0;
                        }
                        this.lastRepaint = System.currentTimeMillis();
                        final ByteArrayInputStream tmpStream = new ByteArrayInputStream(imageBuffer.toByteArray());
                        imageToDraw = ImageIO.read(tmpStream);
//                        System.out.println(System.currentTimeMillis() - this.lastRepaint);
                        repaint();
                    }
                } catch (Exception e) {
                    imageToDraw = null;
                    repaint();
                    e.printStackTrace();
                }
                if (!ipChanged) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException ex) {
                    }
                }
            }
        }
    }
}
