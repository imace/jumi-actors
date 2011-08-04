// Copyright © 2011, Esko Luontola <www.orfjackal.net>
// This software is released under the Apache License 2.0.
// The license text is at http://www.apache.org/licenses/LICENSE-2.0

package fi.jumi.codegenerator;

import fi.jumi.codegenerator.dummy.DummyListenerFactory;
import org.apache.commons.io.IOUtils;
import org.junit.*;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class EventStubGeneratorTest {

    private EventStubGenerator generator;

    @Before
    public void setUp() {
        generator = new EventStubGenerator();
        generator.setListenerType(DummyListener.class);
        generator.setTargetPackage("fi.jumi.codegenerator.dummy");
        generator.setEventInterface(MyEvent.class.getName());
        generator.setFactoryInterface(MyListenerFactory.class.getName());
        generator.setSenderInterface(MyMessageSender.class.getName());
    }

    @Test
    public void factory_advertises_its_listener_type() {
        DummyListenerFactory factory = new DummyListenerFactory();

        assertEquals(DummyListener.class, factory.getType());
    }

    @Test
    public void stubs_forward_events_from_frontend_to_backend() {
        DummyListener target = mock(DummyListener.class);
        DummyListenerFactory factory = new DummyListenerFactory();
        MyMessageSender<MyEvent<DummyListener>> backend = factory.newBackend(target);
        DummyListener frontend = factory.newFrontend(backend);

        frontend.onSomething("foo", "bar");

        verify(target).onSomething("foo", "bar");
    }

    @Test
    public void generates_factory_class() throws IOException {
        assertClassEquals("fi/jumi/codegenerator/dummy/DummyListenerFactory.java", generator.getFactory());
    }

    @Test
    public void generates_frontend_class() throws IOException {
        assertClassEquals("fi/jumi/codegenerator/dummy/DummyListenerToEvent.java", generator.getFrontend());
    }

    @Test
    public void generates_backend_class() throws IOException {
        assertClassEquals("fi/jumi/codegenerator/dummy/EventToDummyListener.java", generator.getBackend());
    }

    @Test
    public void generates_event_classes() throws IOException {
        assertClassEquals("fi/jumi/codegenerator/dummy/OnSomethingEvent.java", generator.getEvent());
    }

    private static void assertClassEquals(String expectedPath, GeneratedClass actual) throws IOException {
        assertEquals(expectedPath, actual.path);
        assertEquals(readFile(expectedPath), actual.source);
    }

    private static String readFile(String resource) throws IOException {
        return IOUtils.toString(EventStubGenerator.class.getClassLoader().getResourceAsStream(resource));
    }
}
