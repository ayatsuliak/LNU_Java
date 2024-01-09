import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.tree.TreePath;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import javax.swing.text.JTextComponent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JFormattedTextField;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.List;

public class Main {
    private Connection connection;
    private JTree databaseTree;
    private JTable resultTable;
    private JTextField sqlQueryField;

    public Main() {
        // Ініціалізація підключення до бази даних
        try {
            connection = DatabaseConnector.connect();
            System.out.println("Connected to the PostgreSQL database!");
        } catch (SQLException e) {
            System.err.println("Failed to connect to the PostgreSQL database!");
            e.printStackTrace();
        }

        // Створення графічного інтерфейсу
        JFrame frame = new JFrame("Database Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Створення компонентів
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database"); // Кореневий вузол
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        databaseTree = new JTree(treeModel); // Дерево для відображення структури бази даних
        databaseTree.setPreferredSize(new Dimension(300, databaseTree.getHeight()));
        JScrollPane treeScrollPane = new JScrollPane(databaseTree);
        treeScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        databaseTree.setPreferredSize(new Dimension(250, 1080));
        frame.add(treeScrollPane, BorderLayout.WEST);

        resultTable = new JTable(); // Таблиця для відображення результатів запитів
        JScrollPane tableScrollPane = new JScrollPane(resultTable);
        frame.add(tableScrollPane, BorderLayout.CENTER);

        sqlQueryField = new JTextField(); // Поле для введення SQL-запитів
        frame.add(sqlQueryField, BorderLayout.NORTH);

        JButton executeButton = new JButton("Execute"); // Кнопка для виконання SQL-запитів
        executeButton.addActionListener(e -> executeQuery());
        frame.add(executeButton, BorderLayout.SOUTH);

        DefaultMutableTreeNode functionsNode = new DefaultMutableTreeNode("Functions");
        addFunctionsToTree(functionsNode);
        root.add(functionsNode);

        DefaultMutableTreeNode proceduresNode = new DefaultMutableTreeNode("Procedures");
        addProceduresToTree(proceduresNode);
        root.add(proceduresNode);

        DefaultMutableTreeNode triggerFunctionsNode = new DefaultMutableTreeNode("Trigger Functions");
        addTriggerFunctionsToTree(triggerFunctionsNode);
        root.add(triggerFunctionsNode);

        // Створення меню та діалогових вікон
        JMenuBar menuBar = new JMenuBar();
        JMenu databaseMenu = new JMenu("DataBase");
        JMenuItem connectMenuItem = new JMenuItem("Connect");
        connectMenuItem.addActionListener(e -> showConnectDialog());
        JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
        disconnectMenuItem.addActionListener(e -> disconnectFromDatabase());
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        databaseMenu.add(connectMenuItem);
        databaseMenu.add(disconnectMenuItem);
        databaseMenu.add(exitMenuItem);
        menuBar.add(databaseMenu);
        JMenu tableMenu = new JMenu("Table");
        JMenuItem insertMenuItem = new JMenuItem("Insert");
        insertMenuItem.addActionListener(e -> showInsertDialog());
        JMenuItem editMenuItem = new JMenuItem("Edit");
        editMenuItem.addActionListener(e -> showEditDialog());
        JMenuItem deleteMenuItem = new JMenuItem("Delete");
        deleteMenuItem.addActionListener(e -> showDeleteDialog());
        tableMenu.add(insertMenuItem);
        tableMenu.add(editMenuItem);
        tableMenu.add(deleteMenuItem);
        menuBar.add(tableMenu);
        JMenu searchMenu = new JMenu("Search");
        JMenuItem searchMenuItem = new JMenuItem("Search");
        searchMenuItem.addActionListener(e -> showSearchDialog());
        searchMenu.add(searchMenuItem);
        menuBar.add(searchMenu);
        JMenu helpMenu = new JMenu("Help");
        JMenuItem databaseMetadataMenuItem = new JMenuItem("Database Metadata");
        databaseMetadataMenuItem.addActionListener(e -> showDatabaseMetadataDialog());
        JMenuItem resultSetMetadataMenuItem = new JMenuItem("Resultset Metadata");
        resultSetMetadataMenuItem.addActionListener(e -> showResultSetMetadataDialog());
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(databaseMetadataMenuItem);
        helpMenu.add(resultSetMetadataMenuItem);
        helpMenu.add(aboutMenuItem);
        menuBar.add(helpMenu);

        // Створення контекстного меню та добавлення компонентів
        JPopupMenu contextMenu = new JPopupMenu();
        JMenuItem viewTable = new JMenuItem("View Table");
        viewTable.addActionListener(e -> viewTable());
        JMenuItem insertMenuItemPopup = new JMenuItem("Insert");
        insertMenuItemPopup.addActionListener(e -> showInsertDialog());
        JMenuItem editMenuItemPopup = new JMenuItem("Edit");
        editMenuItemPopup.addActionListener(e -> showEditDialog());
        JMenuItem deleteMenuItemPopup = new JMenuItem("Delete");
        deleteMenuItemPopup.addActionListener(e -> showDeleteDialog());
        contextMenu.add(viewTable);
        contextMenu.add(insertMenuItemPopup);
        contextMenu.add(editMenuItemPopup);
        contextMenu.add(deleteMenuItemPopup);
        JMenuItem searchMenuItemPopup = new JMenuItem("Search");
        searchMenuItemPopup.addActionListener(e -> showSearchDialog());
        contextMenu.add(searchMenuItemPopup);

        frame.setJMenuBar(menuBar);

        // Додавання слухача подій для відображення контекстного меню при правому кліку миші
        databaseTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (SwingUtilities.isRightMouseButton(evt)) {
                    int row = databaseTree.getClosestRowForLocation(evt.getX(), evt.getY());
                    databaseTree.setSelectionRow(row);
                    contextMenu.show(evt.getComponent(), evt.getX(), evt.getY());
                }
            }
        });


        // Налаштування вікна
        frame.setSize(1200, 800);
        frame.setVisible(true);

        // Додавання таблиць до дерева
        DefaultMutableTreeNode tablesNode = new DefaultMutableTreeNode("Tables");
        root.add(tablesNode);
        addTablesToTree(tablesNode);

        // Додавання слухача подій для подвійного кліку на вузли дерева
        databaseTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
                    if (selectedNode != null && selectedNode.isLeaf()) {
                        String nodeName = selectedNode.getUserObject().toString();
                        String parentName = ((DefaultMutableTreeNode) selectedNode.getParent()).getUserObject().toString();
                        if (parentName.equals("Functions") || parentName.equals("Procedures") || parentName.equals("Trigger Functions")) {
                            // Ви отримали подвійний клік на функції або процедури
                            showFunctionOrProcedureOrTriggerCode(nodeName, parentName);
                        }
                    }
                }
            }
        });


        // Додавання слухача подій для відображення колонок вибраної таблиці
        databaseTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
            if (selectedNode != null && selectedNode.isLeaf()) {
                String nodeName = selectedNode.getUserObject().toString();
                // Перевірка, чи вибрано вузол колонки
                if (selectedNode.getParent() != null && selectedNode.getParent().toString().equals("Columns")) {
                    String tableName = selectedNode.getParent().getParent().toString();
                    // Вибір колонки в таблиці
                    System.out.println("Selected column: " + nodeName + " in table: " + tableName);
                } else {
                    // Вибір таблиці
                    showTableColumns(nodeName);
                }
            }
        });
    }
    //Відображення функцій і процедур
    private void showFunctionOrProcedureOrTriggerCode(String name, String type) {
        try {
            String query;
            if (type.equals("Functions")) {
                query = "SELECT pg_get_functiondef((SELECT oid FROM pg_proc WHERE proname = ?))";
            } else if(type.equals("Procedures")){
                query = "SELECT pg_get_functiondef((SELECT oid FROM pg_proc WHERE proname = ? AND prorettype = 'pg_catalog.void'::pg_catalog.regtype))";
            }
            else {
                query =  "SELECT pg_get_triggerdef((SELECT oid FROM pg_trigger WHERE tgname = ?))";
            }

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String code = resultSet.getString(1);
                    String[] codeLines = code.split("\\r?\\n");
                    String[] columns = {"Code"};
                    DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

                    for (String line : codeLines) {
                        tableModel.addRow(new Object[]{line});
                    }

                    resultTable.setModel(tableModel);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час отримання та відображення коду функції або процедури
            String errorMessage = "Помилка під час отримання коду:\n" + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Відображення вмісту таблиці
    private void viewTable() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
        String tableName = selectedNode.getUserObject().toString();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            String[] columns = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }

            DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            resultTable.setModel(tableModel); // Встановити нову модель у JTable
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час отримання та відображення вмісту таблиці
            String errorMessage = "Помилка під час відображення вмісту таблиці:\n" + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showConnectDialog() {
        String databaseURL = JOptionPane.showInputDialog("Enter database URL:");
        String username = JOptionPane.showInputDialog("Enter username:");
        String password = JOptionPane.showInputDialog("Enter password:");

        // Логіка підключення до бази даних з використанням отриманих параметрів
        try {
            connection = DriverManager.getConnection(databaseURL, username, password);
            System.out.println("Connected to the database!");
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database");

            // Додавання вузлів "Functions" і "Procedures" до дерева
            DefaultMutableTreeNode functionsNode = new DefaultMutableTreeNode("Functions");
            root.add(functionsNode);
            addFunctionsToTree(functionsNode);

            DefaultMutableTreeNode proceduresNode = new DefaultMutableTreeNode("Procedures");
            root.add(proceduresNode);
            addProceduresToTree(proceduresNode);

            DefaultMutableTreeNode triggerFunctionsNode = new DefaultMutableTreeNode("Trigger Functions");
            addTriggerFunctionsToTree(triggerFunctionsNode);
            root.add(triggerFunctionsNode);

            // Додавання вузла "Tables" та його дочірніх вузлів до дерева
            DefaultMutableTreeNode tablesNode = new DefaultMutableTreeNode("Tables");
            root.add(tablesNode);
            addTablesToTree(tablesNode);

            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            databaseTree.setModel(treeModel);
        } catch (SQLException e) {
            System.err.println("Failed to connect to the database!");
            e.printStackTrace();
            // Обробка помилок під час підключення до бази даних
            String errorMessage = "Failed to connect to the database:\n" + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void disconnectFromDatabase() {
        try {
            connection.close();
            System.out.println("Disconnected from the PostgreSQL database!");
            // Очищення JTree
            DefaultMutableTreeNode root = new DefaultMutableTreeNode("Database");
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            databaseTree.setModel(treeModel);
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час відключення
            JOptionPane.showMessageDialog(null, "Failed to disconnect from the database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Створює та відображає діалогове вікно для вставки нових даних у вибрану таблицю бази даних
    private void showInsertDialog() {
        JFrame insertFrame = new JFrame("Insert Data");
        insertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        insertFrame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2));
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
        String tableName = selectedNode.toString();

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT column_name, data_type FROM information_schema.columns WHERE table_name = ?")) {
            preparedStatement.setString(1, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            // Приклад обробки типів даних для відповідних стовпчиків
            while (resultSet.next()) {
                String columnName = resultSet.getString("column_name");
                String dataType = resultSet.getString("data_type");

                JLabel label = new JLabel(columnName + ":");
                JComponent inputComponent;

                if ("integer".equals(dataType)) {
                    inputComponent = new JSpinner();
                } else if ("character varying".equals(dataType)) {
                    inputComponent = new JTextField();
                } else if ("date".equals(dataType)) {
                    try {
                        MaskFormatter maskFormatter = new MaskFormatter("####-##-##");
                        JFormattedTextField formattedTextField = new JFormattedTextField(maskFormatter);
                        inputComponent = formattedTextField;
                    } catch (ParseException e) {
                        e.printStackTrace();
                        inputComponent = new JTextField(); // Використовуйте текстове поле, якщо форматування не вдалося
                    }
                } else {
                    inputComponent = new JTextField(); // Інші типи даних, використовуйте текстове поле
                }

                inputPanel.add(label);
                inputPanel.add(inputComponent);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час виконання запиту
        }

        JPanel buttonPanel = new JPanel();
        JButton insertButton = new JButton("Insert");
        insertButton.addActionListener(e -> {
            String[] columnValues = new String[inputPanel.getComponentCount() / 2];
            for (int i = 0; i < inputPanel.getComponentCount(); i += 2) {
                JComponent inputComponent = (JComponent) inputPanel.getComponent(i + 1);
                if (inputComponent instanceof JSpinner) {
                    columnValues[i / 2] = String.valueOf(((JSpinner) inputComponent).getValue());
                } else if (inputComponent instanceof JTextField || inputComponent instanceof JFormattedTextField) {
                    columnValues[i / 2] = ((JTextComponent) inputComponent).getText();
                }
            }

            StringBuilder sqlQueryBuilder = new StringBuilder("INSERT INTO ");
            sqlQueryBuilder.append(tableName).append(" (");

            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?")) {
                preparedStatement.setString(1, tableName);
                ResultSet resultSet = preparedStatement.executeQuery();
                boolean firstColumn = true;
                while (resultSet.next()) {
                    String columnName = resultSet.getString("column_name");
                    if (!firstColumn) {
                        sqlQueryBuilder.append(", ");
                    }
                    sqlQueryBuilder.append(columnName);
                    firstColumn = false;
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Обробка помилок під час виконання запиту
            }

            sqlQueryBuilder.append(") VALUES (");
            for (int i = 0; i < columnValues.length; i++) {
                sqlQueryBuilder.append("?");
                if (i < columnValues.length - 1) {
                    sqlQueryBuilder.append(", ");
                }
            }
            sqlQueryBuilder.append(")");

            try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQueryBuilder.toString())) {
                for (int i = 0; i < columnValues.length; i++) {
                    // Перевірка типу даних і встановлення значення в підготовлену заяву
                    if (columnValues[i].matches("\\d+")) {
                        preparedStatement.setInt(i + 1, Integer.parseInt(columnValues[i]));
                    } else if (columnValues[i].matches("\\d{4}-\\d{2}-\\d{2}")) {
                        // Обробка формату дати (YYYY-MM-DD)
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date parsedDate = dateFormat.parse(columnValues[i]);
                            java.sql.Date sqlDate = new java.sql.Date(parsedDate.getTime());
                            preparedStatement.setDate(i + 1, sqlDate);
                        } catch (ParseException ex) {
                            ex.printStackTrace();
                            // Обробка помилок при некоректному форматі дати
                        }
                    } else if (columnValues[i].matches("\\d+(\\.\\d{1,2})?")) {
                        // Обробка чисел у форматі numeric(10, 2)
                        preparedStatement.setBigDecimal(i + 1, new BigDecimal(columnValues[i]));
                    } else {
                        preparedStatement.setString(i + 1, columnValues[i]);
                    }
                }
                preparedStatement.executeUpdate();
                JOptionPane.showMessageDialog(null, "Дані успішно вставлено до таблиці " + tableName, "Успіх", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Помилка під час вставки даних: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
            insertFrame.dispose();
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> insertFrame.dispose());
        buttonPanel.add(insertButton);
        buttonPanel.add(cancelButton);

        insertFrame.add(inputPanel, BorderLayout.CENTER);
        insertFrame.add(buttonPanel, BorderLayout.SOUTH);
        insertFrame.pack();
        insertFrame.setSize(300, 150);
        insertFrame.setVisible(true);
    }

    private void showEditDialog() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
        if (selectedNode != null && selectedNode.isLeaf()) {
            String columnName = selectedNode.getUserObject().toString();
            String tableName = selectedNode.getParent().getParent().toString();
            String primaryKeyColumnName = getPrimaryKeyColumnName(tableName);

            String primaryKeyValue = JOptionPane.showInputDialog("Enter the " + primaryKeyColumnName + " of the record you want to edit:");
            if (primaryKeyValue != null) {
                String updateQuery = "UPDATE " + tableName + " SET " + columnName + " = ? WHERE " + primaryKeyColumnName + " = CAST(? AS INTEGER)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                    if (columnName.equals(primaryKeyColumnName)) {
                        preparedStatement.setString(1, primaryKeyValue);
                    } else {
                        String newValue = JOptionPane.showInputDialog("Enter the new value for column " + columnName + ":");
                        if (newValue != null) {
                            // Встановлення значення в підготовлену заяву залежно від типу даних стовпця
                            if (newValue.matches("\\d+")) {
                                preparedStatement.setInt(1, Integer.parseInt(newValue));
                            } else if (newValue.matches("\\d{4}-\\d{2}-\\d{2}")) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                Date parsedDate = dateFormat.parse(newValue);
                                preparedStatement.setDate(1, new java.sql.Date(parsedDate.getTime()));
                            } else if (newValue.matches("\\d+(\\.\\d{1,2})?")) {
                                preparedStatement.setBigDecimal(1, new BigDecimal(newValue));
                            } else {
                                preparedStatement.setString(1, newValue);
                            }
                        } else {
                            // Обробка некоректного вводу для нового значення
                            return;
                        }
                    }
                    preparedStatement.setString(2, primaryKeyValue);

                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(null, "Record successfully updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Record with specified primary key not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (SQLException | ParseException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error updating record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                // Обробка некоректного вводу для первинного ключа
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a leaf node (column) to edit.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private String getPrimaryKeyColumnName(String tableName) {
        String primaryKeyColumnName = null;
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT column_name FROM information_schema.constraint_column_usage WHERE table_name = ? AND constraint_name IN (SELECT constraint_name FROM information_schema.table_constraints WHERE table_name = ? AND constraint_type = 'PRIMARY KEY')")) {
            preparedStatement.setString(1, tableName);
            preparedStatement.setString(2, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                primaryKeyColumnName = resultSet.getString("column_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час отримання назви унікального первинного ключа
        }
        return primaryKeyColumnName;
    }


    //Видалення запису з вибраної таблиці
    private void showDeleteDialog() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
        String tableName = selectedNode.toString();
        String primaryKeyColumnName = getPrimaryKeyColumnName(tableName); // Отримання назви первинного ключа таблиці

        if (primaryKeyColumnName != null) {
            String primaryKeyValue = JOptionPane.showInputDialog("Enter the " + primaryKeyColumnName + " to delete:");
            if (primaryKeyValue != null && !primaryKeyValue.isEmpty()) {
                try {
                    // Ось явне перетворення типу даних для порівняння
                    String deleteQuery = "DELETE FROM " + tableName + " WHERE " + primaryKeyColumnName + " = ?";
                    try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                        // Явне перетворення типу даних для параметра запиту
                        preparedStatement.setObject(1, primaryKeyValue, Types.INTEGER);
                        int rowsAffected = preparedStatement.executeUpdate();
                        if (rowsAffected > 0) {
                            JOptionPane.showMessageDialog(null, "Record with " + primaryKeyColumnName + " " + primaryKeyValue + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(null, "No record found with " + primaryKeyColumnName + " " + primaryKeyValue + ".", "Warning", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error deleting record: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Table " + tableName + " does not have a primary key.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSearchDialog() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            String tableName = selectedNode.getUserObject().toString(); // Отримання назви вибраної таблиці
            // Створення діалогового вікна для введення параметрів пошуку
            JDialog searchDialog = new JDialog();
            searchDialog.setTitle("Search in Table: " + tableName);
            searchDialog.setLayout(new GridLayout(3, 2));

            // Додавання текстових полів для введення параметрів пошуку
            JTextField columnNameField = new JTextField();
            JTextField searchValueField = new JTextField();
            searchDialog.add(new JLabel("Column Name:"));
            searchDialog.add(columnNameField);
            searchDialog.add(new JLabel("Search Value:"));
            searchDialog.add(searchValueField);

            // Кнопка для виконання пошуку
            JButton searchButton = new JButton("Search");
            searchButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String columnName = columnNameField.getText();
                    String searchValue = searchValueField.getText();
                    // Виклик функції для виконання пошуку в базі даних
                    performSearch(tableName, columnName, searchValue);
                    // Закриття діалогового вікна після виконання пошуку
                    searchDialog.dispose();
                }
            });

            searchDialog.add(searchButton);
            searchDialog.setSize(300, 150);
            searchDialog.setVisible(true);
        } else {
            // Попередження, якщо користувач не вибрав таблицю для пошуку
            JOptionPane.showMessageDialog(null, "Будь ласка, виберіть таблицю для пошуку.", "Попередження", JOptionPane.WARNING_MESSAGE);
        }
    }
    //Призначена для виконання пошуку в базі даних за вказаними параметрами
    private void performSearch(String tableName, String columnName, String searchValue) {
        // Логіка для виконання пошуку в базі даних за вказаними параметрами
        try {
            String sqlQuery = "SELECT * FROM " + tableName + " WHERE " + columnName + " = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
            preparedStatement.setString(1, searchValue);
            ResultSet resultSet = preparedStatement.executeQuery();

            // Отримання та відображення результатів пошуку в таблиці
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columns = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }

            DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
            while (resultSet.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    rowData[i - 1] = resultSet.getObject(i);
                }
                tableModel.addRow(rowData);
            }

            // Встановлення нової моделі у resultTable для відображення результатів пошуку
            resultTable.setModel(tableModel);
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час виконання запиту пошуку
            String errorMessage = "Помилка під час виконання пошуку:\n" + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showDatabaseMetadataDialog() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            String databaseProductName = metaData.getDatabaseProductName();
            String databaseProductVersion = metaData.getDatabaseProductVersion();
            String driverName = metaData.getDriverName();
            String driverVersion = metaData.getDriverVersion();

            String metadataInfo = "Database Product Name: " + databaseProductName + "\n" +
                    "Database Product Version: " + databaseProductVersion + "\n" +
                    "JDBC Driver Name: " + driverName + "\n" +
                    "JDBC Driver Version: " + driverVersion;

            JOptionPane.showMessageDialog(null, metadataInfo, "Database Metadata", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час отримання метаданих бази даних
            String errorMessage = "Помилка під час отримання метаданих бази даних:\n" + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    //Відображення метаданих результируючого набору
    private void showResultSetMetadataDialog() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) databaseTree.getLastSelectedPathComponent();
        if (selectedNode != null) {
            try {
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM " + selectedNode.getUserObject().toString());
                ResultSetMetaData metaData = resultSet.getMetaData();

                StringBuilder metadataInfo = new StringBuilder();
                metadataInfo.append("Number of Columns: ").append(metaData.getColumnCount()).append("\n");
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    metadataInfo.append("Column ").append(i).append(" Name: ").append(metaData.getColumnName(i)).append("\n");
                    metadataInfo.append("Column ").append(i).append(" Type: ").append(metaData.getColumnTypeName(i)).append("\n");
                    metadataInfo.append("Column ").append(i).append(" Size: ").append(metaData.getColumnDisplaySize(i)).append("\n");
                    metadataInfo.append("---------\n");
                }

                JOptionPane.showMessageDialog(null, metadataInfo.toString(), "Resultset Metadata",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                e.printStackTrace();
                // Обробка помилок під час відображення метаданих результируючого набору
                String errorMessage = "Помилка під час отримання метаданих результируючого набору:\n" + e.getMessage();
                JOptionPane.showMessageDialog(null, errorMessage, "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Будь ласка, виберіть таблицю для перегляду метаданих результируючого набору.",
                    "Попередження", JOptionPane.WARNING_MESSAGE);
        }
    }


    private void showAboutDialog() {
        String authorInfo = "<html><b>Автор:</b> Yatsuliak Andrii<br>" +
                "<b>E-mail:</b> <b>andrii.yatsuliak@lnu.edu.ua</b><br>" +
                "<b>Сайт:</b> <a href='https://github.com/ayatsuliak'>https://github.com/ayatsuliak</a></html>";

        JOptionPane.showMessageDialog(null, authorInfo, "Про програму", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addTablesToTree(DefaultMutableTreeNode root) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT table_name FROM information_schema.tables WHERE table_schema = 'public' AND table_type = 'BASE TABLE'")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableName);
                root.add(tableNode);

                // Додавання колонок як дочірніх вузлів до вузла таблиці
                DefaultMutableTreeNode columnsNode = new DefaultMutableTreeNode("Columns");
                tableNode.add(columnsNode);

                // Отримання та додавання назв колонок як дочірніх вузлів до вузла "Columns"
                addColumnsToTree(columnsNode, tableName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час виконання запиту
        }
    }

    private void addColumnsToTree(DefaultMutableTreeNode columnsNode, String tableName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT column_name FROM information_schema.columns WHERE table_name = ?")) {
            preparedStatement.setString(1, tableName);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String columnName = resultSet.getString("column_name");
                DefaultMutableTreeNode columnNode = new DefaultMutableTreeNode(columnName);
                columnsNode.add(columnNode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час виконання запиту
        }
    }

    private void showTableColumns(String tableName) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + tableName + " LIMIT 0")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            String[] columns = new String[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                columns[i - 1] = metaData.getColumnName(i);
            }

            // Відображення колонок у resultTable
            DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
            resultTable.setModel(tableModel);
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час отримання та відображення колонок таблиці
        }
    }

    private void addFunctionsToTree(DefaultMutableTreeNode functionsNode) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT routine_name FROM information_schema.routines WHERE routine_schema = 'public' AND routine_type = 'FUNCTION'")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String functionName = resultSet.getString("routine_name");
                DefaultMutableTreeNode functionNode = new DefaultMutableTreeNode(functionName);
                functionsNode.add(functionNode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час виконання запиту для отримання функцій
        }
    }

    private void addProceduresToTree(DefaultMutableTreeNode proceduresNode) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT routine_name FROM information_schema.routines WHERE routine_schema = 'public' AND routine_type = 'PROCEDURE'")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String procedureName = resultSet.getString("routine_name");
                DefaultMutableTreeNode procedureNode = new DefaultMutableTreeNode(procedureName);
                proceduresNode.add(procedureNode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час виконання запиту для отримання збережених процедур
        }
    }

    private void addTriggerFunctionsToTree(DefaultMutableTreeNode triggerFunctionsNode) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT tgname FROM pg_trigger WHERE tgrelid IN (SELECT oid FROM pg_class WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public')) AND NOT (tgname LIKE 'RI_ConstraintTrigger_c_%' OR tgname LIKE 'RI_ConstraintTrigger_a_%')"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String triggerFunctionName = resultSet.getString("tgname");
                DefaultMutableTreeNode triggerFunctionNode = new DefaultMutableTreeNode(triggerFunctionName);
                triggerFunctionsNode.add(triggerFunctionNode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час виконання запиту для отримання тригер-функцій
        }
    }


    //Запит в текстовому полі
    private void executeQuery() {
        String sqlQuery = sqlQueryField.getText().trim();
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            boolean isInsertQuery = sqlQuery.toLowerCase().startsWith("insert into");
            boolean hasResults = preparedStatement.execute();

            if (isInsertQuery) {
                if (!hasResults) {
                    String successMessage = "Запит на вставку успішно виконано!";
                    JOptionPane.showMessageDialog(null, successMessage, "Успіх", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    String warningMessage = "Запит на вставку виконано, але повернув результати.";
                    JOptionPane.showMessageDialog(null, warningMessage, "Попередження", JOptionPane.WARNING_MESSAGE);
                }
            } else {
                // Отримання та відображення результатів запиту у таблиці
                ResultSet resultSet = preparedStatement.getResultSet();
                displayQueryResults(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Обробка помилок під час виконання запиту
            String errorMessage = "Помилка під час виконання запиту:\n" + e.getMessage();
            JOptionPane.showMessageDialog(null, errorMessage, "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayQueryResults(ResultSet resultSet) throws SQLException {
        // Отримання метаданих результируючого набору
        ResultSetMetaData metaData = resultSet.getMetaData();

        // Отримання кількості стовпців
        int columnCount = metaData.getColumnCount();
        String[] columns = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columns[i - 1] = metaData.getColumnName(i);
        }

        // Отримання рядків результатів
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        while (resultSet.next()) {
            Object[] rowData = new Object[columnCount];
            for (int i = 1; i <= columnCount; i++) {
                rowData[i - 1] = resultSet.getObject(i);
            }
            // Додавання рядка до таблиці
            tableModel.addRow(rowData);
        }

        // Встановлення нової моделі у resultTable
        resultTable.setModel(tableModel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}
