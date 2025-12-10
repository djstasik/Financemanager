package financialmanager.view.frames;

import financialmanager.controller.AnalyticsController;
import financialmanager.controller.ExpenseController;
import financialmanager.controller.IncomeController;
import financialmanager.model.managers.CreditCardManager;
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
                                JsonDataManager dataManager,
                                CreditCardManager cardManager) {
        initializeUI(expenseController, incomeController, analyticsController, dataManager, cardManager);
    }

    private void initializeUI(ExpenseController expenseController,
                              IncomeController incomeController,
                              AnalyticsController analyticsController,
                              JsonDataManager dataManager,
                              CreditCardManager cardManager) {
        setTitle("Финансовый менеджер");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Создаем панели с передачей менеджера карт
        ExpensePanel expensePanel = new ExpensePanel(expenseController, cardManager);
        IncomePanel incomePanel = new IncomePanel(incomeController, cardManager);
        AnalyticsPanel analyticsPanel = new AnalyticsPanel(analyticsController,
                expenseController,
                incomeController,
                dataManager);
        CreditCardPanel creditCardPanel = new CreditCardPanel(expenseController, incomeController, cardManager);

        // Создаем вкладки
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("📊 Расходы", expensePanel);
        tabbedPane.addTab("💰 Доходы", incomePanel);
        tabbedPane.addTab("📈 Аналитика", analyticsPanel);
        tabbedPane.addTab("💳 Карты", creditCardPanel);

        add(tabbedPane, BorderLayout.CENTER);

        // Создаем меню
        createMenuBar();

        // Создаем статусную строку
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
                    "Выйти из программы?",
                    "Выход", JOptionPane.YES_NO_OPTION);

            if (result == JOptionPane.YES_OPTION) {
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

        JLabel statusLabel = new JLabel(" Данные сохраняются автоматически");
        JLabel timeLabel = new JLabel(new java.util.Date().toString());

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);

        add(statusBar, BorderLayout.SOUTH);

        // Таймер для обновления времени
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
                "• Управление кредитными картами\n" +
                "• Интеграция карт с операциями\n\n" +
                "Данные сохраняются автоматически";
        JOptionPane.showMessageDialog(this, message, "О программе", JOptionPane.INFORMATION_MESSAGE);
    }
}