/*
 * MIT License
 *
 * Copyright (c) 2025 Francesco Scotti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package it.unicam.cs.mpgc.jbudget126118.view.JavaFX;

import it.unicam.cs.mpgc.jbudget126118.persistency.TransactionPersistency;
import it.unicam.cs.mpgc.jbudget126118.persistency.XmlPersistency;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.net.MalformedURLException;

/**
 * Main application class for the JBudget application.
 * This class sets up the main window with tabs for adding transactions,
 * viewing transactions, and analyzing the budget.
 * It uses JavaFX for the GUI components.
 */
public class MainApp extends Application {
    private final String xmltransactionfile = "transaction.xml";
    private final String xmltagfile = "tags.xml";
    private final TransactionPersistency persistency = new XmlPersistency(xmltransactionfile, xmltagfile);

    /**
     * The main entry point for the JavaFX application.
     * This method initializes the primary stage with a TabPane containing
     * three tabs: "Aggiungi Transazione", "Visualizza Transazioni",
     * and "Analisi Bilancio". Each tab is associated with a specific pane
     * for handling the respective functionality.
     * @param primaryStage The primary stage for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) throws MalformedURLException {

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Opzionale: impedisce la chiusura delle tab

        Tab addTransactionTab = new Tab("Aggiungi Transazione", new AddTransactionPane(persistency));
        Tab viewTransactionsTab = new Tab("Visualizza Transazioni", new ViewTransactionPane(persistency));
        Tab balanceTab = new Tab("Analisi Bilancio", new BalancePane(persistency));

        tabPane.getTabs().addAll(addTransactionTab, viewTransactionsTab, balanceTab);

        Scene scene = new Scene(tabPane);

        primaryStage.setMaximized(true);

        tabPane.prefHeightProperty().bind(scene.heightProperty());
        tabPane.prefWidthProperty().bind(scene.widthProperty());

        addTransactionTab.setClosable(false);
        viewTransactionsTab.setClosable(false);
        balanceTab.setClosable(false);

        primaryStage.setTitle("Gestione Budget");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}