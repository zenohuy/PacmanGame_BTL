# 🎮 Pacman Game (BTL-Java)

**Author:** Duong Quang Huy (@zenohuy)

A classic Pacman game developed in **Java** following the **MVC (Model-View-Controller)** architecture.  
The game not only includes the core Pacman mechanics but also integrates a **user account management system** connected to a **MySQL database**.

---

## 🚀 Key Features

### 👾 Gameplay & Core Mechanics
- **Smart Steering (Buffered Input):** Smooth directional changes at tile centers and emergency 180° turns for quick escapes.
- **Ghost AI:** Grid-snapping movement prevents wall clipping. Ghosts automatically choose valid paths at intersections and never reverse direction unless trapped, creating realistic chase pressure.
- **Accurate Collision Detection:** Uses `java.awt.Rectangle` for precise collision between Pacman, walls, pellets, and ghosts.

### ⚙️ Game Loop & State Management
- Stable **Game Loop** running at **60 FPS** for smooth performance.
- Flexible **Game State Management** with seamless transitions between:
  *Main Menu, Login, Register, Playing, Paused, Settings, Controls, Level Locked,* etc.

### 🏆 Levels & High Scores
- Multiple diverse maps (level1, level2, level3...).
- **Level Unlock System:** Players must complete one level to unlock the next.
- Automatic synchronization and storage of **High Scores** per level in the database.

### 🏗️ MVC Architecture
- Clear separation of **Model**, **View**, and **Controller** for maintainability, scalability, and easier debugging.

### 🔐 Login & Registration (MySQL)
- Authentication screen requiring login or account creation to save scores.
- Local database connection via **XAMPP**.
- Optimized input forms: Tab navigation, Enter confirmation, and **Password Toggle** (show/hide).

### 🔊 Sound Management
- Dynamic audio system: Different background music for menus and gameplay, plus detailed sound effects (pellet eating, win, lose, clicks).
- Quick access to **Settings** with shortcut key `Q`, allowing sound toggle.
- Smooth UI navigation: Returning from Settings brings the player back to the correct previous screen.

### 🎨 User Interface
- Organized asset management (images loaded from `res/assets`).
- **Hover Animations** for buttons (Play, Menu, Back...) to enhance interactivity.

---

## 📁 Project Structure

```text
PacManGame/
│
├── out/                 # File .class sau khi build
│
├── res/                 # Tài nguyên (Resources)
│   ├── assets/          # Hình ảnh
│   ├── maps/            # Bản đồ
│   └── sounds/          # Âm thanh
│
├── src/
│   ├── controller/      # GameController, InputHandler
│   ├── model/           # GameModel, GameState, PacMan, Ghost...
│   ├── utils/           # AssetManager, DatabaseManager, SoundManager
│   ├── view/            # GameWindow, GamePanel
│   └── Main.java        # Entry point
│
├── .gitignore
├── PacManGame.iml
└── README.md

```


---

## ⚙️ System Requirements & Installation

### Requirements
1. **JDK (Java Development Kit):** Version 8 or higher recommended.
2. **IDE:** IntelliJ IDEA, Eclipse, NetBeans, or VS Code with Java extensions.
3. **XAMPP:** Required to run MySQL server locally for login system.

### Installation Steps
1. **Open Project:** Launch IntelliJ → *Open* → select project folder.
2. **Configure Resources:**
    - Right-click `res` → *Mark Directory as* → *Resources Root*.  
      ⚠️ Without this step, images and sounds won’t load.
3. **Add JDBC Library:**
    - Press `Ctrl + Alt + Shift + S` (Project Structure).
    - Go to *Libraries* → click `+` → *Java*.
    - Select `mysql-connector-j-x.x.x.jar` from `lib/`.

### Database Configuration
Default setup uses standard XAMPP configuration.  
If you change MySQL credentials, update `src/utils/DatabaseManager.java`:

```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/";
private static final String DB_NAME = "pacman_game";
private static final String USER = "root"; // Default account
private static final String PASS = "";     // Default password
```
## 🤝 Notes

**PacmanGame_BTL** was developed as a final project to strengthen knowledge in:
- Object-Oriented Programming with Java
- UI design using Swing/AWT
- Database access and JDBC connectivity 

## 🙌 Final Words
Thank you for taking the time to explore this project.
I hope it offers you a bit of fun and some useful insights into Java, game development, and software architecture.

There’s still plenty of room for improvement, but I’m glad to share this small effort with you.
Happy coding, and enjoy the game! 🎉
