
package visualization;

import javax.swing.*;
import java.awt.*;

public class SignalPlotter {

    public static void plotDigital(String title, int[] signal) {
        if (signal == null || signal.length == 0) {
            throw new IllegalArgumentException(
                    "Digital signal cannot be empty.");
        }

        double[] values = new double[signal.length];

        for (int i = 0; i < signal.length; i++) {
            values[i] = signal[i];
        }

        showPlot(title, values, true, "Sample index", "Signal level");
    }

    public static void plotPCM(String title, double[] samples) {
        if (samples == null || samples.length == 0) {
            throw new IllegalArgumentException(
                    "PCM samples cannot be empty.");
        }

        for (double sample : samples) {
            if (!Double.isFinite(sample)) {
                throw new IllegalArgumentException(
                        "Samples must be finite.");
            }
        }

        showPlot(title, samples, false,
                "Sample index", "Amplitude");
    }

    private static void showPlot(
            String title,
            double[] values,
            boolean digital,
            String xLabel,
            String yLabel) {

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame(title);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.add(new PlotPanel(
                    title, values, digital, xLabel, yLabel));
            frame.setSize(920, 520);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private static class PlotPanel extends JPanel {

        private final String plotTitle;
        private final double[] values;
        private final boolean digital;
        private final String xLabel;
        private final String yLabel;

        PlotPanel(String plotTitle, double[] values, boolean digital,
                String xLabel, String yLabel) {
            this.plotTitle = plotTitle;
            this.values = values;
            this.digital = digital;
            this.xLabel = xLabel;
            this.yLabel = yLabel;
            setBackground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);

            Graphics2D g = (Graphics2D) graphics.create();

            try {
                g.setRenderingHint(
                        RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);

                int left = 75;
                int right = getWidth() - 35;
                int top = 40;
                int bottom = getHeight() - 70;

                if (right <= left || bottom <= top) {
                    return;
                }

                double min = values[0];
                double max = values[0];

                for (double value : values) {
                    min = Math.min(min, value);
                    max = Math.max(max, value);
                }

                if (digital) {
                    min = Math.min(min, -1);
                    max = Math.max(max, 1);
                }

                if (min == max) {
                    min -= 1;
                    max += 1;
                }

                double padding = (max - min) * 0.15;
                min -= padding;
                max += padding;

                final double yMin = min;
                final double yMax = max;

                // Title
                g.setColor(new Color(20, 30, 50));
                g.setFont(new Font("SansSerif", Font.BOLD, 16));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(plotTitle,
                        (getWidth() - fm.stringWidth(plotTitle)) / 2,
                        25);

                // Horizontal grid lines and Y-axis labels
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));

                for (int i = 0; i <= 4; i++) {
                    double value = yMax
                            - i * (yMax - yMin) / 4.0;

                    int y = mapY(value, yMin, yMax, top, bottom);

                    g.setColor(new Color(230, 230, 235));
                    g.drawLine(left, y, right, y);

                    g.setColor(Color.DARK_GRAY);
                    g.drawString(
                            String.format("%.2f", value),
                            15, y + 4);
                }

                // Reference line at y = 0
                if (yMin <= 0 && yMax >= 0) {
                    int zeroY = mapY(0.0, yMin, yMax, top, bottom);
                    g.setColor(new Color(180, 180, 185));
                    Stroke prevStroke = g.getStroke();
                    float[] dash = { 4.0f, 4.0f };
                    g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
                    g.drawLine(left, zeroY, right, zeroY);
                    g.setStroke(prevStroke);
                }

                // Axes
                g.setColor(new Color(60, 60, 60));
                g.setStroke(new BasicStroke(1.5f));
                g.drawLine(left, top, left, bottom);
                g.drawLine(left, bottom, right, bottom);

                // X-axis sample index ticks (if count <= 40)
                double width = right - left;
                double step = width / values.length;

                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.setColor(Color.GRAY);
                if (values.length <= 40) {
                    for (int i = 0; i < values.length; i++) {
                        int xCenter = (int) Math.round(left + (i + 0.5) * step);
                        int xStart = (int) Math.round(left + i * step);
                        g.drawLine(xStart, bottom, xStart, bottom + 4);
                        String tickLabel = String.valueOf(i);
                        FontMetrics tfm = g.getFontMetrics();
                        g.drawString(tickLabel, xCenter - tfm.stringWidth(tickLabel) / 2, bottom + 16);
                    }
                }

                // Signal waveform
                g.setColor(new Color(25, 118, 210));
                g.setStroke(new BasicStroke(2.2f));

                for (int i = 0; i < values.length; i++) {
                    int x1 = (int) Math.round(left + i * step);
                    int x2 = (int) Math.round(left + (i + 1) * step);

                    int y1 = mapY(
                            values[i], yMin, yMax, top, bottom);

                    // Horizontal segment for this sample
                    g.drawLine(x1, y1, x2, y1);

                    // Vertical transition to the next sample
                    if (i < values.length - 1) {
                        int y2 = mapY(
                                values[i + 1], yMin, yMax, top, bottom);
                        g.drawLine(x2, y1, x2, y2);
                    }
                }

                // Axis labels (properly centered)
                g.setColor(new Color(30, 30, 30));
                g.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics fmAxis = g.getFontMetrics();
                int xLabelX = (left + right - fmAxis.stringWidth(xLabel)) / 2;
                g.drawString(xLabel, xLabelX, getHeight() - 20);

                g.rotate(-Math.PI / 2);
                int yLabelX = -(top + bottom + fmAxis.stringWidth(yLabel)) / 2;
                g.drawString(yLabel, yLabelX, 22);
                g.rotate(Math.PI / 2);

            } finally {
                g.dispose();
            }
        }

        private int mapY(double value, double min, double max,
                int top, int bottom) {
            return (int) Math.round(
                    bottom - (value - min) / (max - min)
                            * (bottom - top));
        }
    }
}
