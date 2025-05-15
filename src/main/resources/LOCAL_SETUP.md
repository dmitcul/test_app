# Local PostgreSQL Setup for Running Without Docker

This guide provides instructions on how to set up a local PostgreSQL instance with the correct credentials for running the application without Docker.

## Prerequisites

- PostgreSQL installed on your local machine
- Basic knowledge of PostgreSQL administration

## Setup Steps

1. **Install PostgreSQL** if you haven't already:
   - Download and install from [PostgreSQL official website](https://www.postgresql.org/download/)
   - During installation, you'll be prompted to set a password for the default 'postgres' user
   - **Important**: Make sure to check the option to install the PostgreSQL command-line tools
   - By default, PostgreSQL is installed in `C:\Program Files\PostgreSQL\<version>` on Windows

2. **Ensure PostgreSQL commands are available**:

   If you get an error like `psql: command not found` or `The term 'psql' is not recognized`, you need to add PostgreSQL's bin directory to your system PATH:

   **For Windows**:
   - Right-click on "This PC" or "My Computer" and select "Properties"
   - Click on "Advanced system settings"
   - Click on "Environment Variables"
   - Under "System variables", find the "Path" variable, select it and click "Edit"
   - Click "New" and add the path to PostgreSQL bin directory (typically `C:\Program Files\PostgreSQL\<version>\bin`)
   - Click "OK" on all dialogs to save the changes
   - Restart your terminal/PowerShell for changes to take effect

   **Alternatively**, you can run PostgreSQL commands directly using the full path:
   ```powershell
   & 'C:\Program Files\PostgreSQL\<version>\bin\psql.exe' -U postgres
   ```

   **Verify installation** by running:
   ```powershell
   psql --version
   ```

3. **Create a new database and user**:

   Open a terminal/command prompt and connect to PostgreSQL:

   ```bash
   # For Windows
   psql -U postgres

   # For Linux/MacOS
   sudo -u postgres psql
   ```

   Then execute the following SQL commands:

   ```sql
   -- Create the database
   CREATE DATABASE db;

   -- Create the user with password
   CREATE USER "user" WITH ENCRYPTED PASSWORD 'pass';

   -- Grant privileges to the user
   GRANT ALL PRIVILEGES ON DATABASE db TO "user";

   -- Connect to the new database
   \c db

   -- Grant schema privileges
   GRANT ALL ON SCHEMA public TO "user";
   ```

4. **Initialize the database**:

   Run the initialization script:

   ```bash
   # For Windows
   psql -U user -d db -f src\main\resources\db\init.sql

   # For Linux/MacOS
   psql -U user -d db -f src/main/resources/db/init.sql
   ```

5. **Load test data** (optional):

   ```bash
   # For Windows
   psql -U user -d db -f src\main\resources\db\test_data.sql

   # For Linux/MacOS
   psql -U user -d db -f src/main/resources/db/test_data.sql
   ```

6. **Run the application**:

   Now you can run the application directly without Docker, and it will connect to your local PostgreSQL instance with the credentials specified in `application.properties`.

## Troubleshooting

If you encounter connection issues:

1. **Check PostgreSQL service is running**:
   - Windows: Open Services app and check if PostgreSQL service is running
   - Linux: `sudo systemctl status postgresql`
   - MacOS: `brew services list` (if installed via Homebrew)

2. **Verify connection settings**:
   - Ensure PostgreSQL is listening on localhost:5432
   - Check pg_hba.conf file to ensure it allows local connections with password authentication

3. **Test connection manually**:
   ```bash
   psql -U user -d db -h localhost
   ```
   When prompted, enter the password 'pass'
