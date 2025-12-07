package financialmanager.persistence;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import financialmanager.model.entities.Category;
import financialmanager.model.entities.Expense;
import financialmanager.model.entities.Income;
import financialmanager.model.enums.ExpenseType;
import financialmanager.model.enums.IncomeSource;
import financialmanager.model.repositories.ExpenseRepository;
import financialmanager.model.repositories.IncomeRepository;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JsonDataManager {
    private final Gson gson;
    private static final String DATA_DIR = "data";
    private static final String EXPENSES_FILE = DATA_DIR + "/expenses.json";
    private static final String INCOMES_FILE = DATA_DIR + "/incomes.json";
    private static final String CATEGORIES_FILE = DATA_DIR + "/categories.json";

    public JsonDataManager() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(Category.class, new CategoryAdapter())
                .registerTypeAdapter(ExpenseType.class, new ExpenseTypeAdapter())
                .registerTypeAdapter(IncomeSource.class, new IncomeSourceAdapter())
                .create();
        createDataDirectory();
    }

    public void saveExpenses(ExpenseRepository repository) throws IOException {
        List<Expense> expenses = repository.findAll();
        saveToFile(expenses, EXPENSES_FILE);
        System.out.println("✓ Сохранено расходов: " + expenses.size());
    }

    public void saveIncomes(IncomeRepository repository) throws IOException {
        List<Income> incomes = repository.findAll();
        saveToFile(incomes, INCOMES_FILE);
        System.out.println("✓ Сохранено доходов: " + incomes.size());
    }

    public void saveCategories(List<Category> categories) throws IOException {
        saveToFile(categories, CATEGORIES_FILE);
        System.out.println("✓ Сохранено категорий: " + categories.size());
    }

    public void loadExpenses(ExpenseRepository repository) throws IOException {
        Type expenseListType = new TypeToken<List<Expense>>(){}.getType();
        List<Expense> expenses = loadFromFile(EXPENSES_FILE, expenseListType);

        if (expenses != null && !expenses.isEmpty()) {
            for (Expense expense : expenses) {
                // Проверяем, существует ли уже такая запись
                if (!repository.exists(expense.getId())) {
                    repository.add(expense);
                }
            }
            System.out.println("✓ Загружено расходов: " + expenses.size());
        } else {
            System.out.println("✗ Файл расходов пуст или не существует");
        }
    }

    public void loadIncomes(IncomeRepository repository) throws IOException {
        Type incomeListType = new TypeToken<List<Income>>(){}.getType();
        List<Income> incomes = loadFromFile(INCOMES_FILE, incomeListType);

        if (incomes != null && !incomes.isEmpty()) {
            for (Income income : incomes) {
                // Проверяем, существует ли уже такая запись
                if (!repository.exists(income.getId())) {
                    repository.add(income);
                }
            }
            System.out.println("✓ Загружено доходов: " + incomes.size());
        } else {
            System.out.println("✗ Файл доходов пуст или не существует");
        }
    }

    public List<Category> loadCategories() throws IOException {
        Type categoryListType = new TypeToken<List<Category>>(){}.getType();
        List<Category> categories = loadFromFile(CATEGORIES_FILE, categoryListType);

        if (categories != null && !categories.isEmpty()) {
            System.out.println("✓ Загружено категорий: " + categories.size());
            return categories;
        } else {
            System.out.println("✗ Файл категорий пуст. Использую категории по умолчанию");
            return getDefaultCategories();
        }
    }

    public void exportExpensesToJson(List<Expense> expenses, String filename) throws IOException {
        saveToFile(expenses, filename);
        System.out.println("✓ Экспортировано расходов в: " + filename);
    }

    public void exportIncomesToJson(List<Income> incomes, String filename) throws IOException {
        saveToFile(incomes, filename);
        System.out.println("✓ Экспортировано доходов в: " + filename);
    }

    private <T> void saveToFile(T data, String filename) throws IOException {
        // Создаем директорию если не существует
        File file = new File(filename);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (Writer writer = new FileWriter(filename)) {
            gson.toJson(data, writer);
        }
    }

    private <T> T loadFromFile(String filename, Type type) throws IOException {
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            return null;
        }
        try (Reader reader = new FileReader(filename)) {
            return gson.fromJson(reader, type);
        }
    }

    private void createDataDirectory() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            boolean created = dir.mkdir();
            if (created) {
                System.out.println("✓ Создана директория для данных: " + DATA_DIR);
            }
        }
    }

    public List<Category> getDefaultCategories() {
        List<Category> categories = new ArrayList<>();

        // Категории для расходов
        categories.add(new Category("1", "Еда", "Продукты питания", "#FF6B6B"));
        categories.add(new Category("2", "Транспорт", "Транспортные расходы", "#4ECDC4"));
        categories.add(new Category("3", "Жилье", "Аренда, коммуналка", "#45B7D1"));
        categories.add(new Category("4", "Развлечения", "Кино, рестораны, хобби", "#96CEB4"));
        categories.add(new Category("5", "Здоровье", "Медицина, спорт", "#FFEAA7"));

        // Категории для доходов
        categories.add(new Category("6", "Зарплата", "Основная работа", "#55EFC4"));
        categories.add(new Category("7", "Инвестиции", "Дивиденды, проценты", "#74B9FF"));
        categories.add(new Category("8", "Фриланс", "Внештатная работа", "#A29BFE"));
        categories.add(new Category("9", "Бизнес", "Бизнес доходы", "#FD79A8"));
        categories.add(new Category("10", "Прочее", "Прочие доходы", "#DFE6E9"));

        return categories;
    }
}