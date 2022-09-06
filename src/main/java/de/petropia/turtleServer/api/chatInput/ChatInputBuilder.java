package de.petropia.turtleServer.api.chatInput;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ChatInputBuilder {
    protected final Component chatMessage;
    private Consumer<Integer> onInputWithInt;
    private Consumer<String> onInputWithString;
    private Consumer<Double> onInputWithDouble;
    private Runnable onCancel;
    private boolean mustBePositive;
    private Player player;
    private boolean greaterThanZero;

    /**
     * A builder class to create a chat input in an easy way
     * @param chatMessage Message to display in the Chat
     * @param player The player who have to enter smth. in the chat
     */
    public ChatInputBuilder(Component chatMessage, Player player){
        this.chatMessage = chatMessage;
        this.player = player;
    }

    /**
     * Will execute when a player enters a valid integer
     * @param consumer Consumer for a valid integer
     * @return Current instance of {@link ChatInputBuilder}
     */
    public ChatInputBuilder onInputWithInt(Consumer<Integer> consumer){
        this.onInputWithInt = consumer;
        return this;
    }

    /**
     * Will execute when a player enters a String in the chat
     * @param consumer Consumer for a String
     * @return Current instance of {@link ChatInputBuilder}
     */
    public ChatInputBuilder onInputWithString(Consumer<String> consumer){
        this.onInputWithString = consumer;
        return this;
    }

    /**
     * Will execute when a player enters a valid double
     * @param consumer Consumer for a valid double
     * @return Current instance of {@link ChatInputBuilder}
     */
    public ChatInputBuilder onInputWithDouble(Consumer<Double> consumer){
        this.onInputWithDouble = consumer;
        return this;
    }

    /**
     * Set the player for the current {@link ChatInputBuilder} instance
     * @param player Player to
     * @return Current instance of {@link ChatInputBuilder}
     */
    public ChatInputBuilder player(Player player){
        this.player = player;
        return this;
    }

    /**
     * Defines if an Input must be positive. By default its false
     * @param mustBePositive True if iunput must be positive
     * @return Current instance of {@link ChatInputBuilder}
     */
    public ChatInputBuilder mustBePositive(boolean mustBePositive){
        this.mustBePositive = mustBePositive;
        return this;
    }

    /**
     * Defines a runnable which will be executed if the input was canceled
     * @param onCancel {@link Runnable} to run on cancel
     * @return Current instance of {@link ChatInputBuilder}
     */
    public ChatInputBuilder onCancel(Runnable onCancel){
        this.onCancel = onCancel;
        return this;
    }

    /**
     * Defines if the input must be greater than zero. By default its false
     * @param greaterThanZero true if input > 0
     * @return Current instance of {@link ChatInputBuilder}
     */
    public ChatInputBuilder greaterThanZero(boolean greaterThanZero){
        this.greaterThanZero = greaterThanZero;
        return this;
    }

    /**
     * Build the ChatInput and shows is to the player
     */
    public void build(){
        new ChatInput(this);
    }

    public Component getChatMessage() {
        return chatMessage;
    }

    public Consumer<Integer> getOnInputWithInt() {
        return onInputWithInt;
    }

    public Consumer<String> getOnInputWithString() {
        return onInputWithString;
    }

    public Consumer<Double> getOnInputWithDouble() {
        return onInputWithDouble;
    }

    public Runnable getOnCancel() {
        return onCancel;
    }

    public boolean isMustBePositive() {
        return mustBePositive;
    }

    public Player getPlayer(){
        return player;
    }

    public boolean getGreaterThanZero() {
        return greaterThanZero;
    }
}
