package carwashsystem;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;
import java.util.*;
import java.io.*;
import java.awt.Desktop;

// ===============================
// ALL ORIGINAL CLASSES (UNCHANGED)
// ===============================
class Validator {
    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z\\s]+") && !name.trim().isEmpty();
    }
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("\\d{10,15}");
    }
    public static boolean isValidNotEmpty(String val) {
        return val != null && !val.trim().isEmpty();
    }
}

class Customer {
    private String name, phone;
    public Customer(String name, String phone) { this.name = name; this.phone = phone; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
}

class Car {
    private String model, plateNumber;
    public Car(String model, String plateNumber) { this.model = model; this.plateNumber = plateNumber; }
    public String getModel() { return model; }
    public String getPlateNumber() { return plateNumber; }
}

interface WashService {
    String getDescription();
    double getCost();
}

class BasicWash implements WashService {
    public String getDescription() { return "Basic Wash"; }
    public double getCost() { return 50; }
}

class PremiumWash implements WashService {
    public String getDescription() { return "Premium Wash"; }
    public double getCost() { return 100; }
}

class FullWash implements WashService {
    public String getDescription() { return "Full Wash"; }
    public double getCost() { return 150; }
}

class WashFactory {
    public static WashService createWash(int choice) {
        switch (choice) {
            case 1: return new BasicWash();
            case 2: return new PremiumWash();
            case 3: return new FullWash();
            default: return new BasicWash();
        }
    }
}

abstract class WashDecorator implements WashService {
    protected WashService wash;
    public WashDecorator(WashService wash) { this.wash = wash; }
}

class WaxDecorator extends WashDecorator {
    public WaxDecorator(WashService wash) { super(wash); }
    public String getDescription() { return wash.getDescription() + " + Wax"; }
    public double getCost() { return wash.getCost() + 30; }
}

class InteriorDecorator extends WashDecorator {
    public InteriorDecorator(WashService wash) { super(wash); }
    public String getDescription() { return wash.getDescription() + " + Interior Cleaning"; }
    public double getCost() { return wash.getCost() + 40; }
}

interface PaymentStrategy {
    String getLabel();
    void pay(double amount);
}

class CashPayment implements PaymentStrategy {
    public String getLabel() { return "Cash"; }
    public void pay(double amount) { System.out.println("Paid Cash: $" + amount); }
}

class CardPayment implements PaymentStrategy {
    public String getLabel() { return "Card"; }
    public void pay(double amount) { System.out.println("Paid By Card: $" + amount); }
}

class WalletPayment implements PaymentStrategy {
    public String getLabel() { return "Wallet"; }
    public void pay(double amount) { System.out.println("Paid By Wallet: $" + amount); }
}

interface Observer {
    void update(String message);
}

class WashNotifier {
    private List<Observer> observers = new ArrayList<>();
    public void addObserver(Observer o) { observers.add(o); }
    public void notifyAllObservers(String message) { for (Observer o : observers) o.update(message); }
}

class TransactionRecord {
    public String customer, phone, vehicle, plate, service, payment;
    public double cost;
    public long timestamp;
    
    public TransactionRecord(String customer, String phone, String vehicle, String plate,
                           String service, String payment, double cost) {
        this.customer = customer;
        this.phone = phone;
        this.vehicle = vehicle;
        this.plate = plate;
        this.service = service;
        this.payment = payment;
        this.cost = cost;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm");
        return sdf.format(new java.util.Date(timestamp));
    }
}

// ===============================
// JAVAFX MAIN APPLICATION
// ===============================
public class CarWashGUI extends Application {

    // Design tokens
    private static final String BG_DARK       = "#0A0E1A";
    private static final String BG_CARD       = "#111827";
    private static final String BG_CARD2      = "#1A2235";
    private static final String ACCENT_CYAN   = "#00D4FF";
    private static final String ACCENT_BLUE   = "#4F8EF7";
    private static final String ACCENT_GREEN  = "#00E5A0";
    private static final String TEXT_PRIMARY  = "#F0F4FF";
    private static final String TEXT_MUTED    = "#8899BB";
    private static final String ERROR_RED     = "#FF4D6A";
    private static final String BORDER_COLOR  = "#1E3050";

    // State
    private StackPane contentArea;
    private Label stepLabel;
    private HBox stepDots;

    // Data holders
    private TextField tfName, tfPhone, tfModel, tfPlate;
    private ToggleGroup washGroup, payGroup;
    private boolean addWax = false, addInterior = false;
    private ToggleButton btnWax, btnInterior;

    // Transaction history
    private List<TransactionRecord> transactionHistory = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        stage.setTitle("✦ CarWash Pro — Management System");
        stage.setMinWidth(820);
        stage.setMinHeight(680);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        // ── Header ──
        root.setTop(buildHeader());

        // ── Sidebar ──
        root.setLeft(buildSidebar());

        // ── Main content ──
        contentArea = new StackPane();
        contentArea.setPadding(new Insets(30, 40, 30, 40));
        showStep(0);
        root.setCenter(contentArea);

        Scene scene = new Scene(root, 900, 680);
        scene.getStylesheets().add(inlineCSS());
        stage.setScene(scene);
        stage.show();

        // Highlight sidebar
        highlightSidebarRow(0);

        // Fade in
        FadeTransition ft = new FadeTransition(Duration.millis(600), root);
        ft.setFromValue(0); ft.setToValue(1); ft.play();
    }

    // ─────────────────────────────────────────────
    // HEADER
    // ─────────────────────────────────────────────
    private Node buildHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 32, 16, 32));
        header.setSpacing(14);
        header.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 0 0 1 0;"
        );

        // Icon bubble
        Label icon = new Label("🚗");
        icon.setStyle("-fx-font-size: 24px; -fx-background-color: " + ACCENT_CYAN + "22;" +
                      "-fx-background-radius: 10; -fx-padding: 8 12;");

        VBox titleBox = new VBox(2);
        Label title = new Label("CarWash Pro");
        title.setStyle("-fx-font-family: 'Segoe UI Semibold'; -fx-font-size: 20px;" +
                       "-fx-text-fill: " + TEXT_PRIMARY + ";");
        Label sub = new Label("Management System");
        sub.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        titleBox.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Step pill
        stepLabel = new Label("Step 1 of 4");
        stepLabel.setStyle(
            "-fx-font-size: 12px; -fx-text-fill: " + ACCENT_CYAN + ";" +
            "-fx-background-color: " + ACCENT_CYAN + "22;" +
            "-fx-background-radius: 20; -fx-padding: 5 14;"
        );

        // Dot indicators
        stepDots = new HBox(6);
        stepDots.setAlignment(Pos.CENTER);
        for (int i = 0; i < 4; i++) {
            Circle dot = new Circle(i == 0 ? 6 : 4);
            dot.setFill(Color.web(i == 0 ? ACCENT_CYAN : BORDER_COLOR));
            dot.setId("dot" + i);
            stepDots.getChildren().add(dot);
        }

        // History button
        Button historyBtn = new Button("📜 History");
        historyBtn.setStyle(
            "-fx-background-color: " + ACCENT_BLUE + "33;" +
            "-fx-text-fill: " + ACCENT_BLUE + ";" +
            "-fx-border-color: " + ACCENT_BLUE + ";" +
            "-fx-border-width: 1; -fx-border-radius: 8;" +
            "-fx-background-radius: 8; -fx-padding: 6 14;" +
            "-fx-font-size: 11px; -fx-cursor: hand;"
        );
        historyBtn.setOnAction(e -> showHistoryDialog());

        header.getChildren().addAll(icon, titleBox, spacer, stepLabel, stepDots, historyBtn);
        return header;
    }

    // ─────────────────────────────────────────────
    // SIDEBAR
    // ─────────────────────────────────────────────
    private Node buildSidebar() {
        VBox sidebar = new VBox(4);
        sidebar.setPrefWidth(190);
        sidebar.setPadding(new Insets(28, 12, 28, 12));
        sidebar.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 0 1 0 0;"
        );

        String[][] steps = {
            {"👤", "Customer Info",   "Name & phone"},
            {"🚘", "Vehicle Details", "Model & plate"},
            {"🧽", "Wash Service",    "Type & add-ons"},
            {"💳", "Payment",         "Method & confirm"}
        };

        for (int i = 0; i < steps.length; i++) {
            final int idx = i;
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle("-fx-background-radius: 10; -fx-cursor: hand;");
            row.setId("siderow" + i);

            Label ico = new Label(steps[i][0]);
            ico.setStyle("-fx-font-size: 16px;");

            VBox txt = new VBox(1);
            Label nm = new Label(steps[i][1]);
            nm.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_PRIMARY + ";");
            Label desc = new Label(steps[i][2]);
            desc.setStyle("-fx-font-size: 10px; -fx-text-fill: " + TEXT_MUTED + ";");
            txt.getChildren().addAll(nm, desc);

            row.getChildren().addAll(ico, txt);
            row.setOnMouseClicked(e -> navigateTo(idx));
            sidebar.getChildren().add(row);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sidebar.getChildren().add(spacer);
        return sidebar;
    }

    private void highlightSidebarRow(int active) {
        for (int i = 0; i < 4; i++) {
            Node n = contentArea.getScene() == null ? null :
                     contentArea.getScene().lookup("#siderow" + i);
            if (n instanceof HBox r) {
                r.setStyle("-fx-background-radius: 10; -fx-cursor: hand;" +
                    (i == active
                        ? "-fx-background-color: " + ACCENT_CYAN + "22;"
                        : "-fx-background-color: transparent;"));
            }
        }
    }

    // ─────────────────────────────────────────────
    // STEP NAVIGATION
    // ─────────────────────────────────────────────
    private void showStep(int step) {
        updateStepIndicators(step);

        Node page = switch (step) {
            case 0 -> buildCustomerPage();
            case 1 -> buildCarPage();
            case 2 -> buildWashPage();
            case 3 -> buildPaymentPage();
            default -> buildCustomerPage();
        };

        // Animate transition
        page.setOpacity(0);
        page.setTranslateX(30);
        contentArea.getChildren().setAll(page);

        FadeTransition fade = new FadeTransition(Duration.millis(300), page);
        fade.setToValue(1);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(300), page);
        translate.setToX(0);

        ParallelTransition pt = new ParallelTransition(fade, translate);
        pt.play();
    }

    private void navigateTo(int idx) {
        showStep(idx);
        // Sidebar highlight requires scene to be set
        javafx.application.Platform.runLater(() -> highlightSidebarRow(idx));
    }

    private void updateStepIndicators(int step) {
        if (stepLabel != null)
            stepLabel.setText("Step " + (step + 1) + " of 4");
        if (stepDots != null) {
            for (int i = 0; i < stepDots.getChildren().size(); i++) {
                Circle dot = (Circle) stepDots.getChildren().get(i);
                dot.setRadius(i == step ? 6 : 4);
                dot.setFill(Color.web(i <= step ? ACCENT_CYAN : BORDER_COLOR));
            }
        }
    }

    // ─────────────────────────────────────────────
    // PAGE 1 — CUSTOMER INFO
    // ─────────────────────────────────────────────
    private Node buildCustomerPage() {
        VBox page = new VBox(24);
        page.setAlignment(Pos.CENTER);
        page.setMaxWidth(520);

        page.getChildren().add(pageHeader("👤", "Customer Information",
                "Enter the customer's personal details to get started."));

        // Form card
        VBox card = card();
        tfName  = styledField("Full Name",     "e.g. Ahmed Hassan");
        tfPhone = styledField("Phone Number",  "e.g. 01012345678");
        Label nameErr  = errLabel();
        Label phoneErr = errLabel();

        card.getChildren().addAll(
            fieldGroup("Full Name", tfName, nameErr),
            fieldGroup("Phone Number", tfPhone, phoneErr)
        );

        Button next = nextBtn("Next → Vehicle Details");
        next.setOnAction(e -> {
            boolean ok = true;
            if (!Validator.isValidName(tfName.getText())) {
                nameErr.setText("⚠ Letters and spaces only.");
                tfName.setStyle(tfName.getStyle() + "-fx-border-color: " + ERROR_RED + ";");
                ok = false;
            } else { nameErr.setText(""); resetFieldStyle(tfName); }

            if (!Validator.isValidPhone(tfPhone.getText())) {
                phoneErr.setText("⚠ 10–15 digits required.");
                tfPhone.setStyle(tfPhone.getStyle() + "-fx-border-color: " + ERROR_RED + ";");
                ok = false;
            } else { phoneErr.setText(""); resetFieldStyle(tfPhone); }

            if (ok) showStep(1);
        });

        page.getChildren().addAll(card, next);
        return centeredScroll(page);
    }

    // ─────────────────────────────────────────────
    // PAGE 2 — VEHICLE DETAILS
    // ─────────────────────────────────────────────
    private Node buildCarPage() {
        VBox page = new VBox(24);
        page.setAlignment(Pos.CENTER);
        page.setMaxWidth(520);

        page.getChildren().add(pageHeader("🚘", "Vehicle Details",
                "Tell us about the car being washed today."));

        VBox card = card();
        tfModel = styledField("Car Model", "e.g. Toyota Corolla");
        tfPlate = styledField("Plate Number", "e.g. ABC-1234");
        Label modelErr = errLabel(), plateErr = errLabel();

        card.getChildren().addAll(
            fieldGroup("Car Model", tfModel, modelErr),
            fieldGroup("Plate Number", tfPlate, plateErr)
        );

        HBox nav = navRow(
            backBtn("← Back", () -> showStep(0)),
            nextBtn("Next → Wash Service", () -> {
                boolean ok = true;
                if (!Validator.isValidNotEmpty(tfModel.getText())) {
                    modelErr.setText("⚠ Model cannot be empty.");
                    ok = false;
                } else modelErr.setText("");
                if (!Validator.isValidNotEmpty(tfPlate.getText())) {
                    plateErr.setText("⚠ Plate cannot be empty.");
                    ok = false;
                } else plateErr.setText("");
                if (ok) showStep(2);
            })
        );

        page.getChildren().addAll(card, nav);
        return centeredScroll(page);
    }

    // ─────────────────────────────────────────────
    // PAGE 3 — WASH SERVICE
    // ─────────────────────────────────────────────
    private Node buildWashPage() {
        VBox page = new VBox(24);
        page.setAlignment(Pos.CENTER);
        page.setMaxWidth(560);

        page.getChildren().add(pageHeader("🧽", "Wash Service",
                "Choose your wash type and optional add-ons."));

        // Wash type cards
        washGroup = new ToggleGroup();
        HBox washCards = new HBox(12);
        washCards.setAlignment(Pos.CENTER);

        String[][] washOpts = {
            {"1", "Basic\nWash",   "$50",  "Exterior rinse\n+ hand dry"},
            {"2", "Premium\nWash", "$100", "Foam wash\n+ tire clean"},
            {"3", "Full\nWash",    "$150", "Full detail\n+ polish"}
        };

        for (String[] opt : washOpts) {
            ToggleButton tb = serviceCard(opt[0], opt[1], opt[2], opt[3], washGroup);
            washCards.getChildren().add(tb);
        }
        ((ToggleButton)washCards.getChildren().get(0)).setSelected(true);

        // Add-ons
        Label addonsTitle = sectionLabel("Optional Add-ons");
        HBox addons = new HBox(12);
        addons.setAlignment(Pos.CENTER);

        btnWax = addonBtn("✨ Wax Coating", "+$30");
        btnInterior = addonBtn("🪑 Interior Clean", "+$40");

        btnWax.setOnAction(e -> addWax = btnWax.isSelected());
        btnInterior.setOnAction(e -> addInterior = btnInterior.isSelected());
        addons.getChildren().addAll(btnWax, btnInterior);

        HBox nav = navRow(
            backBtn("← Back", () -> showStep(1)),
            nextBtn("Next → Payment", () -> showStep(3))
        );

        page.getChildren().addAll(washCards, addonsTitle, addons, nav);
        return centeredScroll(page);
    }

    // ─────────────────────────────────────────────
    // PAGE 4 — PAYMENT & CONFIRM
    // ─────────────────────────────────────────────
    private Node buildPaymentPage() {
        VBox page = new VBox(20);
        page.setAlignment(Pos.CENTER);
        page.setMaxWidth(560);

        page.getChildren().add(pageHeader("💳", "Payment & Confirmation",
                "Review your order and choose how to pay."));

        // Order summary card
        VBox summary = card();
        summary.setStyle(summary.getStyle() +
            "-fx-border-color: " + ACCENT_CYAN + "44; -fx-border-width: 1; -fx-border-radius: 14;");

        Label summTitle = new Label("Order Summary");
        summTitle.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;" +
                           "-fx-text-fill: " + ACCENT_CYAN + ";");

        // Compute service
        int washChoice = washGroup == null ? 1 :
            (washGroup.getSelectedToggle() == null ? 1 :
             Integer.parseInt(((ToggleButton)washGroup.getSelectedToggle()).getUserData().toString()));

        WashService service = WashFactory.createWash(washChoice);
        if (addWax) service = new WaxDecorator(service);
        if (addInterior) service = new InteriorDecorator(service);

        String custName  = tfName  != null ? tfName.getText()  : "-";
        String custPhone = tfPhone != null ? tfPhone.getText()  : "-";
        String carModel  = tfModel != null ? tfModel.getText()  : "-";
        String carPlate  = tfPlate != null ? tfPlate.getText()  : "-";

        VBox rows = new VBox(8);
        rows.getChildren().addAll(
            summaryRow("👤 Customer",  custName + " · " + custPhone),
            summaryRow("🚘 Vehicle",   carModel + " · " + carPlate),
            summaryRow("🧽 Service",   service.getDescription()),
            divider(),
            summaryRowBig("💰 Total",  String.format("$%.0f", service.getCost()))
        );

        summary.getChildren().addAll(summTitle, rows);

        // Payment method
        payGroup = new ToggleGroup();
        Label payTitle = sectionLabel("Payment Method");
        HBox payOptions = new HBox(12);
        payOptions.setAlignment(Pos.CENTER);

        String[][] payOpts = {{"1","💵","Cash"},{"2","💳","Card"},{"3","📱","Wallet"}};
        for (String[] opt : payOpts) {
            ToggleButton tb = payBtn(opt[0], opt[1], opt[2], payGroup);
            payOptions.getChildren().add(tb);
        }
        ((ToggleButton)payOptions.getChildren().get(0)).setSelected(true);

        final WashService finalService = service;

        Button confirm = new Button("✅  Confirm & Process Order");
        confirm.setStyle(
            "-fx-background-color: linear-gradient(to right, " + ACCENT_GREEN + ", " + ACCENT_CYAN + ");" +
            "-fx-text-fill: #001A22; -fx-font-weight: bold; -fx-font-size: 14px;" +
            "-fx-background-radius: 12; -fx-padding: 14 32; -fx-cursor: hand;"
        );
        confirm.setOnMouseEntered(e -> confirm.setStyle(confirm.getStyle() + "-fx-opacity: 0.88;"));
        confirm.setOnMouseExited(e -> confirm.setStyle(confirm.getStyle().replace("-fx-opacity: 0.88;","")));

        confirm.setOnAction(e -> processOrder(custName, custPhone, carModel, carPlate,
                finalService, payGroup));

        HBox nav = navRow(backBtn("← Back", () -> showStep(2)), confirm);

        page.getChildren().addAll(summary, payTitle, payOptions, nav);
        return centeredScroll(page);
    }

    // ─────────────────────────────────────────────
    // ORDER PROCESSING
    // ─────────────────────────────────────────────
    private void processOrder(String custName, String custPhone,
                              String carModel, String carPlate,
                              WashService service, ToggleGroup payGroup) {

        Customer customer = new Customer(custName, custPhone);
        Car car = new Car(carModel, carPlate);

        int payChoice = payGroup.getSelectedToggle() == null ? 1 :
            Integer.parseInt(((ToggleButton)payGroup.getSelectedToggle()).getUserData().toString());

        PaymentStrategy payment = switch (payChoice) {
            case 2 -> new CardPayment();
            case 3 -> new WalletPayment();
            default -> new CashPayment();
        };

        WashNotifier notifier = new WashNotifier();
        List<String> logs = new ArrayList<>();
        notifier.addObserver(msg -> logs.add("📧 EMAIL: " + msg));
        notifier.addObserver(msg -> logs.add("📱 SMS: " + msg));
        notifier.notifyAllObservers("Car wash completed for " + customer.getName());

        payment.pay(service.getCost());
        
        // Add to transaction history
        TransactionRecord record = new TransactionRecord(
            customer.getName(), customer.getPhone(),
            car.getModel(), car.getPlateNumber(),
            service.getDescription(), payment.getLabel(),
            service.getCost()
        );
        transactionHistory.add(record);
        
        showReceiptDialog(customer, car, service, payment, logs);
    }

    private void showReceiptDialog(Customer customer, Car car,
                                   WashService service, PaymentStrategy payment,
                                   List<String> logs) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Order Confirmation");
        dialog.setResizable(false);

        VBox root = new VBox(18);
        root.setPadding(new Insets(32));
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: " + BG_DARK + ";");
        root.setPrefWidth(420);

        // Success icon animation
        Label checkIcon = new Label("✅");
        checkIcon.setStyle("-fx-font-size: 52px;");
        ScaleTransition st = new ScaleTransition(Duration.millis(500), checkIcon);
        st.setFromX(0); st.setFromY(0); st.setToX(1); st.setToY(1);
        st.setInterpolator(Interpolator.EASE_OUT);

        Label title = new Label("Order Processed!");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; " +
                       "-fx-text-fill: " + ACCENT_GREEN + ";");

        VBox details = new VBox(8);
        details.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12;" +
                         "-fx-padding: 16;");
        details.getChildren().addAll(
            receiptRow("Customer",  customer.getName()),
            receiptRow("Phone",     customer.getPhone()),
            receiptRow("Vehicle",   car.getModel() + " · " + car.getPlateNumber()),
            receiptRow("Service",   service.getDescription()),
            receiptRow("Payment",   payment.getLabel()),
            receiptRowBold("Total", String.format("$%.0f", service.getCost()))
        );

        // Notifications
        VBox notifBox = new VBox(6);
        notifBox.setStyle("-fx-background-color: " + BG_CARD2 + "; -fx-background-radius: 10;" +
                          "-fx-padding: 12;");
        for (String log : logs) {
            Label l = new Label(log);
            l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ACCENT_CYAN + ";");
            notifBox.getChildren().add(l);

            // Also update sidebar log

        }

        Button close = new Button("Close");
        close.setStyle(
            "-fx-background-color: " + ACCENT_CYAN + "; -fx-text-fill: #001A22;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 30; -fx-cursor: hand;"
        );
        close.setOnAction(e -> { dialog.close(); showStep(0); resetForm(); });

        root.getChildren().addAll(checkIcon, title, details, notifBox, close);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(inlineCSS());
        dialog.setScene(scene);
        dialog.show();
        st.play();
    }

    private void resetForm() {
        if (tfName != null)  tfName.clear();
        if (tfPhone != null) tfPhone.clear();
        if (tfModel != null) tfModel.clear();
        if (tfPlate != null) tfPlate.clear();
        addWax = false; addInterior = false;
    }

    private void showHistoryDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Transaction History");
        dialog.setResizable(true);
        dialog.setWidth(850);
        dialog.setHeight(600);

        VBox root = new VBox(16);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        // Header
        Label title = new Label("📜 Transaction History");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_CYAN + ";");

        // Info label
        Label infoLabel = new Label(transactionHistory.isEmpty() ? 
            "No transactions yet." : 
            "Total transactions: " + transactionHistory.size());
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        // History table
        VBox historyContent = new VBox(8);
        historyContent.setStyle("-fx-background-color: " + BG_CARD + "; -fx-background-radius: 12; -fx-padding: 12;");

        if (transactionHistory.isEmpty()) {
            Label emptyMsg = new Label("No wash transactions recorded yet.");
            emptyMsg.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + "; -fx-padding: 30;");
            emptyMsg.setAlignment(Pos.CENTER);
            historyContent.getChildren().add(emptyMsg);
        } else {
            // Display transactions in reverse order (newest first)
            for (int i = transactionHistory.size() - 1; i >= 0; i--) {
                TransactionRecord record = transactionHistory.get(i);
                historyContent.getChildren().add(createHistoryCard(record));
            }
        }

        ScrollPane scrollPane = new ScrollPane(historyContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        // Buttons
        Button closeBtn = new Button("Close");
        closeBtn.setStyle(
            "-fx-background-color: " + ACCENT_CYAN + "; -fx-text-fill: #001A22;" +
            "-fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 10 30; -fx-cursor: hand;"
        );
        closeBtn.setOnAction(e -> dialog.close());

        Button exportBtn = new Button("� Export as PDF");
        exportBtn.setStyle(
            "-fx-background-color: " + ACCENT_BLUE + "33; -fx-text-fill: " + ACCENT_BLUE + ";" +
            "-fx-border-color: " + ACCENT_BLUE + "; -fx-border-width: 1;" +
            "-fx-background-radius: 8; -fx-padding: 10 20; -fx-cursor: hand;"
        );
        exportBtn.setOnAction(e -> exportHistoryAsPDF());

        HBox btnBox = new HBox(12);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        btnBox.getChildren().addAll(exportBtn, closeBtn);

        root.getChildren().addAll(title, infoLabel, scrollPane, btnBox);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(inlineCSS());
        dialog.setScene(scene);
        dialog.show();
    }

    private VBox createHistoryCard(TransactionRecord record) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setStyle(
            "-fx-background-color: " + BG_CARD2 + ";" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-width: 1; -fx-border-radius: 10;"
        );

        // Top row - date and cost
        HBox topRow = new HBox();
        Label dateLabel = new Label(record.getFormattedDate());
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label costLabel = new Label("$" + String.format("%.0f", record.cost));
        costLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_GREEN + ";");
        
        topRow.getChildren().addAll(dateLabel, spacer, costLabel);

        // Customer info row
        HBox customerRow = new HBox(16);
        Label customerInfo = new Label("👤 " + record.customer + " · " + record.phone);
        customerInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        
        Label paymentInfo = new Label("💳 " + record.payment);
        paymentInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ACCENT_BLUE + ";");
        
        customerRow.getChildren().addAll(customerInfo, paymentInfo);

        // Vehicle info row
        Label vehicleInfo = new Label("🚘 " + record.vehicle + " · " + record.plate);
        vehicleInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");

        // Service row
        Label serviceInfo = new Label("🧽 " + record.service);
        serviceInfo.setStyle("-fx-font-size: 12px; -fx-text-fill: " + ACCENT_CYAN + "; -fx-font-weight: bold;");

        card.getChildren().addAll(topRow, customerRow, vehicleInfo, serviceInfo);
        return card;
    }

    private void exportHistoryAsPDF() {
        if (transactionHistory.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No History");
            alert.setHeaderText("No transactions to export");
            alert.setContentText("There are no wash transactions to export yet.");
            alert.showAndWait();
            return;
        }

        try {
            double totalRevenue = 0;
            for (TransactionRecord record : transactionHistory) {
                totalRevenue += record.cost;
            }

            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n");
            html.append("<html lang=\"en\">\n");
            html.append("<head>\n");
            html.append("    <meta charset=\"UTF-8\">\n");
            html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            html.append("    <title>CarWash Pro - Transaction History Report</title>\n");
            html.append("    <style>\n");
            html.append("        * { margin: 0; padding: 0; box-sizing: border-box; }\n");
            html.append("        body {\n");
            html.append("            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\n");
            html.append("            background: #f5f5f5;\n");
            html.append("            padding: 20px;\n");
            html.append("        }\n");
            html.append("        .container {\n");
            html.append("            max-width: 900px;\n");
            html.append("            margin: 0 auto;\n");
            html.append("            background: white;\n");
            html.append("            border-radius: 12px;\n");
            html.append("            box-shadow: 0 4px 20px rgba(0,0,0,0.1);\n");
            html.append("            overflow: hidden;\n");
            html.append("        }\n");
            html.append("        .header {\n");
            html.append("            background: linear-gradient(135deg, #00D4FF 0%, #4F8EF7 100%);\n");
            html.append("            color: white;\n");
            html.append("            padding: 40px;\n");
            html.append("            text-align: center;\n");
            html.append("        }\n");
            html.append("        .header h1 {\n");
            html.append("            font-size: 32px;\n");
            html.append("            margin-bottom: 10px;\n");
            html.append("        }\n");
            html.append("        .header p {\n");
            html.append("            font-size: 14px;\n");
            html.append("            opacity: 0.9;\n");
            html.append("        }\n");
            html.append("        .content {\n");
            html.append("            padding: 40px;\n");
            html.append("        }\n");
            html.append("        .summary {\n");
            html.append("            display: grid;\n");
            html.append("            grid-template-columns: 1fr 1fr;\n");
            html.append("            gap: 20px;\n");
            html.append("            margin-bottom: 40px;\n");
            html.append("        }\n");
            html.append("        .summary-card {\n");
            html.append("            background: linear-gradient(135deg, #111827 0%, #1A2235 100%);\n");
            html.append("            color: white;\n");
            html.append("            padding: 20px;\n");
            html.append("            border-radius: 10px;\n");
            html.append("            text-align: center;\n");
            html.append("        }\n");
            html.append("        .summary-card h3 {\n");
            html.append("            font-size: 14px;\n");
            html.append("            opacity: 0.8;\n");
            html.append("            margin-bottom: 10px;\n");
            html.append("        }\n");
            html.append("        .summary-card .value {\n");
            html.append("            font-size: 28px;\n");
            html.append("            font-weight: bold;\n");
            html.append("            color: #00E5A0;\n");
            html.append("        }\n");
            html.append("        .transactions h2 {\n");
            html.append("            color: #00D4FF;\n");
            html.append("            font-size: 18px;\n");
            html.append("            margin-bottom: 20px;\n");
            html.append("            border-bottom: 2px solid #00D4FF;\n");
            html.append("            padding-bottom: 10px;\n");
            html.append("        }\n");
            html.append("        .transaction {\n");
            html.append("            background: #f9f9f9;\n");
            html.append("            border: 1px solid #e0e0e0;\n");
            html.append("            border-radius: 8px;\n");
            html.append("            padding: 15px;\n");
            html.append("            margin-bottom: 15px;\n");
            html.append("            page-break-inside: avoid;\n");
            html.append("        }\n");
            html.append("        .transaction-header {\n");
            html.append("            display: flex;\n");
            html.append("            justify-content: space-between;\n");
            html.append("            margin-bottom: 10px;\n");
            html.append("            border-bottom: 1px solid #e0e0e0;\n");
            html.append("            padding-bottom: 10px;\n");
            html.append("        }\n");
            html.append("        .transaction-date {\n");
            html.append("            font-size: 12px;\n");
            html.append("            color: #666;\n");
            html.append("        }\n");
            html.append("        .transaction-amount {\n");
            html.append("            font-size: 16px;\n");
            html.append("            font-weight: bold;\n");
            html.append("            color: #00E5A0;\n");
            html.append("        }\n");
            html.append("        .transaction-detail {\n");
            html.append("            display: grid;\n");
            html.append("            grid-template-columns: 1fr 1fr;\n");
            html.append("            gap: 10px;\n");
            html.append("            font-size: 13px;\n");
            html.append("        }\n");
            html.append("        .detail-item {\n");
            html.append("            color: #333;\n");
            html.append("        }\n");
            html.append("        .detail-label {\n");
            html.append("            color: #666;\n");
            html.append("            font-size: 11px;\n");
            html.append("            margin-bottom: 2px;\n");
            html.append("        }\n");
            html.append("        .footer {\n");
            html.append("            background: #f0f0f0;\n");
            html.append("            padding: 20px 40px;\n");
            html.append("            text-align: center;\n");
            html.append("            font-size: 12px;\n");
            html.append("            color: #666;\n");
            html.append("        }\n");
            html.append("        @media print {\n");
            html.append("            body { background: white; }\n");
            html.append("            .container { box-shadow: none; }\n");
            html.append("        }\n");
            html.append("    </style>\n");
            html.append("</head>\n");
            html.append("<body>\n");
            html.append("    <div class=\"container\">\n");
            html.append("        <div class=\"header\">\n");
            html.append("            <h1>🚗 CarWash Pro</h1>\n");
            html.append("            <p>Transaction History Report</p>\n");
            html.append("        </div>\n");
            html.append("\n");
            html.append("        <div class=\"content\">\n");
            html.append("            <div class=\"summary\">\n");
            html.append("                <div class=\"summary-card\">\n");
            html.append("                    <h3>Total Transactions</h3>\n");
            html.append("                    <div class=\"value\">").append(transactionHistory.size()).append("</div>\n");
            html.append("                </div>\n");
            html.append("                <div class=\"summary-card\">\n");
            html.append("                    <h3>Total Revenue</h3>\n");
            html.append("                    <div class=\"value\">$").append(String.format("%.2f", totalRevenue)).append("</div>\n");
            html.append("                </div>\n");
            html.append("            </div>\n");
            html.append("\n");
            html.append("            <div class=\"transactions\">\n");
            html.append("                <h2>📋 Transaction Details</h2>\n");

            for (int i = transactionHistory.size() - 1; i >= 0; i--) {
                TransactionRecord record = transactionHistory.get(i);
                html.append("                <div class=\"transaction\">\n");
                html.append("                    <div class=\"transaction-header\">\n");
                html.append("                        <div class=\"transaction-date\">").append(record.getFormattedDate()).append("</div>\n");
                html.append("                        <div class=\"transaction-amount\">$").append(String.format("%.2f", record.cost)).append("</div>\n");
                html.append("                    </div>\n");
                html.append("                    <div class=\"transaction-detail\">\n");
                html.append("                        <div>\n");
                html.append("                            <div class=\"detail-label\">👤 CUSTOMER</div>\n");
                html.append("                            <div class=\"detail-item\">").append(record.customer).append(" • ").append(record.phone).append("</div>\n");
                html.append("                        </div>\n");
                html.append("                        <div>\n");
                html.append("                            <div class=\"detail-label\">💳 PAYMENT</div>\n");
                html.append("                            <div class=\"detail-item\">").append(record.payment).append("</div>\n");
                html.append("                        </div>\n");
                html.append("                        <div>\n");
                html.append("                            <div class=\"detail-label\">🚘 VEHICLE</div>\n");
                html.append("                            <div class=\"detail-item\">").append(record.vehicle).append(" • ").append(record.plate).append("</div>\n");
                html.append("                        </div>\n");
                html.append("                        <div>\n");
                html.append("                            <div class=\"detail-label\">🧽 SERVICE</div>\n");
                html.append("                            <div class=\"detail-item\">").append(record.service).append("</div>\n");
                html.append("                        </div>\n");
                html.append("                    </div>\n");
                html.append("                </div>\n");
            }

            html.append("            </div>\n");
            html.append("        </div>\n");
            html.append("\n");
            html.append("        <div class=\"footer\">\n");
            html.append("            <p>Generated on ").append(new java.text.SimpleDateFormat("MMM dd, yyyy HH:mm:ss").format(new java.util.Date())).append("</p>\n");
            html.append("            <p>To save as PDF: Press Ctrl+P or use Print function and select \"Save as PDF\"</p>\n");
            html.append("        </div>\n");
            html.append("    </div>\n");
            html.append("</body>\n");
            html.append("</html>\n");

            // Open file chooser to save
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Transaction History Report");
            fileChooser.setInitialFileName("CarWash_Transaction_History.html");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML Files", "*.html"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
            );
            
            java.io.File file = fileChooser.showSaveDialog(new Stage());
            
            if (file != null) {
                java.nio.file.Path path = file.toPath();
                java.nio.file.Files.writeString(path, html.toString());
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("✅ Export Successful");
                alert.setHeaderText("Report saved successfully!");
                alert.setContentText("File saved to:\n" + file.getAbsolutePath() + 
                    "\n\nYou can open it in your browser and print as PDF using Ctrl+P");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Export Failed");
            alert.setHeaderText("Failed to export report");
            alert.setContentText("Error: " + e.getMessage());
            alert.showAndWait();
        }
    }

    // ─────────────────────────────────────────────
    // UI HELPERS
    // ─────────────────────────────────────────────
    private Node pageHeader(String icon, String title, String subtitle) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER_LEFT);

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        Label ico = new Label(icon);
        ico.setStyle("-fx-font-size: 28px;");
        Label ttl = new Label(title);
        ttl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        row.getChildren().addAll(ico, ttl);

        Label sub = new Label(subtitle);
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: " + TEXT_MUTED + ";");

        // Colored underline
        Rectangle underline = new Rectangle(60, 3);
        underline.setFill(Color.web(ACCENT_CYAN));
        underline.setArcWidth(3); underline.setArcHeight(3);

        box.getChildren().addAll(row, sub, underline);
        return box;
    }

    private VBox card() {
        VBox box = new VBox(14);
        box.setPadding(new Insets(22, 24, 22, 24));
        box.setStyle(
            "-fx-background-color: " + BG_CARD + ";" +
            "-fx-background-radius: 14;"
        );
        return box;
    }

    private VBox fieldGroup(String label, TextField field, Label errLabel) {
        VBox g = new VBox(4);
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        g.getChildren().addAll(lbl, field, errLabel);
        return g;
    }

    private TextField styledField(String prompt, String hint) {
        TextField tf = new TextField();
        tf.setPromptText(hint);
        tf.setStyle(
            "-fx-background-color: " + BG_DARK + ";" +
            "-fx-text-fill: " + TEXT_PRIMARY + ";" +
            "-fx-prompt-text-fill: #445577;" +
            "-fx-background-radius: 8;" +
            "-fx-border-color: " + BORDER_COLOR + ";" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 10 14;" +
            "-fx-font-size: 13px;"
        );
        tf.focusedProperty().addListener((o, ov, nv) -> {
            if (nv) tf.setStyle(tf.getStyle().replace("-fx-border-color: " + BORDER_COLOR, "-fx-border-color: " + ACCENT_CYAN));
            else    tf.setStyle(tf.getStyle().replace("-fx-border-color: " + ACCENT_CYAN, "-fx-border-color: " + BORDER_COLOR));
        });
        return tf;
    }

    private void resetFieldStyle(TextField tf) {
        tf.setStyle(tf.getStyle().replaceAll("-fx-border-color:[^;]+;",
                    "-fx-border-color: " + BORDER_COLOR + ";"));
    }

    private Label errLabel() {
        Label l = new Label();
        l.setStyle("-fx-font-size: 11px; -fx-text-fill: " + ERROR_RED + ";");
        return l;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_MUTED + ";" +
                   "-fx-padding: 4 0 0 0;");
        return l;
    }

    private ToggleButton serviceCard(String id, String name, String price, String desc, ToggleGroup group) {
        VBox box = new VBox(6);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16, 20, 16, 20));

        Label nm = new Label(name);
        nm.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        nm.setTextAlignment(TextAlignment.CENTER);

        Label pr = new Label(price);
        pr.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_CYAN + ";");

        Label ds = new Label(desc);
        ds.setStyle("-fx-font-size: 11px; -fx-text-fill: " + TEXT_MUTED + ";");
        ds.setTextAlignment(TextAlignment.CENTER);

        box.getChildren().addAll(nm, pr, ds);

        ToggleButton tb = new ToggleButton();
        tb.setGraphic(box);
        tb.setToggleGroup(group);
        tb.setUserData(id);
        tb.setPrefWidth(150);
        tb.setStyle(serviceCardStyle(false));
        tb.selectedProperty().addListener((o, ov, nv) -> tb.setStyle(serviceCardStyle(nv)));
        return tb;
    }

    private String serviceCardStyle(boolean selected) {
        return "-fx-background-color: " + (selected ? ACCENT_CYAN + "22" : BG_CARD) + ";" +
               "-fx-border-color: " + (selected ? ACCENT_CYAN : BORDER_COLOR) + ";" +
               "-fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12;" +
               "-fx-cursor: hand;";
    }

    private ToggleButton addonBtn(String label, String price) {
        ToggleButton tb = new ToggleButton(label + "\n" + price);
        tb.setStyle(addonStyle(false));
        tb.selectedProperty().addListener((o, ov, nv) -> tb.setStyle(addonStyle(nv)));
        return tb;
    }

    private String addonStyle(boolean on) {
        return "-fx-background-color: " + (on ? ACCENT_BLUE + "33" : BG_CARD) + ";" +
               "-fx-border-color: " + (on ? ACCENT_BLUE : BORDER_COLOR) + ";" +
               "-fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;" +
               "-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 12px; -fx-padding: 10 18; -fx-cursor: hand;";
    }

    private ToggleButton payBtn(String id, String icon, String label, ToggleGroup group) {
        VBox box = new VBox(4);
        box.setAlignment(Pos.CENTER);
        Label ico = new Label(icon);
        ico.setStyle("-fx-font-size: 22px;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        box.getChildren().addAll(ico, lbl);

        ToggleButton tb = new ToggleButton();
        tb.setGraphic(box);
        tb.setToggleGroup(group);
        tb.setUserData(id);
        tb.setPrefWidth(110);
        tb.setStyle(serviceCardStyle(false));
        tb.selectedProperty().addListener((o, ov, nv) -> tb.setStyle(serviceCardStyle(nv)));
        return tb;
    }

    private Button nextBtn(String text) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: linear-gradient(to right, " + ACCENT_BLUE + ", " + ACCENT_CYAN + ");" +
            "-fx-text-fill: #001A22; -fx-font-weight: bold; -fx-font-size: 13px;" +
            "-fx-background-radius: 10; -fx-padding: 12 28; -fx-cursor: hand;"
        );
        return b;
    }

    private Button nextBtn(String text, Runnable action) {
        Button b = nextBtn(text);
        b.setOnAction(e -> action.run());
        return b;
    }

    private Button backBtn(String text, Runnable action) {
        Button b = new Button(text);
        b.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 13px;" +
            "-fx-border-color: " + BORDER_COLOR + "; -fx-border-radius: 10;" +
            "-fx-background-radius: 10; -fx-padding: 12 20; -fx-cursor: hand;"
        );
        b.setOnAction(e -> action.run());
        return b;
    }

    private HBox navRow(Node... nodes) {
        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_RIGHT);
        row.getChildren().addAll(nodes);
        return row;
    }

    private HBox summaryRow(String key, String val) {
        HBox row = new HBox();
        Label k = new Label(key);
        k.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_MUTED + ";");
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        Label v = new Label(val);
        v.setStyle("-fx-font-size: 12px; -fx-text-fill: " + TEXT_PRIMARY + ";");
        row.getChildren().addAll(k, sp, v);
        return row;
    }

    private HBox summaryRowBig(String key, String val) {
        HBox row = summaryRow(key, val);
        row.getChildren().get(0).setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_PRIMARY + ";");
        row.getChildren().get(2).setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + ACCENT_GREEN + ";");
        return row;
    }

    private HBox receiptRow(String key, String val) {
        return summaryRow(key + ":", val);
    }

    private HBox receiptRowBold(String key, String val) {
        return summaryRowBig(key + ":", val);
    }

    private Separator divider() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color: " + BORDER_COLOR + ";");
        return s;
    }

    private ScrollPane centeredScroll(VBox content) {
        StackPane wrapper = new StackPane(content);
        wrapper.setAlignment(Pos.CENTER);
        StackPane.setMargin(content, new Insets(10));

        ScrollPane sp = new ScrollPane(wrapper);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return sp;
    }

    // ─────────────────────────────────────────────
    // INLINE CSS
    // ─────────────────────────────────────────────
    private String inlineCSS() {
        String css =
            ".scroll-bar { -fx-background-color: transparent; }" +
            ".scroll-bar .thumb { -fx-background-color: #1E3050; -fx-background-radius: 6; }" +
            ".scroll-bar .track { -fx-background-color: transparent; }" +
            ".scroll-bar .increment-button, .scroll-bar .decrement-button { -fx-background-color: transparent; }" +
            ".toggle-button { -fx-focus-color: transparent; -fx-faint-focus-color: transparent; }" +
            ".button { -fx-focus-color: transparent; }";
        try {
            java.nio.file.Path tmp = java.nio.file.Files.createTempFile("cwstyle", ".css");
            java.nio.file.Files.writeString(tmp, css);
            return tmp.toUri().toString();
        } catch (Exception ex) { return ""; }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
