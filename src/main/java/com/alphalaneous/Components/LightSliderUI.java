package com.alphalaneous.Components;

import com.alphalaneous.Components.ThemableJComponents.ThemeableColor;

import javax.swing.*;
import javax.swing.plaf.basic.BasicSliderUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;

public class LightSliderUI extends BasicSliderUI {

    private final Color rangeColor = ThemeableColor.getColorByName("accent");
    private final BasicStroke stroke = new BasicStroke(2f);
    private transient boolean upperDragging;

    public LightSliderUI(JSlider b) {
        super(b);
    }

    @Override
    protected Color getFocusColor(){
        return new Color(0,0,0,0);
    }

    @Override
    protected void calculateThumbSize() {
        super.calculateThumbSize();
        thumbRect.setSize(thumbRect.width, thumbRect.height);
    }

    @Override
    protected TrackListener createTrackListener(JSlider slider) {
        return new RangeTrackListener();
    }

    @Override
    protected void calculateThumbLocation() {
        super.calculateThumbLocation();

        if (slider.getSnapToTicks()) {
            int upperValue = slider.getValue() + slider.getExtent();
            int snappedValue = upperValue;
            int majorTickSpacing = slider.getMajorTickSpacing();
            int minorTickSpacing = slider.getMinorTickSpacing();
            int tickSpacing = 0;

            if (minorTickSpacing > 0) {
                tickSpacing = minorTickSpacing;
            } else if (majorTickSpacing > 0) {
                tickSpacing = majorTickSpacing;
            }

            if (tickSpacing != 0) {
                if ((upperValue - slider.getMinimum()) % tickSpacing != 0) {
                    float temp = (float) (upperValue - slider.getMinimum()) / (float) tickSpacing;
                    int whichTick = Math.round(temp);
                    snappedValue = slider.getMinimum() + (whichTick * tickSpacing);
                }

                if (snappedValue != upperValue) {
                    slider.setExtent(snappedValue - slider.getValue());
                }
            }
        }

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            int upperPosition = xPositionForValue(slider.getValue() + slider.getExtent());
            thumbRect.x = upperPosition - (thumbRect.width / 2);
            thumbRect.y = trackRect.y;

        } else {
            int upperPosition = yPositionForValue(slider.getValue() + slider.getExtent());
            thumbRect.x = trackRect.x;
            thumbRect.y = upperPosition - (thumbRect.height / 2);
        }
        slider.repaint();
    }
    @Override
    protected Dimension getThumbSize() {
        return new Dimension(20,20);
    }


    private Shape createThumbShape(int width, int height) {
        return new Ellipse2D.Double(0, 0, width, height);
    }

    @Override
    public void paintTrack(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        Stroke old = g2d.getStroke();
        g2d.setStroke(stroke);
        g2d.setPaint(ThemeableColor.getColorByName("foreground-darker"));
        Color oldColor = ThemeableColor.getColorByName("foreground-darker");
        Rectangle trackBounds = trackRect;
        if (slider.getOrientation() == SwingConstants.HORIZONTAL) {
            g2d.drawLine(trackRect.x, trackRect.y + trackRect.height / 2,
                    trackRect.x + trackRect.width, trackRect.y + trackRect.height / 2);
            int lowerX = thumbRect.width / 2;
            int upperX = thumbRect.x + (thumbRect.width / 2);
            int cy = (trackBounds.height / 2) - 2;
            g2d.translate(trackBounds.x, trackBounds.y + cy);
            g2d.setColor(rangeColor);
            g2d.drawLine(lowerX - trackBounds.x, 2, upperX - trackBounds.x, 2);
            g2d.translate(-trackBounds.x, -(trackBounds.y + cy));
            g2d.setColor(oldColor);
        }
        g2d.setStroke(old);
    }

    @Override
    public void paintThumb(Graphics g) {
        Rectangle knobBounds = thumbRect;
        int w = knobBounds.width;
        int h = knobBounds.height;
        Graphics2D g2d = (Graphics2D) g.create();
        Shape thumbShape = createThumbShape(w - 1, h - 1);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.translate(knobBounds.x, knobBounds.y);
        g2d.setColor(ThemeableColor.getColorByName("accent"));
        g2d.fill(thumbShape);

        g2d.setColor(ThemeableColor.getColorByName("foreground-darker"));
        g2d.draw(thumbShape);
        g2d.dispose();
    }

    public class RangeTrackListener extends TrackListener {

        @Override
        public void mouseClicked(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }
            currentMouseX -= thumbRect.width / 2;
            moveUpperThumb();
        }

        public void mousePressed(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (slider.isRequestFocusEnabled()) {
                slider.requestFocus();
            }

            boolean upperPressed = thumbRect.contains(currentMouseX, currentMouseY);

            if (upperPressed) {
                switch (slider.getOrientation()) {
                    case JSlider.VERTICAL:
                        offset = currentMouseY - thumbRect.y;
                        break;
                    case JSlider.HORIZONTAL:
                        offset = currentMouseX - thumbRect.x;
                        break;
                }
                upperDragging = true;
                return;
            }


            upperDragging = false;
        }

        @Override
        public void mouseReleased(MouseEvent e) {

            upperDragging = false;
            slider.setValueIsAdjusting(false);
            super.mouseReleased(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!slider.isEnabled()) {
                return;
            }

            currentMouseX = e.getX();
            currentMouseY = e.getY();

            if (upperDragging) {
                slider.setValueIsAdjusting(true);
                moveUpperThumb();

            }
        }

        @Override
        public boolean shouldScroll(int direction) {
            return false;
        }

        public void moveUpperThumb() {
            int thumbMiddle;
            if (slider.getOrientation() == JSlider.HORIZONTAL) {
                int halfThumbWidth = thumbRect.width / 2;
                int thumbLeft = currentMouseX - offset;
                int trackLeft = trackRect.x;
                int trackRight = trackRect.x + (trackRect.width - 1);
                int hMax = xPositionForValue(slider.getMaximum() -
                        slider.getExtent());

                if (drawInverted()) {
                    trackLeft = hMax;
                } else {
                    trackRight = hMax;
                }
                thumbLeft = Math.max(thumbLeft, trackLeft - halfThumbWidth);
                thumbLeft = Math.min(thumbLeft, trackRight - halfThumbWidth);

                setThumbLocation(thumbLeft, thumbRect.y);//setThumbLocation

                thumbMiddle = thumbLeft + halfThumbWidth;
                slider.setValue(valueForXPosition(thumbMiddle));
            }
        }
    }
}