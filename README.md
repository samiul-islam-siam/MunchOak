# 🍽️ **MunchOak** 
### Interactive Restaurant Management System

*An Academic Project for **CSE 2104**, University of Dhaka*.

**MunchOak** is an interactive restaurant management system that enables multiple users to log in or browse, 
explore menus and place orders or reserve tables as well as communicate with administrators in real-time using socket-based synchronization.

Built using **Java**, **JavaFX**, and **TCP socket and thread-based networking**, 
this project demonstrates both frontend UI design and backend file system management and communication systems.

## 🌟 **Features**

### **User Features**

* Login or browse as guest
* Explore an interactive menu with detailed food items
* Add items to the cart & place orders (only for logged-in users)
* Reserve tables (only for logged-in users)
* Real-time chat with admin using socket communication
* User login is synchronized over the connected network
* Smooth UI with animations and organized pages

### **Admin Features**

* Dynamically update menu or food items
* Manage menu items through an admin dashboard
* Live chat with customers

### **Networking**

* Custom-built **Chat Server**
* Multiple clients can communicate with the admin
* User to user communication is prohibited
* Real-time message flow
* Menu Server to update Food Items
* Real-time updated menu page and user synchronization

## 📁 **Project Structure**

```
MunchOak/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       ├── example/
│       │           ├── login/                  # Authentication system
│       │           │   ├── AdminDashboard.java
│       │           │   └── ChangeAdminPasswordPage.java
│       │           │
│       │           ├── manager/                # File, session & storage handling
│       │           │   ├── AdminFileStorage.java
│       │           │   ├── FileStorage.java
│       │           │   ├── PasswordUtils.java
│       │           │   └── Session.java
│       │           │
│       │           ├── menu/                   # Food items & menu handling
│       │           │   ├── AdminMenu.java
│       │           │   ├── BaseMenu.java
│       │           │   ├── GuestMenu.java
│       │           │   ├── MenuClient.java
│       │           │   ├── MenuPage.java
│       │           │   ├── MenuServer.java     @Run it first
│       │           │   └── UserMenu.java
│       │           │
│       │           ├── munchoak/               # Main App Logic
│       │           │   ├── Bill.java           
│       │           │   ├── Cart.java
│       │           │   ├── CartPage.java
│       │           │   ├── CheckoutPage.java
│       │           │   ├── FoodItems.java
│       │           │   ├── History.java
│       │           │   ├── Home.java           # Launcher
│       │           │   └── Payment.java
│       │           │
│       │           ├── network/                # Chat server & client
│       │           │   ├── ChatClient.java
│       │           │   ├── ChatMessage.java
│       │           │   └── ChatServer.java     @Run it secondly
│       │           │
│       │           ├── view/                   # UI logic
│       │           │   ├── AboutUsPage.java
│       │           │   ├── ChangePasswordPopup.java
│       │           │   ├── HomePage.java
│       │           │   ├── HomePageComponent.java
│       │           │   ├── HomePageExtension.java
│       │           │   ├── HomePageSecondExtension.java
│       │           │   ├── HomePageThirdExtension.java
│       │           │   ├── HomePageFourthExtension.java
│       │           │   ├── HomePageFifthExtension.java
│       │           │   ├── HomePageSixthExtension.java
│       │           │   ├── HomePageSeventhExtension.java
│       │           │   ├── HomePageEighthExtension.java
│       │           │   ├── LoginPage.java
│       │           │   ├── ProfilePage.java
│       │           │   └── ReservationPage.java
│       │           │
│       │           └── module-info.java
│       └── resources/
│               └── com/
│                   └── example/
│                       ├── manager/
│                       ├── munchoak/
│                       └── network/
│
├── pom.xml
├── .idea/
├── .mvn/
├── target/
└── README.md
```

## **Tools**

| Tool                                   | Description                              |
|----------------------------------------|------------------------------------------|
| **Java**                               | Primary programming language             |
| **JavaFX (BellSoft Liberica v21.0.8)** | UI framework                             |
| **CSS**                                | UI framework                             |
| **IntelliJ IDEA CE v2025.2.2**         | Development environment                  |
| **Maven**                              | Build automation & dependency management |

## ▶️ **How to Run the Application**

### **Run the Main Application**

1. Import the project into **IntelliJ IDEA**.
2. Allow Maven to download all JavaFX dependencies.
3. Build the entire project.
4. First run `MenuServer.java` and then `ChatServer.java`.
5. Navigate to:

```
src/main/java/com/example/munchoak/Home.java
```

6. Run `Home.java` to start the system.

### **Connecting to the Menu & Chat Server**

#### **On Windows**

1. **Turn off Windows Defender Firewall**
   (Required for socket communication over LAN)
2. Choose only one PC as the **server** <br> 
   Menu Server will run at port 8080 and Chat Server will run at port 5050
3. All other devices must be connected to the **same Wi-Fi network**
4. To test connectivity, run:

   ```
   ping <server IPv4 address>
   ```

   Example:

   ```
   ping 10.33.22.87
   ```

## **Contributors**

| Name                      | Role      |
| ------------------------- | --------- |
| **Md. Samiul Islam Siam** | Developer |
| **Partho Kumar Mondal**   | Developer |
| **Adeba Jahan**           | Developer |
| **Shuchita Islam Shuvra**  | Developer |

