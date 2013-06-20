package be.ac.ua.ansymo.cheopsj.distiller.scmconnection;

import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

public class SCMLogEntryHandler implements ISCMLogEntryHandler {
	
	protected String message = "";
	protected Map<?, ?> changedPaths;
	protected long revision = 0;
	protected Date date;
	protected String user = "";
	
	public long getRevision() {
		return revision;
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
