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
import org.apache.lucene.store.LockObtainFailedException;

/**
 *
 * This class was created to deal with all the issues with opening/closing
 * the reader/writer.  It turns out that we can leave the reader open during
 * the length of the class, but we do have to reopen() it before we delete
 * from the list (or it creates an IO error).  Adding a document requires
 * a openWriter, closeWriter.
 *
 * This class simplifes this weirdness so you can just deal with one simple
 * class.
 *
 * jte 3-20-09
 */
public class LWrapper
{

    private IndexReader ir = null;
	private IndexWriter iw = null;

    String index;
    private IndexSearcher searcher;

    //manage the lists
    private List<Integer> toDel;


    public LWrapper(String newIndex) throws IOException, ParseException
    {
        toDel = new ArrayList<Integer>();

        if (testIndexIsValid(newIndex))
        {
            setIndex(newIndex);
        }
    }

    public void setIndex(String newIndex) throws IOException
    {
        index = newIndex;

        //we shouldn't ever have to bother with closing the reader
        //except when we are done with the wrapper
        //but we do have to open and close the writer... cause it creates
        //a lock on the index...
        openReader();

        searcher = new IndexSearcher(ir);
    }

    /*
     * Retrieves the IndexSearcher
     * LQuery uses this function
     */
    public IndexSearcher getSearcher()
    {
        searcher = new IndexSearcher(ir);
        return searcher;
    }

    /*
     * Add and delete functions
     */
    public void addDocument(Document doc) throws CorruptIndexException, IOException {
		openWriter();
		iw.addDocument(doc);
		closeWriter();
	}

	public void deleteDocument(int docId) throws  LockObtainFailedException, IOException{

        //if we don't open and close the reader.. it won't actually delete
        //them from the list.. not sure why?  took me a while to figure
        //that one out too... :)

        openReader();
        ir.deleteDocument(docId);
        closeReader();
	}

    /*
     * Functions for deleting
     * Adds to a list.. then delete them all at once
     */
    public void addToDeleteList(int docId)
    {
        toDel.add(docId);
    }

    public void deleteFromList() throws IOException
    {
        //if we don't do this... we get an IO Error
        //took a while to figure that out... :)
        closeReader();
        openReader();
        
        //now delete all the documents
        for (int docId : toDel) {
            deleteDocument(docId);
        }

        //refresh list
        toDel = new ArrayList<Integer>();

        //we open it back up because it was closed on deleteDocument.
        openReader();
    }

    /*
     * Optimizes
     */
    public void optimize() throws IOException
    {
        closeReader();
        openWriter();

        iw.optimize();
        
        closeWriter();
        openReader();
    }
    /*
     * Our basic open/close functions
     */
    private void openReader() throws IOException {
		//create the index reader
        ir = IndexReader.open(index);
        if (ir == null)
            System.out.println("PROBLEM!");
	}

	private void closeReader() throws IOException {
        if (ir != null)
        ir.close();
	}


    private void openWriter() throws IOException {
		//create the index writer
		Analyzer analyzer = new BasicUnmodifiedTokenAnalyzer();
		iw = new IndexWriter(index, analyzer);
	}

	private void closeWriter() throws IOException {
		//close the index writer
		//LUtilities.log("closeWriter: " + index);
        if (iw != null)
            iw.close();
	}

    public Collection getFieldNames()
    {
        return ir.getFieldNames(IndexReader.FieldOption.ALL);
    }

    public long getVersion()
    {
        return ir.getVersion();
    }
    
    public int numDocs()
    {
        return ir.numDocs();
    }

    /*
     * Nice function to see if its a valid lucen
     */
    public boolean testIndexIsValid(String indexToTest)
    {
        //first test that it is valid directory
        indexToTest = indexToTest.trim();
        File f = new File(indexToTest);
        if (!f.isDirectory())
        {
            LUtilities.log(indexToTest + " is not a valid directory.");
            return false;
        }

        //then test that is is a valid lucene index
        boolean valid = IndexReader.indexExists(indexToTest);
        if (!valid)
            LUtilities.log("The directory " + indexToTest + " is not a valid Lucene index");
        return valid;
    }

}
