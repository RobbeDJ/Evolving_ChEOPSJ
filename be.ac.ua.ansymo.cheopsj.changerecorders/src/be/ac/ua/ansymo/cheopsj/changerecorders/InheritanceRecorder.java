package be.ac.ua.ansymo.cheopsj.changerecorders;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;
import be.ac.ua.ansymo.cheopsj.model.changes.Add;
import be.ac.ua.ansymo.cheopsj.model.changes.AtomicChange;
import be.ac.ua.ansymo.cheopsj.model.changes.IChange;
import be.ac.ua.ansymo.cheopsj.model.changes.Remove;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixClass;
import be.ac.ua.ansymo.cheopsj.model.famix.FamixInheritanceDefinition;

public class InheritanceRecorder extends AbstractEntityRecorder {

	//Need to see if superclass changed , ---> remove previous superclass inheritance relationship + add new superlass inheritance relationship
	//Compare old interfaces to new interfaces.
	String typename = "";
	String[] interfaces;
	String supertypename = "";
	FamixClass type;
	
	public InheritanceRecorder(IType element){
		typename = element.getFullyQualifiedName();
		typename = typename.replace('$', '.');
		
		try {
			supertypename = ((IType)element).getSuperclassName();
			interfaces = ((IType)element).getSuperInterfaceNames();
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	protected void createAndLinkFamixElement() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void createAndLinkChange(AtomicChange change) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unchecked")
	public void storeChanges() {
		//first see, what has changed!
		if(ModelManager.getInstance().famixClassExists(typename)){
			type = ModelManager.getInstance().getFamixClass(typename);
			//Collection<FamixInheritanceDefinition> oldSuperClasses = type.getSuperClasses();
			
			Collection<String> oldSuperClasses = type.getSuperClassNames();
			Collection<String> newSuperClasses = new ArrayList<String>();
			newSuperClasses.add(supertypename);
			CollectionUtils.addAll(newSuperClasses, interfaces);
			
			//Compare oldSuperClasses with newSuperClasses to see which was added and which was removed.
			Collection<String> addedSuperClasses = ListUtils.removeAll(
						newSuperClasses, CollectionUtils.intersection(oldSuperClasses, newSuperClasses));
			Collection<String> removedSuperClasses = ListUtils.removeAll(
					oldSuperClasses, CollectionUtils.intersection(oldSuperClasses, newSuperClasses));
			
			
			//create Add for all in addedSuperClasses
			for(String added: addedSuperClasses){
				this.storeChange(new Add(), added);
			}
			
			//create Remove for all in removedSuperClasses
			for(String removed: removedSuperClasses){
				this.storeChange(new Remove(), removed);
			}
			
			
			
		}
	}

	private void storeChange(IChange change, String added) {
		createAndLinkFamixElement(added);
		createAndLinkChange((AtomicChange) change);
	}

	private void createAndLinkFamixElement(String added) {
		FamixInheritanceDefinition inh = new FamixInheritanceDefinition();
		FamixClass superClass = ModelManager.getInstance().getFamixClass(added);
		inh.setSuperClass(superClass);
		inh.setSubClass(type);
		inh.setStringRepresentation("");
		
		
		
		
	}

}
