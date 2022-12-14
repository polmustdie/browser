package com.example.web1;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.google.gson.Gson;
import javafx.util.Pair;
import org.w3c.dom.Document;

public class WebController implements Initializable {
    @FXML
    public TabPane tabPane;
    @FXML
    public Button newTabButton;
    @FXML
    public Hyperlink hlBackward;


//    public Hyperlink hlSearch;
    @FXML
    public Hyperlink hlForward;
    @FXML
    public Hyperlink hlReload;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private TextField textField;
    @FXML
    private MenuButton menuBtn;
//    private WebHistory webHistory;
//    private WebEngine webEngine;

    private final HashMap<Tab, WebView> currTabs = new HashMap<>();
    private final HashMap<Tab, VBox> htmlViewMap = new HashMap<>();

    private final HashSet<String> notHistory = new HashSet<>();
    private Boolean writeHistoryBool = true;
    private SingleSelectionModel<Tab> selectionModel;

    public Gson gson = new Gson();
    public void writeGsonHistory(Date _date, Long _period, String _url){

        try(FileWriter history = new FileWriter("HistoryPage.json", true);
            BufferedWriter bufferedWriter = new BufferedWriter(history);){
            InfoLog info = new InfoLog(_date, _period, _url);
            gson.toJson(info, bufferedWriter);
            bufferedWriter.newLine();
//            bufferedWriter.flush();
        }
        catch (IOException e) {
            System.out.println("Couldn't write history");
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
    }


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectionModel = tabPane.getSelectionModel();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB); //only the selected tab can be closed
        try(FileReader fr = new FileReader("FavoritePages.txt");) {
            BufferedReader reader = new BufferedReader(fr);
            while (reader.ready()) {
                String line = reader.readLine();
                String urlLine = line.substring(0, line.indexOf(' '));
                String titleLine = line.substring(line.indexOf(' ') + 1);
                MenuItem name = new MenuItem(titleLine);
                menuBtn.getItems().add(name);
                name.setOnAction(ev -> {
                    createNewTab(urlLine);
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        createNewTab("https://www.google.ru/");
    }
    //    public void handleBackward(ActionEvent actionEvent) {
//        hlBackward.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                webEngine.executeScript("history.back()");
//            }
//        });
//    }
//
//    public void handleForward(ActionEvent actionEvent) {
//        hlForward.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                webHistory.go(1);
//            }
//        });
//    }
//
//    public void handleRefresh(ActionEvent actionEvent) {
//        hlRefresh.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent actionEvent) {
//                webEngine.reload();
//            }
//        });
//    }

    public void createNewTabAddr(){

        createNewTab(textField.getText());
    }
    public void createNewTabButton() {

        createNewTab("");
    }
    public void historyOnOff(){
        writeHistoryBool = !writeHistoryBool;
        System.out.println(writeHistoryBool);
    }
    public void createNewTabHTML()
    {
        Tab newTab = new Tab("Title");
        newTab.setOnClosed(event -> {
            htmlViewMap.remove(newTab);
            if (htmlViewMap.isEmpty())
            {
                scrollPane.setContent(null);
            }
        });
        WebView newWebView = new WebView();
        WebEngine webEngine = newWebView.getEngine();
        newTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                scrollPane.setContent(htmlViewMap.get(newTab));
                textField.setText("");
            }
        });
        TextArea text_ = new TextArea();
        text_.textProperty().addListener((observable, oldValue, newValue) -> webEngine.loadContent(newValue));
        VBox vbox = new VBox(text_, newWebView);
        htmlViewMap.put(newTab, vbox);
        tabPane.getTabs().add(newTab);
        scrollPane.setContent(vbox);
        selectionModel.select(newTab);
    }
    public void createContextMenu(WebView newWebView, WebEngine webEngine){
        ContextMenu contextMenu = new ContextMenu();
//        MenuItem goBackward = new MenuItem("Go Back");
//        MenuItem goForward = new MenuItem("Go Forward");
//        MenuItem refresh = new MenuItem("Refresh Page");

        MenuItem addToFavourite = new MenuItem("Add Page To Favourites");
        MenuItem saveZIP = new MenuItem("Save Page ZIP");


        MenuItem showHTML = new MenuItem("Show Page HTML");

        MenuItem turnHistoryOff = new MenuItem("Turn History Off For This Page");

//        goBackward.setOnAction(e -> {
//            try{
//                webEngine.executeScript("history.back()");
//            }catch (IndexOutOfBoundsException er) {
//                return;
//            }});
//        goForward.setOnAction(e -> {
//            try{
//                webEngine.getHistory().go(1);
//            }catch (IndexOutOfBoundsException er) {
//                return;
//            }});
//        refresh.setOnAction(e -> newWebView.getEngine().reload());

        addToFavourite.setOnAction(e -> {
            String nameURL = webEngine.getLocation();
            BufferedWriter writer = null;
            try {
                writer = new BufferedWriter(new FileWriter("FavoritePages.txt", true));
                MenuItem name = new MenuItem(webEngine.getTitle());
                menuBtn.getItems().add(name);
                name.setOnAction(ev -> {
                    createNewTab(nameURL);
                });
                writer.write(nameURL);
                writer.write(" ");
                writer.write(webEngine.getTitle());
                writer.write("\n");
                writer.flush();
                writer.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        saveZIP.setOnAction(e -> {
            try {
                FileOutputStream zipFile = new FileOutputStream("/Users/soulfade/Desktop/" + newWebView.getEngine().getTitle() + ".zip");
                ZipOutputStream zip = new ZipOutputStream(zipFile);
                zip.putNextEntry(new ZipEntry("Page.html"));
                InputStream stream = new URL(newWebView.getEngine().getLocation()).openStream();
                stream.transferTo(zip);
                stream.close();
                zip.close();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            } });


        showHTML.setOnAction(e -> {
            TextArea text_ = new TextArea();
//            text_.setText(((String) webEngine.executeScript("document.documentElement.outerHTML")));
            try {
                InputStream stream = new URL(newWebView.getEngine().getLocation()).openStream();
                String result = new BufferedReader(new InputStreamReader(stream))
                        .lines().parallel().collect(Collectors.joining("\n"));
                text_.setText(result);


            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            text_.textProperty().addListener((observable, oldValue, newValue) -> webEngine.loadContent(newValue));

            HBox newHBox = new HBox(text_, newWebView);
            scrollPane.setContent(newHBox);


        });

        turnHistoryOff.setOnAction( event -> {
            notHistory.add(webEngine.getLocation());
            System.out.println(webEngine.getLocation());
        });

//        contextMenu.getItems().addAll(refresh, saveZIP, goBackward, goForward, showHTML, addToFavourite);
        contextMenu.getItems().addAll(saveZIP, showHTML, addToFavourite);
        newWebView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(newWebView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }
    public void createNewTab(String address) {
        Tab newTab = new Tab(address);

        WebView newWebView = new WebView();
        WebEngine webEngine = newWebView.getEngine();
        currTabs.put(newTab, newWebView);
        newWebView.setContextMenuEnabled(false);
        createContextMenu(newWebView, webEngine);
        newTab.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                scrollPane.setContent(currTabs.get(newTab));
                textField.setText(currTabs.get(newTab).getEngine().getLocation());
            }
        });
        webEngine.load(address);
        webEngine.locationProperty().addListener((observable, oldValue, newValue) -> {
            if (writeHistoryBool){
                int indexEntry = webEngine.getHistory().getCurrentIndex();
                List<WebHistory.Entry> my_ = webEngine.getHistory().getEntries();
                if (!notHistory.contains(my_.get(indexEntry).getUrl())) {
                    Date d = new Date();
                    writeGsonHistory(my_.get(indexEntry).getLastVisitedDate(),
                            (d.getTime() - my_.get(indexEntry).getLastVisitedDate().getTime()) / 1000,  my_.get(indexEntry).getUrl());
                    textField.setText(newValue);
                }
            }
        });
        newTab.setOnClosed(event -> {
            int indexEntry = currTabs.get(newTab).getEngine().getHistory().getCurrentIndex();
            List<WebHistory.Entry> my_ = currTabs.get(newTab).getEngine().getHistory().getEntries();
            if (my_.size() == 0) {
                currTabs.remove(newTab);
                if (currTabs.isEmpty())
                {
                    scrollPane.setContent(null);
                }
            }
            else {
                if (!notHistory.contains(my_.get(indexEntry).getUrl()) && writeHistoryBool) {
                    Date d = new Date();
                    writeGsonHistory(my_.get(indexEntry).getLastVisitedDate(),
                            (d.getTime() - my_.get(indexEntry).getLastVisitedDate().getTime()) / 1000, my_.get(indexEntry).getUrl());
                }
                currTabs.remove(newTab);
                if (currTabs.isEmpty()) {
                    scrollPane.setContent(null);
                }
            }
        });
        tabPane.getTabs().add(newTab);
        scrollPane.setContent(newWebView);
        selectionModel.selectLast();
    }
    private String getNameSites(String address) {
        StringBuilder str = new StringBuilder(address.substring(8));
        return str.substring(0, str.indexOf("/"));
    }
    private Pair<Tab, WebEngine> getCurrentWebEngine() {
        for (Map.Entry<Tab, WebView> entry : currTabs.entrySet()) {
            if (entry.getKey().isSelected()) {
                return new Pair<>(entry.getKey(), entry.getValue().getEngine());
            }
        }
        return null;
    }
    public void handleBackward(ActionEvent actionEvent) {
        hlBackward.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    var currentTab = getCurrentWebEngine();
                    assert currentTab != null;
                    currentTab.getValue().getHistory().go(-1);
                    currentTab.getKey().setText(getNameSites(currentTab.getValue().getLocation()));
                } catch (IndexOutOfBoundsException er) {
                    System.out.println(Arrays.toString(er.getStackTrace()));
                }
            }
        });
    }
    public void handleReload(ActionEvent actionEvent) {
        hlReload.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                var currentTab = getCurrentWebEngine();
                assert currentTab != null;
                currentTab.getValue().reload();
            }
        });
    }

    public void handleForward(ActionEvent actionEvent) {
        hlForward.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    var currentTab = getCurrentWebEngine();
                    assert currentTab != null;
                    currentTab.getValue().getHistory().go(1);
                    currentTab.getKey().setText(getNameSites(currentTab.getValue().getLocation()));
                }catch (IndexOutOfBoundsException er) {
                    System.out.println(Arrays.toString(er.getStackTrace()));
                }
            }
        });
    }


}