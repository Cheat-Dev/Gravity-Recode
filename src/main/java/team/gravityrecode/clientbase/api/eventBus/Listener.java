package team.gravityrecode.clientbase.api.eventBus;

@FunctionalInterface
public interface Listener<Event> {
    void invoke(Event event);
}