/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lsql;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.BasicUnmodifiedTokenAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.*;
import java.io.*;
import java.util.*;

/**
 *
 * @author jte
 */
public class LParser
{
    LSql lsql;
    public LParser(LSql newLsql)
    {
        lsql = newLsql;
    }
 
    public void parseArguments(String[] args) throws IOException, ParseException
    {
    //-v command says that it is verbose
		for (int i = 0; i < args.length; i++) {
			if ("-v".equalsIgnoreCase(args[i]) || "-verbose".equalsIgnoreCase(args[i])) {
				lsql.setVerbose(true);
			}
		}

		//-o command says that it is verbose
		for (int i = 0; i < args.length; i++) {
			if ("-o".equalsIgnoreCase(args[i]) || "-optimize".equalsIgnoreCase(args[i])) {
				lsql.setOptimize(true);
			}
		}

		//look for the -i command, pointing to the index
		for (int i = 0; i < args.length; i++) {
			if ("-i".equalsIgnoreCase(args[i])) {
				lsql.setIndex(args[i + 1]);
			}
		}

		//look for the -c command, pointing to the command
		for (int i = 0; i < args.length; i++) {
			if ("-c".equalsIgnoreCase(args[i])) {
				lsql.setCommand(args[i + 1]);
			}
		}


		for (int i = 0; i < args.length; i++) {
			if ("-d".equalsIgnoreCase(args[i])) {
				lsql.setDisplay(true);
			}
		}


		for (int i = 0; i < args.length; i++) {
			if ("-f".equalsIgnoreCase(args[i])) {
				lsql.setFile(args[i + 1]);
			}
		}


		for (int i = 0; i < args.length; i++) {
			if ("-t".equalsIgnoreCase(args[i])) {
				lsql.setTop(new Integer(args[i + 1]));
			}
		}


		for (int i = 0; i < args.length; i++) {
			if ("-help".equalsIgnoreCase(args[i]) || "--help".equalsIgnoreCase(args[i]) || "help".equalsIgnoreCase(args[i])) {
				lsql.setShowHelp(true);
			}
		}

        for (int i = 0; i < args.length; i++) {
			if ("-l".equalsIgnoreCase(args[i])) {
				lsql.setHorizontal(true);
			}
		}

        for (int i = 0; i < args.length; i++) {
			if ("-e".equalsIgnoreCase(args[i])) {
				lsql.setExport(args[i + 1]);
			}
		}

		for (int i = 0; i < args.length; i++) {
			if ("-x".equalsIgnoreCase(args[i])) {
				lsql.setInteractive(true);
			}
		}

        for (int i = 0; i < args.length; i++) {
			if ("-s".equalsIgnoreCase(args[i])) {
				lsql.setScreenWidth(new Integer(args[i + 1]));
			}
		}

        //we have all of our informatino, now lets do stuff...

        if (lsql.getShowHelp())
        {
            lsql.printHelp();
        }
		if (lsql.getVerbose())
        {
			LUtilities.log("Using Lucene index: " + lsql.getIndex());		//parse the command
            lsql.printLuceneVersion();
		}
        if (lsql.getDisplay())
        {
			lsql.printInformation();
		}
		if (lsql.getCommand() != null)
        {
			parseCommand(lsql.getCommand());
		}
		if (lsql.getFile() != null)
        {
			parseFile(lsql.getFile());
		}
        if (lsql.getOptimize())
        {
			lsql.getLWrapper().optimize();
		}
        if (lsql.getInteractive())
        {
            lsql.interactive();
        }
    }

    //This command figures out what command to execute
	public void parseCommand(String command) throws IOException, ParseException {

        //Do we have a valid index?
        if (!(new File(lsql.getIndex()).isDirectory()))
        {
            LUtilities.log("A valid Lucene index has not yet been registered.");
            return;
        }

        //check to see if there is a lock on the index
        LUtilities.checkLuceneLock(lsql.getIndex());

        //replace $variables with environment variables
        command =  LUtilities.insertEnvVariables(command);

        //LUtilities.log
        //if (!lsql.running_as_interactive)
            LUtilities.log("Executing command: " + command);

        String[] words = command.split("\\s");

		//if there is no where keyword... add a wildcard where
        //TODO: We don't always want this... for the del command esp.
		if (!command.toUpperCase().contains("WHERE")) {
			command = command + " WHERE +*:*";
		}

        //our variables we will be using
        String[] editFields;
        String query;

        //parse the command
		int begin = command.toUpperCase().indexOf(words[0]) + (words[0].length() + 1);
		int end = command.toUpperCase().indexOf("WHERE");
		String fields = command.substring(begin, end).trim().replace("'", "");
		begin = command.toUpperCase().indexOf("WHERE") + 5;
		end = command.length();
		query = command.substring(begin, end).trim().replace("'", "");
        editFields = fields.split(",");

        //LUtilities.log what is going on
		if (lsql.getVerbose()) LUtilities.log("Grabbing field(s): " + fields);
		if (lsql.getVerbose()) LUtilities.log("Lucene query: " + query);

		//select
		if (words[0].equalsIgnoreCase("select")) {
			lsql.doSelect(editFields, query);
		}

		//update
        else if (words[0].equalsIgnoreCase("update")) {
			lsql.doUpdate(editFields, query);
		}

		//delete
		else if (words[0].equalsIgnoreCase("delete")) {
			lsql.doDelete(editFields, query);
		}

		//add
		else if (words[0].equalsIgnoreCase("add")) {
			lsql.doAdd(editFields, query);
		}

		//remove
		else if (words[0].equalsIgnoreCase("remove")) {
			lsql.doRemove(editFields, query);
		}

        //remove
		else if (words[0].equalsIgnoreCase("insert")) {
			lsql.doInsert(editFields, query);
		}


        else
            LUtilities.log("Unknown command syntax!");
	}



    public void parseFile(String file) throws IOException, ParseException {
		try {
			File f = new File(file);
			if (!f.exists()) {
				//it does not exist, try appending the cd
				f = new File(new File(".").getCanonicalPath() + "/" + file);
				file = f.getAbsolutePath();
				if (!f.exists()) {
					LUtilities.log("The file " + file + " does not exist.");
				}
				return;
			}
			FileInputStream fstream = new FileInputStream(file);
			DataInputStream in = new DataInputStream(fstream);


			String strLine;

			//Go through line by line
			while ((strLine = in.readLine()) != null) {
				//If its empty or a comment, continue
				if (strLine.trim().isEmpty() || strLine.startsWith("--")) {
					continue;				//the last line should equal a semi colon
				//eventually, we want to be able to
				}
				if (strLine.trim().charAt(strLine.length() - 1) != ';') {

					LUtilities.log("Error on this line: " + strLine + " -- " + "No semi-colon at the end of line.");
					continue;
				}

				strLine.replace(";", "");

				parseCommand(strLine);

			}
		}
        catch (IOException ex) {
            throw ex;
        } finally {

        }
	}

    public boolean parseInteractiveCommand(String xcommand) throws IOException, ParseException
    {
        boolean parsed = false;
        String[] xcommands = xcommand.split(" ");
        if (xcommands[0].equalsIgnoreCase("help"))
        {
            System.out.println("LSql Interactive Help");
            System.out.println("");
            System.out.println("INDEX       Changes the location to the Lucene index");
            System.out.println("TOP         Limits the number of documents to return on a SELECT");
            System.out.println("OPTIMIXE    Optimizes the index");
            System.out.println("INFO        Displays information about the Lucene index");
            System.out.println("HORIZONTAL  Horizontal dispaly mode in SELECT");
            System.out.println("VERTICAL    Vertical display mode in SELECT");
            System.out.println("WIDTH       Changes the width of the terminal");
            System.out.println("EXPORT      Exports all SELECT commands to the specified file. Turn off   ");
            System.out.println("            with EXPORT OFF");
            System.out.println("HELP        Prints this message");
            System.out.println("");
            parsed = true;
        }

        if (xcommands[0].equalsIgnoreCase("optimize"))
        {
            lsql.getLWrapper().optimize();
            parsed = true;
        }

        if (xcommands[0].equalsIgnoreCase("top"))
        {
            if (xcommands.length > 1)
            {
                if (xcommands[1].equalsIgnoreCase("all"))
                {
                    lsql.setTop(0);
                    LUtilities.log("All documents will be displayed.");
                }
                else
                {

                    lsql.setTop(new Integer(xcommands[1]));
                    LUtilities.log("The top " + lsql.getTop() + " documents will be displayed.");
                }
                parsed = true;
            }
        }

        if (xcommands[0].equalsIgnoreCase("width"))
        {
            if (xcommands.length > 1)
            {
                lsql.screen_width = new Integer(xcommands[1]);
                LUtilities.log("Screen width changed to " + lsql.screen_width + " characters.");

                parsed = true;
            }
        }

        if (xcommands[0].equalsIgnoreCase("index"))
        {
            if (xcommands.length > 1)
            {
                lsql.setIndex(xcommands[1]);
                LUtilities.log("Lucene index set to " + lsql.getIndex());
                parsed = true;
            }
        }

        if (xcommands[0].equalsIgnoreCase("vertical"))
        {
            lsql.setHorizontal(false);
            LUtilities.log("Vertical display selected.");
            parsed = true;
        }

        if (xcommands[0].equalsIgnoreCase("horizontal"))
        {
            lsql.setHorizontal(true);
            LUtilities.log("Horizontal display selected.");
            parsed = true;
        }

        if (xcommands[0].equalsIgnoreCase("info"))
        {
            lsql.printInformation();
            parsed = true;
        }

        if (xcommands[0].equalsIgnoreCase("verbose"))
        {
            if (xcommands.length > 1)
            {
                if (xcommands[1].equalsIgnoreCase("off"))
                {
                    lsql.setVerbose(false);
                    LUtilities.log("Verbose off.");
                }
                if (xcommands[1].equalsIgnoreCase("on"))
                {
                    lsql.setVerbose(true);
                    LUtilities.log("Verbose on.");
                }
            }
            else
            {
                    lsql.setVerbose(true);
                    LUtilities.log("Verbose on.");
             }
             parsed = true;
        }

        if (xcommands[0].equalsIgnoreCase("export"))
        {
            if (xcommands.length > 1)
            {
                if (xcommands[1].equalsIgnoreCase("off"))
                {
                    lsql.setExport(null);
                    LUtilities.log("Exporting turned off.");
                }
                else
                {
                    lsql.setExport(xcommands[1]);
                    LUtilities.log("The output will be exported to: " + lsql.getExport() + ".");
                }
                parsed = true;
            }
        }

        return parsed;
    }
}
