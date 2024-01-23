package nz.geek.goodwin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;

/**
 * @author thomas.goodwin
 *///TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    //https://www.wellingtonwater.co.nz/assets/Resources/Drinking-Water/leak-dashboards/Weekly-Leaks-dashboard-13-November-23.pdf
    //https://www.wellingtonwater.co.nz/assets/Weekly-Leaks-dashboard-10th-January-24.png
    //https://www.wellingtonwater.co.nz/assets/Weekly-Leaks-dashboard-10-January-24.pdf

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MMMM-yy");
    private static final DateTimeFormatter ALT_DATE_TIME_FORMATTER_PT1 = DateTimeFormatter.ofPattern("dd");
    private static final DateTimeFormatter ALT_DATE_TIME_FORMATTER_PT2 = DateTimeFormatter.ofPattern("-MMMM-yy");
    private static final String BASE_URL = "https://www.wellingtonwater.co.nz/assets/Resources/Drinking-Water/leak-dashboards/";
    private static final String ALT_BASE_URL = "https://www.wellingtonwater.co.nz/assets/";

    public void downloadAllFiles() {
        LocalDate currentDate = LocalDate.now();
        var endDate = LocalDate.of(2020,1,1);

        while (currentDate.isAfter(endDate)) {
            String format = currentDate.format(DATE_TIME_FORMATTER);
            String fileName1 = "Weekly-Leaks-dashboard-" + format + ".pdf";

            attemptToDownload(BASE_URL, fileName1);

            String format1 = currentDate.format(ALT_DATE_TIME_FORMATTER_PT1);
            String dayOfMonthSuffix = getDayOfMonthSuffix(currentDate.getDayOfMonth());
            String format2 = currentDate.format(ALT_DATE_TIME_FORMATTER_PT2);

            String fileName2 = "Weekly-Leaks-dashboard-" + format1 + dayOfMonthSuffix + format2 + ".png";

            attemptToDownload(ALT_BASE_URL, fileName2);

            currentDate = currentDate.minusDays(1);
        }
    }

    private void attemptToDownload(String altBaseUrl, String fileName2) {
        Unirest.get(altBaseUrl + fileName2).asBytes()
                .ifFailure(httpResponse -> System.out.println(altBaseUrl + fileName2))
                .ifSuccess(httpResponse -> saveFile(httpResponse, fileName2));
    }

    private void saveFile(HttpResponse<byte[]> httpResponse, String fileName) {
        File file = new File("output/" + fileName);

        if(file.exists()) {
            System.out.println("File exists - Bailing");
            return;
        }

        try(FileOutputStream test = new FileOutputStream(file)) {
            test.write(httpResponse.getBody());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        return switch (n % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

    public static void main(String[] args) {
        new Main().downloadAllFiles();
    }
}