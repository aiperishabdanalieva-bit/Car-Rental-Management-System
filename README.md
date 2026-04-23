#  Car Rental System (CLI + GUI + SQLite)

**Student:** [Shabdanalieva Aiperi] 
**Presentation:** [https://drive.google.com/file/d/1AYuYsaNYcHV_WzuDHEr_mu4SoHsnSDxA/view?usp=sharing]  
**GitHub:** [https://github.com/aiperishabdanalieva-bit/Car-Rental-Management-System]

---

##  Description

The **Car Rental System** is a Java application that manages car rentals, bookings, and user accounts. It offers two interfaces:

- **Graphical User Interface (GUI)** – built with Swing, featuring tabs, tables, and forms  
- **Command Line Interface (CLI)** – lightweight terminal mode for quick operations  

Data is stored in an **SQLite database** via JDBC, with **CSV/JSON export and import** for backup and data exchange.  
Login is required, with two roles: **Admin** (full control) and **Customer** (book and manage own rentals).

---

##  Objectives

- Build a complete CRUD system for cars, bookings, and users  
- Demonstrate all three OOP principles: **Encapsulation**, **Inheritance**, **Polymorphism**  
- Implement role-based access control (Admin vs Customer)  
- Provide both GUI and CLI interfaces  
- Persist data with SQLite and support file import/export  

---

##  Project Requirements Checklist

| # | Requirement | Status |
|---|-------------|--------|
| 1 | **CRUD Operations** — Create, Read, Update, Delete records | ✅ Cars, bookings, users |
| 2 | **Command Line Interface** — user‑friendly menus and prompts | ✅ Available via `--cli` flag |
| 3 | **Graphical User Interface** — Swing‑based windows with tables and forms | ✅ Login, customer, admin panels |
| 4 | **Input Validation** — date formats, empty fields, price values | ✅ All inputs validated; error dialogs shown |
| 5 | **Data Persistence** — SQLite database (`car_rental.db`) | ✅ JDBC with prepared statements |
| 6 | **File Export/Import** — CSV and JSON formats | ✅ `ExportImportService` handles both |
| 7 | **Authentication** — login with role check | ✅ `Authorization` class + LoginFrame |
| 8 | **User Roles** — Admin (full CRUD, view users, income) and Customer (bookings) | ✅ |
| 9 | **Encapsulation** — private fields with getters in `User`, `Car`, `Booking` | ✅ |
|10 | **Inheritance** — abstract `Entity` class → `User`, `Car`, `Booking` | ✅ |
|11 | **Polymorphism** — `toString()` overridden in each subclass; GUI event listeners | ✅ |
|12 | **Error Handling** — try/catch blocks, user‑friendly messages | ✅ All database and I/O errors handled gracefully |

**Bonus ( +30 pts ):**  
✅ GUI Implementation (Swing)  
✅ Database Integration (SQLite via JDBC)  
✅ Authentication & User Roles (Admin / Customer)  

---

##  Project Structure

```
src/
├── Main.java                   # Entry point — launches GUI or CLI
│
├── model/
│   ├── Entity.java             # Abstract base class (Inheritance root)
│   ├── User.java               # Child of Entity — login account (username, password, role)
│   ├── Car.java                # Child of Entity — car info (model, price, availability)
│   └── Booking.java            # Child of Entity — rental booking (dates, total price)
│
├── database/
│   └── DatabaseHelper.java     # SQLite connection & table creation
│
├── service/
│   ├── Authorization.java      # Login & registration logic
│   ├── CarService.java         # CRUD for cars, toggle availability
│   ├── BookingService.java     # CRUD for bookings, conflict detection, total income
│   ├── MenuService.java        # CLI menu navigation for both roles
│   └── ExportImportService.java # CSV/JSON export and import
│
└── gui/
├── LoginFrame.java         # Swing login/registration window
├── CustomerFrame.java      # Customer panel — view cars, book, cancel, export
└── AdminFrame.java         # Admin panel — manage cars, users, bookings, income, import/export
```

---

##  OOP Principles Demonstrated

### Encapsulation
All model classes (`User`, `Car`, `Booking`) use **private fields** with public getters.  
For example, `Car.model`, `Car.pricePerDay`, `Car.available` are private and accessed only via getters.

### Inheritance
`Entity` is an **abstract parent class** holding the common field `id` and its getter.  
All three domain classes extend `Entity` and inherit `getId()`.

```
Entity (abstract)
├── User     — adds username, password, role
├── Car      — adds model, pricePerDay, available
└── Booking  — adds userId, carId, startDate, endDate, totalPrice
```

### Polymorphism
Each subclass provides its own **overridden `toString()`** method, used throughout the application (tables, lists, debug output).  
GUI button listeners also demonstrate polymorphic behaviour via `ActionListener` implementations.

---

##  Features

### Customer Panel
- View all **available cars** in a styled table
- **Book a car** by selecting dates; system checks for overlaps and shows conflicting bookings
- View **personal booking history** with total prices
- **Cancel** a booking (removed immediately)
- **Export** personal booking history to CSV or JSON

### Admin Panel
- View **all bookings** across the system
- **Add new cars** (model, price)
- **Toggle car availability** (on/off)
- **Change car price** via dialog
- View **registered customers**
- **Add new admin accounts** (requires master password `adminpro`)
- **Show all users with passwords** (master password protected)
- View **total system revenue**
- **Export** users, cars, or bookings to CSV/JSON
- **Import cars** from CSV/JSON files

### Authentication & Security
- Login required before any access
- Passwords stored as plain text (for educational purposes; SHA‑256 could be added)
- Admin role required for write operations; customers only manage their own bookings
- Master password `adminpro` protects sensitive admin functions

### Data Persistence
- Primary storage: **SQLite** (`car_rental.db`) – all CRUD operations via JDBC
- Export/Import: **CSV and JSON** via `ExportImportService` – useful for backup and bulk load

### Input Validation
- Dates must be in `YYYY-MM-DD` format; end date must be after start date
- Prices must be positive numbers
- Empty fields are rejected
- Overlapping bookings are checked before confirming

---

##  Default Accounts

| Username | Password | Role |
|----------|----------|------|
| `admin` | `admin` | ADMIN |
| (customer) | (register yourself) | CUSTOMER |

Admin is created automatically when database is initialised.  
Customers can register via the Login screen (GUI) or CLI menu.

---

##  How to Run

### Prerequisites
- Java 17+ (or any version with JDBC)
- SQLite JDBC driver (included in classpath)
- No Maven required; just compile and run

### GUI Mode (default)
From the `src` folder:

```bash
javac -encoding UTF-8 *.java model/*.java database/*.java service/*.java gui/*.java
java Main
```

### CLI Mode
```bash
java Main --cli
```

---

##  Test Cases & Expected Outputs

##  Test Cases & Expected Outputs

| Action | Input | Expected Output |
|--------|-------|-----------------|
| Register new customer | Username: `john`, Password: `1234` | "Registration successful! Please log in." |
| Login with correct credentials | `admin` / `admin` | Access granted; Admin panel opens |
| Login with wrong password | `admin` / `wrong` | "Invalid credentials. Try again." |
| Customer books a car | Car ID 3, dates `2026-05-01` – `2026-05-03` | Total price $190.0 shown; booking added to "My Bookings" |
| Booking with overlapping dates | Same car, dates `2026-05-02` – `2026-05-04` (overlaps existing booking) | "Car is already booked. Conflicting bookings: Booking #4: 2026-05-02 → 2026-05-04" |
| Cancel a booking | Customer selects Booking #5, confirms | "Booking #5 has been cancelled." Table refreshes |
| Customer exports history | Click "Export History", choose CSV | File `my_history_john.csv` created with correct data |
| Admin adds a new car | Model: `Honda Civic`, Price: `75.0` | Car appears in Cars table with "✓ Yes" |
| Admin toggles car availability | Select a car, click "Toggle Availability" | Availability column switches between "✓ Yes" and "✗ No" |
| Admin changes car price | Select BMW M4, new price `130.0` | Price updates to $130.0 instantly in the table |
| Admin views total income | Open "Income" tab | Total System Revenue: $X (sum of all booking totals) |
| Admin exports all bookings | Export → Bookings → CSV → `all_bookings.csv` | File created with all booking records |
| Admin imports cars from CSV | Import Cars → `new_cars.csv` | Success message: e.g., "Successfully imported 3 cars" |
| Invalid date input (GUI) | Start date > end date | "End date must be after start date." |
| Empty field validation | Login with empty username/password | "Username and password cannot be empty." |
| Unauthorised action (Customer) | Customer tries to view system users via GUI | Only own bookings visible; no admin tabs shown (by design) |

##  Screenshots

> *All screenshots include system date and time in the taskbar.*

### 1. Login Screen
![Login Screen](screenshots/1.png)

*Login dialog — user enters credentials or registers.*

---

### 2. Customer Panel – Available Cars
![Customer Cars](screenshots/img.png)

*Customer view showing available cars in a table, with "Book Selected Car" button.*

---

### 3. Booking Dialog with Overlap Warning
![Booking Overlap](screenshots/img_1.png)
![Booking Overlap](screenshots/img_2.png)
*When selected dates conflict, a detailed message shows exactly which bookings overlap.*

---

### 4. Customer Panel – My Bookings
![Customer Bookings](screenshots/img_3.png)
![Customer Bookings](screenshots/img_4.png)
![Customer Bookings](screenshots/img_5.png)
*Personal booking history with cancel and export buttons.*

---

### 5. Admin Panel – Cars Management
![Admin Cars](screenshots/img_6.png)

*Admin manages cars: add, toggle availability, change price.*

---

### 6. Admin Panel – All Bookings
![Admin Bookings](screenshots/img_7.png)

*View of all bookings in the system.*

---

### 7. Admin Panel – Customers List
![Admin Customers](screenshots/img_8.png)

*List of registered customers (USER role).*

---

### 8. Admin Panel – Add New Admin
![Add Admin](screenshots/img_9.png)
![Add Admin](screenshots/img_10.png)

*Creating a new admin account (master password required).*

---

### 9. Admin Panel – Show All Users & Passwords
![Show Passwords](screenshots/img_11.png)
![Show Passwords](screenshots/img_12.png)

*Protected view of all usernames and passwords.*

---

### 10. Admin Panel – Total Income
![Total Income](screenshots/img_13.png)

*Display of total system revenue.*

---

### 11. Admin Panel – Export Data
![Export](screenshots/img_14.png)
![Export](screenshots/img_15.png)
![Export](screenshots/img_16.png)
![Export](screenshots/img_17.png)
*Export options for users, cars, or bookings, with format choice.*

---

### 12. Admin Panel – Import Cars
![Import](screenshots/img_18.png)
![Export](screenshots/img_19.png)
*Import cars from CSV or JSON file, with success message.*

---

### 13. CLI Mode – Main Menu
![CLI Menu](screenshots/img_20.png)

*Command Line Interface showing the main menu after login.*

---

### 14. CLI Mode – Booking Process
![CLI Booking](screenshots/img_21.png)

*Text‑based booking: select car, enter dates, see total, confirm.*

---

## 📘 Error Handling

- All database operations are wrapped in try‑catch blocks, showing descriptive messages.
- Input validation is performed at the UI layer before data reaches service classes.
- File import skips invalid lines and reports how many records were added.
- GUI uses `JOptionPane` for warnings, errors, and informational messages.

---```
