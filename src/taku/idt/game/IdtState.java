package taku.idt.game;

public enum IdtState {

    WAIT(true), GAME(false), GAMETRAITRE(false), GAMESUPER(false), END(false);

    private boolean canJoin;
    private static IdtState currentState;

    IdtState(boolean b) {
        this.canJoin = b;
    }

    public boolean canJoin() {
        return canJoin;
    }

    public static void setState(IdtState state) {
        IdtState.currentState = state;
    }

    public static IdtState getState() {
        return IdtState.currentState;
    }

    public static boolean isState(IdtState state) {
        return IdtState.currentState == state;
    }
}
