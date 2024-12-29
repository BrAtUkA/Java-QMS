package com.quizmanagement.ui.shared;

import static com.quizmanagement.ui.styles.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;

public class TablePanel extends JPanel {
    
    public static class ColumnConfig {
        private final String header;
        private final double width;
        private final int alignment;
        
        public ColumnConfig(String header, double width) {
            this(header, width, FlowLayout.LEFT);
        }
        
        public ColumnConfig(String header, double width, int alignment) {
            this.header = header;
            this.width = width;
            this.alignment = alignment;
        }
    }
    
    public static class FixedColumnPanel extends JPanel {
        private final int preferredWidth;
        
        public FixedColumnPanel(double widthPercentage, int alignment) {
            super(new FlowLayout(alignment, 0, 0));
            this.preferredWidth = (int)(1000 * widthPercentage);
        }
        
        @Override
        public Dimension getPreferredSize() {
            Dimension d = super.getPreferredSize();
            d.width = preferredWidth;
            return d;
        }
        
        @Override
        public Dimension getMaximumSize() {
            Dimension d = super.getMaximumSize();
            d.width = preferredWidth;
            return d;
        }
    }
    
    private final JPanel contentPanel;
    private final ColumnConfig[] columns;
    
    public TablePanel(ColumnConfig[] columns) {
        this.columns = columns;
        
        setLayout(new BorderLayout());
        setBackground(DARK_BG);
        
        // Header stays fixed at top, outside scrollpane
        add(createHeaderRow(), BorderLayout.NORTH);
        
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(DARK_BG);
        
        JScrollPane scrollPane = createScrollPane();
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private JScrollPane createScrollPane() {
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(DARK_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(8);
        
        customizeScrollBar(scrollPane.getVerticalScrollBar());
        customizeScrollBar(scrollPane.getHorizontalScrollBar());
        
        return scrollPane;
    }
    
    private JPanel createHeaderRow() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.X_AXIS));
        headerPanel.setBackground(DARKER_BG);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 1, 1, 1, BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        for (ColumnConfig column : columns) {
            FixedColumnPanel columnPanel = new FixedColumnPanel(column.width, column.alignment);
            columnPanel.setBackground(DARKER_BG);
            
            JLabel label = new JLabel(column.header);
            label.setForeground(TEXT_COLOR);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            columnPanel.add(label);
            
            headerPanel.add(columnPanel);
        }
        
        return headerPanel;
    }
    
    private static final int ROW_HEIGHT = 48;
    
    public JPanel createRow() {
        JPanel rowPanel = new JPanel();
        rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
        rowPanel.setBackground(DARK_BG);
        rowPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        
        // Fix row stretching - constrain to fixed height
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, ROW_HEIGHT));
        rowPanel.setPreferredSize(new Dimension(rowPanel.getPreferredSize().width, ROW_HEIGHT));
        
        addHoverEffect(rowPanel);
        
        for (ColumnConfig column : columns) {
            FixedColumnPanel columnPanel = new FixedColumnPanel(column.width, column.alignment);
            columnPanel.setBackground(DARK_BG);
            rowPanel.add(columnPanel);
        }
        
        // Remove any existing glue before adding row
        removeVerticalGlue();
        contentPanel.add(rowPanel);
        // Add glue after rows to push them to top
        contentPanel.add(Box.createVerticalGlue());
        
        return rowPanel;
    }
    
    private void removeVerticalGlue() {
        Component[] components = contentPanel.getComponents();
        for (int i = components.length - 1; i >= 0; i--) {
            if (components[i] instanceof Box.Filler) {
                contentPanel.remove(i);
                break;
            }
        }
    }
    
    public void clearRows() {
        contentPanel.removeAll();
        contentPanel.add(Box.createVerticalGlue());
        
        revalidate();
        repaint();
    }
    
    private void addHoverEffect(JPanel panel) {
        panel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                updatePanelBackground(panel, HOVER_COLOR);
            }
            public void mouseExited(MouseEvent e) {
                updatePanelBackground(panel, DARK_BG);
            }
        });
    }
    
    private void updatePanelBackground(JPanel panel, Color color) {
        panel.setBackground(color);
        for (Component c : panel.getComponents()) {
            c.setBackground(color);
        }
    }
    
    private void customizeScrollBar(JScrollBar scrollBar) {
        scrollBar.setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = HIGHLIGHT;
                this.trackColor = DARKER_BG;
            }
            
            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }
            
            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }
            
            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                return button;
            }
        });
    }
    
    public static JLabel createStyledLabel(String text, Font font, boolean isTitle) {
        JLabel label = new JLabel(text);
        label.setFont(isTitle ? font.deriveFont(Font.BOLD) : font);
        label.setForeground(isTitle ? TEXT_COLOR : SECONDARY_TEXT);
        return label;
    }
    
    public static JButton createStyledButton(String text, Color bgColor, boolean isEnabled) {
        JButton button = new JButton(text);
        styleButton(button, bgColor);
        button.setEnabled(isEnabled);
        
        if (isEnabled) {
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(bgColor.darker());
                }
                public void mouseExited(MouseEvent e) {
                    button.setBackground(bgColor);
                }
            });
        }
        
        return button;
    }
    
    private static void styleButton(JButton button, Color bgColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setForeground(TEXT_COLOR);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}