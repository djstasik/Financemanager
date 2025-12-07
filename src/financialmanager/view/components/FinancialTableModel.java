package financialmanager.view.components;

import financialmanager.model.entities.FinancialOperation;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public abstract class FinancialTableModel<T extends FinancialOperation> extends AbstractTableModel {
    protected List<T> data;
    protected final String[] columnNames;

    public FinancialTableModel(String[] columnNames) {
        this.columnNames = columnNames;
        this.data = new java.util.ArrayList<>();
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public abstract Object getValueAt(int rowIndex, int columnIndex);

    public T getOperationAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            return data.get(rowIndex);
        }
        return null;
    }

    public void updateData(List<T> newData) {
        this.data = newData;
        fireTableDataChanged();
    }

    public void addOperation(T operation) {
        data.add(operation);
        fireTableRowsInserted(data.size() - 1, data.size() - 1);
    }

    public void removeOperation(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            data.remove(rowIndex);
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public void updateOperation(int rowIndex, T operation) {
        if (rowIndex >= 0 && rowIndex < data.size()) {
            data.set(rowIndex, operation);
            fireTableRowsUpdated(rowIndex, rowIndex);
        }
    }
}