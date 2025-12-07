package financialmanager.view.frames;

import financialmanager.controller.AnalyticsController;
import financialmanager.controller.ExpenseController;
import financialmanager.controller.IncomeController;
import financialmanager.persistence.JsonDataManager;
import financialmanager.view.components.AnalyticsPanel;
import financialmanager.view.components.CreditCardPanel;
import financialmanager.view.components.ExpensePanel;
import financialmanager.view.components.IncomePanel;

import javax.swing.*;
import java.awt.*;

public class MainApplicationFrame extends JFrame {
    private JTabbedPane tabbedPane;

    public MainApplicationFrame(ExpenseController expenseController,
                                IncomeController incomeController,
                                AnalyticsController analyticsController,
                                JsonDataManager dataManager) {
        initializeUI(expenseController, incomeController, analyticsController, dataManager);
    }

    private void initializeUI(ExpenseController expenseController,
                              IncomeController incomeController,
                              AnalyticsController analyticsController,
                              JsonDataManager dataManager) {
        setTitle("Финансовый менеджер (JSON Persistence)");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        ExpensePanel expensePanel = new ExpensePanel(expenseController);
        IncomePanel incomePanel = new IncomePanel(incomeController);
        AnalyticsPanel analyticsPanel = new AnalyticsPanel(analyticsController,
                expenseController,
                incomeController,
                dataManager);
        CreditCardPanel creditCardPanel = new CreditCardPanel();

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📊 Расходы", expensePanel);
        tabbedPane.addTab("💰 Доходы", incomePanel);
        tabbedPane.addTab("📈 Аналитика", analyticsPanel);
        tabbedPane.addTab("💳 Карты", creditCardPanel);

        add(tabbedPane, BorderLayout.CENTER);

        createMenuBar();
        createStatusBar();

        setVisible(true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Файл");
        JMenuItem exportExpensesItem = new JMenuItem("Экспорт расходов...");
        JMenuItem exportIncomesItem = new JMenuItem("Экспорт доходов...");
        JMenuItem exitItem = new JMenuItem("Выход");

        exportExpensesItem.addActionListener(e -> showExportInfo("расходов"));
        exportIncomesItem.addActionListener(e -> showExportInfo("доходов"));
        exitItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this,
                    "Сохранить данные перед выходом?\n(Данные всегда сохраняются автоматически)",
                    "Выход", JOptionPane.YES_NO_CANCEL_OPTION);

            if (result == JOptionPane.YES_OPTION || result == JOptionPane.NO_OPTION) {
                dispose();
                System.exit(0);
            }
        });

        fileMenu.add(exportExpensesItem);
        fileMenu.add(exportIncomesItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        JMenu helpMenu = new JMenu("Справка");
        JMenuItem aboutItem = new JMenuItem("О программе");
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void showExportInfo(String dataType) {
        JOptionPane.showMessageDialog(this,
                "Экспорт " + dataType + " в JSON доступен во вкладке 'Аналитика'",
                "Экспорт данных", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEtchedBorder());

        JLabel statusLabel = new JLabel(" Данные сохраняются автоматически в JSON");
        JLabel timeLabel = new JLabel(new java.util.Date().toString());

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);

        Timer timer = new Timer(1000, e -> {
            timeLabel.setText(new java.util.Date().toString());
        });
        timer.start();
    }

    private void showAboutDialog() {
        String message = "Финансовый менеджер v1.0\n\n" +
                "Система управления финансами с аналитикой\n" +
                "Принципы ООП и SOLID\n\n" +
                "Особенности:\n" +
                "• Управление расходами и доходами\n" +
                "• Аналитика и отчеты\n" +
                "• Автосохранение в JSON формате\n" +
                "• Категории и типы операций\n" +
                "• Управление кредитными картами\n\n" +
                "Данные сохраняются в папке 'data/'";
        JOptionPane.showMessageDialog(this, message, "О программе", JOptionPane.INFORMATION_MESSAGE);
    }
}