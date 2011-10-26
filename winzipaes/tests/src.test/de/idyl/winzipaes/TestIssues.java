package de.idyl.winzipaes;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

import de.idyl.winzipaes.impl.ExtZipEntry;

public class TestIssues extends TestAesZipBase {

	@Test
	public void testIssue21() throws Exception {
		File inFile = getInZipFile("issue21.zip");
		File encFile = getOutFile("issue21outA.zip");
		AesZipFileEncrypter enc = new AesZipFileEncrypter(encFile,encrypter);
    enc.addAll(inFile, "abcdef");
    enc.close();
    issue21Decrypt(encFile);

		encFile = getOutFile("issue21outB.zip");
    AesZipFileEncrypter.zipAndEncryptAll( inFile, encFile, "abcdef", encrypter );
    issue21Decrypt(encFile);

    cleanOut();
	}
	
	protected void issue21Decrypt(File encFile) throws Exception {
		AesZipFileDecrypter dec = new AesZipFileDecrypter(encFile,decrypter);    
		List<ExtZipEntry> list = dec.getEntryList();
    for (int i=0; i<list.size(); i++){
      ExtZipEntry entry = list.get(i);
      String name = entry.getName();
      if( !entry.isDirectory() ) {
      	dec.extractEntryWithTmpFile( entry, getOutFile(name), "abcdef" );
      }
    }
    
    dec.close();		
	}

	@Test
	public void testIssue21b() throws Exception {
		issue21Decrypt( getInZipFile("issue21b.zip") );
	}

	@Test
	public void testIssue18_10() throws Exception {
		File zipFile = getOutFile("issu18_10_out.zip");
		AesZipFileEncrypter enc = new AesZipFileEncrypter(zipFile,encrypter);
		File inFile = getInZipFile("issue18_10.zip");
		enc.addAll(inFile, PASSWORD);
		enc.close();

		AesZipFileDecrypter dec = new AesZipFileDecrypter(zipFile,decrypter);
		List<ExtZipEntry> entryList = dec.getEntryList();
		assertNotNull(entryList);
		assertFalse(entryList.isEmpty());
		
		ExtZipEntry entry = entryList.get(0);
		File extFile = getOutFile(entry.getName());
		dec.extractEntryWithTmpFile(entry, extFile, PASSWORD);
	}

	@Test
	public void testIssue33() throws Exception {
		String fileName1 = "file1.txt";
		String fileContent1 = "";

		File tmpZipFile = getOutFile("tmpFile.zip");
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(tmpZipFile));
		zout.setLevel(Deflater.BEST_COMPRESSION);
		addZipEntry(fileName1, fileContent1, zout);
		zout.close();

		String password = "123456";
		File aesFile = getOutFile("aesFile.zip");
		AesZipFileEncrypter aesEncryptor = new AesZipFileEncrypter(aesFile,encrypter);
		aesEncryptor.addAll(tmpZipFile, password);
		aesEncryptor.close();
		tmpZipFile.delete();

		AesZipFileDecrypter aesDecrypter = new AesZipFileDecrypter(aesFile,decrypter);
		
		checkZipEntry( aesDecrypter, fileName1, fileContent1, password );
		
		aesDecrypter.close();
	}

	@Test
	public void testIssue38() throws Exception {
		String zipFileName = "issue38.zip";
		File zipFile = getOutFile(zipFileName);
		AesZipFileEncrypter enc = new AesZipFileEncrypter(zipFile,encrypter);
		
		enc.add("jpgLarge.jpg",getInFileAsStream("jpgLarge.jpg"), PASSWORD);
		enc.add("jpgLarge2.jpg",getInFileAsStream("jpgLarge.jpg"), PASSWORD);
		enc.close();
		
		AesZipFileDecrypter dec = new AesZipFileDecrypter(zipFile,decrypter);
		File outFile = getOutFile("jpgLarge.jpg");
		dec.extractEntryWithTmpFile(dec.getEntry("jpgLarge.jpg"), outFile, PASSWORD);
		outFile = getOutFile("jpgLarge2.jpg");
		dec.extractEntryWithTmpFile(dec.getEntry("jpgLarge2.jpg"), outFile, PASSWORD);
		dec.close();
	}

}