package financialmanager.view.components;

import financialmanager.model.entities.Expense;

import java.time.format.DateTimeFormatter;

public class ExpenseTableModel extends FinancialTableModel<Expense> {
    private static final String[] COLUMNS = {
            "ID", "Название", "Сумма", "Дата", "Тип", "Категория", "Карта", "Описание"
    };

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public ExpenseTableModel() {
        super(COLUMNS);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Expense expense = data.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> expense.getId();
            case 1 -> expense.getName();
            case 2 -> String.format("%.2f ₽", Math.abs(expense.getAmount()));
            case 3 -> expense.getDate().format(dateFormatter);
            case 4 -> expense.getExpenseType().getDisplayName();
            case 5 -> expense.getCategory().getName();
            case 6 -> expense.hasCreditCard() ? "💳" : "💵";  // Иконка карты или наличных
            case 7 -> expense.getDescription();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
}