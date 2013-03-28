/*******************************************************************************
 * Copyright (c) 2011 Quinten David Soetens
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     Quinten David Soetens - initial API and implementation
 ******************************************************************************/

package be.ac.ua.ansymo.cheopsj.logger.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.ObjectInputStream.GetField;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import be.ac.ua.ansymo.cheopsj.model.Activator;

/**
 * @author quinten
 * 
 */
public class CompilationUnitHistory {

	private static String extension = ".prevcu";

	private static File getFileLocation(String projectLocation, String resourceFileName) {
		//Dump old compilation units into a hidden folder in the project.
		return new File(projectLocation + "/.oldCUs/" + resourceFileName + extension);
		//return Activator.getDefault().getStateLocation().append(filename + extension).toFile();
	}
	
	
	/**
	 * @param newAST
	 * @param fullPath
	 * @param iPath 
	 */
	public static void storeNewAST(CompilationUnit newAST, IPath projectPath, IPath relativeResourcePath) {
		try {
			/*IPath path = fullPath.addFileExtension(extension);

			File file = path.toFile();// new File(path.toString());
			// file.createNewFile();*/
			File file = getFileLocation(projectPath.toOSString(), relativeResourcePath.toOSString());
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			//System.out.println(file.getAbsolutePath());
			
			
			
			
			FileWriter fstream = new FileWriter(file);
			BufferedWriter out = new BufferedWriter(fstream);

			out.write(newAST.toString());
			out.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param fullPath
	 * @return
	 */
	public static CompilationUnit getOldAST(IPath projectPath, IPath relativeResourcePath) {
		CompilationUnit cu = null;

		try {
			/*IPath path = fullPath.addFileExtension(extension);
			File file = path.toFile();*/
			File file = getFileLocation(projectPath.toOSString(), relativeResourcePath.toOSString());

			FileReader input = new FileReader(file);
			BufferedReader br = new BufferedReader(input);

			StringBuilder str = new StringBuilder();
			String line;

			while ((line = br.readLine()) != null) {
				str.append(line);
				str.append("\n");
			}

			ASTParser parser = ASTParser.newParser(AST.JLS3);

			parser.setSource(str.toString().toCharArray());
			cu = (CompilationUnit) parser.createAST(null);
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {
		}

		return cu;
	}
}
