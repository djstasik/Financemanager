package financialmanager.view.components;

import financialmanager.controller.ExpenseController;
import financialmanager.model.entities.Expense;
import financialmanager.model.managers.CreditCardManager;
import financialmanager.model.enums.ExpenseType;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ExpensePanel extends JPanel {
    private final ExpenseController controller;
    private final CreditCardManager cardManager;
    private final ExpenseTableModel tableModel;
    private final JTable table;
    private JLabel balanceLabel;

    public ExpensePanel(ExpenseController controller, CreditCardManager cardManager) {
        this.controller = controller;
        this.cardManager = cardManager;

        setLayout(new BorderLayout(10, 10));

        // Верхняя панель с информацией
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Таблица расходов
        tableModel = new ExpenseTableModel();
        table = new JTable(tableModel);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Список расходов"));
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Загрузить данные
        updateTable();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Управление расходами", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(title, BorderLayout.NORTH);

        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 5));

        balanceLabel = new JLabel("Общий баланс: загрузка...");
        infoPanel.add(balanceLabel);

        // Кнопка обновления
        JButton refreshButton = new JButton("Обновить");
        refreshButton.addActionListener(e -> updateTable());
        infoPanel.add(refreshButton);

        panel.add(infoPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        JButton addButton = new JButton("Добавить расход");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton statisticsButton = new JButton("Статистика");
        JButton runButton = new JButton("Запуск программы");

        addButton.addActionListener(e -> addExpense());
        editButton.addActionListener(e -> editExpense());
        deleteButton.addActionListener(e -> deleteExpense());
        statisticsButton.addActionListener(e -> showStatistics());
        runButton.addActionListener(e -> runProgram());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(statisticsButton);
        panel.add(runButton);

        return panel;
    }

    private void addExpense() {
        ExpenseDialog dialog = new ExpenseDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                controller,
                cardManager,
                "Добавить новый расход"
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            updateTable();
        }
    }

    private void editExpense() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите расход для редактирования", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Expense expense = tableModel.getOperationAt(modelRow);

        ExpenseDialog dialog = new ExpenseDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                controller,
                cardManager,
                expense,
                "Редактировать расход"
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            updateTable();
        }
    }

    private void deleteExpense() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите расход для удаления", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить выбранный расход?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            Expense expense = tableModel.getOperationAt(modelRow);

            try {
                // Если расход привязан к карте, возвращаем средства
                if (expense.hasCreditCard()) {
                    String cardId = expense.getCreditCardId();
                    var card = cardManager.getCardById(cardId);
                    if (card != null) {
                        card.deposit(Math.abs(expense.getAmount())); // Возвращаем средства на карту
                        cardManager.updateCard(card);
                    }
                }

                controller.delete(expense.getId());
                tableModel.removeOperation(modelRow);
                updateBalance();
                JOptionPane.showMessageDialog(this, "Расход успешно удален", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStatistics() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Map<String, Double> statistics = controller.getStatisticsByCategory(startOfMonth, endOfMonth);
        double totalExpenses = controller.getTotalExpenses(startOfMonth, endOfMonth);

        StringBuilder message = new StringBuilder();
        message.append("=== СТАТИСТИКА РАСХОДОВ ЗА ТЕКУЩИЙ МЕСЯЦ ===\n\n");
        message.append("Общие расходы: ").append(String.format("%.2f ₽", totalExpenses)).append("\n\n");
        message.append("Расходы по категориям:\n");

        statistics.forEach((category, amount) -> {
            double percentage = (amount / totalExpenses) * 100;
            message.append(String.format("  %s: %.2f ₽ (%.1f%%)\n", category, amount, percentage));
        });

        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Статистика расходов", JOptionPane.INFORMATION_MESSAGE);
    }

    private void runProgram() {
        controller.RunProgramPotentialExpenses();
        updateTable();
        JOptionPane.showMessageDialog(this, "Программа управления расходами запущена", "Запуск", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTable() {
        tableModel.updateData(controller.getAll());
        updateBalance();
    }

    private void updateBalance() {
        double balance = controller.getTotalBalance();
        balanceLabel.setText(String.format("Общий баланс: %.2f ₽", balance));
    }
}