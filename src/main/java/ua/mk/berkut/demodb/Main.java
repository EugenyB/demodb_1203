package ua.mk.berkut.demodb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    Connection connection;

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        Properties props = new Properties();
        props.setProperty("user", "eugeny");
        props.setProperty("password", "123");
        props.setProperty("useUnicode", "true");
        props.setProperty("characterEncoding", "utf8");
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/library", props);
            work();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void work() {
        int m;
        while((m=menu())!=0) {
            switch (m) {
                case 1:
                    showAllAuthors(); break;
                case 2:
                    showOldest(); break;
                case 3:
                    addAuthor(); break;
            }
        }
    }

    private void addAuthor() {
        Scanner in = new Scanner(System.in);
        System.out.println("Имя:");
        String name = in.nextLine();
        System.out.println("Год:");
        int year = in.nextInt();

        Author author = new Author(0, name, year);
        addToDb(author);
    }

    private void addToDb(Author author) {
        try (PreparedStatement preparedStatement = connection.prepareStatement("insert into author (name, year) VALUES (?, ?)")) {
            preparedStatement.setString(1, author.getName());
            preparedStatement.setInt(2, author.getYear());
            preparedStatement.executeUpdate();
            System.out.println("Ok!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showOldest() {
        List<Author> authors = findOldest();
        printAuthors(authors);
    }

    private List<Author> findOldest() {
        List<Author> authors = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement("select * from author where year < ?")) {
            preparedStatement.setInt(1, 1900);
            ResultSet rs = preparedStatement.executeQuery();
            processRS(authors, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    private void processRS(List<Author> authors, ResultSet rs) throws SQLException {
        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");
            int year = rs.getInt("year");
            Author author = new Author(id, name, year);
            authors.add(author);
        }
    }

    private void showAllAuthors() {
        List<Author> authors = findAllAuthors();
        printAuthors(authors);
    }

    private List<Author> findAllAuthors() {
        List<Author> authors = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery("select * from author");
            processRS(authors, rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return authors;
    }

    private void printAuthors(List<Author> authors) {
        authors.forEach(System.out::println);
//        for (Author author : authors) {
//            System.out.println(author);
//        }
    }

    int menu() {
        System.out.println("1. Show All Authors");
        System.out.println("2. Show Old Authors");
        System.out.println("3. Add Author");
        System.out.println("0. Exit");
        return new Scanner(System.in).nextInt();
    }

    public double f(double x) {
        return x;
    }
}
