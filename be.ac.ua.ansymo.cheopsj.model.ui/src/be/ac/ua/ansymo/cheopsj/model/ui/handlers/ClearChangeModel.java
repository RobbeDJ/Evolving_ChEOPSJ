package be.ac.ua.ansymo.cheopsj.model.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import be.ac.ua.ansymo.cheopsj.model.ModelManager;

public class ClearChangeModel extends AbstractHandler {
	/**
	 * Load the state of the model
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {

		ModelManager.getInstance().clearModel();
		return null;
		
	}
}
