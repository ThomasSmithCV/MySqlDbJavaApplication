import java.awt.FlowLayout;

import javax.swing.JFrame;

import org.jfree.chart.ChartPanel;

// Thomas Smith
public class DataDisplay extends JFrame{
    
    public DataDisplay(ChartPanel chart) {
        setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
        add(chart);
        setLocationRelativeTo(null);
        setSize(675, 450);
        setVisible(true);
    }
}
