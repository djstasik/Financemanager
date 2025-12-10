package financialmanager.view.components;

import financialmanager.controller.ExpenseController;
import financialmanager.model.entities.Category;
import financialmanager.model.entities.CreditCard;
import financialmanager.model.entities.Expense;
import financialmanager.model.managers.CreditCardManager;
import financialmanager.model.enums.ExpenseType;
import financialmanager.util.DateUtils;
import financialmanager.util.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class ExpenseDialog extends JDialog {
    private final ExpenseController controller;
    private final CreditCardManager cardManager;
    private final Expense expenseToEdit;
    private final JTextField nameField;
    private final JTextField amountField;
    private final JTextField dateField;
    private final JComboBox<ExpenseType> typeCombo;
    private final JComboBox<Category> categoryCombo;
    private final JComboBox<String> cardCombo;
    private final JTextArea descriptionArea;
    private boolean saved = false;

    public ExpenseDialog(Frame owner, ExpenseController controller,
                         CreditCardManager cardManager, String title) {
        this(owner, controller, cardManager, null, title);
    }

    public ExpenseDialog(Frame owner, ExpenseController controller,
                         CreditCardManager cardManager, Expense expense, String title) {
        super(owner, title, true);
        this.controller = controller;
        this.cardManager = cardManager;
        this.expenseToEdit = expense;

        setSize(400, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel fieldPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldPanel.add(new JLabel("Название:"));
        nameField = new JTextField();
        fieldPanel.add(nameField);

        fieldPanel.add(new JLabel("Сумма:"));
        amountField = new JTextField();
        fieldPanel.add(amountField);

        fieldPanel.add(new JLabel("Дата (дд.мм.гггг):"));
        dateField = new JTextField(DateUtils.formatForDisplay(LocalDate.now()));
        fieldPanel.add(dateField);

        fieldPanel.add(new JLabel("Тип расхода:"));
        typeCombo = new JComboBox<>(ExpenseType.values());
        fieldPanel.add(typeCombo);

        fieldPanel.add(new JLabel("Категория:"));
        categoryCombo = new JComboBox<>(new Category[]{
                new Category("1", "Еда"),
                new Category("2", "Транспорт"),
                new Category("3", "Жилье"),
                new Category("4", "Развлечения"),
                new Category("5", "Здоровье")
        });
        fieldPanel.add(categoryCombo);

        fieldPanel.add(new JLabel("Кредитная карта:"));
        cardCombo = new JComboBox<>();
        updateCardCombo();
        fieldPanel.add(cardCombo);

        add(fieldPanel, BorderLayout.NORTH);

        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.setBorder(BorderFactory.createTitledBorder("Описание"));
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);

        add(descriptionPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> saveExpense());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (expense != null) {
            fillFields(expense);
        }

        // Добавляем слушатель изменений карт
        cardManager.addListener(cards -> {
            SwingUtilities.invokeLater(() -> {
                updateCardCombo();
                if (expenseToEdit != null && expenseToEdit.hasCreditCard()) {
                    fillFields(expenseToEdit);
                }
            });
        });
    }

    private void updateCardCombo() {
        cardCombo.removeAllItems();
        cardCombo.addItem("Наличные");

        for (CreditCard card : cardManager.getAllCards()) {
            cardCombo.addItem(card.getCardNumber() + " - " + card.getOwnerName() +
                    " (доступно: " + card.getAvailableCredit() + " ₽)");
        }
    }

    private void fillFields(Expense expense) {
        nameField.setText(expense.getName());
        amountField.setText(String.valueOf(Math.abs(expense.getAmount())));
        dateField.setText(DateUtils.formatForDisplay(expense.getDate()));
        typeCombo.setSelectedItem(expense.getExpenseType());
        categoryCombo.setSelectedItem(expense.getCategory());
        descriptionArea.setText(expense.getDescription());

        if (expense.hasCreditCard()) {
            String cardId = expense.getCreditCardId();
            List<CreditCard> cards = cardManager.getAllCards();
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getId().equals(cardId)) {
                    cardCombo.setSelectedIndex(i + 1);
                    break;
                }
            }
        } else {
            cardCombo.setSelectedIndex(0);
        }
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

            // Определяем карту и списываем средства
            String creditCardId = null;
            CreditCard selectedCard = null;
            int selectedCardIndex = cardCombo.getSelectedIndex();

            if (selectedCardIndex > 0) {
                selectedCard = cardManager.getAllCards().get(selectedCardIndex - 1);
                creditCardId = selectedCard.getId();

                // ПРОВЕРКА ЛИМИТА И СПИСАНИЕ
                if (amount > selectedCard.getAvailableCredit()) {
                    JOptionPane.showMessageDialog(this,
                            String.format("❌ Недостаточно средств на карте!\n\n" +
                                            "Сумма расхода: %.2f ₽\n" +
                                            "Доступно на карте: %.2f ₽\n" +
                                            "Не хватает: %.2f ₽",
                                    amount, selectedCard.getAvailableCredit(),
                                    amount - selectedCard.getAvailableCredit()),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // СПИСЫВАЕМ ДЕНЬГИ С КАРТЫ!
                try {
                    selectedCard.withdraw(amount);
                    System.out.println("✅ Списано с карты " + selectedCard.getCardNumber() +
                            ": " + amount + " ₽. Новый баланс: " +
                            selectedCard.getCurrentBalance() + " ₽");

                    // Обновляем карту в менеджере
                    cardManager.updateCard(selectedCard);

                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "Ошибка списания: " + e.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Создаем расход
            Expense expense;
            if (expenseToEdit == null) {
                String id = IDGenerator.generateId("EXP");
                expense = new Expense(id, name, -amount, date, description, category, type, creditCardId);

                // Сохраняем
                controller.add(expense);

                // Сообщение об успехе
                String successMsg = "✅ Расход успешно добавлен!\n" +
                        "Название: " + name + "\n" +
                        "Сумма: " + amount + " ₽\n" +
                        (selectedCard != null ?
                                "С карты: " + selectedCard.getCardNumber() + "\n" +
                                        "Новый баланс карты: " + selectedCard.getCurrentBalance() + " ₽\n" +
                                        "Доступно: " + selectedCard.getAvailableCredit() + " ₽" :
                                "Оплата: наличными");

                JOptionPane.showMessageDialog(this, successMsg, "Успех", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // РЕДАКТИРОВАНИЕ С ВОЗВРАТОМ СРЕДСТВ
                double oldAmount = Math.abs(expenseToEdit.getAmount());
                String oldCardId = expenseToEdit.getCreditCardId();

                // Если была привязана карта - возвращаем старые средства
                if (oldCardId != null && !oldCardId.isEmpty()) {
                    CreditCard oldCard = cardManager.getCardById(oldCardId);
                    if (oldCard != null) {
                        oldCard.deposit(oldAmount);
                        cardManager.updateCard(oldCard);
                    }
                }

                // Если выбрана новая карта - списываем с нее
                if (selectedCard != null) {
                    try {
                        selectedCard.withdraw(amount);
                        cardManager.updateCard(selectedCard);
                    } catch (IllegalArgumentException e) {
                        // Если не удалось списать с новой карты - возвращаем старые средства
                        if (oldCardId != null && !oldCardId.isEmpty()) {
                            CreditCard oldCard = cardManager.getCardById(oldCardId);
                            if (oldCard != null) {
                                oldCard.withdraw(oldAmount);
                                cardManager.updateCard(oldCard);
                            }
                        }
                        throw e;
                    }
                }

                expense = expenseToEdit;
                expense.setName(name);
                expense.setAmount(-amount);
                expense.setDate(date);
                expense.setExpenseType(type);
                expense.setCategory(category);
                expense.setDescription(description);
                expense.setCreditCardId(creditCardId);
                controller.update(expense);

                JOptionPane.showMessageDialog(this, "✅ Расход обновлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
            }

            saved = true;
            dispose();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "❌ Некорректный формат суммы", "Ошибка", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Ошибка: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}