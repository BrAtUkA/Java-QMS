package com.quizmanagement.ui.shared;

import static com.quizmanagement.ui.styles.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class MCQOptionPanel extends JPanel {
    private boolean selected = false;
    private boolean editable = true;
    private final JTextArea textArea;
    private final String label;
    private final JPanel indicatorWrapper;
    private final JLabel indicator;
    private final List<MCQOptionPanel> siblingPanels = new ArrayList<>();
    private Runnable onSelectionChange;
    
    private static final Color UNSELECTED_COLOR = new Color(60, 60, 65);
    private static final Color SELECTED_COLOR = HIGHLIGHT;
    
    public MCQOptionPanel(String label) {
        this(label, true);
    }
    
    public MCQOptionPanel(String label, boolean editable) {
        this.label = label;
        this.editable = editable;
        
        setLayout(new BorderLayout(SPACING_SM, 0));
        setBackground(DARKER_BG);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER_COLOR, 1),
            new EmptyBorder(SPACING_SM, SPACING_SM, SPACING_SM, SPACING_SM)
        ));
        
        // Left: clickable indicator
        indicatorWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        indicatorWrapper.setBackground(DARKER_BG);
        indicatorWrapper.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        indicator = new JLabel(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(selected ? SELECTED_COLOR : UNSELECTED_COLOR);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Color.WHITE);
                g2.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        indicator.setPreferredSize(new Dimension(28, 28));
        indicator.setOpaque(false);
        
        indicatorWrapper.add(indicator);
        
        // Click on indicator to select this option
        indicatorWrapper.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectThis();
            }
        });
        
        // Center: text area
        textArea = new JTextArea(2, 20);
        textArea.setBackground(DARKER_BG);
        textArea.setForeground(TEXT_COLOR);
        textArea.setCaretColor(TEXT_COLOR);
        textArea.setFont(fontBody());
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(new EmptyBorder(SPACING_XS, SPACING_SM, SPACING_XS, SPACING_XS));
        textArea.setEditable(editable);
        
        // If not editable, clicking the text area also selects the option
        if (!editable) {
            textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
            textArea.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectThis();
                }
            });
        }
        
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setBorder(null);
        scroll.setBackground(DARKER_BG);
        scroll.getViewport().setBackground(DARKER_BG);
        
        add(indicatorWrapper, BorderLayout.WEST);
        add(scroll, BorderLayout.CENTER);
    }
    
    /**
     * Register sibling panels for mutual exclusion when selecting.
     */
    public void setSiblings(MCQOptionPanel... siblings) {
        siblingPanels.clear();
        for (MCQOptionPanel sibling : siblings) {
            if (sibling != this) {
                siblingPanels.add(sibling);
            }
        }
    }
    
    /**
     * Set a callback to be notified when selection changes.
     */
    public void setOnSelectionChange(Runnable callback) {
        this.onSelectionChange = callback;
    }
    
    private void selectThis() {
        // Deselect all siblings
        for (MCQOptionPanel sibling : siblingPanels) {
            sibling.setSelected(false);
        }
        setSelected(true);
        
        if (onSelectionChange != null) {
            onSelectionChange.run();
        }
    }
    
    public String getText() {
        return textArea.getText();
    }
    
    public void setText(String text) {
        textArea.setText(text);
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public void setSelected(boolean selected) {
        this.selected = selected;
        repaint();
    }
    
    public void setEditable(boolean editable) {
        this.editable = editable;
        textArea.setEditable(editable);
        
        if (!editable) {
            textArea.setCursor(new Cursor(Cursor.HAND_CURSOR));
        } else {
            textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));
        }
    }
    
    public boolean isEditable() {
        return editable;
    }
    
    public void focus() {
        textArea.requestFocus();
    }
    
    public String getLabel() {
        return label;
    }
}
