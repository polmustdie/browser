//package com.example.laba_7;
//
//import javafx.event.ActionEvent;
//import javafx.event.EventHandler;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.*;
//import javafx.scene.input.MouseButton;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebHistory;
//import javafx.scene.web.WebView;
//
//import java.io.*;
//import java.net.URL;
//import java.util.*;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipOutputStream;
//
//import com.google.gson.Gson;
//import javafx.util.Pair;
//
//public class HelloController implements Initializable {
//    @FXML
//    private Hyperlink hlBackward;
//    @FXML
//    private Hyperlink hlForward;
//    @FXML
//    private Hyperlink hlReload;
//    @FXML
//    private TextField tfSearch;
//    @FXML
//    private Hyperlink hlSearch;
//    @FXML
//    private Button addNewTab;
//    @FXML
//    private Button btnFavorites;
//    @FXML
//    private Button btnHTML;
//    @FXML
//    private Button btnOnOffHistory;
//    @FXML
//    private TabPane tabPanel;
//    @FXML
//    private ScrollPane scrollPane;
//
//    private Boolean isWriteHistory = true;
//    private Gson gson = new Gson();
//    private HashMap<Tab, WebView> currentTabs = new HashMap<>();
//    private HashSet<String> existAdressInOpenTabs = new HashSet<>();
//    private SingleSelectionModel<Tab> selectionModel;
//    private HashMap<Tab, HBox> htmlViewMap = new HashMap<>();
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    private void writeHistoryToJSON(String adress, Date date, Long duration) {
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("historyBrowser.json", true))) {
//            gson.toJson((new PageInformation(adress, date, duration)), writer);
//            writer.newLine();
//            writer.flush();
//        } catch (IOException er) {
//            System.out.println("History not recorder");
//            System.out.println(Arrays.toString(er.getStackTrace()));
//        }
//    }
//
//    private void createNewTab(String adress, String title) {
//        Tab newTab = new Tab(title);
//        WebView webView = new WebView();
//        WebEngine webEngine = webView.getEngine();
//
//        currentTabs.put(newTab, webView);
//
//        webView.setContextMenuEnabled(false);
//        createContextMenu(webView, webEngine);
//
//        newTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                scrollPane.setContent(currentTabs.get(newTab));
//                tfSearch.setText(currentTabs.get(newTab).getEngine().getLocation());
//            }
//        });
//        webEngine.load(adress);
//        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
//            if (isWriteHistory) {
//                int indexEntry = webEngine.getHistory().getCurrentIndex();
//                List<WebHistory.Entry> historyThisPage = webEngine.getHistory().getEntries();
//                if (!existAdressInOpenTabs.contains(historyThisPage.get(indexEntry).getUrl())) {
//                    Date d = new Date();
//                    writeHistoryToJSON(historyThisPage.get(indexEntry).getUrl(), historyThisPage.get(indexEntry).getLastVisitedDate(),
//                            (d.getTime() - historyThisPage.get(indexEntry).getLastVisitedDate().getTime()) / 1000);
//                    tfSearch.setText(newValue);
//                }
//            }
//        });
//        newTab.setOnClosed(event -> {
//            int indexEntry = currentTabs.get(newTab).getEngine().getHistory().getCurrentIndex();
//            List<WebHistory.Entry> historyThisPage = currentTabs.get(newTab).getEngine().getHistory().getEntries();
//            if (!existAdressInOpenTabs.contains(historyThisPage.get(indexEntry).getUrl()) && isWriteHistory) {
//                Date d = new Date();
//                writeHistoryToJSON(historyThisPage.get(indexEntry).getUrl(), historyThisPage.get(indexEntry).getLastVisitedDate(),
//                        (d.getTime() - historyThisPage.get(indexEntry).getLastVisitedDate().getTime()) / 1000);
//            }
//            currentTabs.remove(newTab);
//            if (currentTabs.isEmpty())
//            {
//                scrollPane.setContent(null);
//            }
//        });
//        tabPanel.getTabs().add(newTab);
//        scrollPane.setContent(webView);
//        selectionModel.selectLast();
//    }
//    private String getNameSites(String adress) {
//        StringBuilder str = new StringBuilder(adress.substring(8));
//        return str.substring(0, str.indexOf("/"));
//    }
//
//    public void createContextMenu(WebView newWebView, WebEngine webEngine){
//        ContextMenu contextMenu = new ContextMenu();
//        MenuItem savePage = new MenuItem("Save Page");
//        MenuItem showHTML = new MenuItem("ShowHTML");
//        MenuItem historyOff = new MenuItem("History Off");
//        savePage.setOnAction(e -> {
//            try (FileOutputStream zipFile = new FileOutputStream("/home/dasha/Downloads/java/" + newWebView.getEngine().getTitle() + ".zip");
//                 ZipOutputStream zip = new ZipOutputStream(zipFile);
//                 InputStream stream = new URL(newWebView.getEngine().getLocation()).openStream()){
//                zip.putNextEntry(new ZipEntry("Page.html"));
//                stream.transferTo(zip);
//            }
//            catch(Exception ex){
//                System.out.println(ex.getMessage());
//            } });
//
//        historyOff.setOnAction( event -> {
//            existAdressInOpenTabs.add(webEngine.getLocation());
//        });
//
//        showHTML.setOnAction(e -> {
//            TextArea textArea = new TextArea();
//            textArea.setText(((String) webEngine.executeScript("document.documentElement.outerHTML")));
//
//            textArea.textProperty().addListener((observable, oldValue, newValue) -> webEngine.loadContent(newValue));
//
//            HBox newHBox = new HBox(textArea, newWebView);
//            scrollPane.setContent(newHBox);
//        });
//        contextMenu.getItems().addAll(savePage, showHTML, historyOff);
//        newWebView.setOnMousePressed(e -> {
//            if (e.getButton() == MouseButton.SECONDARY) {
//                contextMenu.show(newWebView, e.getScreenX(), e.getScreenY());
//            } else {
//                contextMenu.hide();
//            }
//        });
//
//    }
//    public void createNewTabHTML() {
//        Tab newTab = new Tab("My HTML");
//        newTab.setOnClosed(event -> {
//            htmlViewMap.remove(newTab);
//            if (htmlViewMap.isEmpty())
//            {
//                scrollPane.setContent(null);
//            }
//        });
//        WebView webView = new WebView();
//        WebEngine webEngine = webView.getEngine();
//        newTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                scrollPane.setContent(htmlViewMap.get(newTab));
//                tfSearch.setText("");
//            }
//        });
//        TextArea textArea = new TextArea();
//        textArea.textProperty().addListener((observable, oldValue, newValue) -> webEngine.loadContent(newValue));
//        HBox hbox = new HBox(textArea, webView);
//        htmlViewMap.put(newTab, hbox);
//        tabPanel.getTabs().add(newTab);
//        scrollPane.setContent(hbox);
//        selectionModel.select(newTab);
//    }
//    public void createNewTabButton() {
//        createNewTab("https://www.google.ru/", "New tab");
//    }
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        selectionModel = tabPanel.getSelectionModel();
//        tabPanel.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
//        try (BufferedReader reader = new BufferedReader(new FileReader("favoriteSites.txt"))) {
//            while (reader.ready()) {
//                String urlLine = reader.readLine();
//                String titleLine = getNameSites(urlLine);
//                createNewTab(urlLine, titleLine);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        createNewTab("https://www.google.com/", "New tab");
//    }
//    private Pair<Tab, WebEngine> getCurrentWebEngine() {
//        for (Map.Entry<Tab, WebView> entry : currentTabs.entrySet()) {
//            if (entry.getKey().isSelected()) {
//                return new Pair<>(entry.getKey(), entry.getValue().getEngine());
//            }
//        }
//        return null;
//    }
//    public void handleBackward(ActionEvent actionEvent) {
//        hlBackward.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                try {
//                    var currentTab = getCurrentWebEngine();
//                    assert currentTab != null;
//                    currentTab.getValue().getHistory().go(-1);
//                    currentTab.getKey().setText(getNameSites(currentTab.getValue().getLocation()));
//                } catch (IndexOutOfBoundsException er) {
//                    System.out.println(Arrays.toString(er.getStackTrace()));
//                }
//            }
//        });
//    }
//
//    public void handleForward(ActionEvent actionEvent) {
//        hlForward.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                try{
//                    var currentTab = getCurrentWebEngine();
//                    assert currentTab != null;
//                    currentTab.getValue().getHistory().go(1);
//                    currentTab.getKey().setText(getNameSites(currentTab.getValue().getLocation()));
//                }catch (IndexOutOfBoundsException er) {
//                    System.out.println(Arrays.toString(er.getStackTrace()));
//                }
//            }
//        });
//    }
//
//    public void handleReload(ActionEvent actionEvent) {
//        hlReload.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                var currentTab = getCurrentWebEngine();
//                assert currentTab != null;
//                currentTab.getValue().reload();
//            }
//        });
//    }
//
//    public void handleSearch(ActionEvent actionEvent) {
//        hlSearch.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                var currentTab = getCurrentWebEngine();
//                assert currentTab != null;
//                currentTab.getValue().load(tfSearch.getText());
//                currentTab.getKey().setText(getNameSites(tfSearch.getText()));
//            }
//        });
//        tfSearch.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                var currentTab = getCurrentWebEngine();
//                assert currentTab != null;
//                currentTab.getValue().load(tfSearch.getText());
//                currentTab.getKey().setText(getNameSites(tfSearch.getText()));
//            }
//        });
//    }
//
//    public void turnOnOffHistory(){
//        isWriteHistory = !isWriteHistory;
//    }
//
//    public void addToFavorites(ActionEvent actionEvent) {
//        var currentTab = getCurrentWebEngine();
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter("favoriteSites.txt", true))) {
//            writer.write(currentTab.getValue().getLocation());
//            writer.newLine();
//            writer.flush();
//        } catch (IOException er) {
//            System.out.println("Favorite site not recorder");
//            System.out.println(Arrays.toString(er.getStackTrace()));
//        }
//    }
//}