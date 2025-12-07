package financialmanager.view.components;

import financialmanager.model.entities.CreditCard;
import financialmanager.model.enums.TransactionType;
import financialmanager.util.DateUtils;
import financialmanager.util.IDGenerator;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CreditCardPanel extends JPanel {
    private final List<CreditCard> creditCards = new ArrayList<>();
    private final CreditCardTableModel tableModel;
    private final JTable table;
    private JLabel totalCardsLabel;
    private JLabel totalBalanceLabel;
    private JLabel totalCreditLabel;

    public CreditCardPanel() {
        setLayout(new BorderLayout(10, 10));

        // Инициализация примерами
        initializeSampleCards();

        // Верхняя панель с информацией
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Таблица кредитных карт
        tableModel = new CreditCardTableModel();
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Кредитные карты"));
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        updateInfo();
    }

    private void initializeSampleCards() {
        // Добавляем примеры карт для демонстрации
        creditCards.add(new CreditCard(
                IDGenerator.generateId("CARD"),
                "**** 1234",
                "Иван Иванов",
                50000.0,
                LocalDate.now().plusYears(3)
        ));

        creditCards.add(new CreditCard(
                IDGenerator.generateId("CARD"),
                "**** 5678",
                "Иван Иванов",
                100000.0,
                LocalDate.now().plusYears(2)
        ));

        // Добавляем немного задолженности для примера
        creditCards.get(0).setCurrentBalance(15000.0);
        creditCards.get(1).setCurrentBalance(25000.0);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Управление кредитными картами", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        totalCardsLabel = new JLabel("Карт: 0");
        totalBalanceLabel = new JLabel("Задолженность: 0 ₽");
        totalCreditLabel = new JLabel("Доступно: 0 ₽");

        infoPanel.add(totalCardsLabel);
        infoPanel.add(totalBalanceLabel);
        infoPanel.add(totalCreditLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Добавить карту");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton depositButton = new JButton("Пополнить");
        JButton withdrawButton = new JButton("Снять средства");
        JButton reportButton = new JButton("Отчет");

        addButton.addActionListener(e -> addCard());
        editButton.addActionListener(e -> editCard());
        deleteButton.addActionListener(e -> deleteCard());
        depositButton.addActionListener(e -> processTransaction(TransactionType.DEPOSIT));
        withdrawButton.addActionListener(e -> processTransaction(TransactionType.WITHDRAWAL));
        reportButton.addActionListener(e -> showReport());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(depositButton);
        panel.add(withdrawButton);
        panel.add(reportButton);

        return panel;
    }

    private void addCard() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Добавить кредитную карту", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel fieldPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldPanel.add(new JLabel("Номер карты:"));
        JTextField cardNumberField = new JTextField();
        fieldPanel.add(cardNumberField);

        fieldPanel.add(new JLabel("Держатель:"));
        JTextField holderField = new JTextField();
        fieldPanel.add(holderField);

        fieldPanel.add(new JLabel("Кредитный лимит:"));
        JTextField limitField = new JTextField();
        fieldPanel.add(limitField);

        fieldPanel.add(new JLabel("Срок действия (мм/гг):"));
        JTextField expiryField = new JTextField();
        fieldPanel.add(expiryField);

        fieldPanel.add(new JLabel("Начальный баланс:"));
        JTextField balanceField = new JTextField("0");
        fieldPanel.add(balanceField);

        dialog.add(fieldPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> {
            try {
                String cardNumber = cardNumberField.getText().trim();
                String holderName = holderField.getText().trim();
                double creditLimit = Double.parseDouble(limitField.getText().trim());
                String expiry = expiryField.getText().trim();
                double initialBalance = Double.parseDouble(balanceField.getText().trim());

                if (cardNumber.isEmpty() || holderName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Заполните все обязательные поля", "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Парсим дату MM/YY
                String[] expiryParts = expiry.split("/");
                int month = Integer.parseInt(expiryParts[0]);
                int year = 2000 + Integer.parseInt(expiryParts[1]); // Преобразуем YY в YYYY
                LocalDate expiryDate = LocalDate.of(year, month, 1).withDayOfMonth(1);

                CreditCard card = new CreditCard(
                        IDGenerator.generateId("CARD"),
                        cardNumber,
                        holderName,
                        creditLimit,
                        expiryDate
                );
                card.setCurrentBalance(initialBalance);

                creditCards.add(card);
                tableModel.fireTableDataChanged();
                updateInfo();
                dialog.dispose();

                JOptionPane.showMessageDialog(this, "Кредитная карта добавлена", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void editCard() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите карту для редактирования", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CreditCard card = creditCards.get(selectedRow);
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Редактировать карту", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));

        JPanel fieldPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldPanel.add(new JLabel("Номер карты:"));
        JTextField cardNumberField = new JTextField(card.getCardNumber());
        cardNumberField.setEditable(false);
        fieldPanel.add(cardNumberField);

        fieldPanel.add(new JLabel("Держатель:"));
        JTextField holderField = new JTextField(card.getHolderName());
        fieldPanel.add(holderField);

        fieldPanel.add(new JLabel("Кредитный лимит:"));
        JTextField limitField = new JTextField(String.valueOf(card.getCreditLimit()));
        fieldPanel.add(limitField);

        fieldPanel.add(new JLabel("Текущий баланс:"));
        JTextField balanceField = new JTextField(String.valueOf(card.getCurrentBalance()));
        fieldPanel.add(balanceField);

        dialog.add(fieldPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        saveButton.addActionListener(e -> {
            try {
                card.setHolderName(holderField.getText().trim());
                card.setCreditLimit(Double.parseDouble(limitField.getText().trim()));
                card.setCurrentBalance(Double.parseDouble(balanceField.getText().trim()));

                tableModel.fireTableDataChanged();
                updateInfo();
                dialog.dispose();

                JOptionPane.showMessageDialog(this, "Карта обновлена", "Успех", JOptionPane.INFORMATION_MESSAGE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteCard() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите карту для удаления", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить выбранную карту?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {
            creditCards.remove(selectedRow);
            tableModel.fireTableDataChanged();
            updateInfo();
            JOptionPane.showMessageDialog(this, "Карта удалена", "Успех", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void processTransaction(TransactionType type) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите карту", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CreditCard card = creditCards.get(selectedRow);

        String amountStr = JOptionPane.showInputDialog(
                this,
                type == TransactionType.DEPOSIT ?
                        "Сумма пополнения (текущая задолженность: " + card.getCurrentBalance() + " ₽)" :
                        "Сумма снятия (доступно: " + card.getAvailableCredit() + " ₽)",
                type.getDisplayName(),
                JOptionPane.QUESTION_MESSAGE
        );

        if (amountStr != null && !amountStr.trim().isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr.trim());

                if (type == TransactionType.DEPOSIT) {
                    card.deposit(amount);
                    JOptionPane.showMessageDialog(this,
                            String.format("Карта пополнена на %.2f ₽\nНовая задолженность: %.2f ₽",
                                    amount, card.getCurrentBalance()),
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    card.withdraw(amount);
                    JOptionPane.showMessageDialog(this,
                            String.format("Снято %.2f ₽\nНовая задолженность: %.2f ₽\nДоступно: %.2f ₽",
                                    amount, card.getCurrentBalance(), card.getAvailableCredit()),
                            "Успех", JOptionPane.INFORMATION_MESSAGE);
                }

                tableModel.fireTableDataChanged();
                updateInfo();

            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Некорректная сумма", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showReport() {
        double totalDebt = creditCards.stream()
                .mapToDouble(CreditCard::getCurrentBalance)
                .sum();

        double totalCredit = creditCards.stream()
                .mapToDouble(CreditCard::getCreditLimit)
                .sum();

        double totalAvailable = creditCards.stream()
                .mapToDouble(CreditCard::getAvailableCredit)
                .sum();

        double utilizationRate = totalCredit > 0 ? (totalDebt / totalCredit) * 100 : 0;

        StringBuilder report = new StringBuilder();
        report.append("=== ОТЧЕТ ПО КРЕДИТНЫМ КАРТАМ ===\n\n");
        report.append(String.format("Всего карт: %d\n", creditCards.size()));
        report.append(String.format("Общая задолженность: %.2f ₽\n", totalDebt));
        report.append(String.format("Общий кредитный лимит: %.2f ₽\n", totalCredit));
        report.append(String.format("Общий доступный кредит: %.2f ₽\n", totalAvailable));
        report.append(String.format("Коэффициент использования: %.1f%%\n\n", utilizationRate));

        report.append("Информация по картам:\n");
        for (int i = 0; i < creditCards.size(); i++) {
            CreditCard card = creditCards.get(i);
            double cardUtilization = (card.getCurrentBalance() / card.getCreditLimit()) * 100;

            report.append(String.format("%d. %s\n", i + 1, card.getCardNumber()));
            report.append(String.format("   Держатель: %s\n", card.getHolderName()));
            report.append(String.format("   Лимит: %.2f ₽ | Задолженность: %.2f ₽ | Доступно: %.2f ₽\n",
                    card.getCreditLimit(), card.getCurrentBalance(), card.getAvailableCredit()));
            report.append(String.format("   Использование: %.1f%% | Срок действия: %s\n\n",
                    cardUtilization, DateUtils.formatForDisplay(card.getExpiryDate())));
        }

        JTextArea textArea = new JTextArea(report.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Отчет по кредитным картам",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateInfo() {
        int cardCount = creditCards.size();
        double totalBalance = creditCards.stream()
                .mapToDouble(CreditCard::getCurrentBalance)
                .sum();
        double totalAvailable = creditCards.stream()
                .mapToDouble(CreditCard::getAvailableCredit)
                .sum();

        totalCardsLabel.setText(String.format("Карт: %d", cardCount));
        totalBalanceLabel.setText(String.format("Задолженность: %.2f ₽", totalBalance));
        totalCreditLabel.setText(String.format("Доступно: %.2f ₽", totalAvailable));
    }

    // Внутренний класс для таблицы кредитных карт
    private class CreditCardTableModel extends AbstractTableModel {
        private final String[] columnNames = {
                "Номер", "Держатель", "Лимит", "Задолженность", "Доступно", "Срок действия", "Использование"
        };

        @Override
        public int getRowCount() {
            return creditCards.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            CreditCard card = creditCards.get(rowIndex);
            double utilization = (card.getCurrentBalance() / card.getCreditLimit()) * 100;

            return switch (columnIndex) {
                case 0 -> card.getCardNumber();
                case 1 -> card.getHolderName();
                case 2 -> String.format("%.2f ₽", card.getCreditLimit());
                case 3 -> String.format("%.2f ₽", card.getCurrentBalance());
                case 4 -> String.format("%.2f ₽", card.getAvailableCredit());
                case 5 -> DateUtils.formatForDisplay(card.getExpiryDate());
                case 6 -> String.format("%.1f%%", utilization);
                default -> "";
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    }
}