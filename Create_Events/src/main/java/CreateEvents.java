// Import Nylas packages
import com.nylas.NylasClient;

// Import DotEnv to handle .env files
import com.nylas.models.*;
import io.github.cdimascio.dotenv.Dotenv;

// Import Java packages
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class create_calendar_events {
    public static void main(String[] args) throws NylasSdkTimeoutError, NylasApiError {
        // Load the .env file
        Dotenv dotenv = Dotenv.load();
        // Initialize the Nylas client
        NylasClient nylas = new NylasClient.Builder(dotenv.get("V3_TOKEN")).apiUri(dotenv.get("NYLAS_API_SERVER")).build();
        // Get today's date
        LocalDate today = LocalDate.now();
        // Set time. As we're using UTC we need to add the hours in difference
        // from our own Timezone
        Instant sixPmUtc = today.atTime(13, 0).toInstant(ZoneOffset.UTC);
        // Set the date and time for the event. We add 30 minutes to the starting time
        Instant sixPmUtcPlus = sixPmUtc.plus(30, ChronoUnit.MINUTES);
        // Get the Date and Time as Epoch
        long startTime = sixPmUtc.getEpochSecond();
        long endTime = sixPmUtcPlus.getEpochSecond();
        // Define title, location and description of the event
        String title = "Let's learn some Nylas JAVA SDK!";
        String location = "Blag's Den!";
        String description = "Using the Nylas API with the Java SDK is easy. Come join us!\"";
        // Create the timespan for the event
        CreateEventRequest.When.Timespan timespan = new CreateEventRequest.When.Timespan.Builder(Math.toIntExact(startTime), Math.toIntExact(endTime)).build();
        // Create the list of participants
        List<CreateEventRequest.Participant> participants = Collections.singletonList(new CreateEventRequest.Participant(dotenv.get("CALENDAR_ID"), ParticipantStatus.NOREPLY, "", "", ""));
        // Build the event details
        CreateEventRequest createEventRequest = new CreateEventRequest.Builder(timespan)
                .participants(participants)
                .title(title)
                .location(location)
                .description(description)
                .build();
        // Build the event parameters, in this case, the Calendar Id
        CreateEventQueryParams createEventQueryParams = new CreateEventQueryParams.Builder(dotenv.get("CALENDAR_ID")).build();
        // Create the event itself
        Event event = nylas.events().create(dotenv.get("CALENDAR_ID"), createEventRequest, createEventQueryParams).getData();
        // Did we have a problem or could we create the event?
        if(!event.getId().equals("")){
            System.out.println("Event created successfully");
        }else{
            System.out.println("There was an error creating the event");
        }
    }
}
