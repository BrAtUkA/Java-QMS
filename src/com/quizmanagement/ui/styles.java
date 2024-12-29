package com.quizmanagement.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class styles {
    // ============== COLORS ==============
    public static final Color ERROR_COLOR = new Color(209, 38, 38);
    public static final Color WARNING_COLOR = new Color(245, 150, 27);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    
    // Main backgrounds - matching #1e1e1e theme
    public static final Color DARK_BG = new Color(30, 30, 30);       // #1e1e1e
    public static final Color DARKER_BG = new Color(24, 24, 24);     // slightly darker
    public static final Color CARD_BG = new Color(40, 40, 40);       // cards/panels
    public static final Color HIGHLIGHT = new Color(59, 130, 246);
    public static final Color TEXT_COLOR = new Color(230, 230, 230);
    public static final Color SECONDARY_TEXT = new Color(160, 160, 160);
    public static final Color MUTED_TEXT = SECONDARY_TEXT; // alias
    public static final Color BORDER_COLOR = new Color(50, 50, 50); 
    public static final Color HOVER_COLOR = new Color(50, 50, 50);
    
    public static final Color DANGER_COLOR = new Color(204, 34, 34);  // match auth button
    public static final Color DANGER_HOVER = new Color(180, 30, 30);
    
    // ============== TYPOGRAPHY ==============
    public static final String FONT_FAMILY = "Segoe UI";
    public static final int FONT_H1 = 24;
    public static final int FONT_H2 = 18;
    public static final int FONT_H3 = 16;
    public static final int FONT_BODY = 14;
    public static final int FONT_SMALL = 12;
    
    public static Font fontH1() { return new Font(FONT_FAMILY, Font.BOLD, FONT_H1); }
    public static Font fontH2() { return new Font(FONT_FAMILY, Font.BOLD, FONT_H2); }
    public static Font fontH3() { return new Font(FONT_FAMILY, Font.PLAIN, FONT_H3); }
    public static Font fontBody() { return new Font(FONT_FAMILY, Font.PLAIN, FONT_BODY); }
    public static Font fontSmall() { return new Font(FONT_FAMILY, Font.PLAIN, FONT_SMALL); }
    
    // ============== SPACING ==============
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 12;
    public static final int SPACING_LG = 16;
    public static final int SPACING_XL = 24;
    
    // ============== BUTTONS ==============
    public static class Buttons {
        
        public static JButton create(String text, Color bgColor, Color hoverColor) {
            JButton button = new JButton(text);
            button.setFont(fontBody());
            button.setForeground(TEXT_COLOR);
            button.setBackground(bgColor);
            button.setBorder(BorderFactory.createEmptyBorder(SPACING_SM, SPACING_LG, SPACING_SM, SPACING_LG));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (button.isEnabled()) {
                        button.setBackground(hoverColor);
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(bgColor);
                }
            });
            
            return button;
        }
        
        public static JButton createPrimary(String text) {
            return create(text, HIGHLIGHT, new Color(37, 99, 235));
        }
        
        public static JButton createSecondary(String text) {
            JButton button = create(text, CARD_BG, HOVER_COLOR);
            button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(SPACING_SM - 1, SPACING_LG - 1, SPACING_SM - 1, SPACING_LG - 1)
            ));
            return button;
        }
        
        public static JButton createSuccess(String text) {
            return create(text, SUCCESS_COLOR, new Color(34, 180, 95));
        }
        
        public static JButton createDanger(String text) {
            return create(text, DANGER_COLOR, DANGER_HOVER);
        }
        
        // Compact table CTA button with arrow icon - fits in table rows
        public static JButton createTableCTA(String text) {
            JButton button = new JButton(text + "  >");
            button.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
            button.setForeground(TEXT_COLOR);
            button.setBackground(HIGHLIGHT);
            button.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (button.isEnabled()) {
                        button.setBackground(new Color(37, 99, 235));
                    }
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(HIGHLIGHT);
                }
            });
            
            return button;
        }
        
        // Subtle text-style button for logout etc
        public static JButton createGhost(String text) {
            JButton button = new JButton(text);
            button.setFont(fontSmall());
            button.setForeground(SECONDARY_TEXT);
            button.setBackground(DARKER_BG);
            button.setBorder(BorderFactory.createEmptyBorder(SPACING_XS, SPACING_SM, SPACING_XS, SPACING_SM));
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setForeground(TEXT_COLOR);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setForeground(SECONDARY_TEXT);
                }
            });
            
            return button;
        }
        
        public static JButton createIcon(String symbol, Color bgColor, Color hoverColor, int size) {
            JButton button = new JButton(symbol);
            button.setFont(new Font(FONT_FAMILY, Font.PLAIN, size - 4));
            button.setForeground(TEXT_COLOR);
            button.setBackground(bgColor);
            button.setPreferredSize(new Dimension(size, size));
            button.setMinimumSize(new Dimension(size, size));
            button.setMaximumSize(new Dimension(size, size));
            button.setBorder(null);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setOpaque(true);
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(hoverColor);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(bgColor);
                }
            });
            
            return button;
        }
        
        // Window control button (rounded rectangle style, matching auth pages: 22-23 x 19)
        public static JButton createWindowControl(Color bgColor, Color hoverColor, int size) {
            // Auth pages use: minButton 23x19, exitButton 22x19
            final int width = 22;
            final int height = 19;
            
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                }
            };
            button.setBackground(bgColor);
            button.setPreferredSize(new Dimension(width, height));
            button.setMinimumSize(new Dimension(width, height));
            button.setMaximumSize(new Dimension(width, height));
            button.setBorder(null);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(hoverColor);
                    button.repaint();
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(bgColor);
                    button.repaint();
                }
            });
            
            return button;
        }
        
        // Simple refresh icon button using Unicode character
        public static JButton createRefreshIcon(int size) {
            JButton button = new JButton("↻");  // Unicode refresh arrow
            button.setFont(new Font("Segoe UI Symbol", Font.PLAIN, size - 8));
            button.setForeground(SECONDARY_TEXT);
            button.setBackground(DARK_BG);
            button.setPreferredSize(new Dimension(size, size));
            button.setMinimumSize(new Dimension(size, size));
            button.setMaximumSize(new Dimension(size, size));
            button.setBorder(null);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setToolTipText("Refresh");
            
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setForeground(TEXT_COLOR);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setForeground(SECONDARY_TEXT);
                }
            });
            
            return button;
        }
    }
    
    // ============== USER BADGE ==============
    public static class UserBadge {
        public static JPanel create(String username, String role, Color accentColor) {
            JPanel badge = new JPanel(new FlowLayout(FlowLayout.LEFT, SPACING_SM, 0));
            badge.setBackground(DARKER_BG);
            badge.setOpaque(false);
            
            // Circular avatar with initials
            String initials = username.length() >= 2 ? username.substring(0, 2).toUpperCase() : username.toUpperCase();
            JLabel avatar = new JLabel(initials) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(accentColor);
                    g2.fillOval(0, 0, getWidth(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            avatar.setFont(new Font(FONT_FAMILY, Font.BOLD, FONT_SMALL));
            avatar.setForeground(TEXT_COLOR);
            avatar.setPreferredSize(new Dimension(32, 32));
            avatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            avatar.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
            badge.add(avatar);
            
            // Username
            JLabel nameLabel = new JLabel(username);
            nameLabel.setFont(fontBody());
            nameLabel.setForeground(TEXT_COLOR);
            badge.add(nameLabel);
            
            // Role pill
            JLabel rolePill = new JLabel(role) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            rolePill.setFont(fontSmall());
            rolePill.setForeground(accentColor);
            rolePill.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            badge.add(rolePill);
            
            return badge;
        }
    }
    
    // ============== STAT CARD ==============
    public static class StatCard {
        public static JPanel create(String title, String value, Color accentColor) {
            JPanel panel = new JPanel(new BorderLayout(0, SPACING_XS));
            panel.setBackground(CARD_BG);
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accentColor),
                BorderFactory.createEmptyBorder(SPACING_LG, SPACING_LG, SPACING_LG, SPACING_LG)
            ));
            
            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(fontSmall());
            titleLabel.setForeground(SECONDARY_TEXT);
            panel.add(titleLabel, BorderLayout.NORTH);
            
            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 28));
            valueLabel.setForeground(TEXT_COLOR);
            panel.add(valueLabel, BorderLayout.CENTER);
            
            return panel;
        }
    }
    
    // ============== EMPTY STATE ==============
    public static class EmptyState {
        public static JPanel create(String message, String subMessage) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(DARK_BG);
            panel.setBorder(BorderFactory.createEmptyBorder(60, 40, 60, 40));
            
            JPanel content = new JPanel();
            content.setLayout(new javax.swing.BoxLayout(content, javax.swing.BoxLayout.Y_AXIS));
            content.setBackground(DARK_BG);
            
            JLabel iconLabel = new JLabel("[ ]");
            iconLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 48));
            iconLabel.setForeground(SECONDARY_TEXT);
            iconLabel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
            content.add(iconLabel);
            
            content.add(javax.swing.Box.createVerticalStrut(SPACING_LG));
            
            JLabel msgLabel = new JLabel(message);
            msgLabel.setFont(fontH2());
            msgLabel.setForeground(TEXT_COLOR);
            msgLabel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
            content.add(msgLabel);
            
            if (subMessage != null && !subMessage.isEmpty()) {
                content.add(javax.swing.Box.createVerticalStrut(SPACING_SM));
                JLabel subLabel = new JLabel(subMessage);
                subLabel.setFont(fontBody());
                subLabel.setForeground(SECONDARY_TEXT);
                subLabel.setAlignmentX(JPanel.CENTER_ALIGNMENT);
                content.add(subLabel);
            }
            
            panel.add(content, BorderLayout.CENTER);
            return panel;
        }
    }
    
    // ============== PROGRESS BAR ==============
    public static class ProgressBar {
        public static JPanel create(int current, int total) {
            JPanel container = new JPanel(new BorderLayout());
            container.setBackground(DARK_BG);
            container.setOpaque(false);
            
            ProgressBarPanel bar = new ProgressBarPanel();
            bar.setProgress(current, total);
            container.add(bar, BorderLayout.CENTER);
            
            return container;
        }
        
        public static void update(JPanel container, int current, int total) {
            if (container.getComponentCount() > 0 && container.getComponent(0) instanceof ProgressBarPanel) {
                ((ProgressBarPanel) container.getComponent(0)).setProgress(current, total);
            }
        }
    }
    
    private static class ProgressBarPanel extends JPanel {
        private int current = 0;
        private int total = 1;
        private Color barColor = HIGHLIGHT;
        
        public ProgressBarPanel() {
            setPreferredSize(new Dimension(100, 6));
            setBackground(BORDER_COLOR);
        }
        
        public void setProgress(int current, int total) {
            this.current = current;
            this.total = Math.max(total, 1);
            repaint();
        }
        
        public void setBarColor(Color color) {
            this.barColor = color;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int width = getWidth();
            int height = getHeight();
            
            // Background
            g2.setColor(BORDER_COLOR);
            g2.fillRoundRect(0, 0, width, height, height, height);
            
            // Progress
            if (current > 0) {
                int progressWidth = (int) ((double) current / total * width);
                g2.setColor(barColor);
                g2.fillRoundRect(0, 0, progressWidth, height, height, height);
            }
            
            g2.dispose();
        }
    }
    
    // ============== TAB STYLING ==============
    public static void styleTabPane(JTabbedPane tabbedPane) {
        tabbedPane.setBackground(DARKER_BG);
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(fontBody());
        tabbedPane.setBorder(null);
        
        tabbedPane.setUI(new BasicTabbedPaneUI() {
            @Override
            protected void installDefaults() {
                super.installDefaults();
                lightHighlight = DARKER_BG;
                shadow = DARKER_BG;
                darkShadow = DARKER_BG;
                focus = HIGHLIGHT;
            }
            
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isSelected) {
                    g2.setColor(DARK_BG);
                } else {
                    g2.setColor(DARKER_BG);
                }
                g2.fillRect(x, y, w, h);
                
                if (isSelected) {
                    g2.setColor(HIGHLIGHT);
                    g2.fillRect(x, y + h - 3, w, 3);
                }
                
                g2.dispose();
            }
            
            @Override
            protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                    int x, int y, int w, int h, boolean isSelected) {
                // No border
            }
            
            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // Minimal content border
                g.setColor(BORDER_COLOR);
                g.drawLine(0, 0, tabPane.getWidth(), 0);
            }
            
            @Override
            protected void paintFocusIndicator(Graphics g, int tabPlacement, java.awt.Rectangle[] rects,
                    int tabIndex, java.awt.Rectangle iconRect, java.awt.Rectangle textRect, boolean isSelected) {
                // No focus indicator
            }
        });
    }
   
    // ============== BORDERS ==============
    public class Borders {
        public static final Color DEFAULT_BORDER_COLOR = new Color(60, 60, 60);

        private static final int PADDING_TOP = 2;
        private static final int PADDING_LEFT = 6; 
        private static final int PADDING_BOTTOM = 2;
        private static final int PADDING_RIGHT = 2;


        public static void setDefaultBorder(JComponent comp) {
            if (comp != null) {
                comp.setBorder(createCompoundBorder(DEFAULT_BORDER_COLOR));
            }
        }

        public static void setErrorBorder(JComponent comp) {
            if (comp != null) {
                comp.setBorder(createCompoundBorder(ERROR_COLOR));
            }
        }

        public static void setWarningBorder(JComponent comp) {
            if (comp != null) {
                comp.setBorder(createCompoundBorder(WARNING_COLOR));
            }
        }
        
        public static void setSuccessBorder(JComponent comp) {
            if (comp != null) {
                comp.setBorder(createCompoundBorder(SUCCESS_COLOR));
            }
        }

        private static CompoundBorder createCompoundBorder(Color borderColor) {
            MatteBorder matteBorder = new MatteBorder(1, 1, 1, 1, borderColor);
            EmptyBorder paddingBorder = new EmptyBorder(PADDING_TOP, PADDING_LEFT, PADDING_BOTTOM, PADDING_RIGHT);
            return new CompoundBorder(matteBorder, paddingBorder);
        }
    }

    // ============== HOVER ==============
    public static class Hover {
        public static void addHoverEffect(JLabel component, String defaultText, String hoverText) {
            component.setText(defaultText);
            component.setCursor(new Cursor(Cursor.HAND_CURSOR));

            component.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    component.setText(hoverText);
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    component.setText(defaultText);
                }
            });
        }
    }
    
    // ============== DRAG ==============
    public static class Drag {
        public static void addDragFunctionality(JFrame frame) {
            final int[] mouseX = {0};
            final int[] mouseY = {0};

            frame.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    mouseX[0] = evt.getX();
                    mouseY[0] = evt.getY();
                }
            });

            frame.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                @Override
                public void mouseDragged(java.awt.event.MouseEvent evt) {
                    int x = evt.getXOnScreen();
                    int y = evt.getYOnScreen();
                    frame.setLocation(x - mouseX[0], y - mouseY[0]);
                }
            });
        }
    }
}


