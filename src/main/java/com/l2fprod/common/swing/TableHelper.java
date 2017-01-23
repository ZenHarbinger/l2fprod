/* * Copyright 2015 Matthew Aguirre * * Licensed under the Apache License, Version 2.0 (the "License"); * you may not use this file except in compliance with the License. * You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */package com.l2fprod.common.swing;import java.awt.Component;import java.awt.Dimension;import java.awt.Point;import java.beans.PropertyChangeEvent;import java.beans.PropertyChangeListener;import javax.swing.JScrollPane;import javax.swing.JTable;import javax.swing.SwingUtilities;import javax.swing.event.TableColumnModelListener;import javax.swing.event.TableModelListener;import javax.swing.table.JTableHeader;import javax.swing.table.TableColumn;import javax.swing.table.TableColumnModel;import javax.swing.table.TableModel;public final class TableHelper {    private TableHelper() {    }    public static PropertyChangeListener addModelTracker(JTable table,            final TableModelListener listener) {        PropertyChangeListener propListener = new PropertyChangeListener() {            @Override            public void propertyChange(PropertyChangeEvent event) {                TableModel oldModel = (TableModel) event.getOldValue();                TableModel newModel = (TableModel) event.getNewValue();                if (oldModel != null) {                    oldModel.removeTableModelListener(listener);                }                if (newModel != null) {                    newModel.addTableModelListener(listener);                }            }        };        table.addPropertyChangeListener("model", propListener);        table.getModel().addTableModelListener(listener);        return propListener;    }    public static PropertyChangeListener addColumnModelTracker(JTable table,            final TableColumnModelListener listener) {        PropertyChangeListener propListener = new PropertyChangeListener() {            @Override            public void propertyChange(PropertyChangeEvent event) {                TableColumnModel oldModel = (TableColumnModel) event.getOldValue();                TableColumnModel newModel = (TableColumnModel) event.getNewValue();                if (oldModel != null) {                    oldModel.removeColumnModelListener(listener);                }                if (newModel != null) {                    newModel.addColumnModelListener(listener);                }            }        };        table.addPropertyChangeListener("columnModel", propListener);        table.getColumnModel().addColumnModelListener(listener);        return propListener;    }    public static void layoutHeaders(JTable table) {        int column = 0;        for (java.util.Enumeration columns = table.getTableHeader()                .getColumnModel().getColumns(); columns.hasMoreElements(); column++) {            TableColumn c = (TableColumn) columns.nextElement();            Component component = c.getHeaderRenderer()                    .getTableCellRendererComponent(table, c.getHeaderValue(), false,                            false, -1, column);            c.setPreferredWidth(Math.max(c.getPreferredWidth(), component                    .getPreferredSize().width));        }    }    public static void layoutColumns(JTable table, boolean onlyVisibleRows) {        int column = 0;        TableColumn c;        int firstRow = onlyVisibleRows ? getFirstVisibleRow(table) : 0;        int lastRow = onlyVisibleRows ? getLastVisibleRow(table) : (table                .getModel().getRowCount() - 1);        Dimension intercellSpacing = table.getIntercellSpacing();        JTableHeader tableHeader = table.getTableHeader();        for (java.util.Enumeration columns = tableHeader.getColumnModel()                .getColumns(); columns.hasMoreElements(); column++) {            c = (TableColumn) columns.nextElement();            Component component = (c.getHeaderRenderer() != null) ? c                    .getHeaderRenderer().getTableCellRendererComponent(table,                            c.getHeaderValue(), false, false, -1, column) : tableHeader                    .getDefaultRenderer().getTableCellRendererComponent(table,                            c.getHeaderValue(), false, false, -1, column);            int width = Math.max(c.getWidth(), component.getPreferredSize().width);            if (firstRow >= 0) {                for (int i = firstRow, d = lastRow; i <= d; i++) {                    width = Math.max(width, table.getCellRenderer(i, column)                            .getTableCellRendererComponent(table,                                    table.getModel().getValueAt(i, column), false, false, i,                                    column).getPreferredSize().width                            + intercellSpacing.width);                }            }            c.setPreferredWidth(width);            c.setWidth(width);        }    }    public static JScrollPane findScrollPane(JTable table) {        return (JScrollPane) SwingUtilities.getAncestorOfClass(JScrollPane.class,                table);    }    public static int getFirstVisibleRow(JTable table) {        Point p = table.getVisibleRect().getLocation();        return table.rowAtPoint(p);    }    public static int getLastVisibleRow(JTable table) {        Point p = table.getVisibleRect().getLocation();        p.y = p.y + table.getVisibleRect().height - 1;        int result = table.rowAtPoint(p);        if (result > 0) {            return result;        }        // if there is no rows at this point,rowatpoint() return -1,        // It means that there is not enough rows to fill the rectangle where        // the table is displayed.        // if this case we return getRowCount()-1 because        // we are sure that the last row is visible        if (table.getVisibleRect().height > 0) {            return table.getRowCount() - 1;        } else {            return -1;        }    }    public static void setColumnWidths(JTable table, int[] columnWidths) {        TableColumnModel columns = table.getTableHeader().getColumnModel();        // when restoring from the prefs with a new version of the product,        // then it is possible that: columnWidths.length !=        // columns.getColumnCount()        if (columnWidths == null                || columnWidths.length != columns.getColumnCount()) {            return;        }        for (int i = 0, c = columns.getColumnCount(); i < c; i++) {            columns.getColumn(i).setPreferredWidth(columnWidths[i]);        }        table.getTableHeader().resizeAndRepaint();        JScrollPane scrollpane = findScrollPane(table);        if (scrollpane != null) {            scrollpane.invalidate();        }        table.invalidate();    }    public static int[] getColumnWidths(JTable table) {        TableColumnModel model = table.getTableHeader().getColumnModel();        int[] columnWidths = new int[model.getColumnCount()];        for (int i = 0, c = columnWidths.length; i < c; i++) {            columnWidths[i] = model.getColumn(i).getWidth();        }        return columnWidths;    }}