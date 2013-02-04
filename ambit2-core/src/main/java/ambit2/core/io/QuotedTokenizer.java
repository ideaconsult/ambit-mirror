package ambit2.core.io;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


/**
 * The QuotedTokenizer class is used to break a string into simple tokens or
 * quoted tokens. A quoted token has the following characteristics:
 * <ul>
 * <li>starts with a double quote
 * <li>two consective double quotes are replaced by a single double quote
 * <li>a single double quote terminates the token
 * </ul>
 *
 * @see SimpleTokenizer
 */
public class QuotedTokenizer extends SimpleTokenizer
{
	private static final char DOUBLE_QUOTE = '"';
	private char delimiter;

	/**
	 * Constructs a tokenizer for the specified string.
	 *
	 * @param  text       a string to be tokenized.
	 * @param  delimiter  the delimiter.
	 */
	public QuotedTokenizer(String text, char delimiter)
	{
		super(text, String.valueOf(delimiter), true);
		this.delimiter = delimiter;
	}

	/**
	 *	Set the delimiter for the next token.
	 *
	 *  @param  delimiter  the delimiter.
	 */
	public void setDelimiter(String delimiter)
	{
		if (delimiter.length() != 1)
			throw new IllegalArgumentException( "Delimiter must be a single character" );

		this.delimiter = delimiter.charAt(0);
		super.setDelimiter(delimiter);
	}

	/**
	 *  Handle parsing of a quoted token here.
	 *  Parsing of a simple token is still done by the parent.
	 *
	 *  @return     the next token from this tokenizer.
	 */
	protected String parseToken()
	{
		if (text.charAt(position) == DOUBLE_QUOTE)
		{
			return parseQuotedToken();
		}
		else
		{
			return super.parseToken();
		}
	}

	/*
	 */
	private String parseQuotedToken()
	{
		StringBuffer token = new StringBuffer();
		boolean searchingForDelimiter = true;

		//  We can skip the first character since we know it's a double quote

		boolean inQuote = true;
		position++;

		while (position < maxPosition)
		{
			char c = text.charAt(position);

			//  Handle double quote character

			if (c == DOUBLE_QUOTE)
			{
				if ( !inQuote )
				{
				   token.append( c );
				}

				inQuote = !inQuote;
				position++;
				continue;
			}

			//  Handle the delimiter

			if (c == delimiter && !inQuote)
			{
				searchingForDelimiter = false;
				position++;
				break;
			}

			//  Handle all other characters

			token.append( c );
			position++;
		}

		//  No delimiter was found, position must be greater than maxPosition
		//  so hasMoreTokens() can return false

		if (searchingForDelimiter)
		{
			position++;
		}

		return token.toString();
	}

	public static void main(String[] args)
	{
		QuotedTokenizer ss = new QuotedTokenizer(args[1], args[0].charAt(0));

		ss.setText(args[1]);
		while (ss.hasMoreTokens())
		{
	     	System.out.print(":");
     		System.out.print(ss.nextToken());
    	 	System.out.println(":");

		}

	}

}

/**
 * The SimpleTokenizer class is used to break a string into tokens.
 *
 * <p>The delimiter	can be used in one of two ways, depending on how
 * the singleDelimiter flag is set:
 * <ul>
 * <li>when true, the entire string is treated as a single delimiter
 * <li>when false, each character in the string is treated as a delimiter
 * </ul>
 *
 * <p>The total number of tokens in the text is equal to the number of
 * delimeters found plus one. An empty token is returned when:
 * <ul>
 * <li>two consecutive delimiters are found
 * <li>the text starts with a delimiter
 * <li>the text ends with a delimiter
 * </ul>
 *
 * <p>You can use the tokenizer like the StringTokenizer:
 * <pre>
 *     SimpleTokenizer st = new SimpleTokenizer("this is a test", " ");
 *     while (st.hasMoreTokens())
 *         println(st.nextToken());
 * </pre>
 *
 * <p>Or, you can use the tokenizer like the String.split(...) method:
 * <pre>
 *     SimpleTokenizer st = new SimpleTokenizer("this is a test", " ");
 *     List list = st.getAllTokens();
 *     for (java.util.Iterator it = list.iterator(); it.hasNext();)
 *         println(it.next());
 * </pre>
 *
 * @see java.util.StringTokenizer
 * @see java.lang.String#split
 */
class SimpleTokenizer
{
	private String delimiter;
	private boolean singleDelimiter;
	private char maxDelimiter;
	private List tokens;

	protected String text;
	protected int position;
	protected int maxPosition;

	/**
	 * Constructs a tokenizer for the specified string.
	 *
	 * Each character in the delimiter string is treated as a delimiter.
	 *
	 * @param  text       a string to be tokenized.
	 * @param  delimiter  the delimiter.
	 */
	public SimpleTokenizer(String text, String delimiter)
	{
		this(text, delimiter, false);
	}

	/**
	 * Constructs a tokenizer for the specified string.
	 *
	 * <p>If the singleDelmiter flag is true, then the delimiter string is
	 * used as a single delimiter. If the flag is false, the each character
	 * in the delimiter is treated as a delimiter.
	 *
	 * @param  text       a string to be tokenized.
	 * @param  delimiter  the delimiter(s).
	 * @param  multipleDelimiters  treat each character as a delimiter.
	 */
	public SimpleTokenizer(String text, String delimiter, boolean singleDelimiter)
	{
		setText( text );
		setDelimiter( delimiter );
		this.singleDelimiter = singleDelimiter;
	}

	/**
	 *	Set the text to be tokenized.
	 *
	 *  @param  text  a string to be tokenized.
	 */
	public void setText(String text)
	{
		this.text = text;
		this.position = 0;
		this.maxPosition = text.length();
	}

	/**
	 *	Set the delimiter(s) used to parse the text.
	 *  The delimiter can be reset before retrieving the next token.
	 *
	 *  @param  delimiter  the delimiter.
	 */
	public void setDelimiter(String delimiter)
	{
		this.delimiter = delimiter;

		maxDelimiter = 0;

		for (int i = 0; i < delimiter.length(); i++)
		{
		    char c = delimiter.charAt(i);

		    if (maxDelimiter < c)
				maxDelimiter = c;
		}
	}

	/**
	 *  Tests if there are more tokens available from this tokenizer.
	 *  A subsequent call to <tt>nextToken</tt> will return a token.
	 *
	 *  @return  <code>true</code> when there is at least one token remaining;
	 *           <code>false</code> otherwise.
	 */
	public boolean hasMoreTokens()
	{
		return position <= maxPosition;
	}

	/**
	 *  Returns the next token from this tokenizer.
	 *
	 *  @return     the next token from this tokenizer.
	 *  @exception  NoSuchElementException  if there are no more tokens
	 */
	public String nextToken()
	{
		if (position > maxPosition)
			throw new NoSuchElementException();

		//  Return an empty token when we have finished parsing all
		//  the other tokens and the text ends with a delimiter

		if (position == maxPosition)
		{
			position++;
			return "";
		}

		return parseToken();
	}
	/**
	 *	Invoked by nextToken() once it is determined that more tokens exist.
	 *
	 *  @return     the next token from this tokenizer.
	 */
	protected String parseToken()
	{
		//  Parsing is different depending on the number of delimiters

		if (singleDelimiter || delimiter.length() == 1)
		{
			return parseUsingSingleDelimiter();
		}
		else
		{
			return parseUsingMultipleDelimiters();
		}
	}

	/*
	 *  Search the entire string until the delimiter is found
	 */
	private String parseUsingSingleDelimiter()
	{
		String token = null;
		int delimiterPosition = 0;

		//  A character search is faster than a String search

		if (delimiter.length() == 1)
			delimiterPosition = text.indexOf(delimiter.charAt(0), position);
		else
			delimiterPosition = text.indexOf(delimiter, position);

		//  Extract token based on search result

		if (delimiterPosition == -1)
		{
			token = text.substring(position, maxPosition);
			position = maxPosition + 1;
		}
		else
		{
			token = text.substring(position, delimiterPosition);
			position = delimiterPosition + delimiter.length();
		}

		return token;
	}

	/*
	 *  Check each character to see if it is one of the delimiters
	 */
	private String parseUsingMultipleDelimiters()
	{
		String token = null;
		int start = position;
		boolean searchingForDelimiter = true;

		while (position < maxPosition)
		{
			char c = text.charAt(position);

            if (c <= maxDelimiter && delimiter.indexOf(c) >= 0)
            {
            	searchingForDelimiter = false;
				break;
			}

		    position++;
		}

		//  Extract token based on search result

		if (searchingForDelimiter)
		{
			token = text.substring(start, maxPosition);
			position = maxPosition + 1;
		}
		else
		{
			token = text.substring(start, position);
			position++;
		}

		return token;
	}

	/**
	 * Returns the nth token from the current position of this tokenizer.
	 * This is equivalent to advancing n-1 tokens and returning the nth token.
	 *
	 * @param  tokenCount  the relative position of the token requested
	 * @return     the token found at the requested relative position.
	 * @exception  NoSuchElementException  if there are no more tokens
	 */
	public String nextToken(int tokenCount)
	{
		while (--tokenCount > 0)
		{
			nextToken();
		}

		return nextToken();
	}

	/**
	 *  Get the text that has not yet been tokenized.
	 *
	 *  @return  the remainder of the text to be tokenized
	 */
	public String getRemainder()
	{
		return hasMoreTokens() ? text.substring(position) : "";
	}

	/**
	 *  Tokenize the remaining text and return all the tokens
	 *
	 *  @return  a List containing all of the individual tokens
	 */
	public List getAllTokens()
	{
		tokens = new ArrayList();

		while (hasMoreTokens())
		{
			tokens.add( nextToken() );
		}

		return tokens;
	}

	public static void main(String[] args)
	{
		SimpleTokenizer ss = new SimpleTokenizer(args[1], args[0], true);

		while (ss.hasMoreTokens())
		{
	     	System.out.print(":");
     		System.out.print(ss.nextToken());
    	 	System.out.println(":");
		}
	}

}

