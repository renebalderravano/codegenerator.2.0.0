package com.codegenerator.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileManager {

	public static void copyDir(String src, String dest, boolean overwrite) {
		
		try {
			Files.walk(Paths.get(src)).forEach(a -> {
				Path b = Paths.get(dest, a.toString().substring(src.length() - 1));
				try {
					if (!b.toString().equals(a.toString())) {
						System.out.println("agregando archivo a: " + b.toString());
						Files.copy(a, b, overwrite ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING }
								: new CopyOption[] {});
					}

				} catch (IOException e) {
					System.out.println("El archivo " + b + " ya existe");
				}
			});
		} catch (IOException e) {
			// permission issue
			e.printStackTrace();
		}

	}

	public static void replaceTextInFilesFolder(String src, String oldText, String newText) {
		try {
			Files.walk(Paths.get(src))		
//			.filter(Files::isRegularFile)
//			.filter(path -> (!path.toString().endsWith(".png") || !path.toString().endsWith(".jpg")
//					|| !path.toString().endsWith(".jpeg") || !path.toString().endsWith(".svg")
//					))
					.forEach(a -> {
						Path path = Paths.get(a.toString());

						if (path.toString().endsWith(".png") || path.toString().endsWith(".jpg")
								|| path.toString().endsWith(".jpeg") || path.toString().endsWith(".svg")
								) {
							System.out.println("Este archivo es una imagen: "+path.getFileName());

						} else {
							Charset charset = StandardCharsets.UTF_8;

							String content;
							try {
								if (!Files.isDirectory(path)) {
									content = new String(Files.readAllBytes(path), charset);
									content = content.replace(oldText, newText);
									Files.write(path, content.getBytes(charset));
								}
							} catch (IOException e) {
								System.out.println("El archivo " + path + " ya existe");
							}
						}

					});
		} catch (IOException e) {
			// permission issue
			e.printStackTrace();
		}

	}

	public static String getFileExtension(String filename) {
		if (filename == null) {
			return null;
		}
		int dotIndex = filename.lastIndexOf(".");
		if (dotIndex >= 0) {
			return filename.substring(dotIndex + 1);
		}
		return "";
	}

	public static void replaceTextInFile(String filePath, String oldText, String newText) {
		Path path = null;
		try {
			path = Paths.get(filePath);
			if (path.toString().endsWith(".png") || path.toString().endsWith(".jpg")
					|| path.toString().endsWith(".jpeg") || path.toString().endsWith(".svg")) {
				System.out.println(path);

			} else {

				if (!Files.isDirectory(path)) {
					Charset charset = StandardCharsets.UTF_8;
					String content = new String(Files.readAllBytes(path), charset);
					content = content.replace(oldText, newText);
					Files.write(path, content.getBytes(charset));
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("El archivo " + path + " no existe");
		}

	}

	public static void createRootDirectory(String workspace, String folderName) {

		File f = new File(workspace + "\\" + folderName);
		f.mkdir();
		f = new File(workspace + "\\" + folderName + "\\src");
		f.mkdir();
		f = new File(workspace + "\\" + folderName + "\\src\\main");
		f.mkdir();
		f = new File(workspace + "\\" + folderName + "\\src\\main\\java");
		f.mkdir();
		f = new File(workspace + "\\" + folderName + "\\src\\main\\resources");
		f.mkdir();
	}

	public static void createPackage(String packagePath, String packageName) {

		String basePackagePath = packagePath;

		String[] pf = packageName.split("\\.");
		for (String fName : pf) {
			createFolder(basePackagePath, fName);

			basePackagePath = basePackagePath + "\\" + fName;
		}
	}

	public static void createFolder(String path, String folderName) {
		File f = new File(path + "\\" + folderName);
		f.mkdir();

	}

}
