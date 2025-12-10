package financialmanager.view.components;

import financialmanager.controller.IncomeController;
import financialmanager.model.entities.Category;
import financialmanager.model.entities.CreditCard;
import financialmanager.model.entities.Income;
import financialmanager.model.managers.CreditCardManager;
import financialmanager.model.enums.IncomeSource;
import financialmanager.util.DateUtils;
import financialmanager.util.IDGenerator;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class IncomeDialog extends JDialog {
    private final IncomeController controller;
    private final CreditCardManager cardManager;
    private final Income incomeToEdit;
    private final JTextField nameField;
    private final JTextField amountField;
    private final JTextField dateField;
    private final JComboBox<IncomeSource> sourceCombo;
    private final JComboBox<Category> categoryCombo;
    private final JComboBox<String> cardCombo;
    private final JTextArea descriptionArea;
    private boolean saved = false;

    public IncomeDialog(Frame owner, IncomeController controller,
                        CreditCardManager cardManager, String title) {
        this(owner, controller, cardManager, null, title);
    }

    public IncomeDialog(Frame owner, IncomeController controller,
                        CreditCardManager cardManager, Income income, String title) {
        super(owner, title, true);
        this.controller = controller;
        this.cardManager = cardManager;
        this.incomeToEdit = income;

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

        fieldPanel.add(new JLabel("Источник дохода:"));
        sourceCombo = new JComboBox<>(IncomeSource.values());
        fieldPanel.add(sourceCombo);

        fieldPanel.add(new JLabel("Категория:"));
        categoryCombo = new JComboBox<>(new Category[]{
                new Category("6", "Зарплата"),
                new Category("7", "Инвестиции"),
                new Category("8", "Фриланс"),
                new Category("9", "Бизнес"),
                new Category("10", "Прочее")
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

        saveButton.addActionListener(e -> saveIncome());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        if (income != null) {
            fillFields(income);
        }

        // Добавляем слушатель изменений карт
        cardManager.addListener(cards -> {
            SwingUtilities.invokeLater(() -> {
                updateCardCombo();
                if (incomeToEdit != null && incomeToEdit.hasCreditCard()) {
                    fillFields(incomeToEdit);
                }
            });
        });
    }

    private void updateCardCombo() {
        cardCombo.removeAllItems();
        cardCombo.addItem("Без карты (наличные)");

        for (CreditCard card : cardManager.getAllCards()) {
            cardCombo.addItem(card.getCardNumber() + " - " + card.getOwnerName() +
                    " (задолженность: " + card.getCurrentBalance() + " ₽)");
        }
    }

    private void fillFields(Income income) {
        nameField.setText(income.getName());
        amountField.setText(String.valueOf(income.getAmount()));
        dateField.setText(DateUtils.formatForDisplay(income.getDate()));
        sourceCombo.setSelectedItem(income.getIncomeSource());
        categoryCombo.setSelectedItem(income.getCategory());
        descriptionArea.setText(income.getDescription());

        if (income.hasCreditCard()) {
            String cardId = income.getCreditCardId();
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

            // Определяем карту и пополняем
            String creditCardId = null;
            CreditCard selectedCard = null;
            int selectedCardIndex = cardCombo.getSelectedIndex();

            if (selectedCardIndex > 0) {
                selectedCard = cardManager.getAllCards().get(selectedCardIndex - 1);
                creditCardId = selectedCard.getId();

                // ПОПОЛНЯЕМ КАРТУ!
                try {
                    double oldBalance = selectedCard.getCurrentBalance();
                    selectedCard.deposit(amount);
                    System.out.println("✅ Пополнена карта " + selectedCard.getCardNumber() +
                            " на " + amount + " ₽. Старый баланс: " + oldBalance +
                            " ₽, новый: " + selectedCard.getCurrentBalance() + " ₽");

                    // Обновляем карту в менеджере
                    cardManager.updateCard(selectedCard);

                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "Ошибка пополнения: " + e.getMessage(),
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Создаем доход
            Income income;
            if (incomeToEdit == null) {
                String id = IDGenerator.generateId("INC");
                income = new Income(id, name, amount, date, description, category, source, creditCardId);

                // Сохраняем
                controller.add(income);

                // Сообщение об успехе
                String successMsg = "✅ Доход успешно добавлен!\n" +
                        "Название: " + name + "\n" +
                        "Сумма: " + amount + " ₽\n" +
                        (selectedCard != null ?
                                "На карту: " + selectedCard.getCardNumber() + "\n" +
                                        "Новый баланс карты: " + selectedCard.getCurrentBalance() + " ₽\n" +
                                        "Доступно: " + selectedCard.getAvailableCredit() + " ₽" :
                                "Получено: наличными");

                JOptionPane.showMessageDialog(this, successMsg, "Успех", JOptionPane.INFORMATION_MESSAGE);

            } else {
                // РЕДАКТИРОВАНИЕ С КОРРЕКТИРОВКОЙ БАЛАНСА КАРТЫ
                double oldAmount = incomeToEdit.getAmount();
                String oldCardId = incomeToEdit.getCreditCardId();

                // Если была привязана старая карта - отменяем старый доход (возвращаем баланс)
                if (oldCardId != null && !oldCardId.isEmpty()) {
                    CreditCard oldCard = cardManager.getCardById(oldCardId);
                    if (oldCard != null) {
                        oldCard.withdraw(oldAmount); // Отменяем старый доход
                        cardManager.updateCard(oldCard);
                    }
                }

                // Если выбрана новая карта - добавляем доход на нее
                if (selectedCard != null) {
                    try {
                        selectedCard.deposit(amount);
                        cardManager.updateCard(selectedCard);
                    } catch (IllegalArgumentException e) {
                        // Если не удалось добавить на новую карту - восстанавливаем старую
                        if (oldCardId != null && !oldCardId.isEmpty()) {
                            CreditCard oldCard = cardManager.getCardById(oldCardId);
                            if (oldCard != null) {
                                oldCard.deposit(oldAmount);
                                cardManager.updateCard(oldCard);
                            }
                        }
                        throw e;
                    }
                }

                income = incomeToEdit;
                income.setName(name);
                income.setAmount(amount);
                income.setDate(date);
                income.setIncomeSource(source);
                income.setCategory(category);
                income.setDescription(description);
                income.setCreditCardId(creditCardId);
                controller.update(income);

                JOptionPane.showMessageDialog(this, "✅ Доход обновлен", "Успех", JOptionPane.INFORMATION_MESSAGE);
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