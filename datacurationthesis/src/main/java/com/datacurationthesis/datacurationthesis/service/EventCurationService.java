import com.datacurationthesis.datacurationthesis.entity.Event;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;


public class EventCurationService {

    private static final Pattern NON_NUMERIC_CHARACTERS_PATTERN = Pattern.compile("[^\\d\\-\\s]");
    private static final Pattern MULTIPLE_SPACES_PATTERN = Pattern.compile("\\s{2,}");
    private static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    private static final SimpleDateFormat STANDARD_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Event cleanEvent(Event event) {
        // Normalize the price range
        if (event.getPriceRange() != null) {
            String priceRange = event.getPriceRange();
            priceRange = NON_NUMERIC_CHARACTERS_PATTERN.matcher(priceRange).replaceAll(""); // Remove non-numeric characters
            priceRange = MULTIPLE_SPACES_PATTERN.matcher(priceRange).replaceAll(" ");      // Remove extra spaces
            event.setPriceRange(priceRange.trim());
        }

        // Standardize the start date
        if (event.getDateEvent() != null) {
            String standardizedStartDate = formatDate(event.getDateEvent());
            event.setStartDate(standardizedStartDate);
        }

        // Standardize the end date
        if (event.getEndDate() != null) {
            String standardizedEndDate = formatDate(event.getEndDate());
            event.setEndDate(standardizedEndDate);
        }

        return event;
    }

    // Helper method to standardize date format
    private String formatDate(String date) {
        try {
            return STANDARD_DATE_FORMAT.format(INPUT_DATE_FORMAT.parse(date));
        } catch (ParseException e) {
            System.err.println("Error parsing date: " + date + ". Using original value.");
            return date; // Return the original value if parsing fails
        }
    }
}
