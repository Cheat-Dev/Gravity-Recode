package team.gravityrecode.clientbase.impl.util.util.render.animations;

public enum Direction {
    FORWARDS,
    BACKWARDS;

    public Direction setOppositeDirection() {
        if (this == Direction.FORWARDS) {
            return Direction.BACKWARDS;
        } else return Direction.FORWARDS;
    }

}
