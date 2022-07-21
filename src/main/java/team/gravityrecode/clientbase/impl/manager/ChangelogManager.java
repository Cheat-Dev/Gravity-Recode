package team.gravityrecode.clientbase.impl.manager;

import lombok.Getter;
import team.gravityrecode.clientbase.api.manager.Manager;
import team.gravityrecode.clientbase.impl.mainmenu.changelog.Changelog;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class ChangelogManager {

    ArrayList<Changelog> changelogs = new ArrayList<>();

    public void init(){
        changelogs.addAll(Stream.of(
             new Changelog("New Watchdog Bhop", Changelog.ChangeType.ADDED),
                new Changelog("New Client Base", Changelog.ChangeType.ADDED),
                new Changelog("Client optimizations", Changelog.ChangeType.FIXED),
                new Changelog("new yayayayayayayayayayayayayaya", Changelog.ChangeType.ADDED)
                ).collect(Collectors.toList()));
    }

}
