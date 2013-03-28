package be.ac.ua.ansymo.cheopsj.distiller.svnconnection;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

public class SVNLogEntryHandler implements ISVNLogEntryHandler {
	
	private String message = "";
	private Map<?, ?> changedPaths;
	private long revision = 0;
	private Date date;
	private String user = "";

	public long getRevision() {
		return revision;
	}

	@Override
	public void handleLogEntry(SVNLogEntry entry) throws SVNException {
		message = entry.getMessage();
		changedPaths = entry.getChangedPaths();
		revision = entry.getRevision();
		date = entry.getDate();
		user = entry.getAuthor();
	}

	public String getMessage(){
		return message;
	}
	
	public Map<?, ?> getChangedPaths(){
		return changedPaths;
	}
	
	public Date getDate(){
		return date;
	}

	public boolean entryIsBugFix() {
		
		final String KEYWORD_REGEX = "\\bfix(e[ds])?\\b|\\bfixing\\b|\\bbugs?\\b|\\bdefects?\\b|\\bpatch\\b|\\bissues?\\b";
		
		Pattern p = Pattern.compile(KEYWORD_REGEX);
		Matcher m = p.matcher(message);
		
		return m.find();
	}

	public String getUser() {
		return user;
	}
	
}
