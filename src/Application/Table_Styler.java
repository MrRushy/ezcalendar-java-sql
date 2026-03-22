package Application;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;

public class Table_Styler {
	
	public enum CalendarTheme {
	    BLUE_GREY,
	    RED_WHITE,
	    EARTH_CREAM,
	    DEFAULT
	}
	
	public static void styleCalendarTable(JTable table, JScrollPane scrollPane, CalendarTheme theme) {
		// General styling
		table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.setDefaultEditor(Object.class, null); // Make cells non-editable
        table.setFocusable(false);
        table.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
	    // Header styling
	    JTableHeader header = table.getTableHeader();
	    header.setFont(new Font("SansSerif", Font.BOLD, 15));
	    // header.setBackground(new Color(220, 220, 220));
	    header.setBorder(BorderFactory.createEtchedBorder());

	    // Scroll pane background
	    scrollPane.getViewport().setBackground(Color.WHITE);
	    scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
	    
	 // Theme-specific colors
        Color evenRow, oddRow, headerBg, textColor; 
        Color selectionColor = null;
        
        switch (theme) {
            case RED_WHITE:
                evenRow = new Color(255, 230, 230);  // Light red
                oddRow = Color.WHITE;
                headerBg = new Color(200, 0, 0);     // Red
                textColor = Color.BLACK;
                selectionColor = Color.decode("#f98b8c"); // soft red highlight
                table.setForeground(Color.BLACK);
                break;
            case BLUE_GREY:
            default:
                evenRow = new Color(230, 240, 255);  // Light blue
                oddRow = Color.WHITE;
                headerBg = new Color(70, 130, 180);  // Steel blue
                textColor = Color.BLACK;
                selectionColor = new Color(180, 200, 255); // light blue highlight
                table.setForeground(Color.BLACK);
                break;
            case EARTH_CREAM:
                evenRow = Color.decode("#FEF3C7");
                oddRow = Color.WHITE;
                headerBg = Color.decode("#A16207");
                textColor = Color.BLACK;
                selectionColor = Color.decode("#c79e63");
                table.setForeground(Color.BLACK);
                break;
            case DEFAULT:
            	evenRow = Color.WHITE;
            	oddRow = Color.WHITE;
            	headerBg = Color.GRAY;
            	textColor = Color.BLACK;
            	selectionColor = Color.decode("#bfc0bd");
            	table.setForeground(Color.BLACK);
            	break;
        }
	    
	    // Combined striping and alignment renderer
        DefaultTableCellRenderer stripedRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                            boolean isSelected, boolean hasFocus,
                                                            int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Apply alternating background only if not selected
                if (!isSelected) {
                    comp.setBackground(row % 2 == 0 ? evenRow : oddRow);
                } else {
                    comp.setBackground(table.getSelectionBackground());
                }

                // Center align specific columns (based on header name)
                String columnName = table.getColumnName(column).toLowerCase();
                if (columnName.contains("date") || columnName.contains("time")) {
                    setHorizontalAlignment(CENTER);
                } else {
                    setHorizontalAlignment(LEFT);
                }

                return comp;
            }
        };

        // Apply renderer to all columns
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(stripedRenderer);
        }
        
        header.setBackground(headerBg);
        header.setOpaque(true); // Ensures custom color is shown
        header.setForeground(textColor);
        scrollPane.setBorder(BorderFactory.createLineBorder(headerBg, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        table.setSelectionBackground(selectionColor);
	}
}
