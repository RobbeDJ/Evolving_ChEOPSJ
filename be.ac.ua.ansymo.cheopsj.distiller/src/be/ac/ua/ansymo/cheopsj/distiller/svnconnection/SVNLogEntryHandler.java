package be.ac.ua.ansymo.cheopsj.distiller.svnconnection;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import be.ac.ua.ansymo.cheopsj.distiller.scmconnection.SCMLogEntryHandler;

public class SVNLogEntryHandler extends SCMLogEntryHandler implements ISVNLogEntryHandler {


	@Override
	public void handleLogEntry(SVNLogEntry entry) throws SVNException {
		message = entry.getMessage();
		changedPaths = entry.getChangedPaths();
		revision = entry.getRevision();
		date = entry.getDate();
		user = entry.getAuthor();
	}


	
}
