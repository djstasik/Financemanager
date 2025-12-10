package financialmanager;

import financialmanager.controller.AnalyticsController;
import financialmanager.controller.ExpenseController;
import financialmanager.controller.IncomeController;
import financialmanager.model.entities.Category;
import financialmanager.model.entities.CreditCard;
import financialmanager.model.entities.Expense;
import financialmanager.model.entities.Income;
import financialmanager.model.managers.CreditCardManager;
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
    private static CreditCardManager creditCardManager;

    public static void main(String[] args) {
        // Инициализация менеджера данных
        dataManager = new JsonDataManager();

        // Инициализация репозиториев
        InMemoryExpenseRepository expenseRepository = new InMemoryExpenseRepository();
        InMemoryIncomeRepository incomeRepository = new InMemoryIncomeRepository();

        // Загрузка данных
        loadAllData(expenseRepository, incomeRepository);

        // Инициализация менеджера кредитных карт
        creditCardManager = new CreditCardManager();
        initializeCreditCards();

        // Создание сервисов
        ExpenseService expenseService = new ExpenseService(expenseRepository);
        IncomeService incomeService = new IncomeService(incomeRepository);
        AnalyticsService analyticsService = new AnalyticsService(expenseRepository, incomeRepository);

        // Создание контроллеров
        ExpenseController expenseController = new ExpenseController(expenseService);
        IncomeController incomeController = new IncomeController(incomeService);
        AnalyticsController analyticsController = new AnalyticsController(analyticsService);

        // Запуск GUI с передачей менеджера карт
        launchGUI(expenseController, incomeController, analyticsController,
                expenseRepository, incomeRepository, dataManager, creditCardManager);
    }

    private static void loadAllData(InMemoryExpenseRepository expenseRepository,
                                    InMemoryIncomeRepository incomeRepository) {
        System.out.println("=== ЗАГРУЗКА ДАННЫХ ===");

        try {
            // Загружаем категории
            categories = dataManager.loadCategories();
            System.out.println("Категории загружены: " + categories.size());

            // Загружаем расходы и доходы
            dataManager.loadExpenses(expenseRepository);
            dataManager.loadIncomes(incomeRepository);

            // Если данных нет - добавляем примеры
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
            // Находим категории
            Category food = findCategory("Еда");
            Category transport = findCategory("Транспорт");
            Category housing = findCategory("Жилье");
            Category salary = findCategory("Зарплата");
            Category freelance = findCategory("Фриланс");

            // Примеры расходов
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

            // Примеры доходов
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

            // Добавляем
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

    private static void initializeCreditCards() {
        // Создаем тестовые карты
        CreditCard card1 = new CreditCard(
                "CARD_1",
                "**** 1234",
                "Иван Иванов",
                50000.0,
                LocalDate.now().plusYears(3)
        );
        card1.setCurrentBalance(15000.0);

        CreditCard card2 = new CreditCard(
                "CARD_2",
                "**** 5678",
                "Мария Петрова",
                100000.0,
                LocalDate.now().plusYears(2)
        );
        card2.setCurrentBalance(25000.0);

        creditCardManager.addCard(card1);
        creditCardManager.addCard(card2);

        System.out.println("Инициализировано кредитных карт: " + creditCardManager.size());
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
                                  JsonDataManager dataManager,
                                  CreditCardManager creditCardManager) {

        SwingUtilities.invokeLater(() -> {
            try {
                // Установка внешнего вида
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                // Создание главного окна с передачей менеджера карт
                MainApplicationFrame frame = new MainApplicationFrame(
                        expenseController,
                        incomeController,
                        analyticsController,
                        dataManager,
                        creditCardManager
                );

                // Добавляем слушатель для сохранения данных при закрытии
                frame.addWindowListener(new WindowCloseListener(
                        dataManager, expenseRepository, incomeRepository
                ));

                // Вывод информации о запуске
                printStartupInfo(expenseController, incomeController, analyticsController, creditCardManager);

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
                                         AnalyticsController analyticsController,
                                         CreditCardManager creditCardManager) {

        System.out.println("\n=== ФИНАНСОВЫЙ МЕНЕДЖЕР ЗАПУЩЕН ===");
        System.out.println("Статистика системы:");
        System.out.println(" - Расходов: " + expenseController.getAll().size());
        System.out.println(" - Доходов: " + incomeController.getAll().size());
        System.out.println(" - Кредитных карт: " + creditCardManager.size());
        System.out.println(" - Общая задолженность по картам: " + creditCardManager.getTotalDebt() + " ₽");
        System.out.println(" - Доступный кредит: " + creditCardManager.getTotalAvailableCredit() + " ₽");
        System.out.println(" - Баланс: " + analyticsController.getTotalBalance() + " ₽");
        System.out.println("\nГотов к работе!");
    }
}