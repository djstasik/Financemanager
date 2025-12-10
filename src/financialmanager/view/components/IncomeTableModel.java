package financialmanager.view.components;

import financialmanager.model.entities.Income;

import java.time.format.DateTimeFormatter;


public class IncomeTableModel extends FinancialTableModel<Income> {
    private static final String[] COLUMNS = {
            "ID", "Название", "Сумма", "Дата", "Источник", "Категория", "Карта", "Описание"
    };

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public IncomeTableModel() {
        super(COLUMNS);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Income income = data.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> income.getId();
            case 1 -> income.getName();
            case 2 -> String.format("%.2f ₽", income.getAmount());
            case 3 -> income.getDate().format(dateFormatter);
            case 4 -> income.getIncomeSource().getDisplayName();
            case 5 -> income.getCategory().getName();
            case 6 -> income.hasCreditCard() ? "💳" : "💵";  // Иконка карты или наличных
            case 7 -> income.getDescription();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }
}