package misc;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

import wp.core.CachedURL;
import wp.core.Rater;
import wp.core.Upgrader;
import wp.core.WordStats;
import wp.model.ParsedPage;
import wp.utils.WebUtils;

public class SimpleTest {

	public static void main (String [] args) throws Exception {
		
		//testZip("testentry");
		
		//double	exp = Math.exp(-600d);
		
		String	testing = "testing" + Upgrader.NL;
		
		System.out.println(testing);
		System.out.println(testing.trim());
		System.out.println("done");
		
		ParsedPage	pp = new WebUtils().getContent(new CachedURL("http://www.cnn.com"), null, true, 255);
		 
		
		CachedURL	url = new CachedURL("http://yan.livejournal.com/1555839.html?nc=1", 1, null);
		
		System.out.println (url.getContent());
		
		if (true)
			return;
		
		
		ParsedPage	pp2 = new WebUtils().newParse(url, true, true, false, 255, null);
		
		System.out.println(pp2.getLinkToContentRatio());
		
		
		double	test = 5.333;
		
		DecimalFormat	format = new DecimalFormat("#.#");
		System.out.println(format.format(test));
		if (true)
			return;
		
		
		MathContext	mc = new MathContext(600);
		BigDecimal	small = new BigDecimal(.1d, mc);
		
		System.out.println (new BigDecimal(.1d, mc).multiply(new BigDecimal(.1d, mc)));
		
		BigDecimal	result = small;
		
		for (int i = 0; i < 500; i++)
			result = result.multiply(small);
		
		System.out.println(result);
		
		
		
		StringBuffer	buf = new StringBuffer();
		buf.append("testing");
		buf.append(WebUtils.NL);
		buf.append("another line");
		buf.append(WebUtils.NL);
		buf.append("third");
		System.out.println (buf.toString());
		
		FileUtils.writeStringToFile(new File(Rater.getTheRater().getDataDir() + "test.txt"), buf.toString(), "UTF-8");
		
		
		int	totalWords = 0;
		Random	r = new Random();
		for (int i = 0; i < 20; i++) {
			
			int	docWords = r.nextInt(3000) + 1;
			
			
			totalWords += docWords;
		}
		
		
		
		double	start = .5d;
		double	notStart = .5d;
		
		for (int i = 0; i < 300; i++) {
			double	random = Math.random();
			if (random < WordStats.MIN_PROB)
				random = WordStats.MIN_PROB;
			
			start *= random;
			notStart *= (1d - random);
			System.out.println ("I IS: " + i + " RANDOM IS: " + random + " start IS: " + start + " NOT start is: " + notStart);
			
			double	totalProb = start / (start + notStart);
			
			System.out.println ("TOTAL PROB: " + totalProb);
		}
	}
	
	
	static void	testZip (String entryName) throws Exception {
		FileOutputStream fout = null;
		try {

			fout = new FileOutputStream("C:/Users/Jeff/todo.zip");
		    ZipOutputStream zout = new ZipOutputStream(fout);

//			br = new BufferedReader(new InputStreamReader(is));
//			StringBuffer	templateBuf = new StringBuffer();
//			String			next;
//			while ((next = br.readLine()) != null) {
//				templateBuf.append(next);
//				templateBuf.append(WebUtils.NL);
//			}
			String			templateText = "testing 123"; // templateBuf.toString().replaceAll(from, to);
			ZipEntry	newEntry = new ZipEntry(entryName + ".txt");
			CRC32 crc = new CRC32();
			
		    byte []	buffer = templateText.getBytes();
		    newEntry.setSize(buffer.length);
			crc.reset();
			crc.update(buffer);
		    newEntry.setCrc(crc.getValue());
			zout.putNextEntry(newEntry);
			zout.write(buffer, 0, buffer.length);					
			zout.flush();
			zout.closeEntry();
			
			
			ZipEntry	secondEntry = new ZipEntry(entryName + "2.txt");
			buffer = "What will this one do".getBytes();
		    secondEntry.setSize(buffer.length);
			crc.reset();
			crc.update(buffer);
		    secondEntry.setCrc(crc.getValue());
			zout.putNextEntry(secondEntry);
			zout.write(buffer, 0, buffer.length);					
			zout.flush();
			zout.closeEntry();
			
			
			zout.finish();
		}
		finally {
			if (fout != null)
				fout.close();
		}
	}
}
