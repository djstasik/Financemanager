package financialmanager.view.components;

import financialmanager.controller.IncomeController;
import financialmanager.model.entities.Income;
import financialmanager.model.managers.CreditCardManager;
import financialmanager.model.enums.IncomeSource;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class IncomePanel extends JPanel {
    private final IncomeController controller;
    private final CreditCardManager cardManager;
    private final IncomeTableModel tableModel;
    private final JTable table;
    private JLabel balanceLabel;

    public IncomePanel(IncomeController controller, CreditCardManager cardManager) {
        this.controller = controller;
        this.cardManager = cardManager;

        setLayout(new BorderLayout(10, 10));

        // Верхняя панель с информацией
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Таблица доходов
        tableModel = new IncomeTableModel();
        table = new JTable(tableModel);
        table.setRowSorter(new TableRowSorter<>(tableModel));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Список доходов"));
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

        JLabel title = new JLabel("Управление доходами", SwingConstants.CENTER);
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

        JButton addButton = new JButton("Добавить доход");
        JButton editButton = new JButton("Редактировать");
        JButton deleteButton = new JButton("Удалить");
        JButton statisticsButton = new JButton("Статистика");
        JButton runButton = new JButton("Запуск программы");

        addButton.addActionListener(e -> addIncome());
        editButton.addActionListener(e -> editIncome());
        deleteButton.addActionListener(e -> deleteIncome());
        statisticsButton.addActionListener(e -> showStatistics());
        runButton.addActionListener(e -> runProgram());

        panel.add(addButton);
        panel.add(editButton);
        panel.add(deleteButton);
        panel.add(statisticsButton);
        panel.add(runButton);

        return panel;
    }

    private void addIncome() {
        IncomeDialog dialog = new IncomeDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                controller,
                cardManager,
                "Добавить новый доход"
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            updateTable();
        }
    }

    private void editIncome() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите доход для редактирования", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);
        Income income = tableModel.getOperationAt(modelRow);

        IncomeDialog dialog = new IncomeDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                controller,
                cardManager,
                income,
                "Редактировать доход"
        );
        dialog.setVisible(true);

        if (dialog.isSaved()) {
            updateTable();
        }
    }

    private void deleteIncome() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите доход для удаления", "Внимание", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Вы уверены, что хотите удалить выбранный доход?",
                "Подтверждение удаления",
                JOptionPane.YES_NO_OPTION
        );

        if (answer == JOptionPane.YES_OPTION) {
            int modelRow = table.convertRowIndexToModel(selectedRow);
            Income income = tableModel.getOperationAt(modelRow);

            try {
                // Если доход привязан к карте, возвращаем средства
                if (income.hasCreditCard()) {
                    String cardId = income.getCreditCardId();
                    var card = cardManager.getCardById(cardId);
                    if (card != null) {
                        card.withdraw(income.getAmount()); // Снимаем доход с карты
                        cardManager.updateCard(card);
                    }
                }

                controller.delete(income.getId());
                tableModel.removeOperation(modelRow);
                updateBalance();
                JOptionPane.showMessageDialog(this, "Доход успешно удален", "Успех", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Ошибка при удалении: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showStatistics() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Map<String, Double> statistics = controller.getStatisticsBySource(startOfMonth, endOfMonth);
        double totalIncomes = controller.getTotalIncomes(startOfMonth, endOfMonth);

        StringBuilder message = new StringBuilder();
        message.append("=== СТАТИСТИКА ДОХОДОВ ЗА ТЕКУЩИЙ МЕСЯЦ ===\n\n");
        message.append("Общие доходы: ").append(String.format("%.2f ₽", totalIncomes)).append("\n\n");
        message.append("Доходы по источникам:\n");

        statistics.forEach((source, amount) -> {
            double percentage = (amount / totalIncomes) * 100;
            message.append(String.format("  %s: %.2f ₽ (%.1f%%)\n", source, amount, percentage));
        });

        JTextArea textArea = new JTextArea(message.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));

        JOptionPane.showMessageDialog(this, scrollPane, "Статистика доходов", JOptionPane.INFORMATION_MESSAGE);
    }

    private void runProgram() {
        controller.RunProgramPotentialIncomes();
        updateTable();
        JOptionPane.showMessageDialog(this, "Программа управления доходами запущена", "Запуск", JOptionPane.INFORMATION_MESSAGE);
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