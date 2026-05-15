 # 🚗 CarWash Pro — Management System

A modern, desktop-based car wash management system built with **Java** and **JavaFX**. Features an intuitive 4-step wizard interface for processing car wash transactions with support for multiple service tiers, add-ons, and payment methods.

## ✨ Features

### Core Functionality
- **4-Step Transaction Wizard**
  - Step 1: Customer Information (name, phone)
  - Step 2: Vehicle Details (model, plate number)
  - Step 3: Wash Service Selection with Add-ons
  - Step 4: Payment Method & Confirmation

- **Wash Services**
  - Basic Wash ($50)
  - Premium Wash ($100)
  - Full Wash ($150)

- **Service Add-ons** (Decorator Pattern)
  - Wax Protection (+$30)
  - Interior Cleaning (+$40)
  - Combinable for custom packages

- **Payment Methods** (Strategy Pattern)
  - Cash
  - Card
  - Wallet
  - Extensible design for new methods

### Transaction Management
- 📜 Complete transaction history tracking
- Persistent storage in transaction logs
- HTML export reports (CarWash_Transaction_History.html)
- Transaction details: date, time, customer, vehicle, service, cost

### Modern UI/UX
- Dark-themed professional interface
- Smooth animations and transitions
- Real-time input validation
- Step progress indicators
- Responsive design
- Modern color scheme with cyan accents

## 🛠️ Technology Stack

- **Language**: Java 17
- **GUI Framework**: JavaFX 17.0.19
- **Build System**: Command-line compilation
- **Platform**: Windows (batch files provided)

## 📋 System Requirements

- **JDK**: Java 17 (Eclipse Adoptium)
  - Download: https://adoptium.net/
- **JavaFX SDK**: 17.0.19
  - Download: https://gluonhq.com/products/javafx/
- **OS**: Windows (or Unix/Linux with script adaptation)
- **RAM**: Minimum 512MB
- **Disk Space**: ~500MB for JDK and JavaFX

## 🚀 Getting Started

### Installation

1. **Install Java 17**
   - Download from [Eclipse Adoptium](https://adoptium.net/)
   - Install and note the installation path

2. **Install JavaFX SDK**
   - Download from [Gluon](https://gluonhq.com/products/javafx/)
   - Extract to a known location

3. **Clone/Download this project**
   ```
   git clone <repository-url>
   cd carwashsystem
   ```

4. **Update paths** (if using different installation paths)
   - Edit `run.bat` or `run.ps1`
   - Update the `--module-path` to your JavaFX lib directory

### Running the Application

**Windows (Batch)**
```batch
run.bat
```

**Windows (PowerShell)**
```powershell
.\run.ps1
```

**Manual Compilation & Run**
```bash
javac -encoding UTF-8 \
  --module-path "path/to/javafx-sdk-17.0.19/lib" \
  --add-modules javafx.controls,javafx.fxml \
  -d . \
  src/carwashsystem/CarWashGUI.java

java --module-path "path/to/javafx-sdk-17.0.19/lib" \
  --add-modules javafx.controls,javafx.fxml \
  carwashsystem.CarWashGUI
```

## 📁 Project Structure

```
carwashsystem/
├── README.md                          # This file
├── run.bat                            # Windows batch launcher
├── run.ps1                            # PowerShell launcher
├── src/
│   └── carwashsystem/
│       └── CarWashGUI.java            # Main application & all classes
├── lib/                               # External libraries (if needed)
├── carwashsystem/                     # Compiled .class files
├── transaction_history.txt            # Transaction log (plain text)
└── CarWash_Transaction_History.html   # Transaction report (HTML)
```

## 🏗️ Architecture & Design Patterns

### Object-Oriented Design
The application implements multiple design patterns for flexibility and maintainability:

#### **Factory Pattern** (Wash Service Creation)
```java
WashService wash = WashFactory.createWash(choice);
// Creates appropriate wash type: Basic, Premium, or Full
```

#### **Decorator Pattern** (Service Add-ons)
```java
WashService wash = new BasicWash();
wash = new WaxDecorator(wash);
wash = new InteriorDecorator(wash);
// Dynamically adds features with cost calculation
```

#### **Strategy Pattern** (Payment Processing)
```java
PaymentStrategy payment = new CardPayment();
payment.pay(totalCost);
// Supports multiple payment methods interchangeably
```

#### **Observer Pattern** (Notification System)
```java
WashNotifier notifier = new WashNotifier();
notifier.addObserver(observer);
notifier.notifyAllObservers(message);
// Decoupled event notifications
```

### Core Classes

| Class | Purpose |
|-------|---------|
| `CarWashGUI` | Main JavaFX application & UI orchestration |
| `Customer` | Customer data model |
| `Car` | Vehicle data model |
| `Validator` | Input validation utilities |
| `WashService` | Interface for wash types |
| `WashFactory` | Creates wash service instances |
| `WashDecorator` | Base for service add-ons |
| `PaymentStrategy` | Interface for payment methods |
| `TransactionRecord` | Transaction data & formatting |

## 💾 Data Files

### transaction_history.txt
Plain text log of all transactions with timestamp and details.

### CarWash_Transaction_History.html
Auto-generated HTML report with formatted transaction data for viewing in a browser.

## 🎨 UI Theme

The application uses a modern dark theme with the following color palette:

| Element | Color | Hex |
|---------|-------|-----|
| Background | Dark Navy | `#0A0E1A` |
| Cards | Deep Blue | `#111827` |
| Accent Primary | Cyan | `#00D4FF` |
| Accent Secondary | Blue | `#4F8EF7` |
| Accent Success | Green | `#00E5A0` |
| Text Primary | Light Blue | `#F0F4FF` |
| Text Muted | Gray-Blue | `#8899BB` |
| Error | Red | `#FF4D6A` |

## 🔍 Key Features Explained

### Input Validation
- **Name**: Letters and spaces only, non-empty
- **Phone**: 10-15 digits
- **Vehicle Model**: Non-empty
- **Plate Number**: Non-empty

### Transaction Flow
1. Collect customer & vehicle info
2. Select wash type and optional add-ons
3. Choose payment method
4. Process payment
5. Log transaction to history
6. Display confirmation

### Extensibility
- Add new wash types by creating classes implementing `WashService`
- Add payment methods by implementing `PaymentStrategy`
- Add service add-ons by extending `WashDecorator`
- Add notifications by implementing `Observer` interface

## 📝 Usage Example

1. **Launch** the application using `run.bat`
2. **Enter customer details** (name: John Doe, phone: 1234567890)
3. **Enter vehicle info** (model: Toyota Camry, plate: ABC123)
4. **Select wash service**: Choose "Premium Wash"
5. **Add options**: Select "Wax Protection"
6. **Choose payment**: Select "Card"
7. **Confirm**: Click to process transaction
8. **View History**: Click "📜 History" button to see all transactions

## 🐛 Troubleshooting

### Application Won't Start
- Verify Java 17 is installed: `java -version`
- Verify JavaFX SDK path in batch/PowerShell file is correct
- Check that all required modules are available

### Compilation Errors
- Ensure `-encoding UTF-8` flag is used (already in scripts)
- Verify `--module-path` points to JavaFX `lib` directory
- Check that `src/carwashsystem/CarWashGUI.java` exists

### JavaFX Module Not Found
- Download the correct JavaFX version (17.0.19)
- Update the `--module-path` in launch scripts

## 📚 Learning Resources

- [JavaFX Documentation](https://openjfx.io/)
- [Design Patterns in Java](https://refactoring.guru/design-patterns)
- [Java 17 Features](https://www.oracle.com/java/technologies/javase/17-relnotes.html)

## 📄 License

[Add your license here]

## 👨‍💻 Author

[Add author information here]

## 🤝 Contributing

Contributions are welcome! Please follow these guidelines:
- Follow existing code style and patterns
- Add comments for new features
- Test thoroughly before submitting
- Update this README with new features
