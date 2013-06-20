package be.ac.ua.ansymo.cheopsj.distiller.scmconnection;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;

import be.ac.ua.ansymo.cheopsj.distiller.svnconnection.SVNLogEntryHandler;

public class SCMConnector implements ISCMConnector {
	
	protected String fUrl;// = "file:///Users/quinten/svn/cruisecontrol"; //TODO extract url from selected project somehow
	protected String fUserName = "";
	protected String fUserPassword = "";
	
	/**
	 * Constructor gets username and password to the svn repository
	 */
	public SCMConnector(String userName, String userPassword) {
		fUserName = userName;
		fUserPassword = userPassword;
	}
	
	/**
	 * Constructor gets username and password to the svn repository
	 */
	public SCMConnector(String url, String userName, String userPassword) {
		fUrl = url;
		fUserName = userName;
		fUserPassword = userPassword;
	}
	
	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateToRevision(File file, long revisionNumber,
			IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	@Override
	public long getCurrentRevision(File file) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getHeadRevisionNumber(File file) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void getCommitMessage(File file, long revisionNumber,
			SCMLogEntryHandler handler) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFileContents(String filePath, long revision) {
		// TODO Auto-generated method stub
		return null;
	}

}
