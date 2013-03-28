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
package be.ac.ua.ansymo.cheopsj.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Change;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.changes.Subject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixAttribute;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixBehaviouralEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixEntity;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInvocation;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixLocalVariable;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixMethod;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixObject;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixPackage;

/**
 * This class is the entity that holds and maintains the entire famix and change model.
 * All famixEntities and all changes that act upon those famixEntities are stored in this ModelManager.
 * 
 * @author quinten
 *
 */
public class ModelManager implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4107886630686152745L;

	//This list contains all FamixEntities
	private List<FamixObject> famixEntities;
	//This List contains all changes
	private List<IChange> changes;
	//This list contains all ModelManagerListeners, i.e. listeners that need to be notified when a new change was added to the model.
	//For instance the views in the ...model.ui plugin
	private List<ModelManagerListener> listeners;

	//We also keep maps to specific FamixEntities to allow easier lookup.
	private Map<String, FamixPackage> famixPackagesMap;
	private Map<String, FamixClass> famixClassesMap;
	private Map<String, FamixMethod> famixMethodsMap;
	private Map<String, FamixAttribute> famixFieldsMap;
	private Map<String, FamixInvocation> famixInvocationsMap;
	private Map<String, FamixLocalVariable> famixVariablesMap;
	//The modelmanager is a Singleton entity, hence the constructor is private.
	//You should always call the static method getInstance() to get the ModelManager instance.
	private static ModelManager INSTANCE = new ModelManager();

	private ModelManager() {
		changes = new ArrayList<IChange>();
		famixEntities = new ArrayList<FamixObject>();
		listeners = new ArrayList<ModelManagerListener>();

		famixPackagesMap = new HashMap<String, FamixPackage>();
		famixClassesMap = new HashMap<String, FamixClass>();
		famixMethodsMap = new HashMap<String, FamixMethod>();
		famixFieldsMap = new HashMap<String, FamixAttribute>();
		famixInvocationsMap = new HashMap<String, FamixInvocation>();
		famixVariablesMap = new HashMap<String, FamixLocalVariable>();
		// loadModel();
	}

	/**
	 * The ModelManger is a Singleton entity. Therefor the constructor is private.
	 * This method returns an instance of the ModelManger. If no instance existed 
	 * before it will call the private constructor to create a new instance. Else
	 * It will return the existing instance. 
	 *  
	 * @return the Singleton ModelManager instance
	 */
	public static ModelManager getInstance() {
		if (INSTANCE == null)
			INSTANCE = new ModelManager();
		return INSTANCE;
	}

	/**
	 * @return the list of changes maintained in the ModelManager
	 */
	public List<IChange> getChanges() {
		return changes;
	}

	/**
	 * Add a change to the ModelManager and alert the listeners of a new change.
	 * @param change
	 */
	public void addChange(Change change) {
		//add change to list
		changes.add(change);
		//alert listeners that a change was added
		fireChangeAdded(change);
	}

	//One change added to ModelManager, this method is used to interate the ModelMangerListeners and notify them.
	private void fireChangeAdded(IChange newChange) {
		ModelManagerEvent event = new ModelManagerEvent(this, new IChange[] {newChange});
		for (ModelManagerListener i : listeners) {
			i.changesAdded(event);
		}
		// printAllChanges();
	}

	//Several changes added to ModelManager, this method is used to interate the ModelMangerListeners and notify them.
	private void fireChangesAdded(IChange[] newChanges) {
		ModelManagerEvent event = new ModelManagerEvent(this, newChanges);
		for (ModelManagerListener i : listeners) {
			i.changesAdded(event);
		}
		// printAllChanges();
	}


	/**
	 * This method is used to count the number of changes in the model as well as how many of those changes are additions and removals.
	 * Used in the ChangeInspector View
	 * @return a string containing the counted changes
	 */
	public String getSummary() {
		int changeCount = changes.size();
		int addCount = 0;
		int removeCount = 0;
		for(IChange change: changes){
			if(change instanceof Add){
				addCount++;
			}else if(change instanceof Remove){
				removeCount++;
			}
		}

		return changeCount + " changes; " + addCount + " additions and " + removeCount + " removals";
	}


	/**
	 * Method to add a famix entity to the ModelManager. It will add the entity to the large list of famixentitites, but also add it to its resepective map.
	 */
	public void addFamixElement(FamixObject fe) {
		famixEntities.add(fe);
		if (fe instanceof FamixPackage) {
			famixPackagesMap.put(((FamixPackage) fe).getUniqueName(), (FamixPackage) fe);
		} else if (fe instanceof FamixClass) {
			famixClassesMap.put(((FamixClass) fe).getUniqueName(), (FamixClass) fe);
		} else if (fe instanceof FamixMethod) {
			famixMethodsMap.put(((FamixMethod) fe).getUniqueName(), (FamixMethod) fe);
		} else if (fe instanceof FamixAttribute) {
			famixFieldsMap.put(((FamixAttribute) fe).getUniqueName(), (FamixAttribute) fe);
		} else if (fe instanceof FamixInvocation) {
			famixInvocationsMap.put(((FamixInvocation) fe).getStringRepresentation(), (FamixInvocation) fe);
		} else if (fe instanceof FamixLocalVariable) {
			famixVariablesMap.put(((FamixLocalVariable) fe).getUniqueName(), (FamixLocalVariable) fe);
		}
	}

	/**
	 * Method to check if a given famixobject exists in the list of famixentities.
	 * @param fe
	 * @return
	 */
	public boolean famixElementExists(FamixObject fe) {
		return famixEntities.contains(fe);
	}

	/**
	 * @return the list of famixentities
	 */
	public Collection<FamixObject> getFamixElements() {
		return famixEntities;
	}


	/*
	public FamixObject getFamixElement(FamixObject fe) {
		int index = famixEntities.indexOf(fe);
		if (index != -1)
			return famixEntities.get(index);
		else
			return null;
	}*/


	/**
	 * Add a listener to the modelmanager, this listener will be updated when a change is added to the model.
	 * @param listener
	 */
	public void addModelManagerListener(ModelManagerListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	/**
	 * remove a given listener
	 * @param listener
	 */
	public void removeModelManagerListener(ModelManagerListener listener) {
		listeners.remove(listener);
	}



	/*
	 * For testing purposes only!
	 */
	public void clearModel() {
		/*
		 * changes = new ArrayList<IChange>(); famixEntities = new
		 * ArrayList<FamixObject>(); listeners = new
		 * ArrayList<ModelManagerListener>();
		 */
		INSTANCE = new ModelManager();
	}

	/*
	 * public void printAllChanges(){
	 * System.out.println("--------------------------------------"); for(IChange
	 * ch : changes){ System.out.println(ch.toString()); }
	 * System.out.println("--------------------------------------"); }
	 */

	// /////////////////////////////////////////////////////////////////////////
	//
	// Persisting Model
	//
	// /////////////////////////////////////////////////////////////////////////

	public void saveModel() {
		// CheopsjLog.logInfo("Saving Model");
		File file = getModelFile();
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(changes);
			out.writeObject(famixEntities);

			out.writeObject(famixPackagesMap);
			out.writeObject(famixClassesMap);
			out.writeObject(famixMethodsMap);
			out.writeObject(famixFieldsMap);
			out.writeObject(famixInvocationsMap);
			out.writeObject(famixVariablesMap);

			out.close();
			// CheopsjLog.logInfo("Model Saved");
		} catch (IOException ex) {
			// CheopsjLog.logError(ex);
		}
	}

	@SuppressWarnings("unchecked")
	public void loadModel() {
		// CheopsjLog.logInfo("Loading Model");
		File file = getModelFile();
		if(file.exists()){

			FileInputStream fis = null;
			ObjectInputStream in = null;
			try {
				fis = new FileInputStream(file);
				in = new ObjectInputStream(fis);
				changes = (List<IChange>) in.readObject();
				famixEntities = (List<FamixObject>) in.readObject();

				famixPackagesMap = (Map<String, FamixPackage>) in.readObject();
				famixClassesMap = (Map<String, FamixClass>) in.readObject();
				famixMethodsMap = (Map<String, FamixMethod>) in.readObject();
				famixFieldsMap = (Map<String, FamixAttribute>) in.readObject();
				famixInvocationsMap = (Map<String, FamixInvocation>) in.readObject();
				famixVariablesMap = (Map<String, FamixLocalVariable>) in.readObject();

				//TODO load maps
				in.close();
				// CheopsjLog.logInfo("Model Loaded");
			} catch (IOException ex) {
				// CheopsjLog.logError(ex);
			} catch (ClassNotFoundException ex) {
				// CheopsjLog.logError(ex);
			}
			fireChangesAdded(changes.toArray(new IChange[changes.size()]));
		}
	}

	private File getModelFile() {
		return Activator.getDefault().getStateLocation().append("changemodel.ser").toFile();
	}


	// /////////////////////////////////////////////////////////////////////////
	//
	// Searching the Maps for specific FamixEntities
	//
	// /////////////////////////////////////////////////////////////////////////

	/**
	 * @param identifier
	 */
	public FamixMethod getFamixMethodWithName(String identifier) {
		for (FamixObject fo : famixEntities) {
			if (fo instanceof FamixMethod) {
				if (((FamixMethod) fo).getUniqueName().equals(identifier)) {
					return (FamixMethod) fo;
				}
			}
		}

		return null;

	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixPackageExists(String elementName) {
		return famixPackagesMap.containsKey(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixPackage getFamixPackage(String elementName) {
		return famixPackagesMap.get(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixClassExists(String elementName) {
		return famixClassesMap.containsKey(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixClass getFamixClass(String elementName) {
		return famixClassesMap.get(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixMethodExists(String elementName) {
		return famixMethodsMap.containsKey(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixMethod getFamixMethod(String elementName) {
		return famixMethodsMap.get(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public boolean famixFieldExists(String elementName) {
		return famixFieldsMap.containsKey(elementName);
	}

	/**
	 * @param elementName
	 * @return
	 */
	public FamixAttribute getFamixField(String elementName) {
		return famixFieldsMap.get(elementName);
	}

	/**
	 * @param stringrepresentation
	 * @return
	 */
	public FamixInvocation getFamixInvocation(String stringrepresentation) {
		return famixInvocationsMap.get(stringrepresentation);
	}

	/**
	 * @param stringrepresentation
	 * @return
	 */
	public boolean famixInvocationExists(String stringrepresentation) {
		return famixInvocationsMap.containsKey(stringrepresentation);
	}

	/**
	 * @param variableName
	 * @return
	 */
	public boolean famixVariableExists(String variableName) {
		return famixVariablesMap.containsKey(variableName);
	}

	/**
	 * @param variableName
	 * @return
	 */
	public FamixLocalVariable getFamixVariable(String variableName) {
		return famixVariablesMap.get(variableName);
	}

	public void printGraphForGroove(){
		try{
			FileWriter fstream = new FileWriter("/Users/quinten/Desktop/start.gst");
			BufferedWriter out = new BufferedWriter(fstream);
			printStartOfGrooveGraph(out);

			for(IChange change : changes){
				printNodeInGrooveGraph(out, change.getID());
				if(change instanceof Add){
					printEdgeInGrooveGraph(out, change.getID(), change.getID(), "type:Add");	
				}else if(change instanceof Remove){
					printEdgeInGrooveGraph(out, change.getID(), change.getID(), "type:Rem");
				}

				String timestampID = change.getID() + "a0";
				printNodeInGrooveGraph(out, timestampID);
				printEdgeInGrooveGraph(out, timestampID, timestampID, "int:"+change.getTimeStamp().getTime());
				printEdgeInGrooveGraph(out, change.getID(), timestampID, "timestamp");
				//ditto for intent / user / & other metadata
			}

			for(FamixObject famix: famixEntities){
				printNodeInGrooveGraph(out, famix.getID());

				printEdgeInGrooveGraph(out, famix.getID(), famix.getID(), "type:"+famix.getFamixType());

				//name ?
				String nameID = famix.getID() + "a0";
				printNodeInGrooveGraph(out, nameID);
				if(famix instanceof FamixEntity){
					printEdgeInGrooveGraph(out, nameID, nameID, "string:&quot;"+((FamixEntity)famix).getName()+"&quot;");
				}else if(famix instanceof FamixInvocation){
					//printEdgeInGrooveGraph(out, nameID, nameID, "string:&quot;"+((FamixInvocation)famix).getStringRepresentation()+":&quot;");
				}
				printEdgeInGrooveGraph(out, famix.getID(), nameID, "name");

				if (famix instanceof FamixPackage) {
					FamixPackage belongsTo = ((FamixPackage) famix).getBelongsToPackage();
					if(belongsTo != null)				
						printEdgeInGrooveGraph(out, famix.getID(), belongsTo.getID(), "belongsTo");
				} else if (famix instanceof FamixClass) {
					FamixPackage belongsTo = ((FamixClass) famix).getBelongsToPackage();
					if(belongsTo != null)				
						printEdgeInGrooveGraph(out, famix.getID(), belongsTo.getID(), "belongsTo");
					FamixClass belongsToClass = ((FamixClass) famix).getBelongsToClass();
					if(belongsToClass != null)
						printEdgeInGrooveGraph(out, famix.getID(), belongsToClass.getID(), "belongsTo");
				} else if (famix instanceof FamixMethod) {
					FamixClass belongsToClass = ((FamixMethod) famix).getBelongsToClass();
					if(belongsToClass != null)
						printEdgeInGrooveGraph(out, famix.getID(), belongsToClass.getID(), "belongsTo");
					FamixClass returnClass = ((FamixMethod) famix).getDeclaredReturnClass();
					if(returnClass != null)
						printEdgeInGrooveGraph(out, famix.getID(), returnClass.getID(), "returnClass");
				} else if (famix instanceof FamixAttribute) {
					FamixClass belongsToClass = ((FamixAttribute) famix).getBelongsToClass();
					if(belongsToClass != null)
						printEdgeInGrooveGraph(out, famix.getID(), belongsToClass.getID(), "belongsTo");
					FamixClass declaredClass = ((FamixAttribute) famix).getDeclaredClass();
					if(declaredClass != null)
						printEdgeInGrooveGraph(out, famix.getID(), declaredClass.getID(), "declaredClass");
				} else if (famix instanceof FamixInvocation) {
					FamixBehaviouralEntity candidate = ((FamixInvocation) famix).getCandidate();
					if(candidate != null)
						printEdgeInGrooveGraph(out, famix.getID(), candidate.getID(), "candidate");
					FamixMethod invokedby = (FamixMethod) ((FamixInvocation) famix).getInvokedBy();
					printEdgeInGrooveGraph(out, famix.getID(), invokedby.getID(), "invokedBy");
				} else if (famix instanceof FamixLocalVariable) {
					FamixMethod belongsToMethod = (FamixMethod) ((FamixLocalVariable) famix).getBelongsToBehaviour();
					if(belongsToMethod != null)
						printEdgeInGrooveGraph(out, famix.getID(), belongsToMethod.getID(), "belongsTo");
					FamixClass declaredClass = ((FamixLocalVariable) famix).getDeclaredClass();
					if(declaredClass != null)
						printEdgeInGrooveGraph(out, famix.getID(), declaredClass.getID(), "declaredClass");
				}

			}

			for(IChange change : changes){
				for(Change dep : ((AtomicChange)change).getStructuralDependencies()){
					printEdgeInGrooveGraph(out, change.getID(), dep.getID(), "depends");
				}

				String subjectID = ((AtomicChange)change).getChangeSubject().getID();
				printEdgeInGrooveGraph(out, change.getID(), subjectID, "subject");
			}

			printEndOfGrooveGraph(out);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void printNodeInGrooveGraph(BufferedWriter out, String id) throws IOException{
		out.write("\t\t<node id=\""+id+"\">\n");
		out.write("\t\t</node>\n");
	}

	private void printEdgeInGrooveGraph(BufferedWriter out, String from, String to, String label) throws IOException{
		out.write("\t\t<edge from=\""+from+"\" to=\""+to+"\">\n");
		out.write("\t\t\t<attr name=\"label\">\n");
		out.write("\t\t\t\t<string>"+label+"</string>\n");
		out.write("\t\t\t</attr>\n");
		out.write("\t\t</edge>\n");
	}


	private void printStartOfGrooveGraph(BufferedWriter out ) throws IOException{
		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
		out.write("<gxl xmlns=\"http://www.gupro.de/GXL/gxl-1.0.dtd\">\n");
		out.write("\t<graph role=\"graph\" edgeids=\"false\" edgemode=\"directed\" id=\"start\">\n");
		out.write("\t\t<attr name=\"$version\">\n");
		out.write("\t\t\t<string>curly</string>\n");
		out.write("\t\t</attr>\n");
		out.write("\t\t<attr name=\"$version\">\n");
		out.write("\t\t\t<string>curly</string>\n");
		out.write("\t\t</attr>\n");
	}

	private void printEndOfGrooveGraph(BufferedWriter out ) throws IOException{
		out.write("\t</graph>\n");
		out.write("</gxl>");
	}


}
