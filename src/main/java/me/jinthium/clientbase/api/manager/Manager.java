package me.jinthium.clientbase.api.manager;

import me.jinthium.clientbase.api.moduleBase.Module;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Manager<T> {
    private final LinkedHashMap<Class<?>, T> objects = new LinkedHashMap<>();

    public void add(T object) {
        this.objects.put(object.getClass(), object);
    }

    public void addAll(Collection<T> objects) {
        objects.forEach(object -> {
            this.objects.put(object.getClass(), object);
        });
    }

    @SafeVarargs
    public final void addAll(T ... objects) {
        for(int i = 0; i < objects.length; i++){
            this.objects.put(Arrays.asList(objects).get(i).getClass(), Arrays.asList(objects).get(i));
        }
    }

    public LinkedHashMap<Class<?>, T> getObjects() {
        return this.objects;
    }

    public Set<T> getIf(Predicate<T> predicate) {
        return this.objects.values().stream().filter(predicate).collect(Collectors.toSet());
    }

    public void removeIf(Predicate<T> predicate) {
        this.objects.values().removeIf(predicate);
    }
}