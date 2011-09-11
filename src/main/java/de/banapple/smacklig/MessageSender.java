package de.banapple.smacklig;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;

/**
 * Entry point for smacky.
 */
public class MessageSender 
{
    public static void main( String[] args ) throws XMPPException
    {
        Options options = getOptions();
        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;
        try {
            cmd = parser.parse( options, args);
        } catch (ParseException e) {
            System.err.println("failed: " + e.getMessage());
            printUsage();
            System.exit(1);
        }
        
        String server = cmd.getOptionValue("server");
        String username = cmd.getOptionValue("username");
        String password = cmd.getOptionValue("password");
        String resource = cmd.getOptionValue("resource");
        String chatRoom = cmd.getOptionValue("mucname");
        String chatPassword = null;
        if (cmd.hasOption("mucpassword")) {
            chatPassword = cmd.getOptionValue("mucpassword");
        }
        String chatNickname = cmd.getOptionValue("mucnickname");
        String message = cmd.getOptionValue("message");
        
        Connection connection = new XMPPConnection(server);
        connection.connect();
        
        /* login */
        connection.login(username, password, resource);       
              
        /* 
         * join a multi-user chat
         * http://www.igniterealtime.org/builds/smack/docs/latest/documentation/extensions/index.html 
         */
        MultiUserChat muc = new MultiUserChat(connection, chatRoom);
        if (chatPassword == null) {
            muc.join(chatNickname);
        } else {
            muc.join(chatNickname, chatPassword);
        }
        
        muc.sendMessage(message);
        
        connection.disconnect();
    }
    
    private static void printUsage()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp( "smacky", getOptions() );        
    }

    @SuppressWarnings("static-access")
    private static Options getOptions()
    {
        Options result = new Options();
        
        Option serverOpt = OptionBuilder
            .withArgName("server")
            .hasArg()
            .isRequired()
            .withDescription("name of the server to connect to")
            .create("server");
        Option usernameOpt = OptionBuilder
            .withArgName("username")
            .hasArg()
            .isRequired()
            .withDescription("username of the sending party")
            .create("username");
        Option passwordOpt = OptionBuilder
            .withArgName("password")
            .hasArg()
            .isRequired()
            .withDescription("password of the sending party")
            .create("password");
        Option resourceOpt = OptionBuilder
            .withArgName("resource")
            .hasArg()
            .isRequired()
            .withDescription("resource of the sending party")
            .create("resource");
        Option messageOpt = OptionBuilder
            .withArgName("message")
            .hasArg()
            .isRequired()
            .withDescription("text to send")
            .create("message");
        Option mucNameOpt = OptionBuilder
            .withArgName("mucname")
            .hasArg()
            .isRequired()
            .withDescription("name of the multi-user chat to send to")
            .create("mucname");
        Option mucPasswordOpt = OptionBuilder
            .withArgName("password")
            .hasArg()
            .withDescription("password for the multi-user chat to send to")
            .create("mucpassword");
        Option mucNicknameOpt = OptionBuilder
            .withArgName("nickname")
            .isRequired()
            .hasArg()
            .withDescription("nickname to be used in the multi-user chat")
            .create("mucnickname");
        
        result.addOption(serverOpt);
        result.addOption(usernameOpt);
        result.addOption(passwordOpt);
        result.addOption(resourceOpt);
        result.addOption(messageOpt);
        result.addOption(mucNameOpt);
        result.addOption(mucPasswordOpt);
        result.addOption(mucNicknameOpt);
        return result;
    }
}
