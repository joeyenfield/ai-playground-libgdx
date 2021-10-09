package com.emptypocketstudios.boardgame.engine.entity.components.brain.btree.parser;

public class CommandParser {

    public static boolean isValidCommandLine(String text) {
        //Empty line
        String trimed = text.trim();
        if (trimed.length() == 0) {
            return false;
        }
        //Commented line
        if (trimed.charAt(0) == '#') {
            return false;
        }
        if (trimed.charAt(0) == '/') {
            return false;
        }
        return true;
    }

    public static Command getCommand(String data) {
        if (data == null || data.length() == 0) {
            return null;
        }
        String[] commands = data.replace("\t", "    ").split("\n");

        Command rootCommand = new Command();


        Command lastCommand = rootCommand;
        Command lastParent = rootCommand;

        boolean first = true;
        for (int i = 0; i < commands.length; i++) {
            String commandText = commands[i];

            // Allow Ignore Empty Lines
            if (isValidCommandLine(commandText)) {
                if (first) {
                    //Capture root command
                    rootCommand.setText(commands[0], i + 1);
                    first = false;
                } else {
                    Command command = new Command(commandText, i + 1);
                    command.verifyIndentation();
                    try {
                        if (command.getIndent() == lastCommand.getIndent()) {
                            //Same Depth
                            if (command.isCondition()) {
                                // Last command adding condition keeps last command
                                lastCommand.conditions.add(command);
                            } else {
                                // Add subcommand to parent
                                lastParent.subCommands.add(command);
                                command.parent = lastParent;
                                lastCommand = command;
                            }
                        } else if (command.getIndent() > lastCommand.getIndent()) {
                            //Goes Deeper
                            lastParent = lastCommand;

                            if (command.isCondition()) {
                                // Last command adding condition keeps last command
                                lastParent.conditions.add(command);
                            } else {
                                // Add subcommand to parent
                                lastParent.subCommands.add(command);
                            }
                            command.parent = lastParent;
                            lastCommand = command;
                        } else {
                            do{
                                lastParent = lastParent.parent;
                            }while (lastParent.getIndent() >= command.getIndent());
//                            //Gone Shallow
                            if (command.getIndent() > lastCommand.getIndent()) {
                                lastParent = lastCommand;
                            }

                            if (command.isCondition()) {
                                // Last command adding condition keeps last command
                                lastParent.conditions.add(command);
                            } else {
                                // Add subcommand to parent
                                lastParent.subCommands.add(command);
                            }
                            command.parent = lastParent;
                            lastCommand = command;
                        }
                    } catch (Throwable t) {
                        throw new ParseException("Unexpected Failure", command, t);
                    }
                }
            }
        }
        return rootCommand;
    }
}
