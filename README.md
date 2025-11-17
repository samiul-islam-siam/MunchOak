# 🍽️ **MunchOak** 
### Interactive Restaurant Management System

*An Academic Project for **CSE 2104**, University of Dhaka*.

**MunchOak** is an interactive restaurant management system that enables multiple users to log in or browse, 
explore menus and place orders or reserve tables as well as communicate with administrators in real-time using socket-based synchronization.

Built using **Java**, **JavaFX**, and **TCP socket and thread-based networking**, 
this project demonstrates both frontend UI design and backend file system management and communication systems.

---

## 🌟 **Features**

### **User Features**

* Login or browse as guest
* Explore an interactive menu with detailed food items
* Add items to the cart & place orders (only for logged-in users)
* Reserve tables (only for logged-in users)
* Real-time chat with admin using socket communication
* Smooth UI with animations and organized pages

### **Admin Features**

* Manage menu items through an admin dashboard
* Live chat with customers

### **Networking**

* Custom-built **Chat Server**
* Multiple clients can communicate with the admin
* User to user communication is prohibited
* Real-time message flow

---

## 📁 **Project Structure**

```
MunchOak/
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       ├── example/
│       │       │       ├── login/        # Authentication system
│       │       │       ├── manager/      # File & session management
│       │       │       ├── menu/         # Menu & food items
│       │       │       ├── munchoak/     # Main application logic
│       │       │       ├── network/      # Chat server & client
│       │       │       └── view/         # JavaFX UI layouts
│       │       └── module-info.java
│       └── resources/
│               └── com/
│                   └── example/
│                       ├── login/
│                       ├── manager/
│                       ├── munchoak/
│                       ├── network/
│                       └── view/
│
├── pom.xml
├── .idea/
├── .mvn/
├── target/
└── README.md
```

---

## **Tools**

| Tool                                   | Description                              |
|----------------------------------------|------------------------------------------|
| **Java**                               | Primary programming language             |
| **JavaFX (BellSoft Liberica v21.0.8)** | UI framework                             |
| **CSS**                                | UI framework                             |
| **IntelliJ IDEA CE v2025.2.2**         | Development environment                  |
| **Maven**                              | Build automation & dependency management |

---

## ▶️ **How to Run the Application**

### **Run the Main Application**

1. Import the project into **IntelliJ IDEA**.
2. Allow Maven to download all JavaFX dependencies.
3. Build the entire project.
4. Navigate to:

```
src/main/java/com/example/munchoak/Home.java
```

5. Run `Home.java` to start the system.

---

### **Connecting to the Chat Server**

#### **On Windows**

1. **Turn off Windows Defender Firewall**
   (Required for socket communication over LAN)
2. Choose one PC as the **server**
3. All other devices must be connected to the **same Wi-Fi network**
4. To test connectivity, run:

   ```
   ping <server IPv4 address>
   ```

   Example:

   ```
   ping 10.33.22.87
   ```

---

## **Contributors**

| Name                      | Role      |
| ------------------------- | --------- |
| **Md. Samiul Islam Siam** | Developer |
| **Partho Kumar Mondal**   | Developer |
| **Adiba Jahan**           | Developer |
| **Suchita Islam Shuvra**  | Developer |

