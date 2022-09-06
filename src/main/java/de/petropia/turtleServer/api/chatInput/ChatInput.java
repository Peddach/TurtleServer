package de.petropia.turtleServer.api.chatInput;

import de.petropia.turtleServer.api.util.SoundUtil;
import de.petropia.turtleServer.server.TurtleServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class ChatInput {

    private final Consumer<String> onInputStr;
    private final Consumer<Integer> onInputInt;
    private final Consumer<Double> onInputDbl;
    private final boolean mustBePositive;
    private final boolean greaterThanNull;
    private final Runnable onCancel;
    private final Player player;

    public ChatInput(ChatInputBuilder builder){
        this.mustBePositive = builder.isMustBePositive();
        this.onCancel = builder.getOnCancel();
        this.onInputStr = builder.getOnInputWithString();
        this.onInputDbl = builder.getOnInputWithDouble();
        this.onInputInt = builder.getOnInputWithInt();
        this.player = builder.getPlayer();
        this.greaterThanNull = builder.getGreaterThanZero();
        TurtleServer.getInstance().getMessageUtil().sendMessage(player, builder.getChatMessage());
        TurtleServer.getInstance().getMessageUtil().sendTitle(player, Component.text("Chateingabe").color(NamedTextColor.GOLD), Component.text("Gib etwas in den Chat ein").color(NamedTextColor.GRAY));
        ChatInputListener.registerChatInput(this);
    }

    /**
     * Cancel the ChatInput
     * @param executeOnCancel if true onCancel get executed if it exists
     */
    public void cancel(boolean executeOnCancel){
        if(executeOnCancel && onCancel != null){
            onCancel.run();
        }
        ChatInputListener.unregisterChatinput(player);
    }

    /**
     * Internal only, for the input from the {@link ChatInputListener}
     * @param input Input as String
     * @return true if input is valid or sucessful
     */
    public boolean input (String input){
        if(input.equalsIgnoreCase("Abbrechen")){
            if(onCancel != null){
                onCancel.run();
            }
            return true;
        }
        if(onInputInt != null){
            if(!testForInteger(input)){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine ganze Zahl an!").color(NamedTextColor.RED));
                SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_NO);
                return false;
            }
            if(mustBePositive){
                if(!testForPositive(convertToInteger(input))){
                    TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Die eingegebene Zahl muss positiv sein!").color(NamedTextColor.RED));                SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_NO);
                    SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_NO);
                    return false;
                }
            }
            if(greaterThanNull){
                if(!testForGreaterThanZero(convertToInteger(input))){
                    TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Die eingegebene Zahl muss größer als 0 sein!").color(NamedTextColor.RED));
                    SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_NO);
                    return false;
                }
            }
            onInputInt.accept(convertToInteger(input));
            if(onInputDbl != null){
                onInputDbl.accept((double) convertToInteger(input));
            }
            return true;
        }
        if(onInputDbl != null){
            if(!testForDouble(input)){
                TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Bitte gib eine gültige Zahl an!").color(NamedTextColor.RED));
                SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_NO);
                return false;
            }
            if(mustBePositive){
                if(!testForPositive(convertToDouble(input))){
                    TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Die eingegebene Zahl muss positiv sein!").color(NamedTextColor.RED));
                    SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_NO);
                    return false;
                }
            }
            if(greaterThanNull){
                if(!testForGreaterThanZero(convertToDouble(input))){
                    TurtleServer.getInstance().getMessageUtil().sendMessage(player, Component.text("Die eingegebene Zahl muss größer als 0 sein!").color(NamedTextColor.RED));
                    SoundUtil.playSound(player, Sound.ENTITY_VILLAGER_NO);
                    return false;
                }
            }
            onInputDbl.accept(convertToDouble(input));
            return true;
        }
        if(onInputStr != null){
            onInputStr.accept(input);
            return true;
        }
        return true;
    }

    /**
     * Test if number is positive
     * @param number Input number
     * @return true if positive
     */
    private boolean testForPositive(double number){
        return (number >= 0);
    }

    /**
     * Test if a number is greater than zero
     * @param number Input number
     * @return true if greater than 0
     */
    private boolean testForGreaterThanZero(double number){
        return number > 0;
    }

    /**
     * Test if String is int
     * @param string String to test
     * @return true if int
     */
    private boolean testForInteger(String string){
        try {
            Integer.parseInt(string);
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    /**
     * Convert a String to a Integer
     * @param string Input String
     * @return Integer
     */
    private int convertToInteger(String string){
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e){
            return 0;   //Won't ever be used if called testForInteger() before
        }
    }

    /**
     * Test if a String is a double
     * @param string Input String
     * @return true if double
     */
    private boolean testForDouble(String string){
        try {
            Long.parseLong(string);
            return true;
        } catch (NumberFormatException exception){
            return false;
        }
    }

    /**
     * Convert a String to a double
     * @param string String input
     * @return double
     */
    private double convertToDouble(String string){
        try{
            return Long.parseLong(string);
        } catch (NumberFormatException exception){
            return 0L; //Won't ever be called if testForLong() is true;
        }
    }

    public Player getPlayer(){
        return player;
    }
    
}
