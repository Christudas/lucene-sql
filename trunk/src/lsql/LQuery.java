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
 * This class is not enterely necessary... but it helped me sort through
 * when to open/close the reader/writer while executing a query
 * which was very complicated...
 */

public class LQuery
{
    String query;
    Hits hits;

    LWrapper wrapper;

    public LQuery(LWrapper newWrapper, String newQuery) throws IOException, ParseException
    {
        wrapper = newWrapper;
        query = newQuery;

        Analyzer analyzer = new BasicUnmodifiedTokenAnalyzer();
        Query q = new QueryParser("", analyzer).parse(query);

        hits = wrapper.getSearcher().search(q);
    }
    
    public int getSize()
    {
        return hits.length();
    }

    public Document getDocument(int pos) throws IOException
    {
        return hits.doc(pos);
    }

    public int getDocumentId(int pos) throws IOException
    {
        return hits.id(pos);
    }


}
