package financialmanager.view.components;

import financialmanager.controller.ExpenseController;
import financialmanager.model.entities.Category;
import financialmanager.model.entities.Expense;
import financialmanager.model.enums.ExpenseType;
import financialmanager.util.DateUtils;
import financialmanager.util.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class ExpenseDialog extends JDialog {
    private final ExpenseController controller;
    private final Expense expenseToEdit;
    private final JTextField nameField;
    private final JTextField amountField;
    private final JTextField dateField;
    private final JComboBox<ExpenseType> typeCombo;
    private final JComboBox<Category> categoryCombo;
    private final JTextArea descriptionArea;
    private boolean saved = false;

    public ExpenseDialog(Frame owner, ExpenseController controller, String title) {
        this(owner, controller, null, title);
    }

    public ExpenseDialog(Frame owner, ExpenseController controller, Expense expense, String title) {
        super(owner, title, true);
        this.controller = controller;
        this.expenseToEdit = expense;

        setSize(400, 350);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel fieldPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Название
        fieldPanel.add(new JLabel("Название:"));
        nameField = new JTextField();
        fieldPanel.add(nameField);

        // Сумма
        fieldPanel.add(new JLabel("Сумма:"));
        amountField = new JTextField();
        fieldPanel.add(amountField);

        // Дата
        fieldPanel.add(new JLabel("Дата (дд.мм.гггг):"));
        dateField = new JTextField(DateUtils.formatForDisplay(LocalDate.now()));
        fieldPanel.add(dateField);

        // Тип расхода
        fieldPanel.add(new JLabel("Тип расхода:"));
        typeCombo = new JComboBox<>(ExpenseType.values());
        fieldPanel.add(typeCombo);

        // Категория
        fieldPanel.add(new JLabel("Категория:"));
        categoryCombo = new JComboBox<>(new Category[]{
                new Category("1", "Еда"),
                new Category("2", "Транспорт"),
                new Category("3", "Жилье"),
                new Category("4", "Развлечения"),
                new Category("5", "Здоровье")
        });
        fieldPanel.add(categoryCombo);

        add(fieldPanel, BorderLayout.NORTH);

        // Описание
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("Описание"));
        descriptionPanel.add(new JLabel("Описание:"), BorderLayout.NORTH);

        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        add(descriptionPanel, BorderLayout.CENTER);

        // Кнопки
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> saveExpense());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Заполнить данные для редактирования
        if (expense != null) {
            fillFields(expense);
        }
    }

    private void fillFields(Expense expense) {
        nameField.setText(expense.getName());
        amountField.setText(String.valueOf(Math.abs(expense.getAmount())));
        dateField.setText(DateUtils.formatForDisplay(expense.getDate()));
        typeCombo.setSelectedItem(expense.getExpenseType());
        categoryCombo.setSelectedItem(expense.getCategory());
        descriptionArea.setText(expense.getDescription());
    }

    private void saveExpense() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите название расхода", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Сумма должна быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate date = DateUtils.parseFromDisplay(dateField.getText().trim());
            ExpenseType type = (ExpenseType) typeCombo.getSelectedItem();
            Category category = (Category) categoryCombo.getSelectedItem();
            String description = descriptionArea.getText().trim();

            Expense expense;
            if (expenseToEdit == null) {
                // Новый расход
                String id = IDGenerator.generateId("EXP");
                expense = new Expense(id, name, amount, date, description, category, type);
                controller.add(expense);
            } else {
                // Редактирование существующего
                expense = expenseToEdit;
                expense.setName(name);
                expense.setAmount(-amount);
                expense.setDate(date);
                expense.setExpenseType(type);
                expense.setCategory(category);
                expense.setDescription(description);
                controller.update(expense);
            }

            saved = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Некорректный формат суммы", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}