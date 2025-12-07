package financialmanager;

import financialmanager.controller.AnalyticsController;
import financialmanager.controller.ExpenseController;
import financialmanager.controller.IncomeController;
import financialmanager.model.entities.Category;
import financialmanager.model.entities.Expense;
import financialmanager.model.entities.Income;
import financialmanager.model.enums.ExpenseType;
import financialmanager.model.enums.IncomeSource;
import financialmanager.model.repositories.InMemoryExpenseRepository;
import financialmanager.model.repositories.InMemoryIncomeRepository;
import financialmanager.persistence.JsonDataManager;
import financialmanager.persistence.WindowCloseListener;
import financialmanager.service.AnalyticsService;
import financialmanager.service.ExpenseService;
import financialmanager.service.IncomeService;
import financialmanager.view.frames.MainApplicationFrame;

import javax.swing.*;
import java.time.LocalDate;
import java.util.List;

public class Main {
    private static JsonDataManager dataManager;
    private static List<Category> categories;

    public static void main(String[] args) {
        dataManager = new JsonDataManager();

        InMemoryExpenseRepository expenseRepository = new InMemoryExpenseRepository();
        InMemoryIncomeRepository incomeRepository = new InMemoryIncomeRepository();

        loadAllData(expenseRepository, incomeRepository);

        ExpenseService expenseService = new ExpenseService(expenseRepository);
        IncomeService incomeService = new IncomeService(incomeRepository);
        AnalyticsService analyticsService = new AnalyticsService(expenseRepository, incomeRepository);

        ExpenseController expenseController = new ExpenseController(expenseService);
        IncomeController incomeController = new IncomeController(incomeService);
        AnalyticsController analyticsController = new AnalyticsController(analyticsService);

        launchGUI(expenseController, incomeController, analyticsController,
                expenseRepository, incomeRepository, dataManager);
    }

    private static void loadAllData(InMemoryExpenseRepository expenseRepository,
                                    InMemoryIncomeRepository incomeRepository) {
        System.out.println("=== ЗАГРУЗКА ДАННЫХ ===");

        try {
            categories = dataManager.loadCategories();
            System.out.println("Категории загружены: " + categories.size());

            dataManager.loadExpenses(expenseRepository);
            dataManager.loadIncomes(incomeRepository);

            if (expenseRepository.findAll().isEmpty() && incomeRepository.findAll().isEmpty()) {
                System.out.println("Данных не найдено. Добавляю примеры...");
                addSampleData(expenseRepository, incomeRepository);
            }

        } catch (Exception e) {
            System.err.println("Ошибка при загрузке данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addSampleData(InMemoryExpenseRepository expenseRepository,
                                      InMemoryIncomeRepository incomeRepository) {
        try {
            Category food = findCategory("Еда");
            Category transport = findCategory("Транспорт");
            Category housing = findCategory("Жилье");
            Category salary = findCategory("Зарплата");
            Category freelance = findCategory("Фриланс");

            Expense expense1 = new Expense(
                    "EXP_" + System.currentTimeMillis() + "_1",
                    "Продукты на неделю",
                    3500.0,
                    LocalDate.now().minusDays(3),
                    "Покупки в супермаркете",
                    food,
                    ExpenseType.VARIABLE
            );

            Expense expense2 = new Expense(
                    "EXP_" + System.currentTimeMillis() + "_2",
                    "Бензин",
                    2500.0,
                    LocalDate.now().minusDays(1),
                    "Заправка автомобиля",
                    transport,
                    ExpenseType.FIXED
            );

            Expense expense3 = new Expense(
                    "EXP_" + System.currentTimeMillis() + "_3",
                    "Аренда квартиры",
                    15000.0,
                    LocalDate.now().minusDays(10),
                    "Ежемесячная аренда",
                    housing,
                    ExpenseType.FIXED
            );

            Income income1 = new Income(
                    "INC_" + System.currentTimeMillis() + "_1",
                    "Зарплата",
                    50000.0,
                    LocalDate.now().minusDays(5),
                    "Основная работа",
                    salary,
                    IncomeSource.SALARY
            );

            Income income2 = new Income(
                    "INC_" + System.currentTimeMillis() + "_2",
                    "Фриланс проект",
                    20000.0,
                    LocalDate.now().minusDays(2),
                    "Веб-разработка сайта",
                    freelance,
                    IncomeSource.FREELANCE
            );

            expenseRepository.add(expense1);
            expenseRepository.add(expense2);
            expenseRepository.add(expense3);
            incomeRepository.add(income1);
            incomeRepository.add(income2);

            System.out.println("Добавлено примеров:");
            System.out.println(" - 3 расхода");
            System.out.println(" - 2 дохода");

        } catch (Exception e) {
            System.err.println("Ошибка при добавлении примеров: " + e.getMessage());
        }
    }

    private static Category findCategory(String name) {
        return categories.stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(new Category("999", name, "Категория по умолчанию", "#CCCCCC"));
    }

    private static void launchGUI(ExpenseController expenseController,
                                  IncomeController incomeController,
                                  AnalyticsController analyticsController,
                                  InMemoryExpenseRepository expenseRepository,
                                  InMemoryIncomeRepository incomeRepository,
                                  JsonDataManager dataManager) {

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                MainApplicationFrame frame = new MainApplicationFrame(
                        expenseController,
                        incomeController,
                        analyticsController,
                        dataManager
                );

                frame.addWindowListener(new WindowCloseListener(
                        dataManager, expenseRepository, incomeRepository
                ));

                printStartupInfo(expenseController, incomeController, analyticsController);

            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Ошибка при запуске приложения:\n" + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static void printStartupInfo(ExpenseController expenseController,
                                         IncomeController incomeController,
                                         AnalyticsController analyticsController) {

        System.out.println("\n=== ФИНАНСОВЫЙ МЕНЕДЖЕР ЗАПУЩЕН ===");
        System.out.println("Данные хранятся в формате JSON в папке 'data/'");
        System.out.println("При закрытии программы данные сохраняются автоматически");
        System.out.println("\nСтатистика системы:");
        System.out.println(" - Расходов: " + expenseController.getAll().size());
        System.out.println(" - Доходов: " + incomeController.getAll().size());
        System.out.println(" - Баланс: " + analyticsController.getTotalBalance() + " ₽");
        System.out.println("\nГотов к работе!");
    }
}