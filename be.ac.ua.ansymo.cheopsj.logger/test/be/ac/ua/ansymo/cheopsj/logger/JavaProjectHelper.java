package be.ac.ua.ansymo.cheopsj.logger;

import java.lang.reflect.Field;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.TypeNameRequestor;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.util.CoreUtility;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Synchronizer;

public class JavaProjectHelper {

	private static final int MAX_RETRY = 5;

	public static IJavaProject createJavaProject(String projectName, String binFolderName) throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		if (!project.exists()) {
			project.create(null);
		} else {
			project.refreshLocal(IResource.DEPTH_INFINITE, null);
		}

		if (!project.isOpen()) {
			project.open(null);
		}

		IPath outputLocation;
		if (binFolderName != null && binFolderName.length() > 0) {
			IFolder binFolder = project.getFolder(binFolderName);
			if (!binFolder.exists()) {
				CoreUtility.createFolder(binFolder, false, true, null);
			}
			outputLocation = binFolder.getFullPath();
		} else {
			outputLocation = project.getFullPath();
		}

		if (!project.hasNature(JavaCore.NATURE_ID)) {
			addNatureToProject(project, JavaCore.NATURE_ID, null);
		}

		IJavaProject jproject = JavaCore.create(project);

		jproject.setOutputLocation(outputLocation, null);
		jproject.setRawClasspath(new IClasspathEntry[0], null);

		return jproject;
	}

	private static void addNatureToProject(IProject proj, String natureId, IProgressMonitor monitor) throws CoreException {
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = natureId;
		description.setNatureIds(newNatures);
		proj.setDescription(description, monitor);
	}

	/**
	 * Adds a source container to a IJavaProject.
	 * 
	 * @param jproject
	 *            The parent project
	 * @param containerName
	 *            The name of the new source container
	 * @return The handle to the new source container
	 * @throws CoreException
	 *             Creation failed
	 */
	public static IPackageFragmentRoot addSourceContainer(IJavaProject jproject, String containerName) throws CoreException {
		return addSourceContainer(jproject, containerName, new Path[0]);
	}

	/**
	 * Adds a source container to a IJavaProject.
	 * 
	 * @param jproject
	 *            The parent project
	 * @param containerName
	 *            The name of the new source container
	 * @param exclusionFilters
	 *            Exclusion filters to set
	 * @return The handle to the new source container
	 * @throws CoreException
	 *             Creation failed
	 */
	public static IPackageFragmentRoot addSourceContainer(IJavaProject jproject, String containerName, IPath[] exclusionFilters) throws CoreException {
		return addSourceContainer(jproject, containerName, new Path[0], exclusionFilters);
	}

	/**
	 * Adds a source container to a IJavaProject.
	 * 
	 * @param jproject
	 *            The parent project
	 * @param containerName
	 *            The name of the new source container
	 * @param inclusionFilters
	 *            Inclusion filters to set
	 * @param exclusionFilters
	 *            Exclusion filters to set
	 * @return The handle to the new source container
	 * @throws CoreException
	 *             Creation failed
	 */
	public static IPackageFragmentRoot addSourceContainer(IJavaProject jproject, String containerName, IPath[] inclusionFilters,
			IPath[] exclusionFilters) throws CoreException {
		return addSourceContainer(jproject, containerName, inclusionFilters, exclusionFilters, null);
	}

	/**
	 * Adds a source container to a IJavaProject.
	 * 
	 * @param jproject
	 *            The parent project
	 * @param containerName
	 *            The name of the new source container
	 * @param inclusionFilters
	 *            Inclusion filters to set
	 * @param exclusionFilters
	 *            Exclusion filters to set
	 * @param outputLocation
	 *            The location where class files are written to, <b>null</b> for
	 *            project output folder
	 * @return The handle to the new source container
	 * @throws CoreException
	 *             Creation failed
	 */
	public static IPackageFragmentRoot addSourceContainer(IJavaProject jproject, String containerName, IPath[] inclusionFilters,
			IPath[] exclusionFilters, String outputLocation) throws CoreException {
		IProject project = jproject.getProject();
		IContainer container = null;
		if (containerName == null || containerName.length() == 0) {
			container = project;
		} else {
			IFolder folder = project.getFolder(containerName);
			if (!folder.exists()) {
				CoreUtility.createFolder(folder, false, true, null);
			}
			container = folder;
		}
		IPackageFragmentRoot root = jproject.getPackageFragmentRoot(container);

		IPath outputPath = null;
		if (outputLocation != null) {
			IFolder folder = project.getFolder(outputLocation);
			if (!folder.exists()) {
				CoreUtility.createFolder(folder, false, true, null);
			}
			outputPath = folder.getFullPath();
		}
		IClasspathEntry cpe = JavaCore.newSourceEntry(root.getPath(), inclusionFilters, exclusionFilters, outputPath);
		addToClasspath(jproject, cpe);
		return root;
	}

	public static void addToClasspath(IJavaProject jproject, IClasspathEntry cpe) throws JavaModelException {
		IClasspathEntry[] oldEntries = jproject.getRawClasspath();
		for (int i = 0; i < oldEntries.length; i++) {
			if (oldEntries[i].equals(cpe)) {
				return;
			}
		}
		int nEntries = oldEntries.length;
		IClasspathEntry[] newEntries = new IClasspathEntry[nEntries + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, nEntries);
		newEntries[nEntries] = cpe;
		jproject.setRawClasspath(newEntries, null);
	}

	/**
	 * Removes a IJavaElement
	 * 
	 * @param elem
	 *            The element to remove
	 * @throws CoreException
	 *             Removing failed
	 * @see #ASSERT_NO_MIXED_LINE_DELIMIERS
	 */
	public static void delete(final IJavaElement elem) throws CoreException {
		IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				performDummySearch();
				if (elem instanceof IJavaProject) {
					IJavaProject jproject = (IJavaProject) elem;
					jproject.setRawClasspath(new IClasspathEntry[0], jproject.getProject().getFullPath(), null);
				}
				delete(elem.getResource());
			}
		};
		ResourcesPlugin.getWorkspace().run(runnable, null);
		emptyDisplayLoop();
	}

	public static void delete(IResource resource) throws CoreException {
		for (int i = 0; i < MAX_RETRY; i++) {
			try {
				resource.delete(true, null);
				i = MAX_RETRY;
			} catch (CoreException e) {
				if (i == MAX_RETRY - 1) {
					JavaPlugin.log(e);
					throw e;
				}
				try {
					Thread.sleep(1000); // sleep a second
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	public static void performDummySearch() throws JavaModelException {
		performDummySearch(SearchEngine.createWorkspaceScope());
	}

	public static void performDummySearch(IJavaElement element) throws JavaModelException {
		performDummySearch(SearchEngine.createJavaSearchScope(new IJavaElement[] { element }));
	}

	private static void performDummySearch(IJavaSearchScope searchScope) throws JavaModelException {
		new SearchEngine().searchAllTypeNames(null, SearchPattern.R_EXACT_MATCH,
				"XXXXXXXXX".toCharArray(), // make sure we search a concrete
											// name. This is faster according to
											// Kent
				SearchPattern.R_EXACT_MATCH | SearchPattern.R_CASE_SENSITIVE, IJavaSearchConstants.CLASS, searchScope, new Requestor(),
				IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
	}

	private static class Requestor extends TypeNameRequestor {
	}

	public static void emptyDisplayLoop() {
		boolean showDebugInfo = false;

		Display display = Display.getCurrent();
		if (display != null) {
			if (showDebugInfo) {
				try {
					Synchronizer synchronizer = display.getSynchronizer();
					Field field = Synchronizer.class.getDeclaredField("messageCount");
					field.setAccessible(true);
					System.out.println("Processing " + field.getInt(synchronizer) + " messages in queue");
				} catch (Exception e) {
					// ignore
					System.out.println(e);
				}
			}
			while (display.readAndDispatch()) { /* loop */
			}
		}
	}

}
