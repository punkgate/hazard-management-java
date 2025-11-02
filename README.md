# Hazard Management System

A Java-based desktop application for tracking and managing workplace hazards with real-time analytics and reporting capabilities.

## Features

- **User Authentication**
  - Secure login system with role-based access (Admin/Member)
  - Protected database credentials using properties file

- **Admin Dashboard**
  - Real-time hazard analytics with interactive charts
  - Pie chart visualization of hazard severity distribution
  - Monthly trend analysis with bar charts
  - Member management system
  - CSV export functionality
  - Color-coded hazard severity indicators

- **Member Features**
  - Report new hazards
  - Track personal hazard reports
  - View status updates
  - Color-coded severity levels for better visibility

- **Hazard Tracking**
  - Multiple severity levels:
    - Critical (Dark Red)
    - High (Light Orange)
    - Moderate (Yellow)
    - Low (Green)
  - Status tracking (Pending, In Progress, Resolved)
  - Location-based categorization
  - Detailed description support

## Technology Stack

- **Frontend**: Java Swing for GUI
- **Backend**: Java
- **Database**: MySQL
- **Libraries**:
  - JFreeChart for data visualization
  - MySQL Connector/J for database connectivity

## Setup Instructions

1. **Prerequisites**
   - Java JDK 8 or higher
   - MySQL Server
   - Git

2. **Database Configuration**
   ```bash
   # Copy the template configuration file
   cp config/database.properties.template config/database.properties
   ```
   Edit `config/database.properties` with your database credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/accident_tracker
   db.username=your_username
   db.password=your_password
   ```

3. **Database Setup**
   - Create a MySQL database named `accident_tracker`
   - Import the schema (SQL file provided)

4. **Running the Application**
   ```bash
   # Compile
   javac -cp "lib/*;." -d bin src/db/DatabaseConnection.java src/model/User.java src/ui/*.java src/Main.java

   # Run
   java -cp "bin;lib/*" Main
   ```

## Project Structure

```
├── src/
│   ├── Main.java
│   ├── db/
│   │   └── DatabaseConnection.java
│   ├── model/
│   │   └── User.java
│   └── ui/
│       ├── AdminDashboardFrame.java
│       ├── DashboardFrame.java
│       ├── LoginFrame.java
│       └── MemberReportFrame.java
├── config/
│   ├── database.properties.template
│   └── database.properties (not tracked)
└── lib/
    ├── jfreechart-1.5.4.jar
    ├── jcommon-1.0.24.jar
    └── mysql-connector-j-9.5.0.jar
```

## Security Features

- Database credentials stored in separate properties file
- Properties file excluded from version control
- Password protection for user accounts
- Role-based access control

## Analytics Dashboard

The analytics dashboard provides real-time insights into hazard data:
- Distribution of hazards by severity level (Pie Chart)
- Monthly trends of reported hazards (Bar Chart)
- Interactive charts with tooltips and legends
- Auto-refresh functionality when data changes

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Author

- [punkgate](https://github.com/punkgate)