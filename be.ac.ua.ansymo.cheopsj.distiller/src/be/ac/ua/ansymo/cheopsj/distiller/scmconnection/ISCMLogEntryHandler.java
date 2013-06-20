package be.ac.ua.ansymo.cheopsj.distiller.scmconnection;

import java.util.Date;
import java.util.Map;

public interface ISCMLogEntryHandler {
	
	public long getRevision();
	public String getMessage();
	public Map<?, ?> getChangedPaths();
	public Date getDate();
	public boolean entryIsBugFix();
	public String getUser();
	
}
