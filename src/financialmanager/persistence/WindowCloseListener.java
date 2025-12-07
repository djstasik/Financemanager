package financialmanager.persistence;

import financialmanager.model.repositories.ExpenseRepository;
import financialmanager.model.repositories.IncomeRepository;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowCloseListener extends WindowAdapter {
    private final JsonDataManager dataManager;
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public WindowCloseListener(JsonDataManager dataManager,
                               ExpenseRepository expenseRepository,
                               IncomeRepository incomeRepository) {
        this.dataManager = dataManager;
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        System.out.println("\n=== СОХРАНЕНИЕ ДАННЫХ ===");
        try {
            dataManager.saveExpenses(expenseRepository);
            dataManager.saveIncomes(incomeRepository);
            System.out.println("✓ Данные сохранены");
        } catch (Exception ex) {
            System.err.println("✗ Ошибка сохранения: " + ex.getMessage());
        }
        System.out.println("Выход...");
        System.exit(0);
    }
}