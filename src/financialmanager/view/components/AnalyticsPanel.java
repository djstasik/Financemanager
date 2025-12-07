package financialmanager.view.components;

import financialmanager.controller.AnalyticsController;
import financialmanager.controller.ExpenseController;
import financialmanager.controller.IncomeController;
import financialmanager.persistence.JsonDataManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.Map;

public class AnalyticsPanel extends JPanel {
    private final AnalyticsController controller;
    private final ExpenseController expenseController;
    private final IncomeController incomeController;
    private final JsonDataManager dataManager;
    private final JTextArea reportArea;

    public AnalyticsPanel(AnalyticsController analyticsController,
                          ExpenseController expenseController,
                          IncomeController incomeController,
                          JsonDataManager dataManager) {
        this.controller = analyticsController;
        this.expenseController = expenseController;
        this.incomeController = incomeController;
        this.dataManager = dataManager;

        setLayout(new BorderLayout(10, 10));

        // Заголовок
        JLabel title = new JLabel("Аналитика и отчеты", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        add(title, BorderLayout.NORTH);

        // Панель кнопок
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.WEST);

        // Область для отчета
        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Отчет"));
        add(scrollPane, BorderLayout.CENTER);

        // Показать начальную информацию
        showGeneralReport();
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton generalReportButton = new JButton("Общий отчет");
        JButton monthlyReportButton = new JButton("Месячный отчет");
        JButton expensesAnalysisButton = new JButton("Анализ расходов");
        JButton incomesAnalysisButton = new JButton("Анализ доходов");
        JButton currentMonthButton = new JButton("Текущий месяц");
        JButton exportExpensesButton = new JButton("Экспорт расходов");
        JButton exportIncomesButton = new JButton("Экспорт доходов");
        JButton refreshButton = new JButton("Обновить");

        generalReportButton.addActionListener(e -> showGeneralReport());
        monthlyReportButton.addActionListener(e -> showMonthlyReport());
        expensesAnalysisButton.addActionListener(e -> showExpensesAnalysis());
        incomesAnalysisButton.addActionListener(e -> showIncomesAnalysis());
        currentMonthButton.addActionListener(e -> showCurrentMonthReport());
        exportExpensesButton.addActionListener(e -> exportExpensesToJson());
        exportIncomesButton.addActionListener(e -> exportIncomesToJson());
        refreshButton.addActionListener(e -> refreshData());

        panel.add(generalReportButton);
        panel.add(monthlyReportButton);
        panel.add(expensesAnalysisButton);
        panel.add(incomesAnalysisButton);
        panel.add(currentMonthButton);
        panel.add(exportExpensesButton);
        panel.add(exportIncomesButton);
        panel.add(refreshButton);

        return panel;
    }

    private void exportExpensesToJson() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Экспорт расходов в JSON");
        fileChooser.setSelectedFile(new File("expenses_export_" + System.currentTimeMillis() + ".json"));

        File currentDir = new File(".");
        fileChooser.setCurrentDirectory(currentDir);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                if (!file.getName().toLowerCase().endsWith(".json")) {
                    file = new File(file.getAbsolutePath() + ".json");
                }

                dataManager.exportExpensesToJson(expenseController.getAll(), file.getAbsolutePath());

                JOptionPane.showMessageDialog(this,
                        "Расходы успешно экспортированы в:\n" + file.getAbsolutePath(),
                        "Экспорт завершен", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при экспорте: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void exportIncomesToJson() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Экспорт доходов в JSON");
        fileChooser.setSelectedFile(new File("incomes_export_" + System.currentTimeMillis() + ".json"));

        File currentDir = new File(".");
        fileChooser.setCurrentDirectory(currentDir);

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                if (!file.getName().toLowerCase().endsWith(".json")) {
                    file = new File(file.getAbsolutePath() + ".json");
                }

                dataManager.exportIncomesToJson(incomeController.getAll(), file.getAbsolutePath());

                JOptionPane.showMessageDialog(this,
                        "Доходы успешно экспортированы в:\n" + file.getAbsolutePath(),
                        "Экспорт завершен", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка при экспорте: " + e.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void showGeneralReport() {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);
        LocalDate endOfYear = LocalDate.of(now.getYear(), 12, 31);

        Map<String, Object> statistics = controller.getFinancialStatistics(startOfYear, endOfYear);

        StringBuilder report = new StringBuilder();
        report.append("=== ОБЩИЙ ОТЧЕТ ЗА ").append(now.getYear()).append(" ГОД ===\n\n");

        statistics.forEach((key, value) -> {
            if (value instanceof Double) {
                report.append(String.format("%-20s: %10.2f ₽\n", key, (Double) value));
            } else {
                report.append(String.format("%-20s: %s\n", key, value));
            }
        });

        reportArea.setText(report.toString());
    }

    private void showMonthlyReport() {
        String monthStr = JOptionPane.showInputDialog(this, "Введите месяц (1-12):",
                "Месячный отчет", JOptionPane.QUESTION_MESSAGE);

        if (monthStr != null && !monthStr.trim().isEmpty()) {
            try {
                int month = Integer.parseInt(monthStr.trim());
                if (month < 1 || month > 12) {
                    JOptionPane.showMessageDialog(this, "Месяц должен быть от 1 до 12",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                LocalDate now = LocalDate.now();
                int year = now.getYear();

                Map<String, Object> report = controller.getMonthlyReport(year, month);

                StringBuilder reportText = new StringBuilder();
                reportText.append("=== ОТЧЕТ ЗА ").append(month).append("/").append(year).append(" ===\n\n");

                report.forEach((key, value) -> {
                    if (value instanceof Double) {
                        reportText.append(String.format("%-20s: %10.2f ₽\n", key, (Double) value));
                    } else {
                        reportText.append(String.format("%-20s: %s\n", key, value));
                    }
                });

                reportArea.setText(reportText.toString());

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Некорректный формат месяца",
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showExpensesAnalysis() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Map<String, Object> analysis = controller.getExpensesAnalytics(startOfMonth, endOfMonth);

        StringBuilder report = new StringBuilder();
        report.append("=== АНАЛИЗ РАСХОДОВ ЗА ТЕКУЩИЙ МЕСЯЦ ===\n\n");

        analysis.forEach((key, value) -> {
            if (value instanceof Map) {
                report.append(key).append(":\n");
                Map<String, Double> map = (Map<String, Double>) value;
                double total = (double) analysis.get("Общие расходы");

                map.forEach((category, amount) -> {
                    double percentage = (amount / total) * 100;
                    report.append(String.format("  %-20s: %8.2f ₽ (%5.1f%%)\n",
                            category, amount, percentage));
                });
                report.append("\n");
            } else if (value instanceof Double) {
                report.append(String.format("%-25s: %10.2f ₽\n", key, (Double) value));
            } else {
                report.append(String.format("%-25s: %s\n", key, value));
            }
        });

        reportArea.setText(report.toString());
    }

    private void showIncomesAnalysis() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = now.withDayOfMonth(1);
        LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());

        Map<String, Object> analysis = controller.getIncomesAnalytics(startOfMonth, endOfMonth);

        StringBuilder report = new StringBuilder();
        report.append("=== АНАЛИЗ ДОХОДОВ ЗА ТЕКУЩИЙ МЕСЯЦ ===\n\n");

        analysis.forEach((key, value) -> {
            if (value instanceof Map) {
                report.append(key).append(":\n");
                Map<String, Double> map = (Map<String, Double>) value;
                double total = (double) analysis.get("Общие доходы");

                map.forEach((source, amount) -> {
                    double percentage = (amount / total) * 100;
                    report.append(String.format("  %-20s: %8.2f ₽ (%5.1f%%)\n",
                            source, amount, percentage));
                });
                report.append("\n");
            } else if (value instanceof Double) {
                report.append(String.format("%-25s: %10.2f ₽\n", key, (Double) value));
            } else {
                report.append(String.format("%-25s: %s\n", key, value));
            }
        });

        reportArea.setText(report.toString());
    }

    private void showCurrentMonthReport() {
        LocalDate now = LocalDate.now();
        Map<String, Object> report = controller.getMonthlyReport(now.getYear(), now.getMonthValue());

        StringBuilder reportText = new StringBuilder();
        reportText.append("=== ОТЧЕТ ЗА ТЕКУЩИЙ МЕСЯЦ ===\n");
        reportText.append("(").append(now.getMonthValue()).append("/").append(now.getYear()).append(")\n\n");

        report.forEach((key, value) -> {
            if (value instanceof Double) {
                reportText.append(String.format("%-20s: %10.2f ₽\n", key, (Double) value));
            } else {
                reportText.append(String.format("%-20s: %s\n", key, value));
            }
        });

        reportArea.setText(reportText.toString());
    }

    private void refreshData() {
        showGeneralReport();
        JOptionPane.showMessageDialog(this, "Данные обновлены", "Обновление",
                JOptionPane.INFORMATION_MESSAGE);
    }
}