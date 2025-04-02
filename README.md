# Library Management System (OOP + JavaFX)

## Features
The system supports two types of users: **Admin** and **Borrower**, each with distinct features.

### Admin Features
- Add, update, and delete books.
- Manage library members (add, update, delete).
- View and manage borrowing records.
- Generate QR codes for books to streamline borrowing and returning processes.
- Add books using ISBN codes and fetch book details automatically via the Google Books API.

### Borrower Features
- Search for books.
- Borrow books using QR codes.
- Return books.
- Log in using QR codes for quick access.
- View borrowing history.
- Explore detailed book information retrieved from the Google Books API.

## Technologies Used
This project utilizes the following technologies:
- **Java**: The core programming language for implementing the system's logic.
- **JavaFX**: For building the graphical user interface.
- **MySQL**: A robust relational database for storing and managing library data.
- **ZXing (Zebra Crossing)**: A library for generating and scanning QR codes.
- **Maven**: For managing dependencies and building the project.
- **CSS**: For styling the JavaFX application.
- **FXML**: For designing the user interface layout.
- **Git**: For version control and collaboration.

## Google Books API Integration

The Library Management System leverages the Google Books API to enhance the user experience by providing detailed book information. Below are the key features and implementation details:

### Features
- **Automatic Book Details Retrieval**: Admins can add books using ISBN-10 or ISBN-13 codes, and the system fetches the book's title, author, publisher, and other details automatically.
- **Enhanced Search**: Borrowers can explore detailed book information, including descriptions and cover images, retrieved from the API.

### Implementation
1. **API Key**: Obtain an API key from the [Google Cloud Console](https://console.cloud.google.com/).
2. **Configuration**: Add the API key to the `src\main\java\utils\GoogleBooksAPI.java` file.
3. **API Request**: The system sends HTTP requests to the Google Books API endpoint to fetch book details.
4. **Error Handling**: If the API request fails or the ISBN code is invalid, the system displays an appropriate error message.

### Example Usage
- **Adding a Book**:  
    Admin enters an ISBN code, and the system automatically populates the book details using the API.
- **Viewing Book Details**:  
    Borrowers can view detailed information, including the book's description and cover image, in the application.

For more information about the Google Books API, visit the [official documentation](https://developers.google.com/books/docs/v1/getting_started).

## Prerequisites
Before running the project, ensure you have the following installed and configured:

- **Java Development Kit (JDK)**: Version 2222 or higher.
- **JavaFX SDK**: Required for building and running JavaFX applications.
- **MySQL Server**: For managing the library's database.
- **Maven**: For dependency management and project building.
- **Git**: For cloning the repository and version control.


## How to Run

To successfully run the Library Management System, follow these detailed steps:

1. **Clone the Repository**:  
    Clone the project repository to your local machine using Git:  
    ```bash
    git clone https://github.com/TranKien2005/LibaryManagerment_OOP_JavaFX_project.git
    ```
2. **Open the Project in Your IDE**:  
    - Open your preferred Java IDE (e.g., IntelliJ IDEA, Eclipse, or NetBeans).  
    - Import the project as a Maven project by selecting the `pom.xml` file during the import process.  

4. **Set Up the Database**:  
    - Install and configure MySQL Server on your machine.  
    - Create a database for the Library Management System by executing the `test.sql` file provided in the repository. You can do this using a MySQL client or command-line tool:  
      ```bash
      mysql -u <your-username> -p < test.sql
      ```  
    - Update the database connection settings in the `src\main\java\DAO\DatabaseConnection.java` file with your MySQL credentials, including the username and password specified in the file.

5. **Install Maven Dependencies**:  
    Ensure Maven is installed and configured on your system. Run the following command in the project directory to download and install all required dependencies:  
    ```bash
    mvn clean install
    ```

6. **Run the Application**:  
    - Locate the `Main` class in the `src` folder.  
    - Run the `Main` class from your IDE to start the application.

7. **Test the Application**:  
    - Log in as an Admin using the default credentials:  
      - **Username**: `1`  
      - **Password**: `1`  
    - Log in as a Borrower using the default credentials:  
      - **Username**: `2`  
      - **Password**: `2`  
    - Explore the features to ensure everything is functioning as expected.

8. **Troubleshooting**:  
    - If you encounter issues, check the logs for error messages.  
    - Verify that all dependencies are correctly installed and configured.  
    - Ensure the database server is running and accessible.

By following these steps, you should be able to successfully run the Library Management System on your local machine.

## License
This project does not currently have a license. If you plan to use or contribute to this project, please contact the repository owner for more information.

## Contributing
Contributions are welcome! Feel free to fork the repository and submit a pull request. For any questions or discussions, you can contact me at [ttk08112005@gmail.com](mailto:ttk08112005@gmail.com).

## Testing with Sample Data

To test the feature of adding books using ISBN-10 codes and Google Books API, the repository includes sample files:

- **`book.txt`**: Contains a list of ISBN-10 codes for testing.
- **`testBook.txt`**: Another sample file with additional ISBN-10 codes.

You can use these files to verify the functionality of adding books by importing the ISBN-10 codes and fetching book details from the Google Books API.

