package wp.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import wp.core.Constants;
import wp.core.Rater;

public class Utils {

	public static final String	ITEM_SEP = "!@#$";
	public static	List<String>	getStringList (BufferedReader br, String name) throws IOException {
		String	line = br.readLine().substring(name.length());
		
		String []	tokens = StringUtils.splitByWholeSeparator(line, ITEM_SEP);
		ArrayList<String>	list = new ArrayList<String>();
		if (tokens != null) {
			for (String token : tokens) {
			if (!StringUtils.isEmpty(token))
				list.add(token);
			}
		}
		return (list);
	}
	
	public static URL	normalizeUrl (URL url) throws MalformedURLException {
		if (url == null)
			return (null);
		
		String	urlStr = url.toString();
		
		// TODO: handle lack of "www."?
		//if (urlStr.startsWith("http://")) {
		//	if (urlStr.substring())
		//}
		if (urlStr.endsWith("/")) {
			return (new URL(urlStr.substring(0, urlStr.length() - 1)));
		}
		return (url);
	}

	public static boolean	xequals (Object o1, Object o2) {
		if (o1 == null)
			return (o2 == null);
		else if (o2 == null)
			return (o1 == null);
		return (o1.equals(o2));
	}
	
	public static String	normalizeUrlStr (String urlStr) {
		if (urlStr == null)
			return (null);
	
		if (!urlStr.startsWith("http://"))
			urlStr = "http://" + urlStr;
		
		// TODO: handle lack of "www."?
		//if (urlStr.startsWith("http://")) {
		//	if (urlStr.substring())
		//}
		if (urlStr.endsWith("/")) 
			return (urlStr.substring(0, urlStr.length() - 1));
		
		return (urlStr);
	}

	public static byte[] zipFiles(String files[], String fielPath) throws IOException {

		ByteArrayOutputStream bout=new ByteArrayOutputStream(); 
		ZipOutputStream zos=new ZipOutputStream(bout); 
		zos.setLevel(6);
		byte[] bytes = new byte[10000];

		for (int i = 0; i < files.length; i++) {

		FileInputStream fis = new FileInputStream(fielPath + "\\" + files[i]);
		BufferedInputStream bis = new BufferedInputStream(fis);

		zos.putNextEntry(new ZipEntry(files[i]));
		int bytesCount = -1;
		while ((bytesCount = bis.read(bytes)) != -1) {
		zos.write(bytes, 0, bytesCount);
		}
		zos.closeEntry();
		bis.close();
		fis.close();
		}
		zos.flush();
		bout.flush();

		zos.close();
		bout.close();
		return bout.toByteArray();

		}

	public static String	stringifyList (List<String> items) {
		StringBuffer	buf = new StringBuffer();
		
		for (String s : items) {
			buf.append(s);
			buf.append(ITEM_SEP);
		}
		return (buf.toString());
	}
	
	public static void	storeToFile (String name, String [] headers, String [] content) throws IOException {
		StringBuffer	buf = new StringBuffer();
		for (int i = 0; i < headers.length; i++) {
			buf.append(headers [i] + ": ");
			if (content [i] != null)
				buf.append(content [i]);
			buf.append(WebUtils.NL);
		}
		
		String	fullName = name + String.valueOf(System.currentTimeMillis());
		FileUtils.writeStringToFile(new File(Rater.getTheRater().getDataDir() + "/" + fullName), buf.toString(), "UTF-8");
	}
	
	public static String	stringStorage (Date timestamp) {
		return (timestamp == null ? "" : Constants.PRECISE_DATE_FORMAT.format(timestamp));
	}
	
	public static String	limit (String val, int size) {
		if (val == null)
			return (val);
		int	len = val.length();
		if (len < size)
			return (val);
		return (val.substring(0, size));
	}
	
	public static boolean	anyUncommonWordsMatch (String s1, String s2) {
		if (StringUtils.isEmpty(s1) || StringUtils.isEmpty(s2))
			return (false);
		
		String	s2Lower = s2.toLowerCase().trim();
		StringTokenizer	tok = new StringTokenizer(s1);
		HashSet<String>	commonWords = Rater.getTheRater().getCommonWords();
		
		while (tok.hasMoreTokens()) {
			String	nextLower = tok.nextToken().toLowerCase().trim();
			if (commonWords.contains(nextLower))
				continue;
			
			if (s2Lower.indexOf(nextLower) != -1)
				return (true);
		}
		return (false);
	}
	
	public static boolean	hasVulgarities (String s1) {
		if (StringUtils.isEmpty(s1))
			return (false);
		
		StringTokenizer	tok = new StringTokenizer(s1.toLowerCase());
		HashSet<String>	vulgarities = Rater.getTheRater().getVulgarities();
		
		while (tok.hasMoreTokens()) {
			String	nextLower = tok.nextToken().toLowerCase().trim();
			if (vulgarities.contains(nextLower))
				return (true);
		}
		return (false);
	}

	public static Collection<String>	extractCollection (String commaSep) {
		if (commaSep == null)
			return (null);
		
		HashSet<String>	set = new HashSet<String>();
		
		StringTokenizer	tok = new StringTokenizer(commaSep, ",");
		while (tok.hasMoreTokens()) {
			set.add(tok.nextToken().trim());
		}
		return (set);
	}
	
	public static boolean	hasVulgaritiesSubstring (String s1) {
		if (StringUtils.isEmpty(s1))
			return (false);
		
		s1 = s1.toLowerCase();
		HashSet<String>	vulgarities = Rater.getTheRater().getVulgarities();
		for (String vulgar : vulgarities) {
			if (s1.indexOf(vulgar) != -1)
				return (true);
		}
		return (false);
	}

	
	public static String []	generateCaptcha () {
		
		Random	r = new Random();
		int		firstInt = r.nextInt(8) + 1;
		int		secondInt = r.nextInt(8) + 1;
		String	first = String.valueOf(firstInt);
		String	second = String.valueOf(secondInt);
		int		total = firstInt + secondInt;
		
		String []	captcha = new String [] {"What does " + first + " plus " + second + " equal?", String.valueOf(total)};
		return (captcha);
	}
	
	public static boolean	anyUncommonWordsMatch (HashSet<String> set, String s2) {
		if (set.isEmpty() || StringUtils.isEmpty(s2))
			return (false);
		
		String	s2Lower = s2.toLowerCase().trim();		
		HashSet<String>	commonWords = Rater.getTheRater().getCommonWords();
		for (String s : set) {
			String	nextLower = s.toLowerCase().trim();
			if (commonWords.contains(nextLower))
				continue;
			if (s2Lower.indexOf(nextLower) != -1)
				return (true);
		}
		return (false);
	}
	
	public static Set<String>	getUncommonWords (String str) {
		HashSet<String>	ret = new HashSet<String>();
		if (StringUtils.isEmpty(str))
			return (ret);
		
		String	sLower = str.toLowerCase().trim();		
		HashSet<String>	commonWords = Rater.getTheRater().getCommonWords();
		StringTokenizer	tok = new StringTokenizer(sLower);
		
		while (tok.hasMoreTokens()) {
			String	nextLower = tok.nextToken().trim();
			if (!commonWords.contains(nextLower))
				ret.add(nextLower);
		}
		return (ret);
	}
	
	public static String	stringStorage (String s) {
		String	toUse = s == null ? "" : s;
		return (toUse.replaceAll(WebUtils.NL, " ").trim());
	}
	
	public static Date	getTimestamp (String timestampStr) {
		Date timestamp;
		try {
			timestamp = Constants.PRECISE_DATE_FORMAT.parse(timestampStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			timestamp = new Date();
		}
		return (timestamp);
	}
	
	public static boolean	isInteger (String s) {
		try {
			Integer.parseInt(s);
		}
		catch (Exception e) {
			return (false);
		}
		return (true);
	}
	
	public static HashSet<String>	readWordSet (String fileName) {
		HashSet<String>	ret = new HashSet<String>();

		BufferedReader	br = null;

		try {
			if (!new File(fileName).exists())
				return (ret);
			
			br = new BufferedReader(new FileReader(fileName));
			String	line;
			while ((line = br.readLine()) != null) {
				ret.add(line.trim());
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return (ret);
	}
	
	public static String []	listFiles (final String extension) {
		String []	list = new File(Rater.getTheRater().getDataDir()).list(new FilenameFilter () {

			public boolean accept(File arg0, String arg1) {
				return (arg1.endsWith(extension));
			}
			
		});
		return (list);
	}
}
