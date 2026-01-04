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
* Explore an interactive menu with detailed food items and add-ons
* Add items to the cart & place orders (only for logged-in users)
* Request reserving tables and get notifications (only for logged-in users)
* Real-time chat with admin using socket communication
* All files are synchronized over the connected network
* Smooth UI with animations and organized pages
* Edit or update profile

### **Admin Features**

* Dynamically update menu, food items or coupons
* Manage menu items, users, coupons through an admin dashboard
* Live chat with customers
* Graphical daily analytics
* Update profile or change password

### **Networking**

* Custom-built **Chat Server**
* Multiple clients can communicate with the admin
* User to user communication is prohibited
* Automatic server detection
* Real-time message flow
* Central Main Server to synchronize all files
* Real-time updated menu page and user synchronization

## 📁 **Project Structure**

```text
MunchOak/
├── .gitignore
├── README.md
├── pom.xml                     # Maven build configuration
├── .idea/                      # IntelliJ project settings (IDE-specific)
├── .mvn/                       # Maven wrapper support files
└── src/
    └── main/
        ├── java/
        │   │
        │   ├── module-info.java
        │   │
        │   └── com/
        │       └── munchoak/
        │           ├── AppLauncher.java            # Application entry / launcher
        │           │
        │           ├── authentication/             # Login, profile & password management
        │           │   ├── ChangeAdminPassPopup.java
        │           │   ├── ChangeUserPassPopup.java
        │           │   ├── EditAdminProfilePopup.java
        │           │   ├── EditUserProfilePopup.java
        │           │   ├── LoginPage.java
        │           │   ├── PasswordPolicy.java
        │           │   ├── PasswordStorage.java
        │           │   ├── PasswordUtil.java
        │           │   └── ProfilePage.java
        │           │
        │           ├── cart/                       # Cart UI, pricing, cart state & helpers
        │           │   ├── Cart.java
        │           │   ├── CartPage.java
        │           │   └── CartSearchCardFactory.java
        │           │
        │           ├── coupon/                     # Coupon CRUD + coupon persistence
        │           │   ├── AddCouponPopup.java
        │           │   ├── CouponStorage.java
        │           │   └── EditCouponPopup.java
        │           │
        │           ├── homepage/                   # Landing/home page components & extensions
        │           │   ├── HomePage.java
        │           │   ├── HomePageComponent.java
        │           │   ├── HomePageExtension.java
        │           │   ├── HomePageSecondExtension.java
        │           │   ├── HomePageThirdExtension.java
        │           │   ├── HomePageFourthExtension.java
        │           │   ├── HomePageFifthExtension.java
        │           │   ├── HomePageSixthExtension.java
        │           │   ├── HomePageSeventhExtension.java
        │           │   └── HomePageEighthExtension.java
        │           │
        │           ├── mainpage/                   # Main navigation pages (admin/user entry views)
        │           │   ├── AdminHome.java
        │           │   ├── FoodItems.java
        │           │   └── Home.java
        │           │
        │           ├── manager/                    # Storage/persistence utilities + session handling
        │           │   ├── AdminStorage.java
        │           │   ├── CategoryStorage.java
        │           │   ├── MenuStorage.java
        │           │   ├── Session.java
        │           │   ├── StorageInit.java
        │           │   ├── StoragePaths.java
        │           │   ├── StorageUtil.java
        │           │   └── UserStorage.java
        │           │
        │           ├── menu/                       # Menu browsing/editing for guest/user/admin
        │           │   ├── AdminMenu.java
        │           │   ├── BaseMenu.java
        │           │   ├── GuestMenu.java
        │           │   ├── MenuEdit.java
        │           │   ├── MenuPage.java
        │           │   └── UserMenu.java
        │           │
        │           ├── network/                    # Socket-based chat/networking layer
        │           │   ├── ChatClient.java
        │           │   ├── ChatMessage.java
        │           │   └── ChatServer.java
        │           │
        │           ├── payment/                    # Checkout, billing, payment history & persistence
        │           │   ├── Bill.java
        │           │   ├── CheckoutPage.java
        │           │   ├── History.java
        │           │   ├── Payment.java
        │           │   ├── PaymentBreakdown.java
        │           │   └── PaymentStorage.java
        │           │
        │           ├── reservation/                # Table reservation & reservation messaging
        │           │   ├── AboutUsPage.java
        │           │   ├── ReservationMsgPage.java
        │           │   ├── ReservationMsgStorage.java
        │           │   ├── ReservationPage.java
        │           │   └── ReservationStorage.java
        │           │
        │           └── server/                     # App-level client/server bootstrapping
        │               ├── MainClient.java
        │               └── MainServer.java
        │           
        └── resources/
            └── com/                                # Static assets (e.g., CSS, images, FXML, data)
                └── munchoak/
                    ├── manager/
                    │   ├── data/
                    │   └── images/
                    │
                    ├── network/
                    |   ├── chats/
                    |   └── ChatWindow.fxml
                    │
                    └── view/
                        ├── styles/
                        └── images/
```
## **Tools**

| Tool                                   | Description                              |
|----------------------------------------|------------------------------------------|
| **Java**                               | Primary programming language             |
| **JavaFX (BellSoft Liberica v21.0.8)** | UI framework                             |
| **CSS**                                | UI framework                             |
| **IntelliJ IDEA CE v2025.2.2**         | Development environment                  |
| **Maven**                              | Build automation & dependency management |

## ▶️ **How to Run the Application in IDE**

### **Run the Main Application**

1. Import the project into **IntelliJ IDEA**.
2. Allow Maven to download all JavaFX dependencies.
3. Build the entire project.
4. Run `AppLauncher.java`

### **Connecting to the Server**

#### **On Windows**

1. **Turn off Windows Defender Firewall**
   (Required for socket communication over LAN)
2. Choose only one PC as the **server** <br> 
   Main Server will run at port 8080 and Chat Server will run at port 5050
3. All other devices must be connected to the **same Wi-Fi network**
4. After running app, if servers are running already then it will automatically detect it and connect to it.
5. You can also check the connectivity as follows: 
-  Run in the command prompt:

   ```
   ping <server IPv4 address>
   ```

- For example:

   ```
   ping 10.33.22.87
   ```

## Executable File and Installer Link

- Exe file: [MunchOak App-3.0.exe](https://drive.google.com/file/d/1Whb9V8VTa6S56lOjPijQDeELMSlrZHFA/view?usp=drive_link)
- Installer: [MunchOak App-3.0.msi](https://drive.google.com/file/d/1rJdzsGo9nasJLeNU1ciBrLAamc9d8pvM/view?usp=drive_link)

_**Note: You have to run the downloaded app as "run as administrator"**_

## **Contributors**

| Name                      | Role      |
| ------------------------- | --------- |
| **Md. Samiul Islam Siam** | Developer |
| **Partho Kumar Mondal**   | Developer |
| **Adeba Jahan**           | Developer |
| **Shuchita Islam Shuvra**  | Developer |

