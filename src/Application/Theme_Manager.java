package Application;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Theme_Manager {
    private static final String FILE_PATH = "theme.config";

    public static void saveTheme(Table_Styler.CalendarTheme theme) {
        try (PrintWriter out = new PrintWriter(FILE_PATH)) {
            out.println(theme.name());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Table_Styler.CalendarTheme loadTheme() {
        try (Scanner scanner = new Scanner(new File(FILE_PATH))) {
            if (scanner.hasNextLine()) {
                return Table_Styler.CalendarTheme.valueOf(scanner.nextLine());
            }
        } catch (Exception ignored) {}
        return Table_Styler.CalendarTheme.DEFAULT; // default
    }
}

