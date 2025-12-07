package financialmanager.view.components;

import financialmanager.controller.IncomeController;
import financialmanager.model.entities.Category;
import financialmanager.model.entities.Income;
import financialmanager.model.enums.IncomeSource;
import financialmanager.util.DateUtils;
import financialmanager.util.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class IncomeDialog extends JDialog {
    private final IncomeController controller;
    private final Income incomeToEdit;
    private final JTextField nameField;
    private final JTextField amountField;
    private final JTextField dateField;
    private final JComboBox<IncomeSource> sourceCombo;
    private final JComboBox<Category> categoryCombo;
    private final JTextArea descriptionArea;
    private boolean saved = false;

    public IncomeDialog(Frame owner, IncomeController controller, String title) {
        this(owner, controller, null, title);
    }

    public IncomeDialog(Frame owner, IncomeController controller, Income income, String title) {
        super(owner, title, true);
        this.controller = controller;
        this.incomeToEdit = income;

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

        // Источник дохода
        fieldPanel.add(new JLabel("Источник дохода:"));
        sourceCombo = new JComboBox<>(IncomeSource.values());
        fieldPanel.add(sourceCombo);

        // Категория
        fieldPanel.add(new JLabel("Категория:"));
        categoryCombo = new JComboBox<>(new Category[]{
                new Category("6", "Зарплата"),
                new Category("7", "Инвестиции"),
                new Category("8", "Фриланс"),
                new Category("9", "Бизнес"),
                new Category("10", "Прочее")
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

        saveButton.addActionListener(e -> saveIncome());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Заполнить данные для редактирования
        if (income != null) {
            fillFields(income);
        }
    }

    private void fillFields(Income income) {
        nameField.setText(income.getName());
        amountField.setText(String.valueOf(income.getAmount()));
        dateField.setText(DateUtils.formatForDisplay(income.getDate()));
        sourceCombo.setSelectedItem(income.getIncomeSource());
        categoryCombo.setSelectedItem(income.getCategory());
        descriptionArea.setText(income.getDescription());
    }

    private void saveIncome() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Введите название дохода", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) {
                JOptionPane.showMessageDialog(this, "Сумма должна быть больше 0", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate date = DateUtils.parseFromDisplay(dateField.getText().trim());
            IncomeSource source = (IncomeSource) sourceCombo.getSelectedItem();
            Category category = (Category) categoryCombo.getSelectedItem();
            String description = descriptionArea.getText().trim();

            Income income;
            if (incomeToEdit == null) {
                // Новый доход
                String id = IDGenerator.generateId("INC");
                income = new Income(id, name, amount, date, description, category, source);
                controller.add(income);
            } else {
                // Редактирование существующего
                income = incomeToEdit;
                income.setName(name);
                income.setAmount(amount);
                income.setDate(date);
                income.setIncomeSource(source);
                income.setCategory(category);
                income.setDescription(description);
                controller.update(income);
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