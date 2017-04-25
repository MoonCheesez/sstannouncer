package sstinc.sstannouncer;

import android.support.test.runner.AndroidJUnit4;

import com.sst.anouncements.android.AndroidEventAdaptor;
import com.sst.anouncements.event.Event;
import com.sst.anouncements.event.EventController;
import com.sst.anouncements.event.EventHandler;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class EventSystemTest
{
    private static boolean eventStatus;

    @Test
    public void testEventController()
    {

        EventController eventController = new EventController();
        Event testEvent = new Event("test.event", new Date(0), "Hello");
        eventStatus = false;

        //Test Listen
        eventController.listen(this.toString(), testEvent.getIdentifier(), new EventHandler() {
            @Override
            public void handle(Event event) {
                //Test Passing of Evnet
                assertEquals(event.getIdentifier(), "test.event");
                assertEquals(event.getTimeStamp(), new Date(0));
                assertEquals(event.getData(), "Hello");
                eventStatus = true;
            }
        });
        //Test Raise
        eventController.raise(testEvent);
        assertTrue(eventStatus);

        //Test Unlisten
        eventController.unlisten(this.toString(), "test.event");
        eventStatus = false;
        eventController.raise(testEvent);
        assertFalse(eventStatus);

        //Test Wildcard
        eventStatus = false;
        eventController.listen(this.toString(), "*", new EventHandler() {
            @Override
            public void handle(Event event) {
                eventStatus = true;
            }
        });
        eventController.raise(testEvent);
        assertTrue(eventStatus);
    }

    @Test
    public void testAndroidEventAdaptor()
    {
        EventController localEventController = new EventController();
        EventController remoteEventController = new EventController();
        AndroidEventAdaptor localEventAdaptor = new AndroidEventAdaptor(localEventController);
        AndroidEventAdaptor remoteEventAdaptor = new AndroidEventAdaptor(remoteEventController);
        Event testEvent = new Event("test.event", new Date(0), "Hello");
        eventStatus = false;

        //Connection Adaptors
        localEventAdaptor.connect(remoteEventAdaptor.getLocalMessenger());

        //Transmission of Event
        localEventController.listen(this.toString(), "test.event", new EventHandler() {
            @Override
            public void handle(Event event) {
                //Test Passing of Event
                assertEquals(event.getIdentifier(), "test.event");
                assertEquals(event.getTimeStamp(), new Date(0));
                assertEquals(event.getData(), "Hello");
                eventStatus = true;
            }
        });

        remoteEventController.raise(testEvent);

        try
        {
            Thread.sleep(1); //Event Processing Must be done within 1ms
        }catch(InterruptedException exp){};

        assertTrue(eventStatus);

        localEventAdaptor.disconnect();
        remoteEventAdaptor.disconnect();
    }

}
