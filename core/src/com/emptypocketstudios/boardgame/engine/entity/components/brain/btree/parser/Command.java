package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser;

import com.badlogic.gdx.utils.Array;

import java.util.HashMap;

public class Command {
    private int line;
    private int indent;
    private String rawText;

    private String command;
    private String name;
    private boolean isCondition;
    private String source = null;

    private HashMap<String, String> args = new HashMap<>();

    public Command parent;
    public Array<Command> subCommands = new Array<>();
    public Array<Command> conditions = new Array<>();

    public Command() {
    }

    public Command(String data, int line) {
        this();
        this.setText(data, line);
    }

    public String getSource() {
        if (source != null) {
            return source;
        }
        if (parent != null) {
            return parent.getSource();
        }
        return null;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isCondition() {
        return isCondition;
    }

    public boolean verifyIndentation() {
        if (indent % 2 != 0) {
            throw new ParseException("Invalid Indent (" + indent + ")", this);
        }
        return true;
    }

    public String getArg(String key) {
        if (args.containsKey(key)) {
            return args.get(key);
        }
        throw new ParseException("Missing required [" + key + "]", this);
    }

    public String getArg(String key, String defaultValue) {
        if (args.containsKey(key)) {
            return args.get(key);
        }
        return defaultValue;
    }

    public long getArgLong(String key) {
        return Long.parseLong(getArg(key));
    }

    public long getArgLong(String key, long defaultValue) {
        if (!args.containsKey(key)) {
            return defaultValue;
        }
        return Long.parseLong(getArg(key));
    }

    public long getArgShort(String key) {
        return Short.parseShort(getArg(key));
    }

    public long getArgShort(String key, short defaultValue) {
        if (!args.containsKey(key)) {
            return defaultValue;
        }
        return Short.parseShort(getArg(key));
    }

    public boolean getArgBoolean(String key) {
        return Boolean.parseBoolean(getArg(key));
    }

    public boolean getArgBoolean(String key, boolean defaultValue) {
        if (!args.containsKey(key)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(getArg(key));
    }

    public float getArgFloat(String keyName) {
        return Float.parseFloat(getArg(keyName));
    }

    public float getArgFloat(String keyName, float defaultValue) {
        if (!args.containsKey(keyName)) {
            return defaultValue;
        }
        return Float.parseFloat(getArg(keyName));
    }

    public void setText(String text, int line) {
        this.line = line;
        this.indent = getIndentation(text, ' ');
        this.rawText = text.substring(indent);
        parseText();
    }

    protected void parseText() {
        //Process Condition
        if (rawText.trim().startsWith("CONDITION")) {
            rawText = rawText.split("CONDITION")[1].trim();
            isCondition = true;
        }
        //Process command
        String[] splitCommand = rawText.split(" ", 2);
        this.command = splitCommand[0];
        if (splitCommand.length == 2) {
            String input = splitCommand[1];
            String[] splits = input.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
            for (int i = 0; i < splits.length; i++) {
                String expression = splits[i].trim();
                String[] parts = expression.split(":", 2);

                try {
                    String key = parts[0].trim();
                    String value = parts[1].trim();
                    if (value.startsWith("\"") && value.endsWith("\"")) {
                        value = value.substring(1, value.length() - 1);
                    }
                    if (key.equalsIgnoreCase("name")) {
                        this.name = value;
                    }
                    this.args.put(key, value);
                } catch (Exception e) {
                    throw new ParseException("Parse Arg Exception (" + expression + ")", this, e);
                }
            }
        }
    }

    private int getIndentation(String data, char indentChar) {
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) != indentChar) {
                return i;
            }
        }
        return 0;
    }

    public int getLine() {
        return line;
    }

    public int getIndent() {
        return indent;
    }

    public String getCommand() {
        return command;
    }

    public String getName() {
        return name;
    }

    public Command sub(int idx) {
        return subCommands.get(idx);
    }

    public int subs() {
        return subCommands.size;
    }

    public HashMap<String, String> getArgs() {
        return args;
    }

    public String getRawText() {
        return rawText;
    }

    public void print(int depth, boolean fullDetails) {
        String base = "";
        for (int i = 0; i < depth; i++) {
            base += "  ";
        }

        if (isCondition) {
            System.out.println(base + "Condition : " + rawText);
        } else {
            System.out.println(base + "Command : " + rawText);
        }
        if (fullDetails) System.out.println(base + " Name : " + name);
        if (fullDetails) System.out.println(base + " Level : " + this.indent);
        if (fullDetails && args.size() > 0) {
            System.out.println(base + " Args : ");
            for (String key : args.keySet()) {
                System.out.println(base + "  " + key + " : " + args.get(key));
            }
        }
        if (conditions.size > 0) {
            if (fullDetails) System.out.println(base + "Conditions:");
            for (Command condition : conditions) {
                condition.print(depth + 1, fullDetails);
            }
        }
        if (subCommands.size > 0) {
            if (fullDetails) System.out.println(base + "Sub Commands :");
            for (Command subCommands : subCommands) {
                subCommands.print(depth + 1, fullDetails);
            }
        }

    }

    public boolean hasArg(String keyName) {
        return args.containsKey(keyName);
    }


}
