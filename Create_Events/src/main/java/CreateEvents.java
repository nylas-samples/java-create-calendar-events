//Import Java Utilities
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

// Import Nylas Packages
import com.nylas.RequestFailedException;
import com.nylas.NylasAccount;
import com.nylas.NylasClient;
import com.nylas.Event;
import com.nylas.Participant;

//Import DotEnv to handle .env files
import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvException;

public class CreateEvents {
    public static void main(String[] args) throws RequestFailedException, IOException {
        Dotenv dotenv = Dotenv.load();
        // Create the client object
        NylasClient client = new NylasClient();
        // Connect it to Nylas using the Access Token from the .env file
        NylasAccount account = client.account(dotenv.get("ACCESS_TOKEN"));

        // Get today's date
        LocalDate today = LocalDate.now();
        // Set time. As we're using UTC we need to add the hours in difference
        // from our own Timezone
        Instant sixPmUtc = today.atTime(13, 0).toInstant(ZoneOffset.UTC);
        // Set the date and time for the event. We add 30 minutes to the starting time
        Event event = new Event(dotenv.get("CALENDAR_ID"),
                                new Event.Timespan(sixPmUtc,
                                        sixPmUtc.plus(30, ChronoUnit.MINUTES)));
        // Set Title, Location and Description
        event.setTitle("Let's learn some Nylas JAVA SDK!");
        event.setLocation("Blag's Den!");
        event.setDescription("Using the Nylas API with the Java SDK is easy. Come join us!");

        // Add participants
        event.setParticipants(
            Arrays.asList(new Participant("alvaro.t@nylas.com").name("Blag"))
        );

        // Create the event. Are we notifying participants? Yes.
        Event event_created = account.events().create(event, true);
        if(event_created.getId() != null){
            System.out.println("Event created successfully");
        }else{
            System.out.println("There was an error creating the event");
        }

    }
}
