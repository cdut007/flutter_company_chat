package com.ultralinked.voip.api;

import com.ultralinked.voip.api.utils.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ZipUitls {

	public static final String TAG="ZipUitls";

	private static java.util.List<File> getFileList(String zipFileString, boolean bContainFolder, boolean bContainFile)throws Exception {
		java.util.List<File> fileList = new java.util.ArrayList<File>();
		java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(new java.io.FileInputStream(zipFileString));
		java.util.zip.ZipEntry zipEntry;
		String szName = "";

		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();

			if (zipEntry.isDirectory()) {

				// get the folder name of the widget
				szName = szName.substring(0, szName.length() - 1);
				File folder = new File(szName);
				if (bContainFolder) {
					fileList.add(folder);
				}

			} else {
				File file = new File(szName);
				if (bContainFile) {
					fileList.add(file);
				}
			}
		}//end of while

		inZip.close();

		return fileList;
	}


	private static InputStream upZip(String zipFilePath, String fileString)throws Exception {
		java.util.zip.ZipFile zipFile = new java.util.zip.ZipFile(zipFilePath);
		java.util.zip.ZipEntry zipEntry = zipFile.getEntry(fileString);

		return zipFile.getInputStream(zipEntry);

	}


	private static void unZipFolder(InputStream input, String outPathString)throws Exception {
		java.util.zip.ZipInputStream inZip = new java.util.zip.ZipInputStream(input);
		java.util.zip.ZipEntry zipEntry = null;
		String szName = "";

		while ((zipEntry = inZip.getNextEntry()) != null) {
			szName = zipEntry.getName();

			if (zipEntry.isDirectory()) {

				// get the folder name of the widget
				szName = szName.substring(0, szName.length() - 1);
				FileUtils.createFileDir(outPathString + File.separator + szName);

			} else {

				File file = new File(outPathString + File.separator + szName);
				file.createNewFile();
				// get the output stream of the file
				java.io.FileOutputStream out = new java.io.FileOutputStream(file);
				int len;
				byte[] buffer = new byte[1024];
				// read (len) bytes into buffer
				while ((len = inZip.read(buffer)) != -1) {
					// write (len) byte from buffer at the position 0
					out.write(buffer, 0, len);
					out.flush();
				}
				out.close();
			}
		}//end of while

		inZip.close();
	}


	private static void unZipFolder(String zipFileString, String outPathString)throws Exception {
		unZipFolder(new java.io.FileInputStream(zipFileString),outPathString);
	}//end of func



	protected static void zipFolder(String srcFilePath, String zipFilePath, boolean containMediaLog)throws Exception {

		java.util.zip.ZipOutputStream outZip = new java.util.zip.ZipOutputStream(new java.io.FileOutputStream(zipFilePath));


		File file = new File(srcFilePath);


		zipFiles(file.getParent()+ File.separator, file.getName(), outZip);


		outZip.finish();
		outZip.close();

	}//end of func


	public static void zipFiles(String folderPath, String filePath, java.util.zip.ZipOutputStream zipOut)throws Exception {
		if(zipOut == null){
			return;
		}
		String parentFilePath=folderPath+ File.separator+ filePath;

		File file = new File(parentFilePath);


		if (file.isFile()) {

			java.util.zip.ZipEntry zipEntry =  new java.util.zip.ZipEntry(filePath);
			java.io.FileInputStream inputStream = new java.io.FileInputStream(file);
			zipOut.putNextEntry(zipEntry);

			int len;
			byte[] buffer = new byte[4096];

			while((len=inputStream.read(buffer)) != -1)
			{
				zipOut.write(buffer, 0, len);
			}

			zipOut.closeEntry();

			inputStream.close();

		}
		else {

			String[] fileList = file.list();

			Log.i(TAG, "Before delete files size is "+fileList.length);


			for (int i = 0; i < fileList.length; i++) {

				Log.i(TAG, "child file path is "+fileList[i]);

				File fileChild=new File(parentFilePath+ File.separator+fileList[i]);

				if(!fileChild.exists()){

					continue;
				}

				long lastModifyTime = fileChild.lastModified();

				SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			    Date date1=new Date(lastModifyTime);

			    String time1=format.format(date1);

			    Log.i(TAG, fileList[i]+"last modify time  is "+time1);

				   if(lastModifyTime+(10*1000*60*60*24)< System.currentTimeMillis()){

					   Log.i(TAG, "delte file is "+fileChild.getAbsolutePath());

					   fileChild.delete();

				   }
			}


			fileList= file.list();

			Log.i(TAG, "after delete files size is "+fileList.length);

			if (fileList.length <= 0) {
				java.util.zip.ZipEntry zipEntry =  new java.util.zip.ZipEntry(filePath+ File.separator);
				zipOut.putNextEntry(zipEntry);
				zipOut.closeEntry();
			}

			for (int i = 0; i < fileList.length; i++) {

		     	File fileChild=new File(parentFilePath+ File.separator+fileList[i]);

					if(!fileChild.exists()){

						continue;
					}

				zipFiles(folderPath, filePath+ File.separator+fileList[i], zipOut);
			}//end of for

		}//end of if

	  }
	}
