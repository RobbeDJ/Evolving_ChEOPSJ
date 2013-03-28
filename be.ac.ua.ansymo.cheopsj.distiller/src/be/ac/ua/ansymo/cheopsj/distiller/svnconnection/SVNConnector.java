package be.ac.ua.ansymo.cheopsj.distiller.svnconnection;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNInfo;
import org.tmatesoft.svn.core.wc.SVNLogClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class SVNConnector {
	private String fSVNUrl = "file:///Users/quinten/svn/cruisecontrol"; //TODO extract url from selected project somehow
	private String fSVNUserName = "";
	private String fSVNUserPassword = "";
	private SVNClientManager clientManager;
	private SVNRepository repository = null;

	/**
	 * Constructor gets username and password to the svn repository
	 */
	public SVNConnector(String svnUserName, String svnUserPassword) {
		fSVNUserName = svnUserName;
		fSVNUserPassword = svnUserPassword;
	}

	/**
	 * Initialize a few things. Create a ClientManager + Authenticate your username and password.
	 */
	public void initialize(){
		setupLibrary();
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager;
		if ((fSVNUserName == null) && (fSVNUserPassword == null)) {
			authManager = SVNWCUtil.createDefaultAuthenticationManager();
		} else {
			authManager = SVNWCUtil.createDefaultAuthenticationManager(fSVNUserName, fSVNUserPassword);
		}
		clientManager = SVNClientManager.newInstance(options, authManager);
		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(fSVNUrl));
			repository.setAuthenticationManager(authManager);
		} catch (SVNException e) {
			System.err
            .println("error while creating an SVNRepository for the location '"
                    + fSVNUrl + "': " + e.getMessage());
			e.printStackTrace();
		}
	    
	}

	/**
	 * Initializes the library to work with a repository using different protocols.
	 * 
	 * @see org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory#setup()
	 * @see org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl#setup()
	 * @see org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory#setup()
	 */
	private void setupLibrary() {
		// For using over http:// and https://
		DAVRepositoryFactory.setup();
		// For using over svn:// and svn+xxx://
		SVNRepositoryFactoryImpl.setup();
		// For using over file:///
		FSRepositoryFactory.setup();
	}

	/**
	 * Update the given file in the working directory to the given revision.
	 * @param file the file that has to be updated (can be a directory)
	 * @param revisionNumber the revision to which the file has to be updated
	 */
	public void updateToRevision(File file, long revisionNumber, IProgressMonitor monitor){		
		monitor.subTask("Updating to revision " + revisionNumber);
		try {
			SVNUpdateClient updateClient = clientManager.getUpdateClient();
			updateClient.setIgnoreExternals(false);
			updateClient.doUpdate(file, SVNRevision.create(revisionNumber), SVNDepth.INFINITY, true, true);
		} catch (SVNException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve what the current revisionnumber is of the given file
	 * @param file the file for which we want to obtain the revision number (can be directory)
	 * @return the revision number of the given file.
	 */
	public long getCurrentRevision(File file){
		long rev = 0;
		try {
			SVNWCClient wcClient = clientManager.getWCClient();
			SVNInfo info = wcClient.doInfo(file, SVNRevision.WORKING);
			SVNRevision revision = info.getRevision();
			rev = revision.getNumber();
		} catch (SVNException e) {
			e.printStackTrace();
		}

		return rev;
	}

	/**
	 * Find out what the revision number is of the latest (HEAD) revision.
	 * @param file the given file for which we want to know what the latest revision number is (can be a directory)
	 * @return the revision number of the latest revision of the given file
	 */
	public long getHeadRevisionNumber(File file) {
		long rev = 0;
		try {
			SVNWCClient wcClient = clientManager.getWCClient();
			SVNInfo info = wcClient.doInfo(file, SVNRevision.HEAD);
			SVNRevision revision = info.getRevision();
			rev = revision.getNumber();
		} catch (SVNException e) {
			e.printStackTrace();
		}

		return rev;
	}
	
	public void getCommitMessage(File file, long revisionNumber, SVNLogEntryHandler handler){
		try {
			SVNLogClient logClient = clientManager.getLogClient();
			SVNRevision revision = SVNRevision.create(revisionNumber);
			File[] files = {file};
			logClient.doLog(files, revision, revision, true, true, 1, handler);
		} catch (SVNException e) {
			//e.printStackTrace();
		}
	}
	
	public String getFileContents(String filePath, long revision){
		SVNProperties fileProperties = new SVNProperties();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            repository.getFile(filePath, revision, fileProperties, baos);
        } catch (SVNException svne) {
            System.err.println("error while fetching the file contents and properties: " + svne.getMessage());
            System.exit(1);
        }

        /*
         * Here the SVNProperty class is used to get the value of the
         * svn:mime-type property (if any). SVNProperty is used to facilitate
         * the work with versioned properties.
         */
        String mimeType = fileProperties.getStringValue(SVNProperty.MIME_TYPE);

        /*
         * SVNProperty.isTextMimeType(..) method checks up the value of the mime-type
         * file property and says if the file is a text (true) or not (false).
         */
        boolean isTextType = SVNProperty.isTextMimeType(mimeType);
        
        /*
         * Displays the file contents in the console if the file is a text.
         */
        if (isTextType) {
			return baos.toString();
        }
        return "";
	}

}
