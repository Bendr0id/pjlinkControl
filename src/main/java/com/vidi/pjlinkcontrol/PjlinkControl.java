package com.vidi.pjlinkcontrol;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

public class PjlinkControl
{
    private static final String HEADER = "PjlinkControl 1.0 (http://github.com/Bendr0id/)\n";
    private static final String USAGE = "[-h] -i <ip> [-p <port>] [-a <authpass>] -c <command>\n";

    private static final int DEFAULT_PORT = 4352;
    private static final String DEFAULT_AUTH_PASSWORD = "JBMIAProjectorLink";

    private Properties params;

    private Socket socket;
    private BufferedReader br;
    private OutputStreamWriter osw;

    private int port;
    private String ip;
    private String command;
    private String authPass;

    public PjlinkControl(String[] args) throws Exception
    {
        loadParamsProperties();

        parseCommandLine(args);

        System.out.println(HEADER);

        sendCommand();

        System.exit(0);
    }

    private void loadParamsProperties() throws IOException
    {
        params = new Properties();
        params.load(ClassLoader.getSystemResourceAsStream("params.properties"));
    }

    private void sendCommand()
    {
        try
        {
            socket = new Socket(ip, port);

            osw = new OutputStreamWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String token = parseToken(br.readLine());

            String request = "";

            if (token.length() > 0)
            {
                request = getRequestHash(token, authPass);
            }

            request += "%1";
            request += command;

            System.out.println("Request:  " + request);

            osw.write(request + "\r");
            osw.flush();

            String response = parseResponse(br.readLine());

            System.out.println("Response: " + response);

            if (command.contains("?") && params.containsKey(command))
            {
                System.out.println("\n"+params.get(command));
            }
        }
        catch (IOException e)
        {
            System.err.println(e.getMessage());
        }
        finally
        {
            if (osw != null)
            {
                try
                {
                    osw.close();
                }
                catch (IOException e)
                {
                }
            }

            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException e)
                {
                }
            }

            if (socket != null)
            {
                try
                {
                    socket.close();
                }
                catch (IOException e)
                {
                }
            }
        }
    }

    private String parseToken(String line)
    {
        String token = "";

        if (line.startsWith("PJLINK 1 "))
        {
            token = line.substring(9);
        }

        return token;
    }

    private String getRequestHash(String token, String authPass)
    {
        try
        {
            return new BigInteger(1, MessageDigest.getInstance("MD5").digest((token + authPass).getBytes("ASCII"))).toString(16);
        }
        catch (NoSuchAlgorithmException e)
        {
        }
        catch (UnsupportedEncodingException e)
        {
        }

        return "";
    }

    private String parseResponse(String response)
    {
        if (response != null)
        {
            if (response.contains(" ERRA"))
            {
                response = "Authentication error.";
            }
            else if (response.endsWith("ERR1"))
            {
                response = "Unknown Command.";
            }
            else if (response.endsWith("ERR2"))
            {
                response = "Wrong Parameter.";
            }
            else if (response.endsWith("ERR3"))
            {
                response = "Device is not responding.";
            }
            else if (response.endsWith("ERR4"))
            {
                response = "Device has internal error.";
            }
            else
            {
                response = response.substring(7);
            }
        }

        return response;
    }

    private void parseCommandLine(String args[]) throws IOException
    {
        Options options = getOptions();

        try
        {
            CommandLineParser cmdParser = new BasicParser();
            CommandLine commandLine = cmdParser.parse(options, args);

            if (commandLine.hasOption('h'))
            {
                printUsage(options);
                System.exit(0);
            }

            ip = commandLine.getOptionValue('i');
            port = Integer.valueOf(commandLine.getOptionValue('p', String.valueOf(DEFAULT_PORT)));
            authPass = commandLine.getOptionValue('a', DEFAULT_AUTH_PASSWORD);
            command = String.join(" ", commandLine.getOptionValues('c'));
        }
        catch (Exception e)
        {
            printUsage(options);
            System.exit(1);
        }
    }

    private Options getOptions()
    {
        Options options = new Options();

        options.addOption(OptionBuilder
            .hasArg(true)
            .withType(String.class)
            .withArgName("ip")
            .withLongOpt("ip")
            .withDescription("The IP of the Pjlink device")
            .isRequired()
            .create('i'));

        options.addOption(OptionBuilder
            .hasArg(true)
            .withType(Integer.class)
            .withArgName("port")
            .withLongOpt("port")
            .withDescription("The Port of the Pjlink device [default: " + DEFAULT_PORT + "]")
            .create('p'));

        options.addOption(OptionBuilder
            .hasArg(true)
            .withType(String.class)
            .withArgName("authpass")
            .withLongOpt("authpass")
            .withDescription("The Password of the Pjlink device [default: " + DEFAULT_AUTH_PASSWORD + "]")
            .create('a'));

        options.addOption(OptionBuilder
            .hasArg(true)
            .withType(String.class)
            .withArgName("command")
            .withLongOpt("command")
            .withDescription("The Commant to send to the Pjlink device")
            .hasOptionalArgs(2)
            .isRequired()
            .create('c'));

        options.addOption(OptionBuilder
            .hasArg(false)
            .withLongOpt("help")
            .withDescription("Prints this help")
            .create('h'));

        return options;
    }

    private void printUsage(Options options) throws IOException
    {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setWidth(140);
        helpFormatter.printHelp(USAGE, HEADER, options, null);

        System.out.println("\nAvailable commands:");

        for (Object key : new TreeSet(params.keySet()))
        {
            System.out.println("\n" + key + " = " + params.get(key));
        }
    }

    public static void main(String[] args) throws Exception
    {
        new PjlinkControl(args);
    }
}