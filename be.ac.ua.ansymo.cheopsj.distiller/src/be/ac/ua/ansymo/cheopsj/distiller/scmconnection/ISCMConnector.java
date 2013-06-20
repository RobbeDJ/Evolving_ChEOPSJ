package be.ac.ua.ansymo.cheopsj.distiller.scmconnection;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import be.ac.ua.ansymo.cheopsj.distiller.svnconnection.SVNLogEntryHandler;

public interface ISCMConnector {
	
	public void initialize();
	public void updateToRevision(File file, long revisionNumber, IProgressMonitor monitor);
	public long getCurrentRevision(File file);
	public long getHeadRevisionNumber(File file);
	
	public void getCommitMessage(File file, long revisionNumber, SCMLogEntryHandler handler);
	
	public String getFileContents(String filePath, long revision);
}
